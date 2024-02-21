package com.steepgraph.ta.framework.common.pages;

import static com.steepgraph.ta.framework.Constants.ATTRIBUTE_LAST;
import static com.steepgraph.ta.framework.Constants.CHECK_TRUE;

import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;
import java.util.Stack;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.custommonkey.xmlunit.DetailedDiff;
import org.custommonkey.xmlunit.Diff;
import org.custommonkey.xmlunit.Difference;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.NoSuchWindowException;
import org.openqa.selenium.Point;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.Wait;
import org.sikuli.script.Button;
import org.sikuli.script.Match;
import org.sikuli.script.Region;
import org.sikuli.script.Screen;
import org.sikuli.script.support.IRobot;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

import com.browserup.harreader.model.HarEntry;
import com.google.common.base.Function;
import com.steepgraph.ta.framework.Constants;
import com.steepgraph.ta.framework.MasterApp;
import com.steepgraph.ta.framework.common.AssertionException;
import com.steepgraph.ta.framework.common.interfaces.IHandler;
import com.steepgraph.ta.framework.common.interfaces.ILibrary;
import com.steepgraph.ta.framework.db.IDatabase;
import com.steepgraph.ta.framework.db.OracleDatabase;
import com.steepgraph.ta.framework.enums.AssertCondition;
import com.steepgraph.ta.framework.enums.AssertCriteria;
import com.steepgraph.ta.framework.enums.Browsers;
import com.steepgraph.ta.framework.enums.IfCriteria;
import com.steepgraph.ta.framework.enums.IndentedTableCriteria;
import com.steepgraph.ta.framework.enums.InputType;
import com.steepgraph.ta.framework.enums.LocatorType;
import com.steepgraph.ta.framework.enums.MouseActions;
import com.steepgraph.ta.framework.utils.interfaces.ICSVUtil;
import com.steepgraph.ta.framework.utils.interfaces.ICommonUtil;
import com.steepgraph.ta.framework.utils.interfaces.IWebServiceUtil;
import com.steepgraph.ta.framework.utils.pages.CSVUtil;
import com.steepgraph.ta.framework.utils.pages.DecryptionUtil;
import com.steepgraph.ta.framework.utils.pages.EnoviaUtil;
import com.steepgraph.ta.framework.utils.pages.LoggerUtil;
import com.steepgraph.ta.framework.utils.pages.PropertyUtil;
import com.steepgraph.ta.framework.utils.pages.RegisterObjectUtil;
import com.steepgraph.ta.framework.utils.pages.RestWebServiceUtil;
import com.steepgraph.ta.framework.utils.pages.SoapWebServiceUtil;

import matrix.util.StringList;

/**
 * Class containing implementation code for processed tags
 * 
 * @author SteepGraph Systems
 */
//
public class MasterLibrary implements ILibrary {

	protected String masterSuiteId = MasterApp.newInstance().getSuiteId();

	protected Stack<String> windowStack = null;

	protected Stack<Object> frameStack = null;

	protected String homeWindowHandler = null;

	protected ICommonUtil commonUtilobj = null;

	protected PropertyUtil propertyUtil = null;

	protected Map<String, WebElement> webElementMap = null;

	protected Map<String, Object> webServiceResponseMap = null;

	protected Map<String, Region> imageRegionMap = null;

	protected Map<String, Connection> dbConnectionMap = null;

	protected RegisterObjectUtil registerUtil;

	protected List<String> indententTableContent = null;

	protected IHandler handler;

	protected String cookieData = "";

	protected StringBuilder performanceResult;

	protected boolean isLoggedIn = false;

	protected Map<String, Object> docObjectMap = null;

	protected Workbook workbookValidate = null;

	/**
	 * Constructor for Library class
	 * 
	 * @author Steepgraph Systems
	 * @throws Exception
	 */
	public MasterLibrary(IHandler handler, RegisterObjectUtil registerUtil, PropertyUtil propertyUtil,
			ICommonUtil commonUtil) throws Exception {
		this.handler = handler;
		this.propertyUtil = propertyUtil;
		this.registerUtil = registerUtil;
		this.commonUtilobj = commonUtil;
		windowStack = new Stack<>();
		frameStack = new Stack<>();
		webElementMap = new HashMap<>();
		webServiceResponseMap = new HashMap<>();
		imageRegionMap = new HashMap<>();
		indententTableContent = new ArrayList<>();
		docObjectMap = new HashMap<>();
		LoggerUtil.debug("Library is initialized");
	}

	@Override
	public ICommonUtil getCommonUtil() throws Exception {
		return commonUtilobj;
	}

	@Override
	public void setCommonUtil(ICommonUtil commonUtilobj) throws Exception {
		this.commonUtilobj = commonUtilobj;
	}

	/**
	 * Method to add parent frame locator to stack
	 * 
	 * @author Steepgraph Systems
	 * @param frameLocator
	 * @return void
	 * @throws Exception
	 */
	public void addToParentFrameStack(Object frameLocator) {
		frameStack.push(frameLocator);
	}

	/**
	 * Method to add parent window handle to stack
	 * 
	 * @author Steepgraph Systems
	 * @param windowHandle
	 * @return void
	 * @throws Exception
	 */
	public void addToParentWindowStack(String windowHandle) {
		windowStack.push(windowHandle);
	}

	/**
	 * Method to add parent window handle to stack
	 * 
	 * @author Steepgraph Systems
	 * @param windowHandle
	 * @return void
	 * @throws Exception
	 */
	public boolean checkExistanceParentWindowStack(String windowHandle) {
		return windowStack.contains(windowHandle);
	}

	/**
	 * Method to get parent window handle from stack
	 * 
	 * @author SteepGraph Systems
	 * @return String
	 * @throws Exception
	 */
	public String getParentWindowFromStack() {
		if (windowStack.size() > 0) {
			return windowStack.pop();
		}

		return "";
	}

	/**
	 * Method to clear Parent Frame Stack
	 * 
	 * @author SteepGraph Systems
	 * @return String
	 * @throws Exception
	 */
	public void clearParentWindowStack() {
		frameStack.clear();
	}

	/**
	 * Method to log out of Enovia
	 * 
	 * @author Steepgraph Systems
	 * @param driver
	 * @param attributeMap
	 * @return void
	 * @throws Exception
	 */
	@Override
	public void logOut(Driver driver, Map<String, String> attributeMap) throws Exception {
		LoggerUtil.debug("Start of logOut.");
		switchToDefaultContent(driver);
		WebElement wbProfileMenu = driver
				.findElement(By.xpath("//div[@class='toolbar group-right']//ul//li[@class='icon-button profile']"));
		driver.waitForJavaScriptToLoad();
		Actions action = new Actions(driver.getWebDriver());
		action.moveToElement(wbProfileMenu).build().perform();

		WebElement wbSignOut = driver.findElement(By.xpath("//ul//li//a/label[text()='Sign Out']/.."));
		driver.click(wbSignOut);
		LoggerUtil.debug("Completed logOut.");
	}

	/**
	 * Method to log in to Enovia
	 * 
	 * @author Steepgraph Systems
	 * @param driver
	 * @param attributeMap
	 * @return void
	 * @throws Exception
	 */
	@Override
	public void logIn(Driver driver, Map<String, String> attributeMap, PropertyUtil propertyUtil) throws Exception {

		String isCas = attributeMap.get(Constants.LOGIN_ISCAS);
		if (isCas == null || "".equals(isCas)) {
			isCas = propertyUtil.getProperty(Constants.PROPERTY_KEY_3DSPACE_URL_ISCAS);
		}

		LoggerUtil.debug("isCas: " + isCas + ".");
		if (Constants.CHECK_TRUE.equalsIgnoreCase(isCas)) {
			logInForCas(driver, attributeMap);
		} else {
			logInForNoCas(driver, attributeMap);
		}

		isLoggedIn = true;

		selectSecurityContext(driver, attributeMap);

		homeWindowHandler = driver.getWebDriver().getWindowHandle();
	}

	/**
	 * Method to log in to Enovia for CAS url
	 * 
	 * @author Steepgraph Systems
	 * @param driver
	 * @param attributeMap
	 * @return void
	 * @throws Exception
	 */
	@Override
	public void logInForCas(Driver driver, Map<String, String> attributeMap) throws Exception {
		LoggerUtil.debug("Start of logInForCas.");
		String username = attributeMap.get(Constants.INPUT_USERNAME);
		LoggerUtil.debug("Username: " + username);
		String password = attributeMap.get(Constants.INPUT_PASSWORD);
		LoggerUtil.debug("Password: " + password);

		for (int i = 1; i <= 2; i++) {
			driver.findElement(By.cssSelector("[name='overridelink']")).click();
		}

		WebElement wbusername = driver.findElement(By.cssSelector("[name='username']"));
		wbusername.clear();
		driver.writeText(wbusername, username);

		WebElement wbpassword = driver.findElement(By.cssSelector("[name='password']"));
		wbpassword.clear();
		driver.writeText(wbpassword, password);

		WebElement wbLogin = driver.findElement(By.xpath("//input[@value='Log in']"));
		driver.click(wbLogin);

		LoggerUtil.debug("Login button clicked.");

		LoggerUtil.debug("Completed logInForCas.");
	}

	/**
	 * Method to log in to Enovia for NoCAS url
	 * 
	 * @author Steepgraph Systems
	 * @param driver
	 * @param attributeMap
	 * @return void
	 * @throws Exception
	 */
	@Override
	public void logInForNoCas(Driver driver, Map<String, String> attributeMap) throws Exception {
		LoggerUtil.debug("Start of logInForNoCas.");
		String username = attributeMap.get(Constants.INPUT_USERNAME);
		LoggerUtil.debug("Username: " + username);
		String password = attributeMap.get(Constants.INPUT_PASSWORD);
		LoggerUtil.debug("Password: " + password);

		WebElement wbusername = driver.findElement(By.cssSelector("[name='login_name']"));
		wbusername.clear();
		driver.writeText(wbusername, username);

		WebElement wbpassword = driver.findElement(By.cssSelector("[name='login_password']"));
		wbpassword.clear();
		driver.writeText(wbpassword, password);

		WebElement wbLogin = driver.findElement(By.xpath("//button[contains(@class,'btn')]"));
		driver.click(wbLogin);

		LoggerUtil.debug("Login button clicked.");

		LoggerUtil.debug("Completed logInForNoCas.");
	}

	/**
	 * Method to expand Global Actions menu and click commands in it
	 * 
	 * @author Steepgraph Systems
	 * @param driver
	 * @param attributeMap
	 * @return void
	 * @throws Exception
	 */
	@Override
	public void clickGlobalActionsMenu(Driver driver, Map<String, String> attributeMap) throws Exception {
		LoggerUtil.debug("Start of clickGlobalActionsMenu.");

		switchToDefaultContent(driver);

		WebElement wbGlobalActions = null;
		String commandLabel = attributeMap.get(Constants.ATTRIBUTE_COMMANDLABEL);

		if (attributeMap == null || attributeMap.size() == 0
				|| !attributeMap.containsKey(Constants.ATTRIBUTE_COMMANDLABEL)) {
			throw new Exception("commandLabel is not defined for tag clickGlobalActionsMenu.");
		}

		LoggerUtil.debug("commandLabel: " + commandLabel);

		String[] labelArray = commandLabel.split(Pattern.quote("|"));
		int length = labelArray.length;

		LoggerUtil.debug("commandLabel length: " + length);

		WebElement wbGlobalMenuPanel = driver.findElement(By.xpath("//div[@class='menu-panel global add']"));

		LoggerUtil.debug("div with class='menu-panel global add' found");

		// Expand Global actions menu
		wbGlobalActions = driver
				.findElement(By.xpath("//div[@class='toolbar group-right']//ul//li[@class='icon-button add']"));

		highLightElement(driver, attributeMap, wbGlobalActions);

		Actions action = new Actions(driver.getWebDriver());
		action.moveToElement(wbGlobalActions);
		action.perform();

		WebElement commandElement = null;

		for (int i = 0; i < length; i++) {

			if (commandElement != null)
				wbGlobalMenuPanel = commandElement;

			if (i + 1 == length) {
				WebElement weGlobalActionCommand = driver.findElement(wbGlobalMenuPanel,
						By.xpath(".//descendant::label[text() = '" + labelArray[i] + "']"));
				driver.click(weGlobalActionCommand, "js", Constants.CHECK_FALSE);
			} else {

				boolean isExpanded = false;

				if (i == 0) {
					commandElement = driver.findElement(wbGlobalMenuPanel,
							By.xpath(".//descendant::label[text() = '" + labelArray[i] + "']/../../parent::div"));
					String commandClass = commandElement.getAttribute(Constants.ATTRIBUTE_CLASS);
					LoggerUtil.debug("commandClass: " + commandClass);
					if ("group expanded".equalsIgnoreCase(commandClass)) {
						isExpanded = true;
						LoggerUtil.debug("isExpanded: " + isExpanded);
					}
				} else {
					commandElement = driver.findElement(wbGlobalMenuPanel,
							By.xpath(".//descendant::label[text() = '" + labelArray[i] + "']/../parent::li"));
					String commandClass = commandElement.getAttribute(Constants.ATTRIBUTE_CLASS);
					LoggerUtil.debug("commandClass: " + commandClass);
					if ("menu expanded".equalsIgnoreCase(commandClass)) {
						isExpanded = true;
						LoggerUtil.debug("isExpanded: " + isExpanded);
					}
				}

				if (!isExpanded) {
					WebElement weGlobalActionCommand = driver.findElement(wbGlobalMenuPanel,
							By.xpath(".//descendant::label[text() = '" + labelArray[i] + "']/../.."));
					driver.click(weGlobalActionCommand, "js", Constants.CHECK_FALSE);
				}

			}

		}
		LoggerUtil.debug("End of clickGlobalActionsMenu.");
	}

	/**
	 * Method to switch to new pop up window
	 * 
	 * @author Steepgraph Systems
	 * @param driver
	 * @param attributeMap
	 * @return void
	 * @throws Exception
	 */
	@Override
	public void switchToWindow(Driver driver, Map<String, String> attributeMap) throws Exception {
		LoggerUtil.debug("Start of switchToWindow.");
		String currentWindowHandle = driver.getWebDriver().getWindowHandle();
		Thread.sleep(500);
		if (attributeMap != null && attributeMap.size() > 0 && attributeMap.containsKey(Constants.ATTRIBUTE_TITLE)) {
			String strWindowTitle = attributeMap.get(Constants.ATTRIBUTE_TITLE);
			LoggerUtil.debug("strWindowTitle: " + strWindowTitle);
			switchToWindow(driver, strWindowTitle, currentWindowHandle);
		} else {
			String isLast = attributeMap.get(ATTRIBUTE_LAST);
			if (Constants.CHECK_TRUE.equalsIgnoreCase(isLast)) {
				switchToLastWindow(driver);
			} else
				switchToWindow(driver);
		}
		LoggerUtil.debug("window title: " + driver.getWebDriver().getTitle());
		Thread.sleep(500);
		switchToDefaultContent(driver);
		Thread.sleep(500);
		// clear parent frame stack
		frameStack.clear();
		LoggerUtil.debug("End of switchToWindow.");
	}

	/**
	 * Method to click override link
	 * 
	 * @author Steepgraph Systems
	 * @param driver
	 * @return void
	 * @throws Exception
	 */
	@Override
	public void clickOverrideLink(Driver driver) throws Exception {
		driver.clickOverrideLink();
		// this is deprecated, please refer 3dx-tas.browser.acceptInsecureCerts=true and
		// update in Browser.properties

	}

	/**
	 * Method to click on an element by it's provided locator
	 * 
	 * @author Steepgraph Systems
	 * @param driver
	 * @param attributeMap
	 * @return void
	 * @throws Exception
	 */
	@Override
	public void clickElement(Driver driver, Map<String, String> attributeMap) throws Exception {
		LoggerUtil.debug("Start of clickElement.");
		if (attributeMap == null) {
			throw new Exception(" locatorType and locatorExpression attribute is not specified for clickElement tag");
		}

		if (!attributeMap.containsKey(Constants.LOCATOR_TYPE)) {
			throw new Exception(" locatorType attribute is not specified for clickElement tag.");
		}

		if (!attributeMap.containsKey(Constants.LOCATOR_EXPRESSION)) {
			throw new Exception(" locatorExpression attribute is not specified for clickElement tag.");
		}

		String strlocatorType = attributeMap.get(Constants.LOCATOR_TYPE);
		LoggerUtil.debug("strlocatorType: " + strlocatorType);
		String strlocatorExpression = attributeMap.get(Constants.LOCATOR_EXPRESSION);
		LoggerUtil.debug("strlocatorExpression: " + strlocatorExpression);

		String mode = attributeMap.get(Constants.ATTRIBUTE_MODE);
		if (mode == null || "".equalsIgnoreCase(mode)) {
			mode = Constants.MODE;
		}
		LoggerUtil.debug("mode: " + mode);

		String async = attributeMap.get(Constants.ATTRIBUTE_ASYNC);
		if (async == null || "".equalsIgnoreCase(async)) {
			async = Constants.CHECK_FALSE;
		}
		LoggerUtil.debug("async: " + async);

		String criteria = attributeMap.get(Constants.ATTRIBUTE_CRITERIA);
		if ("".equalsIgnoreCase(criteria)) {
			throw new IllegalArgumentException("criteria attribute can not be empty for clickElement TAG");
		}
		int retryCnt = 0;
		WebElement wbElement = driver.findElement(strlocatorType, strlocatorExpression);

		String highlight = attributeMap.get(Constants.ATTRIBUTE_HIGHLIGHT);
		if (highlight == null) {
			highlight = Constants.CHECK_FALSE;
			LoggerUtil.debug("WARNING!! : Default value FALSE is set for highlight attribute");
		} else if (Constants.CHECK_TRUE.equalsIgnoreCase(highlight)) {
			highLightElement(driver, attributeMap, wbElement);
		} else if (Constants.CHECK_FALSE.equalsIgnoreCase(highlight)) {
			LoggerUtil.debug("High light attribute is false so element will not be highlighted");
		} else {
			throw new Exception(
					"invalid Value of Highlight attribute for ClickElement TAG , Please provide a possible value : true or false");
		}

		if (criteria != null && !"".equalsIgnoreCase(criteria)) {
			String retryStr = attributeMap.get(Constants.ATTRIBUTE_RETRY);
			if (retryStr == null || "".equalsIgnoreCase(retryStr)) {
				retryCnt = 2;
			} else {
				retryCnt = Integer.parseInt(retryStr);
			}

			int exeCnt = 0;
			criteria = criteria.toLowerCase();
			boolean isVisible = true;
			LoggerUtil.debug("Retry Count : " + retryCnt);
			while (exeCnt < retryCnt) {
				switch (criteria) {

				case "selected":
					if (wbElement.isSelected()) {
						exeCnt = retryCnt;
						LoggerUtil.debug("Webelement found selected");
						break;
					} else {
						driver.click(wbElement, mode, async);
					}
					break;

				case "unselected":
					if (!wbElement.isSelected()) {
						exeCnt = retryCnt;
						LoggerUtil.debug(
								"WARNING :: WebElement found not selected , please select the WebElement to perform unselect");
						break;
					} else {
						driver.click(wbElement, mode, async);
					}
					break;

				case "invisible":
					if (isVisible) {
						driver.click(wbElement, mode, async);
					}
					try {
						Thread.sleep(2000);
						WebElement wb = driver.getWebDriver()
								.findElement(driver.getBy(strlocatorType, strlocatorExpression));
						if (!wb.isDisplayed()) {
							isVisible = false;
						}
					} catch (Exception e) {
						isVisible = false;
					}
					if (!isVisible) {
						exeCnt = retryCnt;
					}
					break;

				default:
					throw new Exception("ERROR: " + criteria
							+ " value of criteria attribute is not specified for clickElement tag.");
				}
				exeCnt++;
			}
		} else {
			driver.click(wbElement, mode, async);
		}

		LoggerUtil.debug("End of clickElement.");
	}

	/**
	 * Method to switch back to parent window
	 * 
	 * @author Steepgraph Systems
	 * @param driver
	 * @return void
	 * @throws Exception
	 */
	@Override
	public void switchToParentWindow(Driver driver) throws Exception {
		LoggerUtil.debug("Start of switchToParentWindow.");
		Thread.sleep(2000);

		String parentWindowHandle = getParentWindowFromStack();
		LoggerUtil.debug("parentWindowHandle: " + parentWindowHandle);
		if (parentWindowHandle != null && !parentWindowHandle.equalsIgnoreCase("")) {
			try {
				driver.getWebDriver().switchTo().window(parentWindowHandle);
			} catch (NoSuchWindowException e) {
				windowStack.clear();
				Set<String> winHandleSet = driver.getWebDriver().getWindowHandles();
				if (winHandleSet.size() == 1) {
					parentWindowHandle = winHandleSet.iterator().next().toString();
					driver.getWebDriver().switchTo().window(parentWindowHandle);
				} else {
					throw new Exception("More than 1 parent window present.");
				}
			}
			driver.getWebDriver().switchTo().defaultContent();
		}

		Thread.sleep(2000);
		// clear parent frame stack
		frameStack.clear();
		LoggerUtil.debug("End of switchToParentWindow.");
	}

	/**
	 * Method to switch to a slide-in frame (window/form)
	 * 
	 * @author Steepgraph Systems
	 * @param driver
	 * @param attributeMap
	 * @return void
	 * @throws Exception
	 */
	@Override
	public void switchToSlideInWindow(Driver driver, Map<String, String> attributeMap) throws Exception {
		LoggerUtil.debug("Start of switchToSlideInWindow.");
		driver.switchToFrame(Constants.SLIDE_IN_FRAME);
		addToParentFrameStack(Constants.SLIDE_IN_FRAME);
		LoggerUtil.debug("End of switchToSlideInWindow.");
	}

	/**
	 * Method to switch out of all frames and back to original window (default
	 * content)
	 * 
	 * @author Steepgraph Systems
	 * @param driver
	 * @return void
	 * @throws Exception
	 */
	@Override
	public void switchToDefaultContent(Driver driver) throws Exception {
		Thread.sleep(500);
		LoggerUtil.debug("Start of switchToDefaultContent.");
		driver.getWebDriver().switchTo().defaultContent();
		Thread.sleep(500);
		frameStack.clear();
		LoggerUtil.debug("End of switchToDefaultContent.");
	}

	/**
	 * Method to handle an alert pop up
	 * 
	 * @author Steepgraph Systems
	 * @param driver
	 * @param attributeMap
	 * @return void
	 * @throws Exception
	 */
	@Override
	public void handleAlert(Driver driver, Map<String, String> attributeMap) throws Exception {
		LoggerUtil.debug("Start of handleAlert.");

		if (attributeMap == null || !attributeMap.containsKey(Constants.ATTRIBUTE_ACTION)) {
			throw new IllegalArgumentException("Attribute action not specified for tag handleAlert.");
		}

		String strAction = attributeMap.get(Constants.ATTRIBUTE_ACTION);
		if (strAction == null || "".equalsIgnoreCase(strAction)) {
			throw new IllegalArgumentException("Attribute action is not specified for handleAlert.");
		}

		String optional = attributeMap.get(Constants.ATTRIBUTE_OPTIONAL);
		if (optional == null || "".equalsIgnoreCase(optional)) {
			optional = Constants.CHECK_TRUE;
		}

		LoggerUtil.debug("action: " + strAction);
		Alert alert = getAlert(driver);
		if (Constants.CHECK_FALSE.equalsIgnoreCase(optional) && alert == null) {
			throw new RuntimeException("No Alert present to handle");
		} else if (alert != null) {
			handleAlertAction(alert, strAction);
		}
		LoggerUtil.debug("End of handleAlert.");
	}

	/**
	 * Method to execute the testcase for clicking the portal command provided the
	 * title of command in portal as an i/p parameter
	 * 
	 * @param (driver, attributeMap)
	 */
	@Override
	public void clickPortalCommand(Driver driver, Map<String, String> attributeMap) throws Exception {
		LoggerUtil.debug("Start of clickPortalCommand.");

		if (attributeMap == null || !attributeMap.containsKey(Constants.ATTRIBUTE_TITLE)) {
			throw new Exception("Attribute title not specified for tag clickPortalCommand.");
		}

		String sTitle = attributeMap.get(Constants.ATTRIBUTE_TITLE);

		if (sTitle == null || sTitle.equalsIgnoreCase("")) {
			throw new Exception("Attribute title not specified for tag clickPortalCommand.");
		}

		switchToPortalDisplayFrame(driver, attributeMap);

		WebElement wPortalCommand = driver.findElement(
				By.xpath("//div[@id='divPowerView']//div[@id='pvChannelTabs']//table//td[text()='" + sTitle + "']"));
		driver.click(wPortalCommand);
		LoggerUtil.debug("End of clickPortalCommand.");
	}

	/**
	 * Method to click on category command of object. Provided the command title
	 * displayed on web-page.
	 * 
	 * @author Steepgraph Systems
	 * @param driver
	 * @param attributeMap
	 * @throws Exception
	 */
	@Override
	public void clickCategoryCommand(Driver driver, Map<String, String> attributeMap) throws Exception {
		LoggerUtil.debug("Start of clickCategoryCommand.");
		if (attributeMap == null || !attributeMap.containsKey(Constants.ATTRIBUTE_TITLE)) {
			throw new Exception("Attribute title not specified for tag clickCategoryCommand.");
		}

		String strCategoryName = attributeMap.get(Constants.ATTRIBUTE_TITLE);
		if (strCategoryName == null || strCategoryName.equalsIgnoreCase("")) {
			throw new Exception("Attribute title not specified for tag clickCategoryCommand.");
		}

		switchToDefaultContent(driver);

		WebElement wCatergoryCommand = driver
				.findElement(By.xpath("//div[@id='catMenu']//li//label[text()='" + strCategoryName + "']"));
		LoggerUtil.debug("Clicking on " + strCategoryName + " category command");
		driver.click(wCatergoryCommand);
		LoggerUtil.debug("End of clickCategoryCommand.");
	}

	/**
	 * Method to switch to the nested frames/ single frame on a web page
	 * 
	 * @author Steepgraph System
	 * @param driver
	 * @param attributeMap
	 * @throws Exception
	 */
	@Override
	public void switchToContentFrame(Driver driver, Map<String, String> attributeMap) throws Exception {
		LoggerUtil.debug("Start of switchToContentFrame.");
		driver.getWebDriver().switchTo().defaultContent();
		LoggerUtil.debug("Switch to frame : " + Constants.CONTENT_FRAME);
		driver.switchToFrame(Constants.CONTENT_FRAME);
		addToParentFrameStack(Constants.CONTENT_FRAME);
		LoggerUtil.debug("End of switchToContentFrame.");
	}

	/**
	 * Method to switch to the nested frames/ single frame on a web page
	 * 
	 * @param driver
	 * @param attributeMap
	 * @throws Exception
	 */
	@Override
	public void switchToDetailsDisplayFrame(Driver driver, Map<String, String> attributeMap) throws Exception {
		LoggerUtil.debug("Start of switchToDetailsDisplayFrame.");
		String currentFrame = driver.getCurrentFrame();
		if (!currentFrame.equalsIgnoreCase(Constants.CONTENT_FRAME)) {
			switchToContentFrame(driver, attributeMap);
		}
		LoggerUtil.debug("Switch to frame : " + Constants.DETAILS_DISPLAY_FRAME);
		driver.switchToFrame(Constants.DETAILS_DISPLAY_FRAME);
		addToParentFrameStack(Constants.DETAILS_DISPLAY_FRAME);
		LoggerUtil.debug("End of switchToDetailsDisplayFrame.");
	}

	@Override
	public void switchToFrameTableFrame(Driver driver, Map<String, String> attributeMap) throws Exception {
		LoggerUtil.debug("Start of switchToFrameTableFrame.");
		String currentFrame = driver.getCurrentFrame();
		if (!currentFrame.equalsIgnoreCase(Constants.CONTENT_FRAME)) {
			switchToContentFrame(driver, attributeMap);
		}
		LoggerUtil.debug("Switch to frame : " + Constants.FRAME_TABLE_FRAME);
		driver.switchToFrame(Constants.FRAME_TABLE_FRAME);
		addToParentFrameStack(Constants.FRAME_TABLE_FRAME);
		LoggerUtil.debug("End of switchToFrameTableFrame.");
	}

	/**
	 * Method to switch to the nested frames/ single frame on a web page
	 * 
	 * @param driver
	 * @param attributeMap
	 * @throws Exception
	 */
	@Override
	public void switchToPortalDisplayFrame(Driver driver, Map<String, String> attributeMap) throws Exception {
		LoggerUtil.debug("Start of switchToPortalDisplayFrame.");
		String currentFrame = driver.getCurrentFrame();
		if (!currentFrame.equalsIgnoreCase(Constants.CONTENT_FRAME)) {
			switchToContentFrame(driver, attributeMap);
		}

		String frameName = driver.findElement(By.xpath("//iframe")).getAttribute("name");
		if (frameName.contains(Constants.FRAME_TABLE_FRAME)) {
			switchToFrameTableFrame(driver, attributeMap);
		} else if (frameName.contains(Constants.DETAILS_DISPLAY_FRAME)) {
			switchToDetailsDisplayFrame(driver, attributeMap);
		}

		LoggerUtil.debug("Switch to frame : " + Constants.PORTAL_DISPLAY_FRAME);
		driver.switchToFrame(Constants.PORTAL_DISPLAY_FRAME);
		addToParentFrameStack(Constants.PORTAL_DISPLAY_FRAME);
		LoggerUtil.debug("End of switchToPortalDisplayFrame.");
	}

	/**
	 * Method to switch to the nested frames/ single frame on a web page
	 * 
	 * @param frameName/s
	 */
	@Override
	public void switchToFrame(Driver driver, Map<String, String> attributeMap) throws Exception {
		LoggerUtil.debug("Start of switchToFrame.");
		if (attributeMap == null) {
			throw new Exception("Attribute name or index or refid is missing for tag switchToFrame.");
		}

		String frameName = attributeMap.get(Constants.ATTRIBUTE_NAME);
		String frameElementRefId = attributeMap.get(Constants.ATTRIBUTE_REFID);
		String strIndex = attributeMap.get(Constants.ATTRIBUTE_INDEX);
		String framexpath = attributeMap.get(Constants.LOCATOR_TYPE);

		if (frameName != null && !"".equals(frameName)) {
			LoggerUtil.debug("Switching to frame: " + frameName);
			driver.switchToFrame(frameName);
			addToParentFrameStack(frameName);
		} else if (strIndex != null && !"".equals(strIndex)) {
			LoggerUtil.debug("strIndex: " + strIndex);
			Integer frameIndex = Integer.parseInt(strIndex);
			driver.switchToFrame(frameIndex);
			addToParentFrameStack(frameIndex);
		} else if (frameElementRefId != null && !"".equals(frameElementRefId)) {
			if (!webElementMap.containsKey(frameElementRefId))
				throw new Exception(
						"Element not found using refid. Please use find element tag to find element then use its id as refid");

			LoggerUtil.debug("Switching to refid: " + frameElementRefId);
			WebElement frameWebElement = webElementMap.get(frameElementRefId);
			driver.switchToFrame(frameWebElement);
			addToParentFrameStack(frameWebElement);
		} else if (framexpath != null && !"".equals(framexpath)) {
			String strlocatorType = attributeMap.get(Constants.LOCATOR_TYPE);
			LoggerUtil.debug("strlocatorType: " + strlocatorType);
			String strlocatorExpression = attributeMap.get(Constants.LOCATOR_EXPRESSION);
			LoggerUtil.debug("strlocatorExpression: " + strlocatorExpression);
			WebElement frameWebElement = driver.findElement(strlocatorType, strlocatorExpression);
			LoggerUtil.debug("frameWebElement: " + frameWebElement);
			driver.switchToFrame(frameWebElement);
			addToParentFrameStack(frameWebElement);
		} else
			throw new Exception("Attribute name or index or refid is missing for tag switchToFrame.");

		LoggerUtil.debug("End of switchToFrame.");
	}

	/**
	 * This method selects indented table rows provided the level/text of the row as
	 * an input in csv column matches with id attribute of tag.
	 * 
	 * @author Steepgraph Systems
	 * @param driver
	 * @param attributeMap
	 * @throws Exception
	 */
	@Override
	public void selectIndentedTableRow(Driver driver, Map<String, String> attributeMap, String input) throws Exception {
		if (attributeMap == null)
			throw new Exception("id,criteria,expand attributes are not defined for selectIndentedTableRow tag.");

		String strCriteria = attributeMap.get(Constants.ATTRIBUTE_CRITERIA);
		if (strCriteria == null || "".equals(strCriteria))
			throw new Exception("criteria are not defined for selectIndentedTableRow tag.");

		IndentedTableCriteria itCriteria = IndentedTableCriteria.valueOf(strCriteria.toLowerCase());
		switch (itCriteria) {
		case level:
			attributeMap.put(Constants.ATTRIBUTE_LEVEL, input);
			selectIndentedTableRowByLevel(driver, attributeMap);
			break;

		case text:
			attributeMap.put(Constants.INPUTTYPE_TEXT, input);
			selectIndentedTableRowByText(driver, attributeMap);
			break;

		default:
			throw new Exception("criteria attribute is not valid in selectIndentedTableRow tag.");

		}
	}

	/**
	 * This method to register indented table row for future references.
	 * 
	 * @author Steepgraph Systems
	 * @param driver
	 * @param weRowCheckBox
	 * @param attributeMap
	 * @throws Exception
	 */
	public void registerIndentedTableRow(Driver driver, WebElement weRowCheckBox, Map<String, String> attributeMap)
			throws Exception {
		if (attributeMap != null
				&& Constants.CHECK_TRUE.equalsIgnoreCase(attributeMap.get(Constants.ATTRIBUTE_REGISTER))) {
			return;
		}

		LoggerUtil.debug("Start of registerIndentedTableRow.");
		WebElement weTableRow = driver.findElement(weRowCheckBox, By.xpath("./ancestor::tr[1]"));
		String tableRowId = weTableRow.getAttribute(Constants.ATTRIBUTE_ID);
		attributeMap.put(Constants.LOCATOR_TYPE, Constants.INPUTTYPE_XPATH);
		attributeMap.put(Constants.LOCATOR_EXPRESSION,
				"//table[@id='treeBodyTable']/tbody/tr[@id='" + tableRowId + "']");
		attributeMap.put(Constants.ATTRIBUTE_ATTRIBUTE, Constants.ATTRIBUTE_ID);
		String rowSelectionText = attributeMap.get(Constants.ATTRIBUTE_ID);
		if (rowSelectionText == null) {
			rowSelectionText = attributeMap.get(Constants.ATTRIBUTE_INPUT);
		}
		registerObject(driver, attributeMap, rowSelectionText);
		LoggerUtil.debug("End of registerIndentedTableRow.");

	}

	/**
	 * This method selects indented table rows provided the text of the row as an
	 * input in csv
	 * 
	 * @author Steepgraph Systems
	 * @param driver
	 * @param attributeMap
	 * @throws Exception
	 */
	public void selectIndentedTableRowByText(Driver driver, Map<String, String> attributeMap) throws Exception {
		LoggerUtil.debug("Start of selectIndentedTableRowByText.");
		String rowSelectionText = attributeMap.get(Constants.INPUTTYPE_TEXT);
		String tableSection = attributeMap.get(Constants.ATTRIBUTE_SECTION);
		LoggerUtil.debug("rowText: " + rowSelectionText);
		String expand = attributeMap.get(Constants.ATTRIBUTE_EXPAND);
		LoggerUtil.debug("expand: " + expand);
		String dialog = attributeMap.get(Constants.ATTRIBUTE_DIALOG);
		LoggerUtil.debug("dialog: " + dialog);

		if (expand == null || "".equals(expand))
			expand = Constants.CHECK_TRUE;
		if (dialog == null || "".equals(dialog))
			dialog = Constants.CHECK_FALSE;
		WebElement weParentElement = null;
		WebElement weRowCheckBox = null;
		if (rowSelectionText == null || "".equals(rowSelectionText))
			throw new Exception(
					"row selection text is not defined in csv column whose name matches with id of selectIndentedTableRow tag.");
		if ("Table".equalsIgnoreCase(tableSection)) {
			String rowNum = driver
					.findElement(By.xpath("//table[@id='bodyTable']//tr/td[text()='" + rowSelectionText + "']"))
					.getAttribute("rmbrow");
			weRowCheckBox = driver.findElement(
					By.xpath("//table[@id='treeBodyTable']//tr[@id='" + rowNum + "']/td/input[@type='checkbox']"));
		} else {
			if (dialog.equalsIgnoreCase(Constants.CHECK_TRUE)) {
				weRowCheckBox = driver.findElement(By.xpath("//table[@id='treeBodyTable']//tr/td[@rmbid='"
						+ rowSelectionText + "']//input[@type='checkbox']"));
			} else {
				weRowCheckBox = driver.findElement(By.xpath("//table[@id='treeBodyTable']//tr/td[@title='"
						+ rowSelectionText + "']/../td//input[@type='checkbox']"));
			}
		}
		LoggerUtil.debug("weRowCheckBox: " + weRowCheckBox);

		if (dialog.equalsIgnoreCase(Constants.CHECK_TRUE)) {
			weParentElement = driver.findElement(weRowCheckBox, By.xpath("../.."));
		} else {
			weParentElement = driver
					.findElement(By.xpath("//table[@id='treeBodyTable']//tr/td[@title='" + rowSelectionText + "']/.."));
		}

		LoggerUtil.debug("weParentElement : " + weParentElement);

		String strRowCheckboxId = weParentElement.getAttribute(Constants.ATTRIBUTE_ID);
		LoggerUtil.debug("strRowCheckboxId: " + strRowCheckboxId);

		String selectCheckBox = attributeMap.get(Constants.ATTRIBUTE_SELECTCHECKBOX);
		if (selectCheckBox == null || "".equals(selectCheckBox))
			selectCheckBox = Constants.CHECK_TRUE;

		LoggerUtil.debug("selectCheckBox : " + selectCheckBox);

		if (Constants.CHECK_TRUE.equalsIgnoreCase(selectCheckBox))
			driver.click(weRowCheckBox);

		registerIndentedTableRow(driver, weRowCheckBox, attributeMap);
		if (expand.equalsIgnoreCase(Constants.CHECK_TRUE)) {
			expandIndentedtableRow(driver, weParentElement, strRowCheckboxId);
		}
		LoggerUtil.debug("End of selectIndentedTableRowByText.");
	}

	/**
	 * This method selects indented table rows provided the level of the row as an
	 * input in csv
	 * 
	 * @author Steepgraph Systems
	 * @param driver
	 * @param attributeMap
	 * @throws Exception
	 */
	public void selectIndentedTableRowByLevel(Driver driver, Map<String, String> attributeMap) throws Exception {
		LoggerUtil.debug("Start of selectIndentedTableRowByLevel.");

		String level = attributeMap.get(Constants.ATTRIBUTE_LEVEL);
		LoggerUtil.debug("level: " + level);

		String expand = attributeMap.get(Constants.ATTRIBUTE_EXPAND);
		LoggerUtil.debug("expand: " + expand);

		String strIsNew = attributeMap.get(Constants.ATTRIBUTE_ISNEW);
		LoggerUtil.debug("strIsNew: " + strIsNew);

		if (strIsNew == null || "".equals(strIsNew))
			strIsNew = Constants.CHECK_FALSE;

		if (level == null || "".equals(level))
			level = Constants.CHECK_ALL;
		if (expand == null || "".equals(expand))
			expand = Constants.CHECK_TRUE;

		if (Constants.CHECK_ALL.equalsIgnoreCase(level)) {
			selectAllTableRows(driver, attributeMap);
		} else {
			String[] levelArray = level.split(",");
			String strRowCheckboxId = "0", strRowCheckboxIdTmp = "";

			WebElement weParentElement = null;
			boolean isRowIdfound = false;

			int arrSize = levelArray.length;

			// first try to find the element with the level given by user in test
			// case xml.
			try {
				if (strIsNew.equalsIgnoreCase(Constants.CHECK_FALSE)) {
					weParentElement = driver
							.findElement(By.xpath("//table[@id='treeBodyTable']/tbody/tr[@id='" + level + "']"));

					strRowCheckboxId = level;
					isRowIdfound = true;
				} else {
					int indexOfComma = level.lastIndexOf(",");
					LoggerUtil.debug("indexOfComma: " + indexOfComma);
					if (indexOfComma > 0) {

						String subLevel = level.substring(0, indexOfComma);
						LoggerUtil.debug("subLevel: " + subLevel);
						String newRowPosition = level.substring(indexOfComma + 1, level.length());
						LoggerUtil.debug("newRowPosition: " + newRowPosition);
						int iNewRowPosition = Integer.parseInt(newRowPosition) + 1;
						LoggerUtil.debug("iNewRowPosition: " + iNewRowPosition);
						weParentElement = driver.findElement(By.xpath("//table[@id='treeBodyTable']/tbody/tr[@id='"
								+ subLevel + "']/following-sibling::tr[" + iNewRowPosition + "]"));
						strRowCheckboxId = level;
						isRowIdfound = true;
					}

				}
			} catch (Exception e) {
				// skipt this exception
				LoggerUtil.debug("initial trial with row checkbox id :" + strRowCheckboxId + " failed");
			}

			LoggerUtil.debug("isRowIdfound: " + isRowIdfound);

			// below code should be executed when the row ids are not sequential.
			// while testing a behavior was observed that if a row is deleted/added
			// the row id was not in sync with its sibling or parent row. To handle
			// it below code is written
			if (!isRowIdfound) {
				for (int i = 0; i < arrSize; i++) {
					String strElementLevel = levelArray[i].trim();

					int index = Integer.parseInt(strElementLevel);
					int maxIndex = index + 20;
					while (index < maxIndex) {
						isRowIdfound = false;

						if (!"".equals(strRowCheckboxIdTmp))
							strRowCheckboxIdTmp = strRowCheckboxId + "," + index;
						else
							strRowCheckboxIdTmp = String.valueOf(index);

						try {
							if (i == 0) {
								weParentElement = driver.findElement(By.xpath(
										"//table[@id='treeBodyTable']/tbody/tr[@id='" + strRowCheckboxIdTmp + "']"));
							} else {

								weParentElement = driver.findElement(weParentElement,
										By.xpath("../tr[@id='" + strRowCheckboxIdTmp + "']"));

							}
							strRowCheckboxId = strRowCheckboxIdTmp;
							LoggerUtil.debug("trial " + index + "row checkbox id " + strRowCheckboxIdTmp + " found");
							isRowIdfound = true;

							if (expand.equalsIgnoreCase(Constants.CHECK_TRUE)) {
								expandIndentedtableRow(driver, weParentElement, strRowCheckboxId);
							}

							break;
						} catch (Exception e) {
							LoggerUtil.debug("trial " + index + " row checkbox id :" + strRowCheckboxIdTmp + " failed");
						}
						index++;

					}

					if (!isRowIdfound) {
						throw new Exception("Row with level " + level + " not found in the indented table.");
					}

				}
			}

			LoggerUtil.debug("strRowCheckboxId: " + strRowCheckboxId);

			LoggerUtil.debug("weParentElement: " + weParentElement);

			String rowId = weParentElement.getAttribute(Constants.ATTRIBUTE_ID);
			LoggerUtil.debug("rowId: " + rowId);
			// -------//
			WebElement weCheckBox = driver.findElement(weParentElement,
					By.xpath(".//descendant::input[@id='rmbrow-" + rowId + "']"));

			String selectCheckBox = attributeMap.get(Constants.ATTRIBUTE_SELECTCHECKBOX);
			if (selectCheckBox == null || "".equals(selectCheckBox)
					|| (!selectCheckBox.equalsIgnoreCase(Constants.CHECK_TRUE)
							&& !selectCheckBox.equalsIgnoreCase(Constants.CHECK_FALSE))) {
				LoggerUtil
						.debug("WARNING!!! selectCheckBox value should be true or false, it's default value is true : "
								+ selectCheckBox);
				selectCheckBox = Constants.CHECK_TRUE;
			}
			LoggerUtil.debug("selectCheckBox : " + selectCheckBox);

			if (Constants.CHECK_TRUE.equalsIgnoreCase(selectCheckBox))
				driver.click(weCheckBox);

			if (expand.equalsIgnoreCase(Constants.CHECK_TRUE)) {
				expandIndentedtableRow(driver, weParentElement, strRowCheckboxId);
			}

			registerIndentedTableRow(driver, weCheckBox, attributeMap);
		}
		LoggerUtil.debug("End of selectIndentedTableRowByLevel.");
	}

	/**
	 * This method is used to expand row if its not already expanded
	 * 
	 * @author Steepgraph Systems
	 * @param driver
	 * @param weExpandImg
	 */
	public void expandIndentedtableRow(Driver driver, WebElement weParentRow, String strRowId) throws Exception {
		WebElement weExpandImg = driver.findElement(weParentRow,
				By.xpath(".//descendant::img[@id='img_" + strRowId + "']"));
		LoggerUtil.debug("weExpandImg: " + weExpandImg);
		String strImgAttr = weExpandImg.getAttribute("src");
		LoggerUtil.debug("strImgAttr : " + strImgAttr);
		if (strImgAttr != null && strImgAttr.contains("images/utilTreeLineNodeClosedSB.gif")) {
			driver.click(weExpandImg);
		}
	}

	/**
	 * This method expands all rows in an indented table.
	 * 
	 * @author Steepgraph Systems
	 * @param driver
	 */
	public void expandAll(Driver driver) throws Exception {

		LoggerUtil.debug("Start of expandAll.");
		WebElement wbElementExpandAll = driver
				.findElement(By.xpath("//div[@id='divToolbar']//td[@title='Expand All']"));
		driver.click(wbElementExpandAll);
		LoggerUtil.debug("End of expandAll.");
	}

	/**
	 * This method expands n rows in an indented table. "n" is the input from the
	 * user, based on which this method will decide the level expansion
	 * 
	 * @author Steepgraph Systems
	 * @param driver
	 * @param level
	 */
	public void expandNLevels(Driver driver, int level) throws Exception {
		LoggerUtil.debug("Start of expandNLevels.");
		LoggerUtil.debug("Expanding to " + level + " levels.");
		WebElement wbElementExpanNLevel = driver
				.findElement(By.xpath("//div[@id='divToolbar']//td[@title='Expand n levels']"));
		driver.click(wbElementExpanNLevel);

		Select selectElement = new Select(
				driver.findElement(By.xpath("//div[@class='menu-content']//select[@id='emxExpandFilter']")));
		selectElement.selectByVisibleText(String.valueOf(level));

		WebElement wbElementApply = driver.findElement(By.xpath("//div[@class='menu-content']//input[@value='Apply']"));
		driver.click(wbElementApply);
		LoggerUtil.debug("End of expandNLevels.");
	}

	/**
	 * Upload file using drag and drop.
	 * 
	 * @author Steepgraph Systems
	 * @param driver
	 * @param attributeMap
	 * @throws Exception
	 */
	@Override
	public void uploadFileUsingDragAndDrop(Driver driver, Map<String, String> attributeMap) throws Exception {
		LoggerUtil.debug("Start of uploadFileUsingDragAndDrop.");
		// switchToDefaultContent(driver);

		if (attributeMap == null || !attributeMap.containsKey(Constants.ATTRIBUTE_PATH)) {
			throw new Exception("Attribute path not specified for tag UploadFileUsingDragAndDrop.");
		}

		String sPath = attributeMap.get(Constants.ATTRIBUTE_PATH);
		if (sPath == null || "".equals(sPath))
			throw new Exception("Attribute path not specified for tag UploadFileUsingDragAndDrop.");

		WebElement inputField = null;

		// drag and drop to web element located by locator expression
		if (attributeMap.containsKey(Constants.LOCATOR_TYPE) && attributeMap.containsKey(Constants.LOCATOR_EXPRESSION))
			inputField = findElement(driver, attributeMap, true);
		else
			inputField = driver.findElement(By.xpath(
					"//div[@id='ExtpageHeadDiv']//div[@id='divExtendedHeaderDocuments']//form//div[@id='divDrag']"));

		DropFile(driver, new File(sPath), inputField, 0, 0);
		LoggerUtil.debug("End of uploadFileUsingDragAndDrop.");
	}

	/**
	 * Method to execute java script utility to drag and drop file on web page
	 * 
	 * @author Steepgraph Systems
	 * @param filePath
	 * @param target
	 * @param offsetX
	 * @param offsetY
	 */
	public void DropFile(Driver driver, File filePath, WebElement target, int offsetX, int offsetY) throws Exception {
		LoggerUtil.debug("Start of DropFile.");
		if (!filePath.exists())
			throw new WebDriverException("File not found: " + filePath.toString());

		JavascriptExecutor jse = (JavascriptExecutor) driver.getWebDriver();
		Wait<WebDriver> wait = driver.getWait();

		Thread.sleep(5000);

		StringBuilder sb = new StringBuilder("var target = arguments[0],");
		sb.append(" offsetX = arguments[1],");
		sb.append(" offsetY = arguments[2],");
		sb.append(" document = target.ownerDocument || document,");
		sb.append(" window = document.defaultView || window;");
		sb.append(" var input = document.createElement('INPUT');");
		sb.append(" input.type = 'file';").append("input.id='seleniumFileUpload_input';");
		sb.append(" input.style.display = 'block';");
		sb.append(" input.onchange = function () {");
		sb.append(" var rect = target.getBoundingClientRect(),");
		sb.append(" x = rect.left + (offsetX || (rect.width >> 1)),");
		sb.append(" y = rect.top + (offsetY || (rect.height >> 1)),");
		sb.append(" dataTransfer = { files: this.files };" + "");
		sb.append(" ['dragenter', 'dragover', 'drop'].forEach(function (name) {");
		sb.append(" var evt = document.createEvent('MouseEvent');");
		sb.append(" evt.initMouseEvent(name, !0, !0, window, 0, 0, 0, x, y, !1, !1, !1, !1, 0, null);");
		sb.append(" evt.dataTransfer = dataTransfer;" + "    target.dispatchEvent(evt);" + "  });" + "");
		sb.append(" setTimeout(function () { document.body.removeChild(input); }, 25);" + "};");
		sb.append(" document.body.appendChild(input);" + "return input;");

		WebElement input = (WebElement) jse.executeScript(sb.toString(), target, offsetX, offsetY);
		LoggerUtil.debug("Executing drag and drop file.");
		String absoluteFilePath = filePath.getAbsoluteFile().toString();
		LoggerUtil.debug("absoluteFilePath " + absoluteFilePath);
		input.sendKeys(absoluteFilePath);
		try {
			wait.until(ExpectedConditions.stalenessOf(input));
			LoggerUtil.debug("element is no longer attached to the DOM");
		} catch (Exception e) {
			LoggerUtil.debug("element is still attached to the DOM to drop more files");
		}

		LoggerUtil.debug("End of DropFile.");
	}

	/**
	 * Method to input text into given field with data read from CSV file
	 * 
	 * @author Steepgraph Systems
	 * @param driver
	 * @param attributeMap
	 * @param text
	 * @return void
	 * @throws Exception
	 */
	@Override
	public void inputText(Driver driver, Map<String, String> attributeMap, String text) throws Exception {
		LoggerUtil.debug("Start of inputText.");
		if (text == null || "".equals(text))
			LoggerUtil.debug(" 'ID' OR 'input' attribute is not defined properly for this tag, as the value is = " + "'"
					+ text + "'");

		String strlocatorType = attributeMap.get(Constants.LOCATOR_TYPE);
		LoggerUtil.debug("strlocatorType: " + strlocatorType);

		String strlocatorExpression = attributeMap.get(Constants.LOCATOR_EXPRESSION);
		LoggerUtil.debug("strlocatorExpression: " + strlocatorExpression);

		String strElementID = attributeMap.get(Constants.ATTRIBUTE_REFID);
		LoggerUtil.debug("refid:" + strElementID);

		WebElement wbElement = null;

		if ((strElementID == null || "".equals(strElementID)) && ((strlocatorType == null || "".equals(strlocatorType))
				|| (strlocatorExpression == null || "".equals(strlocatorExpression)))) {
			throw new Exception(
					"Attribute (refid) or (locatorType and locatorExpression) is not define for inputText tag.");
		} else if (strElementID != null && !"".equals(strElementID))
			wbElement = webElementMap.get(strElementID);
		else
			wbElement = driver.findElement(strlocatorType, strlocatorExpression);

		String mode = attributeMap.get(Constants.ATTRIBUTE_MODE);
		if (mode == null || "".equals(mode))
			mode = Constants.MODE;

		LoggerUtil.debug("mode: " + mode);

		String encrypted = attributeMap.get("encrypted");
		if (encrypted != null && "true".equalsIgnoreCase(encrypted)) {
			text = DecryptionUtil.decrypt(text);
		}
		LoggerUtil.debug("encrypted: " + encrypted);

		String strNewLine = attributeMap.get("newLine");
		if (null != strNewLine && strNewLine.equalsIgnoreCase(Constants.CHECK_TRUE)) {
			text = text.replace("|", "\n");
		}
		LoggerUtil.debug("newline: " + strNewLine);

		// WebElement wbElement = driver.findElement(strlocatorType,
		// strlocatorExpression);
		highLightElement(driver, attributeMap, wbElement);
		driver.click(wbElement);
		driver.writeText(wbElement, text, mode);
		LoggerUtil.debug("End of inputText.");
	}

	/**
	 * Method to select color into given field with data read from CSV file
	 * 
	 * @author Steepgraph Systems
	 * @param driver
	 * @param attributeMap
	 * @param color
	 * @return void
	 * @throws Exception
	 */

	@Override
	public void selectColor(Driver driver, Map<String, String> attributeMap) throws Exception {
		LoggerUtil.debug("Start of selectcolor.");

		String color = attributeMap.get("color");
		LoggerUtil.debug("color: " + color);
		if (color == null || "".equals(color))
			throw new Exception(
					"color is not defined in csv column whose column name matches with id attribute of this tag.");

		String strlocatorType = attributeMap.get(Constants.LOCATOR_TYPE);
		LoggerUtil.debug("strlocatorType: " + strlocatorType);
		if (strlocatorType == null || "".equals(strlocatorType))
			throw new Exception("Attribute locatorType is not define for selectorcolor tag.");

		String strlocatorExpression = attributeMap.get(Constants.LOCATOR_EXPRESSION);
		LoggerUtil.debug("strlocatorExpression: " + strlocatorExpression);
		if (strlocatorExpression == null || "".equals(strlocatorExpression))
			throw new Exception("Attribute locatorExpression is not define for selectorcolor tag.");

		WebElement wbElement = driver.findElement(strlocatorType, strlocatorExpression);
		highLightElement(driver, attributeMap, wbElement);
		driver.setValue(wbElement, color);

		LoggerUtil.debug("End of selectcolor");
	}

	/**
	 * Method to maximise browser window
	 * 
	 * @author Steepgraph Systems
	 * @param driver
	 * @return void
	 * @throws Exception
	 */
	@Override
	public void maximiseWindow(Driver driver) throws Exception {
		LoggerUtil.debug("Start of maximiseWindow.");
		Thread.sleep(5000);
		driver.getWebDriver().manage().window().maximize();
		LoggerUtil.debug("End of maximiseWindow.");
	}

	/**
	 * Method to upload file to 3DExperience. file path and locator expression of
	 * browse button.
	 * 
	 * @author SteepGraph Systems
	 * @param driver
	 * @param attributeMap
	 * @throws Exception
	 */
	public void uploadFileUsingLocator(Driver driver, Map<String, String> attributeMap, String filePath)
			throws Exception {
		LoggerUtil.debug("Start of uploadFileUsingUploadCommand.");

		if (attributeMap == null) {
			throw new Exception("attribute path not specified for tag uploadFileUsingUploadCommand.");
		}

		if (filePath == null || filePath.isEmpty()) {
			throw new Exception("attribute path not specified in csv file or id cannot be blank.");
		}

		String strFilePath = attributeMap.get(Constants.ATTRIBUTE_PATH);

		if (strFilePath != null && !strFilePath.isEmpty()) {
			filePath = strFilePath;
		}

		LoggerUtil.debug("filePath : " + filePath);

		File file = new File(filePath);
		if (!file.exists())
			throw new Exception(filePath + " file doesn't exist");

		String absoulteFilePath = file.getAbsolutePath();
		LoggerUtil.debug("absoulteFilePath: " + absoulteFilePath);

		WebElement wbBrowseButton = findElement(driver, attributeMap, true);
		wbBrowseButton.sendKeys(absoulteFilePath);

		LoggerUtil.debug("End of uploadFileUsingUploadCommand.");
	}

	/**
	 * Method to upload file to 3DDashboard.
	 * 
	 * @author SteepGraph Systems
	 * @param driver
	 * @param attributeMap
	 * @throws Exception
	 */
	public void uploadFileForDashboard(Driver driver, Map<String, String> attributeMap) throws Exception {
		LoggerUtil.debug("Start of uploadFileForDashboard.");
		if (attributeMap == null || !attributeMap.containsKey(Constants.ATTRIBUTE_PATH)) {
			throw new Exception("attribute path not specified for tag uploadFileForDashboard.");
		}

		String filePath = attributeMap.get(Constants.ATTRIBUTE_PATH);
		LoggerUtil.debug("filePath : " + filePath);
		if (filePath == null || "".equalsIgnoreCase(filePath))
			throw new Exception("path attribute is not defined for uploadFileForDashboard tag.");

		File file = new File(filePath);
		if (!file.exists())
			throw new Exception(filePath + " file doesn't exist");

		String absoulteFilePath = file.getAbsolutePath();
		LoggerUtil.debug("absoulteFilePath: " + absoulteFilePath);

		WebElement wbBrowseButton = findElement(driver, attributeMap, false);
		LoggerUtil.debug("wbBrowseButton : " + wbBrowseButton);
		DropFileForDashboard(driver, new File(absoulteFilePath), wbBrowseButton, 0, 0);

		LoggerUtil.debug("End of uploadFileForDashboard.");
	}

	/**
	 * Method to execute java script utility to drag and drop file on 3ddashboard
	 * 
	 * @author Steepgraph Systems
	 * @param filePath
	 * @param target
	 * @param offsetX
	 * @param offsetY
	 */
	public void DropFileForDashboard(Driver driver, File filePath, WebElement target, int offsetX, int offsetY)
			throws Exception {
		LoggerUtil.debug("Start of DropFileForDashboard.");
		if (!filePath.exists())
			throw new WebDriverException("File not found: " + filePath.toString());

		JavascriptExecutor jse = (JavascriptExecutor) driver.getWebDriver();
		Wait<WebDriver> wait = driver.getWait();

		Thread.sleep(5000);

		StringBuilder sb = new StringBuilder(
				"for (var b = arguments[0], k = arguments[1], l = arguments[2], c = b.ownerDocument, m = 0;;) {");
		sb.append("var e = b.getBoundingClientRect()," + "        g = e.left + (k || e.width / 2),");
		sb.append("h = e.top + (l || e.height / 2)," + "        f = c.elementFromPoint(g, h);");
		sb.append("if (f && b.contains(f)) break;"
				+ "    if (1 < ++m) throw b = Error('Element not interractable'), b.code = 15, b;");
		sb.append("b.scrollIntoView({" + "        behavior: 'instant'," + "        block: 'center',"
				+ "        inline: 'center'");
		sb.append("})" + "}" + "var a = c.createElement('INPUT');" + "a.setAttribute('type', 'file');");
		sb.append("a.setAttribute('style', 'position:fixed;z-index:2147483647;left:0;top:0;');"
				+ "a.onchange = function() {");
		sb.append("var b = {" + "        effectAllowed: 'all'," + "        dropEffect: 'none',"
				+ "        types: ['Files'],");
		sb.append("files: this.files," + "        setData: function() {}," + "        getData: function() {},");
		sb.append("clearData: function() {}," + "        setDragImage: function() {}" + "    };");
		sb.append("window.DataTransferItemList && (b.items = Object.setPrototypeOf([Object.setPrototypeOf({"
				+ "        kind: 'file',");
		sb.append("type: this.files[0].type," + "        file: this.files[0]," + "        getAsFile: function() {");
		sb.append("return this.file" + "        }," + "        getAsString: function(b) {");
		sb.append("var a = new FileReader;" + "            a.onload = function(a) {"
				+ "                b(a.target.result)");
		sb.append("};" + "            a.readAsText(this.file)" + "        }");
		sb.append("}, DataTransferItem.prototype)], DataTransferItemList.prototype));");
		sb.append("Object.setPrototypeOf(b, DataTransfer.prototype);"
				+ "    ['dragenter', 'dragover', 'drop'].forEach(function(a) {");
		sb.append("var d = c.createEvent('DragEvent');");
		sb.append("d.initMouseEvent(a, !0, !0, c.defaultView, 0, 0, 0, g, h, !1, !1, !1, !1, 0, null);");
		sb.append("Object.setPrototypeOf(d, null);" + "        d.dataTransfer = b;");
		sb.append("Object.setPrototypeOf(d, DragEvent.prototype);" + "        f.dispatchEvent(d)" + "    });");
		sb.append("a.parentElement.removeChild(a)" + "};" + "c.documentElement.appendChild(a);"
				+ "a.getBoundingClientRect();");
		sb.append("return a;");

		WebElement input = (WebElement) jse.executeScript(sb.toString(), target, offsetX, offsetY);
		LoggerUtil.debug("Executing drag and drop file.");
		String absoluteFilePath = filePath.getAbsoluteFile().toString();
		LoggerUtil.debug("absoluteFilePath " + absoluteFilePath);
		input.sendKeys(absoluteFilePath);
		wait.until(ExpectedConditions.stalenessOf(input));
		LoggerUtil.debug("End of DropFileForDashboard.");
	}

	/**
	 * @author Steepgraph Systems
	 * @param driver
	 * @param attributeMap
	 * @throws Exception
	 */
	@Override
	public void selectAllTableRows(Driver driver, Map<String, String> attributeMap) throws Exception {
		LoggerUtil.debug("End of selectAllTableRows.");
		LoggerUtil.debug("Selecting all files in the table.");
		WebElement wSelectAllTableRows = driver
				.findElement(By.xpath("//form[@name='emxTableForm']//table//tr//input[@name='chkList']"));
		driver.click(wSelectAllTableRows);
		LoggerUtil.debug("End of selectAllTableRows.");
	}

	/**
	 * @author Steepgraph Systems
	 * @param driver
	 * @param attributeMap
	 * @param browserName
	 * @throws Exception
	 */
	@Override
	public void downloadFileUsingCommand(Driver driver, Map<String, String> attributeMap, String browserName,
			String strInputFilePath) throws Exception {
		LoggerUtil.debug("Start of downloadFileUsingCommand.");
		// To select the table row which user wants to download

		if (attributeMap == null || !attributeMap.containsKey(Constants.ATTRIBUTE_COMMANDLABEL.toLowerCase()))
			throw new Exception("commandlabel attribute is missing in DownloadFileUsingCommand tag.");

		String commandLabel = attributeMap.get(Constants.ATTRIBUTE_COMMANDLABEL.toLowerCase());
		LoggerUtil.debug("commandLabel : " + commandLabel);
		if (commandLabel == null || "".equalsIgnoreCase(commandLabel))
			throw new Exception("attribute commandlabel is empty in DownloadFileUsingCommand tag.");

		openActionToolbarMenu(driver, attributeMap);
		LoggerUtil.debug("action menu clicked");

		downloadFile(driver, null, browserName);
		LoggerUtil.debug("End of downloadFileUsingCommand.");
	}

	/**
	 * Method to download the file by clicking on download icon for table row. input
	 * for the data row *
	 * 
	 * @author SteepGraph Systems
	 * @param driver
	 * @param attributeMap
	 * @param browserName
	 */
	@Override
	public void downloadFileUsingIcon(Driver driver, Map<String, String> attributeMap, String browserName,
			String strInputFilePath) throws Exception {
		LoggerUtil.debug("Start of downloadFileUsingIcon.");

		WebElement weTableRowColumn = getIndentedTableCell(driver, attributeMap);
		LoggerUtil.debug("weTableRowColumn : " + weTableRowColumn);

		WebElement wbDownload = driver.findElement(weTableRowColumn, By.xpath("a/img[@title='Download']"));
		LoggerUtil.debug("wbDownload : " + wbDownload);

		downloadFile(driver, wbDownload, browserName);
		LoggerUtil.debug("End of downloadFileUsingIcon.");
	}

	/**
	 * Method to download the file by clicking given a web element. input for the
	 * data row *
	 * 
	 * @author SteepGraph Systems
	 * @param driver
	 * @param attributeMap
	 * @param browserName
	 */
	@Override
	public void downloadFileUsingLocator(Driver driver, Map<String, String> attributeMap, String browserName)
			throws Exception {
		LoggerUtil.debug("Start of downloadFileUsingLocator.");
		WebElement wbDownload = findElement(driver, attributeMap, true);
		LoggerUtil.debug("wbDownload : " + wbDownload);
		downloadFile(driver, wbDownload, browserName);
		LoggerUtil.debug("End of downloadFileUsingLocator.");
	}

	/**
	 * Download utility to handle window dialogues for file save for browsers
	 * Internet Explorer and Firefox
	 * 
	 * @param driver
	 * @param wbDownload
	 * @param browserName
	 * @throws Exception
	 */
	public void downloadFile(Driver driver, WebElement wbDownload, String browserName) throws Exception {
		LoggerUtil.debug("Start of downloadFile.");
		LoggerUtil.debug("Downloading file.");

		Browsers browser = Browsers.valueOf(browserName);
		String absoulteImagePath = null;
		switch (browser) {
		case internetexplorer:
			LoggerUtil.debug("browserName: internetexplorer");
			if (wbDownload != null) {
				Actions builder = new Actions(driver.getWebDriver());
				builder.moveToElement(wbDownload).click().build().perform();
			}
			// Thread.sleep(5000);
			LoggerUtil.debug("Download button clicked");

			if ((absoulteImagePath = (getAbsoluteFilePath(Constants.IE_SAVE_IMAGE))) != null) {
				downloadFileClickImage(absoulteImagePath);
			}

			// Commenting below code while fixing SELENIUM-471, SELENIUM-472 [it is causing
			// NullPointerException and there is no impact if we use
			// this downloadIE.exe]
			// String exeFilePath = propertyUtil.getProperty("DownloadFileIE");
			// LoggerUtil.debug("exeFilePath : " + exeFilePath);
			// Process processIE = Runtime.getRuntime().exec(exeFilePath);
			// processIE.waitFor();
			break;

		case firefox:
			LoggerUtil.debug("browserName: firefox");
			// Commenting below code while fixing SELENIUM-471, SELENIUM-472 [it is causing
			// NullPointerException and there is no impact if we use
			// this downloadFirefox.exe]
			// Process processFF =
			// Runtime.getRuntime().exec(propertyUtil.getProperty("DownloadFileFirefox"));
			if (wbDownload != null) {
				driver.click(wbDownload);
			}
			// Thread.sleep(5000);
			LoggerUtil.debug("Download button clicked");

			if ((absoulteImagePath = (getAbsoluteFilePath(Constants.FIREFOX_SAVE_IMAGE))) != null) {
				downloadFileClickImage(absoulteImagePath);
			}
			if ((absoulteImagePath = (getAbsoluteFilePath(Constants.CHECK_OK_IMAGE))) != null) {
				downloadFileClickImage(absoulteImagePath);
			}
			// processFF.waitFor();
			break;

		case chrome:
			if (wbDownload != null) {
				driver.click(wbDownload);
			}
			break;

		case edge:
			LoggerUtil.debug("browserName: edge");
			if (wbDownload != null) {
				driver.click(wbDownload);
			}
			LoggerUtil.debug("Download button clicked");

			break;

		case edgelegacy:
			LoggerUtil.debug("browserName: edge");
			if (wbDownload != null) {
				driver.click(wbDownload);
			}
			LoggerUtil.debug("Download button clicked");

			if ((absoulteImagePath = (getAbsoluteFilePath(Constants.EDGE_SAVE_IMAGE))) != null) {
				downloadFileClickImage(absoulteImagePath);
			}
			break;

		default:
			break;
		}
		LoggerUtil.debug("End of downloadFile.");
	}

	/**
	 * Using Sikuli APIs this method will select window region and click on image
	 * specified using absoulteImagePath
	 * 
	 * @param absoulteImagePath
	 * @throws Exception
	 */
	public void downloadFileClickImage(String absoulteImagePath) throws Exception {
		Screen screen = new Screen();
		org.sikuli.script.Pattern regionPattern = new org.sikuli.script.Pattern(absoulteImagePath);

		int attempt = 0;
		while (attempt < 5) {
			LoggerUtil.debug("attempt: " + attempt);
			try {
				screen.wait(regionPattern, 10);
				Match match = screen.find(regionPattern);
				Rectangle rect = match.getRect();
				System.out.println("Location: " + rect);
				Region imageRegion = new Region(rect);
				imageRegion.click();
				attempt = 5;
			} catch (Exception e) {
				attempt++;
				if (attempt >= 5)
					throw e;
			}
		}
	}

	/**
	 * This method will give path of files present in src/main/resources folder
	 * 
	 * @param imageName
	 * @return
	 * @throws URISyntaxException
	 */
	public String getAbsoluteFilePath(String imageName) throws URISyntaxException {
		return Paths.get(getClass().getClassLoader().getResource(imageName).toURI()).toFile().getAbsolutePath();
	}

	/**
	 * This method is used for Refresh any page by clicking refresh button in the
	 * inner frame of 3DEXPERIENCE UI.
	 * 
	 * @param driver
	 * @param level
	 * @throws Exception
	 */

	@Override
	public void clickRefreshButton(Driver driver, Map<String, String> attributeMap) throws Exception {
		System.out.println("Masterlibrary");
		LoggerUtil.debug("Start of clickRefreshButton.");
		WebElement wbElementRefresh = driver.findElement(By.xpath(
				"//div[@id='divExtendedHeaderNavigation']//div[@class='field refresh button']/a[@title='Refresh']"));
		driver.click(wbElementRefresh);
		LoggerUtil.debug("End of clickRefreshButton.");
	}

	/**
	 * This method is used to click BACK button in the inner frame of 3DEXPERIENCE
	 * UI.
	 * 
	 * @param driver
	 * @param level
	 * @throws Exception
	 */

	public void clickBackButton(Driver driver) throws Exception {
		LoggerUtil.debug("Start of clickBackButton.");
		WebElement wbElementBack = driver.findElement(By.xpath(
				"//div[@id='divExtendedHeaderNavigation']//div[@class='field previous button']/a[@title='Back']"));
		driver.click(wbElementBack);
		LoggerUtil.debug("End of clickBackButton.");
	}

	/**
	 * This method is used to click fORWARD button in the inner frame of
	 * 3DEXPERIENCE UI.
	 * 
	 * @param driver
	 * @param level
	 * @throws Exception
	 */
	public void clickForwardButton(Driver driver) throws Exception {
		LoggerUtil.debug("Start of clickForwardButton.");
		WebElement wbElementForward = driver.findElement(By.xpath(
				"//div[@id='divExtendedHeaderNavigation']//div[@class='field next button']/a[@title='Forward']"));
		driver.click(wbElementForward);
		LoggerUtil.debug("End of clickForwardButton.");
	}

	/**
	 * This method is used to wait for given time duration.
	 * 
	 * @param driver
	 * @param attributeMap
	 * @throws Exception
	 */
	@Override
	public void wait(Driver driver, Map<String, String> attributeMap) throws Exception {
		LoggerUtil.debug("Start of wait.");
		if (attributeMap == null || !attributeMap.containsKey(Constants.ATTRIBUTE_TIME)) {
			throw new Exception("Attribute time not defined for Wait tag.");
		}

		String sTime = attributeMap.get(Constants.ATTRIBUTE_TIME);
		String strlocatorType = attributeMap.get(Constants.LOCATOR_TYPE);
		String strlocatorExpression = attributeMap.get(Constants.LOCATOR_EXPRESSION);
		String actionAfterTime = attributeMap.get(Constants.ATTRIBUTE_AFTER_TIME);

		if (sTime == null || ("").equalsIgnoreCase(sTime)) {
			throw new Exception("Attribute time not specified.");
		}

		if (actionAfterTime == null || ("").equalsIgnoreCase(actionAfterTime)) {
			actionAfterTime = "fail";
		}

		if (attributeMap.containsKey("for")) {
			String strFor = attributeMap.get("for");

			// If value of "for" attribute is "page", the script will check if document is
			// ready in a loop. The loop will keep processing till the
			// condition becomes true. Also it will check if the time taken for this is less
			// than or equal to given time. If not, the loop will break
			// and the script will move onto the next tag. Else if value is "time" the
			// script will wait for the given time.
			if (("page").equalsIgnoreCase(strFor)) {

				long start = System.currentTimeMillis();
				JavascriptExecutor executer = (JavascriptExecutor) driver.getWebDriver();

				boolean isReady = false;

				while (!isReady) {
					Boolean result = (Boolean) executer.executeScript("return document.readyState==='complete';");
					if (result != null) {
						isReady = result.booleanValue();
					}

					long totalTime = System.currentTimeMillis() - start;

					if (totalTime >= Long.parseLong(sTime)) {
						break;
					}
				}
			} else if (("element").equalsIgnoreCase(strFor)) {
				FluentWait<WebDriver> w = new FluentWait<WebDriver>(driver.getWebDriver())
						.withTimeout(Duration.ofMillis(Integer.parseInt(sTime))).pollingEvery(Duration.ofMillis(250))
						.ignoring(NoSuchElementException.class);
				strlocatorType = strlocatorType.toLowerCase();
				LocatorType locator = LocatorType.valueOf(strlocatorType);
				switch (locator) {
				case xpath:
					try {
						w.until(new Function<WebDriver, WebElement>() {

							@Override
							public WebElement apply(WebDriver driver) {
								return driver.findElement(By.xpath(strlocatorExpression));
							}
						});
					} catch (Exception e) {
						if ("pass".equalsIgnoreCase(actionAfterTime)) {
							LoggerUtil.debug("Element not found.");
						} else {
							LoggerUtil.debug("Element not found.");
							throw new Exception(e);
						}
					}
					break;

				case id:
					try {
						w.until(new Function<WebDriver, WebElement>() {

							@Override
							public WebElement apply(WebDriver driver) {
								return driver.findElement(By.id(strlocatorExpression));
							}
						});
					} catch (Exception e) {
						if ("pass".equalsIgnoreCase(actionAfterTime)) {
							LoggerUtil.debug("Element not found.");
						} else {
							LoggerUtil.debug("Element not found.");
							throw new Exception(e);
						}
					}
					break;

				case cssselector:
					try {
						w.until(new Function<WebDriver, WebElement>() {

							@Override
							public WebElement apply(WebDriver driver) {
								return driver.findElement(By.cssSelector(strlocatorExpression));
							}
						});
					} catch (Exception e) {
						if ("pass".equalsIgnoreCase(actionAfterTime)) {
							LoggerUtil.debug("Element not found.");
						} else {
							LoggerUtil.debug("Element not found.");
							throw new Exception(e);
						}
					}
					break;

				case name:
					try {
						w.until(new Function<WebDriver, WebElement>() {

							@Override
							public WebElement apply(WebDriver driver) {
								return driver.findElement(By.name(strlocatorExpression));
							}
						});
					} catch (Exception e) {
						if ("pass".equalsIgnoreCase(actionAfterTime)) {
							LoggerUtil.debug("Element not found.");
						} else {
							LoggerUtil.debug("Element not found.");
							throw new Exception(e);
						}
					}
					break;

				default:
					throw new Exception("Locator type = " + strlocatorType + " is not valid.");
				}

			} else {
				Thread.sleep(Long.parseLong(sTime));
			}
		} else {
			Thread.sleep(Long.parseLong(sTime));
		}

		LoggerUtil.debug("End of wait.");
	}

	/**
	 * This method is used for promote/Demote action
	 * 
	 * @param driver
	 * @param attributeMap
	 * @throws Exception
	 */
	public void Lifecycle(Driver driver, Map<String, String> attributeMap) throws Exception {
		LoggerUtil.debug("Start of Lifecycle.");
		WebElement wbElementLifecycle = null;
		String strAction = attributeMap.get(Constants.ATTRIBUTE_ACTION);
		String strValidateTargetState = attributeMap.get(Constants.ATTRIBUTE_VALIDATE_TARGET_STATE);

		if (strAction == null || "".equals(strAction))
			throw new Exception("Attribute action not specified for tag Lifecycle.");

		if (strValidateTargetState == null || "".equals(strValidateTargetState)) {
			strValidateTargetState = Constants.CHECK_TRUE;
		}

		if (strValidateTargetState.equalsIgnoreCase(CHECK_TRUE)
				|| strValidateTargetState.equalsIgnoreCase(Constants.CHECK_FALSE)) {
			String srcState = "";
			String trgtState = "";

			if ("promote".equalsIgnoreCase(strAction)) {

				driver.switchToFrame("pagecontent");
				srcState = driver.getText(By.xpath("//table[@class='lifecycle']//td[@class='stateNameHighlight']"));
				LoggerUtil.debug("srcState before promote : " + srcState);
				driver.getWebDriver().switchTo().parentFrame();
				wbElementLifecycle = driver
						.findElement(By.xpath("//div[@id='divToolbar']//td[@id='AEFLifecyclePromote']/img"));
				driver.click(wbElementLifecycle);
				highLightElement(driver, wbElementLifecycle, null);
				driver.waitUntil2(ExpectedConditions.invisibilityOfElementLocated(
						By.xpath("//div[@id='imgProgressDiv' and @style='visibility: visible;']")));
				driver.switchToFrame("pagecontent");
				trgtState = driver.getText(By.xpath("//table[@class='lifecycle']//td[@class='stateNameHighlight']"));
				LoggerUtil.debug("targetState after promote : " + trgtState);
				driver.getWebDriver().switchTo().parentFrame();

			} else if ("demote".equalsIgnoreCase(strAction)) {

				driver.switchToFrame("pagecontent");
				srcState = driver.getText(By.xpath("//table[@class='lifecycle']//td[@class='stateNameHighlight']"));
				LoggerUtil.debug("srcState before demote : " + srcState);
				driver.getWebDriver().switchTo().parentFrame();
				wbElementLifecycle = driver
						.findElement(By.xpath("//div[@id='divToolbar']//td[@id='AEFLifecycleDemote']"));
				driver.click(wbElementLifecycle);
				highLightElement(driver, wbElementLifecycle, null);
				driver.waitUntil2(ExpectedConditions.invisibilityOfElementLocated(
						By.xpath("//div[@id='imgProgressDiv' and @style='visibility: visible;']")));
				driver.switchToFrame("pagecontent");
				trgtState = driver.getText(By.xpath("//table[@class='lifecycle']//td[@class='stateNameHighlight']"));
				LoggerUtil.debug("targetState after demote : " + trgtState);
				driver.getWebDriver().switchTo().parentFrame();
			}

			if (strValidateTargetState.equalsIgnoreCase(Constants.CHECK_TRUE)) {
				if (srcState.equals(trgtState)) {
					throw new AssertionException("Since source state and target state are same.");
				}
			}
		} else {
			throw new Exception("true and false values does not match with give value: " + strValidateTargetState);
		}
		LoggerUtil.debug("End of Lifecycle.");
	}

	@Override
	public WebElement findElement(Driver driver, Map<String, String> attributeMap, Boolean bWrite) throws Exception {
		LoggerUtil.debug("Start of findElement.");

		WebElement wbReturnElement = findElement(driver, attributeMap, null, bWrite);
		LoggerUtil.debug("End of findElement.");
		return wbReturnElement;
	}

	@Override
	public WebElement findElement(Driver driver, Map<String, String> attributeMap, WebElement wbReferenceElement,
			Boolean bWrite) throws Exception {
		LoggerUtil.debug("Start of findElement using reference web element.");
		String strElementID = attributeMap.get(Constants.ATTRIBUTE_ID);
		if (strElementID == null || "".equals(strElementID)) {
			LoggerUtil
					.debug("Warning: ID attribute is empty. Random Id will be used to store reference of this element");
			strElementID = UUID.randomUUID().toString();
		}

		LoggerUtil.debug("strElementID: " + strElementID);

		String strlocatorType = attributeMap.get(Constants.LOCATOR_TYPE);
		if (strlocatorType == null || "".equals(strlocatorType))
			throw new Exception("locatorType attribute is not define for findElement tag");
		LoggerUtil.debug("strlocatorType: " + strlocatorType);

		String strlocatorExpression = attributeMap.get(Constants.LOCATOR_EXPRESSION);
		if (strlocatorExpression == null || "".equals(strlocatorExpression))
			throw new Exception("locatorExpression is not define for findElement tag");
		LoggerUtil.debug("strlocatorExpression: " + strlocatorExpression);

		WebElement wbReturnElement = null;
		if (wbReferenceElement == null)
			wbReturnElement = driver.findElement(strlocatorType, strlocatorExpression);
		else
			wbReturnElement = driver.findElement(wbReferenceElement, strlocatorType, strlocatorExpression);

		webElementMap.put(strElementID, wbReturnElement);

		String strinput = attributeMap.get(Constants.ATTRIBUTE_INPUT);
		if ((null != strinput && !strinput.isEmpty()) && bWrite == true) {

			String mode = attributeMap.get(Constants.ATTRIBUTE_MODE);
			if (mode == null || "".equals(mode))
				mode = Constants.MODE;

			LoggerUtil.debug("mode: " + mode);
			WebElement wbElement = driver.findElement(strlocatorType, strlocatorExpression);
			highLightElement(driver, attributeMap, wbElement);
			driver.click(wbElement);
			driver.writeText(wbElement, strinput, mode);
		}
		LoggerUtil.debug("End of findElement using reference web element.");
		return wbReturnElement;
	}

	@Override
	public void action(Driver driver, Map<String, String> attributeMap) throws Exception {
		LoggerUtil.debug("Start of action library method.");

		String strElementID = attributeMap.get(Constants.ATTRIBUTE_REFID);
		if (strElementID == null || "".equals(strElementID))
			throw new Exception("Attribute refid is not define for action tag.");

		LoggerUtil.debug("refid:" + strElementID);

		if (!webElementMap.containsKey(strElementID))
			throw new Exception("FindElement tag should be declared before action tag with the id = " + strElementID);

		String strActionName = attributeMap.get(Constants.ATTRIBUTE_NAME);
		if (strActionName == null || "".equals(strActionName))
			throw new Exception("name attribute is not define for action tag");

		LoggerUtil.debug("name:" + strActionName);

		strActionName = strActionName.toLowerCase();

		WebElement wbElement = webElementMap.get(strElementID);

		highLightElement(driver, attributeMap, wbElement);

		Actions actions = new Actions(driver.getWebDriver());
		actions.moveToElement(wbElement);
		MouseActions mouseAction = MouseActions.valueOf(strActionName.toLowerCase());

		Region region = new Region(0, 0);
		IRobot robot = region.getScreen().getRobot();

		switch (mouseAction) {
		case click:
			actions.click(wbElement);
			break;

		case doubleclick:
			actions.doubleClick(wbElement);
			break;

		case rightclick:
			actions.contextClick(wbElement);
			break;

		case input:
			String strInputValue = attributeMap.get(Constants.INPUT_VALUE);
			if (strInputValue == null || "".equals(strInputValue))
				throw new Exception("value attribute is requied for action input of action tag.");

			LoggerUtil.debug("value: " + strInputValue);
			try {
				wbElement.sendKeys(Keys.HOME, Keys.chord(Keys.SHIFT, Keys.END), Keys.valueOf(strInputValue));
			} catch (Exception e) {
				wbElement.sendKeys(strInputValue);
			}
			break;

		case keydown:
			String strKeyValue = attributeMap.get(Constants.INPUT_VALUE);
			if (strKeyValue == null || "".equals(strKeyValue))
				throw new Exception("Attribute value is requied for action keydown of action tag.");

			LoggerUtil.debug("value: " + strKeyValue);

			strKeyValue = strKeyValue.toUpperCase();
			Keys key = Keys.valueOf(strKeyValue);
			LoggerUtil.debug("Converted to selenium key: " + key + ".");
			actions.keyDown(key).build().perform();
			break;

		case keyup:
			strKeyValue = attributeMap.get(Constants.INPUT_VALUE);
			if (strKeyValue == null || "".equals(strKeyValue))
				throw new Exception("Attribute value is requied for action keydown of action tag.");

			LoggerUtil.debug("value:" + strKeyValue);

			strKeyValue = strKeyValue.toUpperCase();
			key = Keys.valueOf(strKeyValue);

			LoggerUtil.debug("Converted to selenium key: " + key + ".");
			/*
			 * START : SELENIUM-418 : IE is not able to perform only key up operation hence
			 * first peformed keydown and then performed keyup
			 */
			actions.keyDown(key).keyUp(key).build().perform();
			// END : SELENIUM-418
			break;

		case clickandhold:
			actions.clickAndHold(wbElement);
			break;

		case release:
			actions.release(wbElement);
			break;

		case mousedown:
			robot.mouseDown(Button.LEFT);
			actions.clickAndHold(wbElement).perform();
			// driver.getWebDriver().switchTo().defaultContent();
			break;

		case mouseup:
			actions.moveToElement(wbElement).perform();
			actions.release().perform();
			robot.mouseUp(Button.LEFT);

			// driver.getWebDriver().switchTo().defaultContent();
			break;

		case hover:
			actions.moveToElement(wbElement);
			wbElementHover(wbElement, robot, mouseAction);
			PropertyUtil propertyutil = PropertyUtil.newInstance();
			String strWait = propertyutil.getProperty(Constants.PROPERTY_KEY_EXECUTION_STEP_TIMEOUT_POLLINGINTERVAL);
			long ltime = Long.parseLong(strWait) / 1000;
			Thread.sleep(ltime);
			break;

		default:
			throw new Exception(strActionName + " is not a valid action.");
		}

		LoggerUtil.debug("Before performing action.");
		actions.build().perform();
		LoggerUtil.debug("Completed given action.");

	}

	public void wbElementHover(WebElement wbElement, IRobot robot, MouseActions mouseAction) {
		try {
			LoggerUtil.debug("Start of WebElement hover");
			Point point = wbElement.getLocation();
			int wbElement_X = point.getX();
			int wbElement_Y = point.getY();
			LoggerUtil.debug(mouseAction + " WebElement X coordinates: " + wbElement_X);
			LoggerUtil.debug(mouseAction + " WebElement Y coordinates: " + wbElement_Y);

			Dimension hoverDim = wbElement.getSize();
			int width = hoverDim.width;
			int height = hoverDim.height;
			LoggerUtil.debug("Mouse Action " + mouseAction + " Width : " + width);
			LoggerUtil.debug("Mouse Action " + mouseAction + " height : " + height);

			Region hover = new Region(wbElement_X, wbElement_Y + 120, width, height);
			robot.smoothMove(hover.getCenter());
			LoggerUtil.debug("End of of WebElement hover");
		} catch (Exception e) {
			// not handle
		}
	}

	/**
	 * This method is used for Global search functionality
	 * 
	 * @param driver
	 * @param attributeMap
	 * @throws Exception
	 */
	@Override
	public void globalSearch(Driver driver, Map<String, String> attributeMap, String strSearchInputText)
			throws Exception {
		LoggerUtil.debug("Start of globalSearch.");

		switchToDefaultContent(driver);

		String strType = null;
		WebElement wbElementGlobalSearch = null;
		if (attributeMap == null || !attributeMap.containsKey(Constants.ATTRIBUTE_ID)) {
			throw new Exception("attribute id not specified for tag GlobalSearch ");
		}

		String strId = attributeMap.get(Constants.ATTRIBUTE_ID);

		if (strId == null || "".equals(strId))
			throw new Exception(" attribute id not specified for tag GlobalSearch ");
		LoggerUtil.debug("strId: " + strId);

		if (!attributeMap.containsKey(Constants.ATTRIBUTE_TYPE)) {
			strType = Constants.CHECK_ALL;
		} else {
			strType = attributeMap.get(Constants.ATTRIBUTE_TYPE);
			if (strType == null || "".equals(strType)) {
				strType = Constants.CHECK_ALL;
			}
			LoggerUtil.debug("strType: " + strType);
		}
		if (!Constants.CHECK_ALL.equalsIgnoreCase(strType)) {
			wbElementGlobalSearch = driver.findElement(
					By.xpath("//div[@id='GTBsearchDiv']//div[@id='AEFGlobalFullTextSearch']//label[text()='All']"));
			driver.click(wbElementGlobalSearch);
			String[] arrInputType = strType.split("\\|");
			int length = arrInputType.length;
			WebElement wbMenuLiElement = null;
			for (int i = 0; i < length; i++) {
				if (i + 1 != length) {
					wbMenuLiElement = driver.findElement(By.xpath("//div[@id='AEFGlobalSearchHolder']//label[text()='"
							+ arrInputType[i].trim() + "']/../parent::li"));
					String strMenuClass = wbMenuLiElement.getAttribute(Constants.ATTRIBUTE_CLASS);
					if (strMenuClass != null && strMenuClass.toLowerCase().contains(Constants.MENU_COLLAPSE)) {
						wbElementGlobalSearch = driver.findElement(wbMenuLiElement,
								By.xpath(".//descendant::label[text()='" + arrInputType[i].trim() + "']/.."));
						driver.click(wbElementGlobalSearch);
					}

				} else {
					wbElementGlobalSearch = driver.findElement(wbMenuLiElement,
							By.xpath(".//descendant::label[text()='" + arrInputType[i].trim() + "']/.."));
					driver.click(wbElementGlobalSearch);
				}

			}

		}
		LoggerUtil.debug("Type selected for Global search");

		WebElement wbElementGlobalSearchText = driver
				.findElement(By.xpath("//div[@id='pageHeadDiv']//div[@id='GTBsearchDiv']//input[@id='GlobalNewTEXT']"));
		highLightElement(driver, attributeMap, wbElementGlobalSearchText);
		driver.writeText(wbElementGlobalSearchText, strSearchInputText);
		LoggerUtil.debug("Text to search entered into search input field.");

		WebElement wbElementGlobalSearchSubmit = driver
				.findElement(By.xpath("//div[@id='pageHeadDiv']//div[@id='GTBsearchDiv']//a[@class='btn search']/.."));
		driver.click(wbElementGlobalSearchSubmit);
		LoggerUtil.debug("Global Search Successfully Done.");
		LoggerUtil.debug("End of globalSearch.");
	}

	/**
	 * This method is used as generic method for all the search fields in form of
	 * emxFullSearch.
	 * 
	 * @param driver
	 * @param attributeMap
	 * @param strSearchInputText
	 * @throws Exception
	 */
	@Override
	public void filterSearchForm(Driver driver, Map<String, String> attributeMap, String strSearchInputText)
			throws Exception {

		LoggerUtil.debug("Start of filterSearchForm.");
		String strId = attributeMap.get(Constants.ATTRIBUTE_ID);
		if (strId == null || "".equals(strId))
			throw new Exception(" attribute id not specified for tag FilterSearchForm ");
		LoggerUtil.debug("strId: " + strId);

		String strFieldlabel = attributeMap.get(Constants.ATTRIBUTE_FIELDLABEL);
		if (strFieldlabel == null || "".equals(strFieldlabel))
			throw new Exception(" attribute fieldlabel not specified for tag FilterSearchForm ");
		LoggerUtil.debug("strFieldlabel: " + strFieldlabel);

		String strInputfieldtype = attributeMap.get(Constants.INPUT_FIELD_TYPE);
		if (strInputfieldtype == null || "".equals(strInputfieldtype))
			throw new Exception(" attribute inputfieldtype not specified for tag FilterSearchForm ");
		LoggerUtil.debug("strInputfieldtype: " + strInputfieldtype);

		String strSelection = attributeMap.get(Constants.ATTRIBUTE_SELECTION);
		if (strSelection == null || "".equals(strSelection))
			strSelection = Constants.SELECTION_SINGLE;
		LoggerUtil.debug("strSelection: " + strSelection);

		String strSearchFormType = attributeMap.get(Constants.SEARCH_FORM_TYPE);
		if (strSearchFormType == null || "".equals(strSearchFormType))
			strSelection = "Full";

		if ("Global".equalsIgnoreCase(strSearchFormType))
			driver.switchToFrame(Constants.WINDOW_SHADE_FRAME);

		InputType inputField = InputType.valueOf(strInputfieldtype.toLowerCase());
		switch (inputField) {
		// case Constants.INPUTTYPE_TEXT:
		case text:
			fieldTypeText(driver, strFieldlabel, strSearchInputText, attributeMap);
			break;
		// case Constants.INPUTTYPE_TEXTCHOOSER:
		case textchooser:
			fieldTypeTextChooser(driver, strFieldlabel, strSearchInputText, attributeMap);
			break;
		// case Constants.INPUTTYPE_SELECT:
		case select:
			fieldTypeSelect(driver, strFieldlabel, strSearchInputText, attributeMap, strSelection);
			break;
		// case Constants.INPUTTYPE_OBJECT:
		case object:
			fieldTypeObject(driver, strFieldlabel, strSearchInputText, attributeMap, strSelection);
			break;
		// case Constants.INPUTTYPE_PERSON:
		case person:
			fieldTypePerson(driver, strFieldlabel, strSearchInputText, attributeMap, strSelection);
			break;
		// case Constants.INPUTTYPE_DATE:
		case date:
			fieldTypeDate(driver, strFieldlabel, strSearchInputText, attributeMap);
			break;
		// case Constants.INPUTTYPE_CLICK:
		case click:
			fieldTypeClick(driver, strFieldlabel, strSearchInputText, attributeMap);
			break;
		default:
			break;
		}
		LoggerUtil.debug("End of filterSearchForm.");
	}

	/**
	 * This method is used to fill field of type text
	 * 
	 * @param driver
	 * @param strFieldlabel
	 * @param strSearchInputText
	 * @throws Exception
	 */
	@Override
	public void fieldTypeText(Driver driver, String strFieldlabel, String strSearchInputText,
			Map<String, String> attributeMap) throws Exception {
		LoggerUtil.debug("Start of fieldTypeText.");
		String strInputTexta = "//div[@id='searchPanel']//div[@id='searchBody']/ul/li[label/text() = '" + strFieldlabel
				+ "']//input[@type='text']";
		WebElement wbElementFieldTexta = driver.findElement(By.xpath(strInputTexta));
		driver.writeText(wbElementFieldTexta, strSearchInputText);
		LoggerUtil.debug("Field type text search successful.");
		LoggerUtil.debug("End of fieldTypeText.");

	}

	/**
	 * This method is used to fill field of type text Chooser
	 * 
	 * @param driver
	 * @param strFieldlabel
	 * @param strSearchInputText
	 * @throws Exception
	 */
	@Override
	public void fieldTypeTextChooser(Driver driver, String strFieldlabel, String strSearchInputText,
			Map<String, String> attributeMap) throws Exception {
		LoggerUtil.debug("Start of fieldTypeTextChooser.");
		String strInputText = "//div[@id='searchPanel']//div[@id='searchBody']/ul/li[label/text() = '"
				+ strFieldlabel.trim() + "']//input[@type='button']";
		WebElement wbElementFieldText = driver.findElement(By.xpath(strInputText));
		driver.click(wbElementFieldText);
		String strInputTexta = "//div[@id='divid']/textarea";
		WebElement wbElementFieldTexta = driver.findElement(By.xpath(strInputTexta));
		driver.writeText(wbElementFieldTexta, strSearchInputText);
		LoggerUtil.debug("Field type text chooser search successful.");
		LoggerUtil.debug("End of fieldTypeTextChooser.");
	}

	/**
	 * This method is used to fill field of type select.
	 * 
	 * @param driver
	 * @param strFieldlabel
	 * @param strSearchInputText
	 * @param attributeMap
	 * @param strSelection
	 * @throws Exception
	 */
	@Override
	public void fieldTypeSelect(Driver driver, String strFieldlabel, String strSearchInputText,
			Map<String, String> attributeMap, String strSelection) throws Exception {
		LoggerUtil.debug("Start of fieldTypeSelect.");
		String strInputChooser = "//div[@id='searchPanel']//div[@id='searchBody']/ul/li[label/text() = '"
				+ strFieldlabel.trim() + "']//input[@type='button']";
		WebElement wbElementFieldChooser = driver.findElement(By.xpath(strInputChooser));
		driver.click(wbElementFieldChooser);
		WebElement wbElementFieldInputSelect = driver
				.findElement(By.xpath("//form[@id='full_search']//div[@class='popup form multi-list']//select"));
		driver.selectByText(wbElementFieldInputSelect, strSearchInputText);
		WebElement wbElementDone = driver.findElement(By.xpath(
				"//form[@id='full_search']//div[@class='popup form multi-list']//div[@class='head']//button[@class='done']"));
		driver.click(wbElementDone);
		LoggerUtil.debug("Field type select search successful.");
		LoggerUtil.debug("End of fieldTypeSelect.");
	}

	/**
	 * This method is used to fill field of type object.
	 * 
	 * @param driver
	 * @param strFieldlabel
	 * @param strSearchInputText
	 * @param attributeMap
	 * @param strSelection
	 * @throws Exception
	 */
	@Override
	public void fieldTypeObject(Driver driver, String strFieldlabel, String strSearchInputText,
			Map<String, String> attributeMap, String strSelection) throws Exception {
		LoggerUtil.debug("Start of fieldTypeObject.");
		String strInputChooser = "//div[@id='searchPanel']//div[@id='searchBody']/ul/li[label/text() = '"
				+ strFieldlabel.trim() + "']//input[@type='button']";
		WebElement wbElementFieldChooser = driver.findElement(By.xpath(strInputChooser));
		driver.click(wbElementFieldChooser);
		switchToWindow(driver, attributeMap);
		WebElement wbElementNameInput = null;
		if (Constants.SELECTION_SINGLE.equalsIgnoreCase(strSelection)) {
			try {
				wbElementNameInput = driver.findElement(By.xpath(
						"//form[@id='full_search']//div[@id='searchPanel']//div[@id='searchBody']//input[@id='Name']"));
			} catch (Exception e) {
				wbElementNameInput = driver.findElement(By.xpath(
						"//form[@id='full_search']//div[@id='searchPanel']//div[@id='searchBody']//input[@id='NAME']"));
			}

			driver.writeText(wbElementNameInput, strSearchInputText);

			WebElement wbElementSearch = driver.findElement(By.xpath(
					"//form[@id='full_search']//div[@id='searchPanel']//div[@id='searchHead']//a[@id='mx_btn-search']"));
			driver.click(wbElementSearch);

			driver.switchToFrame(Constants.STRUCTURE_BROWSER_FRAME);
			LoggerUtil.debug("Field type select search form opened successfully.");

			WebElement wbElementSelect = driver.findElement(By.xpath(
					"//form[@name='emxTableForm']//div[@id='mx_divBody']//table[@id='treeBodyTable']//tr[@id='0,0']//input[@name='emxTableRowIdActual']"));
			driver.click(wbElementSelect);

		} else {
			WebElement wbElementSearch = driver.findElement(By.xpath(
					"//form[@id='full_search']//div[@id='searchPanel']//div[@id='searchHead']//a[@id='mx_btn-search']"));
			driver.click(wbElementSearch);
			driver.switchToFrame(Constants.STRUCTURE_BROWSER_FRAME);
			LoggerUtil.debug("Field type select search form opened successfully.");
			String[] arrInputText = strSearchInputText.split("\\|");
			int length = arrInputText.length;
			for (int i = 0; i < length; i++) {

				String strXPathRadio = "//form[@name='emxTableForm']//div[@id='mx_divBody']//table[@id='treeBodyTable']//tr[td/div/table/tbody/tr/td[@title='"
						+ arrInputText[i] + "']]//input[@name='emxTableRowIdActual']";
				WebElement wbElementSelect = driver.findElement(By.xpath(strXPathRadio));
				driver.click(wbElementSelect);
			}

		}
		LoggerUtil.debug("Field type select search objects selected successfully.");

		WebElement wbElementSubmit = driver.findElement(By.xpath("//div[@id='divPageFoot']//a[@class='button']"));
		driver.click(wbElementSubmit);
		LoggerUtil.debug("Field type object search successful.");
		LoggerUtil.debug("End of fieldTypeObject.");
	}

	/**
	 * This method is used to fill field of type person.
	 * 
	 * @param driver
	 * @param strFieldlabel
	 * @param strSearchInputText
	 * @param attributeMap
	 * @param strSelection
	 * @throws Exception
	 */
	@Override
	public void fieldTypePerson(Driver driver, String strFieldlabel, String strSearchInputText,
			Map<String, String> attributeMap, String strSelection) throws Exception {
		LoggerUtil.debug("Start of fieldTypePerson.");
		fieldTypeObject(driver, strFieldlabel, strSearchInputText, attributeMap, strSelection);
		LoggerUtil.debug("End of fieldTypePerson.");
	}

	/**
	 * This method is used to fill field of type date.
	 * 
	 * @param driver
	 * @param strFieldlabel
	 * @param strSearchInputText
	 * @param attributeMap
	 * @throws Exception
	 */
	@Override
	public void fieldTypeDate(Driver driver, String strFieldlabel, String strSearchInputText,
			Map<String, String> attributeMap) throws Exception {
		LoggerUtil.debug("Start of fieldTypeDate.");
		String strCriteria = attributeMap.get(Constants.ATTRIBUTE_CRITERIA);
		if (strCriteria == null || "".equals(strCriteria)) {
			strCriteria = Constants.DATE_SELECTION_CRITERIA_ON;
		}
		LoggerUtil.debug("strCriteria: " + strCriteria);
		if (strSearchInputText.contains("|")
				&& !Constants.DATE_SELECTION_CRITERIA_BETWEEN.equalsIgnoreCase(strCriteria)) {
			throw new Exception("Given input date is incorrect.");
		}

		WebElement wbElementFieldChooser = driver
				.findElement(By.xpath("//div[@id='searchPanel']//div[@id='searchBody']/ul/li[label/text() = '"
						+ strFieldlabel.trim() + "']//input[@type='button']"));

		driver.click(wbElementFieldChooser);
		getDateFromDatePicker(driver, strFieldlabel, strSearchInputText, strCriteria);
		LoggerUtil.debug("Field type Date search successful.");
		LoggerUtil.debug("End of fieldTypeDate.");
	}

	/**
	 * This method is used to get date from date picker
	 * 
	 * @param driver
	 * @param strFieldlabel
	 * @param strSearchInputText
	 * @param strCriteria
	 * @throws Exception
	 */
	@Override
	public void getDateFromDatePicker(Driver driver, String strFieldlabel, String strSearchInputText,
			String strCriteria) throws Exception {
		LoggerUtil.debug("Start of getDateFromDatePicker.");
		WebElement wbElementFieldCriteriaSelect = driver
				.findElement(By.xpath("//form[@id='full_search']//div[@class='popup form multi-list']//select"));
		driver.selectByText(wbElementFieldCriteriaSelect, strCriteria);

		int iDateInputSeperatorIndex = strSearchInputText.indexOf("|");
		LoggerUtil.debug("iDateInputSeperatorIndex: " + iDateInputSeperatorIndex);

		String strDateInput1 = null;
		if (iDateInputSeperatorIndex != -1) {
			strDateInput1 = strSearchInputText.substring(0, iDateInputSeperatorIndex).trim();
		} else {
			strDateInput1 = strSearchInputText;
		}

		String strXPathDatePicker1 = "//div[@class='popup form multi-list']//li[2]//a";

		selectDateFromDatePicker(driver, strFieldlabel, strDateInput1, strXPathDatePicker1);
		LoggerUtil.debug("Date picker1 date selection successful.");

		if (Constants.DATE_SELECTION_CRITERIA_BETWEEN.equalsIgnoreCase(strCriteria)) {
			String strDateInput2 = strSearchInputText.substring(iDateInputSeperatorIndex + 1).trim();
			String strXPathDatePicker2 = "//div[@class='popup form multi-list']//li[3]//a";
			selectDateFromDatePicker(driver, strFieldlabel, strDateInput2, strXPathDatePicker2);
			LoggerUtil.debug("Date picker2 date selection successful");
		}

		WebElement wbElementDoneButton = driver
				.findElement(By.xpath("//div[@class='popup form multi-list']//button[@class='done']"));
		driver.click(wbElementDoneButton);
		LoggerUtil.debug("End of getDateFromDatePicker.");
	}

	/**
	 * This method is used to get date from date picker
	 * 
	 * @param driver
	 * @param strFieldlabel
	 * @param strSearchInputText
	 * @param strXPathDatePicker
	 * @throws Exception
	 */
	@Override
	public void selectDateFromDatePicker(Driver driver, String strFieldlabel, String strSearchInputText,
			String strXPathDatePicker) throws Exception {
		WebElement wbElementFieldDatePicker = driver.findElement(By.xpath(strXPathDatePicker));
		selectDateFromDatePicker(driver, strSearchInputText, wbElementFieldDatePicker);

	}

	/**
	 * This method is used to get date from date picker
	 * 
	 * @param driver
	 * @param strSearchInputText
	 * @param wbElement
	 * @throws Exception
	 */

	@Override
	public void selectDateFromDatePicker(Driver driver, String strSearchInputText, WebElement wbElement)
			throws Exception {
		LoggerUtil.debug("Start of selectDateFromDatePicker.");

		int iMonthindex = strSearchInputText.indexOf(" ");
		String strMonth = strSearchInputText.substring(0, iMonthindex);
		LoggerUtil.debug("strMonth: " + strMonth);
		int iYearIndex = strSearchInputText.indexOf(",");
		String strYear = strSearchInputText.substring(iYearIndex + 1).trim();
		LoggerUtil.debug("strYear: " + strYear);
		driver.click(wbElement);
		WebElement wbElementFieldSelectMonth = driver
				.findElement(By.xpath("//div[@class='calendar-container']//td[@id='tdMonth']"));
		driver.click(wbElementFieldSelectMonth);

		WebElement wbElementFieldSelectedMonth = driver.findElement(
				By.xpath("//div[@class='menu-panel page']//li//label[start-with(@text(),'" + strMonth + "')]"));
		driver.click(wbElementFieldSelectedMonth, "js", "false");
		LoggerUtil.debug("Date picker month selection successful.");

		WebElement wbElementFieldSelectYear = driver
				.findElement(By.xpath("//div[@class='calendar-container']//td[@id='tdYear']"));
		driver.click(wbElementFieldSelectYear, "js", "false");
		WebElement wbElementFieldSelectedYear = driver
				.findElement(By.xpath("//div[@class='menu-panel page']//li//label[text()='" + strYear + "']"));
		driver.click(wbElementFieldSelectedYear, "js", "false");
		LoggerUtil.debug("Date picker year selection successful.");

		WebElement wbElementFieldSelectDay = driver.findElement(
				By.xpath("//div[@class='calendar-container']//td[@title='" + strSearchInputText.trim() + "']"));
		driver.click(wbElementFieldSelectDay, "js", "false");
		LoggerUtil.debug("Date picker day selection successful.");

		LoggerUtil.debug("End of selectDateFromDatePicker.");
	}

	/**
	 * This method is used to fill field of type person
	 * 
	 * @param driver
	 * @param strFieldlabel
	 * @param strSearchInputText
	 * @throws Exception
	 */
	@Override
	public void filterSearchPerson(Driver driver, Map<String, String> attributeMap, String strSearchInputText)
			throws Exception {
		LoggerUtil.debug("Start of filterSearchPerson.");
		String strId = attributeMap.get(Constants.ATTRIBUTE_ID);
		if (strId == null || "".equals(strId))
			throw new Exception(" attribute id not specified for tag FilterSearchForm ");
		LoggerUtil.debug("strId: " + strId);

		String strFieldlabel = attributeMap.get(Constants.ATTRIBUTE_FIELDLABEL);
		if (strFieldlabel == null || "".equals(strFieldlabel))
			throw new Exception(" attribute fieldlabel not specified for tag FilterSearchForm ");
		LoggerUtil.debug("strFieldlabel: " + strFieldlabel);

		String strInputfieldtype = attributeMap.get(Constants.INPUT_FIELD_TYPE);
		if (strInputfieldtype == null || "".equals(strInputfieldtype))
			throw new Exception(" attribute inputfieldtype not specified for tag FilterSearchForm ");
		LoggerUtil.debug("strInputfieldtype: " + strInputfieldtype);

		String strSelection = attributeMap.get(Constants.ATTRIBUTE_SELECTION);
		if (strSelection == null || "".equals(strSelection))
			strSelection = Constants.SELECTION_SINGLE;
		LoggerUtil.debug("strSelection: " + strSelection);

		fieldTypeObject(driver, strFieldlabel, strSearchInputText, attributeMap, strSelection);
		LoggerUtil.debug("Field type Person search successful.");
		LoggerUtil.debug("End of filterSearchPerson.");
	}

	/**
	 * Method to provide value to select in a single/ multi-value dropdown list
	 * 
	 * @author Steepgraph Systems
	 * @param driver
	 * @param attributeMap
	 * @param strSelectValue
	 * @return void
	 * @throws Exception
	 */
	@Override
	public void selectElement(Driver driver, Map<String, String> attributeMap, String strSelectValue) throws Exception {
		LoggerUtil.debug("Start of selectElement.");

		String strlocatorType = attributeMap.get(Constants.LOCATOR_TYPE);

		if (strlocatorType == null || "".equals(strlocatorType))
			throw new Exception("Attribute locatorType is not define for inputText tag.");
		LoggerUtil.debug("strlocatorType: " + strlocatorType);

		String strlocatorExpression = attributeMap.get(Constants.LOCATOR_EXPRESSION);

		if (strlocatorExpression == null || "".equals(strlocatorExpression))
			throw new Exception("Attribute locatorExpression is not define for inputText tag.");
		LoggerUtil.debug("strlocatorExpression: " + strlocatorExpression);

		WebElement wbDropDownList = driver.findElement(strlocatorType, strlocatorExpression);

		LoggerUtil.debug("strSelectValue: " + strSelectValue);
		driver.selectByText(wbDropDownList, strSelectValue);
		Select select = new Select(wbDropDownList);
		WebElement wbSelectedOption = select.getFirstSelectedOption();
		String selectedValue = wbSelectedOption.getText();
		LoggerUtil.debug("selectedValue: " + selectedValue);
		if (!selectedValue.equalsIgnoreCase(strSelectValue)) {
			driver.selectByText(wbDropDownList, strSelectValue);
		}
		LoggerUtil.debug("End of selectElement.");
	}

	/**
	 * Method to handle an close current child window and switch to parent window
	 * 
	 * @author Steepgraph Systems
	 * @param driver
	 * @return void
	 * @throws Exception
	 */
	@Override
	public void closeCurrentWindow(Driver driver) throws Exception {
		LoggerUtil.debug("Start of closeCurrentWindow.");
		Thread.sleep(500);
		dismissAlert(driver);
		driver.getWebDriver().close();
		Thread.sleep(500);
		LoggerUtil.debug("End of closeCurrentWindow.");
	}

	/**
	 * This method will click on chooser button next to field whose label is
	 * provided in test case xml
	 * 
	 * @author Steepgraph Systems
	 * @param driver
	 * @param attributeMap: filedLabel key is required in this map
	 * @return void
	 * @throws Exception
	 */
	@Override
	public void openChooser(Driver driver, Map<String, String> attributeMap) throws Exception {

		LoggerUtil.debug("Start of openChooser");

		if (attributeMap == null)
			throw new Exception("fieldlabel attribute is not define for openChooser tag.");

		String strFieldLabel = attributeMap.get(Constants.ATTRIBUTE_FIELDLABEL);
		LoggerUtil.debug("strFieldLabel: " + strFieldLabel);
		if (strFieldLabel == null || "".equals(strFieldLabel))
			throw new Exception("fieldlabel attribute is not define for openChooser tag.");

		WebElement weChooser = driver.findElement(
				By.xpath("//input[@fieldLabel='" + strFieldLabel + "']/following-sibling::input[@value='...']"));

		driver.click(weChooser);

		switchToWindow(driver, attributeMap);

		LoggerUtil.debug("End of openChooser");
	}

	/**
	 * This method will click on the input field from the search filter frame and
	 * select the text which is passed as an input
	 * 
	 * @author Steepgraph Systems
	 * @param driver
	 * @param strFieldlabel
	 * @param attributeMap: filedLabel key is required in this map
	 * @return void
	 * @throws Exception
	 */
	@Override
	public void fieldTypeClick(Driver driver, String strFieldlabel, String strSearchInputText,
			Map<String, String> attributeMap) throws Exception {
		LoggerUtil.debug("Start of fieldTypeClick.");
		LoggerUtil.debug("input Fieldlabel : " + strFieldlabel);
		String strInputTexta = "//div[@id='searchPanel']//div[@id='searchBody']/ul/li[label/text() = '"
				+ strFieldlabel.trim() + "']//input[@type='text']";
		WebElement wbElementInputField = driver.findElement(By.xpath(strInputTexta));
		driver.click(wbElementInputField);

		String strSubInputTexta = "//div[@class='popup form multi-list']//div//ul//li[2]//select[@id='select_TYPE']";
		WebElement wbElementFieldInputSelect = driver.findElement(By.xpath(strSubInputTexta));
		driver.selectByText(wbElementFieldInputSelect, strSearchInputText);

		WebElement wbElementDone = driver.findElement(By.xpath(
				"//form[@id='full_search']//div[@class='popup form multi-list']//div[@class='head']//button[@class='done']"));
		driver.click(wbElementDone);

		LoggerUtil.debug("Field type click search successful.");
		LoggerUtil.debug("End of fieldTypeClick.");

	}

	/**
	 * This method will click on the search button of the filter criteria
	 * 
	 * @author Steepgraph Systems
	 * @param driver
	 * @param attributeMap
	 * @return void
	 * @throws Exception
	 */
	@Override
	public void applySearchFilter(Driver driver, Map<String, String> attributeMap) throws Exception {
		LoggerUtil.debug("Start of applySearch.");
		WebElement wbElementDone = driver.findElement(By.xpath(
				"//form[@id='full_search']//div[@id='pageContentDiv']//div[@id='searchPanel']//div[@class='buttons']//a[@id='mx_btn-search']"));
		driver.click(wbElementDone);

		LoggerUtil.debug("applySearch successful.");
		LoggerUtil.debug("End of applySearch.");
	}

	/**
	 * This method will click on the reset button of the filter criteria
	 * 
	 * @author Steepgraph Systems
	 * @param driver
	 * @param attributeMap
	 * @return void
	 * @throws Exception
	 */
	@Override
	public void resetSearchFilter(Driver driver, Map<String, String> attributeMap) throws Exception {
		LoggerUtil.debug("Start of resetearch.");
		WebElement wbElementDone = driver.findElement(By.xpath(
				"//form[@id='full_search']//div[@id='pageContentDiv']//div[@id='searchPanel']//div[@class='buttons']//a[@id='mx_reset']"));
		driver.click(wbElementDone);

		LoggerUtil.debug("resetearch successful.");
		LoggerUtil.debug("End of resetearch.");

	}

	/**
	 * This method will click on the inputs like : check boxes or radio buttons
	 * corresponding to the objects resulted from the filter/search createria and
	 * click on submit
	 * 
	 * @author Steepgraph Systems
	 * @param driver
	 * @param attributeMap  : selection,criteria are the required keys
	 * @param selectObjects
	 * @return void
	 * @throws Exception
	 * 
	 * @Deprecated This tag is Deprecated From 21x version and New tag is
	 *             SelectAndSubmit6WSearch tag
	 */
	@Override
	public void selectAndSubmitSearch(Driver driver, Map<String, String> attributeMap, String selectObjects)
			throws Exception {
		LoggerUtil.debug("Start of selectAndSubmitSearch.");
		driver.switchToFrame(Constants.STRUCTURE_BROWSER_FRAME);
		String strSelection = attributeMap.get(Constants.ATTRIBUTE_SELECTION);
		String strCriteria = attributeMap.get(Constants.ATTRIBUTE_CRITERIA);
		String strSubmitLabel = attributeMap.get(Constants.SUBMIT_LABEL);
		LoggerUtil.debug("i/p selection : " + strSelection);
		LoggerUtil.debug("i/p criteria : " + strCriteria);
		LoggerUtil.debug("i/p select object/s based on criteria : " + selectObjects);

		if (strSelection == null || "".equalsIgnoreCase(strSelection))
			strSelection = Constants.SELECTION_SINGLE;

		if (strCriteria == null || "".equalsIgnoreCase(strCriteria))
			throw new Exception("Please provide the selection criteria. Either text or row-number");

		if (strSubmitLabel == null || "".equalsIgnoreCase(strSubmitLabel))
			throw new Exception(
					"Please provide the text for the submission label. It should either be Submit or Done or relevant text.");

		if (selectObjects == null || "".equalsIgnoreCase(selectObjects))
			throw new Exception("Please provide the values or row-numbers for objects to be selected in csv file");

		if (Constants.INPUTTYPE_TEXT.equalsIgnoreCase(strCriteria)) {
			if (Constants.SELECTION_MULTIPLE.equalsIgnoreCase(strSelection)) {
				String[] arrSelectObjects = selectObjects.split("\\|");
				for (String str : arrSelectObjects) {
					WebElement wbElementInputBtn = driver.findElement(By.xpath(
							"//form[@name='emxTableForm']//div[@id='mx_divBody']//div[@id='mx_divTreeBody']//table//tr//td[@title='"
									+ str + "']/../td/input[starts-with(@id,'rmbrow-')]"));

					driver.click(wbElementInputBtn);
				}
			} else {
				WebElement wbElementInputBtn = driver.findElement(By.xpath(
						"//form[@name='emxTableForm']//div[@id='mx_divBody']//div[@id='mx_divTreeBody']//table//tr//td[@title='"
								+ selectObjects + "']/../td/input[starts-with(@id,'rmbrow-')]"));

				driver.click(wbElementInputBtn);
			}
		} else {
			if (Constants.SELECTION_MULTIPLE.equalsIgnoreCase(strSelection)) {
				String[] arrSelectObjects = selectObjects.split("\\|");
				for (String str : arrSelectObjects) {
					WebElement wbElementInputBtn = driver.findElement(By.xpath(
							"//form[@name='emxTableForm']//div[@id='mx_divBody']//div[@id='mx_divTreeBody']//table//tr/td/input[@id='rmbrow-0,"
									+ str + "']"));

					driver.click(wbElementInputBtn);
				}
			} else {
				WebElement wbElementInputBtn = driver.findElement(By.xpath(
						"//form[@name='emxTableForm']//div[@id='mx_divBody']//div[@id='mx_divTreeBody']//table//tr/td/input[@id='rmbrow-0,"
								+ selectObjects + "']"));
				driver.click(wbElementInputBtn);
			}
		}

		WebElement wbElementSubmitBtn = driver
				.findElement(By.xpath("//form[@name='emxTableForm']//button[text()='" + strSubmitLabel + "']"));
		driver.click(wbElementSubmitBtn);
		LoggerUtil.debug("selectAndSubmitSearch successful.");
		LoggerUtil.debug("End of selectAndSubmitSearch.");

	}

	/**
	 * This method will click on the inputs like : check boxes or radio buttons
	 * corresponding to the objects resulted from the filter/search criteria on 6W
	 * tags page and click on submit
	 * 
	 * @author Steepgraph Systems
	 * @param driver
	 * @param attributeMap  : selection, id, submitLabel are the required keys
	 * @param selectObjects
	 * @return void
	 * @throws Exception
	 */
	@Override
	public void selectAndSubmit6WTagsSearch(Driver driver, Map<String, String> attributeMap, String selectObjects)
			throws Exception {
		LoggerUtil.debug("Start of selectAndSubmit6WTagsSearch.");
		String strSelection = attributeMap.get(Constants.ATTRIBUTE_SELECTION);
		String strSubmitLabel = attributeMap.get(Constants.SUBMIT_LABEL);
		String colName = attributeMap.get(Constants.ATTRIBUTE_COLUMN);
		String colValue = attributeMap.get(Constants.ATTRIBUTE_VALUE);
		LoggerUtil.debug("i/p selection : " + strSelection);
		LoggerUtil.debug("i/p select object/s based on criteria : " + selectObjects);

		WebElement wbResult;
		String count = "-1";
		int exeCnt = 0;
		while (exeCnt < 3) {
			try {
				wbResult = driver.findElement(By.xpath("//span[@class=' set-detail-view-title']"));
				driver.waitUntil(ExpectedConditions.visibilityOf(wbResult));
				count = driver.getText(By.id("search-nb-result"));
				break;
			} catch (StaleElementReferenceException e) {
				// intentionally not handled.
			}
			exeCnt++;
		}

		LoggerUtil.debug("Search Result Count : " + count);
		int intCount = -1;
		try {
			intCount = Integer.valueOf(count);
		} catch (Exception e) {
			LoggerUtil.debug("Error while parsing search result count. Invalid string in argumment");
		}

		if (intCount == 0) {
			clickClock(driver, attributeMap);
		}

		if (strSelection == null || "".equalsIgnoreCase(strSelection))
			strSelection = Constants.SELECTION_SINGLE;

		if (strSubmitLabel == null || "".equalsIgnoreCase(strSubmitLabel))
			throw new Exception(
					"Please provide the text for the submission label. It should either be Submit or Done or relevant text.");

		if (selectObjects == null || "".equalsIgnoreCase(selectObjects))
			throw new Exception("Please provide the values or row-numbers for objects to be selected in csv file");

		if (colName == null) {
			colName = "";
		}
		if (colValue == null) {
			colValue = "";
		}

		Thread.sleep(2000);
		if (Constants.SELECTION_MULTIPLE.equalsIgnoreCase(strSelection)) {
			LoggerUtil.debug("Select Multiple results.");
			String[] arrSelectObjects = selectObjects.split("\\|");
			String[] colNameArr = colName.split("\\|");
			String[] colValueArr = colValue.split("\\|");

			if (colNameArr.length > 1) {
				if (colNameArr.length != arrSelectObjects.length && colValueArr.length != arrSelectObjects.length)
					throw new Exception("Please provide the column name and values same as number of selection");
			}

			for (int i = 0; i < arrSelectObjects.length; i++) {
				if (colName != null && colName != "" && colValue != null && colValue != "") {
					clickCheckBox(driver, arrSelectObjects[i], colNameArr[i], colValueArr[i], attributeMap);
				} else {
					clickCheckBox(driver, arrSelectObjects[i], "", "", attributeMap);
				}
			}
		} else {
			LoggerUtil.debug("Select Single result.");
			clickCheckBox(driver, selectObjects, colName, colValue, attributeMap);
		}

		WebElement wbElementSubmitBtn = driver
				.findElement(By.xpath("//div[@id='searchResultsContainer']//button[text()='" + strSubmitLabel + "']"));
		driver.click(wbElementSubmitBtn);
		LoggerUtil.debug("selectAndSubmit6WTagsSearch successful.");
		LoggerUtil.debug("End of selectAndSubmit6WTagsSearch.");

	}

	public void clickCheckBox(Driver driver, String selectObjects, String colName, String colVal,
			Map<String, String> attributeMap) throws Exception {
		LoggerUtil.debug("Start of clickCheckBox.");
		boolean finalmatch = false;
		WebElement wbElement = null;
		int checkBoxCellId = 0;
		String strWait = attributeMap.get(Constants.ATTRIBUTE_WAIT);
		String finalStrWait = getTimeOut(strWait);
		LoggerUtil.debug("timeout : " + finalStrWait);

		HashMap<String, String> attributeMapForDashboardList1 = new HashMap<String, String>() {

			/**
			 * 
			 */
			private static final long serialVersionUID = -4048538848589698181L;

			{
				put(Constants.LOCATOR_TYPE, Constants.INPUTTYPE_XPATH);
				put(Constants.LOCATOR_EXPRESSION,
						"//div[contains(@class, 'wux-layouts-gridengine-poolcontainer-rel')]//div[@is-visible='true']//div[text()='"
								+ selectObjects + "']/../..");

				put(Constants.ATTRIBUTE_CRITERIA, "found");
				put(Constants.ATTRIBUTE_WAIT, finalStrWait);
			}
		};

		HashMap<String, String> attributeMapForDashboardList = new HashMap<String, String>() {

			/**
			 * 
			 */
			private static final long serialVersionUID = -4048538848589698181L;

			{
				put(Constants.LOCATOR_TYPE, Constants.INPUTTYPE_XPATH);
				put(Constants.LOCATOR_EXPRESSION,
						"//div[contains(@class, 'wux-layouts-left-poolcontainer')]//div[@is-visible='true']//span[text()='"
								+ selectObjects + "']/../..");

				put(Constants.ATTRIBUTE_CRITERIA, "found");
				put(Constants.ATTRIBUTE_WAIT, finalStrWait);
			}
		};

		if (!ifCondition(driver, attributeMapForDashboardList1, null)
				&& !ifCondition(driver, attributeMapForDashboardList, null)) {
			clickClock(driver, attributeMap);
		}

		if (colName != null && !"".equalsIgnoreCase(colName)) {
			List<WebElement> wbEl = null; // multiple

			if (ifCondition(driver, attributeMapForDashboardList, null)) {

				LoggerUtil.debug("selectObjects: " + selectObjects + "is present in freeze pane");
				wbEl = driver.findElements(By.xpath(
						"//div[contains(@class, 'wux-layouts-left-poolcontainer')]//div[@is-visible='true']//span[text()='"
								+ selectObjects + "']/../.."));

				HashMap<String, String> attributeMapForfreezepane = new HashMap<String, String>() {

					/**
					 * 
					 */
					private static final long serialVersionUID = -4048538848589698181L;

					{
						put(Constants.LOCATOR_TYPE, Constants.INPUTTYPE_XPATH);
						put(Constants.LOCATOR_EXPRESSION,
								"//div[@class='wux-layouts-datagridview-tweaker-container']/div[text()='" + colName
										+ "']");

						put(Constants.ATTRIBUTE_CRITERIA, "found");
						put(Constants.ATTRIBUTE_WAIT, finalStrWait);
					}
				};

				if (!ifCondition(driver, attributeMapForfreezepane, null)) {
					throw new NoSuchElementException("colName :" + colName + " is not found");
				}

				WebElement wbCol = driver
						.findElement(By.xpath("//div[@class='wux-layouts-datagridview-tweaker-container']/div[text()='"
								+ colName + "']/../.."));

				LoggerUtil.debug("Column Name Element : " + wbCol);
				String colCellCnt = wbCol.getAttribute("cell-id");
				int intPos = 0;
				try {
					intPos = Integer.parseInt(colCellCnt) - 4;
					LoggerUtil.debug("Relative position from 1st column : " + intPos);
				} catch (NumberFormatException e) {
					LoggerUtil.debug("Invalid string in argument.");
				}

				for (WebElement wb : wbEl) {
					LoggerUtil.debug("Current Webelement : " + wb);
					String cellCnt = wb.getAttribute("cell-id");
					int intCellId = 0;
					try {
						intCellId = Integer.parseInt(cellCnt) - 1;
					} catch (Exception e) {
						LoggerUtil.debug("Invalid string in argument.");
					}
					checkBoxCellId = intCellId;

					WebElement wbColCell = driver.findElement(
							By.xpath("//div[@cell-id=" + ((intCellId + 1) + intPos) + "][@is-visible='true']/div"));
					LoggerUtil.debug("colVal : " + colVal);
					LoggerUtil.debug("column value in search result : " + wbColCell.getText());
					if (colVal.equalsIgnoreCase(wbColCell.getText())) {
						finalmatch = true;
						LoggerUtil.debug("Selected wbelement : " + wb);
						break;
					}
				}

				if (finalmatch == false) {
					throw new NoSuchElementException("for the object =" + selectObjects + " ,colname =" + colName
							+ ", col.value =" + colVal + " , data not found for col. value :" + colVal);
				}

				LoggerUtil.debug("checkBoxCellId : " + checkBoxCellId);
				WebElement wbCheckBoxElement;
				if (checkBoxCellId != 0) {
					wbCheckBoxElement = driver.findElement(By.xpath("//div[@cell-id='" + checkBoxCellId
							+ "' and @is-visible='true']/div[@class='wux-datagridview-selection-label']"));
					driver.click(wbCheckBoxElement);
				}

			} else {

				if (ifCondition(driver, attributeMapForDashboardList1, null)) {
					LoggerUtil.debug("selectObjects: " + selectObjects + "is present in non freeze pane");
					wbEl = driver.findElements(By.xpath(
							"//div[contains(@class, 'wux-layouts-gridengine-poolcontainer-rel')]//div[@is-visible='true']//div[text()='"
									+ selectObjects + "']/../.."));

					HashMap<String, String> attributeMapForDashboardList2 = new HashMap<String, String>() {

						/**
						 * 
						 */
						private static final long serialVersionUID = -4048538848589698181L;

						{
							put(Constants.LOCATOR_TYPE, Constants.INPUTTYPE_XPATH);
							put(Constants.LOCATOR_EXPRESSION,
									"//div[@class='wux-layouts-datagridview-tweaker-container']/div[text()='" + colName
											+ "']");

							put(Constants.ATTRIBUTE_CRITERIA, "found");
							put(Constants.ATTRIBUTE_WAIT, finalStrWait);
						}
					};

					if (!ifCondition(driver, attributeMapForDashboardList2, null)) {
						throw new NoSuchElementException("colName :" + colName + " is not found");
					}

					WebElement wbCol = driver.findElement(
							By.xpath("//div[@class='wux-layouts-datagridview-tweaker-container']/div[text()='" + colName
									+ "']/../.."));
					LoggerUtil.debug("Column Name Element : " + wbCol.getText());

					String colCellCnt = wbCol.getAttribute("cell-id");
					int intPos = 0;
					try {
						intPos = Integer.parseInt(colCellCnt) - 4;
						LoggerUtil.debug("Relative position from 1st column : " + intPos);
					} catch (NumberFormatException e1) {
						LoggerUtil.debug("Invalid string in argument.");
					}

					List<WebElement> wbrow = null;
					wbrow = driver.findElements(By.xpath(
							"//div[@class='wux-layouts-gridengine-poolcontainer-rel']//div[@class='wux-controls-abstract wux-layouts-collectionview-cell wux-layouts-datagridview-row']"));
					int count = wbrow.size();

					WebElement firstwbcell = null;
					for (int i = 1; i < count + 1; i++) {

						firstwbcell = driver.findElement(By.xpath(
								"//div[@class='wux-layouts-gridengine-poolcontainer-rel']//div[@class='wux-controls-abstract wux-layouts-collectionview-cell wux-layouts-datagridview-row']["
										+ i
										+ "]/div[@class='wux-controls-abstract wux-layouts-collectionview-cell wux-layouts-datagridview-cell'][1]"));

						// Getting the 1st col cell's cell-Id to get the 1st position
						String firstcolCellCnt = firstwbcell.getAttribute("cell-id");
						int firstcellId = Integer.parseInt(firstcolCellCnt); // 16

						WebElement strName = driver.findElement(By.xpath(
								"//div[@class='wux-layouts-gridengine-poolcontainer-rel']//div[@class='wux-controls-abstract wux-layouts-collectionview-cell wux-layouts-datagridview-row']["
										+ i + "]//div[@is-visible='true']//div[text()='" + selectObjects + "']"));

						if (strName.isDisplayed()) {
							WebElement wbColCell = driver.findElement(By.xpath("//div[@cell-id="
									+ ((firstcellId - 1) + intPos)
									+ "][@is-visible='true']//div[@class='wux-tweakers wux-tweakers-string wux-tweakers-string-label wux-tweakers-labelonly']"));

							LoggerUtil.debug("colVal : " + colVal);
							LoggerUtil.debug("column value in search result : " + wbColCell.getText());

							if (colVal.equalsIgnoreCase(wbColCell.getText())) {
								finalmatch = true;

								LoggerUtil.debug("Selected wbelement : " + wbColCell.getText());
								WebElement checkBoxCell = driver.findElement(By.xpath("//div[@cell-id='"
										+ (firstcellId - 2)
										+ "' and @is-visible='true']/div[@class='wux-datagridview-selection-label']"));
								driver.click(checkBoxCell);
								return;
							}
						}
					}
					if (finalmatch == false) {
						throw new NoSuchElementException("for the object =" + selectObjects + " ,colname =" + colName
								+ ", col.value =" + colVal + " , data not found for col. value :" + colVal);
					}

				} else {
					throw new NoSuchElementException(
							"for the object =" + selectObjects + " ,colname =" + colName + ", col.value =" + colVal
									+ " , data not found for main object selectObject :" + selectObjects);
				}
			}
		} else {
			try {
				wbElement = driver.findElement(By.xpath(
						"//div[contains(@class, 'wux-layouts-left-poolcontainer')]//div[@is-visible='true']//span[text()='"
								+ selectObjects + "']"));
			} catch (Exception e) {

				try {
					wbElement = driver.findElement(By.xpath(
							"//div[contains(@class, 'wux-layouts-gridengine-poolcontainer-rel')]//div[@is-visible='true']//div[text()='"
									+ selectObjects + "']"));

				} catch (NoSuchElementException elementException) {
					throw new NoSuchElementException("for the object =" + selectObjects + " data not found.");
				}

			}

			driver.click(wbElement);
		}
		LoggerUtil.debug("End of clickCheckBox.");
	}

	/**
	 * This method will click on the object name link to open the object
	 * 
	 * @author Steepgraph Systems
	 * @param driver
	 * @param attributeMap: criteria key is required in this map
	 * @param selectObjects
	 * @return void
	 * @throws Exception
	 */
	@Override
	public void openSearchResult(Driver driver, Map<String, String> attributeMap, String strOpenObject)
			throws Exception {
		LoggerUtil.debug("Start of openSearchResult.");
		switchToDefaultContent(driver);
		driver.switchToFrame(Constants.WINDOW_SHADE_FRAME + "=>" + Constants.STRUCTURE_BROWSER_FRAME);

		String strCriteria = attributeMap.get(Constants.ATTRIBUTE_CRITERIA);
		LoggerUtil.debug("i/p criteria : " + strCriteria);
		LoggerUtil.debug("i/p open objects : " + strOpenObject);

		if (strCriteria == null || "".equalsIgnoreCase(strCriteria))
			throw new Exception("Please provide the selection criteria. Either text or row-number");

		if (strOpenObject == null || "".equalsIgnoreCase(strOpenObject))
			throw new Exception("Please provide the value or row-number for objects to be open in csv file");

		if (Constants.INPUTTYPE_TEXT.equalsIgnoreCase(strCriteria)) {

			WebElement wbElementInputBtn = driver.findElement(By.xpath(
					"//form[@name='emxTableForm']//div[@id='mx_divBody']//div[@id='mx_divTreeBody']//table//tr//td[@title='"
							+ strOpenObject + "']/a"));

			driver.click(wbElementInputBtn);

		} else {

			WebElement wbElementInputBtn = driver.findElement(By.xpath(
					"//form[@name='emxTableForm']//div[@id='mx_divBody']//div[@id='mx_divTreeBody']//table//tr/td/input[@id='rmbrow-0,"
							+ strOpenObject + "']/../../td[@rmbrow='0," + strOpenObject + "']/a"));
			driver.click(wbElementInputBtn);

		}

		LoggerUtil.debug("openSearchResult successful.");
		LoggerUtil.debug("End of openSearchResult.");

	}

	/**
	 * This method will open the specified action toolbar menu command
	 * 
	 * @author Steepgraph Systems
	 * @param driver
	 * @param attributeMap
	 * @return void
	 * @throws Exception
	 */
	@Override
	public void openActionToolbarMenu(Driver driver, Map<String, String> attributeMap) throws Exception {
		LoggerUtil.debug("Start of openActionToolbarMenu.");
		String strTargetWindow = attributeMap.get(Constants.ATTRIBUTE_TARGETWINDOW);
		String commandLabel = attributeMap.get(Constants.ATTRIBUTE_COMMANDLABEL.toLowerCase());
		LoggerUtil.debug("commandlabel: " + commandLabel);
		if (attributeMap == null || "".equals(commandLabel)) {
			throw new Exception("commandlabel is not defined for tag openActionToolbarMenu.");
		}

		String[] labelArray = commandLabel.split(Pattern.quote("|"));
		int length = labelArray.length;

		LoggerUtil.debug("commandLabel length: " + length);

		WebElement weActionsMenu = driver.findElement(By.xpath("//div[@id='divToolbar']//td[@title='Actions']/img[1]"));
		driver.click(weActionsMenu);

		Actions actions = new Actions(driver.getWebDriver());

		if (length > 1) {
			// Click Menu to expand it
			WebElement weCommand = driver
					.findElement(By.xpath("//div[@class='menu-panel page']/div[@class='menu-content']//label[text()='"
							+ labelArray[1] + "']"));

			if (!weCommand.isDisplayed()) {
				WebElement weMenu = driver.findElement(
						By.xpath("//div[@class='menu-panel page']/div[@class='menu-content']//label[text()='"
								+ labelArray[0] + "']"));
				actions.moveToElement(weMenu).click(weMenu);
			}
			LoggerUtil.debug("weCommand :" + weCommand);

			if (length > 2) {
				WebElement weSubCommand = driver.findElement(
						By.xpath("//div[@class='menu-panel page']/div[@class='menu-content']//label[text()='"
								+ labelArray[1] + "']/../../ul//label[text()='" + labelArray[2] + "']"));
				if (!weSubCommand.isDisplayed()) {
					actions.moveToElement(weCommand).click(weCommand);
				}

				LoggerUtil.debug("weSubCommand :" + weSubCommand);
				actions.moveToElement(weSubCommand).click(weSubCommand);
			} else {
				actions.click(weCommand);
			}
		} else {
			WebElement weCommand = driver
					.findElement(By.xpath("//div[@class='menu-panel page']/div[@class='menu-content']//label[text()='"
							+ labelArray[0] + "']"));
			LoggerUtil.debug("weCommand :" + weCommand);
			actions.moveToElement(weCommand).click(weCommand);
		}

		actions.build().perform();
		if (strTargetWindow.equalsIgnoreCase("content")) {
			driver.waitUntil(
					ExpectedConditions.presenceOfElementLocated(By.cssSelector("button[class='btn-primary']")));
		}

		LoggerUtil.debug("End of openActionToolbarMenu.");
	}

	/**
	 * This method will print message on console and debug log file.
	 * 
	 * @author Steepgraph Systems
	 * @param driver
	 * @param attributeMap: expression
	 * @return void
	 * @throws Exception
	 */
	@Override
	public void print(Driver driver, Map<String, String> attributeMap) throws Exception {

		if (attributeMap == null)
			throw new Exception("Message attribute is not define for print tag.");

		String strMessage = attributeMap.get(Constants.ATTRIBUTE_MESSAGE);
		LoggerUtil.debug("strMessage: " + strMessage);
		if (strMessage == null || "".equals(strMessage))
			throw new Exception("expression attribute is not define for print tag.");

		String strCurrentFrameName = driver.getCurrentFrame();
		strMessage = strMessage.replaceAll("[$]current[Ff]rame", strCurrentFrameName);

		String strCurrentWindowTitle = driver.getWebDriver().getTitle();
		strMessage = strMessage.replaceAll("[$]current[Ww]indow", strCurrentWindowTitle);

		String strLineNumber = attributeMap.get("lineNumber");
		strMessage = strMessage.replaceAll("[$]line[Nn]umber", strLineNumber);

		String strCurrentFileName = attributeMap.get("fileName");
		strMessage = strMessage.replaceAll("[$]current[Ff]ile[Nn]ame", strCurrentFileName);

		String strCurrentDate = new Date().toString();
		strMessage = strMessage.replaceAll("[$]current[Dd]ate", strCurrentDate);

		LoggerUtil.debug(strMessage);
		System.out.println(strMessage);

	}

	/**
	 * This method will be used to select date from date chooser. Date format: Month
	 * Day, Year Eg. May 6, 2018
	 * 
	 * @author Steepgraph Systems
	 * @param driver
	 * @param attributeMap: label
	 * @return void
	 * @throws Exception
	 */
	@Override
	public void selectDate(Driver driver, Map<String, String> attributeMap, String strDate) throws Exception {

		LoggerUtil.debug("Start of selectDate ");

		WebElement wbDATE = findElement(driver, attributeMap, true);

		if (!isValidDateFormat(strDate))
			throw new Exception(strDate
					+ " is not matching with required date format. Date format should be 'Month Day, Year' Eg. May 6, 2018");

		selectDateFromDatePicker(driver, strDate, wbDATE);
		LoggerUtil.debug("End of selectDate ");

	}

	/**
	 * This method will be register newly created data into property file or
	 * database
	 * 
	 * @author Steepgraph Systems
	 * @param driver
	 * @param attributeMap: label
	 * @return void
	 * @throws Exception
	 */
	@Override
	public void registerObject(Driver driver, Map<String, String> attributeMap, String strKey) throws Exception {
		LoggerUtil.debug("Start of registerObject.");

		if (!attributeMap.containsKey("id") && !attributeMap.containsKey("input")) {
			throw new Exception("id/input is not defined for registerObject.");
		}
		registerUtil.registerObject(strKey, getValueForRegistration(driver, attributeMap));
		LoggerUtil.debug("End of registerObject.");
	}

	/**
	 * This method will be used to scroll current view port to given element.
	 * 
	 * @author Steepgraph Systems
	 * @param driver
	 * @param attributeMap
	 * @return void
	 * @throws Exception
	 */
	@Override
	public void scrollToElement(Driver driver, Map<String, String> attributeMap) throws Exception {
		LoggerUtil.debug("Start of scrollToElement.");
		int count_vertical;
		int count_horizontal;

		String strscrollarea = attributeMap.get(Constants.ATTRIBUTE_SCROLLAREA);
		LoggerUtil.debug("scrollArea : " + strscrollarea);

		if ((strscrollarea != null && !"".equalsIgnoreCase(strscrollarea))
				&& attributeMap.containsKey(Constants.ATTRIBUTE_SCROLLAREA)) {
			LoggerUtil.debug("processing for Dynamic scroll");

			String StrCountForVertical = attributeMap.get(Constants.ATTRIBUTE_VERTICAL_COUNT);
			if (StrCountForVertical == null || "".equals(StrCountForVertical)) {
				StrCountForVertical = "21";
			}

			try {
				count_vertical = Integer.parseInt(StrCountForVertical);
			} catch (NumberFormatException e) {
				throw new Exception(" countForVertical attribute is not specified properly." + e);
			}

			LoggerUtil.debug("CountForVertical : " + StrCountForVertical);

			String StrCountForhorizontal = attributeMap.get(Constants.ATTRIBUTE_HORIZONTAL_COUNT);
			if (StrCountForhorizontal == null || "".equals(StrCountForhorizontal)) {
				StrCountForhorizontal = "3";
			}

			try {
				count_horizontal = Integer.parseInt(StrCountForhorizontal);
			} catch (NumberFormatException e) {
				throw new Exception(" countForHorizontal attribute is not specified properly." + e);
			}

			LoggerUtil.debug("CountForhorizontal : " + StrCountForhorizontal);

			String strDirection = attributeMap.get(Constants.ATTRIBUTE_DIRECTION);
			LoggerUtil.debug("strDirection: " + strDirection);
			if (null == strDirection || strDirection.isEmpty()) {
				throw new Exception("direction  is not defined for scrollToElement tag.");
			}

			String strlocatorType = attributeMap.get(Constants.LOCATOR_TYPE);
			LoggerUtil.debug("strlocatorType: " + strlocatorType);
			if (strlocatorType == null || "".equals(strlocatorType))
				throw new Exception("Attribute locatorType is not defined for scrollToElement tag.");

			String strlocatorExpression = attributeMap.get(Constants.LOCATOR_EXPRESSION);
			LoggerUtil.debug("strlocatorExpression: " + strlocatorExpression);
			if (strlocatorExpression == null || "".equals(strlocatorExpression))
				throw new Exception("Attribute locatorExpression is not defined for scrollToElement tag.");

			String strclick = attributeMap.get(Constants.ATTRIBUTE_CLICK);
			LoggerUtil.debug("strclick: " + strclick);
			if (strclick == null || "".equals(strclick)) {
				strclick = "left";
			}

			WebElement wb = driver.findElement(By.xpath(strscrollarea + "/div"));
			if (strclick.equalsIgnoreCase("left")) {
				driver.click(wb);
			}

			if (strclick.equalsIgnoreCase("right")) {
				Actions actions = new Actions(driver.getWebDriver());
				actions.contextClick(wb).build().perform();
			}

			String strWait = attributeMap.get(Constants.ATTRIBUTE_WAIT);

			String finalStrWait = getTimeOut(strWait);

			LoggerUtil.debug("polling time : " + finalStrWait);

			HashMap<String, String> findelement = new HashMap<String, String>() {

				/**
				 *   
				 */
				private static final long serialVersionUID = -68521774098799094L;

				{
					put(Constants.LOCATOR_TYPE, strlocatorType);
					put(Constants.LOCATOR_EXPRESSION, strlocatorExpression);
					put(Constants.ATTRIBUTE_CRITERIA, "found");
					put(Constants.ATTRIBUTE_WAIT, finalStrWait);
				}
			};

			while (!ifCondition(driver, findelement, null)) {
				WebElement previous_parentElement = driver.findElement(By.xpath(strscrollarea));
				List<WebElement> previous_allChildElements = previous_parentElement.findElements(By.xpath("*"));
				scrollToElement(driver, strDirection, count_vertical, count_horizontal);
				WebElement present_parentElement = driver.findElement(By.xpath(strscrollarea));
				List<WebElement> present_allChildElements = present_parentElement.findElements(By.xpath("*"));

				if (present_allChildElements.equals(previous_allChildElements)) {

					WebElement previous_parentElement1 = driver.findElement(By.xpath(strscrollarea));
					List<WebElement> previous_allChildElements1 = previous_parentElement1.findElements(By.xpath("*"));
					WebElement LastChild = driver
							.findElement(By.xpath("(" + strscrollarea + "/div" + ")" + "[last()]"));

					if (strclick.equalsIgnoreCase("left")) {
						driver.click(LastChild);
					}

					if (strclick.equalsIgnoreCase("right")) {
						Actions actions = new Actions(driver.getWebDriver());
						actions.contextClick(LastChild).build().perform();
					}

					Actions actions = new Actions(driver.getWebDriver());
					actions.moveToElement(LastChild).build().perform();

					WebElement present_parentElement1 = driver.findElement(By.xpath(strscrollarea));
					List<WebElement> present_allChildElements1 = present_parentElement1.findElements(By.xpath("*"));

					if (present_allChildElements1.equals(previous_allChildElements1)) {
						throw new NoSuchElementException("Targeted Element not found." + strlocatorExpression);
					}
				}
			}
			Actions actions = new Actions(driver.getWebDriver());
			WebElement element = findElement(driver, attributeMap, true);
			actions.moveToElement(element).build().perform();
			LoggerUtil.debug("End of Dynamic scroll.");
		} else {
			WebElement element = findElement(driver, attributeMap, true);
			scrollToElement(driver, element);
		}
		LoggerUtil.debug("End of scrollToElement.");
	}

	public void scrollToElement(Driver driver, String strDirection, int count_vertical, int count_horizontal)
			throws NoSuchElementException {

		Actions actions = new Actions(driver.getWebDriver());
		String strAction = strDirection.toLowerCase();
		switch (strAction) {

		case "up":
			for (int i = 1; i <= count_vertical; i++) {
				actions.sendKeys(Keys.ARROW_UP).build().perform();
			}
			break;
		case "down":
			for (int i = 1; i <= count_vertical; i++) {
				actions.sendKeys(Keys.ARROW_DOWN).build().perform();
			}
			break;
		case "right":
			for (int i = 1; i <= count_horizontal; i++) {
				actions.sendKeys(Keys.ARROW_RIGHT).build().perform();
			}
			break;
		case "left":
			for (int i = 1; i <= count_horizontal; i++) {
				actions.sendKeys(Keys.ARROW_LEFT).build().perform();
			}
			break;
		default:
			throw new IllegalArgumentException(strAction
					+ ": Given Direction is not valid, Please provide correct for the action attribute.Possible values are up , down, right. left.");
		}
	}

	/**
	 * This method will be used to scroll current view port to given element.
	 * 
	 * @author Steepgraph Systems
	 * @param driver
	 * @param attributeMap
	 * @return void
	 * @throws Exception
	 */
	@Override
	public void scrollToElement(Driver driver, WebElement element) throws Exception {
		// this line is commented out for Jira SELENIUM-349
		// with this line whole page was getting scrolled so the upper content was
		// getting hidden
		// without this line everything is working fine even though there is scrollbar
		// present in the page
		// driver.scrollToElement(element);

		// Jira SELENIUM-459
		// moveToElement is not work for the element which are not in current view port
		// in IE.
		// X & Y Coordinate of the element will use to move the element which are not in
		// current view port.
		Actions actions = new Actions(driver.getWebDriver());
		try {
			actions.moveToElement(element).build().perform();
		} catch (Exception e) {
			Point point = element.getLocation();
			int xcord = point.getX();
			int ycord = point.getY();
			actions.moveByOffset(xcord, ycord);
		}
	}

	@Override
	public WebElement getIndentedTableCell(Driver driver, Map<String, String> attributeMap) throws Exception {
		LoggerUtil.debug("Start of getIndentedTableCell ");

		String strPosition = attributeMap.get(Constants.ATTRIBUTE_POSITION);
		LoggerUtil.debug("strPositition: " + strPosition);
		if (strPosition == null || "".equals(strPosition))
			throw new Exception("position attribute is not defined.");

		String strRefId = attributeMap.get(Constants.ATTRIBUTE_REFID);
		LoggerUtil.debug("strRefId: " + strRefId);
		if (strRefId == null || "".equals(strRefId))
			throw new Exception("refid attribute is not defined.");

		String strRowId = registerUtil.getRegisteredData(masterSuiteId, strRefId);

		String tableClass = "bodyTable";

		String insideFreezePane = attributeMap.get(Constants.INSIDE_FREEZE_PANE);
		if (insideFreezePane != null && Constants.CHECK_TRUE.equalsIgnoreCase(insideFreezePane))
			tableClass = "treeBodyTable";

		int position = Integer.parseInt(strPosition);

		WebElement weTableRowColumn = driver.findElement(By.xpath(
				"//table[@id='" + tableClass + "']/tbody/tr[@id='" + strRowId + "']/td[@position='" + position + "']"));

		LoggerUtil.debug("weTableRowColumn: " + weTableRowColumn);
		LoggerUtil.debug("End of getIndentedTableCell ");
		return weTableRowColumn;

	}

	@Override
	public WebElement getIndentedTableRow(Driver driver, Map<String, String> attributeMap) throws Exception {
		LoggerUtil.debug("Start of getIndentedTableRow ");

		String strRefId = attributeMap.get(Constants.ATTRIBUTE_REFID);
		LoggerUtil.debug("strRefId: " + strRefId);
		if (strRefId == null || "".equals(strRefId))
			throw new Exception("refid attribute is not defined.");

		String strRowId = registerUtil.getRegisteredData(masterSuiteId, strRefId);

		WebElement weTableRow = driver
				.findElement(By.xpath("//table[@id='bodyTable']/tbody/tr[@id='" + strRowId + "']"));

		LoggerUtil.debug("End of getIndentedTableRow ");
		return weTableRow;

	}

	/**
	 * This method will be used get indented table row using refid
	 * 
	 * @author Steepgraph Systems
	 * @param driver
	 * @param attributeMap: position
	 * @return void
	 * @throws Exception
	 */
	@Override
	public void editIndentedTableRow(Driver driver, Map<String, String> attributeMap, String strInput)
			throws Exception {

		LoggerUtil.debug("Start of editIndentedTableRow ");

		if (attributeMap == null)
			throw new Exception("position attribute is not define for EditIndentedTableRow tag.");

		String strAction = attributeMap.get(Constants.ATTRIBUTE_ACTION);
		LoggerUtil.debug("strAction: " + strAction);
		if (strAction == null || "".equals(strAction))
			throw new Exception("action attribute is not defined for EditIndentedTableRow tag.");

		WebElement weTableRowColumn = getIndentedTableCell(driver, attributeMap);

		scrollToElement(driver, weTableRowColumn);

		LoggerUtil.debug("weTableRowColumn: " + weTableRowColumn);
		Actions actions = new Actions(driver.getWebDriver());
		InputType inputType = InputType.valueOf(strAction.toLowerCase());
		switch (inputType) {
		case text:

			// actions.moveToElement(weTableRowColumn).perform();
			driver.click(weTableRowColumn);
			WebElement weInputBox = driver.findElement(By.xpath("//div[@class='formLayer']"));
			LoggerUtil.debug("weInputBox : " + weInputBox);
			actions.moveToElement(weInputBox);
			actions.sendKeys(strInput);
			actions.sendKeys(Keys.TAB);
			actions.build().perform();
			break;

		case select:
			// actions = new Actions(driver.getWebDriver());
			// actions.moveToElement(weTableRowColumn).click().build().perform();
			driver.click(weTableRowColumn);
			WebElement weSelect = driver.findElement(By.xpath("//div[@class='formLayer']/select"));
			LoggerUtil.debug("weSelect : " + weSelect);
			driver.selectByText(weSelect, strInput);
			weSelect.sendKeys(Keys.TAB);
			break;

		case click:
			actions = new Actions(driver.getWebDriver());
			actions.moveToElement(weTableRowColumn).click().build().perform();
			break;

		case date:
			driver.click(weTableRowColumn);
			String strXPathDatePicker = "//div[contains(@class,'formLayer')]/a/img";
			selectDateFromDatePicker(driver, "", strInput, strXPathDatePicker);
			break;

		case textwithselect:
			driver.click(weTableRowColumn);
			populateTextWithSelectField(driver, attributeMap, strInput);
			break;

		case checkbox:
			driver.click(weTableRowColumn);
			WebElement weCheckbox = driver.findElement(By.xpath("//div[@class='formLayer']//input[@type='checkbox']"));
			driver.click(weCheckbox);
			weCheckbox.sendKeys(Keys.TAB);
			break;

		case typechooser:
			driver.click(weTableRowColumn);
			selectTypeFromChooser(driver, attributeMap, strInput);
			driver.switchToFrame(weTableRowColumn);
			break;

		case chooser:
			driver.click(weTableRowColumn);
			WebElement weChooserButton = driver
					.findElement(By.xpath("//div[contains(@class,'formLayer')]/input[@type='button']"));
			driver.click(weChooserButton);
			break;

		default:
			throw new Exception("Not a valid action : " + strAction + " for editIndentedTable tag.");
		}

		LoggerUtil.debug("End of editIndentedTableRow ");

	}

	/**
	 * This method will be used check visibility and editability cell of given
	 * indented table row.
	 * 
	 * @author Steepgraph Systems
	 * @param driver
	 * @param attributeMap: position
	 * @return void
	 * @throws Exception
	 */
	@Override
	public void checkIndentedTableRow(Driver driver, Map<String, String> attributeMap) throws Exception {
		LoggerUtil.debug("Start of checkIndentedTableRow ");

		String strCriteria = attributeMap.get(Constants.ATTRIBUTE_CRITERIA);
		LoggerUtil.debug("strCriteria: " + strCriteria);
		if (strCriteria == null || "".equals(strCriteria))
			throw new Exception("criteria attribute is not defined for CheckIndentedTableRow tag.");

		String strColumnLabel = attributeMap.get(Constants.ATTRIBUTE_COLUMNLABEL);
		if (strColumnLabel == null || "".equals(strColumnLabel)) {
			throw new Exception("columnlabel attribute is not defined for CheckIndentedTableRow tag.");
		}

		String strPosition = attributeMap.get(Constants.ATTRIBUTE_POSITION);
		LoggerUtil.debug("strPositition: " + strPosition);
		if (strPosition == null || "".equals(strPosition))
			throw new Exception("position attribute is not define for CheckIndentedTableRow tag.");

		int position = Integer.parseInt(strPosition);
		position--;

		int columnHeaderIndex = (position - 1);
		columnHeaderIndex = position + columnHeaderIndex;
		boolean isDisplayed = false;
		try {
			String tableClass = "headTable";

			String insideFreezePane = attributeMap.get(Constants.INSIDE_FREEZE_PANE);
			if (insideFreezePane != null && Constants.CHECK_TRUE.equalsIgnoreCase(insideFreezePane))
				tableClass = "treeHeadTable";

			driver.findElement(
					By.xpath("//table[@id='" + tableClass + "']//th//td/a[text()='" + strColumnLabel + "']"));
			isDisplayed = true;
		} catch (Exception e) {
			// throw new Exception("Indented table header with position : " + strPosition +
			// " and label : " + strColumnLabel + " id not found");
		}

		IndentedTableCriteria itCriteria = IndentedTableCriteria.valueOf(strCriteria.toLowerCase());
		WebElement weTableRowColumn = getIndentedTableCell(driver, attributeMap);
		highLightElement(driver, attributeMap, weTableRowColumn);
		switch (itCriteria) {
		case editable:
			if (isDisplayed) {
				String strElementeClass = weTableRowColumn.getAttribute(Constants.ATTRIBUTE_CLASS);
				if (strElementeClass == null
						|| !strElementeClass.trim().contains(Constants.INDENTED_TABLE_CELL_CLASS)) {
					throw new AssertionException(
							"Assertion criteria failed : Indented Table cell is not editable. cell position in row : "
									+ strPosition);
				}
				LoggerUtil.debug("Assertion criteria passed : The given Indented Table cell is editable");
			} else {
				throw new Exception("Indented table header with position : " + strPosition + " and label : "
						+ strColumnLabel + " id not found");
			}
			break;

		case noneditable:
			if (isDisplayed) {
				String strElementeClass = weTableRowColumn.getAttribute(Constants.ATTRIBUTE_CLASS);
				if (strElementeClass != null && strElementeClass.trim().contains(Constants.INDENTED_TABLE_CELL_CLASS)) {
					throw new AssertionException(
							"Assertion criteria failed :Indented Table cell is editable. cell position in row : "
									+ strPosition);
				}
				LoggerUtil.debug("Assertion criteria passed : The given Indented Table cell is non editable");
			} else {
				throw new Exception("Indented table header with position : " + strPosition + " and label : "
						+ strColumnLabel + " id not found");
			}
			break;

		case visible:
			if (isDisplayed) {
				if (!weTableRowColumn.isDisplayed()) {
					throw new AssertionException(
							"Assertion criteria failed :Indented Table column is not visible. column position: "
									+ strPosition);
				}
				LoggerUtil.debug("Assertion criteria passed : The given Indented Table cell is visible");
			} else {
				throw new Exception("Indented table header with position : " + strPosition + " and label : "
						+ strColumnLabel + " id not found");
			}
			break;

		case invisible:
			if (isDisplayed) {
				if (weTableRowColumn.isDisplayed()) {
					throw new AssertionException(
							"Assertion criteria failed :Indented Table column is visible. column position: "
									+ strPosition);
				}
			}
			LoggerUtil.debug("Assertion criteria passed : The given Indented Table cell is invisible");
			break;
		default:
			throw new Exception("Not a valid criteria : " + strCriteria + " for tag CheckIndentedTableRow tag.");
		}

		LoggerUtil.debug("End of checkIndentedTableRow ");
	}

	/**
	 * This method is used to check if an element satisfies specified conditions for
	 * further processing.
	 * 
	 * @author Steepgraph Systems
	 * @param driver
	 * @param attributeMap
	 * @return void
	 * @throws Exception
	 */
	@Override
	public void checkElement(Driver driver, Map<String, String> attributeMap) throws Exception {
		LoggerUtil.debug("Start of checkElement ");

		String strlocatorType = attributeMap.get(Constants.LOCATOR_TYPE);
		LoggerUtil.debug("strlocatorType: " + strlocatorType);
		if (strlocatorType == null || "".equals(strlocatorType))
			throw new Exception("Attribute locatorType is not defined for CheckElement tag.");

		String strlocatorExpression = attributeMap.get(Constants.LOCATOR_EXPRESSION);
		LoggerUtil.debug("strlocatorExpression: " + strlocatorExpression);
		if (strlocatorExpression == null || "".equals(strlocatorExpression))
			throw new Exception("Attribute locatorExpression is not defined for CheckElement tag.");

		String strCriteria = attributeMap.get(Constants.ATTRIBUTE_CRITERIA);
		LoggerUtil.debug("strCriteria : " + strCriteria);
		if (strCriteria == null || "".equals(strCriteria))
			throw new Exception("attribute criteria is not defined for CheckElement tag.");

		String strWait = attributeMap.get(Constants.ATTRIBUTE_WAIT);
		strWait = getTimeOut(strWait);
		int iWait = Integer.parseInt(strWait);
		PropertyUtil propertyutil = PropertyUtil.newInstance();
		String strPollingInterval = propertyutil
				.getProperty(Constants.PROPERTY_KEY_EXECUTION_STEP_TIMEOUT_POLLINGINTERVAL);
		int iPollingInterval = Integer.parseInt(strPollingInterval);

		LoggerUtil.debug("timeout : " + iWait);

		int iRetryCount = getRetryCount(iWait, iPollingInterval);

		LoggerUtil.debug("polling time : " + iRetryCount);

		WebElement webElement = null;

		boolean isDisplayed = false;
		boolean disabledFlag = false;
		boolean classdisabledFlag = false;
		try {

			FluentWait<WebDriver> w = new FluentWait<WebDriver>(driver.getWebDriver())
					.withTimeout(Duration.ofMillis(iWait)).pollingEvery(Duration.ofMillis(iRetryCount))
					.ignoring(NoSuchElementException.class);
			strlocatorType = strlocatorType.toLowerCase();
			LocatorType locator = LocatorType.valueOf(strlocatorType);
			switch (locator) {
			case xpath:
				try {
					webElement = w.until(new Function<WebDriver, WebElement>() {

						@Override
						public WebElement apply(WebDriver driver) {
							return driver.findElement(By.xpath(strlocatorExpression));
						}
					});
				} catch (Exception e) {
					LoggerUtil.debug("Element not found.");
					throw new Exception(e);
				}
				break;

			case id:
				try {
					webElement = w.until(new Function<WebDriver, WebElement>() {

						@Override
						public WebElement apply(WebDriver driver) {
							return driver.findElement(By.id(strlocatorExpression));
						}
					});
				} catch (Exception e) {
					LoggerUtil.debug("Element not found.");
					throw new Exception(e);
				}
				break;

			case cssselector:
				try {
					webElement = w.until(new Function<WebDriver, WebElement>() {

						@Override
						public WebElement apply(WebDriver driver) {
							return driver.findElement(By.cssSelector(strlocatorExpression));
						}
					});
				} catch (Exception e) {
					LoggerUtil.debug("Element not found.");
					throw new Exception(e);
				}
				break;

			case name:
				try {
					webElement = w.until(new Function<WebDriver, WebElement>() {

						@Override
						public WebElement apply(WebDriver driver) {
							return driver.findElement(By.name(strlocatorExpression));
						}
					});
				} catch (Exception e) {
					LoggerUtil.debug("Element not found.");
					throw new Exception(e);
				}
				break;

			default:
				throw new Exception("Locator type = " + strlocatorType + " is not valid.");
			}

			isDisplayed = webElement.isDisplayed();
			if ((isAttributePresent(webElement, "disabled"))) {
				disabledFlag = true;
			}
			if (isAttributePresent(webElement, "readonly")) {
				disabledFlag = true;

				if (isAttributePresent(webElement, "aria-readonly")) {
					String streValue = webElement.getAttribute("aria-readonly");
					LoggerUtil.debug("aria-readonly : ." + streValue);
					if (streValue != null && streValue.equalsIgnoreCase("false")) {
						disabledFlag = false;
					}
				}
			}

			String classvalue = webElement.getAttribute("class");
			String[] arrOfStr = classvalue.split(" ");
			int size = arrOfStr.length;

			for (int i = 0; i < size; i++) {
				if (arrOfStr[i].equals("disabled")) {
					classdisabledFlag = true;
					break;
				}
			}
		} catch (Exception e) {
			// intentionally handled as unavailable elements throws:
			// org.openqa.selenium.TimeoutException
		}

		switch (strCriteria) {
		case "visible":

			if (isDisplayed) {
				if (webElement != null)
					highLightElement(driver, webElement, "");
				LoggerUtil.debug("Assertion criteria passed: Specified element is visible.");
			} else {
				throw new AssertionException("Assertion criteria failed: Specified element is not visible.");
			}

			break;

		case "invisible":

			if (isDisplayed) {
				String style = "border: 2px solid rgba(255, 0, 0, 0.8);";
				if (webElement != null) {
					highLightElement(driver, webElement, style);
				}
				throw new AssertionException("Assertion criteria failed: Specified element is visible.");
			} else {
				LoggerUtil.debug("Assertion criteria passed: Specified element is not visible.");
			}

			break;

		case "editable":

			if (!disabledFlag)
				LoggerUtil.debug("Assertion criteria passed: Specified element is editable.");
			else {
				String style = "border: 2px solid rgba(255, 0, 0, 0.8);";
				if (webElement != null) {
					highLightElement(driver, webElement, style);
				}
				LoggerUtil.debug("Assertion criteria failed: Specified element is not editable.");
				throw new AssertionException("Assertion criteria failed: Specified element is not editable.");
			}

			break;

		case "noneditable":

			if (disabledFlag)
				LoggerUtil.debug("Assertion criteria passed: Specified element is not editable.");
			else {
				String style = "border: 2px solid rgba(255, 0, 0, 0.8);";
				if (webElement != null) {
					highLightElement(driver, webElement, style);
				}
				LoggerUtil.debug("Assertion criteria failed: Specified element is editable.");
				throw new AssertionException("Assertion criteria failed: Specified element is editable.");
			}

			break;
		case "enabled":

			if (isButtonEnabled(driver, strlocatorType, strlocatorExpression)) {
				LoggerUtil.debug("Assertion criteria passed: Specified element is enabled.");
			} else {
				String style = "border: 2px solid rgba(255, 0, 0, 0.8);";
				if (webElement != null) {
					highLightElement(driver, webElement, style);
				}
				LoggerUtil.debug("Assertion criteria failed: Specified element is not enabled.");
				throw new AssertionException("Assertion criteria failed: Specified element is not enabled.");
			}

			break;

		case "disabled":

			if (isButtonEnabled(driver, strlocatorType, strlocatorExpression)) {
				String style = "border: 2px solid rgba(255, 0, 0, 0.8);";
				if (webElement != null) {
					highLightElement(driver, webElement, style);
				}
				LoggerUtil.debug("Assertion criteria failed: Specified element is not disabled.");
				throw new AssertionException("Assertion criteria failed: Specified element is not disabled.");
			} else {
				LoggerUtil.debug("Assertion criteria passed: Specified element is disabled.");
			}

			break;

		case "class: disabled":

			if (!classdisabledFlag) {
				String style = "border: 2px solid rgba(255, 0, 0, 0.8);";
				if (webElement != null) {
					highLightElement(driver, webElement, style);
				}
				LoggerUtil.debug("Assertion criteria failed: Specified element's class is not disabled.");
				throw new AssertionException("Assertion criteria failed: Specified element's class is not disabled.");
			} else {
				LoggerUtil.debug("Assertion criteria passed: Specified element's class is disabled.");
			}

			break;

		default:
			throw new Exception("Not a valid property: " + strCriteria + " for tag checkElement.");

		}
		LoggerUtil.debug("End of checkElement.");
	}

	/**
	 * This method is used to check if attribute exists for specified WebElement and
	 * contains a String in that attribute.
	 * 
	 * @author Steepgraph Systems
	 * @param element
	 * @param attribute
	 * @param disabledString
	 * @return boolean
	 * @throws Exception
	 */
	private boolean isButtonEnabled(Driver driver, String strlocatorType, String strlocatorExpression)
			throws Exception {
		boolean result = false;

		WebElement element = driver.findElement(strlocatorType, strlocatorExpression);
		if (element.isEnabled()) {
			String value = element.getAttribute(Constants.ATTRIBUTE_CLASS);
			if (value != null) {
				if (!value.contains(Constants.CLASS_BUTTON_DISABLED)) {
					result = true;
				}
			}
		}

		return result;
	}

	/**
	 * This method is used to check if attribute exists for specified WebElement.
	 * 
	 * @author Steepgraph Systems
	 * @param element
	 * @param attribute
	 * @return boolean
	 * @throws Exception
	 */
	private boolean isAttributePresent(WebElement element, String attribute) {
		Boolean result = false;
		try {
			String value = element.getAttribute(attribute);
			final List<String> lstOfTags = new ArrayList<String>() {
				/**
				 * 
				 */
				private static final long serialVersionUID = 4769080828904097737L;

				{
					add("input");
					add("textarea");
					add("select");
				}
			};

			if (value != null || !lstOfTags.contains(element.getTagName())) {
				result = true;
			}
		} catch (Exception e) {
		}
		return result;
	}

	/**
	 * This method is used to check if attribute exists for specified WebElement.
	 * 
	 * @author Steepgraph Systems
	 * @param element
	 * @param attribute
	 * @throws Exception
	 */
	@Override
	public void showNotification(Driver driver, Map<String, String> attributeMap) throws Exception {
		LoggerUtil.debug("Start of showNotification.");
		if (attributeMap == null || "".equals(attributeMap.get(Constants.ATTRIBUTE_MESSAGE))) {
			throw new Exception("message attribute is not defined for showNotification tag.");
		}

		String strMessage = attributeMap.get(Constants.ATTRIBUTE_MESSAGE);
		LoggerUtil.debug("strMessage: " + strMessage);
		if (strMessage == null || "".equals(strMessage))
			throw new Exception("Attribute Message is not defined for ShowNotification tag.");

		LoggerUtil.debug(strMessage);
		commonUtilobj.showNotification(strMessage);
		LoggerUtil.debug("End of showNotification.");

	}

	/**
	 * This method is used to populate the indented table field with combined text
	 * box and select elements in edit mode
	 * 
	 * @author Steepgraph Systems
	 * @param driver
	 * @param attributeMap
	 * @param strInput
	 * @throws Exception
	 */
	protected void populateTextWithSelectField(Driver driver, Map<String, String> attributeMap, String strInput)
			throws Exception {
		LoggerUtil.debug("Start of populateTextWithSelectField.");
		String[] strInputValues = strInput.split(Pattern.quote("|"));
		String TextValue = strInputValues[0];
		LoggerUtil.debug("strInputValues: " + TextValue);
		String SelectText = strInputValues[1];
		LoggerUtil.debug("strInputValues: " + SelectText);

		WebElement weInputBox = driver.findElement(By.xpath("//div[@class='formLayer']//input[@type='text']"));
		driver.click(weInputBox);
		driver.writeText(weInputBox, TextValue);

		WebElement weSelect = driver.findElement(By.xpath("//div[@class='formLayer']//select"));
		driver.selectByText(weSelect, SelectText);

		WebElement weDone = driver.findElement(By.xpath("//div[@class='formLayer']/table/tbody/tr[2]/td/input[1]"));
		driver.click(weDone);

		LoggerUtil.debug("End of populateTextWithSelectField.");

	}

	/**
	 * This is the implementation of IF tag.
	 * 
	 * @author SteepGraph Systems
	 * @param element
	 * @param attribute
	 * @throws Exception
	 */
	@Override
	public boolean ifCondition(Driver driver, Map<String, String> attributeMap, String strInputText) throws Exception {
		LoggerUtil.debug("Start of ifCondition.");

		WebElement webElement = null;
		if (attributeMap == null || "".equals(attributeMap.get(Constants.ATTRIBUTE_MESSAGE))) {
			throw new IllegalArgumentException("condition attribute is not defined for ifCondition tag.");
		}

		String strCriteria = attributeMap.get(Constants.ATTRIBUTE_CRITERIA);
		LoggerUtil.debug("criteria : " + strCriteria);
		if ((strCriteria == null || "".equalsIgnoreCase(strCriteria))
				|| !attributeMap.containsKey(Constants.ATTRIBUTE_CRITERIA)) {
			throw new Exception(" criteria attribute is not specified for ifCondition tag.");
		}

		String strRefId = attributeMap.get(Constants.ATTRIBUTE_REFID);
		LoggerUtil.debug("refId : " + strRefId);

		String strlocatorType = attributeMap.get(Constants.LOCATOR_TYPE);
		LoggerUtil.debug("strlocatorType: " + strlocatorType);

		String strlocatorExpression = attributeMap.get(Constants.LOCATOR_EXPRESSION);
		LoggerUtil.debug("strlocatorExpression: " + strlocatorExpression);

		if (attributeMap.containsKey(Constants.ATTRIBUTE_REFID) && (null != strRefId)) {
			webElement = webElementMap.get(strRefId);
			if (webElement == null) {
				LoggerUtil.debug("Warning !!!Element not found by using refid attribute !!!");
			}
		} else {

			if (strlocatorType == null || "".equals(strlocatorType))
				throw new Exception("Attribute locatorType is not defined for ifCondition tag.");

			if (strlocatorExpression == null || "".equals(strlocatorExpression))
				throw new Exception("Attribute locatorExpression is not defined for ifCondition tag.");
		}

		String strWait = attributeMap.get(Constants.ATTRIBUTE_WAIT);
		strWait = getTimeOut(strWait);
		int iWait = Integer.parseInt(strWait);
		PropertyUtil propertyutil = PropertyUtil.newInstance();
		String strPollingInterval = propertyutil
				.getProperty(Constants.PROPERTY_KEY_EXECUTION_STEP_TIMEOUT_POLLINGINTERVAL);
		int iPollingInterval = Integer.parseInt(strPollingInterval);
		LoggerUtil.debug("timeout : " + iWait);

		int iRetryCount = getRetryCount(iWait, iPollingInterval);
		LoggerUtil.debug("polling time : " + iRetryCount);

		String strCondition = attributeMap.get(Constants.ATTRIBUTE_CONDITION);
		LoggerUtil.debug("condition : " + strCondition);

		if (strCriteria.equalsIgnoreCase("text") || strCriteria.equalsIgnoreCase("attribute")) {
			if (!attributeMap.containsKey(Constants.ATTRIBUTE_ID)) {
				throw new Exception(" id attribute is not specified for ifCondition tag.");
			}
			if (strCondition == null || "".equals(strCondition)) {
				throw new Exception(
						" condition attribute is not specified for ifCondition tag as it is required when criteria is 'text' or 'attribute'");
			}
		}

		String strAttribute = attributeMap.get(Constants.ATTRIBUTE_ATTRIBUTE);
		LoggerUtil.debug("attribute : " + strAttribute);

		if (strCriteria.equalsIgnoreCase("attribute")) {
			if (strAttribute.equalsIgnoreCase("") || strAttribute == null) {
				throw new Exception(" 'attribute' attribute is not specified for ifCondition tag.");
			}
			if (strCondition.equalsIgnoreCase("") || strCondition == null) {
				throw new Exception(" 'condition' attribute is not specified for ifCondition tag.");
			}
		}

		if (webElement == null) {
			try {

				FluentWait<WebDriver> w = new FluentWait<WebDriver>(driver.getWebDriver())
						.withTimeout(Duration.ofMillis(iWait)).pollingEvery(Duration.ofMillis(iRetryCount))
						.ignoring(NoSuchElementException.class);

				strlocatorType = strlocatorType.toLowerCase();
				LocatorType locator = LocatorType.valueOf(strlocatorType);
				switch (locator) {
				case xpath:
					try {
						webElement = w.until(new Function<WebDriver, WebElement>() {

							@Override
							public WebElement apply(WebDriver driver) {
								return driver.findElement(By.xpath(strlocatorExpression));
							}
						});
					} catch (Exception e) {
						LoggerUtil.debug("Element not found.");
						throw new Exception(e);
					}
					break;

				case id:
					try {
						webElement = w.until(new Function<WebDriver, WebElement>() {

							@Override
							public WebElement apply(WebDriver driver) {
								return driver.findElement(By.id(strlocatorExpression));
							}
						});
					} catch (Exception e) {
						LoggerUtil.debug("Element not found.");
						throw new Exception(e);
					}
					break;

				case cssselector:
					try {
						webElement = w.until(new Function<WebDriver, WebElement>() {

							@Override
							public WebElement apply(WebDriver driver) {
								return driver.findElement(By.cssSelector(strlocatorExpression));
							}
						});
					} catch (Exception e) {
						LoggerUtil.debug("Element not found.");
						throw new Exception(e);
					}
					break;

				case name:
					try {
						webElement = w.until(new Function<WebDriver, WebElement>() {

							@Override
							public WebElement apply(WebDriver driver) {
								return driver.findElement(By.name(strlocatorExpression));
							}
						});
					} catch (Exception e) {
						LoggerUtil.debug("Element not found.");
						throw new Exception(e);
					}
					break;

				default:
					throw new Exception("Locator type = " + strlocatorType + " is not valid.");
				}

				webElement.isDisplayed();
			} catch (Exception e) {
				LoggerUtil.debug("Element not found.");
				// throw new Exception(e);
				return false;
			}
		}

		LoggerUtil.debug("wbElement : " + webElement);
		boolean result = evaluateIfCondition(webElement, strCondition, strCriteria, strAttribute, strInputText);
		LoggerUtil.debug("End of ifCondition return Value: " + result);
		return result;
	}

	/**
	 * Evaluate IF condition using different criteria
	 * 
	 * @author Steepgraph Systems
	 * @param driver
	 * @return wbElement
	 * @param strCondition
	 * @param strCriteria
	 * @param strAttribute
	 * @param strInputText
	 * @throws Exception
	 */
	public boolean evaluateIfCondition(WebElement wbElement, String strCondition, String strCriteria,
			String strAttribute, String strInputText) throws Exception {

		boolean bResult = false;
		if (wbElement == null)
			return bResult;

		IfCriteria itCriteria = null;
		try {
			itCriteria = IfCriteria.valueOf(strCriteria.toLowerCase());
		} catch (Exception e) {
			throw new Exception("criteria attribute is not specified properly for ifCondition tag.");
		}

		switch (itCriteria) {

		case text:

			String strTextValue = wbElement.getText();
			if (("=".equals(strCondition) && strInputText.equalsIgnoreCase(strTextValue))
					|| ("!=".equals(strCondition) && !strInputText.equalsIgnoreCase(strTextValue))) {
				bResult = true;
			}
			break;

		case attribute:

			String strAttributeValue = wbElement.getAttribute(strAttribute);

			LoggerUtil.debug("attribute's value get from the UI:" + strAttributeValue);
			LoggerUtil.debug("attribute's value get from user i/p:" + strInputText);
			try {
				if (strAttributeValue.equalsIgnoreCase(strInputText)) {
					LoggerUtil.debug("attribute's value matched");
				} else {
					LoggerUtil.debug("attribute's value not matched");
				}
				if (("=".equals(strCondition) && strInputText.equalsIgnoreCase(strAttributeValue))
						|| ("!=".equals(strCondition) && !strInputText.equalsIgnoreCase(strAttributeValue))) {
					bResult = true;
				}
			} catch (NullPointerException e) {
				LoggerUtil.debug("WARNING !!! Please provide the correct 'attribute' as an attribute !!!");
			}

			break;

		case visible:

			boolean isDisplayed = wbElement.isDisplayed();
			if (isDisplayed)
				bResult = true;
			break;

		case found:
			bResult = true;
			break;
		}

		return bResult;
	}

	/**
	 * This method is used to populate the type chooser indented table field with
	 * apporpriate value in edit mode
	 * 
	 * @author Steepgraph Systems
	 * @param driver
	 * @param attributeMap
	 * @param strInput
	 * @throws Exception
	 */
	protected void selectTypeFromChooser(Driver driver, Map<String, String> attributeMap, String strInput)
			throws Exception {
		LoggerUtil.debug("Start of selectTypeFromChooser.");

		WebElement weTypeChooser = driver.findElement(By.xpath("//div[@class='formLayer']//input[@type='button']"));
		driver.click(weTypeChooser);

		switchToWindow(driver, attributeMap);

		WebElement weTextFilter = driver.findElement(By.cssSelector("input[id='txtFilter']"));
		weTextFilter.click();
		weTextFilter.clear();
		weTextFilter.sendKeys(strInput);

		WebElement weFilterCheckBox = driver.findElement(By.cssSelector("input[id='chkTopLevelOnly']"));
		driver.click(weFilterCheckBox);

		WebElement weFilterButton = driver.findElement(By.cssSelector("input[id='btnFilter']"));
		driver.click(weFilterButton);

		WebElement weSelectFilterResult = driver.findElement(By.xpath("//input[contains(@value,'" + strInput + "')]"));
		driver.click(weSelectFilterResult);

		WebElement weSubmit = driver.findElement(By.cssSelector("button[class='btn-primary']"));
		driver.click(weSubmit);

		switchToParentWindow(driver);

		LoggerUtil.debug("End of selectTypeFromChooser.");
	}

	/**
	 * Method to select project organization and role.
	 * 
	 * @author Steepgraph Systems
	 * @param driver
	 * @param attributeMap
	 * @return void
	 * @throws Exception
	 */
	@Override
	public void selectSecurityContext(Driver driver, Map<String, String> attributeMap) throws Exception {

		LoggerUtil.debug("Start of selectSecurityContext.");

		String hasSecurityContextPage = propertyUtil
				.getProperty(Constants.PROPERTY_KEY_3DSPACE_HAS_SECURITY_CONTEXT_PAGE);

		if (hasSecurityContextPage == null || "".equals(hasSecurityContextPage))
			hasSecurityContextPage = Constants.CHECK_TRUE;

		LoggerUtil.debug("hasSecurityContextPage : " + hasSecurityContextPage);

		if (Constants.CHECK_TRUE.equalsIgnoreCase(hasSecurityContextPage)) {
			String location = attributeMap.get(Constants.ATTRIBUTE_LOCATION);
			LoggerUtil.debug("location : " + location);
			if (location != null && !"".equalsIgnoreCase(location)) {
				WebElement wbLocation = driver
						.findElement(By.xpath("//form[@id='loginForm']//select[@id='txtNewLocation']"));
				driver.selectByText(wbLocation, location);
			}

			String organization = attributeMap.get(Constants.ATTRIBUTE_ORGANIZATION);
			LoggerUtil.debug("organization : " + organization);
			if (organization != null && !"".equalsIgnoreCase(organization)) {
				WebElement wbOrganization = driver
						.findElement(By.xpath("//form[@id='loginForm']//select[@id='Organization']"));
				driver.selectByText(wbOrganization, organization);
			}

			String project = attributeMap.get(Constants.ATTRIBUTE_PROJECT);
			LoggerUtil.debug("project : " + project);
			if (project != null && !"".equalsIgnoreCase(project)) {
				WebElement wbProject = driver.findElement(By.xpath("//form[@id='loginForm']//select[@id='Project']"));
				driver.selectByText(wbProject, project);
			}

			String role = attributeMap.get(Constants.ATTRIBUTE_ROLE);
			LoggerUtil.debug("role : " + role);
			if (role != null && !"".equalsIgnoreCase(role)) {
				WebElement wbRole = driver.findElement(By.xpath("//form[@id='loginForm']//select[@id='Role']"));
				driver.selectByText(wbRole, role);
			}

			WebElement wbSubmit = driver.findElement(By.xpath("//form[@id='loginForm']//button[@id='submitButton']"));
			driver.click(wbSubmit);
			LoggerUtil.debug("submit buttion clicked");
		}

		LoggerUtil.debug("End of selectSecurityContext.");
	}

	/**
	 * Method to click element on window dialog using Sikuli API.
	 * 
	 * @author Steepgraph Systems
	 * @param driver
	 * @param attributeMap
	 * @return void
	 * @throws Exception
	 */
	@Override
	public void clickWindowElement(Driver driver, Map<String, String> attributeMap) throws Exception {
		LoggerUtil.debug("Start of clickWindowElement.");

		String skiperror = null;
		int iWait;
		if (attributeMap.get(Constants.IMAGE_PATH) == null || "".equals(attributeMap.get(Constants.IMAGE_PATH))) {
			throw new Exception("imagepath attribute is not defined for ClickWindowElement tag");
		}

		String imagepath = attributeMap.get(Constants.IMAGE_PATH);
		File file = new File(imagepath);
		if (!file.exists()) {
			throw new IllegalArgumentException(imagepath + " file doesn't exist");
		}

		try {
			skiperror = attributeMap.get(Constants.ATTRIBUTE_SKIP_ERROR);
			if (skiperror == null || "".equals(skiperror))
				skiperror = Constants.CHECK_TRUE;

			LoggerUtil.debug("skiperror : " + skiperror);

			String wait = attributeMap.get(Constants.ATTRIBUTE_WAIT);

			wait = getTimeOut(wait);
			LoggerUtil.debug("Wait : " + wait);

			iWait = Integer.parseInt(wait) / 1000;

			String absoulteImagePath = file.getAbsolutePath();
			LoggerUtil.debug("absoulteImagePath : " + absoulteImagePath);

			Region imageRegion = null;
			String refid = attributeMap.get(Constants.ATTRIBUTE_REFID);
			if (refid != null && !"".equals(refid) && imageRegionMap.containsKey(refid))
				imageRegion = imageRegionMap.get(refid);

			org.sikuli.script.Pattern imagePattern = new org.sikuli.script.Pattern(absoulteImagePath);

			int attempt = 0;
			while (attempt < 5) {
				LoggerUtil.debug("attempt: " + attempt);
				try {
					if (imageRegion != null) {
						imageRegion.wait(imagePattern, iWait);
						imageRegion.click(imagePattern);
					} else {
						Screen screen = new Screen();
						screen.wait(imagePattern, iWait);
						screen.click(imagePattern);
					}
					attempt = 5;
				} catch (Exception e) {
					attempt++;
					if (attempt >= 5)
						throw e;
				}
			}

			LoggerUtil.debug("End of clickWindowElement.");
		} catch (

		Exception e) {
			if (Constants.CHECK_TRUE.equalsIgnoreCase(skiperror))
				LoggerUtil.debug("clickWindowElement error skipped : Exception " + e);
			else
				throw e;
		}
	}

	/**
	 * This Method will be used to validate input date with given format
	 * 
	 * @author SteepGraph Systems
	 * @param date
	 * @return true if input date matching with given format else return false.
	 * @throws Exception
	 */
	@Override
	public boolean isValidDateFormat(String date) throws Exception {

		Pattern p = Pattern.compile("^[a-zA-Z]+\\s{1}\\d{1,2}[,]{1}\\s{1}\\d{4}$");
		Matcher m = p.matcher(date);
		return m.matches();
	}

	/**
	 * Method to expand MyDesk menu and click commands in it
	 * 
	 * @author Steepgraph Systems
	 * @param driver
	 * @param attributeMap
	 * @return void
	 * @throws Exception
	 */
	@Override
	public void clickMyDeskMenu(Driver driver, Map<String, String> attributeMap) throws Exception {
		LoggerUtil.debug("Start of clickMyDeskMenu.");

		switchToDefaultContent(driver);

		String commandLabel = attributeMap.get(Constants.ATTRIBUTE_COMMANDLABEL);

		if (attributeMap == null || attributeMap.size() == 0
				|| !attributeMap.containsKey(Constants.ATTRIBUTE_COMMANDLABEL)) {
			throw new Exception("commandLabel is not defined for tag clickMyDeskMenu.");
		}

		LoggerUtil.debug("commandLabel: " + commandLabel);

		String[] labelArray = commandLabel.split(Pattern.quote("|"));
		int length = labelArray.length;

		LoggerUtil.debug("commandLabel length: " + length);

		WebElement wbGlobalMenuPanel = driver
				.findElement(By.xpath("//div[@class='slide-in-panel menu categories my-desk footer-notice-yes']"));

		LoggerUtil.debug("div with class='slide-in-panel menu categories my-desk footer-notice-yes' found");

		Actions action = new Actions(driver.getWebDriver());

		for (int i = 0; i < length; i++) {

			if (i + 1 == length) {

				WebElement weGlobalActionCommand = driver.findElement(wbGlobalMenuPanel,
						By.xpath(".//descendant::label[text() = '" + labelArray[i] + "']"));
				driver.click(weGlobalActionCommand);
			} else {
				WebElement commandElement = null;
				boolean isExpanded = false;

				if (i == 0) {
					commandElement = driver.findElement(wbGlobalMenuPanel,
							By.xpath("//label[text() = '" + labelArray[i] + "']/../.."));
					String commandClass = commandElement.getAttribute(Constants.ATTRIBUTE_CLASS);
					LoggerUtil.debug("commandClass: " + commandClass);
					if ("menu expanded".equalsIgnoreCase(commandClass)) {
						isExpanded = true;
						LoggerUtil.debug("isExpanded: " + isExpanded);
					}
				} else {
					commandElement = driver.findElement(wbGlobalMenuPanel,
							By.xpath(".//descendant::label[text() = 'System Data']/../../../parent::li"));
					String commandClass = commandElement.getAttribute(Constants.ATTRIBUTE_CLASS);
					LoggerUtil.debug("commandClass: " + commandClass);
					if ("menu expanded".equalsIgnoreCase(commandClass)) {
						isExpanded = true;
						LoggerUtil.debug("isExpanded: " + isExpanded);
					}
				}
				if (!isExpanded) {
					LoggerUtil.debug("\nNot Expanded. Clicking: " + labelArray[i]);
					WebElement weGlobalActionCommand = driver.findElement(wbGlobalMenuPanel,
							By.xpath(".//descendant::label[text() = '" + labelArray[i] + "']/../.."));
					action.click(weGlobalActionCommand);
				}

			}
			action.build().perform();

		}
		LoggerUtil.debug("End of clickMyDeskMenu.");
	}

	/**
	 * Method to set content(text) of any tag
	 * 
	 * @author Steepgraph Systems
	 * @param driver
	 * @param attributeMap
	 * @return void
	 * @throws Exception
	 */
	@Override
	public void setContent(Driver driver, Map<String, String> attributeMap, String text) throws Exception {

		LoggerUtil.debug("Started setContent.");
		if (text == null || "".equals(text))
			throw new Exception(
					"input is not defined in csv column whose column name matches with id attribute of this tag.");

		WebElement elementToSetContent = findElement(driver, attributeMap, true);

		JavascriptExecutor jsExecutor = (JavascriptExecutor) driver.getWebDriver();
		jsExecutor.executeScript("arguments[0].textContent = arguments[1];", elementToSetContent, text);

		LoggerUtil.debug("End of setContent.");

	}

	/**
	 * 
	 * This method will be used to send input to windows dialog by image processing
	 * 
	 * @author Steepgraph Systems
	 * @param driver
	 * @param attributeMap
	 * @param input
	 * @return void
	 * @throws Exception
	 */

	@Override
	public void inputWindowElement(Driver driver, Map<String, String> attributeMap, String input) throws Exception {

		LoggerUtil.debug("Start of inputWindowElement.");

		String skiperror = null;
		int iWait;

		try {
			skiperror = attributeMap.get(Constants.ATTRIBUTE_SKIP_ERROR);
			if (skiperror == null || "".equals(skiperror))
				skiperror = Constants.CHECK_TRUE;

			LoggerUtil.debug("skiperror : " + skiperror);

			if (input == null || "".equals(input))
				throw new Exception("Input is not given in csv");

			if (attributeMap.get(Constants.IMAGE_PATH) == null || "".equals(attributeMap.get(Constants.IMAGE_PATH))) {
				throw new Exception("imagepath attribute is not defined for InputWindowElement tag");
			}

			File inputFile = new File(input);
			if (!inputFile.exists())
				throw new Exception(input + " path doesn't exist");

			input = inputFile.getAbsolutePath();

			LoggerUtil.debug("absoulte input file path : " + input);

			String wait = attributeMap.get(Constants.ATTRIBUTE_WAIT);

			wait = getTimeOut(wait);
			LoggerUtil.debug("Wait : " + wait);
			iWait = Integer.parseInt(wait) / 1000;

			String imagepath = attributeMap.get(Constants.IMAGE_PATH);
			File file = new File(imagepath);
			if (!file.exists())
				throw new Exception(imagepath + " file doesn't exist");

			String absoulteImagePath = file.getAbsolutePath();
			LoggerUtil.debug("absoulteImagePath : " + absoulteImagePath);

			Region imageRegion = null;
			String refid = attributeMap.get(Constants.ATTRIBUTE_REFID);
			if (refid != null && !"".equals(refid) && imageRegionMap.containsKey(refid))
				imageRegion = imageRegionMap.get(refid);

			org.sikuli.script.Pattern imagePattern = new org.sikuli.script.Pattern(absoulteImagePath);

			int attempt = 0;
			while (attempt < 5) {
				LoggerUtil.debug("attempt: " + attempt);
				try {
					if (imageRegion != null) {
						imageRegion.wait(imagePattern, iWait);
						imageRegion.click(imagePattern);
						imageRegion.type(imagePattern, input);
					} else {
						Screen screen = new Screen();
						screen.wait(imagePattern, iWait);
						screen.click(imagePattern);
						screen.type(imagePattern, input);
					}
					attempt = 5;
				} catch (Exception e) {
					attempt++;
					if (attempt >= 5)
						throw e;
				}
			}

			LoggerUtil.debug("End of inputWindowElement.");

		} catch (Exception e) {
			if (Constants.CHECK_TRUE.equalsIgnoreCase(skiperror))
				LoggerUtil.debug("InputWindowElement error skipped : Exception " + e);
			else
				throw e;
		}
	}

	/**
	 * This method will be used to validate given condition to confirm test cases
	 * execution.
	 * 
	 * @author SteepGraph Systems
	 * @param driver
	 * @param attributeMap
	 * @param input
	 * @return void
	 * @throws Exception
	 */
	@Override
	public void assertTag(Driver driver, Map<String, String> attributeMap, String strInputText) throws Exception {
		LoggerUtil.debug("Started assertTag ");

		WebElement wbElement = null;
		if (!attributeMap.containsKey(Constants.ATTRIBUTE_ID) && !attributeMap.containsKey(Constants.ATTRIBUTE_INPUT)) {
			throw new Exception("input / id attribute is not defined for Assert tag");
		}

		String refid = attributeMap.get(Constants.ATTRIBUTE_REFID);
		if (refid != null && !"".equals(refid)) {
			wbElement = webElementMap.get(refid);
			if (wbElement == null) {
				throw new Exception("Element not found by using refid attribute");
			}
//			Commenting the below code as there's no use of it  SELENIUM-1408
//			WebElement weTableRow = getIndentedTableRow(driver, attributeMap);
//			wbElement = findElement(driver, attributeMap, weTableRow);
		} else
			wbElement = findElement(driver, attributeMap, true);
		highLightElement(driver, attributeMap, wbElement);
		assertTag(driver, attributeMap, wbElement, strInputText);
		LoggerUtil.debug("Assertion: Assert condition is satisfied");

		LoggerUtil.debug("End of assertTag ");

	}

	/**
	 * This method will be used to validate given condition to confirm test cases
	 * execution.
	 * 
	 * @author SteepGraph Systems
	 * @param driver
	 * @param attributeMap
	 * @param input
	 * @return void
	 * @throws Exception
	 */

	@Override
	public void assertTag(Driver driver, Map<String, String> attributeMap, WebElement wbElement, String strInputText)
			throws Exception {

		LoggerUtil.debug("Started assertTag using web element");

		if (!attributeMap.containsKey(Constants.ATTRIBUTE_CRITERIA))
			throw new Exception("criteria attribute is missing for assert tag.");

		String criteria = attributeMap.get(Constants.ATTRIBUTE_CRITERIA);
		if (criteria == null || "".equals(criteria))
			throw new Exception("criteria attribute is missing for assert tag.");

		criteria = criteria.toLowerCase();

		String elementText = null;

		if (Constants.ATTRIBUTE_ATTRIBUTE.equalsIgnoreCase(criteria)) {
			String attributeName = attributeMap.get(Constants.ATTRIBUTE_ATTRIBUTE);
			if (attributeName == null || "".equals(attributeName)) {
				throw new Exception("attribute is missing for assert tag.");
			} else if (attributeName.equalsIgnoreCase("dropdown")) {

				Select se = new Select(wbElement);
				WebElement option = se.getFirstSelectedOption();
				elementText = option.getText();
			} else {
				elementText = wbElement.getAttribute(attributeName);
			}

		} else if (Constants.INPUT_VALUE.equalsIgnoreCase(criteria)) {
			elementText = attributeMap.get(Constants.ATTRIBUTE_INPUT);
			if (elementText == null || "".equals(elementText))
				throw new Exception("Input value is missing for assert tag.");

		} else if (Constants.ATTRIBUTE_NUMBER_OF_CHARACTERS.equalsIgnoreCase(criteria)) {
			String elText = wbElement.getText();
			elementText = String.valueOf(elText.length());

		} else {
			elementText = wbElement.getText();
		}
		LoggerUtil.debug("elementText " + elementText);

		String condition = attributeMap.get(Constants.ATTRIBUTE_CONDITION);
		if (condition == null || "".equals(condition))
			throw new Exception("condition attribute is missing for assert tag.");

		LoggerUtil.debug("condition " + condition);

		LoggerUtil.debug("strInputText " + strInputText);

		String errorMessage = attributeMap.get(Constants.ERROR_MESSAGE);
		if (errorMessage == null || "".equals(errorMessage))
			errorMessage = "The given assert condition is not satisifed";

		LoggerUtil.debug("criteria " + criteria);
		validateAssertCondition(condition, elementText, strInputText, errorMessage);
		LoggerUtil.debug("End of assertTag using web element");

	}

	/**
	 * Validate the assert condition
	 * 
	 * @author SteepGraph Systems
	 * @param condition
	 * @param input1
	 * @param input2
	 * @param errorMessage
	 * @throws Exception
	 */
	public void validateAssertCondition(String condition, String input1, String input2, String errorMessage)
			throws Exception {
		// AssertionCondition assertionCondition =
		// AssertionCondition.valueOf(condition);
		// switch (condition) {
		double dElementText = 0.0d;
		double dInput = 0.0d;
		// default:
		// case "contains":

		if ("contains".equalsIgnoreCase(condition) || "not contains".equalsIgnoreCase(condition)
				|| "=".equalsIgnoreCase(condition) || "!=".equalsIgnoreCase(condition)
				|| ">".equalsIgnoreCase(condition) || ">=".equalsIgnoreCase(condition)
				|| "<".equalsIgnoreCase(condition) || "<=".equalsIgnoreCase(condition)) {

			if ("contains".equalsIgnoreCase(condition)) {
				if (!input1.contains(input2)) {
					throw new AssertionException(errorMessage);
				}
			}
			// break;
			// case "not contains":
			else if ("not contains".equalsIgnoreCase(condition)) {
				if (input1.contains(input2)) {
					throw new AssertionException(errorMessage);
				}
			}
			// break;
			// case "=":
			else if ("=".equalsIgnoreCase(condition)) {
				if (!input1.trim().equals(input2.trim())) {
					throw new AssertionException(errorMessage);
				}
			}
			// break;

			// case "!=":
			else if ("!=".equalsIgnoreCase(condition)) {
				if (input1.trim().equals(input2.trim())) {
					throw new AssertionException(errorMessage);
				}
			}
			// break;

			// case ">":
			else if (">".equalsIgnoreCase(condition)) {
				dElementText = Double.parseDouble(input1.trim());
				dInput = Double.parseDouble(input2.trim());
				if (!(dElementText > dInput)) {
					throw new AssertionException(errorMessage);
				}
			}
			// break;

			// case "<":
			else if ("<".equalsIgnoreCase(condition)) {
				dElementText = Double.parseDouble(input1.trim());
				dInput = Double.parseDouble(input2.trim());
				if (!(dElementText < dInput)) {
					throw new AssertionException(errorMessage);
				}
			}
			// break;

			// case ">=":
			else if (">=".equalsIgnoreCase(condition)) {
				dElementText = Double.parseDouble(input1.trim());
				dInput = Double.parseDouble(input2.trim());
				if (!(dElementText >= dInput)) {
					throw new AssertionException(errorMessage);
				}
			}
			// break;

			// case "<=":
			else if ("<=".equalsIgnoreCase(condition)) {
				dElementText = Double.parseDouble(input1.trim());
				dInput = Double.parseDouble(input2.trim());
				if (!(dElementText <= dInput)) {
					throw new AssertionException(errorMessage);
				}
			}
			// break;
		} else {
			throw new AssertionException("condition does not exist in TAS.");
		}
	}

	/**
	 * Highlight Element on Web Page
	 * 
	 * @author Steepgraph Systems
	 * @param driver
	 * @param attributeMap
	 * @return void
	 * @throws Exception
	 */
	@Override
	public void highLightElement(Driver driver, Map<String, String> attributeMap) throws Exception {

		WebElement element = findElement(driver, attributeMap, true);
		String style = attributeMap.get(Constants.ATTRIBUTE_STYLE);
		highLightElement(driver, element, style);
	}

	/**
	 * Highlight Element on Web Page
	 * 
	 * @author SteepGraph Systems
	 * @param Driver
	 * @param WebElement
	 * @param style
	 * @return void
	 * @throws Exception
	 */
	@Override
	public void highLightElement(Driver driver, WebElement webElement, String style) throws Exception {

		if (style == null || "".equals(style))
			style = "border: 2px solid rgba(81, 203, 238, 1);";

		// style = "box-shadow: 0 0 10px rgba(81, 203, 238, 1);border: 2px solid
		// rgba(81, 203, 238, 1);padding: 2px;";

		JavascriptExecutor js = (JavascriptExecutor) driver.getWebDriver();
		js.executeScript("arguments[0].setAttribute('style', '" + style + "');", webElement);
	}

	/**
	 * Highlight Element on Web Page
	 * 
	 * @author SteepGraph Systems
	 * @param driver
	 * @param attributeMap
	 * @param WebElement
	 * @return void
	 * @throws Exception
	 */
	@Override
	public void highLightElement(Driver driver, Map<String, String> attributeMap, WebElement webElement)
			throws Exception {

		String highlight = attributeMap.get(Constants.ATTRIBUTE_HIGHLIGHT);

		if ("".equals(highlight)) {
			throw new Exception(
					"invalid Value of Highlight attribute , Please provide a possible value : true or false");
		}

		if (highlight == null) {
			highlight = propertyUtil.getProperty(Constants.PROPERTY_KEY_HIGHLIGHT_WEBELEMENT_ENABLE);
			if (highlight == null || "".equals(highlight)) {
				highlight = Constants.CHECK_FALSE;
			}
		}

		LoggerUtil.debug("highlight: " + highlight);

		if (Constants.CHECK_TRUE.equalsIgnoreCase(highlight)) {
			String style = attributeMap.get(Constants.ATTRIBUTE_STYLE);
			if (style == null || "".equals(style))
				style = propertyUtil.getProperty(Constants.PROPERTY_KEY_HIGHLIGHT_WEBELEMENT_STYLE);

			highLightElement(driver, webElement, style);
		}
	}

	/**
	 * This method will be used to validate input against enovia via mql
	 * 
	 * @author SteepGraph Systems
	 * @param driver
	 * @param attributeMap
	 * @param input
	 * @return void
	 * @throws Exception
	 */
	@Override
	public void mqlAssert(Driver driver, Map<String, String> attributeMap, String strInputText) throws Exception {

		LoggerUtil.debug("Start of mqlAssert ");

		String condition = attributeMap.get(Constants.ATTRIBUTE_CONDITION);
		if (condition == null || "".equals(condition))
			throw new Exception("condition attribute is missing on MqlAssert tag.");

		condition = condition.toLowerCase();

		LoggerUtil.debug("condition: " + condition);

		String errorMessage = attributeMap.get(Constants.ERROR_MESSAGE);
		if (errorMessage == null || "".equals(errorMessage))
			errorMessage = "The given mql assert condition is not satisifed";

		LoggerUtil.debug("strInputText: " + strInputText);

		if (strInputText == null || "".equals(strInputText)) {
			String criteria = attributeMap.get(Constants.ATTRIBUTE_CRITERIA);
			if (criteria == null || "".equals(criteria))
				throw new Exception("criteria attribute is missing for MqlAssert tag.");

			criteria = criteria.toLowerCase();

			LoggerUtil.debug("criteria: " + criteria);

			WebElement wbElement = findElement(driver, attributeMap, true);

			if (Constants.ATTRIBUTE_ATTRIBUTE.equalsIgnoreCase(criteria)) {
				String attributeName = attributeMap.get(Constants.ATTRIBUTE_ATTRIBUTE);
				if (attributeName == null || "".equals(attributeName))
					throw new Exception("'attribute' attribute is missing for MqlAssert tag.");

				strInputText = wbElement.getAttribute(attributeName);
			} else
				strInputText = wbElement.getText();
		}

		LoggerUtil.debug("Input to Validate: " + strInputText);

		StringList selectables = new StringList();

		boolean isConditionSatisfied = false;

		EnoviaUtil enoviaUtil = EnoviaUtil.newInstance(this.propertyUtil);

		AssertCondition assertCondition = AssertCondition.valueOf(condition);
		switch (assertCondition) {
		case validatetnr:
			String mql = enoviaUtil.getMQLfromTNR(strInputText);
			selectables.clear();
			selectables.add(Constants.ATTRIBUTE_NAME);
			mql = enoviaUtil.appendSelectablesToMql(mql, selectables);
			try {

				enoviaUtil.executeMQL(mql);
				isConditionSatisfied = true;
			} catch (Exception e) {
				LoggerUtil.debug("MQL Exception: " + e);
				isConditionSatisfied = false;
			}
			break;

		case validatetid:
			mql = enoviaUtil.getMQLfromId(strInputText);
			selectables.clear();
			selectables.add(Constants.ATTRIBUTE_NAME);
			mql = enoviaUtil.appendSelectablesToMql(mql, selectables);
			try {
				enoviaUtil.executeMQL(mql);
				isConditionSatisfied = true;
			} catch (Exception e) {
				LoggerUtil.debug("MQL Exception: " + e);
				isConditionSatisfied = false;
			}
			break;

		case validatetstatefromtnr:
			selectables.clear();
			selectables.add("current");
			mql = enoviaUtil.getMQLfromTNR(strInputText);
			mql = enoviaUtil.appendSelectablesToMql(mql, selectables);
			try {
				String currentState = enoviaUtil.executeMQL(mql);
				String stateToValidate = strInputText.split("\\|")[3];
				if (currentState.equals(stateToValidate))
					isConditionSatisfied = true;
			} catch (Exception e) {
				LoggerUtil.debug("MQL Exception: " + e);
				isConditionSatisfied = false;
			}
			break;

		case validatetstatefromid:
			selectables.clear();
			selectables.add("current");
			mql = enoviaUtil.getMQLfromId(strInputText);
			mql = enoviaUtil.appendSelectablesToMql(mql, selectables);
			try {
				String currentState = enoviaUtil.executeMQL(mql);
				String stateToValidate = strInputText.split("\\|")[3];
				if (currentState.equals(stateToValidate))
					isConditionSatisfied = true;
			} catch (Exception e) {
				LoggerUtil.debug("MQL Exception: " + e);
				isConditionSatisfied = false;
			}
			break;

		case validatetattributefromtnr:

			String selectExpression = attributeMap.get(Constants.ATTRIBUTE_SELECT_EXPRESSION);
			if (selectExpression == null || "".equals(selectExpression))
				throw new Exception("selectExpression attribute is missing on MqlAssert tag.");

			mql = enoviaUtil.getMQLfromTNR(strInputText);
			mql += " " + selectExpression;
			try {
				String attributeValue = enoviaUtil.executeMQL(mql);
				String attributeValuetoValidate = strInputText.split("\\|")[3];
				if (attributeValuetoValidate.equals(attributeValue))
					isConditionSatisfied = true;
			} catch (Exception e) {
				LoggerUtil.debug("MQL Exception: " + e);
				isConditionSatisfied = false;
			}
			break;

		case validatetattributefromid:

			selectExpression = attributeMap.get(Constants.ATTRIBUTE_SELECT_EXPRESSION);
			if (selectExpression == null || "".equals(selectExpression))
				throw new Exception("selectExpression attribute is missing on MqlAssert tag.");

			mql = enoviaUtil.getMQLfromId(strInputText);
			mql += " " + selectExpression;
			try {
				String attributeValue = enoviaUtil.executeMQL(mql);
				String attributeValuetoValidate = strInputText.split("\\|")[3];
				if (attributeValuetoValidate.equals(attributeValue))
					isConditionSatisfied = true;
			} catch (Exception e) {
				LoggerUtil.debug("MQL Exception: " + e);
				isConditionSatisfied = false;
			}
			break;

		case validatetdeletionfromid:

			mql = enoviaUtil.getMQLfromId(strInputText);
			mql += " select name";

			try {
				enoviaUtil.executeMQL(mql);
				isConditionSatisfied = false;
			} catch (Exception e) {
				LoggerUtil.debug("MQL Exception: " + e);
				isConditionSatisfied = true;
			}
			break;

		case validatetdeletionfromtnr:

			mql = enoviaUtil.getMQLfromTNR(strInputText);
			mql += " select id";

			try {
				enoviaUtil.executeMQL(mql);
				isConditionSatisfied = false;
			} catch (Exception e) {
				LoggerUtil.debug("MQL Exception: " + e);
				isConditionSatisfied = true;
			}
			break;

		default:
			throw new Exception("Invalid criteria in MqlAssert Tag.");
		}

		if (isConditionSatisfied == false)
			throw new AssertionException(errorMessage);

		LoggerUtil.debug("Assertion: Assert condition is satisfied");

		LoggerUtil.debug("End of mqlAssert ");

	}

	/**
	 * This method will be used to send keystroke to application
	 * 
	 * @author SteepGraph Systems
	 * @param driver
	 * @param attributeMap
	 * @throws Exception
	 */
	@Override
	public void sendKey(Driver driver, Map<String, String> attributeMap) throws Exception {

		LoggerUtil.debug("Start of sendKey.");
		WebElement wbElement = null;
		try {
			wbElement = findElement(driver, attributeMap, true);
		} catch (Exception e) {
			// Intentionally not handled
		}

		String strKey = attributeMap.get(Constants.ATTRIBUTE_KEY);
		if (strKey == null || "".equals(strKey))
			throw new Exception("key attribute is missing on SendKey tag.");

		String[] allKeys = strKey.split("\\+");
		LoggerUtil.debug("allKeys size:" + allKeys.length);

		List<CharSequence> keysCordList = new ArrayList<CharSequence>();
		for (int i = 0; i < allKeys.length; i++) {
			String tempKey = allKeys[i];
			LoggerUtil.debug("key : " + tempKey);

			// if not the valid key then send text as is to web element
			try {
				Keys key = Keys.valueOf(tempKey);
				keysCordList.add(key);
			} catch (Exception e) {
				LoggerUtil.debug("Key is not valid " + e);
				LoggerUtil.debug("so,  sending this " + tempKey + " Key as text to the web element.");
				keysCordList.add(tempKey);
			}
		}

		if (wbElement == null) {
			wbElement = driver.getWebDriver().switchTo().activeElement();
		}
		// Actions actions = new Actions(driver.getWebDriver());
		// actions.sendKeys(wbElement, Keys.chord(keysCordList)).build().perform();
		wbElement.sendKeys(Keys.chord(keysCordList));

		LoggerUtil.debug("End of sendKey.");
	}

	/**
	 * This method will be used to validate alter messages
	 * 
	 * @author SteepGraph Systems
	 * @param driver
	 * @param attributeMap
	 * @throws Exception
	 */
	@Override
	public void assertAlert(Driver driver, Map<String, String> attributeMap, String inputText) throws Exception {

		LoggerUtil.debug("Start of assertAlert.");

		String strAction = attributeMap.get(Constants.ATTRIBUTE_ACTION);
		if (strAction == null || "".equals(strAction)) {
			throw new Exception("Attribute action not specified for tag AssertAlert.");
		}

		String errorMessage = attributeMap.get(Constants.ERROR_MESSAGE);
		LoggerUtil.debug("strAction: " + strAction);
		Alert alert = getAlert(driver);
		LoggerUtil.debug("alert: " + alert);
		if ("dismiss".equals(strAction) || "accept".equals(strAction)) {
			if (inputText == null || "".equals(inputText))
				throw new Exception("assert input in not given in csv file");

			if (errorMessage == null || "".equals(errorMessage))
				errorMessage = "The given alert message does not match with current alert message.";

			String alertText = alert.getText();
			alertText = alertText.replaceAll("\\r|\\n", "");
			LoggerUtil.debug("alertText: " + alertText);
			alertText = alertText.trim();

			inputText = inputText.replaceAll("\\r|\\n", "");
			inputText = inputText.trim();

			if (!inputText.equals(alertText))
				throw new AssertionException(errorMessage);
			handleAlertAction(alert, strAction);
		} else {
			if (errorMessage == null || "".equals(errorMessage)) {
				if ("present".equals(strAction)) {
					errorMessage = "No Alert message was present";
				} else {
					errorMessage = "Alert message was present";
				}
			}

			boolean result = handleAlertAction(alert, strAction);
			if (!result) {
				throw new AssertionException(errorMessage);
			}
		}
		LoggerUtil.debug("Assertion: Assert condition is satisfied");
		LoggerUtil.debug("End of assertAlert.");
	}

	/**
	 * This method will be used to validate deletion of object
	 * 
	 * @author SteepGraph Systems
	 * @param driver
	 * @param attributeMap
	 * @throws Exception
	 */
	@Override
	public void assertDeletion(Driver driver, Map<String, String> attributeMap) throws Exception {

		LoggerUtil.debug("Start of assertDeletion");

		String strlocatorType = attributeMap.get(Constants.LOCATOR_TYPE);
		String strlocatorExpression = attributeMap.get(Constants.LOCATOR_EXPRESSION);
		WebElement wbElement = null;
		if (strlocatorExpression == null || "".equals(strlocatorExpression)) {
			throw new Exception("locatorExpression attribute is not null or empty for assertDeletion tag");
		}
		LoggerUtil.debug("strlocatorExpression: " + strlocatorExpression);
		LoggerUtil.debug("strlocatorType: " + strlocatorType);

		try {
			wbElement = driver.findElement(strlocatorType, strlocatorExpression);
		} catch (Exception e) {
			// Web Element is not found due to deletion
			LoggerUtil.debug("Assertion: Assert condition is satisfied");
			return;
		}

		String errorMessage = attributeMap.get(Constants.ERROR_MESSAGE);
		if (errorMessage == null || "".equals(errorMessage))
			errorMessage = "The given assert condition is not satisifed";

		if (wbElement != null && wbElement.isDisplayed()) {
			throw new AssertionException(errorMessage);
		}

		LoggerUtil.debug("Assertion: Assert condition is satisfied");
		LoggerUtil.debug("End of assertDeletion");
	}

	/**
	 * Method to switch to the parent frame
	 * 
	 * @param driver
	 * @param attributeMap
	 */
	@Override
	public void switchToParentFrame(Driver driver, Map<String, String> attributeMap) throws Exception {
		LoggerUtil.debug("Start of switchToParentFrame.");

		int frameStackSize = frameStack.size();
		if (frameStackSize < 2) {
			LoggerUtil.debug("Warning: There is no parent frame available in stack, control remains in same frame.");
			return;
		}

		@SuppressWarnings("unchecked")
		Stack<Object> frameStackTmp = (Stack<Object>) frameStack.clone();
		switchToDefaultContent(driver);
		LoggerUtil.debug("frameStackSize: " + frameStackSize);

		for (int i = 0; i < (frameStackSize - 1); i++) {
			Object frameLocator = frameStackTmp.get(i);

			if (frameLocator instanceof String) {
				String frameName = (String) frameLocator;
				LoggerUtil.debug("frameName: " + frameName);
				driver.switchToFrame(frameName);
			} else if (frameLocator instanceof Integer) {
				Integer frameIndex = (Integer) frameLocator;
				LoggerUtil.debug("frameIndex: " + frameIndex);
				driver.switchToFrame(frameIndex);
			} else if (frameLocator instanceof WebElement) {
				WebElement frameElement = (WebElement) frameLocator;
				LoggerUtil.debug("Frame Web Element: " + frameElement.getTagName());
				driver.switchToFrame(frameElement);
			}
		}

		frameStackTmp.pop();
		frameStack.addAll(frameStackTmp);

		LoggerUtil.debug("End of switchToParentFrame.");
	}

	/**
	 * Method to assert values of list box
	 * 
	 * @param driver
	 * @param attributeMap
	 */
	@Override
	public void assertListValues(Driver driver, Map<String, String> attributeMap, String input) throws Exception {

		LoggerUtil.debug("Start of assertListValues");

		if (!attributeMap.containsKey(Constants.ATTRIBUTE_ID) && !attributeMap.containsKey(Constants.ATTRIBUTE_INPUT))
			throw new IllegalArgumentException("id /input attribute is not defined properly for assertListValues tag");

		if (input == null || "".equals(input))
			throw new Exception("input is not given in csv file for assertListValues tag.");

		List<String> inputValuesFromCSV = Arrays.asList(input.split("\\|"));

		LoggerUtil.debug("iinputValuesFromCSV size: " + inputValuesFromCSV.size());

		WebElement wbListBox = findElement(driver, attributeMap, false);
		highLightElement(driver, attributeMap, wbListBox);
		String strCriteria = attributeMap.get(Constants.ATTRIBUTE_CRITERIA);
		if (strCriteria == null || "".equals(strCriteria))
			throw new Exception("criteria attribute is not define for assertListValues tag");

		LoggerUtil.debug("strCriteria: " + strCriteria);

		strCriteria = strCriteria.toLowerCase();

		String errorMessage = attributeMap.get(Constants.ERROR_MESSAGE);
		if (errorMessage == null || "".equals(errorMessage))
			errorMessage = "The given assert condition is not satisifed";

		Select selectObj = new Select(wbListBox);

		List<WebElement> listBoxOptions = selectObj.getOptions();

		LoggerUtil.debug("listBoxOptions size: " + listBoxOptions.size());

		List<String> valuesFromListBox = new ArrayList<String>();

		for (WebElement listBoxOption : listBoxOptions) {
			valuesFromListBox.add(listBoxOption.getText());
		}
		AssertCriteria criteria = AssertCriteria.valueOf(strCriteria.replace(" ", "_"));
		switch (criteria) {
		case contains:
			for (String csvInputValue : inputValuesFromCSV) {
				if (!valuesFromListBox.contains(csvInputValue))
					throw new AssertionException(errorMessage);
			}
			break;

		case equal:
			if (!commonUtilobj.equals(inputValuesFromCSV, valuesFromListBox))
				throw new AssertionException(errorMessage);

			break;

		case not_contains:
			for (String csvInputValue : inputValuesFromCSV) {
				if (valuesFromListBox.contains(csvInputValue))
					throw new AssertionException(errorMessage);
			}
			break;

		case not_equal:
			if (commonUtilobj.equals(inputValuesFromCSV, valuesFromListBox))
				throw new AssertionException(errorMessage);

			break;

		default:
			throw new Exception("Not a validate Assertion criteria: " + strCriteria);
		}

		LoggerUtil.debug("Assert Criteria satisfied");
		LoggerUtil.debug("End of assertListValues");
	}

	/**
	 * Method to open given URL
	 * 
	 * @param driver
	 * @param attributeMap
	 * @param strInputText
	 */

	@Override
	public void openURL(Driver driver, Map<String, String> attributeMap, String url) throws Exception {
		LoggerUtil.debug("Start of openURL");

		if ((null != url && !url.isEmpty())
				&& (null != attributeMap.get(Constants.URL) && !attributeMap.get(Constants.URL).isEmpty())) {
			throw new IllegalArgumentException(
					"any one of id or url is mandatory, can not put both of them. Please put any one of those value for OpenUrl TAG");
		}

		if (url == null || "".equals(url))
			url = attributeMap.get(Constants.URL);

		if (url == null || "".equals(url))
			throw new IllegalArgumentException(
					"URL input is missing. Please given it either in csv or in url attribute or mention in properties file.");

		String target = attributeMap.get(Constants.ATTRIBUTE_TARGET);

		if (target == null || "".equalsIgnoreCase(target)) {
			target = "self";
		}

		switch (target) {
		case "tab":
			((JavascriptExecutor) driver.getWebDriver()).executeScript("window.open('" + url + "', '_blank');");
			switchToLastWindow(driver);
			Thread.sleep(1000);
			break;

		case "window":
			((JavascriptExecutor) driver.getWebDriver())
					.executeScript("window.open('" + url + "', '_blank', 'toolbar=yes,scrollbars=yes,resizable=yes');");
			switchToLastWindow(driver);
			Thread.sleep(1000);
			driver.getWebDriver().manage().window().maximize();
			break;

		default:
			driver.getWebDriver().navigate().to(url);
			break;
		}

		LoggerUtil.debug("End of openURL");
	}

	/**
	 * Method to call web service
	 * 
	 * @param driver
	 * @param attributeMap
	 * @param strInputText
	 */
	@Override
	public void callWebService(Driver driver, Map<String, String> attributeMap, String strInputText) throws Exception {

		LoggerUtil.debug("Started callWebService");

		Object response = null;
		IWebServiceUtil webService = null;

		String webServiceType = attributeMap.get(Constants.ATTRIBUTE_TYPE);
		if (webServiceType == null || "".equals(webServiceType))
			throw new Exception("type attribute is not define for CallWebService tag");

		LoggerUtil.debug("webServiceType: " + webServiceType);

		if ("REST".equalsIgnoreCase(webServiceType))
			webService = new RestWebServiceUtil(this.propertyUtil);
		else if ("SOAP".equalsIgnoreCase(webServiceType))
			webService = new SoapWebServiceUtil(this.propertyUtil);

		String refId = attributeMap.get(Constants.ATTRIBUTE_REFID);

		if (refId == null || "".equals(refId)) {

			String id = attributeMap.get(Constants.ATTRIBUTE_ID);
			if (id == null || "".equals(id))
				throw new AssertionException("id attribute is not define for CallWebService tag");

			String url = attributeMap.get(Constants.URL);
			if (url == null || "".equals(url))
				throw new AssertionException("url attribute is not define for CallWebService tag");

			// call webservice
			response = webService.call(attributeMap, strInputText);
			webServiceResponseMap.put(id, response);
		} else {
			LoggerUtil.debug("refId: " + refId);
			response = webServiceResponseMap.get(refId);
			if (response == null)
				throw new Exception("Refid= " + refId + " is not valid. Please check CallWebService Tag.");
		}

		if (response == null)
			throw new Exception("Null Response. Unable to check this request");

		String strMethod = attributeMap.get("method");
		if (StringUtils.isBlank(strMethod)) {
			strMethod = "get";
		}
		String criteria = attributeMap.get(Constants.ATTRIBUTE_CRITERIA);
		if (!"delete".equalsIgnoreCase(strMethod)) {
			if (criteria == null || "".equals(criteria))
				throw new Exception("criteria attribute is not define for CallWebService tag");
		}

		LoggerUtil.debug("criteria: " + criteria);

		String key = attributeMap.get(Constants.ATTRIBUTE_KEY);
		if (key == null || "".equals(key))
			throw new Exception("key attribute is not define for CallWebService tag");

		LoggerUtil.debug("key: " + key);

		String compareWith = attributeMap.get(Constants.COMPAREWITH);
		if (!"delete".equalsIgnoreCase(strMethod)) {
			if (compareWith == null || "".equals(compareWith))
				throw new Exception("comparewith attribute is not define for CallWebService tag");
		}

		LoggerUtil.debug("compareWith: " + compareWith);

		String errorMessage = attributeMap.get(Constants.ERROR_MESSAGE);
		if (errorMessage == null || "".equals(errorMessage))
			errorMessage = "The given assert condition is not satisifed";

		String valueOfKey = webService.readResponse(response, key);
		LoggerUtil.debug("valueOfKey: " + valueOfKey);

		AssertCriteria asserCriteria = AssertCriteria.valueOf(criteria.replace(" ", "_"));
		if (!strMethod.equalsIgnoreCase("delete")) {
			switch (asserCriteria) {
			case contains:
				if (!valueOfKey.contains(compareWith))
					throw new AssertionException(errorMessage);
				break;

			case not_contains:
				if (valueOfKey.contains(compareWith))
					throw new AssertionException(errorMessage);
				break;

			case equal:
				if (!compareWith.equals(valueOfKey))
					throw new AssertionException(errorMessage);
				break;

			case not_equal:
				if (compareWith.equals(valueOfKey))
					throw new AssertionException(errorMessage);
				break;

			case greater_than:
				Double iCompareWith = Double.parseDouble(compareWith);
				Double ivalueOfKey = Double.parseDouble(valueOfKey);
				if (iCompareWith <= ivalueOfKey)
					throw new AssertionException(errorMessage);
				break;

			case less_than:
				iCompareWith = Double.parseDouble(compareWith);
				ivalueOfKey = Double.parseDouble(valueOfKey);
				if (iCompareWith >= ivalueOfKey)
					throw new AssertionException(errorMessage);
				break;

			default:
				throw new Exception("Not a validate Assertion criteria: " + criteria);
			}
		}

		LoggerUtil.debug("AssertCondition is satisfied");
		LoggerUtil.debug("End of callWebService");
	}

	/**
	 * Method to call external java method
	 * 
	 * @param driver
	 * @param attributeMap
	 * @param strInputText
	 */
	@Override
	public void execute(Driver driver, Map<String, String> attributeMap, String strInputText) throws Exception {

		String className = attributeMap.get(Constants.ATTRIBUTE_CLASS);
		if (className == null || "".equals(className))
			throw new Exception("className attribute is not define for CallWebService tag");

		String methodName = attributeMap.get(Constants.METHOD_NAME);
		if (methodName == null || "".equals(methodName))
			throw new Exception("method attribute is not define for CallWebService tag");

		Class<?> classObj = Class.forName(className);
		Object obj = classObj.newInstance();

		Class<?> noparams[] = { Driver.class, ILibrary.class, Map.class, String.class };
		Object[] inputArray = { driver, this, attributeMap, strInputText };
		Method method = classObj.getDeclaredMethod(methodName, noparams);
		method.invoke(obj, inputArray);

	}

	/**
	 * Method to connect to database
	 * 
	 * @param driver
	 * @param attributeMap
	 * @param strInputText
	 */

	@Override
	public void openDBConnection(Driver driver, Map<String, String> attributeMap, String strInputText,
			PropertyUtil propertyUtil) throws Exception {

		LoggerUtil.debug("Start of openDBConnection");

		String id = attributeMap.get(Constants.ATTRIBUTE_ID);
		if (id == null || "".equals(id))
			throw new Exception("id attribute is not define for OpenDBConnection tag");

		LoggerUtil.debug("id: " + id);

		String url = attributeMap.get(Constants.URL);
		if (url == null || "".equals(url))
			throw new Exception("url attribute is not define for OpenDBConnection tag");

		LoggerUtil.debug("url: " + url);

		String type = attributeMap.get(Constants.ATTRIBUTE_TYPE);
		if (type == null || "".equals(type))
			throw new Exception("type attribute is not define for OpenDBConnection tag");

		LoggerUtil.debug("type: " + type);

		String username = attributeMap.get(Constants.INPUT_USERNAME);
		if (username == null || "".equals(username))
			throw new Exception("username attribute is not define for OpenDBConnection tag");

		LoggerUtil.debug("username: " + username);

		String password = attributeMap.get(Constants.INPUT_PASSWORD);
		if (type == null || "".equals(type))
			throw new Exception("password attribute is not define for OpenDBConnection tag");

		LoggerUtil.debug("password: " + password);

		type = type.toLowerCase();

		IDatabase database = null;
		if ("oracle".equals(type)) {
			database = new OracleDatabase();
		}

		if (database == null)
			throw new Exception("type= " + type + " attribute value is not valid for  OpenDBConnection tag");

		Connection conn = database.connect(url, username, password, propertyUtil);

		dbConnectionMap.put(id, conn);

		LoggerUtil.debug("End of openDBConnection");
	}

	/**
	 * Method to check given entries are present in database
	 * 
	 * @param driver
	 * @param attributeMap
	 * @param strInputText
	 */

	@Override
	public void assertDB(Driver driver, Map<String, String> attributeMap, String strInputText) throws Exception {

		LoggerUtil.debug("Start of assertDB");

		String refid = attributeMap.get(Constants.ATTRIBUTE_REFID);
		if (refid == null || "".equals(refid))
			throw new Exception("refid attribute is not define for AssertDB tag");

		LoggerUtil.debug("refid: " + refid);

		if (strInputText == null || "".equals(strInputText))
			throw new Exception("json file path is not defined in csv file.");

		LoggerUtil.debug("json file: " + strInputText);

		String strCount = attributeMap.get(Constants.COUNT);
		LoggerUtil.debug("strCount: " + strCount);

		IDatabase database = new OracleDatabase();
		Connection conn = dbConnectionMap.get(refid);

		if (conn == null)
			throw new Exception("Database connection is not established or refid is not valid");

		long resultCount = database.read(conn, strInputText);

		LoggerUtil.debug("resultCount: " + resultCount);

		if ((strCount == null || strCount.isEmpty()) && resultCount < 1)
			throw new Exception("Result Count is empty or zero");
		else if (resultCount != Long.parseLong(strCount))
			throw new Exception();

	}

	/**
	 * Method to handle an close all other window except current one.
	 * 
	 * @author Steepgraph Systems
	 * @param driver
	 * @return void
	 * @throws Exception
	 */
	@Override
	public void closeAllOtherWindow(Driver driver, Map<String, String> attributeMap) throws Exception {
		LoggerUtil.debug("Start of closeAllOtherWindow.");
		// Thread.sleep(500);

		String dismissAlert = attributeMap.get(Constants.ATTRIBUTE_DISMISS_ALERT);
		boolean bDismissAlert = false;
		if (dismissAlert != null || "true".equalsIgnoreCase(dismissAlert)) {
			bDismissAlert = true;
		}
		String homeWindow = driver.getWebDriver().getWindowHandle();
		Set<String> allWindows = driver.getWebDriver().getWindowHandles();
		LoggerUtil.debug("All windows size: " + allWindows.size());
		for (String childWindow : allWindows) {
			if (!homeWindow.equals(childWindow)) {
				try {
					driver.getWebDriver().switchTo().window(childWindow);
					if (bDismissAlert) {
						dismissAlert(driver);
					}
					driver.getWebDriver().close();
					Thread.sleep(500);
				} catch (NoSuchWindowException e) {
					LoggerUtil.debug("close withdow failed:", e);
				}
			}
		}
		LoggerUtil.debug("All other windows are closed");
		driver.getWebDriver().switchTo().window(homeWindow);
		if (bDismissAlert) {
			dismissAlert(driver);
		}
		Thread.sleep(500);
		clearParentWindowStack();
		LoggerUtil.debug("End of closeAllOtherWindow.");
	}

	@Override
	public void assertFileExists(Driver driver, Map<String, String> attributeMap, String strInputText)
			throws Exception {

		LoggerUtil.debug("Start of assertFileExists");
		if (!attributeMap.containsKey(Constants.ATTRIBUTE_ID) && !attributeMap.containsKey(Constants.ATTRIBUTE_INPUT))
			throw new IllegalArgumentException("id /input attribute is not defined properly for assertFileExists tag");

		if (strInputText == null || "".equals(strInputText))
			throw new Exception("filepath is not given in csv file.");

		LoggerUtil.debug("File path: " + strInputText);

		String strWait = attributeMap.get(Constants.ATTRIBUTE_WAIT);
		strWait = getTimeOut(strWait);
		int iWait = Integer.parseInt(strWait);
		PropertyUtil propertyutil = PropertyUtil.newInstance();
		String strPollingInterval = propertyutil
				.getProperty(Constants.PROPERTY_KEY_EXECUTION_STEP_TIMEOUT_POLLINGINTERVAL);
		int iPollingInterval = Integer.parseInt(strPollingInterval);
		LoggerUtil.debug("timeout : " + iWait);

		int iRetryCount = getRetryCount(iWait, iPollingInterval);
		LoggerUtil.debug("polling time : " + iRetryCount);

		FluentWait<WebDriver> w = new FluentWait<WebDriver>(driver.getWebDriver()).withTimeout(Duration.ofMillis(iWait))
				.pollingEvery(Duration.ofMillis(iRetryCount)).ignoring(NoSuchElementException.class);

		File file = new File(strInputText);

		try {
			w.until(driverInstance -> file.exists());
			LoggerUtil.debug("File downloaded");
		} catch (Exception e) {
			throw new AssertionException("File is not exists at given path " + strInputText);
		}

		LoggerUtil.debug("AssertCondition is satisfied");

		LoggerUtil.debug("End of assertFileExists");

	}

	@Override
	public void clickGlobalToolsMenu(Driver driver, Map<String, String> attributeMap) throws Exception {
		throw new Exception("clickGlobalToolsMenu tag is not support for current Enovia Version");
	}

	/**
	 * This method will be used assert indented table cell using refid
	 * 
	 * @author Steepgraph Systems
	 * @param driver
	 * @param attributeMap: position
	 * @return void
	 * @throws Exception
	 */
	@Override
	public void assertIndentedTableRow(Driver driver, Map<String, String> attributeMap, String strInput)
			throws Exception {

		LoggerUtil.debug("Start of assertIndentedTableRow ");
		WebElement weTableRowColumn = getIndentedTableCell(driver, attributeMap);
		LoggerUtil.debug("weTableRowColumn: " + weTableRowColumn);
		scrollToElement(driver, weTableRowColumn);
		highLightElement(driver, attributeMap, weTableRowColumn);
		assertTag(driver, attributeMap, weTableRowColumn, strInput);
		LoggerUtil.debug("Assertion: Assert condition is satisfied");
		LoggerUtil.debug("End of assertIndentedTableRow ");

	}

	/**
	 * This method will be used run mql
	 * 
	 * @author Steepgraph Systems
	 * @param driver
	 * @param attributeMap: position
	 * @return void
	 * @throws Exception
	 */
	@Override
	public void runMql(Driver driver, Map<String, String> attributeMap, String strInput) throws Exception {

		LoggerUtil.debug("Start of runMql ");
		if (strInput == null || "".equals(strInput))
			throw new Exception("mql is given in csv file.");

		String condition = attributeMap.get(Constants.ATTRIBUTE_CONDITION);
		if (condition == null || "".equals(condition))
			condition = "none";

		String expectedResult = attributeMap.get(Constants.ATTRIBUTE_RESULT);

		LoggerUtil.debug("expectedResult: " + expectedResult);

		String errorMessage = attributeMap.get(Constants.ERROR_MESSAGE);
		if (errorMessage == null || "".equals(errorMessage))
			errorMessage = "Asserion condition is not satisfed for tag RunMQL";

		EnoviaUtil enoviaUtil = EnoviaUtil.newInstance(this.propertyUtil);
		String executionResult = enoviaUtil.executeMQL(strInput);

		LoggerUtil.debug("execution result: " + executionResult);

		if (!"none".equalsIgnoreCase(condition))
			validateAssertCondition(condition, executionResult, expectedResult, errorMessage);

		LoggerUtil.debug("End of runMql ");

	}

	/**
	 * This method will be used to browser cookies
	 * 
	 * @author Steepgraph Systems
	 * @param driver
	 * @param attributeMap: position
	 * @return void
	 * @throws Exception
	 */
	@Override
	public void deleteAllCookies(Driver driver, Map<String, String> attributeMap) throws Exception {
		LoggerUtil.debug("Start of deleteAllCookies ");
		driver.getWebDriver().manage().deleteAllCookies();
		LoggerUtil.debug("End of deleteAllCookies ");

	}

	/**
	 * This method click on Home Menu
	 * 
	 * @author SteepGraph Systems
	 * @param driver
	 * @param attributeMap: commandLabel
	 * @return void
	 * @throws Exception
	 */
	@Override
	public void clickHomeMenu(Driver driver, Map<String, String> attributeMap) throws Exception {
		throw new Exception("No implementation found for this tag");
	}

	/**
	 * Method to select regision for click
	 * 
	 * @author Steepgraph Systems
	 * @param driver
	 * @param attributeMap
	 * @return void
	 * @throws Exception
	 */
	@Override
	public void selectWindowRegion(Driver driver, Map<String, String> attributeMap) throws Exception {
		LoggerUtil.debug("Start of selectWindowRegion.");

		String skiperror = null;
		int iWait;

		try {
			skiperror = attributeMap.get(Constants.ATTRIBUTE_SKIP_ERROR);
			if (skiperror == null || "".equals(skiperror))
				skiperror = Constants.CHECK_TRUE;

			LoggerUtil.debug("skiperror : " + skiperror);

			String id = attributeMap.get(Constants.ATTRIBUTE_ID);
			if (id == null || "".equals(id))
				throw new Exception("id attribute is not defined for selectWindowRegion tag");

			LoggerUtil.debug("id : " + id);

			if (attributeMap.get(Constants.IMAGE_PATH) == null || "".equals(attributeMap.get(Constants.IMAGE_PATH))) {
				throw new Exception("imagepath attribute is not defined for selectWindowRegion tag");
			}

			String wait = attributeMap.get(Constants.ATTRIBUTE_WAIT);
			wait = getTimeOut(wait);
			LoggerUtil.debug("Wait : " + wait);
			iWait = Integer.parseInt(wait) / 1000;

			String imagepath = attributeMap.get(Constants.IMAGE_PATH);
			File file = new File(imagepath);
			if (!file.exists())
				throw new Exception(imagepath + " file doesn't exist");

			String absoulteImagePath = file.getAbsolutePath();
			LoggerUtil.debug("absoulteImagePath : " + absoulteImagePath);
			Screen screen = new Screen();
			org.sikuli.script.Pattern regionPattern = new org.sikuli.script.Pattern(absoulteImagePath);

			int attempt = 0;
			while (attempt < 5) {
				LoggerUtil.debug("attempt: " + attempt);
				try {
					screen.wait(regionPattern, iWait);
					Match match = screen.find(regionPattern);
					Rectangle rect = match.getRect();
					System.out.println("Location: " + rect);
					Region imageRegion = new Region(rect);
					imageRegionMap.put(id, imageRegion);
					attempt = 5;
				} catch (Exception e) {
					attempt++;
					if (attempt >= 5)
						throw e;
				}
			}

			LoggerUtil.debug("End of selectWindowRegion.");
		} catch (

		Exception e) {
			if (Constants.CHECK_TRUE.equalsIgnoreCase(skiperror))
				LoggerUtil.debug("selectWindowRegion error skipped : Exception " + e);
			else
				throw e;
		}
	}

	/**
	 * Method to switch window that is launched after log in
	 * 
	 * @author Steepgraph Systems
	 * @param driver
	 * @param attributeMap
	 * @return void
	 * @throws Exception
	 */
	@Override
	public void switchToStartWindow(Driver driver, Map<String, String> attributeMap) throws Exception {
		LoggerUtil.debug("Start of switchToStartWindow.");
		driver.waitForJavaScriptToLoad();
		LoggerUtil.debug("homeWindowHandler: " + homeWindowHandler);
		driver.getWebDriver().switchTo().window(homeWindowHandler);
		LoggerUtil.debug("End of switchToStartWindow.");
	}

	/**
	 * Method to set start window that would be used to later for switching
	 * 
	 * @author Steepgraph Systems
	 * @param driver
	 * @param attributeMap
	 * @return void
	 * @throws Exception
	 */
	@Override
	public void setStartWindow(Driver driver, Map<String, String> attributeMap) throws Exception {
		LoggerUtil.debug("Start of setStartWindow.");
		driver.waitForJavaScriptToLoad();
		LoggerUtil.debug("old homeWindowHandler: " + homeWindowHandler);
		String previoushomeWindowHandler = homeWindowHandler;
		homeWindowHandler = driver.getWebDriver().getWindowHandle();
		driver.getWebDriver().switchTo().window(homeWindowHandler);
		LoggerUtil.debug("new homeWindowHandler: " + homeWindowHandler);
		if (previoushomeWindowHandler.equalsIgnoreCase(homeWindowHandler)) {
			LoggerUtil.debug("old homeWindowHandler :" + previoushomeWindowHandler + " and new homeWindowHandler :"
					+ homeWindowHandler + " is same.");
		}
		LoggerUtil.debug("End of setStartWindow.");
	}

	@Override
	public void openCompassApp(Driver driver, Map<String, String> attributeMap) throws Exception {
		LoggerUtil.debug("Start of openCompassApp.");

		switchToDefaultContent(driver);
		String quadrantvalue = attributeMap.get(Constants.QUADRANT);
		String strAppName = attributeMap.get(Constants.APPNAME);
		String strAppIndex = attributeMap.get(Constants.APPINDEX);

		if (quadrantvalue != null) {
			quadrantvalue = quadrantvalue.toLowerCase();
		} else {
			throw new Exception("Attribute quadrant not specified.");
		}

		if (strAppName == null || "".equals(strAppName.trim())) {
			throw new Exception("Attribute AppName not specified.");
		}

		String strCheckCompassOpen = driver
				.findElement(By.xpath("//div[@id='compass_ctn']/div[contains(@class,'compass-ct')]"))
				.getAttribute("class");

		if (!strCheckCompassOpen.contains("open")) {

			// Locate the apps panel to set required quadrant
			WebElement appsPanel = driver.findElement(By.xpath(
					"//div[@class='column-ctn']/div[@class='column-container']/div[contains(@class,'my-apps-panel')]"));

			// set required quadrant in apps panel
			JavascriptExecutor executor = (JavascriptExecutor) driver.getWebDriver();
			executor.executeScript(
					"arguments[0].setAttribute('class','my-apps-panel items-per-line-5 " + quadrantvalue + "');",
					appsPanel);

			// Open Quadrant
			attributeMap.put(Constants.LOCATOR_TYPE, Constants.INPUTTYPE_XPATH);
			attributeMap.put(Constants.LOCATOR_EXPRESSION, "//div[contains(@class,'compass-small-over')]");
			clickElement(driver, attributeMap);

			// Highlight the required quadrant
			WebElement wbQuadrantOn = driver.findElement(
					By.xpath("//div[contains(@class,'compass-base')]/div[contains(@class, 'compass-on')]"));
			executor.executeScript("arguments[0].setAttribute('class','compass-on " + quadrantvalue + "');",
					wbQuadrantOn);

			attributeMap.put(Constants.ATTRIBUTE_TIME, "1000");
			wait(driver, attributeMap);

			// Highlight the required quadrant
			attributeMap.put(Constants.LOCATOR_TYPE, Constants.INPUTTYPE_XPATH);
			attributeMap.put(Constants.LOCATOR_EXPRESSION,
					"//div[@class='compass-search-button fonticon fonticon-search']");
			clickElement(driver, attributeMap);
		}

		// <InputText locatorType="xpath"
		// locatorExpression="//input[@placeholder='Search Apps']" />
		attributeMap.put(Constants.ATTRIBUTE_MODE, "selenium");
		attributeMap.put(Constants.LOCATOR_TYPE, Constants.INPUTTYPE_XPATH);
		attributeMap.put(Constants.LOCATOR_EXPRESSION,
				"//div[contains(@class,'compass-search')]//input[contains(@class,'compass-search-text')]");
		inputText(driver, attributeMap, strAppName);
		attributeMap.put(Constants.ATTRIBUTE_TIME, "1000");
		wait(driver, attributeMap);

		attributeMap.put(Constants.ATTRIBUTE_MODE, "js");
		attributeMap.put(Constants.LOCATOR_TYPE, Constants.INPUTTYPE_XPATH);

		if (strAppIndex == null || strAppIndex.equalsIgnoreCase("")) {
			strAppIndex = "1";
		}
		attributeMap.put(Constants.LOCATOR_EXPRESSION,
				"//div[@class='compass-scroll-accordion-section my-apps-section']/div[@class='compass-scroll-accordion-section-content-container']//ul[@class='experience-list "
						+ quadrantvalue + "']/li[" + strAppIndex + "]/img");
		clickElement(driver, attributeMap);

		LoggerUtil.debug("End of openCompassApp.");

	}

	@Override
	public void selectOropenNewIndentedTableRow(Driver driver, Map<String, String> attributeMap, String strInputText)
			throws Exception {
		LoggerUtil.debug("Start of selectOropenNewIndentTableRow.");
		String strCriteria = attributeMap.get(Constants.ATTRIBUTE_CRITERIA);
		if (strCriteria == null || "".equals(strCriteria))
			throw new Exception("criteria are not defined for selectIndentedTableRow tag.");

		String searchByIdentifier = attributeMap.get(Constants.ATTRIBUTE_SEARCH_BY_IDENTIFIER);
		WebElement newWebElement = null;
		if (searchByIdentifier == null || "".equalsIgnoreCase(searchByIdentifier)) {
			searchByIdentifier = Constants.CHECK_FALSE;
		}
		if (searchByIdentifier.equalsIgnoreCase(Constants.CHECK_TRUE)) {
			LoggerUtil.debug("strInputText : " + strInputText);
			WebElement wbFindBtn = driver.findElement(By.xpath("//td[@title='Find...']/img"));
			driver.click(wbFindBtn);

			WebElement wbSearch = driver.findElement(By.id("findInStr"));
			Thread.sleep(2000);
			wbSearch.sendKeys(strInputText);

			WebElement wbNext = driver.findElement(By.id("findInNext"));
			driver.click(wbNext);
			Thread.sleep(2000);
			driver.click(wbFindBtn);

			newWebElement = driver.findElement(
					By.xpath("//div[@id='mx_divTreeBody']//a[contains(text(),'" + strInputText + "')]/.."));

			Actions actions = new Actions(driver.getWebDriver());
			actions.moveToElement(newWebElement).build().perform();

			Thread.sleep(1000);

		} else {
			List<WebElement> afterRows = driver.findElements(By.xpath(
					"//form[@name='emxTableForm']//div[@id='mx_divBody']//div[@id='mx_divTreeBody']//table//tbody//tr//td[2]//div//table//tbody//tr/td[@rmbid]"));
			LoggerUtil.debug("Size " + afterRows.size() + "  " + indententTableContent.size());

			for (WebElement after : afterRows) {
				if (!indententTableContent.contains(after.getAttribute("rmbid"))) {
					newWebElement = after;
					break;
				}
			}
		}

		LoggerUtil.debug("WebElement " + newWebElement);
		IndentedTableCriteria itCriteria = IndentedTableCriteria.valueOf(strCriteria.toLowerCase());
		switch (itCriteria) {
		case select:
			WebElement weRowCheckBox = driver.findElement(By.xpath("//table[@id='treeBodyTable']//tr/td[@rmbid='"
					+ newWebElement.getAttribute("rmbid") + "']/../td//input[@type='checkbox']"));
			driver.click(weRowCheckBox, "js", "false");
			// driver.click(weRowCheckBox);
			break;
		case open:
			int exeCnt = 0;
			while (exeCnt < 3) {
				try {
					driver.waitUntil(ExpectedConditions.visibilityOf(newWebElement));
					driver.click(newWebElement);
					break;
				} catch (StaleElementReferenceException e) {
					// intentionally not handled.
				}
				exeCnt++;
			}
			break;
		default:
			LoggerUtil.error(itCriteria.toString() + " not implemented.",
					new Exception(itCriteria.toString() + " not implemented."));
			break;
		}

		LoggerUtil.debug("End of selectOropenNewIndentTableRow.");
	}

	@Override
	public void registerIndentedTableObjects(Driver driver, Map<String, String> attributeMap) throws Exception {
		LoggerUtil.debug("Start of registerIndentTableObjects.");

		List<WebElement> tableObjectList = driver.findElements(By.xpath(
				"//form[@name='emxTableForm']//div[@id='mx_divBody']//div[@id='mx_divTreeBody']//table//tbody//tr//td[2]//div//table//tbody//tr/td[@rmbid]"));
		for (WebElement objectID : tableObjectList) {
			indententTableContent.add(objectID.getAttribute("rmbid"));
		}

		LoggerUtil.debug("End of registerIndentTableObjects.");
	}

	/**
	 * This method will be used to validate current page/window/frame load time with
	 * given condition to confirm test cases execution.
	 * 
	 * @author SteepGraph Systems
	 * @param driver
	 * @param attributeMap
	 * @return void
	 * @throws Exception
	 */
	@Override
	public void assertPageLoadTime(Driver driver, Map<String, String> attributeMap) throws Exception {
		LoggerUtil.debug("Started assertPageLoadTime ");
		String condition = attributeMap.get(Constants.ATTRIBUTE_CONDITION);
		String id = attributeMap.get(Constants.ATTRIBUTE_ID);
		if (StringUtils.isBlank(id) || id == null) {
			throw new IllegalArgumentException("id attribute is cannot be null or empty for AssertPageLoadTime tag.");
		}
		String input = attributeMap.get(Constants.ATTRIBUTE_INPUT);
		if (StringUtils.isBlank(input) || !StringUtils.isNumeric(input)) {
			throw new IllegalArgumentException(
					"input attribute is missing or input value is not numeric value for AssertPageLoadTime tag.");
		}

		JavascriptExecutor executer = (JavascriptExecutor) driver.getWebDriver();
		boolean isReady = false;
		do {
			Boolean result = (Boolean) executer.executeScript("return document.readyState==='complete';");
			if (result != null) {
				isReady = result.booleanValue();
			}
			LoggerUtil.debug("Window/Frame is ready? :" + isReady);
		} while (!isReady);

		Long loadTime = (Long) executer
				.executeScript("return performance.timing.loadEventEnd - performance.timing.navigationStart;");

		LoggerUtil.debug("Window/Frame loadTime :" + loadTime);
		String errorMessage = "AssertPageLoadTime failed for " + id + ". Not matching the given input";
		String infoMessage = id + " : loadtime " + loadTime;
		handler.setInfoMessage(infoMessage);
		validateAssertCondition(condition, loadTime, Long.parseLong(input.trim()), errorMessage);
		LoggerUtil.debug("AssetPageLoadTime satisfied as per given input");

		LoggerUtil.debug("End of assertPageLoadTime ");
	}

	/**
	 * Validate the assert condition
	 * 
	 * @author SteepGraph Systems
	 * @param condition
	 * @param input1
	 * @param input2
	 * @param errorMessage
	 * @throws Exception
	 */
	public void validateAssertCondition(String condition, long input1, long input2, String errorMessage)
			throws Exception {
		if ("=".equalsIgnoreCase(condition)) {
			if (input1 != input2) {
				throw new AssertionException(errorMessage);
			}
		} else if ("!=".equalsIgnoreCase(condition)) {
			if (input1 == input2) {
				throw new AssertionException(errorMessage);
			}
		} else if (">".equalsIgnoreCase(condition)) {
			if (input1 <= input2) {
				throw new AssertionException(errorMessage);
			}
		} else if ("<".equalsIgnoreCase(condition)) {
			if (input1 >= input2) {
				throw new AssertionException(errorMessage);
			}
		} else if (">=".equalsIgnoreCase(condition)) {
			if (input1 < input2) {
				throw new AssertionException(errorMessage);
			}
		} else if ("<=".equalsIgnoreCase(condition)) {
			if (input1 > input2) {
				throw new AssertionException(errorMessage);
			}
		} else {
			throw new Exception("Please choose correct condition, Your condition is :" + condition);
		}
	}

	/**
	 * Method to drag and drop on an element by it's provided source and target
	 * 
	 * @author Steepgraph Systems
	 * @param driver
	 * @param attributeMap
	 * @return void
	 * @throws Exception
	 */
	public void dragAndDrop(Driver driver, Map<String, String> attributeMap, String browserName) throws Exception {
		LoggerUtil.debug("Start of dragAndDrop.");

		if (attributeMap == null) {
			throw new IllegalArgumentException(
					" locatorType ,sourceLocatorExpression and targetLocatorExpression attribute is not specified for dragAndDrop tag");
		}

		String mode = attributeMap.get(Constants.ATTRIBUTE_MODE);
		if (StringUtils.isBlank(mode)) {
			mode = "js";
		}

		if (!attributeMap.containsKey(Constants.LOCATOR_TYPE)) {
			throw new IllegalArgumentException(" locatorType attribute is not specified for clickElement tag.");
		}

		if (!attributeMap.containsKey(Constants.SOURCE_LOCATOR_EXPRESSION)) {
			throw new IllegalArgumentException(
					" sourceLocatorExpression attribute is not specified for dragAndDrop tag.");
		}

		if (!attributeMap.containsKey(Constants.TARGET_LOCATOR_EXPRESSION)) {
			throw new IllegalArgumentException(
					" targetLocatorExpression attribute is not specified for dragAndDrop tag.");
		}

		if ("robot".equalsIgnoreCase(mode)) {
			dragAndDropRobotMode(driver, attributeMap, browserName);
		} else {

			String strlocatorType = attributeMap.get(Constants.LOCATOR_TYPE);
			LoggerUtil.debug("strlocatorType: " + strlocatorType);
			String strSourceLocatorExpression = attributeMap.get(Constants.SOURCE_LOCATOR_EXPRESSION);
			LoggerUtil.debug("strSourceLocatorExpression: " + strSourceLocatorExpression);
			String strTargetLocatorExpression = attributeMap.get(Constants.TARGET_LOCATOR_EXPRESSION);
			LoggerUtil.debug("strTargetLocatorExpression: " + strTargetLocatorExpression);

			String sourceFrameRefId = attributeMap.get(Constants.ATTRIBUTE_SOURCE_FRAME_REFID);
			WebElement wbSourceElement = null;
			String sourceFrameID = null;
			if (sourceFrameRefId != null && !"".equals(sourceFrameRefId)
					&& webElementMap.containsKey(attributeMap.get(Constants.ATTRIBUTE_SOURCE_FRAME_REFID))) {
				WebElement sourecFrameEle = webElementMap.get(sourceFrameRefId);
				sourceFrameID = sourecFrameEle.getAttribute("id");
				driver.switchToFrame(sourecFrameEle);

				// sourceFrameID=sourecFrameEle.getAttribute("id");
				wbSourceElement = driver.findElement(strlocatorType, strSourceLocatorExpression);
				highLightElement(driver, wbSourceElement, null);
			}
			switchToDefaultContent(driver);
			String targetFrameRefId = attributeMap.get(Constants.ATTRIBUTE_TARGET_FRAME_REFID);
			WebElement wbTargetElement = null;
			String targetFrameID = null;

			if (targetFrameRefId != null && !"".equals(targetFrameRefId)
					&& webElementMap.containsKey(attributeMap.get(Constants.ATTRIBUTE_TARGET_FRAME_REFID))) {
				WebElement targetFrameEle = webElementMap.get(targetFrameRefId);
				targetFrameID = targetFrameEle.getAttribute("id");
				driver.switchToFrame(targetFrameEle);
				wbTargetElement = driver.findElement(strlocatorType, strTargetLocatorExpression);
				highLightElement(driver, wbTargetElement, null);
			}

			if (wbSourceElement == null) {
				throw new Exception("Unable to loacte source element, please check source frame or source xpath");
			}
			if (wbTargetElement == null) {
				throw new Exception("Unable to loacte target element, please check target frame or target xpath");
			}

			switchToDefaultContent(driver);

			if ("selenium".equalsIgnoreCase(mode)) {
				Actions actions = new Actions(driver.getWebDriver());
				actions.moveToElement(wbSourceElement).dragAndDrop(wbSourceElement, wbTargetElement)
						.moveToElement(wbTargetElement).build().perform();
			} else if ("enterprise".equalsIgnoreCase(mode)) {
				dragAndDropJSEnterprise(sourceFrameID, targetFrameID, strSourceLocatorExpression,
						strTargetLocatorExpression, driver);
			} else {
				dragAndDropJS(wbSourceElement, wbTargetElement, driver);
			}
		}
		Thread.sleep(1000);
		LoggerUtil.debug("End of dragAndDrop.");
	}

	public void dragAndDropJS(WebElement source, WebElement destination, Driver driver) throws Exception {
		JavascriptExecutor js = (JavascriptExecutor) driver.getWebDriver();
		js.executeScript("function createEvent(typeOfEvent) {\n" + "var event =document.createEvent(\"CustomEvent\");\n"
				+ "event.initCustomEvent(typeOfEvent,true, true, null);\n" + "event.dataTransfer = {\n" + "data: {},\n"
				+ "setData: function (key, value) {\n" + "this.data[key] = value;\n" + "},\n"
				+ "getData: function (key) {\n" + "return this.data[key];\n" + "}\n" + "};\n" + "return event;\n"
				+ "}\n" + "\n" + "function dispatchEvent(element, event,transferData) {\n"
				+ "if (transferData !== undefined) {\n" + "event.dataTransfer = transferData;\n" + "}\n"
				+ "if (element.dispatchEvent) {\n" + "element.dispatchEvent(event);\n"
				+ "} else if (element.fireEvent) {\n" + "element.fireEvent(\"on\" + event.type, event);\n" + "}\n"
				+ "}\n" + "\n" + "function simulateHTML5DragAndDrop(element, destination) {\n"
				+ "var dragStartEvent =createEvent('dragstart');\n" + "dispatchEvent(element, dragStartEvent);\n"
				+ "var dropEvent = createEvent('drop');\n"
				+ "dispatchEvent(destination, dropEvent,dragStartEvent.dataTransfer);\n"
				+ "var dragEndEvent = createEvent('dragend');\n"
				+ "dispatchEvent(element, dragEndEvent,dropEvent.dataTransfer);\n" + "}\n" + "\n"
				+ "var source = arguments[0];\n" + "var destination = arguments[1];\n"
				+ "simulateHTML5DragAndDrop(source,destination);", source, destination);

		Thread.sleep(1500);
	}

	protected void dragAndDropJSEnterprise(String sourceFrameID, String targetFrameID, String sourceXPath,
			String targetXPath, Driver driver) throws IOException, InterruptedException {
		String script = "";
		try {
			String dragAndDropGeneric = propertyUtil.getProperty(Constants.PROPERTY_KEY_FOR_DRAG_AND_DROP_GENERIC);
			if (dragAndDropGeneric != null && dragAndDropGeneric.equalsIgnoreCase("false")) {
				script = new String(Files.readAllBytes(Paths.get(Constants.DRAG_AND_DROP_FILE_PATH)));
			} else {
				script = new String(Files.readAllBytes(Paths.get(Constants.DRAG_AND_DROP_GENERIC_FILE_PATH)));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		script += " simulateHTML5DragAndDrop(arguments[0], arguments[1],arguments[2], arguments[3]);";
		Thread.sleep(5000);
		JavascriptExecutor executor = (JavascriptExecutor) driver.getWebDriver();
		executor.executeScript(script, sourceFrameID, targetFrameID, sourceXPath, targetXPath);
	}

	/**
	 * Method to select security context from top bar by provided organization,
	 * collaborative space and role.
	 * 
	 * @author Steepgraph Systems
	 * @param driver
	 * @param attributeMap
	 * @return void
	 * @throws Exception
	 */

	public void selectSecurityContextFromTopBar(Driver driver, Map<String, String> attributeMap) throws Exception {
		LoggerUtil.debug("Start of selectSecurityContextFromTopBar.");

		if (attributeMap == null) {
			throw new IllegalArgumentException(
					"organization, collaborativeSpace and role is not defined for SelectSecurityContext.");
		}

		String strOrganization = attributeMap.get(Constants.ATTRIBUTE_ORGANIZATION);
		if (strOrganization == null || "".equals(strOrganization))
			throw new IllegalArgumentException("Attribute organization is not defined for SelectSecurityContext tag.");
		LoggerUtil.debug("Organization: " + strOrganization);

		String strCollaborativeSpace = attributeMap.get(Constants.ATTRIBUTE_COLLABORATIVESPACE);
		if (strCollaborativeSpace == null || "".equals(strCollaborativeSpace))
			throw new IllegalArgumentException(
					"Attribute collaborativeSpace is not defined for SelectSecurityContext tag.");
		LoggerUtil.debug("Collaborative Space: " + strCollaborativeSpace);

		String strRole = attributeMap.get(Constants.ATTRIBUTE_ROLE);
		if (strRole == null || "".equals(strRole))
			throw new IllegalArgumentException("Attribute role is not defined for SelectSecurityContext tag.");
		LoggerUtil.debug("Role: " + strRole);

		// Click user profile menu
		WebElement wbUserProfile = driver.findElement(By.xpath(
				"//div[@id='topbar']//div[@class='topbar-menu']/div[@class='profile topbar-menu-item topbar-cmd enabled fonticon fonticon-user-alt' or @class='profile topbar-menu-item topbar-cmd enabled fonticon fonticon-user-alt active' or @class='profile topbar-menu-item topbar-cmd enabled fonticon fonticon-user-alt']"));
		driver.click(wbUserProfile);

		// Click My Credentials command
		WebElement wbMyCredentials = driver.findElement(By.xpath("//span[contains(text(),'My Credentials')]"));
		driver.click(wbMyCredentials);

		// If Organization is greyed out (disabled for editing), it shows as input. In
		// this case find the input element that represents it. If value
		// of input does not match the given value, Exception is thrown. If Organization
		// is not greyed out, it is a dropdown available for option
		// selection based on the provided value. In this case, if dropdown option is
		// found equal to given value, then the option is selected. If
		// option is not found, Exception is thrown.

		// Same behaviour as for Organization is presented by Collaborative Space and
		// Role.

		WebElement wbOrganization = null;
		WebElement wbOrganizationNode = driver.findElement(
				By.xpath("//li[@class='select-box']//label[text()='Organization']/following-sibling::*[1]"));
		if (wbOrganizationNode.getTagName().equalsIgnoreCase("input")) {
			wbOrganization = driver.findElement(By.id("Organization_txt"));
			String strOrgDefValue = wbOrganization.getAttribute("value");
			LoggerUtil.debug("Default Value of Organization: " + strOrgDefValue);
			if (strOrgDefValue != null && !(strOrganization).equalsIgnoreCase(strOrgDefValue)) {
				throw new IllegalArgumentException(
						"Cannot set the given Organization value. Input does not match the available value");
			}
			LoggerUtil.debug("Organization value matches given input.");
		} else {
			try {
				// Select given value from Organization dropdown
				wbOrganization = driver.findElement(By.xpath("//select[@id='Organization']"));
				driver.selectByText(wbOrganization, strOrganization);
			} catch (Exception e) {
				throw new IllegalArgumentException(
						"Cannot select given value for Organization. Option does not exist.");
			}
		}

		Thread.sleep(2000);

		WebElement wbCollaborativeSpace = null;
		WebElement wbCollaborativeSpaceNode = driver.findElement(
				By.xpath("//li[@class='select-box']//label[text()='Collaborative Space']/following-sibling::*[1]"));
		if (wbCollaborativeSpaceNode.getTagName().equalsIgnoreCase("input")) {
			wbCollaborativeSpace = driver.findElement(By.id("Project_txt"));
			String strCSDefValue = wbCollaborativeSpace.getAttribute("value");
			LoggerUtil.debug("Default Value of Collaborative Space: " + strCSDefValue);
			if (strCSDefValue != null && !(strCollaborativeSpace).equalsIgnoreCase(strCSDefValue)) {
				throw new IllegalArgumentException(
						"Cannot set the given Collaborative Space value. Input does not match the available value");
			}
			LoggerUtil.debug("Collaborative Space value matches given input.");
		} else {
			try {
				// Select given value from Collaborative Space dropdown
				wbCollaborativeSpace = driver.findElement(By.xpath("//select[@id='Project']"));
				driver.selectByText(wbCollaborativeSpace, strCollaborativeSpace);
			} catch (Exception e) {
				throw new IllegalArgumentException(
						"Cannot select given value for Collaborative Space. Option does not exist.");
			}
		}

		Thread.sleep(2000);

		WebElement wbRole = null;
		WebElement wbRoleNode = driver
				.findElement(By.xpath("//li[@class='select-box']//label[text()='Role']/following-sibling::*[1]"));
		if (wbRoleNode.getTagName().equalsIgnoreCase("input")) {
			wbRole = driver.findElement(By.id("Project_txt"));
			String strRoleDefValue = wbRole.getAttribute("value");
			LoggerUtil.debug("Default Value of Role: " + strRoleDefValue);
			if (strRoleDefValue != null && !(strRole).equalsIgnoreCase(strRoleDefValue)) {
				throw new IllegalArgumentException(
						"Cannot set the given Role value. Input does not match the available value");
			}
			LoggerUtil.debug("Role value matches given input.");
		} else {
			try {
				// Select given value from Role dropdown
				wbRole = driver.findElement(By.id("Role"));
				driver.selectByText(wbRole, strRole);
			} catch (Exception e) {
				throw new IllegalArgumentException("Cannot select given value for Role. Option does not exist.");
			}
		}

		// Click OK button
		WebElement wbOK = driver.findElement(By.xpath("//button[@class='btn-primary']"));
		wbOK.click();

		LoggerUtil.debug("End of selectSecurityContextFromTopBar.");
	}

	@Override
	public PropertyUtil getPropertyUtil() {
		return propertyUtil;
	}

	@Override
	public void setPropertyUtil(PropertyUtil propertyUtil) {
		this.propertyUtil = propertyUtil;
	}

	@Override
	public RegisterObjectUtil getRegisterUtil() {
		return registerUtil;
	}

	@Override
	public void setRegisterUtil(RegisterObjectUtil registerUtil) {
		this.registerUtil = registerUtil;
	}

	@Override
	public IHandler getHandler() {
		return handler;
	}

	@Override
	public void setHandler(IHandler handler) {
		this.handler = handler;
	}

	/**
	 * Method to get switch to new pop-up window based on it's title
	 * 
	 * @author Steepgraph Systems
	 * @param driver
	 * @param title
	 * @return void
	 * @throws Exception
	 */
	protected void switchToWindow(Driver driver, String title, String parentWindowHandle) throws Exception {

		LoggerUtil.debug("Start of switchToWindow ");

		driver.waitForJavaScriptToLoad();

		String windowHandle = driver.getWebDriver().getWindowHandle();
		addToParentWindowStack(windowHandle);
		int iPollingInterval = Integer.parseInt(
				propertyUtil.getProperty(Constants.PROPERTY_KEY_EXECUTION_STEP_TIMEOUT_POLLINGINTERVAL)) / 1000;
		int iPollingCount = (Integer.parseInt(propertyUtil.getProperty(Constants.PROPERTY_KEY_EXECUTION_STEP_TIMEOUT))
				/ 1000) / iPollingInterval;
		LoggerUtil.debug("title : " + title);
		int countSameTitleWindow = 0;
		Set<?> winHandleSet = null;

		// Switch to Create Change Request form
		while (iPollingCount > 0 && countSameTitleWindow == 0) {

			winHandleSet = driver.getWebDriver().getWindowHandles();
			Iterator<?> sameTitleHandleitr = winHandleSet.iterator();
			while (sameTitleHandleitr.hasNext()) {
				String popupHandle = sameTitleHandleitr.next().toString();
				driver.getWebDriver().switchTo().window(popupHandle);
				String windowTitle = driver.getWebDriver().getTitle();
				if (windowTitle.equalsIgnoreCase(title)) {
					countSameTitleWindow++;
				}
			}
			Thread.sleep(iPollingInterval * 1000);
			--iPollingCount;
		}
		if (countSameTitleWindow == 0) {
			throw new Exception("Window title name may be wrong or title doesnot exit.");
		}

		Iterator<?> winHandleitr = winHandleSet.iterator();
		while (winHandleitr.hasNext()) {
			String popupHandle = winHandleitr.next().toString();
			String currentWindowTitle = driver.getWebDriver().getTitle();
			String currentWindowHandle = driver.getWebDriver().getWindowHandle();
			if (countSameTitleWindow <= 1) {
				if (currentWindowTitle.equalsIgnoreCase(title)) {
					break;
				} else {
					if (popupHandle.equalsIgnoreCase(currentWindowHandle)) {
						continue;
					} else
						driver.getWebDriver().switchTo().window(popupHandle);
				}

			} else {
				if (currentWindowTitle.equalsIgnoreCase(title)) {
					if (currentWindowHandle.equalsIgnoreCase(parentWindowHandle)) {
						if (popupHandle.equalsIgnoreCase(currentWindowHandle)) {
							continue;
						} else
							driver.getWebDriver().switchTo().window(popupHandle);
					} else {
						break;
					}
				} else {
					if (popupHandle.equalsIgnoreCase(currentWindowHandle)) {
						continue;
					} else
						driver.getWebDriver().switchTo().window(popupHandle);
				}
			}

		}

		LoggerUtil.debug("End of switchToWindow ");
	}

	/**
	 * Method to get switch to new pop-up window
	 * 
	 * @author Steepgraph Systems
	 * @param driver
	 * @returnvoid
	 * @throws Exception
	 */
	protected void switchToWindow(Driver driver) throws Exception {
		LoggerUtil.debug("Start of switchToWindow ");

		driver.waitForJavaScriptToLoad();

		String windowHandle = driver.getWebDriver().getWindowHandle();

		addToParentWindowStack(windowHandle);
		Set<?> winHandleSet = driver.getWebDriver().getWindowHandles();
		Iterator<?> winHandleitr = winHandleSet.iterator();
		while (winHandleitr.hasNext()) {
			String popupHandle = winHandleitr.next().toString();
			if (!popupHandle.contains(windowHandle)) {
				driver.getWebDriver().switchTo().window(popupHandle);
			}
		}
		LoggerUtil.debug("End of switchToWindow ");
	}

	/**
	 * Method to get switch last window
	 * 
	 * @author Steepgraph Systems
	 * @param driver
	 * @return void
	 * @throws Exception
	 */
	protected void switchToLastWindow(Driver driver) throws Exception {
		LoggerUtil.debug("Start of switchToLastWindow ");

		driver.waitForJavaScriptToLoad();

		String windowHandle = driver.getWebDriver().getWindowHandle();

		addToParentWindowStack(windowHandle);

		Set<?> winHandleSet = driver.getWebDriver().getWindowHandles();

		int size = winHandleSet.size();
		int i = 0;

		Iterator<?> winHandleitr = winHandleSet.iterator();
		while (winHandleitr.hasNext()) {
			i++;
			String popupHandle = winHandleitr.next().toString();
			if ((i == size) || (!popupHandle.contains(windowHandle) && !checkExistanceParentWindowStack(popupHandle))) {
				driver.getWebDriver().switchTo().window(popupHandle);
				break;
			}
		}
		LoggerUtil.debug("End of switchToLastWindow ");
	}

	public String getValueForRegistration(Driver driver, Map<String, String> attributeMap) throws Exception {
		LoggerUtil.debug("Start of getRegistrationValue.");
		String strlocatorType = (String) attributeMap.get("locatorType");
		LoggerUtil.debug("strlocatorType: " + strlocatorType);
		String strFieldText = null;

		String strlocatorExpression = attributeMap.get(Constants.LOCATOR_EXPRESSION);
		LoggerUtil.debug("strlocatorExpression: " + strlocatorExpression);

		if (strlocatorType == null
				|| "".equals(strlocatorType) && (strlocatorExpression == null || "".equals(strlocatorExpression))) {

			String strsuffix = attributeMap.get(Constants.ATTRIBUTE_SUFFIX);
			if (strsuffix == null || "".equals(strsuffix)) {
				strsuffix = "";
			}
			LoggerUtil.debug("suffix :" + strsuffix);
			int suffixlen = strsuffix.length();

			String strprefix = attributeMap.get(Constants.ATTRIBUTE_PREFIX);
			if (strprefix == null || "".equals(strprefix)) {
				strprefix = "";
			}
			LoggerUtil.debug("prefix :" + strprefix);
			int prefixlen = strprefix.length();

			String strlenth = (String) attributeMap.get(Constants.ATTRIBUTE_LENGTH);
			if (strlenth == null || "".equals(strlenth)) {
				strlenth = "5";
				LoggerUtil.debug("as the length is not specified by the user so length is set to its default value :"
						+ strlenth);
			}
			LoggerUtil.debug("length :" + strlenth);
			int len = Integer.parseInt(strlenth);
			int actual_length = len - prefixlen - suffixlen;
			LoggerUtil.debug("actual_length = length - prefixlen - suffixlen :" + actual_length);

			if (actual_length < 1) {
				LoggerUtil.debug("actual length is '< 1' then, numeric value should not be generated");
				strFieldText = strprefix + strsuffix;
				LoggerUtil.debug("Text = prefix + suffix :" + strFieldText);
			} else {

				String randomNumber = attributeMap.get(Constants.ATTRIBUTE_NUMERIC);
				if (randomNumber == null || "".equals(randomNumber)) {
					randomNumber = Constants.CHECK_FALSE;
					LoggerUtil.debug("Numeric value is null or empty. so, it is set to its default value false");
				}

				LoggerUtil.debug("numeric :" + randomNumber);
				if ("true".equalsIgnoreCase(randomNumber)) {
					int max = 1;
					for (int i = 0; i < actual_length; i++) {
						max = max * 10;
						LoggerUtil.debug("The value of max after multiplying by 10 is :" + max);
					}
					Random rand = new Random();
					int random_number = rand.nextInt(max);
					String num = String.valueOf(random_number);

					while (num.length() < actual_length)
						num = "0" + num;

					strFieldText = strprefix + num + strsuffix;
					LoggerUtil.debug("Text = strprefix + num + strsuffix :" + strFieldText);

				} else {
					String uuid = UUID.randomUUID().toString();
					String strNew = uuid.replace("-", "");
					String s = strNew.substring(0, Math.min(strNew.length(), actual_length));
					strFieldText = strprefix + s + strsuffix;
					LoggerUtil.debug("Text = strprefix + s + strsuffix :" + strFieldText);
				}
			}
		} else {

			WebElement weRegisterObj = driver.findElement(strlocatorType, strlocatorExpression);

			String strAttributeToRegister = (String) attributeMap.get("attribute");
			LoggerUtil.debug("strAttributeToRegister: " + strAttributeToRegister);

			strFieldText = weRegisterObj.getText();
			if (strAttributeToRegister != null && !"".equals(strAttributeToRegister))
				strFieldText = weRegisterObj.getAttribute(strAttributeToRegister);

			String removeSpace = (String) attributeMap.get(Constants.ATTRIBUTE_REMOVESPACE);
			LoggerUtil.debug("removeSpace : " + removeSpace);
			if (removeSpace == null || "".equals(removeSpace))
				removeSpace = Constants.CHECK_FALSE;

			if (removeSpace.equalsIgnoreCase(Constants.CHECK_TRUE)) {
				strFieldText = strFieldText.trim();
			}

		}
		// Get name of object to register in property file
		LoggerUtil.debug("Object being registered in property file: " + strFieldText);
		LoggerUtil.debug("End of getRegistrationValue.");

		return strFieldText;
	}

	/**
	 * Method to check if alert is thrown and handle it based on action passed from
	 * caller
	 * 
	 * @author SteepGraph Systems
	 * @param driver
	 * @param action
	 * @return Boolean
	 * @throws Exception
	 */
	protected boolean handleAlertAction(Alert alert, String action) throws Exception {
		LoggerUtil.debug("Start of checkAlertExistenceAndAccept");
		String strAction = action.toLowerCase();
		LoggerUtil.debug("strAction : " + strAction);

		switch (strAction) {
		case "accept":
			alert.accept();
			return true;
		case "dismiss":
			alert.dismiss();
			return true;
		case "present":
			return (alert != null);
		case "notpresent":
			return (alert == null);
		default:
			throw new IllegalArgumentException(strAction
					+ ": Given action is not valid, Please provide correct for the action attribute.Possible values are accept or dismiss or present or notpresent.");
		}
	}

	protected Alert getAlert(Driver driver) throws InterruptedException {
		LoggerUtil.debug("Start of getAlert");
		try {
			Thread.sleep(1000);
			ExpectedConditions.alertIsPresent();
			return driver.getWebDriver().switchTo().alert();
		} catch (NoAlertPresentException | TimeoutException | NoSuchWindowException e) {
			LoggerUtil.debug("No alert is present");
			LoggerUtil.debug("End of  getAlert");
			return null;
		}
	}

	protected void dismissAlert(Driver driver) throws InterruptedException {
		Alert alert = getAlert(driver);
		if (alert != null) {

			alert.dismiss();
			dismissAlert(driver);
		}
	}

	@Override
	public void startPerformanceLogs(Driver driver) {
		Set<Cookie> cookieSet = driver.getWebDriver().manage().getCookies();
		LoggerUtil.debug("Cookie List : ");
		LoggerUtil.performance("Cookie List : ");
		cookieData = cookieSet.toString();
		for (Cookie ck : cookieSet) {
			LoggerUtil.debug("" + ck);
			LoggerUtil.performance("" + ck);
		}

		if (driver.getProxy() != null)
			driver.getProxy().newHar();
		else
			LoggerUtil.debug("Proxy not enabled. Make changes to proxy key.");
	}

	@Override
	public void stopPerformanceLogs(Driver driver, Map<String, String> attributeMap, String msg) throws Exception {
		String timeStamp = ICommonUtil.getCurrentDateTime();
		LoggerUtil.performance(msg);
		String startRequest = attributeMap.get(Constants.ATTR_START_REQUEST);
		String stopRequest = attributeMap.get(Constants.ATTR_STOP_REQUEST);
		LoggerUtil.performance("Start WebRequest : " + startRequest);
		LoggerUtil.performance("Stop WebRequest : " + stopRequest);
		String requestOccurence = attributeMap.get(Constants.ATTR_REQUEST_OCCURENCE);
		HarEntry startEntry = null;
		HarEntry stopEntry = null;
		boolean startPoint = false;
		boolean stopPoint = false;
		long performanceTime = 0;
		String errMsg = "";

		if (driver.getProxy() == null)
			LoggerUtil.debug("Proxy not enabled. Make changes to proxy key.");
		else {
			List<HarEntry> harEntries = driver.getProxy().getHar(true).getLog().getEntries();
			if ((startRequest == null && stopRequest == null)
					|| (startRequest.equalsIgnoreCase("") && stopRequest.equalsIgnoreCase(""))) {
				for (int i = 0; i < harEntries.size(); i++) {
					if (i < (harEntries.size() - 1)) {
						if (harEntries.get(i) != null && harEntries.get(i).getRequest() != null
								&& harEntries.get(i).getRequest().getUrl() != null
								&& harEntries.get(i).getStartedDateTime() != null && harEntries.get(i + 1) != null
								&& harEntries.get(i + 1).getStartedDateTime() != null) {
							LoggerUtil.debug(
									"HarEntry URL : " + harEntries.get(i).getRequest().getUrl() + " StartedDateTime : "
											+ harEntries.get(i).getStartedDateTime() + " StartedDateTimeDifference : "
											+ (harEntries.get(i + 1).getStartedDateTime().getTime()
													- harEntries.get(i).getStartedDateTime().getTime()));
							LoggerUtil.performance(
									"HarEntry URL : " + harEntries.get(i).getRequest().getUrl() + " StartedDateTime : "
											+ harEntries.get(i).getStartedDateTime() + " StartedDateTimeDifference : "
											+ (harEntries.get(i + 1).getStartedDateTime().getTime()
													- harEntries.get(i).getStartedDateTime().getTime()));
						} else {
							LoggerUtil.debug("Exception occured. Please rerun the execution.");
							LoggerUtil.performance("Exception occured. Please rerun the execution.");
							if (harEntries.get(i) == null) {
								errMsg = "Unable to get complete HarEntry.";
								LoggerUtil.debug(errMsg);
								LoggerUtil.performance(errMsg);
							} else if (harEntries.get(i).getRequest() == null) {
								errMsg = "Unable to get request for HarEntry.";
								LoggerUtil.debug(errMsg);
								LoggerUtil.performance(errMsg);
							} else if (harEntries.get(i).getRequest().getUrl() == null) {
								errMsg = "Unable to get Url for HarEntry.";
								LoggerUtil.debug(errMsg);
								LoggerUtil.performance(errMsg);
							} else if (harEntries.get(i).getStartedDateTime() == null) {
								errMsg = "Unable to get StartedDateTime for HarEntry.";
								LoggerUtil.debug(errMsg);
								LoggerUtil.performance(errMsg);
							} else if (harEntries.get(i + 1) == null) {
								errMsg = "Unable to get next HarEntry.";
								LoggerUtil.debug(errMsg);
								LoggerUtil.performance(errMsg);
							} else if (harEntries.get(i + 1).getStartedDateTime() == null) {
								errMsg = "Unable to get StartedDateTime for next HarEntry.";
								LoggerUtil.debug(errMsg);
								LoggerUtil.performance(errMsg);
							}
							break;
						}
					} else {
						if (harEntries.get(i) != null && harEntries.get(i).getRequest() != null
								&& harEntries.get(i).getRequest().getUrl() != null
								&& harEntries.get(i).getStartedDateTime() != null) {
							LoggerUtil.debug("HarEntry URL : " + harEntries.get(i).getRequest().getUrl()
									+ " StartedDateTime : " + harEntries.get(i).getStartedDateTime());
							LoggerUtil.performance("HarEntry URL : " + harEntries.get(i).getRequest().getUrl()
									+ " StartedDateTime : " + harEntries.get(i).getStartedDateTime());
						} else {
							LoggerUtil.debug("Exception occured. Please rerun the execution.");
							LoggerUtil.performance("Exception occured. Please rerun the execution.");
							if (harEntries.get(i) == null) {
								errMsg = "Unable to get complete HarEntry.";
								LoggerUtil.debug(errMsg);
								LoggerUtil.performance(errMsg);
							} else if (harEntries.get(i).getRequest() == null) {
								errMsg = "Unable to get request for HarEntry.";
								LoggerUtil.debug(errMsg);
								LoggerUtil.performance(errMsg);
							} else if (harEntries.get(i).getRequest().getUrl() == null) {
								errMsg = "Unable to get Url for HarEntry.";
								LoggerUtil.debug(errMsg);
								LoggerUtil.performance(errMsg);
							} else if (harEntries.get(i).getStartedDateTime() == null) {
								errMsg = "Unable to get StartedDateTime for HarEntry.";
								LoggerUtil.debug(errMsg);
								LoggerUtil.performance(errMsg);
							}
							break;
						}
					}
				}
				startEntry = harEntries.get(0);
				stopEntry = harEntries.get(harEntries.size() - 1);
				performanceTime = stopEntry.getStartedDateTime().getTime() - startEntry.getStartedDateTime().getTime();
				LoggerUtil.debug("PERFORMANCE Time:=" + performanceTime);
				LoggerUtil.performance("PERFORMANCE Time:=" + performanceTime);
			} else {
				for (int i = 0; i < harEntries.size(); i++) {
					if (i < (harEntries.size() - 1)) {
						if (harEntries.get(i) != null && harEntries.get(i).getRequest() != null
								&& harEntries.get(i).getRequest().getUrl() != null
								&& harEntries.get(i).getStartedDateTime() != null && harEntries.get(i + 1) != null
								&& harEntries.get(i + 1).getStartedDateTime() != null) {
							LoggerUtil.debug(
									"HarEntry URL : " + harEntries.get(i).getRequest().getUrl() + " StartedDateTime : "
											+ harEntries.get(i).getStartedDateTime() + " StartedDateTimeDifference : "
											+ (harEntries.get(i + 1).getStartedDateTime().getTime()
													- harEntries.get(i).getStartedDateTime().getTime()));
							LoggerUtil.performance(
									"HarEntry URL : " + harEntries.get(i).getRequest().getUrl() + " StartedDateTime : "
											+ harEntries.get(i).getStartedDateTime() + " StartedDateTimeDifference : "
											+ (harEntries.get(i + 1).getStartedDateTime().getTime()
													- harEntries.get(i).getStartedDateTime().getTime()));
						} else {
							LoggerUtil.debug("Exception occured. Please rerun the execution.");
							LoggerUtil.performance("Exception occured. Please rerun the execution.");
							if (harEntries.get(i) == null) {
								errMsg = "Unable to get complete HarEntry.";
								LoggerUtil.debug(errMsg);
								LoggerUtil.performance(errMsg);
							} else if (harEntries.get(i).getRequest() == null) {
								errMsg = "Unable to get request for HarEntry.";
								LoggerUtil.debug(errMsg);
								LoggerUtil.performance(errMsg);
							} else if (harEntries.get(i).getRequest().getUrl() == null) {
								errMsg = "Unable to get Url for HarEntry.";
								LoggerUtil.debug(errMsg);
								LoggerUtil.performance(errMsg);
							} else if (harEntries.get(i).getStartedDateTime() == null) {
								errMsg = "Unable to get StartedDateTime for HarEntry.";
								LoggerUtil.debug(errMsg);
								LoggerUtil.performance(errMsg);
							} else if (harEntries.get(i + 1) == null) {
								errMsg = "Unable to get next HarEntry.";
								LoggerUtil.debug(errMsg);
								LoggerUtil.performance(errMsg);
							} else if (harEntries.get(i + 1).getStartedDateTime() == null) {
								errMsg = "Unable to get StartedDateTime for next HarEntry.";
								LoggerUtil.debug(errMsg);
								LoggerUtil.performance(errMsg);
							}
							break;
						}
					} else {
						if (harEntries.get(i) != null && harEntries.get(i).getRequest() != null
								&& harEntries.get(i).getRequest().getUrl() != null
								&& harEntries.get(i).getStartedDateTime() != null) {
							LoggerUtil.debug("HarEntry URL : " + harEntries.get(i).getRequest().getUrl()
									+ " StartedDateTime : " + harEntries.get(i).getStartedDateTime());
							LoggerUtil.performance("HarEntry URL : " + harEntries.get(i).getRequest().getUrl()
									+ " StartedDateTime : " + harEntries.get(i).getStartedDateTime());
						} else {
							LoggerUtil.debug("Exception occured. Please rerun the execution.");
							LoggerUtil.performance("Exception occured. Please rerun the execution.");
							if (harEntries.get(i) == null) {
								errMsg = "Unable to get complete HarEntry.";
								LoggerUtil.debug(errMsg);
								LoggerUtil.performance(errMsg);
							} else if (harEntries.get(i).getRequest() == null) {
								errMsg = "Unable to get request for HarEntry.";
								LoggerUtil.debug(errMsg);
								LoggerUtil.performance(errMsg);
							} else if (harEntries.get(i).getRequest().getUrl() == null) {
								errMsg = "Unable to get Url for HarEntry.";
								LoggerUtil.debug(errMsg);
								LoggerUtil.performance(errMsg);
							} else if (harEntries.get(i).getStartedDateTime() == null) {
								errMsg = "Unable to get StartedDateTime for HarEntry.";
								LoggerUtil.debug(errMsg);
								LoggerUtil.performance(errMsg);
							}
							break;
						}
					}

					if (!startPoint && harEntries.get(i).getRequest().getUrl().contains(startRequest)) {
						startEntry = harEntries.get(i);
						startPoint = true;
					}

					if (!startPoint)
						continue;

					// default value of requestOccurence is "first", other possible value is "last"
					if (requestOccurence != null && requestOccurence.equalsIgnoreCase("last")) {
						if (harEntries.get(i).getRequest().getUrl().contains(stopRequest))
							stopEntry = harEntries.get(i);
					} else {
						if (!stopPoint && harEntries.get(i).getRequest().getUrl().contains(stopRequest)) {
							stopEntry = harEntries.get(i);
							stopPoint = true;
						}
					}
				}

				if (startEntry != null && stopEntry != null) {
					performanceTime = stopEntry.getStartedDateTime().getTime()
							- startEntry.getStartedDateTime().getTime();
					LoggerUtil.debug("PERFORMANCE Time:=" + performanceTime);
					LoggerUtil.performance("PERFORMANCE Time:=" + performanceTime);
				} else {
					errMsg = "Start request or Stop request not found";
					LoggerUtil.debug("Start request or Stop request not found");
					LoggerUtil.performance("Start request or Stop request not found");
				}
			}
			performanceResult = new StringBuilder();
			performanceResult.append("\"").append(timeStamp).append("\",");
			performanceResult.append("\"").append(handler.getTestCaseName()).append("\",");
			performanceResult.append("\"").append(performanceTime).append("\",");
			performanceResult.append("\"").append(cookieData).append("\",");
			performanceResult.append("\"").append(errMsg).append("\"");

			LoggerUtil.recordPerformanceResult(performanceResult.toString());
		}
	}

	public void readUserInput(Driver driver) throws Exception {
		String strIsHeadLess = propertyUtil.getProperty(Constants.PROPERTY_KEY_ENABLE_OTP);
		LoggerUtil.debug("strIsHeadLess : " + strIsHeadLess);
		if (strIsHeadLess != null && strIsHeadLess.equalsIgnoreCase("true")) {
			System.out.println("Please Enter User OTP : ");
			@SuppressWarnings("resource")
			Scanner s = new Scanner(System.in);
			String oneTimePasswd = s.nextLine();
			LoggerUtil.debug("OTP received : " + oneTimePasswd);
			WebElement wbOTP = driver.findElement(By.xpath("//div[@id='loginArea']//input[@id='OTPInput']"));
			wbOTP.clear();
			driver.writeText(wbOTP, oneTimePasswd);
			WebElement wbOTPClick = driver
					.findElement(By.xpath("//div[@id='submissionArea']/input[@id='submitButton']"));
			driver.click(wbOTPClick);
			// RegisterObjectUtil registryObject = getRegisterUtil();
			// registryObject.registerObject("oneTimePassword", oneTimePasswd);
			registerUtil.registerObject("oneTimePassword", oneTimePasswd);
			LoggerUtil.debug("register Object Saved");
		}
	}

	/**
	 * Method is used to get the value from cookies.
	 * 
	 * @author Steepgraph Systems
	 * @param driver
	 * @param attributeMap
	 * @return void
	 * @throws Exception
	 */
	@Override
	public void getValueFromCookies(Driver driver, Map<String, String> attributeMap) throws Exception {
		LoggerUtil.debug("Start of getValueFromCookies");

		String cookiesName = attributeMap.get(Constants.COOKIE_NAME);
		if (cookiesName == null || "".equals(cookiesName)) {
			throw new Exception("Attribute cookieName is not define for getValueFromCookies tag.");
		}
		if (cookiesName != null && cookiesName != "") {
			Cookie cookieValue = driver.getWebDriver().manage().getCookieNamed(cookiesName);
			LoggerUtil.debug("Cookies : " + cookieValue);
		} else {
			LoggerUtil.debug("CookieName not passed in tag");
		}

		LoggerUtil.debug("End of getValueFromCookies");
	}

	/**
	 * Method to pin widget to new tab in dashboard.
	 * 
	 * @author Steepgraph Systems
	 * @param driver
	 * @param attributeMap
	 * @return void
	 * @throws Exception
	 */
	@Override
	public void pinWidgetToDashboard(Driver driver, Map<String, String> attributeMap) throws Exception {
		LoggerUtil.debug("Start of pinWidgetToDashboard");

		String pinBtnInWidget = attributeMap.get(Constants.ATTRIBUTE_PIN_BTN_IN_WIDGET);
		if (pinBtnInWidget == null || "".equalsIgnoreCase(pinBtnInWidget)) {
			pinBtnInWidget = Constants.CHECK_FALSE;
		}

		WebElement wbPinDb = null;
		if (pinBtnInWidget.equalsIgnoreCase(Constants.CHECK_TRUE)) {
			wbPinDb = driver.findElement(By.cssSelector("[class='preview-icon fonticon fonticon-publish']"));
		} else {
			wbPinDb = driver.findElement(By.cssSelector("[class='preview-header-icon fonticon fonticon-publish']"));
		}
		driver.click(wbPinDb);

		driver.waitUntil(ExpectedConditions.elementToBeClickable(By.xpath("//div[@class='modal-body']//form")));

		WebElement wbDropDwn = driver
				.findElement(By.xpath("//div[@class='notification-handlers-inputs']/div[2]/div[1]"));
		driver.click(wbDropDwn);

		LoggerUtil.debug("Drop down menu opened.");

		// driver.waitUntil(ExpectedConditions.visibilityOfElementLocated(By.xpath("//li[contains(text(),'Create
		// new tab')]")));
		Thread.sleep(1000);

		WebElement wbNewTab = driver.findElement(By.xpath("//li[text()='Create new tab'][@class='result-option']"));
		driver.click(wbNewTab, "js", "false");

		LoggerUtil.debug("Create new tab choosen from drop down.");

		driver.waitUntil(ExpectedConditions
				.presenceOfElementLocated(By.xpath("//li[@class='select-choice'][contains(text(),'Create new tab')]")));

		WebElement wbAddBtn = driver.findElement(By.cssSelector("[class='btn-primary btn btn-root']"));
		driver.click(wbAddBtn);
		LoggerUtil.debug("Widget added");

		LoggerUtil.debug("End of pinWidgetToDashboard");

	}

	/**
	 * Method to delete current tab in dashboard.
	 * 
	 * @author Steepgraph Systems
	 * @param driver
	 * @param attributeMap
	 * @return void
	 * @throws Exception
	 */
	@Override
	public void deleteCurrentTabInDashboard(Driver driver, Map<String, String> attributeMap) throws Exception {
		LoggerUtil.debug("Start of deleteCurrentTabInDashboard");

		WebElement wbDropDwnBtn = driver.findElement(By
				.xpath("//li[@class='wp-tab wp-drop selected']/div/span[@class='action fonticon fonticon-down-open']"));
		driver.click(wbDropDwnBtn);

		// driver.waitUntil(ExpectedConditions.elementToBeClickable(By.cssSelector("[class='moduleMenu
		// dropdown-menu dropdown-menu-root dropdown
		// dropdown-root']")));

		WebElement wbDeleteOptn = driver.findElement(By.xpath(
				"//div[@class='moduleMenu dropdown-menu dropdown-menu-root dropdown dropdown-root']/ul/li[@name='deleteItem']"));
		driver.click(wbDeleteOptn);

		LoggerUtil.debug("End of deleteCurrentTabInDashboard");
	}

	public void dragAndDropRobotMode(Driver driver, Map<String, String> attributeMap, String browserName)
			throws Exception {
		// Setup robot
		Robot robot = new Robot();
		robot.setAutoDelay(500);

		// driver.getWebDriver().manage().window().fullscreen();
		// Thread.sleep(2000);
		// Fullscreen page so selenium coordinates work
		robot.keyPress(KeyEvent.VK_F11);
		robot.keyRelease(KeyEvent.VK_F11);
		robot.delay(2000);

		String strlocatorType = attributeMap.get(Constants.LOCATOR_TYPE);
		LoggerUtil.debug("strlocatorType: " + strlocatorType);
		String strSourceLocatorExpression = attributeMap.get(Constants.SOURCE_LOCATOR_EXPRESSION);
		LoggerUtil.debug("strSourceLocatorExpression: " + strSourceLocatorExpression);
		String strTargetLocatorExpression = attributeMap.get(Constants.TARGET_LOCATOR_EXPRESSION);
		LoggerUtil.debug("strTargetLocatorExpression: " + strTargetLocatorExpression);

		// Source element calculation
		String sourceFrameRefId = attributeMap.get(Constants.ATTRIBUTE_SOURCE_FRAME_REFID);
		boolean switchToParent = false;
		if (sourceFrameRefId != null && !"".equals(sourceFrameRefId)
				&& webElementMap.containsKey(attributeMap.get(Constants.ATTRIBUTE_SOURCE_FRAME_REFID))) {
			WebElement sourecFrameEle = webElementMap.get(sourceFrameRefId);
			driver.switchToFrame(sourecFrameEle);
			switchToParent = true;
		}

		JavascriptExecutor js = (JavascriptExecutor) driver.getWebDriver();
		Map<?, ?> framePosition = (Map<?, ?>) js.executeScript(
				"function getCoordinatesOfFrame() {var y = 0;var x = 0;var currentWindow = window;while(currentWindow && currentWindow.frameElement) {var position = currentWindow.frameElement.getBoundingClientRect();y += position.y;x += position.x;currentWindow = currentWindow.parent;}return {x:x, y:y};} return getCoordinatesOfFrame();");

		Object xCoordinate = framePosition.get("x");
		Object yCoordinate = framePosition.get("y");
		long frameX;
		long frameY;
		LoggerUtil.debug("xCoordinate Sneha: " + xCoordinate);
		LoggerUtil.debug("yCoordinate Sneha: " + yCoordinate);
		if (xCoordinate instanceof Double) {
			frameX = ((Double) xCoordinate).longValue();
		} else {
			frameX = ((Long) xCoordinate).longValue();
		}

		if (yCoordinate instanceof Double) {
			frameY = ((Double) yCoordinate).longValue();
		} else {
			frameY = ((Long) yCoordinate).longValue();
		}

		WebElement wbSourceElement = driver.findElement(strlocatorType, strSourceLocatorExpression);
		highLightElement(driver, wbSourceElement, null);
		Dimension fromSize = wbSourceElement.getSize();
		Point fromLocation = wbSourceElement.getLocation();

		// Get centre distance
		int xCentreFrom = fromSize.width / 2;
		int yCentreFrom = fromSize.height / 2;

		long fromOffsetX = frameX;
		String fromOffsetXS = attributeMap.get(Constants.ATTR_SOURCE_OFFSET_X);
		if (NumberUtils.isParsable(fromOffsetXS)) {
			fromOffsetX = Integer.parseInt(fromOffsetXS);
		}

		long fromOffsetY = frameY;
		String fromOffsetYS = attributeMap.get(Constants.ATTR_SOURCE_OFFSET_Y);
		if (NumberUtils.isParsable(fromOffsetYS)) {
			fromOffsetY = Integer.parseInt(fromOffsetYS);
		}

		if (browserName.equalsIgnoreCase("chrome") || browserName.equalsIgnoreCase("edge")) {
			fromOffsetY += 42;
		}

		fromLocation.x += xCentreFrom + fromOffsetX;
		fromLocation.y += yCentreFrom + fromOffsetY;

		if (switchToParent) {
			driver.getWebDriver().switchTo().parentFrame();
			switchToParent = false;
		}

		// Target element calculation
		String targetFrameRefId = attributeMap.get(Constants.ATTRIBUTE_TARGET_FRAME_REFID);
		if (targetFrameRefId != null && !"".equals(targetFrameRefId)
				&& webElementMap.containsKey(attributeMap.get(Constants.ATTRIBUTE_TARGET_FRAME_REFID))) {
			WebElement targetFrameEle = webElementMap.get(targetFrameRefId);
			driver.switchToFrame(targetFrameEle);
			switchToParent = true;
		}

		js = (JavascriptExecutor) driver.getWebDriver();
		framePosition = (Map<?, ?>) js.executeScript(
				"function getCoordinatesOfFrame() {var y = 0;var x = 0;var currentWindow = window;while(currentWindow && currentWindow.frameElement) {var position = currentWindow.frameElement.getBoundingClientRect();y += position.y;x += position.x;currentWindow = currentWindow.parent;}return {x:x, y:y};} return getCoordinatesOfFrame();");

		frameX = ((Long) framePosition.get("x")).longValue();
		frameY = ((Long) framePosition.get("y")).longValue();

		WebElement wbTargetElement = driver.findElement(strlocatorType, strTargetLocatorExpression);
		highLightElement(driver, wbTargetElement, null);
		// Get size of elements
		Dimension toSize = wbTargetElement.getSize();
		// Get x and y of WebElement to drag to
		Point toLocation = wbTargetElement.getLocation();

		int xCentreTo = toSize.width / 2;
		int yCentreTo = toSize.height / 2;

		// Make Mouse coordinate centre of element
		long toOffsetX = frameX;
		String toOffsetXS = attributeMap.get(Constants.ATTR_TARGET_OFFSET_X);
		if (NumberUtils.isParsable(toOffsetXS)) {
			toOffsetX = Integer.parseInt(toOffsetXS);
		}

		long toOffsetY = frameY;
		String toOffsetYS = attributeMap.get(Constants.ATTR_TARGET_OFFSET_Y);
		if (NumberUtils.isParsable(toOffsetYS)) {
			toOffsetY = Integer.parseInt(toOffsetYS);
		}

		if (browserName.equalsIgnoreCase("chrome") || browserName.equalsIgnoreCase("edge")) {
			toOffsetY += 42;
		}

		toLocation.x += xCentreTo + toOffsetX;
		toLocation.y += yCentreTo + toOffsetY;

		if (switchToParent) {
			driver.getWebDriver().switchTo().parentFrame();
			switchToParent = false;
		}

		Region imageRegion = new Region(fromLocation.x, fromLocation.y);
		imageRegion.mouseMove();
		imageRegion.mouseDown(Button.LEFT);
		Thread.sleep(500);

		// Drag events require more than one movement to register
		// Just appearing at destination doesn't work so move halfway first
		imageRegion.setRect((new Region(((toLocation.x - fromLocation.x) / 2) + fromLocation.x,
				((toLocation.y - fromLocation.y) / 2) + fromLocation.y)));
		imageRegion.mouseMove();
		Thread.sleep(500);
		imageRegion.setRect(new Region(toLocation.x, toLocation.y));
		imageRegion.mouseMove();
		Thread.sleep(500);

		// Drop
		imageRegion.mouseUp();
		Thread.sleep(500);
		robot.keyPress(KeyEvent.VK_F11);
		robot.keyRelease(KeyEvent.VK_F11);
		robot.delay(1000);
	}

	public void clickClock(Driver driver, Map<String, String> attributeMap) throws Exception {

		LoggerUtil.debug("Start of click on clock");

		String strWait = attributeMap.get(Constants.ATTRIBUTE_WAIT);

		String finalStrWait = getTimeOut(strWait);
		LoggerUtil.debug("timeout : " + finalStrWait);

		HashMap<String, String> attributeMapForDashboardList = new HashMap<String, String>() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 2172767121600699396L;
			{
				put(Constants.LOCATOR_TYPE, Constants.INPUTTYPE_XPATH);
				put(Constants.LOCATOR_EXPRESSION, "//span[@class='wux-ui-3ds wux-ui-3ds-1x wux-ui-3ds-clock']");
				put(Constants.ATTRIBUTE_CRITERIA, "found");
				put(Constants.ATTRIBUTE_WAIT, finalStrWait);

			}
		};
		if (ifCondition(driver, attributeMapForDashboardList, null)) {
			WebElement wbClock = driver
					.findElement(By.cssSelector("[class='wux-ui-3ds wux-ui-3ds-1x wux-ui-3ds-clock']"));
			driver.click(wbClock);
		} else {
			WebElement wbSeach = driver
					.findElement(By.xpath("//span[@class='wux-ui-3ds wux-ui-3ds-1x wux-ui-3ds-search']"));
			driver.click(wbSeach);
		}
		try {
			WebElement wbShowAll = driver.findElement(By.xpath("//span[contains(text(),'Show all')]"));
			driver.click(wbShowAll);
		} catch (Exception e) {
			try {
				WebElement wbShowMtchType = driver
						.findElement(By.xpath("//span[contains(text(),'Search in my recent content')]"));
				driver.click(wbShowMtchType);
			} catch (Exception f) {
				LoggerUtil.debug("Unable to find element");
			}
		}
		LoggerUtil.debug("End of click on clock");
	}

	/**
	 * Method to select option from context menu in dashboard.
	 * 
	 * @author Steepgraph Systems
	 * @param driver
	 * @param attributeMap
	 * @return void
	 * @throws Exception
	 */
	@Override
	public void selectContextMenuOption(Driver driver, Map<String, String> attributeMap) throws Exception {
		LoggerUtil.debug("Start of selectContextMenuOption method.");

		String strElementID = attributeMap.get(Constants.ATTRIBUTE_REFID);
		if (strElementID == null || "".equals(strElementID))
			throw new Exception("Attribute refid is not define for action tag.");

		LoggerUtil.debug("refid:" + strElementID);

		if (!webElementMap.containsKey(strElementID))
			throw new Exception("FindElement tag should be declared before selectContextMenuOption tag with the id = "
					+ strElementID);

		WebElement wbElement = webElementMap.get(strElementID);

		highLightElement(driver, attributeMap, wbElement);

		Actions actions = new Actions(driver.getWebDriver());
		actions.moveToElement(wbElement);
		actions.contextClick(wbElement).build().perform();

		String menuOptionNumber = attributeMap.get(Constants.ATTRIBUTE_MENU_OPTION);
		if (menuOptionNumber == null || "".equals(menuOptionNumber))
			menuOptionNumber = "2";

		WebElement contextOptn = driver.findElement(
				By.xpath("//div[@class='wux-menu-column wux-menu-column-left']/div[" + menuOptionNumber + "]/div"));
		driver.click(contextOptn);

		LoggerUtil.debug("End of selectContextMenuOption method.");
	}

	/**
	 * Method to Maximize or minimize the widget.
	 * 
	 * @author Steepgraph Systems
	 * @param driver
	 * @param attributeMap
	 * @return void
	 * @throws Exception
	 */
	@Override
	public void maximizeAndMinimize(Driver driver, Map<String, String> attributeMap) throws Exception {
		LoggerUtil.debug("Start of maximizeAndMinimize");
		WebElement wbAddBtnMaxMin = null;
		String strMaxMinWidget = attributeMap.get(Constants.ATTRIBUTE_MAXIMIZE_MINIMIZE_WIDGET);
		String strAppName = attributeMap.get(Constants.APPNAME);
		if (strMaxMinWidget != null && "true".equalsIgnoreCase(strMaxMinWidget)) {

			wbAddBtnMaxMin = driver.findElement(By.xpath(
					"//div[@class='wp-tab-panel selected']//div[@class='wp-tab-panel selected']//span[contains(., '"
							+ strAppName + "')]/../span[3]/span[@class='fonticon fonticon-resize-full']"));
		}

		if (strMaxMinWidget != null && "false".equalsIgnoreCase(strMaxMinWidget)) {

			wbAddBtnMaxMin = driver.findElement(By.xpath("//div[@class='wp-tab-panel selected']//span[contains(., '"
					+ strAppName + "')]/../span[3]/span[@class='fonticon fonticon-resize-small']"));
		}
		driver.waitUntil(ExpectedConditions.visibilityOf(wbAddBtnMaxMin));
		driver.click(wbAddBtnMaxMin);

		LoggerUtil.debug("End of maximizeAndMinimize");

	}

	@Override
	public void refreshBrowser(Driver driver, Map<String, String> attributeMap) throws Exception {
		LoggerUtil.debug("Start of refreshBrowsers.");
		boolean isWebElementDisplayed = false;
		if (attributeMap == null) {
			throw new Exception(" locatorType and locatorExpression attribute is not specified for refreshBrowser tag");
		}

		if (!attributeMap.containsKey(Constants.LOCATOR_TYPE)) {
			throw new Exception(" locatorType attribute is not specified for refreshBrowser tag.");
		}

		if (!attributeMap.containsKey(Constants.LOCATOR_EXPRESSION)) {
			throw new Exception(" locatorExpression attribute is not specified for refreshBrowser tag.");
		}

		String strlocatorType = attributeMap.get(Constants.LOCATOR_TYPE);
		LoggerUtil.debug("refreshBrowser strlocatorType: " + strlocatorType);
		String strlocatorExpression = attributeMap.get(Constants.LOCATOR_EXPRESSION);
		LoggerUtil.debug("refreshBrowser strlocatorExpression: " + strlocatorExpression);
		try {
			LoggerUtil.debug("refreshBrowser strlocatorExpression: inside try ");
			driver.findElement(strlocatorType, strlocatorExpression);
			LoggerUtil.debug("refreshBrowser strlocatorExpression: " + strlocatorExpression);
			// highLightElement(driver, attributeMap, wbElement);
			isWebElementDisplayed = driver.findElement(strlocatorType, strlocatorExpression).isDisplayed();
		} catch (Exception e) {
			e.printStackTrace();
		}

		LoggerUtil.debug("Start of isWebElementDisplayed." + isWebElementDisplayed);
		if (!isWebElementDisplayed) {
			LoggerUtil.debug("Start of inside if.");
			driver.getWebDriver().navigate().refresh();
		}

	}

	/**
	 * Method to open navigation panel.
	 * 
	 * @author Steepgraph Systems
	 * @param driver
	 * @param attributeMap
	 * @return void
	 * @throws Exception
	 */
	@Override
	public void clickNavigationPanelMenu(Driver driver, Map<String, String> attributeMap) throws Exception {
		LoggerUtil.debug("Left blank for Aras.");
	}

	/**
	 * Method to Open New Tab in 3DDashBoard
	 * 
	 * @author SteepGraph Systems
	 * @param driver
	 * @return void
	 * @throws Exception
	 */
	@Override
	public void openNewTabInDashBoard(Driver driver) throws Exception {

		LoggerUtil.debug("Start of OpenNewTabInDashBoard.");

		String strClassName = driver.findElement(By.xpath(
				"//div[contains(@class, 'wp-scroller-button icon fonticon') and contains(@class, 'fonticon-right-open')]"))
				.getAttribute("class");
		WebElement webElement = null;

		if (strClassName.contains(Constants.STR_DISABLED)) {
			webElement = driver.findElement(By.xpath("//span[@class='icon fonticon fonticon-plus']"));
			driver.click(webElement);
		} else {
			webElement = driver
					.findElement(By.xpath("//div/div[@class='wp-tab-addlist icon fonticon fonticon-list-add']"));
			driver.click(webElement);
			webElement = driver.findElement(By.xpath("//ul/li[@class='wp-menu-action addtab']"));
			driver.click(webElement);
		}

		LoggerUtil.debug("End of OpenNewTabInDashBoard.");
	}

	@Override
	public void validateTableExport(Driver driver, Map<String, String> attributeMap) throws Exception {
		LoggerUtil.debug("Start of validateTableExport.");
		switchToContentFrame(driver, attributeMap);

		WebElement toolBtn = driver.findElement(By.xpath("//div[@id='divToolbar']//td[@title='Tools']/img"));
		driver.click(toolBtn);

		WebElement exportBtn;
		Thread.sleep(2000);
		int exeCnt = 0;
		while (exeCnt < 3) {
			try {
				exportBtn = driver.findElement(By.xpath("//label[text()='Export']"));
				driver.waitUntil(ExpectedConditions.visibilityOf(exportBtn));
				driver.click(exportBtn);
				break;
			} catch (StaleElementReferenceException e) {
				// intentionally not handled.
			}
			exeCnt++;
		}

		String inputFilePath = attributeMap.get(Constants.ATTRIBUTE_FILEPATH);
		String headerList = attributeMap.get(Constants.ATTRIBUTE_HEADER);
		String sCount = attributeMap.get(Constants.ATTRIBUTE_SKIPCOUNT);
		String wTime = attributeMap.get(Constants.ATTRIBUTE_WAITTIME);

		int waitTime;
		if (wTime == null || "".equals(wTime)) {
			waitTime = 5000;
		} else {
			waitTime = Integer.parseInt(wTime) * 1000;
		}
		Thread.sleep(waitTime);

		if (attributeMap == null || attributeMap.size() == 0 || !attributeMap.containsKey(Constants.ATTRIBUTE_HEADER)) {
			throw new Exception("headerList is not defined for tag validateExport.");
		}

		int skipCount;
		if (sCount == null || "".equals(sCount))
			throw new Exception("Attribute refid is not define for action tag.");
		else
			skipCount = Integer.parseInt(sCount);

		if (attributeMap == null || attributeMap.size() == 0
				|| !attributeMap.containsKey(Constants.ATTRIBUTE_SKIPCOUNT)) {
			throw new Exception("skipCount is not defined for tag validateExport.");
		}

		LoggerUtil.debug("headerList: " + headerList);

		String[] headerArray = headerList.split(Pattern.quote("|"));
		int headerArrayLength = headerArray.length;

		File downloadedFile = ICommonUtil.getLatestDownloadedFile(inputFilePath, "csv");

		ICSVUtil csvUtilObj = new CSVUtil(downloadedFile.getAbsolutePath(), skipCount);
		HashMap<String, Integer> csvHeaderMap = csvUtilObj.getHeaderMap();

		HashMap<String, Integer> tableHeaderMap = getTableHeaderMap(driver);
		LoggerUtil.debug("tableHeaderMap" + tableHeaderMap);

		String headValue = null;
		for (int i = 0; i < headerArrayLength; i++) {
			headValue = headerArray[i];
			if (!csvHeaderMap.containsKey(headValue)) {
				LoggerUtil.debug("Input header " + headValue + " in tag is not present in csv");
				return;
			}

			if (!tableHeaderMap.containsKey(headValue)) {
				LoggerUtil.debug("Input header " + headValue + " in tag is not present in table");
				return;
			}
		}
		LoggerUtil.debug("Input headers in tag are present in both table and csv");

		List<WebElement> wbtableRows = driver.findElements2("xpath", "//table[@id='treeBodyTable']/tbody/tr[@id]");
		int tableRowCount = wbtableRows.size();
		int rowCount = 0;
		while (csvUtilObj.next() && rowCount < 5 && rowCount < tableRowCount) {
			for (int i = 0; i < headerArrayLength; i++) {
				headValue = headerArray[i];
				if (tableHeaderMap.get(headValue) != 1) {
					WebElement wbTableEl = driver.findElement(By.xpath("//div[@id='mx_divTableBody']//td[@rmbrow='0,"
							+ rowCount + "' and @position='" + tableHeaderMap.get(headValue)
							+ "'][not(a)] | //div[@id='mx_divTableBody']//td[@rmbrow='0," + rowCount
							+ "' and @position='" + tableHeaderMap.get(headValue) + "']/a"));
					if (!(wbTableEl.getText().replace("\"", "").replace("=", "").trim()).equalsIgnoreCase(
							csvUtilObj.getCell(headValue).replace("\"", "").replace("=", "").trim())) {
						LoggerUtil.debug("Table value under " + headValue + " column is not matching");
						return;
					}
				} else {
					WebElement wbTreeEl = driver.findElement(
							By.xpath("//div[@id='mx_divTreeBody']//td[@title][@rmbrow='0," + rowCount + "']/a"));
					if (!(wbTreeEl.getText().replace("\"", "").replace("=", ""))
							.equalsIgnoreCase(csvUtilObj.getCell(headValue).replace("\"", "").replace("=", ""))) {
						LoggerUtil.debug("Table value under " + headValue + " column is not matching");
						return;
					}
				}
			}

			rowCount++;
		}

		LoggerUtil.debug("Content of table and exported file matches for headerList provided in tag.");

		csvHeaderMap = null;
		tableHeaderMap = null;

		LoggerUtil.debug("End of validateTableExport.");
	}

	public HashMap<String, Integer> getTableHeaderMap(Driver driver) throws Exception {
		LoggerUtil.debug("Start of getTableHeaderMap.");
		HashMap<String, Integer> tableHeaderMap = new HashMap<String, Integer>();

		WebElement wbTreeHead = driver.findElement(By.xpath("//div[@id='mx_divTreeHead']//th[@id]//td/a"));
		String wbTreeHeadValue = wbTreeHead.getText();
		tableHeaderMap.put(wbTreeHeadValue, 1);

		List<WebElement> wbTableHeadElements = driver.findElements2("xpath",
				"//div[@id='mx_divTableHead']//th[@id]//td[1]/child::*");

		WebElement wbTableHeadEl;
		for (int i = 1; i <= wbTableHeadElements.size(); i++) {
			wbTableHeadEl = driver
					.findElement(By.xpath("//div[@id='mx_divTableHead']//th[@id]//td['" + i + "']/child::*"));
			tableHeaderMap.put(wbTableHeadEl.getText(), (i + 1));
		}

		LoggerUtil.debug("End of getTableHeaderMap.");
		return tableHeaderMap;
	}

	/**
	 * @Method This method unzip the file and and store the doc structure in
	 *         variable.
	 * 
	 * @author SteepGraph Systems
	 * @param driver
	 * @return void
	 * @throws Exception
	 */
	@Override
	public void readXML(Driver driver, Map<String, String> attributeMap, String strInputText) throws Exception {

		LoggerUtil.debug("Starting of readxml Tag.");
		String strFilePath = Constants.STR_EMPTY_STRING;
		String strDeleteFile = attributeMap.get(Constants.ATTRIBUTE_DELETE_FILE);

		if (null == attributeMap.get(Constants.ATTRIBUTE_UNZIP)
				|| attributeMap.get(Constants.ATTRIBUTE_UNZIP).isEmpty())
			throw new AssertionException(
					"Please select boolean unzip as true for unzip file or false to use XML file.");

		if (null == attributeMap.get(Constants.ATTRIBUTE_UNZIP_LOCATION)
				&& attributeMap.get(Constants.ATTRIBUTE_UNZIP).equalsIgnoreCase(Constants.CHECK_TRUE))
			throw new AssertionException(
					"Unzip file path should be blank for unzip value false for reading the xml file.");

		if (null == attributeMap.get(Constants.ATTRIBUTE_FROM_BROWSER_DOWNLOAD_LOCATION)
				|| attributeMap.get(Constants.ATTRIBUTE_FROM_BROWSER_DOWNLOAD_LOCATION).isEmpty())
			throw new AssertionException(
					"browser download location must not be null. please set boolean true or false.");

		if (null == strInputText || strInputText.isEmpty()
				&& (attributeMap.get(Constants.ATTRIBUTE_FROM_BROWSER_DOWNLOAD_LOCATION).equalsIgnoreCase(CHECK_TRUE)
						&& null == attributeMap.get(Constants.ATTRIBUTE_DOWNLOAD_FILE_EXTENSION)
						|| attributeMap.get(Constants.ATTRIBUTE_DOWNLOAD_FILE_EXTENSION).isEmpty()))
			throw new AssertionException(
					"Attribute id value cannot be null or empty. only the input of csv file set to null in case of browser download location. ");

		if (attributeMap.get(Constants.ATTRIBUTE_UNZIP).equalsIgnoreCase(Constants.CHECK_TRUE))
			strFilePath = attributeMap.get(Constants.ATTRIBUTE_UNZIP_LOCATION)
					.concat(unZipFileInLocation(attributeMap, strInputText)).concat(".xml");
		else if (attributeMap.get(Constants.ATTRIBUTE_UNZIP).equalsIgnoreCase(Constants.CHECK_FALSE) && (attributeMap
				.get(Constants.ATTRIBUTE_FROM_BROWSER_DOWNLOAD_LOCATION).equalsIgnoreCase(Constants.CHECK_TRUE)))
			strFilePath = ICommonUtil
					.getLatestDownloadedFile(null, attributeMap.get(Constants.ATTRIBUTE_DOWNLOAD_FILE_EXTENSION))
					.getAbsolutePath();
		else if (attributeMap.get(Constants.ATTRIBUTE_UNZIP).equalsIgnoreCase(Constants.CHECK_FALSE))
			strFilePath = strInputText;
		else
			throw new AssertionException("Attribute unzip must boolean check either true or false.");

		LoggerUtil.debug("File Path: " + strFilePath);

		if (null == strDeleteFile || strDeleteFile.isEmpty() || (!strDeleteFile.equalsIgnoreCase(Constants.CHECK_TRUE)
				&& !strDeleteFile.equalsIgnoreCase(Constants.CHECK_FALSE))) {
			strDeleteFile = Constants.CHECK_TRUE;
			LoggerUtil.debug("WARANING!!! deletefile attribute value should be true or false, and Default value is true.");
		}

		Document doc = getXMLDocumentObject(strFilePath, strDeleteFile, attributeMap);
		docObjectMap.put(attributeMap.get(Constants.ATTRIBUTE_ID), doc);
		LoggerUtil.debug("End of ReadXml Tag.");
	}

	/**
	 * @Method This method is check for Regular Expression and Create Document
	 *         Object
	 * @param strFilePath
	 * @param strRegexExpression
	 * @return Document
	 * @throws Exception
	 */
	public Document getXMLDocumentObject(String strFilePath, String strDeleteFile, Map<String, String> attributeMap)
			throws Exception {

		String strLines = Constants.STR_EMPTY_STRING;
		String strLineByLine = Constants.STR_EMPTY_STRING;
		String strXML = Constants.STR_EMPTY_STRING;
		String strRegexExpr = "[<>](?=(([^\"]*\"){2})*[^\"]*\"[^\"]*$)";
		String strRegexExpression = attributeMap.get(Constants.ATTRIBUTE_REGEX_EXPRESSION);
		String strReplaceTo = "&gt;";
		Document doc = null;

		LoggerUtil.debug("Creating the XML Document Object.");
		File file = new File(strFilePath);
		if (null != strRegexExpression && strRegexExpression.equalsIgnoreCase(Constants.CHECK_TRUE)) {

			BufferedReader bufferReader = new BufferedReader(new FileReader(file));
			while ((strLines = bufferReader.readLine()) != null) {
				strLineByLine += strLines;
			}

			Pattern pattern = Pattern.compile(strRegexExpr);
			Matcher matcher = pattern.matcher(strLineByLine);
			strXML = matcher.replaceAll(strReplaceTo);
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			doc = db.parse(new InputSource(new StringReader(strXML)));
			bufferReader.close();
		} else {

			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			doc = db.parse(file);
		}

		if (strDeleteFile.equalsIgnoreCase(Constants.CHECK_TRUE)) {
			if (file.exists()) {
				file.delete();
				LoggerUtil.debug("Deleted the file: " + strFilePath);
			}
		}
		LoggerUtil.debug("XML Document Object get created." + doc);
		return doc;
	}

	/**
	 * @method This method used to UnZip the File with given Format
	 * @param attributeMap Zip and Unzip Path
	 * @return null
	 * @throws Exception
	 */
	public String unZipFileInLocation(Map<String, String> attributeMap, String strInputText) throws Exception {

		LoggerUtil.debug("Starting to Unzip the File.");
		File destDir = null;
		if (attributeMap.get(Constants.ATTRIBUTE_FROM_BROWSER_DOWNLOAD_LOCATION).equalsIgnoreCase(Constants.CHECK_TRUE))
			destDir = ICommonUtil.getLatestDownloadedFile(null,
					attributeMap.get(Constants.ATTRIBUTE_DOWNLOAD_FILE_EXTENSION));
		else
			destDir = new File(strInputText);

		String strUnZipFileLocation = attributeMap.get(Constants.ATTRIBUTE_UNZIP_LOCATION);
		String strDeleteFile = attributeMap.get(Constants.ATTRIBUTE_DELETE_FILE);
		String xmlFileName = null;

		LoggerUtil.debug("UnZipFileLocation: " + strUnZipFileLocation);

		if (!destDir.exists()) {
			destDir.mkdir();
		}
		ZipInputStream zipIn = new ZipInputStream(new FileInputStream(destDir.getAbsolutePath()));
		ZipEntry entry = zipIn.getNextEntry();

		while (entry != null) {
			String filePath = strUnZipFileLocation + File.separator + entry.getName();
			xmlFileName = entry.getName();

			if (!entry.isDirectory()) {
				BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(filePath));
				byte[] bytesIn = new byte[4096];
				int read = 0;
				while ((read = zipIn.read(bytesIn)) != -1) {
					bos.write(bytesIn, 0, read);
				}
				bos.close();
			} else {
				File dir = new File(filePath);
				dir.mkdirs();
			}
			zipIn.closeEntry();
			entry = zipIn.getNextEntry();
		}

		zipIn.close();
		LoggerUtil.debug("File get UnZip Succesfully...");

		LoggerUtil.debug("Getting the File Name.");
		Path path = Paths.get(destDir.getAbsolutePath());
		String fileName = path.getFileName().toString();
		LoggerUtil.debug("File Name is: " + fileName.toString().substring(0, fileName.toString().lastIndexOf(".")));

		if (null == strDeleteFile || strDeleteFile.equalsIgnoreCase(Constants.CHECK_TRUE)) {
			if (destDir.exists()) {
				destDir.delete();
				LoggerUtil.debug("Deleted zip folder: " + destDir.getAbsolutePath());
			}
		}
		return xmlFileName.replace(".xml", "");
	}

	/**
	 * @Method This method used to compare Two XML Full or Parital
	 * 
	 * @author SteepGraph Systems
	 * @param driver
	 * @return void
	 * @throws Exception
	 */
	@Override
	public void assertXML(Driver driver, Map<String, String> attributeMap, String strInputText) throws Exception {

		LoggerUtil.debug("Starting of AssertXML Tag.");
		String docsReferenceId = attributeMap.get(Constants.ATTRIBUTE_REFID);

		if (docObjectMap.containsKey(docsReferenceId)) {

			if (attributeMap.get(Constants.ATTRIBUTE_MATCH).equalsIgnoreCase(Constants.STR_FULL)) {

				if (null == strInputText || strInputText.isEmpty())
					throw new AssertionException("Please provide full XML path in id attribute value.");

				validatedFullXMLFile(attributeMap, strInputText);
			} else if (attributeMap.get(Constants.ATTRIBUTE_MATCH).equalsIgnoreCase(Constants.STR_PARTIAL)) {

				if (attributeMap.get(Constants.ATTRIBUTE_CRITERIA).equals("none")
						|| attributeMap.get(Constants.ATTRIBUTE_CRITERIA).isEmpty())
					throw new AssertionException("criteria must be exist or notexist.");

				validatedParitalXMLFile(driver, attributeMap);
			} else {
				throw new AssertionException("Match Attribute values doesnot match.");
			}
		} else {
			throw new AssertionException(
					"Refid should not null. Refid document could not found, please provide valid refid same as id that are present in ReadXML tag.");
		}
		LoggerUtil.debug("End of AssertXML Tag.");
	}

	/**
	 * @method This Method is used to Compare two Full XML
	 * @param attributeMap
	 * @throws Exception
	 * @return null
	 */
	public void validatedFullXMLFile(Map<String, String> attributeMap, String strInputText) throws Exception {

		LoggerUtil.debug("Starting of Validating Full XML.");

		String strDeleteFile = attributeMap.get(Constants.ATTRIBUTE_DELETE_FILE);
		if (null == strDeleteFile || strDeleteFile.isEmpty()) {
			strDeleteFile = Constants.CHECK_FALSE;
		}

		Document docOfOriginalFile = getXMLDocumentObject(strInputText, strDeleteFile, attributeMap);
		docOfOriginalFile.normalizeDocument();
		Document docOfCompareFile = (Document) docObjectMap.get(attributeMap.get(Constants.ATTRIBUTE_REFID));
		docOfCompareFile.normalizeDocument();

		Diff xmlDiff = new Diff(docOfOriginalFile, docOfCompareFile);
		DetailedDiff detailDifference = new DetailedDiff(xmlDiff);
		List<Difference> lstOfDifference = detailDifference.getAllDifferences();

		if (lstOfDifference.size() > 0) {
			for (Difference difference : lstOfDifference) {
				LoggerUtil.debug("Difference in two XMLs is: " + difference.toString());
			}
			throw new AssertionException("Fails to Compare two XMLs files.");
		}
		LoggerUtil.debug("Validation of XML is Successfully Done.");
	}

	/**
	 * @method This method retrieve value of Xpath Expression and compare with id
	 *         value.
	 * 
	 * @param driver
	 * @param attributeMap
	 * @throws Exception
	 * @return null
	 */
	public void validatedParitalXMLFile(Driver driver, Map<String, String> attributeMap) throws Exception {

		LoggerUtil.debug("Partial Validation Starting.");
		String strExpr = attributeMap.get(Constants.ATTRIBUTE_XPATH_EXPRESSION);

		if (null == strExpr || strExpr.isEmpty())
			throw new AssertionException("Attribute xpathExpression should not be blank or null");

		Document doc = (Document) docObjectMap.get(attributeMap.get(Constants.ATTRIBUTE_REFID));
		XPathFactory xpathfactory = XPathFactory.newInstance();
		XPath xpath = xpathfactory.newXPath();

		XPathExpression expr = xpath.compile(attributeMap.get(Constants.ATTRIBUTE_XPATH_EXPRESSION));
		Object result = expr.evaluate(doc, XPathConstants.NODE);
		Node node = (Node) result;
		LoggerUtil.debug("Node: " + node);

		if (attributeMap.get(Constants.ATTRIBUTE_CRITERIA).equals(Constants.STR_EXIST) && (null == node))
			throw new AssertionException("Assertion Criteria fail for this expression: " + strExpr);
		else if (attributeMap.get(Constants.ATTRIBUTE_CRITERIA).equals(Constants.STR_NOT_EXIST) && null != node)
			throw new AssertionException("Assertion Criteria fail for this expression: " + strExpr);
		else
			LoggerUtil.debug("Partial Validation is Completed Successfully.");
	}

	/**
	 * @method This method is used open new toolbar in dashboard.
	 * 
	 * @param driver
	 * @param attributeMap
	 * @throws Exception
	 * @return null
	 */
	@Override
	public void openNewToolbarInDashboard(Driver driver, Map<String, String> attributeMap) throws Exception {

		LoggerUtil.debug("Start of OpenNewToolbarInDashboard.");
		String strToolbarMenu = attributeMap.get(Constants.TOOLBAR_NAME);
		String strActionMenu = attributeMap.get(Constants.ACTION_MENU_NAME);
		String strDropdownOption = attributeMap.get(Constants.DROPDOWN_OPTION);

		LoggerUtil.debug("strToolbarMenu: " + strToolbarMenu);
		LoggerUtil.debug("strActionMenu: " + strActionMenu);
		LoggerUtil.debug("strDropdown: " + strDropdownOption);

		if (null == strToolbarMenu || strToolbarMenu.isEmpty()) {
			throw new Exception("Toolbarname should not be null.");
		}

		if (null == strActionMenu || strActionMenu.isEmpty()) {
			throw new Exception("ActionMenu should not be null.");
		}
		// All toolbar : Select one specific value
		WebElement wbElement = driver.findElement(By.xpath("//span[text()='" + strToolbarMenu + "']//parent::div"));
		wbElement.click();

		if (null == strDropdownOption || strDropdownOption.isEmpty()) {
			wbElement = driver.findElement(By.xpath(
					"//div[@class='wux-afr-cmdstarter']//div[text()='" + strActionMenu + "']//parent::div/div[1]"));
			wbElement.click();
		} else {
			wbElement = driver.findElement(By.xpath("//div[@class='wux-afr-label'  and text()='" + strActionMenu
					+ "']/parent::div/parent::div/parent::div/div[2]"));
			wbElement.click();
			Thread.sleep(3000);
			wbElement = driver.findElement(By
					.xpath("//ul[contains(@class,'wux-afr-dropdown-menu')]//div[text()='" + strDropdownOption + "']"));
			wbElement.click();
		}
		LoggerUtil.debug("End of OpenNewToolbarInDashboard.");

	}

	/**
	 * @method This method is used Validated xls and xlsx Header from downloaded
	 *         excel.
	 * 
	 * @param driver
	 * @param attributeMap
	 * @throws Exception
	 * @return null
	 */
	@Override
	public void validateTableHeader(Driver driver, Map<String, String> attributeMap) throws Exception {

		LoggerUtil.debug("start of validateTableHeader.");
		String strHeaderList = attributeMap.get(Constants.ATTRIBUTE_HEADER);
		String strFileExtension = attributeMap.get(Constants.ATTRIBUTE_DOWNLOAD_FILE_EXTENSION);
		File inputFile = null;

		if (null == strHeaderList || strHeaderList.isEmpty()) {
			throw new Exception("headerList is not defined for tag validateTableHeader.");
		}

		if (null == strFileExtension || strFileExtension.isEmpty()) {
			throw new Exception("fileExt is not defined for tag validateTableHeader.");
		}

		if (strFileExtension.equalsIgnoreCase("xlsx") || strFileExtension.equalsIgnoreCase("xls"))
			inputFile = ICommonUtil.getLatestDownloadedFile(null, strFileExtension);
		else
			throw new Exception("Please Provide xls or xlsx file extension.");

		List<String> lstOfHeaderList = Arrays.stream(strHeaderList.split(Pattern.quote("|")))
				.collect(Collectors.toList());
		List<String> lstOfHeaderColumnFromFile = getHeaderColumn(inputFile, strFileExtension);

		LoggerUtil.debug("HeaderList Data:  " + lstOfHeaderList);
		LoggerUtil.debug("HeaderListFromDownloaded Data: " + lstOfHeaderColumnFromFile);

		if (!lstOfHeaderColumnFromFile.equals(lstOfHeaderList))
			throw new Exception("File Data and Header List doesnot match.");

		LoggerUtil.debug("End of validateTableHeader and Successfully validated the data.");
	}

	/**
	 * @method This method read xls and xlsx file
	 * @param inputFile
	 * @param strFileExtension
	 * @return List having all the headerList data of xls and xlsx file
	 * @throws Exception
	 */
	public List<String> getHeaderColumn(File inputFile, String strFileExtension) throws Exception {

		LoggerUtil.debug("Getting Header Column for file having extension: " + strFileExtension);
		List<String> lstOfHeaderColumn = new ArrayList<String>();
		FileInputStream fis = new FileInputStream(inputFile);
		LoggerUtil.debug("inputFile:   " + inputFile);
		Workbook workbook = null;

		if (strFileExtension.equalsIgnoreCase("xlsx"))
			workbook = new XSSFWorkbook(fis);
		else
			workbook = new HSSFWorkbook(fis);

		Sheet sheet = workbook.getSheetAt(0);
		Iterator<Row> rowIt = sheet.iterator();

		while (rowIt.hasNext()) {

			Row row = rowIt.next();
			Iterator<Cell> cellIterator = row.cellIterator();

			while (cellIterator.hasNext()) {
				Cell cell = cellIterator.next();
				lstOfHeaderColumn.add(cell.toString());
			}

			break;
		}
		if (workbook != null) {
			workbook.close();
		}
		fis.close();
		LoggerUtil.debug("Returning the Header Column Header for file having extension: " + strFileExtension);
		return lstOfHeaderColumn;
	}

	/**
	 * @method This method used to clickDashboardCheckbox
	 * @param driver
	 * @param attributeMap
	 * @return void
	 * @throws Exception
	 */
	@Override
	public void clickDashboardCheckbox(Driver driver, Map<String, String> attributeMap) throws Exception {

		LoggerUtil.debug("start of Selecting Checkbox and unselecting checkbox.");

		String strAction = attributeMap.get(Constants.ATTRIBUTE_DASHBOARD_CHECKBOX);
		String strRows = attributeMap.get(Constants.ATTRIBUTE_DASHBOARD_ROWS);

		if (strAction == null || strAction.isEmpty()) {
			throw new Exception("Attribute action not specified for tag checkbox.");
		}

		if (strAction.equalsIgnoreCase(Constants.STR_CHECK_ALL) || strAction.equals(Constants.STR_UNCHECK_ALL)) {
			WebElement wbElement = driver.findElement(By.xpath(
					"//div[@draggable='true']//div[@class='wux-controls-abstract wux-controls-toggle wux-controls-checkbox wux-datagridview-selection-checkbox']"));
			wbElement.click();
		} else if (strAction.equalsIgnoreCase(Constants.STR_CHECK) || strAction.equals(Constants.STR_UNCHECK)) {

			if (strRows == null || strRows.isEmpty()) {
				throw new Exception("Attribute rows cannot be null for check and uncheck.");
			}

			List<String> listOfCheckBox = Arrays.stream(strRows.split(Pattern.quote("|"))).collect(Collectors.toList());
			for (String strRowCheck : listOfCheckBox) {
				WebElement wbElement = driver.findElement(
						By.xpath("//div[@class='wux-datagridview-layouts-row-header-extension-poolcontainer']/div["
								+ strRowCheck + "]"));
				wbElement.click();
			}
		} else {
			throw new Exception(
					"Attribute action is not specified properly for tag checkbox.Possible values are checkAll,unCheckAll,unCheck,check.");
		}
		LoggerUtil.debug("End of Selecting Checkbox and unselecting checkbox.");
	}

	@Override
	public void option(Driver driver, Map<String, String> attributeMap) throws Exception {

		LoggerUtil.debug("Start of option.");
		String selectListString = attributeMap.get("selectList");
		String strXpath = Constants.STR_EMPTY_STRING;
		String strSelectBoxId = attributeMap.get("selectBoxId");

		if (null != strSelectBoxId && !strSelectBoxId.isEmpty()) {
			strXpath = "//*[@id='" + strSelectBoxId + "']";
		} else {
			strSelectBoxId = attributeMap.get("selectBoxName");
			strXpath = "//*[@name='" + strSelectBoxId + "']";
		}
		if (null == strSelectBoxId || strSelectBoxId.isEmpty()) {
			throw new Exception("id or name attribute cannot be blank");
		}
		if (null == selectListString || selectListString.isEmpty()) {
			throw new Exception(" selectList attribute cannot be blank.");
		}
		Select oSel = null;
		try {
			oSel = new Select(driver.findElement(By.xpath(strXpath)));
		} catch (Exception e) {
			throw new Exception(" Verify the id or name attribute for optional tag : " + strSelectBoxId);
		}

		List<String> selectList = Arrays.asList(selectListString.split("\\s*,\\s*"));
		if (selectList.isEmpty()) {
			throw new Exception("No any value assigned to selectList attribute.");
		}

		for (String selectItemText : selectList) {
			try {
				WebElement ele = driver
						.findElement(By.xpath(strXpath + "//option[contains(text(),'" + selectItemText + "')]"));
				oSel.selectByVisibleText(selectItemText);
				if (ele.isSelected()) {
					LoggerUtil.debug(selectItemText + " is selected.");
				} else {
					throw new Exception(selectItemText + " not selected.");
				}
			} catch (Exception e) {
				// LoggerUtil.debug(selectItemText+" not found.");
				throw new Exception(selectItemText + " not found.");
			}
		}
		LoggerUtil.debug("End of option.");
	}

	@SuppressWarnings("resource")
	@Override
	public void validateBegin(Driver driver, Map<String, String> attributeMap) throws Exception {

		LoggerUtil.debug("start in the validate Begin ");
		String strFilePath = attributeMap.get(Constants.ATTRIBUTE_FILEPATH);

		if (null == strFilePath || strFilePath.isEmpty()) {
			throw new Exception("filepath cannot be null or empty.");
		}

		File validateFile = new File(strFilePath);
		FileInputStream validateFileInputStream = new FileInputStream(validateFile);

		if (FilenameUtils.getExtension(strFilePath).equalsIgnoreCase("xls")) {
			workbookValidate = new HSSFWorkbook(validateFileInputStream);
		} else if (FilenameUtils.getExtension(strFilePath).equalsIgnoreCase("xlsx")) {
			workbookValidate = new XSSFWorkbook(validateFileInputStream);
		} else {
			throw new Exception("Please provide proper xls, xlsx file to validate file.");
		}

		validateFileInputStream.close();
		LoggerUtil.debug("End of validate Begin");
	}

	@Override
	public void validateEnd(Driver driver, Map<String, String> attributeMap) throws Exception {

		LoggerUtil.debug("start of validate end.");
		if (attributeMap != null) {
			LoggerUtil.debug("WARNING !!! This is an empty tag , there's no attribute required for this tag.");
		}

		if (null != workbookValidate) {
			workbookValidate = null;
		} else {
			throw new AssertionException("File couldnot open or please provide validatebegin before validateEnd tag.");
		}

		LoggerUtil.debug("end of validate end");
	}

	@Override
	public void validateBehaviour(Driver driver, Map<String, String> attributeMap) throws Exception {

		LoggerUtil.debug("start of validateBehaviour.");
		String strRow = attributeMap.get(Constants.ATTRIBUTE_ROW);
		String strCol = attributeMap.get(Constants.ATTRIBUTE_COL);
		String strData = attributeMap.get(Constants.ATTRIBUTE_DATA);
		boolean blRow = null != strRow && !strRow.isEmpty();
		boolean blCol = null != strCol && !strCol.isEmpty();
		int iSheet = 0;

		if (null == strData || strData.isEmpty())
			throw new Exception("data cannot be null or empty, Please provide cell data to validate.");

		if (null != attributeMap.get(Constants.ATTRIBUTE_SHEET)
				|| !attributeMap.get(Constants.ATTRIBUTE_SHEET).isEmpty())
			iSheet = Integer.parseInt(attributeMap.get(Constants.ATTRIBUTE_SHEET));

		List<String> lstOfValidateData = new ArrayList<String>(Arrays.asList(strData.split(Pattern.quote("|"))));

		if (blRow && blCol) {
			getValidatedCellData(Integer.parseInt(strCol), Integer.parseInt(strRow), iSheet, lstOfValidateData);
		} else if (blRow) {
			getAllRowData(Integer.parseInt(strRow), iSheet, lstOfValidateData);
		} else if (blCol) {
			getAllColData(Integer.parseInt(strCol), iSheet, lstOfValidateData);
		}

		LoggerUtil.debug("end of validateBehaviour");
	}

	/**
	 * @method This methos is used to validate cell data
	 * @param iCol
	 * @param iRow
	 * @param iSheet
	 * @param lstOfValidateData
	 * @throws AssertionException
	 */
	private void getValidatedCellData(int iCol, int iRow, int iSheet, List<String> lstOfValidateData)
			throws AssertionException {

		LoggerUtil.debug("start in validate the cell data");
		String strExceptionMsg = Constants.STR_EMPTY_STRING;

		try {

			Sheet sheet = workbookValidate.getSheetAt(iSheet);
			Row row = sheet.getRow(iRow);
			Cell cell = row.getCell(iCol);
			String strCellValue = cell.getStringCellValue();
			lstOfValidateData.remove(strCellValue);

			if (lstOfValidateData.size() > 0) {
				strExceptionMsg = "Validate cell data not found: " + lstOfValidateData;
				throw new Exception();
			}
		} catch (Exception ex) {

			if (strExceptionMsg.isEmpty()) {
				throw new AssertionException(
						"Incorrect input validate data of row index and col index for given sheet.");
			} else {
				throw new AssertionException(strExceptionMsg);
			}
		}

		LoggerUtil.debug("end in validate the cell data");

	}

	/**
	 * @method This method is used to validate column Data
	 * @param iCol
	 * @param iSheet
	 * @param lstOfValidateData
	 * @throws AssertionException
	 */
	private void getAllColData(int iCol, int iSheet, List<String> lstOfValidateData) throws AssertionException {

		LoggerUtil.debug("start in validate the col data");
		String strExceptionMsg = Constants.STR_EMPTY_STRING;

		try {

			List<String> lstOfExcelData = new ArrayList<String>();
			Sheet sheet = workbookValidate.getSheetAt(iSheet);

			for (int iRow = 0; iRow <= sheet.getLastRowNum(); iRow++) {

				Row row = sheet.getRow(iRow);
				if (null != row) {

					Cell cell = row.getCell(iCol);
					if (null != cell) {
						lstOfExcelData.add(cell.getStringCellValue());
					}
				}
			}

			lstOfValidateData.removeAll(lstOfExcelData);

			if (lstOfValidateData.size() > 0) {
				strExceptionMsg = "Validate col data not found: " + lstOfValidateData;
				throw new Exception();
			}

		} catch (Exception ex) {

			if (strExceptionMsg.isEmpty()) {
				throw new AssertionException("Incorrect input validate data of col index for given sheet.");
			} else {
				throw new AssertionException(strExceptionMsg);
			}
		}

		LoggerUtil.debug("end in validate the col data");
	}

	/**
	 * @method This method is used to validate Row data
	 * @param iRow
	 * @param iSheet
	 * @param lstOfValidateData
	 * @throws AssertionException
	 */
	private void getAllRowData(int iRow, int iSheet, List<String> lstOfValidateData) throws AssertionException {

		LoggerUtil.debug("start in validate the rows data");

		String strExceptionMsg = Constants.STR_EMPTY_STRING;

		try {

			List<String> lstOfExcelData = new ArrayList<String>();
			Sheet sheet = workbookValidate.getSheetAt(iSheet);
			Row rowData = sheet.getRow(iRow);
			Iterator<Cell> cellIterator = rowData.iterator();

			while (cellIterator.hasNext()) {
				Cell cell = cellIterator.next();
				lstOfExcelData.add(cell.getStringCellValue());
			}

			lstOfValidateData.removeAll(lstOfExcelData);

			if (lstOfValidateData.size() > 0) {
				strExceptionMsg = "Validate row data not found: " + lstOfValidateData;
				throw new Exception();
			}
		} catch (Exception ex) {

			if (strExceptionMsg.isEmpty()) {
				throw new AssertionException("Incorrect input validate data of row index for given sheet.");
			} else {
				throw new AssertionException(strExceptionMsg);
			}
		}

		LoggerUtil.debug("end of Validate the rows data");

	}

	/**
	 * @method This method is used to create a new dashboard and delete the previous
	 *         dashboard with same name
	 * @param input
	 */
	@Override
	public void createDashboard(Driver driver, Map<String, String> attributeMap) throws Exception {

		LoggerUtil.debug("start in createDashboard");
		WebElement wbCreatedashBoard = null;
		String strInput = attributeMap.get(Constants.ATTRIBUTE_INPUT);
		if (null == strInput || "".equalsIgnoreCase(strInput)) {
			throw new IllegalArgumentException("Invalid value for the attribute input in CreateDashboard");
		}

		String strDelete = attributeMap.get(Constants.ATTRIBUTE_DELETE);
		if (null == strDelete) {
			strDelete = Constants.CHECK_FALSE;
		}

		if (!strDelete.equalsIgnoreCase("true") && !strDelete.equalsIgnoreCase("false")) {
			LoggerUtil.debug(
					"Invalid value for the attribute delete in CreateDashboard so set to it's default value :false");
			strDelete = Constants.CHECK_FALSE;
		}
		String strWait = attributeMap.get(Constants.ATTRIBUTE_WAIT);

		String finalStrWait = getTimeOut(strWait);

		LoggerUtil.debug("polling time : " + finalStrWait);

		driver.waitForJavaScriptToLoad();

		HashMap<String, String> attributeMapForDashboardList = new HashMap<String, String>() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 2172767121600699396L;

			{
				put(Constants.LOCATOR_TYPE, Constants.INPUTTYPE_XPATH);
				put(Constants.LOCATOR_EXPRESSION,
						"//span[@class='wp-panel-button fonticon fonticon-menu new-dashboard-menu-open-btn inactive']");
				put(Constants.ATTRIBUTE_CRITERIA, "found");
				put(Constants.ATTRIBUTE_WAIT, finalStrWait);
			}
		};
		if (ifCondition(driver, attributeMapForDashboardList, null)) {
			wbCreatedashBoard = driver.findElement(By.xpath(
					"//span[@class='wp-panel-button fonticon fonticon-menu new-dashboard-menu-open-btn inactive']"));
			driver.click(wbCreatedashBoard, "js", "true");
		}

		if (strDelete.equalsIgnoreCase(Constants.CHECK_TRUE)) {

			WebElement wbsearchBox = driver.findElement(By.xpath("//input[@class='form-control']"));
			highLightElement(driver, attributeMap, wbsearchBox);
			driver.writeText(wbsearchBox, strInput, "js");
			wbsearchBox.sendKeys(Keys.ENTER);
			driver.waitForJavaScriptToLoad();

			HashMap<String, String> checkDashboard = new HashMap<String, String>() {

				/**
				 * 
				 */
				private static final long serialVersionUID = 1680808090054918236L;

				{
					put(Constants.LOCATOR_TYPE, Constants.INPUTTYPE_XPATH);
					put(Constants.LOCATOR_EXPRESSION,
							"//div[@class='dashboard-menu-list-item-text']//p[text()='" + strInput + "']");
					put(Constants.ATTRIBUTE_CRITERIA, "found");
					put(Constants.ATTRIBUTE_WAIT, finalStrWait);
				}
			};

			if (ifCondition(driver, checkDashboard, null)) {
				wbCreatedashBoard = driver.findElement(
						By.xpath("//div[@class='dashboard-menu-list-item-text']//p[text()='" + strInput + "']"));
				driver.click(wbCreatedashBoard, "js", "true");
				deletedashboard(driver);
			} else {
				LoggerUtil.debug("WARNING !! : Can't delete " + strInput + " Dashboard as " + strInput
						+ " dashboard is not found");
			}
		}

		LoggerUtil.debug("creating new dashboard");
		wbCreatedashBoard = driver
				.findElement(By.xpath("//span[@class='fonticon fonticon-plus dashboard-menu-actions-item-add']"));
		driver.click(wbCreatedashBoard, "js", "true");

		wbCreatedashBoard = driver
				.findElement(By.xpath("//input[@placeholder='Enter a name for your dashboard (optional)']"));
		driver.writeText(wbCreatedashBoard, strInput, "js");

		wbCreatedashBoard = driver.findElement(By.xpath("//button[normalize-space()='Create']"));
		driver.click(wbCreatedashBoard, "js", "true");
		LoggerUtil.debug("Create new dashboard");

		LoggerUtil.debug("End of creatDashboard");
	}

	/**
	 * @method This method is used to deactive the 6Wsearch
	 * @param wait
	 */
	@Override
	public void deactive6Wsearch(Driver driver, Map<String, String> attributeMap) throws Exception {
		LoggerUtil.debug("Start of Check Deactive 6W Search.");

		WebElement wbDropDownSearch = null;
		wbDropDownSearch = driver.findElement(By.id("searchFieldDropdown"));
		driver.click(wbDropDownSearch);

		String strWait = attributeMap.get(Constants.ATTRIBUTE_WAIT);
		String finalStrWait = getTimeOut(strWait);
		LoggerUtil.debug("polling timeout : " + finalStrWait);

		HashMap<String, String> checkdeactivatesearch = new HashMap<String, String>() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			{
				put(Constants.LOCATOR_TYPE, Constants.INPUTTYPE_XPATH);
				put(Constants.LOCATOR_EXPRESSION, "//span[@class='fonticon fonticon-checkbox-on']");
				put(Constants.ATTRIBUTE_CRITERIA, "found");
				put(Constants.ATTRIBUTE_WAIT, finalStrWait);
			}
		};

		if (ifCondition(driver, checkdeactivatesearch, null)) {
			LoggerUtil.debug("6W Search experience is Enabled");

			wbDropDownSearch = driver.findElement(By.xpath("//span[@class='fonticon fonticon-checkbox-on']"));
			driver.click(wbDropDownSearch);
			wbDropDownSearch = driver.findElement(By.xpath("//button[normalize-space()='Deactivate']"));
			driver.click(wbDropDownSearch);
			LoggerUtil.debug("6W Search experience is Deactivated");
		} else {
			wbDropDownSearch = driver.findElement(By.id("searchFieldDropdown"));
			driver.click(wbDropDownSearch);
			LoggerUtil.debug("6W Search experience is disabled");
		}
		LoggerUtil.debug("End of Check Deactivate 6W Search.");
	}

	/**
	 * This method is used for Deleting the current Dashboard
	 * 
	 * @throws Exception
	 */
	@Override
	public void deletedashboard(Driver driver) throws Exception {
		LoggerUtil.debug("Start of deleteDashboard.");
		WebElement wbelement = null;

		boolean isDisplay = driver.findElement(By.xpath("//span[@class='topbar-app-opts']")).isDisplayed();

		if (isDisplay) {
			wbelement = driver.findElement(By.xpath("//span[@class='topbar-app-opts']"));
		} else {
			wbelement = driver
					.findElement(By.xpath("//div[@class='ifwe-topbar-ddmenu-open-icon fonticon fonticon-down-open']"));
		}
		driver.click(wbelement, "js", "true");

		wbelement = driver.findElement(By.xpath("//span[normalize-space()='Delete']"));
		driver.click(wbelement, "js", "true");

		wbelement = driver
				.findElement(By.xpath("//button[@class='btn-primary btn btn-root'][normalize-space()='Delete']"));
		driver.click(wbelement, "js", "true");

		LoggerUtil.debug("End of deleteDashboard.");
	}

	public static boolean resultOfIsElementPresent(Driver driver, String[] xpaths, boolean bStatus) {
		bStatus = false;
		for (String xpath : xpaths) {
			if (isElementPresent(driver, By.xpath(xpath))) {
				bStatus = true;
				break;
			}
		}
		return bStatus;
	}

	public static boolean isElementPresent(Driver driver, By by) {
		try {
			driver.findElement(by);
			return true;
		} catch (NoSuchElementException e) {
			return false;
		}
	}

	public String getTimeOut(String strTimeOut) throws Exception {
		PropertyUtil propertyutil = PropertyUtil.newInstance();

		if (strTimeOut == null || strTimeOut.equalsIgnoreCase("")) {
			LoggerUtil.debug(
					"As wait is not initialized so taking value from '3dx-tas.execution.step.timeout' as default wait");
			strTimeOut = propertyutil.getProperty(Constants.PROPERTY_KEY_EXECUTION_STEP_TIMEOUT);
		}
		LoggerUtil.debug("TimeOut " + strTimeOut);
		if (!isNumeric(strTimeOut)) {
			throw new NumberFormatException("wait attribute is not specified properly.");
		}
		return strTimeOut;
	}

	public int getRetryCount(int iTimeOut, int iPollingInterval) throws Exception {
		if (iTimeOut < iPollingInterval) {
			LoggerUtil.debug(
					"As iTimeOut is less than the value from '3dx-tas.execution.step.timeout.PollingInterval' so iTimeOut is equals to the value of PollingInterval to get the retry count as 1ms.");
			iTimeOut = iPollingInterval;
		}
		LoggerUtil.debug("RetryCount = TimeOut / PollingInterval");
		int iRetryCount = iTimeOut / iPollingInterval;
		return iRetryCount;
	}

	private static boolean isNumeric(String str) {
		for (char c : str.toCharArray()) {
			if (!Character.isDigit(c)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * This method is for the assertion between two dates
	 * 
	 * @author SteepGraph
	 * @param driver, attributeMap, string
	 * @return void
	 * @throws Exception
	 */
	public void assertDate(ICSVUtil csvUtil, Driver driver, Map<String, String> attributeMap, String strInputText)
			throws Exception {
		LoggerUtil.debug("Start of assertDate");

		LoggerUtil.debug("input data for date 1 :" + strInputText);
		String criteria = attributeMap.get(Constants.ATTRIBUTE_CRITERIA);
		if (criteria == null || "".equals(criteria))
			throw new Exception("criteria attribute is missing for assertDate tag.");

		String toCompare = attributeMap.get(Constants.ATTRIBUTE_TOCOMPARE);
		if (toCompare == null || "".equals(toCompare))
			throw new Exception("toCompare attribute is missing for assertDate tag.");

		String dateDifference = attributeMap.get(Constants.ATTRIBUTE_DATEDIFFERENCE);
		if (dateDifference == null || "".equals(dateDifference))
			throw new Exception("dateDifference attribute is missing for assertDate tag.");

		String errorMessage = attributeMap.get(Constants.ERROR_MESSAGE);
		if (errorMessage == null || "".equals(errorMessage))
			errorMessage = "The given assertDate condition is not satisifed";

		String date1Format = attributeMap.get(Constants.ATTRIBUTE_DATE_FORMAT);
		if (date1Format == null || "".equals(date1Format))
			date1Format = PropertyUtil.STR_DEFAULT_DATE_FORMAT;
		LoggerUtil.debug("date1Format" + date1Format);

		String date2Format = attributeMap.get(Constants.ATTRIBUTE_DATE2_FORMAT);
		if (date2Format == null || "".equals(date2Format))
			date2Format = PropertyUtil.STR_DEFAULT_DATE_FORMAT;
		LoggerUtil.debug("date2Format" + date2Format);

		WebElement wbElement1 = null;
		String strDate1 = null;

		if (null != strInputText && !"".equalsIgnoreCase(strInputText)) {
			strDate1 = strInputText;
		} else {
			wbElement1 = findElement(driver, attributeMap, false);
			strDate1 = wbElement1.getText();
		}

		Date formattedDate1 = formattingDate(strDate1, date1Format);

		LoggerUtil.debug("first date : : " + formattedDate1);

		String strDate2 = null;
		WebElement wbElement2 = null;
		String strInputText2 = null;

		strInputText2 = attributeMap.get(Constants.ATTRIBUTE_DATE2_INPUT);
		if (strInputText2 == null || "".equalsIgnoreCase(strInputText2)) {
			String strAttributeKey = attributeMap.get(Constants.ATTRIBUTE_DATE2_ID);
			if (null != strAttributeKey && !"".equalsIgnoreCase(strAttributeKey)) {
				strInputText2 = csvUtil.getCell(strAttributeKey);
				strInputText2 = handler.parseREGAttribute(strInputText2, this.registerUtil);
			}
		}

		LoggerUtil.debug("input data for date 2 :" + strInputText2);

		if (null != strInputText2 && !"".equalsIgnoreCase(strInputText2)) {
			strDate2 = strInputText2;
		} else {
			String strdate2LocatorType = attributeMap.get(Constants.ATTRIBUTE_DATE2_LOCATOR_TYPE);
			LoggerUtil.debug("strlocatorType2: " + strdate2LocatorType);

			String strdate2LocatorExpression = attributeMap.get(Constants.ATTRIBUTE_DATE2_LOCATOR_EXPRESSION);
			LoggerUtil.debug("strlocatorExpression2: " + strdate2LocatorExpression);

			HashMap<String, String> attributeMapDate2 = new HashMap<String, String>() {

				/**
				 * 
				 */
				private static final long serialVersionUID = 5669548162736256412L;

				{
					put(Constants.LOCATOR_TYPE, strdate2LocatorType);
					put(Constants.LOCATOR_EXPRESSION, strdate2LocatorExpression);
				}
			};
			wbElement2 = findElement(driver, attributeMapDate2, false);
			strDate2 = wbElement2.getText();
		}

		Date formattedDate2 = formattingDate(strDate2, date2Format);

		LoggerUtil.debug("Second date : : " + formattedDate2);

		validateAssertDateCriteria(criteria, formattedDate1, formattedDate2, dateDifference, toCompare, errorMessage);
		LoggerUtil.debug("Assert condition satisfied. ");
		LoggerUtil.debug("End of assertDate");
	}

	/**
	 * This method is for the validate the assertion between two dates
	 * 
	 * @author SteepGraph
	 * @param String criteria, date1,date2, format1, format2,difference , toCompare
	 *               , errorMessage
	 * @return void
	 * @throws Exception
	 */

	private void validateAssertDateCriteria(String criteria, Date date1, Date date2, String difference,
			String toCompare, String errorMessage) throws AssertionException {
		LoggerUtil.debug("Start of validateAssertDateCriteria");

		if ("=".equalsIgnoreCase(criteria) || "!=".equalsIgnoreCase(criteria) || ">".equalsIgnoreCase(criteria)
				|| ">=".equalsIgnoreCase(criteria) || "<".equalsIgnoreCase(criteria)
				|| "<=".equalsIgnoreCase(criteria)) {

			Duration duration = Duration.between(date1.toInstant(), date2.toInstant());
			long expectedDateDiff = Long.parseLong(difference);
			long actualDateDiff = 0;

			switch (toCompare) {
			case "minutes":
				actualDateDiff = duration.toMinutes();
				break;
			case "hours":
				actualDateDiff = duration.toHours();
				break;
			case "days":
				actualDateDiff = duration.toDays();
				break;
			case "months":
				actualDateDiff = (duration.toDays() / 30);
				break;
			case "years":
				actualDateDiff = (duration.toDays() / 365);
				break;

			}

			LoggerUtil.debug("expectedDateDiff : " + expectedDateDiff);
			LoggerUtil.debug("actualDateDiff : " + actualDateDiff);
			LoggerUtil.debug("criteria : " + criteria);

			switch (criteria) {
			case "=":
				if (expectedDateDiff != actualDateDiff) {
					throw new AssertionException(errorMessage);
				}
				break;
			case "!=":
				if (expectedDateDiff == actualDateDiff) {
					throw new AssertionException(errorMessage);
				}
				break;
			case ">":
				if (actualDateDiff <= expectedDateDiff) {
					throw new AssertionException(errorMessage);
				}
				break;
			case "<":
				if (actualDateDiff >= expectedDateDiff) {
					throw new AssertionException(errorMessage);
				}
				break;
			case ">=":
				if (actualDateDiff < expectedDateDiff) {
					throw new AssertionException(errorMessage);
				}
				break;
			case "<=":
				if (actualDateDiff > expectedDateDiff) {
					throw new AssertionException(errorMessage);
				}
				break;
			}

		} else {
			throw new AssertionException("Condition does not exist in TAS.");
		}
		LoggerUtil.debug("End of validateAssertDateCriteria");
	}

	public String getTextValue(WebElement wbWlement, Map<String, String> attributeMap) {

		LoggerUtil.debug("Start of getTextValue");

		String strGetText = null;

		String attributeName = attributeMap.get(Constants.ATTRIBUTE_ATTRIBUTE);
		LoggerUtil.debug("attribute :" + attributeName);

		if (attributeName != null && !"".equalsIgnoreCase(attributeName)
				&& attributeMap.containsKey(Constants.ATTRIBUTE_ATTRIBUTE)) {
			if (attributeName.equalsIgnoreCase("dropdown")) {
				Select se = new Select(wbWlement);
				WebElement option = se.getFirstSelectedOption();
				strGetText = option.getText();
			} else {
				strGetText = wbWlement.getAttribute(attributeName);
			}
		} else {
			strGetText = wbWlement.getText();
		}

		LoggerUtil.debug("Value get from the Web Element is  :" + strGetText);

		LoggerUtil.debug("Ending of getTextValue");
		return strGetText;
	}

	public Date formattingDate(String date, String format) throws ParseException {
		LoggerUtil.debug("Start of formattingDate");

		LoggerUtil.debug("date :" + date);
		LoggerUtil.debug("format :" + format);

		SimpleDateFormat df = new SimpleDateFormat(format);
		Date dateObject = df.parse(date);

		LoggerUtil.debug("Ending of formattingDate");
		return dateObject;

	}

	protected void validateFileDownloading(Driver driver, int iFilesCountBeforeDownload, String strFilePath,
			Map<String, String> attributeMap, String strWait) throws Exception {

		String strCheckDownloadFile = attributeMap.getOrDefault(Constants.ATTRIBUTE_CHECK_DOWNLOAD_FILE,
				Constants.CHECK_FALSE);
		LoggerUtil.debug("checkDownloadFile: " + strCheckDownloadFile);

		LoggerUtil.debug("downloadedFilePath attribute used, proceeding for Downloaded File validation");
		File parentDir = null;
		if (!attributeMap.containsKey(Constants.ATTRIBUTE_ID) && !attributeMap.containsKey(Constants.ATTRIBUTE_INPUT)) {
			PropertyUtil propertyutil = PropertyUtil.newInstance();
			strFilePath = propertyutil.getProperty(Constants.PROPERTY_KEY_DEFAULT_DOWNLOAD_FILE_PATH);
			parentDir = new File(strFilePath);
		} else {
			parentDir = new File(strFilePath).getParentFile();
		}

		int waitTime = Integer.parseInt(getTimeOut(strWait));
		PropertyUtil propertyutil = PropertyUtil.newInstance();
		String strPollingInterval = propertyutil
				.getProperty(Constants.PROPERTY_KEY_EXECUTION_STEP_TIMEOUT_POLLINGINTERVAL);
		int iPollingInterval = Integer.parseInt(strPollingInterval);
		LoggerUtil.debug("timeout : " + waitTime);

		int iRetryCount = getRetryCount(waitTime, iPollingInterval);
		LoggerUtil.debug("polling time : " + iRetryCount);

		int i = 0;

		LoggerUtil.debug("Waiting for files to be downloaded...");
		while (i < waitTime && iFilesCountBeforeDownload >= parentDir.listFiles().length) {
			Thread.sleep(iRetryCount);
			i += iRetryCount;
		}

		int iFilesCountAfterDownload = parentDir.listFiles().length;
		LoggerUtil.debug("Total File Contains after starting downloading = " + iFilesCountAfterDownload);

		if (iFilesCountAfterDownload == iFilesCountBeforeDownload) {
			throw new Exception("File not downloading");
		}
		if (strCheckDownloadFile.equalsIgnoreCase("true")) {
			assertFileExists(driver, attributeMap, strFilePath);
		} else {
			LoggerUtil.debug(
					"WARNING!!! checkDownloadFile attribute is false so, can't proceed further for Downloaded File validation");
		}
	}

	public void validateLocatorType(String strLocatorType) throws IllegalArgumentException {
		LoggerUtil.debug("Start of validate LocatorType");

		LoggerUtil.debug("LocatorType :" + strLocatorType);
		try {
			strLocatorType = strLocatorType.toLowerCase();
		} catch (Exception e) {
			throw new IllegalArgumentException("Locator type = " + strLocatorType + " is not valid." + e);
		}

		switch (strLocatorType) {
		case "xpath":
			break;
		case "cssselector":
			break;
		case "name":
			break;
		case "id":
			break;
		default:
			throw new IllegalArgumentException("Locator type = " + strLocatorType + " is not valid.");

		}
		LoggerUtil.debug("End of validate LocatorType");
	}
}