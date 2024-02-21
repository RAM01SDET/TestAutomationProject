package com.steepgraph.ta.framework.V6R2022xFD06.pages;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
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
import com.steepgraph.ta.framework.enums.InputType;
import com.steepgraph.ta.framework.utils.interfaces.ICommonUtil;
import com.steepgraph.ta.framework.utils.pages.LoggerUtil;
import com.steepgraph.ta.framework.utils.pages.PropertyUtil;
import com.steepgraph.ta.framework.utils.pages.RegisterObjectUtil;

public class Library extends com.steepgraph.ta.framework.V6R2022x.pages.Library {

	public Library(IHandler handler, RegisterObjectUtil registerUtil, PropertyUtil propertyUtil, ICommonUtil commonUtil)
			throws Exception {
		super(handler, registerUtil, propertyUtil, commonUtil);
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
						"//li[@class='item topbar-menu-dd-item item-submenu hide-responsive-divider']//span[text()='"
								+ labelArray[i] + "']"));
				highLightElement(driver, attributeMap, weGlobalActionCommand);
				// action.moveToElement(weGlobalActionCommand).build().perform();
			}

		}
		LoggerUtil.debug("End of clickGlobalActionsMenu.");
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
		WebElement wbElement = null;
		String style = "border: 2px solid rgba(255, 0, 0, 0.8);";
		try {

			wbElement = driver.findElement(By.xpath(
					"//li[@class='wp-tab wp-drop selected']//span[@class='action fonticon fonticon-down-open']"));
			highLightElement(driver, wbElement, style);
			driver.click(wbElement);

			wbElement = driver.findElement(By.xpath(
					"//div[@class='wp-tab-dd-menu dropdown-menu dropdown-menu-root dropdown dropdown-root']//li[@name='deleteItem']"));
			highLightElement(driver, wbElement, style);
			driver.click(wbElement);

			String strWait = attributeMap.get(Constants.ATTRIBUTE_WAIT);
			String finalStrWait = getTimeOut(strWait);
			LoggerUtil.debug("polling timeout : " + finalStrWait);

			HashMap<String, String> IslastTabDeleted = new HashMap<String, String>() {
				/**
				 * 
				 */
				private static final long serialVersionUID = 1L;
				{
					put(Constants.LOCATOR_TYPE, Constants.INPUTTYPE_XPATH);
					put(Constants.LOCATOR_EXPRESSION,
							"//div[@class='modal-content']//button[@class='btn-primary btn btn-root' and text()='Delete']");
					put(Constants.ATTRIBUTE_CRITERIA, "found");
					put(Constants.ATTRIBUTE_WAIT, finalStrWait);
				}
			};

			if (ifCondition(driver, IslastTabDeleted, null)) {
				wbElement = driver.findElement(By.xpath(
						"//div[@class='modal-content']//button[@class='btn-primary btn btn-root' and text()='Delete']"));
				highLightElement(driver, wbElement, style);
				driver.waitUntil(ExpectedConditions.elementToBeClickable((wbElement)));
				driver.click(wbElement);
				try {
					wbElement = driver.findElement(By.xpath(
							"//div[@class='alert-message alert-success fade alert-closable alert-has-icon in']"));
					LoggerUtil.debug(wbElement.getText() + " it's deleted message from pop-up box.");

					wbElement = driver.findElement(By.xpath("//span[@class='close fonticon fonticon-cancel']"));
					driver.waitUntil(ExpectedConditions.elementToBeClickable((wbElement)));
					highLightElement(driver, wbElement, style);
					driver.click(wbElement);
				} catch (Exception e) {
					LoggerUtil.debug("WARNING !! Notification is not visible");
				}

			} else {
				wbElement = driver.findElement(By.xpath(
						"//div[@class='modal-content']//button[@class='btn-primary btn btn-root' and text()='OK']"));
				if (wbElement != null)
					driver.click(wbElement);
				throw new Exception("Please Note: that you cannot Delete the last tab of Dashboard.");
			}
		} catch (Exception e) {
			throw new Exception("deleteCurrentTabInDashboard tag failed to complete operation. Reason of Failure : "
					+ e.getMessage());
		}

		LoggerUtil.debug("End of deleteCurrentTabInDashboard");
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

		String commandLabel = (String) attributeMap.get("commandLabel");

		if (attributeMap == null || attributeMap.size() == 0 || !attributeMap.containsKey("commandLabel")) {
			throw new Exception("commandLabel is not defined for tag clickMyDeskMenu.");
		}

		LoggerUtil.debug("commandLabel: " + commandLabel);

		String[] labelArray = commandLabel.split(Pattern.quote("|"));
		int length = labelArray.length;

		LoggerUtil.debug("commandLabel length: " + length);

		WebElement wbGlobalMenuPanel = driver.findElement(By.xpath("//div[@id='mydeskpanel']"));

		LoggerUtil.debug("div with class='slide-in-panel menu categories my-desk footer-notice-yes' found");

		WebElement commandElement = null;
		for (int i = 0; i < length; i++) {

			if (commandElement != null)
				wbGlobalMenuPanel = commandElement;

			if (i + 1 == length) {
				WebElement weGlobalActionCommand = null;
				if (labelArray[i - 1].equals(labelArray[i])) {
					weGlobalActionCommand = driver.findElement(wbGlobalMenuPanel,
							By.xpath(".//descendant::label[text() = '" + labelArray[i] + "'][2]"));
				} else {
					weGlobalActionCommand = driver.findElement(wbGlobalMenuPanel,
							By.xpath(".//descendant::label[text() = '" + labelArray[i] + "']"));
				}
				highLightElement(driver, attributeMap, weGlobalActionCommand);
				driver.click(weGlobalActionCommand);
			} else {

				boolean isExpanded = false;

				if (i == 0) {
					commandElement = driver.findElement(wbGlobalMenuPanel,
							By.xpath(".//descendant::label[text() = '" + labelArray[i] + "']/../../.."));
				} else {
					commandElement = driver.findElement(wbGlobalMenuPanel,
							By.xpath(".//descendant::label[text() = '" + labelArray[i] + "']/../.."));
				}
				highLightElement(driver, attributeMap, commandElement);
				String commandClass = commandElement.getAttribute("class");
				LoggerUtil.debug("commandClass: " + commandClass);
				if (commandClass != null && commandClass.toLowerCase().contains("expanded")) {
					isExpanded = true;
				}

				LoggerUtil.debug("isExpanded: " + isExpanded);

				if (!isExpanded) {
					LoggerUtil.debug("\nNot Expanded. Clicking: " + labelArray[i]);
					WebElement weGlobalActionCommand = driver.findElement(wbGlobalMenuPanel,
							By.xpath(".//descendant::label[text() = '" + labelArray[i] + "']/../.."));
					driver.click(weGlobalActionCommand);
				}
			}
		}
		LoggerUtil.debug("End of clickMyDeskMenu.");
	}

	@Override
	public void openSearchResult(Driver driver, Map<String, String> attributeMap, String strOpenObject)
			throws Exception {
		LoggerUtil.debug("Start of openSearchResult.");
		String colCellCnt = "";
		boolean bStatus = false;
		int nameCellValueId = 5;
		WebElement wbColHeader = null;
		WebElement wbDtlDspl = null;

		if (!attributeMap.containsKey(Constants.ATTRIBUTE_ID) && !attributeMap.containsKey(Constants.ATTRIBUTE_INPUT)) {
			throw new IllegalArgumentException("id/input attribute is not defined properly for OpenSerachResult tag");
		}

		if (strOpenObject == null || "".equalsIgnoreCase(strOpenObject))
			throw new Exception("Please provide the value for objects to be open in csv file");

		switchToDefaultContent(driver);
		LoggerUtil.debug("i/p open objects : " + strOpenObject);

		String strCustomizedResult = attributeMap.get("customizedResult");
		if (strCustomizedResult == null || "".equalsIgnoreCase(strCustomizedResult)) {
			strCustomizedResult = "false";
		}

		if (strCustomizedResult.equalsIgnoreCase("true")
				&& (!attributeMap.containsKey("column") || !attributeMap.containsKey("value"))) {
			throw new Exception(
					"column attribute/value attribute is not defined properly ,Please define these attribute properly from NonFreezePane");
		}

		String columnName = attributeMap.get("column");
		if (!attributeMap.containsKey("column") || !attributeMap.containsKey("value")) {
			LoggerUtil.debug(
					"As column attribute or value attribute is not defined properly , both the attributes switching to its default values : column='Name' & value ="
							+ strOpenObject);
		}
		String columnValue = attributeMap.get("value");
		if ((columnValue == null || "".equals(columnValue)) && (columnName == null || "".equals(columnName))) {
			columnValue = strOpenObject;
			columnName = "Name";
		}

		WebElement wbResult;
		String count = "-1";
		int exeCnt = 0;
		while (exeCnt < 3) {
			Thread.sleep(3000);
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
		String[] xpaths = {
				"//div[contains(@class, 'wux-controls-abstract wux-layouts-collectionview-cell wux-layouts-datagridview-cell')]//div[text()='"
						+ strOpenObject + "']",
				"//div[contains(@class, 'wux-controls-abstract wux-layouts-collectionview-cell wux-layouts-datagridview-cell')]//div//span[text()='"
						+ strOpenObject + "']" };

		if (resultOfIsElementPresent(driver, xpaths, bStatus)) {
			bStatus = true;
		}
		if (!bStatus) {
			String Count = propertyUtil.getProperty(Constants.PROPERTY_KEY_EXECUTION_STEP_RETRY_COUNT);

			for (int i = 0; i <= Integer.parseInt(Count); i++) {
				WebElement wbArrow = driver
						.findElement(By.xpath("//span[@class='wux-ui-3ds wux-ui-3ds-1x wux-ui-3ds-chevron-down ']"));
				driver.click(wbArrow);
				WebElement wbRefresh = driver.findElement(By.xpath("//li[@id='refresh_search']"));
				driver.click(wbRefresh);
				resultOfIsElementPresent(driver, xpaths, bStatus);
				if (resultOfIsElementPresent(driver, xpaths, bStatus)) {
					bStatus = true;
				}
				if (bStatus) {
					break;
				}
			}
			if (!bStatus) {
				throw new NoSuchElementException("Targeted object is not found." + strOpenObject);
			}
		}
		try {
			wbElementInputBtn = driver.findElements(By.xpath(
					"//div[contains(@class, 'wux-controls-abstract wux-layouts-collectionview-cell wux-layouts-datagridview-cell')]//div[text()='"
							+ strOpenObject + "']"));
		} catch (Exception e) {
			wbElementInputBtn = driver.findElements(By.xpath(
					"//div[contains(@class, 'wux-controls-abstract wux-layouts-collectionview-cell wux-layouts-datagridview-cell')]//div//span[text()='"
							+ strOpenObject + "']"));
		}

		WebElement firstMatchWebElement = null;

		if (strCustomizedResult.equalsIgnoreCase("true")) {
			wbElementInputBtn = driver.findElements(By.xpath(
					"//div[contains(@class, 'wux-controls-abstract wux-layouts-collectionview-cell wux-layouts-datagridview-cell')]//div[text()='"
							+ strOpenObject + "']"));

			wbColHeader = driver
					.findElement(By.xpath("//div[@class='wux-layouts-datagridview-tweaker-container']/div[text()='"
							+ columnName + "']/../.."));
			String CellValue = wbColHeader.getAttribute("cell-id");
			int intCellValue = Integer.parseInt(CellValue);
			LoggerUtil.debug("Cell-Id of the given col. : " + intCellValue);

			List<WebElement> wbResultRows = driver.findElements2("xpath",
					"//div[@class='wux-layouts-gridengine-poolcontainer-rel']/div[@class='wux-controls-abstract wux-layouts-collectionview-cell wux-layouts-datagridview-row'][@is-visible='true']");
			LoggerUtil.debug("No. of rows in right container :" + wbResultRows.size());

			for (int i = 1; i <= wbResultRows.size(); i++) {

				nameCellValueId = nameCellValueId + 8;
				intCellValue = intCellValue + 8;

				WebElement wbRow = driver.findElement("xpath",
						"//div[@class='wux-layouts-gridengine-poolcontainer-rel']/div[@class='wux-controls-abstract wux-layouts-collectionview-cell wux-layouts-datagridview-row'][@is-visible='true']["
								+ i + "]//div[@cell-id='" + nameCellValueId + "']");

				if (strOpenObject.equalsIgnoreCase(wbRow.getText())) {
					firstMatchWebElement = driver.findElement("xpath",
							"//div[@class='wux-layouts-gridengine-poolcontainer-rel']/div[@class='wux-controls-abstract wux-layouts-collectionview-cell wux-layouts-datagridview-row'][@is-visible='true']["
									+ i + "]//div[@cell-id='" + intCellValue + "']");

					if (firstMatchWebElement.getText().equals(columnValue)) {
						break;
					}
				}
			}
			if (!firstMatchWebElement.getText().equals(columnValue)) {
				throw new Exception("Unable to find result matching the criteria.");
			}

		} else {

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
					wbColHeader = driver.findElement(
							By.xpath("//div[@class='wux-layouts-datagridview-tweaker-container']/div[text()='"
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
			if (wbElementInputBtn != null && columnName != null && columnValue != null
					&& firstMatchWebElement == null) {
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

					columnName = columnName.toLowerCase();
					switch (columnName) {

					case ("revision"):
						wbNameCell = driver.findElement(
								By.xpath("//div[@cell-id='" + (minCellId - 4 + intPosCol) + "'][@is-visible='true']"));
						break;

					case ("type"):
						wbNameCell = driver.findElement(
								By.xpath("//div[@cell-id='" + (minCellId + 1 + intPosCol) + "'][@is-visible='true']"));
						break;

					case ("title"):
						wbNameCell = driver.findElement(
								By.xpath("//div[@cell-id='" + (minCellId - 1 + intPosCol) + "'][@is-visible='true']"));
						break;

					case ("name"):
						wbNameCell = driver.findElement(
								By.xpath("//div[@cell-id='" + (minCellId - 1 + intPosCol) + "'][@is-visible='true']"));
						break;

					case ("description"):
						wbNameCell = driver.findElement(
								By.xpath("//div[@cell-id='" + (minCellId + intPosCol) + "'][@is-visible='true']"));
						break;

					case ("modification date"):
						wbNameCell = driver.findElement(
								By.xpath("//div[@cell-id='" + (minCellId - 2 + intPosCol) + "'][@is-visible='true']"));
						break;

					case ("creation date"):
						wbNameCell = driver.findElement(
								By.xpath("//div[@cell-id='" + (minCellId - 1 + intPosCol) + "'][@is-visible='true']"));
						break;

					default:
						wbNameCell = driver.findElement(
								By.xpath("//div[@cell-id='" + (minCellId - 6 + intPosCol) + "'][@is-visible='true']"));
						break;
					}

					if (wbNameCell.findElements(By.xpath("./div")).size() > 0) {
						WebElement wbNameCellText = null;
						if (columnName.equalsIgnoreCase("Creation Date")) {
							int tempMinCellId = minCellId - 2;
							wbNameCellText = driver.findElement(By.xpath("//div[@cell-id='"
									+ (tempMinCellId - 1 + intPosCol) + "'][@is-visible='true']/div"));
						} else if (columnName.equalsIgnoreCase("Title")) {
							int tempMinCellId = minCellId + 3;
							wbNameCellText = driver.findElement(By.xpath("//div[@cell-id='"
									+ (tempMinCellId - 1 + intPosCol) + "'][@is-visible='true']/div"));
						} else {
							wbNameCellText = wbNameCell.findElement(By.xpath("./div"));
						}
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
		}
		if (firstMatchWebElement != null) {
			LoggerUtil.debug("WebElement : " + driver.getText(firstMatchWebElement));
			/*
			 * To Open selected row in search result
			 */
			String strOpenWith = attributeMap.get(Constants.OPEN_WITH);
			Actions actions = new Actions(driver.getWebDriver());
			actions.contextClick(firstMatchWebElement).perform();
			if (!"".equalsIgnoreCase(strOpenWith) && null != strOpenWith) {
				LoggerUtil.debug("openWith :" + strOpenWith);
				WebElement Wbopenwith = driver.findElement(By.xpath(
						"//ul[@class='dropdown-menu-wrap dropdown-menu-icons']//span[@class='item-text' and text()='Open With' ]"));
				driver.click(Wbopenwith);

				try {
					wbDtlDspl = driver
							.findElement(By.xpath("//span[@class='item-text' and text()='" + strOpenWith + "']"));
				} catch (Exception e) {
					throw new NoSuchElementException(
							"Target object " + strOpenWith + " to open the searched object is not found.");
				}
			} else {
				wbDtlDspl = driver.findElement(By.id("action_DisplayDetails"));
			}
			driver.click(wbDtlDspl);
		} else {
			LoggerUtil.debug("Unable to find result matching the criteria.");
			throw new Exception("Unable to find result matching the criteria.");

		}

		LoggerUtil.debug("openSearchResult successful.");
		LoggerUtil.debug("End of openSearchResult.");
	}

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

		case hyperlinktext:
		case text:
			attributeMap.put(Constants.INPUTTYPE_TEXT, input);
			selectIndentedTableRowByText(driver, attributeMap);
			break;

		default:
			throw new Exception("criteria attribute is not valid in selectIndentedTableRow tag.");

		}
	}

	@Override
	public void selectIndentedTableRowByText(Driver driver, Map<String, String> attributeMap) throws Exception {
		LoggerUtil.debug("Start of selectIndentedTableRowByText.");
		String rowSelectionText = attributeMap.get(Constants.INPUTTYPE_TEXT);
		String tableSection = attributeMap.get(Constants.ATTRIBUTE_SECTION);
		LoggerUtil.debug("rowText: " + rowSelectionText);

		String expand = attributeMap.get(Constants.ATTRIBUTE_EXPAND);
		if (expand == null || "".equals(expand))
			throw new Exception("expand attribute is not defined for selectIndentedTableRow tag.");
		LoggerUtil.debug("expand: " + expand);

		String dialog = attributeMap.get(Constants.ATTRIBUTE_DIALOG);
		LoggerUtil.debug("dialog: " + dialog);

		String valueLink = attributeMap.get(Constants.ATTRIBUTE_VALUELINK);
		LoggerUtil.debug("valueLink : " + valueLink);

		String strWait = attributeMap.get(Constants.ATTRIBUTE_WAIT);
		String finalStrWait = getTimeOut(strWait);
		LoggerUtil.debug("timeout : " + finalStrWait);

		if (expand == null || "".equals(expand)
				|| (!expand.equalsIgnoreCase(Constants.CHECK_TRUE) && !expand.equalsIgnoreCase(Constants.CHECK_FALSE)))
			LoggerUtil.debug("WARNING!!! expand value should be true or false, it's default value is true : " + expand);
		expand = Constants.CHECK_TRUE;
		LoggerUtil.debug("expand value is set to:" + expand);

		if (dialog == null || "".equals(dialog) || (!dialog.equalsIgnoreCase(Constants.CHECK_TRUE)
				&& !dialog.equalsIgnoreCase(Constants.CHECK_FALSE))) {
			LoggerUtil
					.debug("WARNING!!! dialog value should be true or false, it's default value is false : " + dialog);
			dialog = Constants.CHECK_FALSE;
			LoggerUtil.debug("dialog value is set to:" + dialog);
		}

		if (valueLink == null || "".equals(valueLink))
			valueLink = Constants.CHECK_FALSE;
		if (tableSection == null || "".equals(tableSection)) {
			LoggerUtil.debug("section attribute set to its default value : Table");
			expand = "Table";
		}

		WebElement weParentElement = null;
		WebElement weRowCheckBox = null;
		WebElement wbElement = null;
		String rowNum = null;
		if (rowSelectionText == null || "".equals(rowSelectionText))
			throw new Exception(
					"row selection text is not defined in csv column whose name matches with id of selectIndentedTableRow tag.");
		if ("Table".equalsIgnoreCase(tableSection)) {
			LoggerUtil.debug("section value given is : " + tableSection);
			if (valueLink.equalsIgnoreCase(Constants.CHECK_TRUE)) {
				rowNum = driver
						.findElement(
								By.xpath("//table[@id='bodyTable']//tr/td/a[text()='" + rowSelectionText + "']/.."))
						.getAttribute("rmbrow");
			} else {
				if (attributeMap.get(Constants.ATTRIBUTE_CRITERIA).equalsIgnoreCase("hyperlinktext")) {

					HashMap<String, String> mExistData = new HashMap<String, String>() {
						/**
						 * 
						 */
						private static final long serialVersionUID = 2172767121600699396L;

						{
							put(Constants.LOCATOR_TYPE, Constants.INPUTTYPE_XPATH);
							put(Constants.LOCATOR_EXPRESSION, "//div[@id='mx_divTableBody']//following::span[text()='"
									+ rowSelectionText + "']//ancestor::td");
							put(Constants.ATTRIBUTE_CRITERIA, "found");
							put(Constants.ATTRIBUTE_WAIT, finalStrWait);
						}
					};
					if (ifCondition(driver, mExistData, null)) {

						wbElement = driver.findElement(By.xpath("//div[@id='mx_divTableBody']//following::span[text()='"
								+ rowSelectionText + "']//ancestor::td"));

						rowNum = wbElement.getAttribute("rmbrow");

					} else {

						wbElement = driver.findElement(
								By.xpath("//table[@id='bodyTable']//tr/td//a[text()='" + rowSelectionText + "']/.."));
						rowNum = wbElement.getAttribute("rmbrow");
					}

				} else {

					rowNum = driver
							.findElement(By.xpath("//table[@id='bodyTable']//tr/td[text()='" + rowSelectionText + "']"))
							.getAttribute("rmbrow");
				}
			}

			if (attributeMap.get(Constants.ATTRIBUTE_CRITERIA).equalsIgnoreCase("hyperlinktext")) {
				weRowCheckBox = driver.findElement(By.xpath("//table[@id='treeBodyTable']//tbody//tr[@id='" + rowNum
						+ "']//table//input[@type='checkbox']"));
			} else {
				weRowCheckBox = driver.findElement(
						By.xpath("//table[@id='treeBodyTable']//tr[@id='" + rowNum + "']/td/input[@type='checkbox']"));
			}
		} else if ("freezepan".equalsIgnoreCase(tableSection)) {
			LoggerUtil.debug("section value given is : " + tableSection);
			if (dialog.equalsIgnoreCase(Constants.CHECK_TRUE)) {
				weRowCheckBox = driver.findElement(By.xpath("//table[@id='treeBodyTable']//tr/td[@rmbid='"
						+ rowSelectionText + "']//input[@type='checkbox']"));
			} else {
				weRowCheckBox = driver.findElement(By.xpath("//table[@id='treeBodyTable']//tr/td[@title='"
						+ rowSelectionText + "']/../td//input[@type='checkbox']"));
			}
		} else {
			throw new IllegalArgumentException(
					"section attribute is not defined properly for SelectIndentedTableRow tag. Possible values are : table/freezepan, Given value is :"
							+ tableSection);
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
		} else if (attributeMap.get(Constants.ATTRIBUTE_CRITERIA).equalsIgnoreCase("hyperlinktext")) {
			try {
				weParentElement = driver.findElement(By.xpath("//div[@id='mx_divTableBody']//following::span[text()='"
						+ rowSelectionText + "']//ancestor::td"));
				System.out.println(weParentElement);
			} catch (Exception e) {
				weParentElement = driver.findElement(
						By.xpath("//table[@id='bodyTable']//tr/td//a[text()='" + rowSelectionText + "']/.."));
			}

		} else {
			weParentElement = driver
					.findElement(By.xpath("//table[@id='bodyTable']//tr/td[text()='" + rowSelectionText + "']/.."));
		}

		LoggerUtil.debug("weParentElement : " + weParentElement);

		String strRowCheckboxId = rowNum;
		if (null == strRowCheckboxId || "".equals(strRowCheckboxId)) {
			strRowCheckboxId = weParentElement.getAttribute(Constants.ATTRIBUTE_ID);
		}
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

		if (strIsNew == null || "".equals(strIsNew) || (!strIsNew.equalsIgnoreCase(Constants.CHECK_TRUE)
				&& !strIsNew.equalsIgnoreCase(Constants.CHECK_FALSE))) {
			strIsNew = Constants.CHECK_FALSE;
			LoggerUtil.debug("IsNew attribute value should be true or false, Default value is false: " + strIsNew);
		}
			

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
	
	@Override
	public void expandIndentedtableRow(Driver driver, WebElement weParentRow, String strRowId) throws Exception {
		LoggerUtil.debug("Row start expanding");
		WebElement weExpandImg = null;
		WebElement weParentRowTemp = weParentRow;
		weParentRowTemp = driver.findElement(By.xpath("//table[@id='treeBodyTable']/tbody/tr[@id='" + strRowId + "']"));
		weExpandImg = driver.findElement(weParentRowTemp, By.xpath(".//descendant::img[@id='img_" + strRowId + "']"));
		LoggerUtil.debug("weExpandImg: " + weExpandImg);
		String strImgAttr = weExpandImg.getAttribute("src");
		LoggerUtil.debug("strImgAttr : " + strImgAttr);
		if (strImgAttr != null && strImgAttr.contains("images/utilTreeLineNodeClosedSB.gif")) {
			driver.click(weExpandImg);
		}
		LoggerUtil.debug("Row is expanded");
	}

	@Override
	public void clickRefreshButton(Driver driver, Map<String, String> attributeMap) throws Exception {
		LoggerUtil.debug("Start of clickRefreshButton.");
		WebElement wbElementRefresh = null;
		String target = attributeMap.get(Constants.ATTRIBUTE_TARGET);
		LoggerUtil.debug("target : " + target);
		if (target != null && target.equalsIgnoreCase("header")) {
			LoggerUtil.debug("Clicking on the refresh button of widget header without Pin to dashboard");
			try {
				wbElementRefresh = driver.findElement(By.xpath(
						"//span[@class='refresh-icon preview-icon ifwe-action-icon fonticon fonticon-refresh clickable']"));
			} catch (Exception e) {
				LoggerUtil.debug(
						"Not able to Clicking on the refresh button of widget header as widget already pin to dashboard");
			}
		} else if (target != null && target.equalsIgnoreCase("tab")) {
			LoggerUtil.debug("Clicking on the refresh button on tab in dashboard");
			try {
				WebElement wbDropDwnBtn = driver.findElement(By.xpath(
						"//span[@class='widget-menu-icon ifwe-action-icon fonticon fonticon-down-open clickable']"));
				driver.click(wbDropDwnBtn);
				wbElementRefresh = driver.findElement(By.xpath("//li[@id='refresh']"));
			} catch (Exception e) {
				throw new Exception(
						"Not able to Clicking on the refresh button on tab as widget is not pin to dashboard");
			}
		} else {
			wbElementRefresh = driver.findElement(By.xpath("//a[@title='Refresh']"));
		}
		driver.click(wbElementRefresh);
		LoggerUtil.debug("End of clickRefreshButton.");
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
		String strDashboard = attributeMap.get(Constants.ATTRIBUTE_ISDASHBOARD);
		if (strDashboard == null || "".equalsIgnoreCase(strDashboard)) {
			strDashboard = "false";
		}
		LoggerUtil.debug("isDashboard : " + strDashboard);
		LoggerUtil.debug("i/p selection : " + strSelection);
		LoggerUtil.debug("i/p select object/s based on criteria : " + selectObjects);
		if ((Constants.SELECTION_SINGLE.equalsIgnoreCase(strSelection)
				|| Constants.SELECTION_MULTIPLE.equalsIgnoreCase(strSelection))) {
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
					// TODO: handle exception
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

			if ((selectObjects == null || "".equalsIgnoreCase(selectObjects))) {
				throw new Exception(
						"Please provide any of ID or Input attribute, id value it will take from CSV value, input attribute will take hard-coded value.");
			}

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
				if (strDashboard.equalsIgnoreCase("true")) {
					clickCheckBoxForDashboard(driver, selectObjects, colName, colValue, attributeMap);
				} else {
					clickCheckBox(driver, selectObjects, colName, colValue, attributeMap);
				}
			}
			WebElement wbElementSubmitBtn = null;

			if (strDashboard.equalsIgnoreCase("true")) {
				wbElementSubmitBtn = driver.findElement(By.xpath(
						"//div[@class='buttonContainer footer_button']//button[text()='" + strSubmitLabel + "']"));
			} else {
				wbElementSubmitBtn = driver.findElement(
						By.xpath("//div[@id='searchResultsContainer']//button[text()='" + strSubmitLabel + "']"));
			}
			driver.click(wbElementSubmitBtn);
		} else {
			throw new Exception("Please choose correct values for selection attribute: " + strSelection);
		}

		LoggerUtil.debug("selectAndSubmit6WTagsSearch successful.");
		LoggerUtil.debug("End of selectAndSubmit6WTagsSearch.");

	}

	public void clickCheckBoxForDashboard(Driver driver, String selectObjects, String colName, String colVal,
			Map<String, String> attributeMap) throws Exception {
		LoggerUtil.debug("Start of clickCheckBox For Dashboard.");
		boolean finalmatch = false;
		WebElement wbElement = null;
		int checkBoxCellId = 0;

		String strWait = attributeMap.get(Constants.ATTRIBUTE_WAIT);
		String finalStrWait = getTimeOut(strWait);

		LoggerUtil.debug("polling time : " + finalStrWait);
		LoggerUtil.debug("timeout : " + finalStrWait);

		HashMap<String, String> hashMapForFreezepaneElemenet = new HashMap<String, String>() {

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

		HashMap<String, String> hashMapForNonFreezepaneElemenet = new HashMap<String, String>() {

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

		if (!ifCondition(driver, hashMapForNonFreezepaneElemenet, null)
				&& !ifCondition(driver, hashMapForFreezepaneElemenet, null)) {
			clickClock(driver, attributeMap);
		}

		if (colName != null && !"".equalsIgnoreCase(colName)) {
			List<WebElement> wbEl = null; // multiple

			if (ifCondition(driver, hashMapForFreezepaneElemenet, null)) {

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
						intCellId = Integer.parseInt(cellCnt) + intPos;
						// intCellId = Integer.parseInt(cellCnt) - 1;
					} catch (Exception e) {
						LoggerUtil.debug("Invalid string in argument.");
					}
					checkBoxCellId = intCellId;

					WebElement wbColCell = driver
							.findElement(By.xpath("//div[@cell-id=" + intCellId + "][@is-visible='true']/div"));
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
							+ "' and @is-visible='true']/div[@class='wux-tweakers wux-tweakers-string wux-tweakers-string-label wux-tweakers-labelonly']"));
					driver.click(wbCheckBoxElement);
				}

			} else {

				if (ifCondition(driver, hashMapForNonFreezepaneElemenet, null)) {
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

					for (WebElement wb : wbEl) {

						LoggerUtil.debug("Current Webelement : " + wb);
						String cellCnt = wb.getAttribute("cell-id");

						int intFirstCellId = Integer.parseInt(cellCnt) + 3;

						int intCellId = 0;
						try {
							intCellId = intFirstCellId + intPos;
							// intCellId = Integer.parseInt(cellCnt) - 1;
						} catch (Exception e) {
							LoggerUtil.debug("Invalid string in argument.");
						}
						checkBoxCellId = intCellId;

						WebElement wbColCell = driver
								.findElement(By.xpath("//div[@cell-id=" + intCellId + "][@is-visible='true']/div"));
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
								+ "' and @is-visible='true']/div[@class='wux-tweakers wux-tweakers-string wux-tweakers-string-label wux-tweakers-labelonly']"));
						driver.click(wbCheckBoxElement);
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
		LoggerUtil.debug("End of clickCheckBox for Dashboard.");
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
		WebElement applySearchFilter = driver.findElement(By.xpath(
				"//div[@id='search-button-container']/button[@class='btn-primary search-button btn btn-root' and text()='Search']"));
		highLightElement(driver, attributeMap, applySearchFilter);
		driver.click(applySearchFilter);
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
		WebElement resetSearchFilter = driver.findElement(By.xpath("//div[@id='trash-_-context2']"));
		highLightElement(driver, attributeMap, resetSearchFilter);
		driver.click(resetSearchFilter);
		LoggerUtil.debug("resetearch successful.");
		LoggerUtil.debug("End of resetearch.");

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
		if ((strSearchInputText == null || "".equalsIgnoreCase(strSearchInputText))
				|| !attributeMap.containsKey(Constants.ATTRIBUTE_ID)) {
			throw new Exception(" attribute id not specified for filterSearchPerson tag");
		}
		LoggerUtil.debug("strSearchInputText: " + strSearchInputText);

		String strFieldlabel = attributeMap.get(Constants.ATTRIBUTE_FIELDLABEL);
		if (strFieldlabel == null || "".equals(strFieldlabel))
			throw new Exception(" attribute fieldlabel not specified for filterSearchPerson tag");
		LoggerUtil.debug("strFieldlabel: " + strFieldlabel);

		String strSelectionType = attributeMap.get(Constants.ATTRIBUTE_SELECTION_TYPE);
		if (strSelectionType == null || "".equals(strSelectionType))
			throw new Exception(" attribute selection not specified for filterSearchPerson tag");
		LoggerUtil.debug("strSelectionType: " + strSelectionType);

		fieldTypeObject(driver, strFieldlabel, strSearchInputText, attributeMap, strSelectionType);
		LoggerUtil.debug("Field type Person search successful.");
		LoggerUtil.debug("End of filterSearchPerson.");
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
			Map<String, String> attributeMap, String strSelectionType) throws Exception {
		LoggerUtil.debug("Start of fieldTypeObject.");
		String strInputChooser = "(//td[@class='createLabel']//following::td[@class='createInputField']/input[@title='"
				+ strFieldlabel + "']//following::input[@value='...'])[1]";
		WebElement wbElementFieldChooser = driver.findElement(By.xpath(strInputChooser));
		driver.click(wbElementFieldChooser);
		switchToWindow(driver, attributeMap);
		WebElement wbElementInput = null;

		wbElementInput = driver.findElement(By.xpath(
				"//input[@class='sn-search-field uwa-autocomplete nv-autocomplete-input' and @placeholder='Search']"));

		driver.writeText(wbElementInput, strSearchInputText);
		wbElementInput = driver.findElement(By.xpath("//div[@data-rec-id='run_btn_search']"));
		driver.click(wbElementInput);

		wbElementInput = driver.findElement(
				By.xpath("//label[@id='switch-view-button']/span[@class='fonticon fonticon-view-small-tile']"));
		driver.click(wbElementInput);

		wbElementInput = driver.findElement(By.xpath(
				"//ul[@class='dropdown-menu-wrap dropdown-menu-icons']//span[@class='item-text' and text()='Datagrid View']"));
		driver.click(wbElementInput);

		if (strSelectionType.equalsIgnoreCase("text")) {

			wbElementInput = driver.findElement(By.xpath(
					"//div[@data-rec-id='DataGridView' ]//following::div[@class='wux-tweakers wux-tweakers-string wux-tweakers-string-label wux-tweakers-labelonly' and text()='"
							+ strSearchInputText + "']"));
			String getText = driver.getText(wbElementInput);
			if (getText.equalsIgnoreCase(strSearchInputText)) {
				driver.click(wbElementInput);
				wbElementInput = driver.findElement(By.id("id_in_app_ok"));
				driver.click(wbElementInput);
			}

		} else if (strSelectionType.equalsIgnoreCase("row-number")) {
			String strRowNumber = attributeMap.get(Constants.ATTRIBUTE_ROW);
			if (strRowNumber == null || "".equals(strRowNumber))
				throw new Exception(" attribute is not defined for row-number");
			LoggerUtil.debug("strId: " + strRowNumber);

			try {
				wbElementInput = driver.findElement(
						By.xpath("//div[@class='wux-datagridview-selection-label' and text()='" + strRowNumber + "']"));
				driver.click(wbElementInput);

				wbElementInput = driver.findElement(By.id("id_in_app_ok"));
				driver.click(wbElementInput);

			} catch (Exception e) {
				LoggerUtil.debug("row is not available " + strRowNumber);
			}

		}

		LoggerUtil.debug("Field type object search successful.");
		LoggerUtil.debug("End of fieldTypeObject.");
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

		if ((strSearchInputText == null || "".equalsIgnoreCase(strSearchInputText))
				|| !attributeMap.containsKey(Constants.ATTRIBUTE_ID)) {
			throw new Exception("attribute id not specified for FilterSearchForm tag");
		}
		LoggerUtil.debug("strSearchInputText: " + strSearchInputText);

		String strFieldlabel = attributeMap.get(Constants.ATTRIBUTE_FIELDLABEL);
		if (strFieldlabel == null || "".equals(strFieldlabel))
			throw new Exception(" attribute fieldlabel not specified for FilterSearchForm tag");
		LoggerUtil.debug("strFieldlabel: " + strFieldlabel);

		String strInputfieldtype = attributeMap.get(Constants.INPUT_FIELD_TYPE);
		if (strInputfieldtype == null || "".equals(strInputfieldtype))
			throw new Exception(" attribute inputfieldtype not specified for FilterSearchForm tag");
		LoggerUtil.debug("strInputfieldtype: " + strInputfieldtype);

		String strSelection = attributeMap.get(Constants.ATTRIBUTE_SELECTION);
		LoggerUtil.debug("strSelection: " + strSelection);

		InputType inputField = InputType.valueOf(strInputfieldtype.toLowerCase());

		switch (inputField) {
		// case Constants.INPUTTYPE_TEXT:
		case text:
			fieldTypeText(driver, strFieldlabel, strSearchInputText, attributeMap);
			break;
		// case Constants.INPUTTYPE_TEXTCHOOSER: //deprecated for 21x,22x, it's
		// available for tll 17x
		case textchooser:
			fieldTypeTextChooser(driver, strFieldlabel, strSearchInputText, attributeMap);
			break;
		// case Constants.INPUTTYPE_SELECT:
		case select:
			fieldTypeSelect(driver, strFieldlabel, strSearchInputText, attributeMap, strSelection);
			break;
		// case Constants.INPUTTYPE_OBJECT: //deprecated for 21x,22x, it's available for
		// tll 17x
		case object:
			fieldTypeObject(driver, strFieldlabel, strSearchInputText, attributeMap, strSelection);
			break;
		// case Constants.INPUTTYPE_PERSON: //deprecated for 21x,22x, it's available for
		// tll 17x
		case person:
			fieldTypePerson(driver, strFieldlabel, strSearchInputText, attributeMap, strSelection);
			break;
		// case Constants.INPUTTYPE_DATE:
		case date:
			fieldTypeDate(driver, strFieldlabel, strSearchInputText, attributeMap);
			break;
		// case Constants.INPUTTYPE_CLICK: //deprecated for 21x,22x, it's available for
		// tll 17x
		case click:
			fieldTypeClick(driver, strFieldlabel, strSearchInputText, attributeMap);
			break;
		default:
			throw new Exception("Please select correct inputType : " + inputField);
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
		WebElement wbElement = null;
		LoggerUtil.debug("Start of fieldTypeText.");
		String strInputTexta = "(//div[@class='predicate-header' and @title='" + strFieldlabel
				+ "']//following::div[@class='autocomplete-searchbox']/input[@type='text'])[1]";
		wbElement = driver.findElement(By.xpath(strInputTexta));
		driver.writeText(wbElement, strSearchInputText);
		driver.sendKey(wbElement, Keys.TAB);
		wbElement = driver.findElement(By.xpath(
				"//div[@id='search-button-container']/button[@class='btn-primary search-button btn btn-root' and text()='Search']"));
		driver.click(wbElement);
		try {
			String strSelection = attributeMap.get(Constants.ATTRIBUTE_SELECTION);
			if (strSelection.contains(strSelection)) {
				selectSingleAndMultiCheckBox(driver, attributeMap, strSelection);
			}
		} catch (Exception e) {
			LoggerUtil.debug("Selection is not defined for selecting values." + e.getMessage());
		}
		LoggerUtil.debug("Field type text search successful.");
		LoggerUtil.debug("End of fieldTypeText.");
	}

	/**
	 * This method is used to fieldTypeSelect select.
	 * 
	 * @param driver
	 * @param strFieldlabel
	 * @param strSearchInputText
	 * @throws Exception
	 */
	public void fieldTypeSelect(Driver driver, String strFieldlabel, String strSearchInputText,
			Map<String, String> attributeMap, String strSelection) throws Exception {
		LoggerUtil.debug("Start of fieldTypeSelect.");
		WebElement webElement = null;
		LoggerUtil.debug("Selected  field label is : ." + strFieldlabel);
		String strInputTexta = "(//div[@class='predicate-header' and @title='" + strFieldlabel
				+ "']//following::div[@class='autocomplete-searchbox']/input[@type='text'])[1]";
		webElement = driver.findElement(By.xpath(strInputTexta));
		driver.click(webElement);

		driver.writeText(webElement, strSearchInputText);
		driver.sendKey(webElement, Keys.TAB);
		WebElement applySearchFilter = driver.findElement(By.xpath(
				"//div[@id='search-button-container']/button[@class='btn-primary search-button btn btn-root' and text()='Search']"));
		driver.click(applySearchFilter);

		LoggerUtil.debug("Selection option is : ." + strSelection);
		selectSingleAndMultiCheckBox(driver, attributeMap, strSelection);

		LoggerUtil.debug("Field type select search successful.");
		LoggerUtil.debug("End of fieldTypeSelect.");
	}

	/**
	 * @method This method used to selectCheckBoxUnselectCheckBox.
	 * @param driver
	 * @param attributeMap
	 * @return void
	 * @throws Exception
	 */
	public void selectSingleAndMultiCheckBox(Driver driver, Map<String, String> attributeMap, String strSelection)
			throws Exception {
		LoggerUtil.debug("Start selecting Check box");
		WebElement webElement = null;
		// boolean isChecked = false;
		webElement = driver.findElement(By.xpath("//label[@id='switch-view-button']"));
		driver.click(webElement);
		webElement = driver.findElement(By.xpath(
				"//ul[@class='dropdown-menu-wrap dropdown-menu-icons']//span[@class='item-text' and text()='Datagrid View']"));
		driver.click(webElement);
		LoggerUtil.debug("Your option is : " + strSelection);

		switch (strSelection) {
		case Constants.SELECTION_SINGLE:
			String strRows = attributeMap.get(Constants.ATTRIBUTE_ROW);
			if (strRows == null || strRows.isEmpty()) {
				throw new Exception("Attribute row cannot be null or empty for single selection");
			}
			webElement = driver.findElement(
					By.xpath("//div[@class='wux-datagridview-selection-label' and text()='" + strRows + "']"));
			webElement.click();
			break;

		case Constants.SELECTION_MULTIPLE:
			multipleSelection(driver, strSelection, attributeMap);
			break;

		default:
			throw new Exception("Please select correct selection type: " + strSelection);
		}
		LoggerUtil.debug("End of Selecting Checkbox");
	}

	public void multipleSelection(Driver driver, String strSelection, Map<String, String> attributeMap)
			throws Exception {
		LoggerUtil.debug("Start sulti selecting Checkbox");
		WebElement webElement = null;
		String strRows = attributeMap.get(Constants.ATTRIBUTE_ROW);
		if (strRows == null || strRows.isEmpty()) {
			throw new Exception("Attribute row cannot be null or empty for multiple selection");
		}
		List<String> listOfCheckBox = Arrays.stream(strRows.split(Pattern.quote("|"))).collect(Collectors.toList());
		for (String strRowCheck : listOfCheckBox) {
			webElement = driver.findElement(
					By.xpath("//div[@class='wux-datagridview-layouts-row-header-extension-poolcontainer']/div["
							+ strRowCheck + "]"));
			webElement.click();
		}
		LoggerUtil.debug("End of multi selecting Checkbox");
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
		WebElement wbElement = null;
		LoggerUtil.debug("Start of fieldTypeDate.");

		String clickElement = "//div[@id='search-button-container']/button[@class='btn-primary search-button btn btn-root' and text()='Search']";
		String strCriteriaType = attributeMap.get(Constants.ATTRIBUTE_CRITERIA);
		LoggerUtil.debug("Your Option is: " + strCriteriaType);
		switch (strCriteriaType.toLowerCase()) {
		case Constants.DATE_SELECTION_CRITERIA_ON_OR_AFTER:
			wbElement = fromDate(driver, strFieldlabel);
			getTargetDateMonthAndYearSearch(driver, strSearchInputText, wbElement);
			driver.findElement(By.xpath(clickElement)).click();
			break;

		case Constants.DATE_SELECTION_CRITERIA_ON_OR_BEFORE:
			wbElement = toDate(driver, strFieldlabel);
			getTargetDateMonthAndYearSearch(driver, strSearchInputText, wbElement);
			driver.findElement(By.xpath(clickElement)).click();
			break;

		case Constants.DATE_SELECTION_CRITERIA_ON:
			wbElement = fromDate(driver, strFieldlabel);
			getTargetDateMonthAndYearSearch(driver, strSearchInputText, wbElement);
			wbElement = toDate(driver, strFieldlabel);
			getTargetDateMonthAndYearSearch(driver, strSearchInputText, wbElement);
			driver.findElement(By.xpath(clickElement)).click();
			break;

		case Constants.DATE_SELECTION_CRITERIA_BETWEEN:
			wbElement = fromDate(driver, strFieldlabel);
			int iDateInputSeperatorIndex = strSearchInputText.indexOf("|");
			String strDateInput1 = null;
			if (iDateInputSeperatorIndex != -1) {
				strDateInput1 = strSearchInputText.substring(0, iDateInputSeperatorIndex).trim();
				getTargetDateMonthAndYearSearch(driver, strDateInput1, wbElement);
			} else {
				strDateInput1 = strSearchInputText;
				LoggerUtil.debug("WARNINGS!!!: It required pipe seprator date value for between attribute.");
			}
			String strDateInput2 = strSearchInputText.substring(iDateInputSeperatorIndex + 1).trim();
			wbElement = toDate(driver, strFieldlabel);
			getTargetDateMonthAndYearSearch(driver, strDateInput2, wbElement);
			driver.findElement(By.xpath(clickElement)).click();
			break;
		default:
			throw new Exception("Please select currect Criteria value: " + strCriteriaType);
		}
		LoggerUtil.debug("Field type Date search successful.");
		LoggerUtil.debug("End of fieldTypeDate.");
	}

	public WebElement fromDate(Driver driver, String strFieldlabel) throws Exception {
		LoggerUtil.debug("Starts of From Date.");
		WebElement wbElement = null;
		wbElement = driver.findElement(By.xpath("(//div[@class='predicate-header' and @title='" + strFieldlabel
				+ "']//following::div[@class='predicate-content expandable']//input[@placeholder='From'])[1]"));
		driver.waitUntil(ExpectedConditions.elementToBeClickable((wbElement)));
		driver.click(wbElement, "js", "true");

		LoggerUtil.debug("End of From Date.");
		return wbElement;
	}

	public WebElement toDate(Driver driver, String strFieldlabel) throws Exception {
		LoggerUtil.debug("Starts of To Date.");
		WebElement wbElement = null;
		wbElement = driver.findElement(By.xpath("(//div[@class='predicate-header' and @title='" + strFieldlabel
				+ "']//following::div[@class='predicate-content expandable']//input[@placeholder='To'])[1]"));
		driver.waitUntil(ExpectedConditions.elementToBeClickable((wbElement)));
		driver.click(wbElement, "js", "true");

		LoggerUtil.debug("End of To Date.");
		return wbElement;
	}

	/**
	 * This method is used to get Current Day, Current Month and Current Year And
	 * jump in specified month
	 * 
	 * @param driver
	 * @param strSearchInputText
	 * @throws Exception
	 */
	public void getTargetDateMonthAndYearSearch(Driver driver, String strSearchInputText, WebElement wbElement)
			throws Exception {
		LoggerUtil.debug("Start target Date, Month and Year Selection");

		LoggerUtil.debug("selectFromDate: " + strSearchInputText);
		int firstIndex = strSearchInputText.indexOf("-");
		int lastIndex = strSearchInputText.lastIndexOf("-");
		String day = strSearchInputText.substring(0, firstIndex);
		String month = strSearchInputText.substring(firstIndex + 1, lastIndex);
		String year = strSearchInputText.substring(lastIndex + 1, strSearchInputText.length());
		int targetDay = Integer.parseInt(day);
		int targetMonth = Integer.parseInt(month);
		int targetYear = Integer.parseInt(year);
		LoggerUtil.debug("target Day:" + targetDay + " target Month: " + targetMonth + " target Year: " + targetYear);

		Select selectElement = null;
		selectElement = new Select(driver.findElement(By.xpath(
				"//div[@class='datepicker datepicker-root  is-bound']//div//select[@class='datepicker-select datepicker-select-year']")));
		selectElement.selectByVisibleText(year);
		LoggerUtil.debug("Date picker year selection successful.");
		driver.click(wbElement);
		selectElement = new Select(driver.findElement(By.xpath(
				"//div[@class='datepicker datepicker-root  is-bound']//div//select[@class='datepicker-select datepicker-select-month']")));
		selectElement.selectByIndex(targetMonth - 1);
		LoggerUtil.debug("Date picker month selection" + " successful.");
		driver.click(wbElement);
		WebElement wb_Element = driver.findElement(By.xpath(
				"//div[@class='datepicker datepicker-root  is-bound']//table//tbody/tr//td[@data-day='" + day + "']"));
		driver.click(wb_Element);
		LoggerUtil.debug("Date picker day selection successful.");
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
		else if (insideFreezePane != null && !Constants.CHECK_FALSE.equalsIgnoreCase(insideFreezePane))
			throw new Exception("InsideFreezePane attribute value must be true or false.");

		int position = Integer.parseInt(strPosition);

		WebElement weTableRowColumn = driver.findElement(By.xpath(
				"//table[@id='" + tableClass + "']/tbody/tr[@id='" + strRowId + "']/td[@position='" + position + "']"));

		LoggerUtil.debug("weTableRowColumn: " + weTableRowColumn);
		LoggerUtil.debug("End of getIndentedTableCell ");
		return weTableRowColumn;

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

		if (!attributeMap.containsKey(Constants.ATTRIBUTE_ID) && !attributeMap.containsKey(Constants.ATTRIBUTE_INPUT)) {
			throw new Exception("input / id attribute is not defined for EditIndentedTableRow tag");
		}

		LoggerUtil.debug("Input/ID : " + strInput);
		if (strInput == null && "".equals(strInput)) {
			throw new Exception("input / id attribute is not defined properly for EditIndentedTableRow tag");
		}

		String strAction = attributeMap.get(Constants.ATTRIBUTE_ACTION);
		LoggerUtil.debug("strAction: " + strAction);
		if (strAction == null || "".equals(strAction))
			throw new Exception("action attribute is not defined for EditIndentedTableRow tag.");

		WebElement weTableRowColumn = getIndentedTableCell(driver, attributeMap);

		scrollToElement(driver, weTableRowColumn);

		LoggerUtil.debug("weTableRowColumn: " + weTableRowColumn);
		Actions actions = new Actions(driver.getWebDriver());
		InputType inputType = null;
		try {
			inputType = InputType.valueOf(strAction.toLowerCase());
		} catch (Exception enumconstant) {
			throw new Exception("Not a valid action : " + strAction + " for editIndentedTable tag.");
		}
		switch (inputType) {
		case text:
			driver.click(weTableRowColumn);
			WebElement weInputBox = driver.findElement(By.xpath("//div[@class='formLayer']"));
			LoggerUtil.debug("weInputBox : " + weInputBox);
			actions.moveToElement(weInputBox);
			actions.sendKeys(strInput);
			actions.sendKeys(Keys.TAB);
			actions.build().perform();
			break;

		case select:
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
		File parentDir = null;
		if (!attributeMap.containsKey(Constants.ATTRIBUTE_ID) && !attributeMap.containsKey(Constants.ATTRIBUTE_INPUT)) {
			PropertyUtil propertyutil = PropertyUtil.newInstance();
			strInputFilePath = propertyutil.getProperty(Constants.PROPERTY_KEY_DEFAULT_DOWNLOAD_FILE_PATH);
			parentDir = new File(strInputFilePath);
		} else {
			parentDir = new File(strInputFilePath).getParentFile();
		}

		LoggerUtil.debug("Folder" + parentDir);
		File[] files = parentDir.listFiles();

		int iFilesCountBeforeDownload = files.length;

		LoggerUtil.debug(
				"Total File Contains in " + parentDir + " before starts downloading = " + iFilesCountBeforeDownload);
		String strWait = attributeMap.get(Constants.ATTRIBUTE_WAIT);

		if (attributeMap == null || !attributeMap.containsKey(Constants.ATTRIBUTE_COMMANDLABEL.toLowerCase()))
			throw new Exception("commandlabel attribute is missing in DownloadFileUsingCommand tag.");

		String commandLabel = attributeMap.get(Constants.ATTRIBUTE_COMMANDLABEL.toLowerCase());
		LoggerUtil.debug("commandLabel : " + commandLabel);
		if (commandLabel == null || "".equalsIgnoreCase(commandLabel))
			throw new Exception("attribute commandlabel is empty in DownloadFileUsingCommand tag.");

		openActionToolbarMenu(driver, attributeMap);
		LoggerUtil.debug("action menu clicked");

		downloadFile(driver, null, browserName);

		validateFileDownloading(driver, iFilesCountBeforeDownload, strInputFilePath, attributeMap, strWait);

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
		String strWait = attributeMap.get(Constants.ATTRIBUTE_WAIT);

		File parentDir = null;
		if (!attributeMap.containsKey(Constants.ATTRIBUTE_ID) && !attributeMap.containsKey(Constants.ATTRIBUTE_INPUT)) {
			PropertyUtil propertyutil = PropertyUtil.newInstance();
			strInputFilePath = propertyutil.getProperty(Constants.PROPERTY_KEY_DEFAULT_DOWNLOAD_FILE_PATH);
			parentDir = new File(strInputFilePath);
		} else {
			parentDir = new File(strInputFilePath).getParentFile();
		}

		LoggerUtil.debug("Folder" + parentDir);
		File[] files = parentDir.listFiles();

		int iFilesCountBeforeDownload = files.length;

		LoggerUtil.debug(
				"Total File Contains in " + parentDir + " before starts downloading = " + iFilesCountBeforeDownload);

		WebElement weTableRowColumn = getIndentedTableCell(driver, attributeMap);
		LoggerUtil.debug("weTableRowColumn : " + weTableRowColumn);

		WebElement wbDownload = driver.findElement(weTableRowColumn,
				By.xpath("//img[contains(@src,'../common/images/iconActionDownload.gif')]"));
		LoggerUtil.debug("wbDownload : " + wbDownload);

		downloadFile(driver, wbDownload, browserName);

		validateFileDownloading(driver, iFilesCountBeforeDownload, strInputFilePath, attributeMap, strWait);

		LoggerUtil.debug("End of downloadFileUsingIcon.");
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
		if (!attributeMap.isEmpty()) {
			throw new Exception(
					"It is Empty tag no attribute is defined " + attributeMap + " for the switchToContentFrame tag.");
		}
		LoggerUtil.debug("End of switchToContentFrame.");
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
		validateLocatorType(strlocatorType);
		LoggerUtil.debug("locator type : " + strlocatorType + " is valid.");

		try {
			wbElement = driver.findElement(strlocatorType, strlocatorExpression);
		} catch (Exception e) {
			// Web Element is not found due to deletion
			LoggerUtil.debug("File not found.");
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
			throw new Exception("attribute are not specified for tag uploadFileUsingUpload.");
		}

		String strFilePath = attributeMap.get(Constants.ATTRIBUTE_PATH);

		if (!attributeMap.containsKey("id") && !attributeMap.containsKey(Constants.ATTRIBUTE_PATH)) {
			throw new Exception("attribute id/path not specified for uploadFileUsingLocator.");
		}
		if (filePath == null || "".equalsIgnoreCase(filePath)) {
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
}