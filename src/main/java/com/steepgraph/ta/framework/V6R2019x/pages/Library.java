package com.steepgraph.ta.framework.V6R2019x.pages;

import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import com.steepgraph.ta.framework.Constants;
import com.steepgraph.ta.framework.common.AssertionException;
import com.steepgraph.ta.framework.common.interfaces.IHandler;
import com.steepgraph.ta.framework.common.pages.Driver;
import com.steepgraph.ta.framework.utils.interfaces.ICommonUtil;
import com.steepgraph.ta.framework.utils.pages.LoggerUtil;
import com.steepgraph.ta.framework.utils.pages.PropertyUtil;
import com.steepgraph.ta.framework.utils.pages.RegisterObjectUtil;

public class Library extends com.steepgraph.ta.framework.V6R2017x.pages.Library {

	public Library(IHandler handler, RegisterObjectUtil registerUtil, PropertyUtil propertyUtil, ICommonUtil commonUtil)
			throws Exception {
		super(handler, registerUtil, propertyUtil, commonUtil);
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
		String username = (String) attributeMap.get("username");
		String password = (String) attributeMap.get("password");

		WebElement wbusername = null;
		boolean isElementFound = false;

		try {
			wbusername = driver.findElement(By.cssSelector("[name='username']"));
			isElementFound = true;
		} catch (Exception e) {
			// No need to handle exception to this case.
		}

		try {
			if (!isElementFound || wbusername == null || !wbusername.isDisplayed()) {
				WebElement wbIamNotLisk = driver.findElement(By.cssSelector("[class='username']"));
				driver.click(wbIamNotLisk);
			}
		} catch (Exception e) {
			// No need to handle exception to this case.
		}

		if (wbusername == null)
			wbusername = driver.findElement(By.cssSelector("[name='username']"));

		wbusername.clear();
		driver.writeText(wbusername, username);

		WebElement wbpassword = driver.findElement(By.cssSelector("[name='password']"));
		wbpassword.clear();
		driver.writeText(wbpassword, password);

		WebElement wbLogin = driver.findElement(By.cssSelector("[value='Log in']"));

		driver.click(wbLogin);
		// driver.sendKey(wbLogin, Keys.ENTER);
		LoggerUtil.debug("Login button clicked.");

		// attributeMap.put("time", "2000");
		// wait(driver, attributeMap);

		// attributeMap.put("locatorType", "xpath");
		// attributeMap.put("locatorExpression", "//li[@class='error-message' and
		// contains(text(),'Incorrect username')]");
		// attributeMap.put("criteria", "text");
		// attributeMap.put("condition", "not contains");
		// attributeMap.put("errormessage", "Incorrect username/email or password.");

		// assertTag(driver, attributeMap, "Incorrect username");

		String closeWlcom = attributeMap.get("closeWelcome");
		if (closeWlcom == null || "".equalsIgnoreCase(closeWlcom)) {
			closeWlcom = Constants.CHECK_FALSE;
		}
		LoggerUtil.debug("closeWelcomePage : " + closeWlcom);
		if (Constants.CHECK_TRUE.equalsIgnoreCase(closeWlcom)) {
			try {
				// driver.findElement(By.xpath("//div[@class='ds-coachmark-carousel']"));
				WebElement wbCheckBox = driver.findElement(By.xpath("//label[@class='ds-coachmark-checkbox']/span"));
				driver.click(wbCheckBox);
				WebElement closeBtn = driver
						.findElement(By.xpath("//span[@class='ds-coachmark-close fonticon fonticon-wrong']"));
				driver.click(closeBtn);
				LoggerUtil.debug("Welcome page closed.");
			} catch (org.openqa.selenium.TimeoutException e1) {
				// No need to handle exception to this case.
			}
		}

		try {
			driver.findElement("cssselector", "[class='compass-small-over']");
		} catch (org.openqa.selenium.TimeoutException e1) {
			try {
				driver.findElement("xpath", "//li[@class='error-message' and contains(text(),'Incorrect username')]");
				throw new AssertionException("Incorrect username/email or password.");
			} catch (org.openqa.selenium.TimeoutException e2) {
				throw new AssertionException("Unable to process UI elements for login. Please contact admin");
			}

		}

		LoggerUtil.debug("Completed logInForCas.");
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
		String strSearchType = null;

		if (attributeMap == null || !attributeMap.containsKey("id")) {
			throw new Exception("attribute id not specified for tag GlobalSearch ");
		}

		String strId = (String) attributeMap.get("id");

		if (strId == null || "".equals(strId))
			throw new Exception(" attribute id not specified for tag GlobalSearch ");
		LoggerUtil.debug("strId: " + strId);

		if (!attributeMap.containsKey("type")) {
			strType = "";
		} else {
			strType = (String) attributeMap.get("type");
			if (strType == null || "".equals(strType)) {
				strType = "";
			}
			LoggerUtil.debug("strType: " + strType);
		}

		if (!attributeMap.containsKey("searchtype")) {
			strSearchType = "search";
		} else {
			strSearchType = (String) attributeMap.get("searchtype");
			if (strSearchType == null || "".equals(strSearchType)) {
				strSearchType = "search";
			}
			LoggerUtil.debug("searchtype: " + strSearchType);
		}

		if (strSearchType.equalsIgnoreCase("advance")) {
			LoggerUtil.debug("Type selected for Advance Global search");

			WebElement wbElementGlobalSearchText = driver
					.findElement(By.xpath("//*[@id='input_search_div']/form/div[1]/input"));
			highLightElement(driver, attributeMap, wbElementGlobalSearchText);
			driver.click(wbElementGlobalSearchText);

			LoggerUtil.debug(strSearchInputText + " text to search entered into search input field.");
			WebElement wbElementAdvanceSearchButton = driver.findElement(By.xpath("//a[text()='Advanced Search']"));
			driver.click(wbElementAdvanceSearchButton, "js", "false");

			/*
			 * Advance Global Search for Particular Type [Ex : Part]
			 */
			if (strType != null && !"".equals("")) {
				driver.findElement(By.xpath("//*[@id='id_-1994706941sug']/div[2]/div/div/div/div[1]/input"))
						.sendKeys(strType);
				LoggerUtil.debug(strType + " type for Advance Global search");

				WebElement dropdown = driver.findElement(By.id("id_-1994706941sug"));
				dropdown.click();
				List<WebElement> options = dropdown.findElements(By.tagName("li"));
				for (WebElement option : options) {
					if (option.getText().equals(strType)) {
						option.click(); // click the desired option
						break;
					}
				}
			}

			WebElement wbElementNameClick = driver
					.findElement(By.xpath("//*[@id='id_1530516146sug']/div[2]/div/div/div/div[1]"));
			driver.click(wbElementNameClick);
			WebElement wbElementNameInput = driver
					.findElement(By.xpath("//*[@id='id_1530516146sug']/div[2]/div/div/div/div[1]/input"));
			highLightElement(driver, attributeMap, wbElementNameInput);
			driver.writeText(wbElementNameInput, strSearchInputText);
			driver.click(wbElementNameClick);
			LoggerUtil.debug(strSearchInputText + " text to search entered into search input field.");

			WebElement wbElementSearchButton = driver
					.findElement(By.xpath("//div[@id='search-button-container']/button"));
			highLightElement(driver, attributeMap, wbElementSearchButton);
			driver.click(wbElementSearchButton);
			LoggerUtil.debug("Click on search button");

		} else {
			LoggerUtil.debug("Type selected for Global search");

			WebElement wbElementGlobalSearchText = driver
					.findElement(By.xpath("//*[@id='input_search_div']/form/div[1]/input"));
			highLightElement(driver, attributeMap, wbElementGlobalSearchText);
			driver.click(wbElementGlobalSearchText);

			WebElement wbElementNormalSearchDropdown = driver.findElement(By.xpath("//a[text()='Search']"));
			LoggerUtil.debug("wbElementNormalSearchDropdown " + wbElementNormalSearchDropdown.getText());
			driver.click(wbElementNormalSearchDropdown, "js", "false");

			driver.writeText(wbElementGlobalSearchText, strSearchInputText);
			LoggerUtil.debug(strSearchInputText + " Text to search entered into search input field.");

			WebElement wbElementGlobalSearchSubmit = driver
					.findElement(By.xpath("//div[@id='global_ctn_search']//div[@class='run_ctn_search ctn_search']"));
			LoggerUtil.debug("wbElementGlobalSearchSubmit " + wbElementGlobalSearchSubmit.getText());

			driver.click(wbElementGlobalSearchSubmit, "js", "false");
			LoggerUtil.debug("Click on search button");

			/*
			 * Global Search for Particular Type [Ex : Part]
			 */
			if (strType != null && !"".equals("")) {
				WebElement wbElementGlobalSearchText2 = driver
						.findElement(By.xpath("//*[@id='id_-1994706941']/div[2]/div/ul"));
				List<WebElement> webElement = wbElementGlobalSearchText2.findElements(By.tagName("li"));

				for (int i = 0; i < webElement.size(); i++) {
					String type = webElement.get(i).getText();
					if (type.contains(strType)) {
						driver.click(webElement.get(i));
						break;
					}
				}
			}
		}
		LoggerUtil.debug("Global Search Successfully Done.");
		LoggerUtil.debug("End of globalSearch.");
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

		if (attributeMap == null || attributeMap.size() == 0 || !attributeMap.containsKey("commandLabel")) {
			throw new Exception("commandLabel is not defined for tag clickGlobalActionsMenu.");
		}

		String commandLabel = (String) attributeMap.get("commandLabel");
		if (commandLabel == null || "".equals(commandLabel)) {
			throw new Exception("commandLabel is empty for tag clickGlobalActionsMenu.");
		}

		LoggerUtil.debug("commandLabel: " + commandLabel);

		String[] labelArray = commandLabel.split(Pattern.quote("|"));
		int length = labelArray.length;

		LoggerUtil.debug("commandLabel length: " + length);

		WebElement wbGlobalActions = driver.findElement(By.xpath(
				"//div[@id='topbar']//div[@class='topbar-menu']/div[@class='add topbar-menu-item topbar-cmd fonticon fonticon-plus' or @class='add topbar-menu-item topbar-cmd fonticon fonticon-plus active' or @class='add topbar-menu-item topbar-cmd enabled fonticon fonticon-plus']"));
		LoggerUtil.debug("Global Actions Menu Found");

		highLightElement(driver, attributeMap, wbGlobalActions);

		driver.click(wbGlobalActions);
		LoggerUtil.debug("Global Actions Menu Clicked");

//		Actions action = new Actions(driver.getWebDriver());
		for (int i = 0; i < length; i++) {

			if (i + 1 == length) {
				WebElement weGlobalActionCommand = driver.findElement(By.xpath(
						"//div[@class='no-native-scrollbars  scroller-content']//li[@class='item topbar-menu-dd-item']/div/span[text()='"
								+ labelArray[i] + "']"));
				LoggerUtil.debug("Global Action command to Click is found");
				highLightElement(driver, attributeMap, weGlobalActionCommand);
				driver.click(weGlobalActionCommand, "js", "false");
				LoggerUtil.debug("Global Action command is Clicked");

			} else {

				WebElement weGlobalActionCommand = driver.findElement(By.xpath(
						"//div[@class='topbar-menu-dd responsive-dropdown-menu add']//li[@class='item topbar-menu-dd-item item-submenu']/div/span[text()='"
								+ labelArray[i] + "']"));
				highLightElement(driver, attributeMap, weGlobalActionCommand);
				// action.moveToElement(weGlobalActionCommand).build().perform();

			}

		}

		LoggerUtil.debug("End of clickGlobalActionsMenu.");
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

		if (attributeMap == null || !attributeMap.containsKey("title")) {
			throw new Exception("Attribute title not specified for tag clickPortalCommand.");
		}

		String sTitle = (String) attributeMap.get("title");

		if (sTitle == null || sTitle.equalsIgnoreCase("")) {
			throw new Exception("Attribute title not specified for tag clickPortalCommand.");
		}

		switchToPortalDisplayFrame(driver, attributeMap);

		WebElement wPortalCommand = driver
				.findElement(By.xpath("//div[@id='divPowerView']//td[@title='" + sTitle + "']"));
		driver.click(wPortalCommand);
		LoggerUtil.debug("End of clickPortalCommand.");
	}

	/**
	 * Method to log out of 3dspace
	 * 
	 * @author Steepgraph Systems
	 * @param driver
	 * @param attributeMap
	 * @return void
	 * @throws Exception
	 */
	@Override
	public void logOut(Driver driver, Map<String, String> attributeMap) throws Exception {
		if (isLoggedIn) {
			LoggerUtil.debug("Start of logOut.");
			String preCheck = attributeMap.get(Constants.ATTR_PRE_CHECK);
			String strIsCheck = attributeMap.get(Constants.ATTRIBUTE_ISDASHBOARD);

			if (preCheck == null && "".equals(preCheck)) {
				preCheck = Constants.CHECK_FALSE;
			}

			boolean isVisible = false;
			switchToDefaultContent(driver);
			WebElement wbProfileMenu = null;
			String strOutUser = Constants.STR_EMPTY_STRING;
			if (null == strIsCheck || !strIsCheck.equalsIgnoreCase("true")) {

				try {
					wbProfileMenu = driver.findElement(By.xpath(
							"//div[@class='profile topbar-menu-item topbar-cmd enabled fonticon fonticon-user-alt']"));
					isVisible = wbProfileMenu.isDisplayed();
				} catch (Exception e) {
					LoggerUtil.debug("Profile menu not found");
				}
				if (isVisible == false) {
					driver.getWebDriver().navigate().refresh();
					wbProfileMenu = driver.findElement(By.xpath(
							"//div[@class='profile topbar-menu-item topbar-cmd enabled fonticon fonticon-user-alt']"));
				}
				driver.click(wbProfileMenu);
				strOutUser = "Sign Out";
			} else {
				wbProfileMenu = driver.findElement(By.xpath("//div[@class='profile-picture']"));
				driver.click(wbProfileMenu);
				strOutUser = "Log Out";
			}

			WebElement wbSignOut = driver
					.findElement(By.xpath("//ul[@class='dropdown-menu-wrap']//span[text()='" + strOutUser + "']"));
			driver.click(wbSignOut);

			// commonUtilobj.releaseResources();
			LoggerUtil.debug("Completed logOut.");
		} else {
			LoggerUtil.debug("Logout Skipped as Login Failed.");
		}
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
		Thread.sleep(1000);
		WebElement wbElementFieldSelectMonth = driver.findElement(By.xpath(
				"//div[@class='calendar-container'and not(contains(@style,'visibility:hidden'))]//td[@id='tdMonth']"));
		driver.click(wbElementFieldSelectMonth, "js", "false");

		WebElement wbElementFieldSelectedMonth = driver.findElement(By.xpath(
				"//div[@class='menu-panel page'and not(contains(@style,'visibility:hidden'))and(contains(@style,'display: block'))]//li//label[starts-with(text(),'"
						+ strMonth + "')]/.."));
		driver.click(wbElementFieldSelectedMonth, "js", "false");
		LoggerUtil.debug("Date picker month selection successful.");

		WebElement wbElementFieldSelectYear = driver.findElement(By.xpath(
				"//div[@class='calendar-container'and not(contains(@style,'visibility:hidden'))]//input[@id='tdYear']"));
		driver.writeText(wbElementFieldSelectYear, strYear);

		wbElementFieldSelectYear.sendKeys(Keys.ENTER);
		LoggerUtil.debug("Date picker year selection successful.");

		int iCommaIndex = strSearchInputText.indexOf(",");

		String strDay = strSearchInputText.substring(iMonthindex, iCommaIndex).trim();
		WebElement wbElementFieldSelectDay = driver.findElement(By.xpath(
				"//div[@class='calendar-container'and not(contains(@style,'visibility:hidden'))]//td[@class='calendarBody']//td[text()='"
						+ strDay + "' and contains(@class,'day')]"));
		driver.click(wbElementFieldSelectDay, "js", "false");
		LoggerUtil.debug("Date picker day selection successful.");

		LoggerUtil.debug("End of selectDateFromDatePicker.");
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
		LoggerUtil.debug("Start of clickHomeMenu.");
		switchToDefaultContent(driver);
		WebElement homeCommand = driver.findElement(
				By.xpath("//div[@class='home topbar-menu-item topbar-cmd enabled fonticon fonticon-home-alt']"));
		driver.click(homeCommand, "js", "false");
		LoggerUtil.debug("End of clickHomeMenu.");
	}

	/**
	 * Highlight Element on Web Page
	 * 
	 * @author SteepGraph Systems
	 * @param driver
	 * @param attributeMap
	 * @return void
	 * @throws Exception
	 */
	@Override
	public void clickGlobalToolsMenu(Driver driver, Map<String, String> attributeMap) throws Exception {
		LoggerUtil.debug("Start of clickGlobalToolsMenu.");

		switchToDefaultContent(driver);

		if (attributeMap == null || attributeMap.size() == 0 || !attributeMap.containsKey("commandLabel")) {
			throw new IllegalArgumentException("commandLabel is not defined for tag clickGlobalActionsMenu.");
		}

		String commandLabel = attributeMap.get("commandLabel");
		if (commandLabel == null || "".equals(commandLabel)) {
			throw new IllegalArgumentException("commandLabel is empty for tag clickGlobalActionsMenu.");
		}

		LoggerUtil.debug("commandLabel: " + commandLabel);

		String[] labelArray = commandLabel.split(Pattern.quote("|"));
		int length = labelArray.length;

		LoggerUtil.debug("commandLabel length: " + length);
		// SELENIUM-960:added notification and Social toolbar menu : Start
		String strXPath = null;
		if (length > 0) {
			if ("user".equalsIgnoreCase(labelArray[0])) {
				strXPath = "//div[contains(@class, 'topbar-user') and contains(@class, 'topbar-user-right-menu')]";
			} else if ("profile".equalsIgnoreCase(labelArray[0])) {
				strXPath = "//div[contains(@class, 'profile') and contains(@class, 'fonticon-user-alt')]";
			} else if ("add".equalsIgnoreCase(labelArray[0])) {
				strXPath = "//div[contains(@class, 'add') and contains(@class, 'fonticon-plus')]";
			} else if ("share".equalsIgnoreCase(labelArray[0])) {
				strXPath = "//div[contains(@class, 'share') and contains(@class, 'fonticon-mail-forward-alt')]";
			} else if ("home".equalsIgnoreCase(labelArray[0])) {
				strXPath = "//div[contains(@class, 'home') and contains(@class, 'fonticon-home-alt')]";
			} else if ("help".equalsIgnoreCase(labelArray[0])) {
				strXPath = "//div[contains(@class, 'help') and contains(@class, 'fonticon-question-circle')]";
			} else if ("communities".equalsIgnoreCase(labelArray[0])) {
				strXPath = "//div[contains(@class, 'social') and contains(@class, 'fonticon-users-alt')]";
			} else if ("notifications".equalsIgnoreCase(labelArray[0])) {
				strXPath = "//div[contains(@class, 'notification') and contains(@class, 'fonticon-bell-alt')]";
			} else {
				throw new IllegalArgumentException("commandLabel is not proper");
			}
		}
		// SELENIUM-960:added notification and Social toolbar menu : End

		WebElement wbGlobalActions = driver.findElement(By.xpath(strXPath));
		LoggerUtil.debug("Global Tools Menu Found");

		highLightElement(driver, attributeMap, wbGlobalActions);

		driver.click(wbGlobalActions);
		LoggerUtil.debug("Global Tools Menu Clicked");

		if (length > 1) {
			WebElement wbGlobalActionCommand = null;
			for (int i = 1; i < length; i++) {
				wbGlobalActionCommand = driver.findElement(
						By.xpath("//div[@class='item item-template']//span[text()='" + labelArray[i] + "']"));
				if (wbGlobalActionCommand == null)
					throw new Exception("Unable to locate Global Tools Menu for command lable: " + labelArray[i]);

				LoggerUtil.debug("Element Found: " + labelArray[i]);

				highLightElement(driver, attributeMap, wbGlobalActionCommand);

				Thread.sleep(1000);
				driver.click(wbGlobalActionCommand);
				LoggerUtil.debug("Global Tools Menu Clicked" + labelArray[i]);
			}

		}

		LoggerUtil.debug("End of clickGlobalToolsMenu.");
	}

	@Override
	public void openSearchResult(Driver driver, Map<String, String> attributeMap, String strOpenObject)
			throws Exception {
		LoggerUtil.debug("Start of openSearchResult.");

		if (strOpenObject == null || "".equalsIgnoreCase(strOpenObject))
			throw new Exception("Please provide the value for objects to be open in csv file");

		switchToDefaultContent(driver);
		LoggerUtil.debug("i/p open objects : " + strOpenObject);

		/*
		 * To Check Table View Mode is selected or not
		 */
		try {
			driver.findElement(By.xpath(
					"//div[contains(@class, 'switcher-button')]//label[@id='switch-view-button']//span[@class='fonticon fonticon-view-list']"));
		} catch (Exception e) {
			WebElement wbSwtchBtn = driver.findElement(
					By.xpath("//div[contains(@class, 'switcher-button')]//label[@id='switch-view-button']"));
			driver.click(wbSwtchBtn);
			WebElement wbTbl = driver
					.findElement(By.xpath("//ul[contains(@class, 'icon-dropdown')]//li[@name='table']"));
			driver.click(wbTbl);
		}

		/*
		 * To Find all the Rows which contain input strOpenObject In Case of multiple
		 * element always select first match element
		 */
		List<WebElement> wbElementInputBtn = driver.findElements(
				By.xpath("//div[contains(@class, 'wux-datagrid-cell wux-layouts-treeview-cell') and text() = '"
						+ strOpenObject + "']"));
		WebElement firstMatchWebElement = wbElementInputBtn.get(0);
		LoggerUtil.debug("WebElement : " + firstMatchWebElement.getText());

		/*
		 * To Open selected row in search result
		 */
		Actions actions = new Actions(driver.getWebDriver());
		actions.contextClick(firstMatchWebElement).perform();
		WebElement wbDtlDspl = driver.findElement(By.xpath(
				"//div[@class='search-ctx-actions-dropdown-container dropdown-menu dropdown-menu-root dropdown dropdown-root']//ul[@class='dropdown-menu-wrap dropdown-menu-icons']//li[@id='action_DisplayDetails']"));
		driver.click(wbDtlDspl);

		LoggerUtil.debug("openSearchResult successful.");
		LoggerUtil.debug("End of openSearchResult.");

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

		WebElement wbDownload = driver.findElement(weTableRowColumn,
				By.xpath("//img[contains(@src,'../common/images/iconActionDownload.gif')]"));
		LoggerUtil.debug("wbDownload : " + wbDownload);

		downloadFile(driver, wbDownload, browserName);
		LoggerUtil.debug("End of downloadFileUsingIcon.");
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

		WebElement labelEl = driver.findElement(By.xpath("//label[text()='" + strFieldLabel + "']/.."));
		String clName = labelEl.getAttribute("class");

		WebElement weChooser;

		if (clName.contains("createLabel")) {
			weChooser = driver.findElement(
					By.xpath("//input[@fieldLabel='" + strFieldLabel + "']/following-sibling::input[@value='...']"));
		} else {
			weChooser = driver
					.findElement(By.xpath("//label[text()='" + strFieldLabel + "']/../..//input[@value='...']"));
		}

		driver.click(weChooser);

		switchToWindow(driver, attributeMap);

		LoggerUtil.debug("End of openChooser");
	}
}