package com.steepgraph.ta.framework.V6R2020x.pages;

import java.util.List;
import java.util.Map;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;

import com.steepgraph.ta.framework.common.interfaces.IHandler;
import com.steepgraph.ta.framework.common.pages.Driver;
import com.steepgraph.ta.framework.utils.interfaces.ICommonUtil;
import com.steepgraph.ta.framework.utils.pages.LoggerUtil;
import com.steepgraph.ta.framework.utils.pages.PropertyUtil;
import com.steepgraph.ta.framework.utils.pages.RegisterObjectUtil;

public class Library extends com.steepgraph.ta.framework.V6R2019x.pages.Library {

	public Library(IHandler handler, RegisterObjectUtil registerUtil, PropertyUtil propertyUtil, ICommonUtil commonUtil)
			throws Exception {
		super(handler, registerUtil, propertyUtil, commonUtil);
	}

	/**
	 * This method will click on the object name link to open the object
	 * 
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

		/*
		 * To Select Table View Mode
		 */
		WebElement wbSwitchBtn = driver
				.findElement(By.xpath("//div[contains(@class, 'switcher-button')]//label[@id='switch-view-button']"));
		driver.click(wbSwitchBtn);
		WebElement wbTable = driver.findElement(By.xpath("//ul[contains(@class, 'icon-dropdown')]//li[@name='table']"));
		driver.click(wbTable);

		Thread.sleep(3000);

		/*
		 * To Find all the Rows which contain input strOpenObject In Case of multiple
		 * element always select first match element
		 */
		List<WebElement> wbElementInputBtn = null;
		try {
			wbElementInputBtn = driver.findElements(By.xpath(
					"//div[contains(@class, 'wux-controls-abstract wux-layouts-collectionview-cell wux-layouts-datagridview-cell')]//div[text()='"
							+ strOpenObject + "']"));
		} catch (Exception e) {
			LoggerUtil.debug("Element not available in indexed objects.");
			clickClock(driver, attributeMap);
			wbElementInputBtn = driver.findElements(By.xpath(
					"//div[contains(@class, 'wux-controls-abstract wux-layouts-collectionview-cell wux-layouts-datagridview-cell')]//div[text()='"
							+ strOpenObject + "']"));
		}
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
			Thread.sleep(5000);
			colCellCnt = wbColHeader.getAttribute("cell-id");
		}
		try {
			intPosCol = Integer.parseInt(colCellCnt) - 4;
			LoggerUtil.debug("Relative position of " + columnName + " column from 1st column : " + intPosCol);
		} catch (NumberFormatException e) {
			firstMatchWebElement = wbElementInputBtn.get(0);
		}

		if (wbElementInputBtn != null && columnName != null && columnValue != null && firstMatchWebElement == null)

		{
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

				WebElement wbNameCell = driver.findElement(
						By.xpath("//div[@cell-id='" + (minCellId - 1 + intPosCol) + "'][@is-visible='true']"));
				if (wbNameCell.findElements(By.xpath("./div")).size() > 0) {

					WebElement wbNameCellText = wbNameCell.findElement(By.xpath("./div"));
					if (wbNameCellText.getText().equalsIgnoreCase(strOpenObject)) {
						if (columnName.equalsIgnoreCase("Title")) {
							WebElement wbTitleCell = driver.findElement(By.xpath(
									"//div[@cell-id='" + (minCellId - 1 + intPosCol) + "'][@is-visible='true']"));
							if (wbTitleCell.findElements(By.xpath("./div")).size() > 0) {
								WebElement wbTitleCellText = wbTitleCell.findElement(By.xpath("./div"));
								if (wbTitleCellText.getText().equalsIgnoreCase(columnValue)) {
									firstMatchWebElement = wbTitleCellText;
									LoggerUtil.debug("firstMatchWebElement : " + firstMatchWebElement);
									break;
								}
							}
						} else if (columnName.equalsIgnoreCase("Name")) {
							firstMatchWebElement = wbNameCellText;
							LoggerUtil.debug("firstMatchWebElement : " + firstMatchWebElement);
							break;
						} else {

							WebElement wbColCell = driver.findElement(By.xpath(
									"//div[@cell-id='" + (minCellId - 1 + intPosCol) + "'][@is-visible='true']"));
							if (wbColCell.findElements(By.xpath("./div")).size() > 0) {
								WebElement wbColCellText = wbColCell.findElement(By.xpath("./div"));
								if (columnValue.equalsIgnoreCase(wbColCellText.getText())) {
									firstMatchWebElement = wbColCellText;
									LoggerUtil.debug("firstMatchWebElement : " + firstMatchWebElement);
									break;
								}
							}
						}
					} else {
						WebElement wbColCell = driver.findElement(
								By.xpath("//div[@cell-id='" + (minCellId - 1 + intPosCol) + "'][@is-visible='true']"));
						if (wbColCell.findElements(By.xpath("./div")).size() > 0) {
							WebElement wbColCellText = wbColCell.findElement(By.xpath("./div"));
							if (columnValue.equalsIgnoreCase(wbColCellText.getText())) {
								firstMatchWebElement = wbColCellText;
								LoggerUtil.debug("firstMatchWebElement : " + firstMatchWebElement);
								break;
							}
						}
					}
				} else {
					continue;
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
		}

		LoggerUtil.debug("openSearchResult successful.");
		LoggerUtil.debug("End of openSearchResult.");

	}

	/**
	 * This method is used for Refresh any page by clicking refresh button in the
	 * inner frame of 3DEXPERIENCE UI.
	 * 
	 * @param driver
	 * @param level
	 * @return void
	 * @throws Exception
	 */
	@Override
	public void clickRefreshButton(Driver driver, Map<String, String> attributeMap) throws Exception {
		LoggerUtil.debug("Start of clickRefreshButton.");
		WebElement wbElementRefresh = driver
				.findElement(By.xpath("//div[@id='divExtendedHeaderNavigation']//a[@title='Refresh']"));
		driver.click(wbElementRefresh);
		LoggerUtil.debug("End of clickRefreshButton.");
	}

	/**
	 * This method is used to click BACK button in the inner frame of 3DEXPERIENCE
	 * UI.
	 * 
	 * @param driver
	 * @param level
	 * @return void
	 * @throws Exception
	 */
	@Override
	public void clickBackButton(Driver driver) throws Exception {
		LoggerUtil.debug("Start of clickBackButton.");
		WebElement wbElementBack = driver.findElement(By.xpath(
				".//*[@id='ExtpageHeadDiv']//div[@id='divExtendedHeaderNavigation']//a[@class='fonticon fonticon-chevron-left'] | .//div[@id='pageHeadDiv']//a[@class='fonticon fonticon-chevron-left']"));
		driver.click(wbElementBack);
		LoggerUtil.debug("End of clickBackButton.");
	}

	/**
	 * This method is used to click fORWARD button in the inner frame of
	 * 3DEXPERIENCE UI.
	 * 
	 * @param driver
	 * @param level
	 * @return void
	 * @throws Exception
	 */
	@Override
	public void clickForwardButton(Driver driver) throws Exception {
		LoggerUtil.debug("Start of clickForwardButton.");
		WebElement wbElementForward = driver.findElement(By.xpath(
				".//*[@id='ExtpageHeadDiv']//div[@id='divExtendedHeaderNavigation']//a[@class='fonticon fonticon-chevron-right'] | .//div[@id='pageHeadDiv']//a[@class='fonticon fonticon-chevron-right']"));
		driver.click(wbElementForward);
		LoggerUtil.debug("End of clickForwardButton.");
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

		LoggerUtil.debug("Start of selectDate 2020");
		WebElement wbDATE = findElement(driver, attributeMap, true);

		if (!strDate.isEmpty() && !isValidDateFormat(strDate)) {
			throw new Exception(strDate
					+ " is not matching with required date format. Date format should be 'Month Day, Year' Eg. May 6, 2018");
		}
		if (!strDate.isEmpty()) {
			selectDateFromDatePicker(driver, strDate, wbDATE);
		}
		if (strDate.isEmpty()) {
			driver.click(wbDATE);
		}
		LoggerUtil.debug("End of selectDate ");

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
			WebElement wbElementNameInputSelected = driver.findElement(By
					.xpath("//li[@class='autocomplete-item default-template js-selected']//span[@class='item-label']"));
			driver.click(wbElementNameInputSelected);
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
}