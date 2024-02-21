package com.steepgraph.ta.framework.common.pages;

import java.net.HttpURLConnection;
import java.net.URL;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.Wait;

import com.browserup.bup.BrowserUpProxy;
import com.google.common.base.Function;
import com.steepgraph.ta.framework.Constants;
import com.steepgraph.ta.framework.enums.LocatorType;
import com.steepgraph.ta.framework.utils.pages.BrowserUtil;
import com.steepgraph.ta.framework.utils.pages.LoggerUtil;
import com.steepgraph.ta.framework.utils.pages.PropertyUtil;

/**
 * Driver class providing driver object related functionalities
 * 
 * @author Steepgraph Systems
 *
 */
public class Driver {

	private WebDriver webDriver;

	private BrowserUpProxy proxy;

	FluentWait<WebDriver> wait;

	/**
	 * Driver constructor
	 * 
	 * @author Steepgraph Systems
	 * @param browserName
	 * @throws Exception
	 */
	public Driver(String browserName, PropertyUtil propertyUtil, Map<String, String> parameterMap) throws Exception {
		LoggerUtil.debug("Driver initialization...");

		BrowserUtil browserUtilObj = new BrowserUtil();
		LoggerUtil.debug("Browser util inialization...");

		this.webDriver = browserUtilObj.getDriver(browserName, propertyUtil, parameterMap);
		LoggerUtil.debug("web driver object is created");
		this.proxy = browserUtilObj.getBrowserProxy();
		initializeWebDriver(propertyUtil);

		LoggerUtil.debug("wait object inialization");
	}

	public void initializeWebDriver(PropertyUtil propertyutil) throws Exception {
		String implicitWait = propertyutil.getProperty("driver.implicit.wait");
		LoggerUtil.debug("implicitWait : " + implicitWait);
		if (implicitWait != null && !"".equals(implicitWait)) {
			int iImplicitWait = Integer.parseInt(implicitWait);
			webDriver.manage().timeouts().implicitlyWait(iImplicitWait, TimeUnit.SECONDS);
		}
		String strTimeout = propertyutil.getProperty(Constants.PROPERTY_KEY_EXECUTION_STEP_TIMEOUT);
		LoggerUtil.debug("iTimeout in Millis: " + strTimeout);
		int iTimeout = Integer.parseInt(strTimeout);

		String strPollingInterval = propertyutil
				.getProperty(Constants.PROPERTY_KEY_EXECUTION_STEP_TIMEOUT_POLLINGINTERVAL);
		LoggerUtil.debug("iPollingInterval in Millis: " + strPollingInterval);
		int iPollingInterval = Integer.parseInt(strPollingInterval);

		if (iPollingInterval > iTimeout) {
			iTimeout = iPollingInterval;
			LoggerUtil.debug(
					"3dx-tas.execution.step.timeout is less than 3dx-tas.execution.step.timeout.pollinginterval, default value is iTimeout = iPollingInterval");
			LoggerUtil.debug("iTimeout : " + iTimeout);
			LoggerUtil.debug("iPollingInterval : " + iPollingInterval);
		}
		wait = new FluentWait<WebDriver>(webDriver).withTimeout(Duration.ofMillis(iTimeout))
				.pollingEvery(Duration.ofMillis(iPollingInterval)).ignoring(NoSuchElementException.class);
	}

	public WebDriver getWebDriver() {
		return webDriver;
	}

	public void setWebDriver(WebDriver webDriver) {
		this.webDriver = webDriver;
	}

	public void click(WebElement webElement) throws Exception {
		click(webElement, "selenium", "true");
	}

	public void click(final WebElement webElement, String mode, String async) throws Exception {

		if ("js".equalsIgnoreCase(mode)) {
			final JavascriptExecutor executor = (JavascriptExecutor) webDriver;
			if ("true".equalsIgnoreCase(async)) {
				new Thread() {

					public void run() {
						executor.executeScript("arguments[0].click();", webElement);
					}
				}.start();
				Thread.sleep(2000);
			} else {
				Thread.sleep(1000);
				executor.executeScript("arguments[0].click();", webElement);
			}
		} else {
			try {
				int exeCnt = 0;
				while (exeCnt < 3) {
					try {
						try {
							LoggerUtil.debug("before click");
							waitUntil(ExpectedConditions.elementToBeClickable(webElement), 2).click();
							LoggerUtil.debug("after click");
						} catch (Exception e) {
							LoggerUtil.debug("before js click");
							JavascriptExecutor executor = (JavascriptExecutor) webDriver;
							executor.executeScript("arguments[0].click();", webElement);
							LoggerUtil.debug("after js click");
						}
						break;
					} catch (StaleElementReferenceException e) {
						// TODO: handle exception
						// intentionally not handled
					}
					exeCnt++;
				}
			} catch (StaleElementReferenceException e) {
				// TODO: handle exception
				click(webElement, "js", "true");
			}
		}
	}

	public void selectByText(WebElement webElement, String selectText) throws Exception {
		LoggerUtil.debug("Start of  selectByText");
		LoggerUtil.debug("selectText : " + selectText);
		try {
			wait.until(ExpectedConditions.elementToBeClickable(webElement));
		} catch (Exception e) {
			LoggerUtil.debug("WARNING !!!! Element is still not clickable but still trying !!!");
		}
		Select selectObj = new Select(webElement);

		if ("$ALL".equalsIgnoreCase(selectText)) {
			for (WebElement weOption : selectObj.getOptions()) {
				selectObj.selectByVisibleText(weOption.getText());
			}
		} else {
			if (selectText.contains("|")) {
				String[] arrSelectText = selectText.split("\\|");
				int length = arrSelectText.length;

				for (int i = 0; i < length; i++) {
					LoggerUtil.debug("arrSelectText[i] : " + arrSelectText[i]);
					selectObj.selectByVisibleText(arrSelectText[i]);
				}
			} else {
				selectObj.selectByVisibleText(selectText);
			}
		}
		LoggerUtil.debug("End of  selectByText");
	}

	public WebElement findElement(final By by) {

		WebElement element = wait.until(new Function<WebDriver, WebElement>() {

			@Override
			public WebElement apply(WebDriver driver) {
				return driver.findElement(by);
			}
		});

		return element;
	}

	/**
	 * Method to find element relative to another element
	 * 
	 * @author Steepgraph Systems
	 * @param webElement
	 * @param by
	 * @return WebElement
	 * @throws Exception
	 */
	public WebElement findElement(final WebElement webElement, final By by) {

		WebElement element = wait.until(new Function<WebDriver, WebElement>() {

			@Override
			public WebElement apply(WebDriver driver) {
				return webElement.findElement(by);

			}
		});
		return element;
	}

	/**
	 * Method to find element relative to another element
	 * 
	 * @author Steepgraph Systems
	 * @param locatorType
	 * @param locatorExpresion
	 * @return WebElement
	 * @throws Exception
	 */
	public WebElement findElement(final WebElement webReferenceElement, String locatorType, String locatorExpresion)
			throws Exception {
		WebElement webElement = null;
		locatorType = locatorType.toLowerCase();

		LocatorType locator = LocatorType.valueOf(locatorType);

		switch (locator) {

		case xpath:
			webElement = wait.until(new Function<WebDriver, WebElement>() {

				@Override
				public WebElement apply(WebDriver driver) {
					return webReferenceElement.findElement(By.xpath(locatorExpresion));
				}
			});
			break;

		case id:
			webElement = wait.until(new Function<WebDriver, WebElement>() {

				@Override
				public WebElement apply(WebDriver driver) {
					return webReferenceElement.findElement(By.id(locatorExpresion));
				}
			});
			break;

		case cssselector:
			webElement = wait.until(new Function<WebDriver, WebElement>() {

				@Override
				public WebElement apply(WebDriver driver) {
					return webReferenceElement.findElement(By.cssSelector(locatorExpresion));
				}
			});
			break;

		case name:
			webElement = wait.until(new Function<WebDriver, WebElement>() {

				@Override
				public WebElement apply(WebDriver driver) {
					return webReferenceElement.findElement(By.name(locatorExpresion));
				}
			});
			break;

		default:
			throw new Exception("Locator type = " + locatorType + " is not valid.");
		}
		return webElement;
	}

	public By getBy(String locatorType, String locatorExpresion) throws Exception {
		locatorType = locatorType.toLowerCase();

		LocatorType locator = LocatorType.valueOf(locatorType);

		switch (locator) {

		case xpath:
			return By.xpath(locatorExpresion);

		case id:
			return By.id(locatorExpresion);

		case cssselector:
			return By.cssSelector(locatorExpresion);

		case name:
			return By.name(locatorExpresion);

		default:
			throw new Exception("Locator type = " + locatorType + " is not valid.");
		}
	}

	/**
	 * Method to find elements
	 * 
	 * @author Steepgraph Systems
	 * @param webElement
	 * @param by
	 * @return WebElement
	 * @throws Exception
	 */
	public List<WebElement> findElements(final By by) {
		List<WebElement> element = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(by));
		return element;
	}

	/**
	 * Method to find elements
	 * 
	 * @author Steepgraph Systems
	 * @param webElement
	 * @param by
	 * @return WebElement
	 * @throws Exception
	 */
	public List<WebElement> findElements2(String locatorType, String locatorExpresion) throws Exception {
		List<WebElement> wbElementList = null;
		locatorType = locatorType.toLowerCase();
		LocatorType locator = LocatorType.valueOf(locatorType);
		switch (locator) {
		case xpath:
			wbElementList = wait.until(new Function<WebDriver, List<WebElement>>() {

				@Override
				public List<WebElement> apply(WebDriver driver) {
					return driver.findElements(By.xpath(locatorExpresion));
				}
			});
			break;

		case id:
			wbElementList = wait.until(new Function<WebDriver, List<WebElement>>() {

				@Override
				public List<WebElement> apply(WebDriver driver) {
					return driver.findElements(By.id(locatorExpresion));
				}
			});
			break;

		case cssselector:
			wbElementList = wait.until(new Function<WebDriver, List<WebElement>>() {

				@Override
				public List<WebElement> apply(WebDriver driver) {
					return driver.findElements(By.cssSelector(locatorExpresion));
				}
			});
			break;

		case name:
			wbElementList = wait.until(new Function<WebDriver, List<WebElement>>() {

				@Override
				public List<WebElement> apply(WebDriver driver) {
					return driver.findElements(By.name(locatorExpresion));
				}
			});
			break;

		default:
			throw new Exception("Locator type = " + locatorType + " is not valid.");
		}
		return wbElementList;
	}

	/**
	 * Method to find elements
	 * 
	 * @author Steepgraph Systems
	 * @param webElement
	 * @param by
	 * @return WebElement
	 * @throws Exception
	 */
	public List<WebElement> findElements(final WebElement webElement, final By by) {
		List<WebElement> element = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(by));
		return element;
	}

	/**
	 * Method to find element
	 * 
	 * @author Steepgraph Systems
	 * @param locatorType
	 * @param locatorExpresion
	 * @return WebElement
	 * @throws Exception
	 */
	public WebElement findElement(String locatorType, String locatorExpresion) throws Exception {
		WebElement webElement = null;

		LocatorType locator = null;
		try {
			locatorType = locatorType.toLowerCase();
		} catch (Exception e) {
			throw new Exception("locatorType attribute is not specified properly.");
		}
		locator = LocatorType.valueOf(locatorType);
		switch (locator) {
		case xpath:
			webElement = wait.until(new Function<WebDriver, WebElement>() {

				@Override
				public WebElement apply(WebDriver driver) {
					return driver.findElement(By.xpath(locatorExpresion));
				}
			});
			break;

		case id:
			webElement = wait.until(new Function<WebDriver, WebElement>() {

				@Override
				public WebElement apply(WebDriver driver) {
					return driver.findElement(By.id(locatorExpresion));
				}
			});
			break;

		case cssselector:
			webElement = wait.until(new Function<WebDriver, WebElement>() {

				@Override
				public WebElement apply(WebDriver driver) {
					return driver.findElement(By.cssSelector(locatorExpresion));
				}
			});
			break;

		case name:
			webElement = wait.until(new Function<WebDriver, WebElement>() {

				@Override
				public WebElement apply(WebDriver driver) {
					return driver.findElement(By.name(locatorExpresion));
				}
			});
			break;

		default:
			throw new Exception("Locator type = " + locatorType + " is not valid.");
		}
		return webElement;
	}

	/**
	 * 
	 * @param driver
	 * @return Wait<WebDriver>
	 */
	public Wait<WebDriver> getWait() {
		return wait;
	}

	/**
	 * method to check the current frame
	 * 
	 * @author Steepgraph System
	 * @param webDriver
	 * @return String
	 * @throws Exception
	 */
	public String getCurrentFrame() throws Exception {
		String currentFrame = null;
		JavascriptExecutor jsExecutor = (JavascriptExecutor) webDriver;
		try {
			currentFrame = (String) jsExecutor.executeScript("return self.name");
		} catch (Exception e) {
			LoggerUtil.debug("Self name data not available.");
		}

		String appName = null;
		if (currentFrame == null || "".equalsIgnoreCase(currentFrame)) {
			try {
				appName = (String) jsExecutor.executeScript("return self.frames.widget.data.appId");
				if (appName != null || !"".equalsIgnoreCase(appName)) {
					currentFrame = appName;
				}
			} catch (Exception e) {
				LoggerUtil.debug("AppId data not available.");
			}
		}
		return currentFrame;
	}

	/**
	 * method to switch to the nested frames/ single frame on a webpage
	 * 
	 * @author Steepgraph Systems
	 * @param frameName
	 * @throws Exception
	 */
	public void switchToFrame(String frameName) throws Exception {
		String[] arrFrameNames = null;
		arrFrameNames = frameName.split("=>");

		for (String str : arrFrameNames) {
			if (!"".equalsIgnoreCase(str)) {
				wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(str));
			}
		}
	}

	/**
	 * method to switch to frame using its webElement
	 * 
	 * @author Steepgraph Systems
	 * @param WebElement
	 * @throws Exception
	 */
	public void switchToFrame(WebElement elementInFrame) throws Exception {
		wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(elementInFrame));

	}

	/**
	 * method to switch to frame using its webElement
	 * 
	 * @author Steepgraph Systems
	 * @param index
	 * @throws Exception
	 */
	public void switchToFrame(int index) throws Exception {
		wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(index));
	}

	/**
	 * method to send text to input element
	 * 
	 * @author Steepgraph Systems
	 * @param element
	 * @param text
	 * @throws Exception
	 */
	public void writeText(WebElement element, String text) throws Exception {
		writeText(element, text, "selenium");
	}

	/**
	 * method to send text to input element
	 * 
	 * @author Steepgraph Systems
	 * @param element
	 * @param text
	 * @param mode
	 * @throws Exception
	 */
	public void writeText(WebElement element, String text, String mode) throws Exception {
		wait.until(ExpectedConditions.elementToBeClickable(element));
		// use js executor to set value. send keys requires clear before setting value.
		// Sometimes clearing value of text field is not allowed
		if ("js".equalsIgnoreCase(mode)) {
			JavascriptExecutor executor = (JavascriptExecutor) webDriver;
			executor.executeScript("arguments[0].value = '" + text + "';", element);
			Actions actions = new Actions(getWebDriver());
			actions.sendKeys(Keys.ARROW_RIGHT).build().perform();
		} else {
			// This try catch block is used to handle staleElement Exception
			try {
				int exeCnt = 0;
				while (exeCnt < 3) {
					try {
						// modification for SELENIUM-381: clear method is not working for input tag so
						// using sendKeys instead
						element.clear();
						// element.sendKeys(Keys.chord(Keys.CONTROL, "a"));
						// element.sendKeys(Keys.DELETE);
						element.sendKeys(text);
						break;
					} catch (StaleElementReferenceException e) {
						// TODO: handle exception
						// intentionally not handled
					}
					exeCnt++;
				}
			} catch (StaleElementReferenceException e) {
				// TODO: handle exception
				writeText(element, text, "js");
			}
		}
	}

	/**
	 * method to send color to insertColor
	 * 
	 * @author Steepgraph Systems
	 * @param WebElement
	 * @param color
	 * @throws Exception
	 */
	public void setValue(WebElement wbElement, String value) throws Exception {
		JavascriptExecutor executor = (JavascriptExecutor) webDriver;
		executor.executeScript("arguments[0].setAttribute( 'value','" + value + "');", wbElement);
	}

	/**
	 * method to send text to input element
	 * 
	 * @author Steepgraph Systems
	 * @param WebElement
	 * @param Keys
	 * @throws Exception
	 */
	public void sendKey(WebElement element, Keys key) throws Exception {
		Thread.sleep(2000);
		element.sendKeys(key);

	}

	/**
	 * Wait for DOM to get ready
	 */
	public void waitForJavaScriptToLoad() throws Exception {
		try {
			wait.until((ExpectedCondition<Boolean>) wd -> ((JavascriptExecutor) wd)
					.executeScript("return document.readyState").equals("complete"));
		} catch (Exception e) {
			// do nothing
		}
	}

	/**
	 * This method will be used to scroll current view port to given element.
	 * 
	 * @author Steepgraph Systems
	 * @param WebElement
	 * @return void
	 * @throws Exception
	 */
	public void scrollToElement(WebElement element) throws Exception {
		Thread.sleep(2000);
		((JavascriptExecutor) webDriver).executeScript("arguments[0].scrollIntoView();", element);
		wait.until(ExpectedConditions.visibilityOf(element));
	}

	/**
	 * This method will be wait until expected condition met.
	 * 
	 * @author Steepgraph Systems
	 * @param ExpectedCondition
	 * @return void
	 * @throws Exception
	 */
	public WebElement waitUntil(ExpectedCondition<WebElement> expectedCondition) throws Exception {
		return wait.until(expectedCondition);
	}

	public Boolean waitUntil2(ExpectedCondition<Boolean> expectedCondition) throws Exception {
		return wait.until(expectedCondition);
	}

	public Alert waitUntil3(ExpectedCondition<Alert> expectedCondition) {
		return wait.until(expectedCondition);
	}

	/**
	 * This method will be wait until expected condition met and retry it for given
	 * times
	 * 
	 * @author SteepGraph Systems
	 * @param expectedCondition
	 * @param retry
	 * @return void
	 * @throws Exception
	 */
	public WebElement waitUntil(ExpectedCondition<WebElement> expectedCondition, int retry) throws Exception {

		int i = 0;

		while (i < retry) {
			i++;
			LoggerUtil.debug("attempt: " + i);
			try {
				return wait.until(expectedCondition);
			} catch (Exception e) {
				if (i == retry)
					throw e;
			}
		}

		return null;

	}

	/**
	 * Method to click override link displayed in Internet Explorer browser
	 * 
	 * @author Steepgraph Systems
	 * @param driver
	 * @return void
	 * @throws Exception
	 */
	public void clickOverrideLink() throws Exception {
		LoggerUtil.debug("Start of clickOverrideLink.");
		// this tag is deprecated now, it has alternatives:
		// 3dx-tas.browser.acceptInsecureCerts in Browser.properties setting
		WebElement wbOverrideLink = null;
		try {
			wbOverrideLink = this.findElement(By.cssSelector("[id='details-button']"));
			this.click(wbOverrideLink);

			wbOverrideLink = this.findElement(By.cssSelector("[id='proceed-link']"));
			this.click(wbOverrideLink);
		} catch (Exception e) {
			LoggerUtil.debug("Override link not found.");
			return;
		}

		LoggerUtil.debug("Override link clicked.");
		LoggerUtil.debug("End of clickOverrideLink.");

	}

	/**
	 * Method to release driver and wait objects
	 * 
	 * @author Steepgraph Systems
	 * @return void
	 * @throws Exception
	 */
	public void close() {
		if (this.webDriver != null) {
			this.webDriver.quit();
			this.webDriver = null;
		}

		if (this.proxy != null) {
			this.proxy.stop();
			this.proxy = null;
		}
	}

	/**
	 * Method to start browser and get passed URL
	 * 
	 * @author Steepgraph Systems
	 * @param driver
	 * @param url
	 * @return void
	 * @throws Exception
	 */
	public void startBrowsing(String url, boolean verifyUrl) throws Exception {
		if (this.verifyURLActive(url, verifyUrl)) {
			this.webDriver.navigate().to(url);
			this.webDriver.manage().window().maximize();
			LoggerUtil.debug("Browser started with URL: " + url + ". Window maximised.");
		} else {
			throw new Exception(url + " is not active");
		}
	}

	/**
	 * This method is used to check URL is active or not
	 */
	public boolean verifyURLActive(String linkUrl, boolean verifyUrl) {
		LoggerUtil.debug("Started verifyURLActive");
		boolean linkActive = false;
		try {
			if (!verifyUrl) {
				return true;
			}

			URL url = new URL(linkUrl);
			HttpURLConnection httpURLConnect = (HttpURLConnection) url.openConnection();
			httpURLConnect.setRequestMethod("HEAD");
			httpURLConnect.connect();

			if (httpURLConnect.getResponseCode() >= 400) {
				linkActive = false;
			} else
				linkActive = true;
		} catch (Exception e) {
			linkActive = false;
		}

		LoggerUtil.debug("End of  verifyURLActive" + linkActive);
		return linkActive;
	}

	public BrowserUpProxy getProxy() {
		return this.proxy;
	}

	public String getText(By locator) throws Exception {
		LoggerUtil.debug("Start of getText");
		wait.until(ExpectedConditions.presenceOfElementLocated(locator));
		WebElement element = findElement(locator);
		LoggerUtil.debug("End of getText");
		return element.getText();
	}

	public String getText(WebElement wb) throws Exception {
		LoggerUtil.debug("Start of getText");
		// Thread.sleep(3000);
		LoggerUtil.debug("End of getText");
		return wb.getText();
	}

	public void closeCurrentWindow() {

		if (this.webDriver != null) {
			this.webDriver.close();
		}
	}
}