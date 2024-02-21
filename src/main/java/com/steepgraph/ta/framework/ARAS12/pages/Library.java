package com.steepgraph.ta.framework.ARAS12.pages;

import java.util.Map;
import java.util.regex.Pattern;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.steepgraph.ta.framework.common.AssertionException;
import com.steepgraph.ta.framework.common.interfaces.IHandler;
import com.steepgraph.ta.framework.common.pages.Driver;
import com.steepgraph.ta.framework.common.pages.MasterLibrary;
import com.steepgraph.ta.framework.utils.interfaces.ICommonUtil;
import com.steepgraph.ta.framework.utils.pages.LoggerUtil;
import com.steepgraph.ta.framework.utils.pages.PropertyUtil;
import com.steepgraph.ta.framework.utils.pages.RegisterObjectUtil;

public class Library extends MasterLibrary {

	public Library(IHandler handler, RegisterObjectUtil registerUtil, PropertyUtil propertyUtil, ICommonUtil commonUtil)
			throws Exception {
		super(handler, registerUtil, propertyUtil, commonUtil);
	}

	/**
	 * Method to log in to Aras
	 * 
	 * @author Steepgraph Systems
	 * @param driver
	 * @param attributeMap
	 * @return void
	 * @throws Exception
	 */
	@Override
	public void logIn(Driver driver, Map<String, String> attributeMap, PropertyUtil propertyUtil) throws Exception {
		LoggerUtil.debug("Start of logIn.");
		String username = (String) attributeMap.get("username");
		String password = (String) attributeMap.get("password");

		WebElement wbusername = null;
		boolean isElementFound = false;

		try {
			wbusername = driver.findElement(By.cssSelector("[name='Username']"));
			isElementFound = true;
		} catch (Exception e) {
			// No need to handle exception to this case.
		}

		try {
			if (!isElementFound || wbusername == null || !wbusername.isDisplayed()) {
				WebElement wbLoginAgainLink = driver.findElement(By.cssSelector("[id='LoginAgain']"));
				driver.click(wbLoginAgainLink);
			}
		} catch (Exception e) {
			// No need to handle exception to this case.
		}

		if (wbusername == null)
			wbusername = driver.findElement(By.cssSelector("[name='Username']"));

		wbusername.clear();
		driver.writeText(wbusername, username);

		WebElement wbpassword = driver.findElement(By.cssSelector("[id='Password']"));
		wbpassword.clear();
		driver.writeText(wbpassword, password);

		WebElement wbLogin = driver.findElement(By.cssSelector("[value='Login']"));

		driver.click(wbLogin);
		LoggerUtil.debug("Login button clicked.");

		try {
			driver.findElement("xpath", "//button[contains(@class,'aras-header__navigation-button')]");
		} catch (org.openqa.selenium.TimeoutException e1) {
			try {
				driver.findElement("xpath",
						"//div[@id='AuthError' and contains(text(),'Authentication failed for admin')]");
				throw new AssertionException("Incorrect username/email or password.");
			} catch (org.openqa.selenium.TimeoutException e2) {
				throw new AssertionException("Unable to process UI elements for login. Please contact admin");
			}

		}

		LoggerUtil.debug("Completed logIn.");
	}

	/**
	 * Method to log out of Aras
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

		boolean isVisible = false;
		switchToDefaultContent(driver);
		WebElement wbProfileMenu = null;

		try {
			wbProfileMenu = driver.findElement(By.xpath("//button[@class='aras-button' and @title='Innovator Admin']"));
			isVisible = wbProfileMenu.isDisplayed();
		} catch (Exception e) {
			LoggerUtil.debug("Profile menu not found");
		}
		if (isVisible == false) {
			driver.getWebDriver().navigate().refresh();
			wbProfileMenu = driver.findElement(By.xpath("//button[@class='aras-button' and @title='Innovator Admin']"));

		}
		driver.click(wbProfileMenu);

		WebElement wbSignOut = driver
				.findElement(By.xpath("//li[@class='aras-list-item aras-list-item_arrowed']/span[text()='Logout']"));
		driver.click(wbSignOut);

		// commonUtilobj.releaseResources();
		LoggerUtil.debug("Completed logOut.");
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
		LoggerUtil.debug("Start of clickNavigationPanelMenu.");

		switchToDefaultContent(driver);

		String commandLabel = (String) attributeMap.get("commandLabel");

		if (attributeMap == null || attributeMap.size() == 0 || !attributeMap.containsKey("commandLabel")) {
			throw new Exception("commandLabel is not defined for tag clickMyDeskMenu.");
		}

		LoggerUtil.debug("commandLabel: " + commandLabel);

		String[] labelArray = commandLabel.split(Pattern.quote("|"));
		int length = labelArray.length;

		LoggerUtil.debug("commandLabel length: " + length);

		WebElement wbNavigationHeader = driver
				.findElement(By.xpath("//button[contains(@class,'aras-header__navigation-button')]"));
		driver.click(wbNavigationHeader);

		WebElement wbGlobalMenuPanel = driver.findElement(By.cssSelector("[class='aras-nav-toc']"));

		LoggerUtil.debug("aras-nav with class='aras-nav-toc' found");

		WebElement commandElement = null;
		for (int i = 0; i < length; i++) {

			if (commandElement != null)
				wbGlobalMenuPanel = commandElement;

			if (i + 1 == length) {
				WebElement weGlobalActionCommand = driver.findElement(wbGlobalMenuPanel,
						By.xpath(".//descendant::span[text() = '" + labelArray[i] + "']/.."));
				driver.click(weGlobalActionCommand);
			} else {
				commandElement = driver.findElement(wbGlobalMenuPanel,
						By.xpath(".//descendant::span[text() = '" + labelArray[i] + "']/../.."));

				String menuExpanded = commandElement.getAttribute("aria-expanded");
				LoggerUtil.debug("menuExpanded: " + menuExpanded);
				if (menuExpanded.equalsIgnoreCase("false")) {
					driver.click(commandElement);
				}
			}
		}
		LoggerUtil.debug("End of clickNavigationPanelMenu.");
	}

}
