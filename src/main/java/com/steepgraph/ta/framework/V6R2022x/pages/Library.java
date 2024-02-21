package com.steepgraph.ta.framework.V6R2022x.pages;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.regex.Pattern;

import org.apache.commons.io.FilenameUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;

import com.steepgraph.ta.framework.Constants;
import com.steepgraph.ta.framework.common.AssertionException;
import com.steepgraph.ta.framework.common.interfaces.IHandler;
import com.steepgraph.ta.framework.common.pages.Driver;
import com.steepgraph.ta.framework.enums.IndentedTableCriteria;
import com.steepgraph.ta.framework.utils.interfaces.ICSVUtil;
import com.steepgraph.ta.framework.utils.interfaces.ICommonUtil;
import com.steepgraph.ta.framework.utils.pages.CSVUtil;
import com.steepgraph.ta.framework.utils.pages.LoggerUtil;
import com.steepgraph.ta.framework.utils.pages.PropertyUtil;
import com.steepgraph.ta.framework.utils.pages.RegisterObjectUtil;

public class Library extends com.steepgraph.ta.framework.V6R2021x.pages.Library {

	public Library(IHandler handler, RegisterObjectUtil registerUtil, PropertyUtil propertyUtil, ICommonUtil commonUtil)
			throws Exception {
		super(handler, registerUtil, propertyUtil, commonUtil);
	}

	/**
	 * Method to pin widget to new tab in dashboard on 22x.
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
		if (pinBtnInWidget.equalsIgnoreCase(Constants.CHECK_TRUE)
				|| pinBtnInWidget.equalsIgnoreCase(Constants.CHECK_FALSE)) {
			WebElement wbPinDb = null;

			if (pinBtnInWidget.equalsIgnoreCase(Constants.CHECK_TRUE)) {
				// outside the widgets
				wbPinDb = driver.findElement(By.xpath(
						("//span[contains(@class,'pin-icon preview-header-icon ifwe-action-icon fonticon fonticon-pin clickable')]")));
			} else {
				// inside the widgets
				wbPinDb = driver.findElement(By.xpath(
						("//span[contains(@class,'pin-icon preview-icon ifwe-action-icon fonticon fonticon-pin clickable')]")));
			}
			driver.click(wbPinDb);
			driver.waitUntil(ExpectedConditions.elementToBeClickable(By.xpath("//div[@class='modal-body']//form")));

			WebElement wbDropDwn = driver
					.findElement(By.xpath("//div[@class='notification-handlers-inputs']/div[2]/div[1]"));
			driver.click(wbDropDwn);

			LoggerUtil.debug("Drop down menu opened.");

			Thread.sleep(1000);

			WebElement wbNewTab = driver.findElement(By.xpath("//li[text()='Create new tab'][@class='result-option']"));
			driver.click(wbNewTab, "js", "false");

			LoggerUtil.debug("Create new tab choosen from drop down.");

			driver.waitUntil(ExpectedConditions.presenceOfElementLocated(
					By.xpath("//li[@class='select-choice'][contains(text(),'Create new tab')]")));

			WebElement wbAddBtn = driver.findElement(By.cssSelector("[class='btn-primary btn btn-root']"));
			driver.click(wbAddBtn);
			LoggerUtil.debug("Widget added");
		} else {
			throw new Exception("true and false values does not match with give value: " + pinBtnInWidget);
		}
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
				"//div[@class='wp-tab-dd-menu dropdown-menu dropdown-menu-root dropdown dropdown-root']//ul//li[@name='deleteItem']"));
		driver.click(wbDeleteOptn);

		LoggerUtil.debug("End of deleteCurrentTabInDashboard");
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
		WebElement wbRoleNode = driver.findElement(
				By.xpath("//li[@class='select-box']//label[text()='Responsibility']/following-sibling::*[1]"));
		if (wbRoleNode.getTagName().equalsIgnoreCase("input")) {
			wbRole = driver.findElement(By.id("Role_txt"));
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
				"//div[@id='topbar']//div[@class='topbar-menu']/div[@class='add topbar-menu-item topbar-cmd enabled fonticon fonticon-plus active' or @class='add topbar-menu-item topbar-cmd enabled fonticon fonticon-plus']"));
		LoggerUtil.debug("Global Actions Menu Found");

		highLightElement(driver, attributeMap, wbGlobalActions);
		driver.click(wbGlobalActions);
		LoggerUtil.debug("Global Actions Menu Clicked");

//		Actions action = new Actions(driver.getWebDriver());
		for (int i = 0; i < length; i++) {

			if (i + 1 == length) {
				WebElement weGlobalActionCommand = driver.findElement(By.xpath(
						"//ul[@class='dropdown-menu-wrap']//li[@class='item topbar-menu-dd-item']//div//span[text()='"
								+ labelArray[i] + "']"));
				LoggerUtil.debug("Global Action command to Click is found");
				highLightElement(driver, attributeMap, weGlobalActionCommand);
				driver.click(weGlobalActionCommand, "js", "false");
				LoggerUtil.debug("Global Action command is Clicked");

			} else {

				WebElement weGlobalActionCommand = driver.findElement(By.xpath(
						"//li[@class='item topbar-menu-dd-item item-submenu']//span[text()='" + labelArray[i] + "']"));
				highLightElement(driver, attributeMap, weGlobalActionCommand);
				// action.moveToElement(weGlobalActionCommand).build().perform();
			}

		}
		LoggerUtil.debug("End of clickGlobalActionsMenu.");
	}

	@Override
	public void clickBackButton(Driver driver) throws Exception {
		LoggerUtil.debug("Start of clickBackButton.");
		WebElement wbElementBack = driver.findElement(By.xpath("//a[@class='fonticon fonticon-chevron-left']"));
		driver.click(wbElementBack);
		LoggerUtil.debug("End of clickBackButton.");
	}

	@Override
	public void clickForwardButton(Driver driver) throws Exception {
		LoggerUtil.debug("Start of clickForwardButton.");
		WebElement wbElementForward = driver.findElement(By.xpath("//a[@class='fonticon fonticon-chevron-right']"));
		driver.click(wbElementForward);
		LoggerUtil.debug("End of clickForwardButton.");
	}

	@Override
	public void clickRefreshButton(Driver driver, Map<String, String> attributeMap) throws Exception {
		LoggerUtil.debug("Start of clickRefreshButton.");
		WebElement wbElementRefresh = driver.findElement(By.xpath("//a[@title='Refresh']"));
		driver.click(wbElementRefresh);
		LoggerUtil.debug("End of clickRefreshButton.");
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
			WebElement weCommand = driver.findElement(By
					.xpath("//div[@class='menu-panel page']/div[@class='menu-content']//label[text()='" + labelArray[0]
							+ "']/../../following-sibling::div[1]//label[text()='" + labelArray[1] + "']"));

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
		/*
		 * if (strTargetWindow.equalsIgnoreCase("content")) { System.out.println("DB9");
		 * driver.waitUntil(ExpectedConditions.presenceOfElementLocated(By.cssSelector(
		 * "button[class='btn-primary']"))); }
		 */
		LoggerUtil.debug("End of openActionToolbarMenu.");
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
				strOutUser = "Log out";

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
		IndentedTableCriteria itCriteria = null;
		try {
			itCriteria = IndentedTableCriteria.valueOf(strCriteria.toLowerCase());
		} catch (Exception enumconstant) {
			throw new Exception("Not a valid criteria : " + strCriteria + " for tag CheckIndentedTableRow tag.");
		}
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

		LoggerUtil.debug("Start of selectDate 2022");

		if (!attributeMap.containsKey("id") && !attributeMap.containsKey("input")) {
			throw new Exception("id/input attribute is not defined for selectDate tag");
		}
		LoggerUtil.debug("strDate : " + strDate);

		if (strDate.equals("") || strDate == null || strDate.isEmpty()) {
			throw new Exception("id attribute value is not defined for selectDate tag");
		}
		Boolean bWrite = false;

		WebElement wbDATE = findElement(driver, attributeMap, bWrite);

		String strIsCheck = attributeMap.get(Constants.ATTRIBUTE_SELECTTAG);

		if (!strDate.isEmpty() && !isValidDateFormat(strDate)) {
			throw new Exception(strDate
					+ " is not matching with required date format. Date format should be 'Month Day, Year' Eg. May 6, 2018");
		}
		if (strDate.isEmpty()) {
			driver.click(wbDATE);

		}
		if (!strDate.isEmpty()) {
			selectDateFromDatePicker(driver, strDate, wbDATE, strIsCheck);
		}
		LoggerUtil.debug("End of selectDate ");
	}

	public void selectDateFromDatePicker(Driver driver, String strSearchInputText, WebElement wbElement,
			String strIsCheck) throws Exception {
		LoggerUtil.debug("Start of selectDateFromDatePicker.");

		if (strIsCheck == null && "".equals(strIsCheck)) {
			strIsCheck = Constants.CHECK_FALSE;
		}
		int iYearIndex = strSearchInputText.indexOf(",");
		String strYear = strSearchInputText.substring(iYearIndex + 1).trim();
		LoggerUtil.debug("strYear: " + strYear);

		int iMonthindex = strSearchInputText.indexOf(" ");
		String strMonth = strSearchInputText.substring(0, iMonthindex);
		LoggerUtil.debug("strMonth: " + strMonth);

		int iCommaIndex = strSearchInputText.indexOf(",");
		String strDay = strSearchInputText.substring(iMonthindex, iCommaIndex).trim();
		LoggerUtil.debug("strDay: " + strDay);

		driver.click(wbElement);

		if (null == strIsCheck || !strIsCheck.equalsIgnoreCase("true")) {

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

			WebElement wbElementFieldSelectDay = driver.findElement(By.xpath(
					"//div[@class='calendar-container'and not(contains(@style,'visibility:hidden'))]//td[@class='calendarBody']//td[text()='"
							+ strDay + "' and contains(@class,'day')]"));
			driver.click(wbElementFieldSelectDay, "js", "false");
			LoggerUtil.debug("Date picker day selection successful.");

		} else {

			Select selectElement = null;

			selectElement = new Select(driver.findElement(By.xpath(
					"//div[@class='datepicker datepicker-root  is-bound']//div//select[@class='datepicker-select datepicker-select-year']")));

			selectElement.selectByVisibleText(strYear);

			LoggerUtil.debug("Date picker year selection successful.");

			driver.click(wbElement);

			selectElement = new Select(driver.findElement(By.xpath(
					"//div[@class='datepicker datepicker-root  is-bound']//div//select[@class='datepicker-select datepicker-select-month']")));

			selectElement.selectByVisibleText(strMonth);

			LoggerUtil.debug("Date picker month selection" + " successful.");

			driver.click(wbElement);

			WebElement wb_Element = driver.findElement(
					By.xpath("//div[@class='datepicker datepicker-root  is-bound']//table//tbody/tr//td[@data-day='"
							+ strDay + "']"));

			driver.click(wb_Element);
			LoggerUtil.debug("Date picker day selection successful.");
		}
		LoggerUtil.debug("End of selectDateFromDatePicker.");
	}

	@Override
	public void maximizeAndMinimize(Driver driver, Map<String, String> attributeMap) throws Exception {
		LoggerUtil.debug("Start of maximizeAndMinimize");
		WebElement wbAddBtnMaxMin = null;
		String strMaxMinWidget = attributeMap.get(Constants.ATTRIBUTE_MAXIMIZE_MINIMIZE_WIDGET);
		if (strMaxMinWidget == null || "".equalsIgnoreCase(strMaxMinWidget)) {
			strMaxMinWidget = Constants.CHECK_FALSE;
		}
		String strAppName = attributeMap.get(Constants.APPNAME);
		if (strAppName == null || "".equalsIgnoreCase(strAppName)) {
			throw new Exception(" APPNAME is not define for this tag");
		}
		if (strMaxMinWidget != null && "true".equalsIgnoreCase(strMaxMinWidget)) {

			wbAddBtnMaxMin = driver.findElement(By.xpath(
					"//ul[@class='wp-tablist']//li[@class='wp-tab wp-drop selected']//span[contains(text(),'New Tab')]"));
			String tabName = driver.getText(wbAddBtnMaxMin);

			if (tabName.equalsIgnoreCase(tabName))
				driver.getWebDriver().navigate().refresh();
			wbAddBtnMaxMin = driver.findElement(
					By.xpath("(//ul[@class='wp-tablist']//li[@class='wp-tab wp-drop selected']//span[text()='" + tabName
							+ "']//following::div[@class='moduleHeader__title' and  contains(text() ,'" + strAppName
							+ "')]//parent::div[@class='moduleHeader']//span[contains(@class,'fullscreen-icon ifwe-action')])[1]"));

		} else {
			wbAddBtnMaxMin = driver.findElement(By.xpath(
					"//div[@class='topbar-extrabtn']//div[contains(@class,'topbar-cmd extrabtn fonticon fullscreen-off')]"));
		}
		if (!wbAddBtnMaxMin.isDisplayed()) {
			throw new NoSuchElementException("element is not visible due to multiple dashboard tabs opened");
		}
		driver.waitUntil(ExpectedConditions.visibilityOf(wbAddBtnMaxMin));
		driver.click(wbAddBtnMaxMin);
		LoggerUtil.debug("End of maximizeAndMinimize");
	}

	@Override
	public void validateBegin(Driver driver, Map<String, String> attributeMap) throws Exception {

		LoggerUtil.debug("start in the validate Begin ");

		String strFilePath = attributeMap.get(Constants.ATTRIBUTE_FILEPATH);
		if (strFilePath == null || strFilePath.isEmpty()) {
			throw new IllegalArgumentException("Filepath attribute can not be null or empty.");
		}
		LoggerUtil.debug("Given file path is : " + strFilePath);

		if (!new File(strFilePath).exists()) {
			throw new FileNotFoundException("File not found, Please check your file path..");
		}

		FileInputStream validateFileInputStream = null;
		try {
			validateFileInputStream = new FileInputStream(strFilePath);
			if (FilenameUtils.getExtension(strFilePath).equalsIgnoreCase("xls")) {
				workbookValidate = new HSSFWorkbook(validateFileInputStream);
			} else if (FilenameUtils.getExtension(strFilePath).equalsIgnoreCase("xlsx")) {
				workbookValidate = new XSSFWorkbook(validateFileInputStream);
			} else {
				throw new Exception("Please provide proper xls, xlsx file to validate file.");
			}
		} catch (IOException e) {
			throw new IOException("Error reading file: " + strFilePath, e);
		} finally {
			if (validateFileInputStream != null) {
				try {
					validateFileInputStream.close();
				} catch (IOException e) {
					LoggerUtil.debug("Error closing file input stream." + e);
				}
			}
		}
		LoggerUtil.debug("End of validate Begin");
	}

	public void validateEnd(Driver driver) throws Exception {

		LoggerUtil.debug("start of validate end.");

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
		if (strRow == null || strRow.isEmpty()) {
			throw new IllegalArgumentException("row attribute cannot be null or empty: " + strRow);
		}
		LoggerUtil.debug("Given row value is : " + strRow);

		String strCol = attributeMap.get(Constants.ATTRIBUTE_COL);
		if (strCol == null || strCol.isEmpty()) {
			throw new IllegalArgumentException("Col attribute cannot be null or empty: " + strCol);
		}
		LoggerUtil.debug("Given col value is : " + strCol);

		String strData = attributeMap.get(Constants.ATTRIBUTE_DATA);
		if (null == strData || strData.isEmpty()) {
			throw new Exception("data cannot be null or empty, Please provide cell data to validate.");
		}
		LoggerUtil.debug("Given data is : " + strData);

		boolean blRow = null != strRow && !strRow.isEmpty();
		boolean blCol = null != strCol && !strCol.isEmpty();
		int iSheet = 0;

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

	@Override
	public void validateTableExport(Driver driver, Map<String, String> attributeMap) throws Exception {
		LoggerUtil.debug("Start of validateTableExport.");
		// Commenting switchtocontentframe as the frame is diffrerent for every scenario
		// switchToContentFrame(driver, attributeMap);

		WebElement toolBtn = null;

		String strElementID = attributeMap.get(Constants.ATTRIBUTE_REFID_FOR_TOOL);
		LoggerUtil.debug("refid:" + strElementID);
		if (strElementID == null || "".equalsIgnoreCase(strElementID)) {

			try {
				toolBtn = driver.findElement(By.xpath(
						"//div[@id='divToolbar']//td[@title='Tools']/img[@src='../common/images/iconSmallAdministration.png']"));
			} catch (Exception e) {
				// This xpath is changed in 22xfd06
				toolBtn = driver.findElement(By.xpath("//div[@id='divToolbar']//td[@title='Tools']/img"));
			}
		} else {
			toolBtn = webElementMap.get(strElementID);
		}

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
				// TODO: handle exception
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
			waitTime = Integer.parseInt(wTime);
		}
		Thread.sleep(waitTime);

		if (attributeMap == null || attributeMap.size() == 0 || !attributeMap.containsKey(Constants.ATTRIBUTE_HEADER)) {
			throw new Exception("headerList is not defined for tag validateExport.");
		}

		int skipCount;
		if (sCount == null || "".equals(sCount))
			throw new Exception("Attribute skipcount is not define for validateExport tag.");
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
		LoggerUtil.debug("Start of globalSearch. 2020x");

		switchToDefaultContent(driver);

		String strType = null;
		String strSearchType = null;

		if (attributeMap == null || (!attributeMap.containsKey("id") && !attributeMap.containsKey("input"))) {
			throw new Exception("attribute id not specified for tag GlobalSearch ");
		}

		if (strSearchInputText == null || "".equals(strSearchInputText))
			throw new Exception(" attribute id/input not specified for tag GlobalSearch ");
		LoggerUtil.debug("Id/input: " + strSearchInputText);

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

			WebElement wbElementGlobalSearchText = driver.findElement(By.xpath("//div[@id='searchFieldDropdown']"));
			highLightElement(driver, attributeMap, wbElementGlobalSearchText);
			driver.click(wbElementGlobalSearchText);

			LoggerUtil.debug(strSearchInputText + " text to search entered into search input field.");
			WebElement wbElementAdvanceSearchButton = driver
					.findElement(By.xpath("//span[@class='item-text' and text()='Advanced Search']"));
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
			driver.click(wbElementNameClick, "js", "false");

			WebElement wbElementNameInput = driver
					.findElement(By.xpath("//*[@id='id_1530516146sug']/div[2]/div/div/div/div[1]/input"));
			driver.waitUntil(ExpectedConditions.elementToBeClickable(wbElementNameInput));
			highLightElement(driver, attributeMap, wbElementNameInput);
			driver.writeText(wbElementNameInput, strSearchInputText, "js");
			WebElement wbName = driver.findElement(By.xpath("//div[@class='predicate-header' and @Title='Name']"));
			highLightElement(driver, attributeMap, wbName);
			driver.click(wbName, "js", "false");

			LoggerUtil.debug(strSearchInputText + " text to search entered into search input field.");

			WebElement wbElementSearchButton = driver
					.findElement(By.xpath("//div[@id='search-button-container']/button"));
			highLightElement(driver, attributeMap, wbElementSearchButton);
			driver.click(wbElementSearchButton);
			LoggerUtil.debug("Click on search button");

		} else {
			LoggerUtil.debug("Type selected for Global search");

			WebElement wbElementGlobalSearchText = driver.findElement(By.xpath("//div[@id='searchFieldDropdown']"));
			highLightElement(driver, attributeMap, wbElementGlobalSearchText);
			driver.click(wbElementGlobalSearchText);
			WebElement wbElementSearchText = driver
					.findElement(By.xpath("//*[@id='input_search_div']/form/div[1]/input"));

			WebElement wbElementNormalSearchDropdown = driver
					.findElement(By.xpath("//span[@class='item-text' and text()='Search']"));
			LoggerUtil.debug("wbElementNormalSearchDropdown " + wbElementNormalSearchDropdown.getText());
			driver.click(wbElementNormalSearchDropdown, "js", "false");

			driver.writeText(wbElementSearchText, strSearchInputText);
			highLightElement(driver, attributeMap, wbElementSearchText);
			LoggerUtil.debug(strSearchInputText + " Text to search entered into search input field.");

			WebElement wbElementGlobalSearchSubmit = driver.findElement(By.xpath(
					"//div[@class='run_ctn_search ctn_search']//div[@class='run_btn_search fonticon fonticon-search']"));

			driver.click(wbElementGlobalSearchSubmit, "js", "false");
			LoggerUtil.debug("Click on search button");

			/*
			 * Global Search for Particular Type [Ex : Part]
			 */
			if (strType != null && !"".equals("")) {
				WebElement wbElementShowAll = driver
						.findElement(By.xpath("//div[@class='show-more type-show-more' and text()='Show all']"));
				boolean isShowAllVisible = wbElementShowAll.isDisplayed();
//                WebElement wbElementShowLess = driver.findElement(By.xpath("//div[@class='show-more type-show-more' and text()='Show less']"));
//                boolean isShowLessVisible = wbElementShowAll.isDisplayed();
				if (isShowAllVisible) {
					driver.click(wbElementShowAll);
				}
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
}