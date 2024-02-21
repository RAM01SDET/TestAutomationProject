package com.steepgraph.ta.framework.V6R2017x.pages;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;

import com.steepgraph.ta.framework.common.interfaces.IHandler;
import com.steepgraph.ta.framework.common.pages.Driver;
import com.steepgraph.ta.framework.utils.interfaces.ICommonUtil;
import com.steepgraph.ta.framework.utils.pages.LoggerUtil;
import com.steepgraph.ta.framework.utils.pages.PropertyUtil;
import com.steepgraph.ta.framework.utils.pages.RegisterObjectUtil;

public class Library extends com.steepgraph.ta.framework.V6R2015x.pages.Library {

	public Library(IHandler handler, RegisterObjectUtil registerUtil, PropertyUtil propertyUtil, ICommonUtil commonUtil)
			throws Exception {
		super(handler, registerUtil, propertyUtil, commonUtil);
	}

	/**
	 * This method is used to click BACK button in the inner frame of 3DEXPERIENCE
	 * UI.
	 * 
	 * @param driver
	 * @param level
	 * @throws Exception
	 */
	@Override
	public void clickBackButton(Driver driver) throws Exception {
		LoggerUtil.debug("Start of clickBackButton.");
		WebElement wbElementBack = driver.findElement(By.xpath(
				".//*[@id='ExtpageHeadDiv']//div[@id='divExtendedHeaderNavigation']//div[@class='field previous button']//button[@class='previous']"));
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
	@Override
	public void clickForwardButton(Driver driver) throws Exception {
		LoggerUtil.debug("Start of clickForwardButton.");
		WebElement wbElementForward = driver.findElement(By.xpath(
				".//*[@id='ExtpageHeadDiv']//div[@id='divExtendedHeaderNavigation']//div[@class='field next button']//button[@class='next']"));
		driver.click(wbElementForward);
		LoggerUtil.debug("End of clickForwardButton.");
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

		WebElement wbElementFieldSelectedMonth = driver
				.findElement(By.xpath("//div[@class='menu-panel page']//li//label[text()='" + strMonth + "']/.."));
		driver.click(wbElementFieldSelectedMonth);
		LoggerUtil.debug("Date picker month selection successful.");

		WebElement wbElementFieldSelectYear = driver.findElement(By.xpath(
				"//div[@class='calendar-container' and contains(@style,'visibility: visible;')]//input[@id='tdYear']"));
		driver.writeText(wbElementFieldSelectYear, strYear);

		wbElementFieldSelectYear.sendKeys(Keys.ENTER);
		LoggerUtil.debug("Date picker year selection successful.");

		int iCommaIndex = strSearchInputText.indexOf(",");

		String strDay = strSearchInputText.substring(iMonthindex, iCommaIndex).trim();

		WebElement wbElementFieldSelectDay = driver.findElement(
				By.xpath("//div[@class='calendar-container']//td[text()='" + strDay + "' and contains(@class,'day')]"));
		driver.click(wbElementFieldSelectDay);
		LoggerUtil.debug("Date picker day selection successful.");

		LoggerUtil.debug("End of selectDateFromDatePicker.");
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
		if (attributeMap == null || !attributeMap.containsKey("id")) {
			throw new Exception("attribute id not specified for tag GlobalSearch ");
		}

		String strId = (String) attributeMap.get("id");

		if (strId == null || "".equals(strId))
			throw new Exception(" attribute id not specified for tag GlobalSearch ");
		LoggerUtil.debug("strId: " + strId);

		if (!attributeMap.containsKey("type")) {
			strType = "all";
		} else {
			strType = (String) attributeMap.get("type");
			if (strType == null || "".equals(strType)) {
				strType = "all";
			}
			LoggerUtil.debug("strType: " + strType);
		}
		if (!"all".equalsIgnoreCase(strType)) {
			wbElementGlobalSearch = driver.findElement(
					By.xpath("//div[@id='GTBsearchDiv']//div[@id='AEFGlobalFullTextSearch']//label[text()='All']"));
			driver.click(wbElementGlobalSearch);

			WebElement wbMenuLiElement = null;
			String[] arrInputType = strType.split("\\|");
			int length = arrInputType.length;
			for (int i = 0; i < length; i++) {
				if (i + 1 != length) {
					wbMenuLiElement = driver.findElement(By.xpath("//div[@id='AEFGlobalSearchHolder']//label[text()='"
							+ arrInputType[i].trim() + "']/../parent::li"));
					String strMenuClass = wbMenuLiElement.getAttribute("class");
					if (strMenuClass != null && strMenuClass.toLowerCase().contains("menu collapsed")) {
						wbElementGlobalSearch = driver.findElement(wbMenuLiElement,
								By.xpath(".//descendant::label[text()='" + arrInputType[i].trim() + "']/.."));
						driver.click(wbElementGlobalSearch, "js", "false");
					}

				} else {
					wbElementGlobalSearch = driver.findElement(wbMenuLiElement,
							By.xpath(".//descendant::label[text()='" + arrInputType[i].trim() + "']/.."));
					driver.click(wbElementGlobalSearch, "js", "false");
				}

			}

		} else {
			LoggerUtil.debug("Type selected for Global search");

			WebElement wbElementGlobalSearchText = driver.findElement(
					By.xpath("//div[@id='pageHeadDiv']//div[@id='GTBsearchDiv']//input[@id='GlobalNewTEXT']"));
			highLightElement(driver, attributeMap, wbElementGlobalSearchText);
			driver.writeText(wbElementGlobalSearchText, strSearchInputText);
			LoggerUtil.debug("Text to search entered into search input field.");

			WebElement wbElementGlobalSearchSubmit = driver.findElement(
					By.xpath("//div[@id='pageHeadDiv']//div[@id='GTBsearchDiv']//a[@class='btn search']/.."));
			driver.click(wbElementGlobalSearchSubmit);
		}
		LoggerUtil.debug("Global Search Successfully Done.");
		LoggerUtil.debug("End of globalSearch.");
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
		// TODO Auto-generated method stub
		LoggerUtil.debug("End of clickHomeMenu.");
		switchToDefaultContent(driver);

		String commandLabel = (String) attributeMap.get("commandLabel");
		if (commandLabel == null || "".equals(commandLabel))
			throw new Exception("commandLabel attribute is not specified for tag clickHomeMenu tag.");

		LoggerUtil.debug("commandLabel: " + commandLabel);

		WebElement homeMenuElement = driver
				.findElement(By.xpath("//div[@id='globalToolbar']//li[@class='icon-button home']"));
		LoggerUtil.debug("Home menu found");
		driver.getWait().until(ExpectedConditions.elementToBeClickable(homeMenuElement));
		highLightElement(driver, attributeMap, homeMenuElement);
		Actions actions = new Actions(driver.getWebDriver());
		actions.moveToElement(homeMenuElement).build().perform();

//		JavascriptExecutor executor = (JavascriptExecutor) driver.getWebDriver();
		String[] labels = commandLabel.split("\\|");

		for (int i = 0; i < labels.length; i++) {
			String label = labels[i];
			LoggerUtil.debug("label: " + label);
			WebElement homeCommand = driver.findElement(
					By.xpath("//div[@class='menu-panel global home']//a//label[text()='" + label + "']/.."));
			driver.click(homeCommand, "js", "false");
		}

		LoggerUtil.debug("End of clickHomeMenu.");
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
				driver.click(weGlobalActionCommand);
			} else {

				boolean isExpanded = false;

				if (i == 0)
					commandElement = driver.findElement(wbGlobalMenuPanel,
							By.xpath(".//descendant::label[text() = '" + labelArray[i] + "']/../../.."));
				else
					commandElement = driver.findElement(wbGlobalMenuPanel,
							By.xpath(".//descendant::label[text() = '" + labelArray[i] + "']/../.."));

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
}