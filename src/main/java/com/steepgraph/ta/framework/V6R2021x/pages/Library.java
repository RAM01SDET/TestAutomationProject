package com.steepgraph.ta.framework.V6R2021x.pages;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;

import com.steepgraph.ta.framework.Constants;
import com.steepgraph.ta.framework.common.AssertionException;
import com.steepgraph.ta.framework.common.interfaces.IHandler;
import com.steepgraph.ta.framework.common.pages.Driver;
import com.steepgraph.ta.framework.enums.IndentedTableCriteria;
import com.steepgraph.ta.framework.utils.interfaces.ICommonUtil;
import com.steepgraph.ta.framework.utils.pages.LoggerUtil;
import com.steepgraph.ta.framework.utils.pages.PropertyUtil;
import com.steepgraph.ta.framework.utils.pages.RegisterObjectUtil;

public class Library extends com.steepgraph.ta.framework.V6R2020x.pages.Library {

	public Library(IHandler handler, RegisterObjectUtil registerUtil, PropertyUtil propertyUtil, ICommonUtil commonUtil)
			throws Exception {
		super(handler, registerUtil, propertyUtil, commonUtil);
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
			LoggerUtil.debug(strSearchInputText + " Text to search entered into search input field.");

			WebElement wbElementGlobalSearchSubmit = driver.findElement(By.xpath(
					"//div[@class='run_ctn_search ctn_search']//div[@class='run_btn_search fonticon fonticon-search']"));
			LoggerUtil.debug("wbElementGlobalSearchSubmit " + wbElementGlobalSearchSubmit.getText());

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

	@Override
	public void openCompassApp(Driver driver, Map<String, String> attributeMap) throws Exception {
		LoggerUtil.debug("Start of openCompassApp.");

		switchToDefaultContent(driver);
		String strAppName = attributeMap.get(Constants.APPNAME);
		String strAppIndex = attributeMap.get("appIndex");
		String strWait = attributeMap.get(Constants.ATTRIBUTE_WAIT);

		if (strAppName == null || strAppName.isEmpty()) {
			throw new Exception("Attribute AppName not specified.");
		}
		String finalStrWait = getTimeOut(strWait);
		LoggerUtil.debug("Wait : " + finalStrWait);
		try {
			Integer.parseInt(finalStrWait);
		} catch (NumberFormatException e) {
			throw new Exception(" wait attribute is not specified properly. " + e);
		}

		attributeMap.put(Constants.LOCATOR_TYPE, Constants.INPUTTYPE_XPATH);
		attributeMap.put(Constants.LOCATOR_EXPRESSION,
				"//i[@class='compass-nav-search-icon compass-search fonticon fonticon-search active']");
		attributeMap.put(Constants.ATTRIBUTE_WAIT, finalStrWait);
		attributeMap.put(Constants.ATTRIBUTE_CRITERIA, "visible");

		boolean isSearchCommandVisible = ifCondition(driver, attributeMap, null);

		// Open Compass

		if (!isSearchCommandVisible) {
			WebElement wbCompass = driver.findElement(By.xpath("//div[contains(@class,'compass-small')]"));
			driver.click(wbCompass);
		}

		attributeMap.put(Constants.ATTRIBUTE_TIME, finalStrWait);
		attributeMap.put(Constants.ATTRIBUTE_AFTER_TIME, "pass");
		attributeMap.put("for", "element");
		wait(driver, attributeMap);

		if (!isSearchCommandVisible) {
			WebElement wbSearch = driver.findElement(
					By.xpath("//i[@class='compass-nav-search-icon compass-search fonticon fonticon-search']"));
			driver.click(wbSearch);
		}

		// <InputText locatorType="xpath"
		// locatorExpression="//input[@placeholder='Search Apps']" />
		attributeMap.put(Constants.ATTRIBUTE_MODE, "selenium");
		attributeMap.put(Constants.LOCATOR_EXPRESSION,
				"//div[contains(@class,'compass-search')]//input[contains(@class,'compass-search-text')]");
		inputText(driver, attributeMap, strAppName);
		wait(driver, attributeMap);

		if (strAppIndex == null || strAppIndex.equalsIgnoreCase("")) {
			strAppIndex = "1";
		}

		WebElement wbAppEl;
		int exeCnt = 0;
		while (exeCnt < 3) {
			try {
				wbAppEl = driver.findElement(By.xpath(
						"//div[contains(@class,'my-apps-section')]//div[contains(@class,'item-view-v3 app-item drag-app')][@data-search='"
								+ strAppName + "'][" + strAppIndex + "]/div[text()='" + strAppName + "']"));
				driver.waitUntil(ExpectedConditions.visibilityOf(wbAppEl));
				driver.click(wbAppEl);
				break;
			} catch (StaleElementReferenceException e) {
				// TODO: handle exception
			}
			exeCnt++;
		}
		LoggerUtil.debug("End of openCompassApp.");

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
			wbPinDb = driver.findElement(By.cssSelector("[class='preview-icon fonticon fonticon-pin']"));
		} else {
			wbPinDb = driver.findElement(By.cssSelector("[class='preview-header-icon fonticon fonticon-pin']"));
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
				By.xpath("//li[@class='select-box']//label[text()='Access Role']/following-sibling::*[1]"));
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
	 * This method selects indented table rows provided the text of the row as an
	 * input in csv
	 * 
	 * @author Steepgraph Systems
	 * @param driver
	 * @param attributeMap
	 * @throws Exception
	 */
	@Override
	public void selectIndentedTableRowByText(Driver driver, Map<String, String> attributeMap) throws Exception {
		LoggerUtil.debug("Start of selectIndentedTableRowByText.");
		String rowSelectionText = attributeMap.get(Constants.INPUTTYPE_TEXT);
		String tableSection = attributeMap.get(Constants.ATTRIBUTE_SECTION);
		LoggerUtil.debug("rowText: " + rowSelectionText);
		String expand = attributeMap.get(Constants.ATTRIBUTE_EXPAND);
		LoggerUtil.debug("expand: " + expand);
		String dialog = attributeMap.get(Constants.ATTRIBUTE_DIALOG);
		LoggerUtil.debug("dialog: " + dialog);
		String valueLink = attributeMap.get(Constants.ATTRIBUTE_VALUELINK);
		LoggerUtil.debug("valueLink : " + valueLink);

		if (expand == null || "".equals(expand))
			expand = Constants.CHECK_TRUE;
		if (dialog == null || "".equals(dialog))
			dialog = Constants.CHECK_FALSE;
		if (valueLink == null || "".equals(valueLink))
			valueLink = Constants.CHECK_FALSE;
		WebElement weParentElement = null;
		WebElement weRowCheckBox = null;
		if (rowSelectionText == null || "".equals(rowSelectionText))
			throw new Exception(
					"row selection text is not defined in csv column whose name matches with id of selectIndentedTableRow tag.");
		if ("Table".equalsIgnoreCase(tableSection)) {
			String rowNum;
			if (valueLink.equalsIgnoreCase(Constants.CHECK_TRUE)) {
				rowNum = driver
						.findElement(
								By.xpath("//table[@id='bodyTable']//tr/td/a[text()='" + rowSelectionText + "']/.."))
						.getAttribute("rmbrow");
			} else {
				rowNum = driver
						.findElement(By.xpath("//table[@id='bodyTable']//tr/td[text()='" + rowSelectionText + "']"))
						.getAttribute("rmbrow");
			}
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
		} else if ("freezepan".equalsIgnoreCase(tableSection)) {
			weParentElement = driver
					.findElement(By.xpath("//table[@id='treeBodyTable']//tr/td[@title='" + rowSelectionText + "']/.."));
		} else if (valueLink.equalsIgnoreCase(Constants.CHECK_TRUE)) {
			weParentElement = driver
					.findElement(By.xpath("//table[@id='bodyTable']//tr/td/a[text()='" + rowSelectionText + "']/.."));
		} else {
			weParentElement = driver
					.findElement(By.xpath("//table[@id='bodyTable']//tr/td[text()='" + rowSelectionText + "']/.."));
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
					By.xpath("//table[@id='" + tableClass + "']//th//td/span[text()='" + strColumnLabel + "']"));
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

	@Override
	public void openSearchResult(Driver driver, Map<String, String> attributeMap, String strOpenObject)
			throws Exception {
		LoggerUtil.debug("Start of openSearchResult.");
		String colCellCnt = "";
		WebElement wbColHeader = null;
		if (strOpenObject == null || "".equalsIgnoreCase(strOpenObject))
			throw new Exception("Please provide the value for objects to be open in csv file");

		switchToDefaultContent(driver);
		LoggerUtil.debug("i/p open objects : " + strOpenObject);

		String columnName = attributeMap.get("column");
		String columnValue = attributeMap.get("value");

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

		/*
		 * To Select Table View Mode
		 */
		WebElement wbSwitchBtn = driver
				.findElement(By.xpath("//div[contains(@class, 'switcher-button')]//label[@id='switch-view-button']"));
		driver.click(wbSwitchBtn);
		WebElement wbTable = driver.findElement(By.xpath("//span[@class='fonticon fonticon-view-list']"));
		driver.click(wbTable);

		/*
		 * To Find all the Rows which contain input strOpenObject In Case of multiple
		 * element always select first match element
		 */
		List<WebElement> wbElementInputBtn = null;

		String strWait = attributeMap.get(Constants.ATTRIBUTE_WAIT);
		String finalStrWait = getTimeOut(strWait);
		LoggerUtil.debug("timeout : " + finalStrWait);

		HashMap<String, String> attributeMapForDashboardList = new HashMap<String, String>() {

			/**
			 * 
			 */
			private static final long serialVersionUID = -4048538848589698181L;

			{
				put(Constants.LOCATOR_TYPE, Constants.INPUTTYPE_XPATH);
				put(Constants.LOCATOR_EXPRESSION,
						"//div[contains(@class, 'wux-controls-abstract wux-layouts-collectionview-cell wux-layouts-datagridview-cell')]//div[text()='"
								+ strOpenObject + "']");
				put(Constants.ATTRIBUTE_CRITERIA, "found");
				put(Constants.ATTRIBUTE_WAIT, finalStrWait);
			}
		};
		if (!ifCondition(driver, attributeMapForDashboardList, null)) {
			String Count = propertyUtil.getProperty(Constants.PROPERTY_KEY_EXECUTION_STEP_RETRY_COUNT);

			for (int i = 0; i <= Integer.parseInt(Count); i++) {
				WebElement wbArrow = driver
						.findElement(By.xpath("//span[@class='wux-ui-3ds wux-ui-3ds-1x wux-ui-3ds-chevron-down ']"));
				driver.click(wbArrow);
				WebElement wbRefresh = driver.findElement(By.xpath("//li[@id='refresh_search']"));
				driver.click(wbRefresh);
				if (ifCondition(driver, attributeMapForDashboardList, null)) {
					break;
				}
			}
			if (!ifCondition(driver, attributeMapForDashboardList, null)) {

				throw new NoSuchElementException("Targeted object is not found." + strOpenObject);
			}
		}

		wbElementInputBtn = driver.findElements(By.xpath(
				"//div[contains(@class, 'wux-controls-abstract wux-layouts-collectionview-cell wux-layouts-datagridview-cell')]//div[text()='"
						+ strOpenObject + "']"));

		WebElement firstMatchWebElement = null;

		// finding position of name column : name is 1st criteria of searching object
		WebElement nameColHeader = driver.findElement(
				By.xpath("//div[@class='wux-layouts-datagridview-tweaker-container']/div[text()='Name']/../.."));
		String nameCellCnt = nameColHeader.getAttribute("cell-id");

		int nameIntPos = 0;
		try {
			nameIntPos = Integer.parseInt(nameCellCnt) - 4;
			LoggerUtil.debug("Relative position of name column from 1st column : " + nameIntPos);
		} catch (NumberFormatException e) {
			firstMatchWebElement = wbElementInputBtn.get(0);
		}

		int intPosCol = 0;
		if (columnName != null) {
			try {
				wbColHeader = driver
						.findElement(By.xpath("//div[@class='wux-layouts-datagridview-tweaker-container']/div[text()='"
								+ columnName + "']/../.."));
				colCellCnt = wbColHeader.getAttribute("cell-id");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		if (wbColHeader != null) {
			colCellCnt = wbColHeader.getAttribute("cell-id");

		} else {
			WebElement scrollDiv = driver
					.findElement(By.xpath("//div[@id='table']//div[@class='wux-scroller wux-ui-is-rendered']"));
			driver.waitUntil(ExpectedConditions.visibilityOf(scrollDiv));
			((JavascriptExecutor) driver.getWebDriver()).executeScript("arguments[0].scrollLeft += 600", scrollDiv);
			wbColHeader = driver
					.findElement(By.xpath("//div[@class='wux-layouts-datagridview-tweaker-container']/div[text()='"
							+ columnName + "']/../.."));
			colCellCnt = wbColHeader.getAttribute("cell-id");
		}
		try {
			intPosCol = Integer.parseInt(colCellCnt) - 4;
			LoggerUtil.debug("Relative position of " + columnName + " column from 1st column : " + intPosCol);
		} catch (NumberFormatException e) {
			firstMatchWebElement = wbElementInputBtn.get(0);
		}
		if (wbElementInputBtn != null && columnName != null && columnValue != null && firstMatchWebElement == null) {
			// getting all rows in right container
			List<WebElement> wbResultRows = driver.findElements2("xpath",
					"//div[@class='wux-layouts-gridengine-poolcontainer-rel']/div[@class='wux-controls-abstract wux-layouts-collectionview-cell wux-layouts-datagridview-row'][@is-visible='true']");
			LoggerUtil.debug("No. of rows in right container :" + wbResultRows.size());

			for (int i = 1; i <= wbResultRows.size(); i++) {

				if (firstMatchWebElement != null)
					break;

				// getting cells in each row of right container
				List<WebElement> wbRow = driver.findElements2("xpath",
						"//div[@class='wux-layouts-gridengine-poolcontainer-rel']/div[@class='wux-controls-abstract wux-layouts-collectionview-cell wux-layouts-datagridview-row'][@is-visible='true']["
								+ i + "]/div");

				int minCellId = 0;
				int tempCellId = 0;
				for (WebElement wbCell : wbRow) {
					tempCellId = Integer.parseInt(wbCell.getAttribute("cell-id"));

					if (minCellId == 0) {
						minCellId = tempCellId;
					} else if (minCellId > tempCellId) {
						minCellId = tempCellId;
					}
				}
				LoggerUtil.debug("minCellId for the row : " + minCellId);

				WebElement wbNameCell = null;

				if (columnName.equalsIgnoreCase("Revision")) {
					wbNameCell = driver.findElement(
							By.xpath("//div[@cell-id='" + (minCellId - 4 + intPosCol) + "'][@is-visible='true']"));
				} else if (columnName.equalsIgnoreCase("Type")) {
					wbNameCell = driver.findElement(
							By.xpath("//div[@cell-id='" + (minCellId + 1 + intPosCol) + "'][@is-visible='true']"));
				} else if (columnName.equalsIgnoreCase("Title")) {
					wbNameCell = driver.findElement(
							By.xpath("//div[@cell-id='" + (minCellId - 1 + intPosCol) + "'][@is-visible='true']"));
				} else if (columnName.equalsIgnoreCase("Name")) {
					wbNameCell = driver.findElement(
							By.xpath("//div[@cell-id='" + (minCellId - 1 + intPosCol) + "'][@is-visible='true']"));
				} else if (columnName.equalsIgnoreCase("Description")) {
					wbNameCell = driver.findElement(
							By.xpath("//div[@cell-id='" + (minCellId + intPosCol) + "'][@is-visible='true']"));
				} else {
					wbNameCell = driver.findElement(
							By.xpath("//div[@cell-id='" + (minCellId - 5 + intPosCol) + "'][@is-visible='true']"));
				}

				if (wbNameCell.findElements(By.xpath("./div")).size() > 0) {
					WebElement wbNameCellText = wbNameCell.findElement(By.xpath("./div"));
					wbNameCellText = wbNameCell.findElement(By.xpath("./div"));
					WebElement wbColCell = driver.findElement(
							By.xpath("//div[@cell-id='" + (minCellId - 1 + intPosCol) + "'][@is-visible='true']"));
					if (wbColCell.findElements(By.xpath("./div")).size() > 0) {
						WebElement wbColCellText = wbColCell.findElement(By.xpath("./div"));
						if (columnValue.equalsIgnoreCase(wbColCellText.getText())
								&& (wbNameCellText.getText().equalsIgnoreCase(strOpenObject))) {
							firstMatchWebElement = wbColCellText;
							LoggerUtil.debug("firstMatchWebElement : " + firstMatchWebElement);
							break;
						}
					}
				}
			}
		} else {
			firstMatchWebElement = wbElementInputBtn.get(0);
		}
		if (firstMatchWebElement != null) {
			LoggerUtil.debug("WebElement : " + driver.getText(firstMatchWebElement));
			/*
			 * To Open selected row in search result
			 */
			Actions actions = new Actions(driver.getWebDriver());
			actions.contextClick(firstMatchWebElement).perform();
			WebElement wbDtlDspl = driver.findElement(By.id("action_DisplayDetails"));
			driver.click(wbDtlDspl);
		} else {
			LoggerUtil.debug("Unable to find result matching the criteria.");
			throw new Exception("Unable to find result matching the criteria.");

		}
		LoggerUtil.debug("openSearchResult successful.");
		LoggerUtil.debug("End of openSearchResult.");
	}
}