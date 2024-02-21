package com.steepgraph.ta.framework.V6R2012x.pages;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;

import com.steepgraph.ta.framework.Constants;
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

	@Override
	public void logInForNoCas(Driver driver, Map<String, String> attributeMap) throws Exception {
		LoggerUtil.debug("Start of logInForNoCas.");
		String username = (String) attributeMap.get("username");
		String password = (String) attributeMap.get("password");

		WebElement wbusername = driver.findElement(By.cssSelector("[name='login_name']"));
		wbusername.clear();
		driver.writeText(wbusername, username);

		WebElement wbpassword = driver.findElement(By.cssSelector("[name='login_password']"));
		wbpassword.clear();
		driver.writeText(wbpassword, password);

		WebElement wbLogin = driver.findElement(By.xpath("//input[@class='button']"));
		driver.click(wbLogin);

		LoggerUtil.debug("Login button clicked.");

		LoggerUtil.debug("End of logInForNoCas.");
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

		WebElement wbGlobalActions = driver.findElement(By.xpath("//div[@id='pageHeadDiv']//td[@title='Actions']"));
		LoggerUtil.debug("Global Actions Menu Found");

		highLightElement(driver, attributeMap, wbGlobalActions);

		driver.click(wbGlobalActions);
		LoggerUtil.debug("Global Actions Menu Clicked");

		WebElement wbGlobalActionCommand = selectDialogItem(driver, labelArray);
		if (wbGlobalActionCommand == null)
			throw new Exception("Unable to locate Global Action Menu for command lable: " + commandLabel);

		LoggerUtil.debug("Element Found: " + wbGlobalActionCommand.getText());

		highLightElement(driver, attributeMap, wbGlobalActionCommand);

		Thread.sleep(1000);
		driver.click(wbGlobalActionCommand);

		LoggerUtil.debug("End of clickGlobalActionsMenu.");
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
		LoggerUtil.debug("Start of logOut.");
		switchToDefaultContent(driver);
		WebElement wbSignOut = driver.findElement(By.xpath("//div[@id='pageHeadDiv']//td[@title='Logout']"));
		Thread.sleep(2000);
		driver.click(wbSignOut);
		// commonUtilobj.releaseResources();
		LoggerUtil.debug("Completed logOut.");
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
			hasSecurityContextPage = "true";

		LoggerUtil.debug("hasSecurityContextPage : " + hasSecurityContextPage);

		if ("true".equalsIgnoreCase(hasSecurityContextPage)) {

			switchToDefaultContent(driver);

			String organization = (String) attributeMap.get("organization");
			LoggerUtil.debug("organization : " + organization);

			String project = (String) attributeMap.get("project");
			LoggerUtil.debug("project : " + project);

			String role = (String) attributeMap.get("role");
			LoggerUtil.debug("role : " + role);

			if (organization != null && !"".equals(organization) && project != null && !"".equals(project)
					&& role != null && !"".equals(role)) {
				String securityContext = role + "." + organization + "." + project;
				WebElement wbSelectSecurityContext = driver.findElement(By.xpath("//select[@name='SecContext']"));
				driver.selectByText(wbSelectSecurityContext, securityContext);
			}

			WebElement wbDone = driver.findElement(By.xpath("//input[@name='enter']"));
			driver.click(wbDone);
			LoggerUtil.debug("Done buttion clicked");
		}

		LoggerUtil.debug("End of selectSecurityContext.");
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

		}

		LoggerUtil.debug("strType: " + strType);

		LoggerUtil.debug("Input: " + strSearchInputText);

		String strHasTVC = propertyUtil.getProperty(Constants.PROPERTY_KEY_3DSPACE_HAS_TVC);
		if (strHasTVC == null || "".equals(strHasTVC))
			strHasTVC = "false";

		WebElement wbElementGlobalSearchText = null;

		if ("false".equalsIgnoreCase(strHasTVC))
			wbElementGlobalSearchText = driver
					.findElement(By.xpath("//div[@id='pageHeadDiv']//input[@name='AEFGlobalFullTextSearch']"));
		else
			wbElementGlobalSearchText = driver.findElement(By.id("quickSearch"));

		highLightElement(driver, attributeMap, wbElementGlobalSearchText);
		driver.writeText(wbElementGlobalSearchText, strSearchInputText);
		LoggerUtil.debug("Text to search entered into search input field.");

		if (!"all".equalsIgnoreCase(strType)) {
			wbElementGlobalSearch = driver.findElement(
					By.xpath("//div[@id='pageHeadDiv']//td[contains(@class,'menu-arrow') and @title='Search']"));
			driver.click(wbElementGlobalSearch);
			String[] arrInputType = strType.split("\\|");

			WebElement wbSearchType = selectDialogItem(driver, arrInputType);
			if (wbSearchType == null)
				throw new Exception("Unable to select type of search: type: " + strType);

			LoggerUtil.debug("Element Found: " + wbSearchType.getText());
			highLightElement(driver, attributeMap, wbSearchType);
			driver.click(wbSearchType);

			LoggerUtil.debug("Type selected for Global search");
		} else {
			wbElementGlobalSearchText.sendKeys(Keys.ENTER);
		}

		LoggerUtil.debug("Global Search Successfully Done.");
		LoggerUtil.debug("End of globalSearch.");
	}

	@Override
	public void clickMyDeskMenu(Driver driver, Map<String, String> attributeMap) throws Exception {
		LoggerUtil.debug("Start of clickMyDeskMenu.");

		switchToDefaultContent(driver);

		if (attributeMap == null || attributeMap.size() == 0 || !attributeMap.containsKey("commandLabel")) {
			throw new Exception("commandLabel is not defined for tag ClickMyDeskMenu.");
		}

		String commandLabel = (String) attributeMap.get("commandLabel");
		if (commandLabel == null || "".equals(commandLabel)) {
			throw new Exception("commandLabel is empty for tag ClickMyDeskMenu.");
		}

		LoggerUtil.debug("commandLabel: " + commandLabel);

		WebElement wbMyDeskMenu = null;

		String strHasTVC = propertyUtil.getProperty(Constants.PROPERTY_KEY_3DSPACE_HAS_TVC);
		if (strHasTVC == null || "".equals(strHasTVC))
			strHasTVC = "false";

		if ("true".equalsIgnoreCase(strHasTVC))
			wbMyDeskMenu = driver.findElement(By.xpath("//div[@id='pageHeadDiv']//td[@title='My Desk']"));
		else
			wbMyDeskMenu = driver.findElement(By.xpath("//div[@id='pageHeadDiv']//td[@title='My Enovia']"));

		LoggerUtil.debug("My Desk menu found");

		highLightElement(driver, attributeMap, wbMyDeskMenu);

		driver.click(wbMyDeskMenu);
		LoggerUtil.debug("My Desk menu clicked");

		String[] labelArray = commandLabel.split(Pattern.quote("|"));

		WebElement wbMyDeskCommand = selectDialogItem(driver, labelArray);
		if (wbMyDeskCommand == null)
			throw new Exception("Unable to locate my desk menu with command label: " + commandLabel);

		LoggerUtil.debug("Element Found: " + wbMyDeskCommand.getText());
		highLightElement(driver, attributeMap, wbMyDeskCommand);

		Thread.sleep(1000);
		driver.click(wbMyDeskCommand);

		LoggerUtil.debug("End of clickMyDeskMenu.");
	}

	/**
	 * This method is used by global search and global action menu to select dialog
	 * item.
	 * 
	 * @param driver
	 * @param attributeMap
	 * @throws Exception
	 */
	private WebElement selectDialogItem(Driver driver, String[] inputArray) throws Exception {
		LoggerUtil.debug("Start of selectDialogItem.");

		List<WebElement> wbPreviousCommandList = null;
		WebElement wbPreviousCommand = null;

		int length = inputArray.length;
		LoggerUtil.debug("inputArray length: " + inputArray);

		if (length == 1) {
			return driver.findElement(By.xpath("//div[@class='mmenu']//li/span[text()='" + inputArray[0] + "']/.."));
		}

		for (int i = 0; i < length; i++) {
			if (i == 0) {
				wbPreviousCommandList = driver
						.findElements(By.xpath("//div[@class='mmenu']//h3/span[text()='" + inputArray[i] + "']/.."));
			} else if (i == 1) {
				for (Iterator<WebElement> iterator = wbPreviousCommandList.iterator(); iterator.hasNext();) {
					WebElement wbTemp = (WebElement) iterator.next();

					try {
						wbTemp = driver.findElement(wbTemp,
								By.xpath("./following-sibling::ul[1]/li/span[text()='" + inputArray[i] + "']/.."));
						if (wbTemp.isDisplayed()) {
							wbPreviousCommand = wbTemp;
							break;
						}
					} catch (Exception e) {
					}
				}
				if (i + 1 == length) {
					return wbPreviousCommand;
				}
			} else if (i + 1 == length) {
				wbPreviousCommand = driver.findElement(wbPreviousCommand,
						By.xpath("./following-sibling::ul[1]/li/span[text()='" + inputArray[i] + "']/.."));
				return wbPreviousCommand;
			} else {
				wbPreviousCommand = driver.findElement(wbPreviousCommand,
						By.xpath("./following-sibling::ul[1]/li/span[text()='" + inputArray[i] + "']/.."));
			}

		}
		LoggerUtil.debug("End of selectDialogItem.");
		return null;
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
		if (attributeMap == null || !attributeMap.containsKey("title")) {
			throw new Exception("Attribute title not specified for tag clickCategoryCommand.");
		}

		String strCategoryName = attributeMap.get("title");
		if (strCategoryName == null || strCategoryName.equalsIgnoreCase("")) {
			throw new Exception("Attribute title not specified for tag clickCategoryCommand.");
		}

		WebElement wCatergoryMenu = driver
				.findElement(By.xpath("//div[@id='divToolbarContainer']//td[@title='Categories']"));
		driver.click(wCatergoryMenu);

		LoggerUtil.debug("Category menu clicked");

		WebElement wCatergoryCommand = driver
				.findElement(By.xpath("//div[@class='mmenu dialog']//li//span[text()='" + strCategoryName + "']/.."));
		LoggerUtil.debug("Clicking on " + strCategoryName + " category command");

		Thread.sleep(1000);
		driver.click(wCatergoryCommand);

		LoggerUtil.debug("End of clickCategoryCommand.");
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
				.findElement(By.xpath("//div[contains(@class,'mmenu page')]//li/span[text()='" + strMonth + "']"));
		driver.click(wbElementFieldSelectedMonth);
		LoggerUtil.debug("Date picker month selection successful.");

		WebElement wbElementFieldSelectYear = driver
				.findElement(By.xpath("//div[@class='calendar-container']//td[@id='tdYear']"));
		driver.click(wbElementFieldSelectYear);
		WebElement wbElementFieldSelectedYear = driver
				.findElement(By.xpath("//div[contains(@class,'mmenu page')]//li/span[text()='" + strYear + "']"));
		driver.click(wbElementFieldSelectedYear);
		LoggerUtil.debug("Date picker year selection successful.");

		WebElement wbElementFieldSelectDay = driver.findElement(
				By.xpath("//div[@class='calendar-container']//td[@title='" + strSearchInputText.trim() + "']"));
		driver.click(wbElementFieldSelectDay);
		LoggerUtil.debug("Date picker day selection successful.");

		LoggerUtil.debug("End of selectDateFromDatePicker.");
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
	public void clickGlobalToolsMenu(Driver driver, Map<String, String> attributeMap) throws Exception {
		LoggerUtil.debug("Start of clickGlobalToolsMenu.");

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

		WebElement wbGlobalActions = driver.findElement(By.xpath("//div[@id='pageHeadDiv']//td[@title='Tools']"));
		LoggerUtil.debug("Global Tools Menu Found");

		highLightElement(driver, attributeMap, wbGlobalActions);

		driver.click(wbGlobalActions);
		LoggerUtil.debug("Global Tools Menu Clicked");

		WebElement wbGlobalActionCommand = selectDialogItem(driver, labelArray);
		if (wbGlobalActionCommand == null)
			throw new Exception("Unable to locate Global Tools Menu for command lable: " + commandLabel);

		LoggerUtil.debug("Element Found: " + wbGlobalActionCommand.getText());

		highLightElement(driver, attributeMap, wbGlobalActionCommand);

		Thread.sleep(1000);
		driver.click(wbGlobalActionCommand);

		LoggerUtil.debug("End of clickGlobalToolsMenu.");
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

		String strFieldLabel = (String) attributeMap.get("fieldlabel");
		LoggerUtil.debug("strFieldLabel: " + strFieldLabel);
		if (strFieldLabel == null || "".equals(strFieldLabel))
			throw new Exception("fieldlabel attribute is not define for openChooser tag.");

		WebElement weChooser = driver.findElement(
				By.xpath("//td[contains(text(),'" + strFieldLabel + "')]/following-sibling::td/input[@value='...']"));

		driver.click(weChooser);

		switchToWindow(driver, attributeMap);

		LoggerUtil.debug("End of openChooser");
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

		String commandLabel = (String) attributeMap.get("commandlabel");
		LoggerUtil.debug("commandlabel: " + commandLabel);
		if (attributeMap == null || "".equals(commandLabel)) {
			throw new Exception("commandlabel is not defined for tag openActionToolbarMenu.");
		}

		String[] labelArray = commandLabel.split(Pattern.quote("|"));
		int length = labelArray.length;

		LoggerUtil.debug("commandLabel length: " + length);

		String strHasTVC = propertyUtil.getProperty(Constants.PROPERTY_KEY_3DSPACE_HAS_TVC);
		if (strHasTVC == null || "".equals(strHasTVC))
			strHasTVC = "false";

		LoggerUtil.debug("Has TVC : " + strHasTVC);

		boolean isActualTVCMenu = false;
		WebElement weActionsMenu = null;
		if ("true".equalsIgnoreCase(strHasTVC)) {
			try {
				weActionsMenu = driver.findElement(By.xpath("//div[contains(@id,'Toolbar')]//td[@title='Actions']"));
				isActualTVCMenu = true;
			} catch (Exception e) {
				weActionsMenu = driver.findElement(By.xpath("//div[@id='divToolbar']//td[@title='Actions']"));
			}
		} else {
			weActionsMenu = driver.findElement(By.xpath("//div[@id='divToolbar']//td[@title='Actions']"));
		}

		if (weActionsMenu == null)
			throw new Exception(
					"Enable to find Actions menu in current frame. Please switch to correct frame to open actions menu");

		driver.click(weActionsMenu);

		WebElement wbCommand = null;
		for (int i = 0; i < labelArray.length; i++) {
			String label = labelArray[i];
			if (!isActualTVCMenu)
				wbCommand = driver
						.findElement(By.xpath("//div[@class='mmenu dialog']//li//span[text()='" + label + "']/.."));
			else
				wbCommand = driver
						.findElement(By.xpath("//div[@class='mmenu dialog']//span[text()='" + label + "']/.."));

			Thread.sleep(1000);
			driver.click(wbCommand);
		}

		LoggerUtil.debug("End of openActionToolbarMenu.");
	}

	/**
	 * This method is used to click FORWARD button.
	 * 
	 * @param driver
	 * @param level
	 * @throws Exception
	 */
	@Override
	public void clickForwardButton(Driver driver) throws Exception {
		LoggerUtil.debug("Start of clickForwardButton.");
		WebElement wbElementForward = driver.findElement(By.xpath("//div[@id='pageHeadDiv']//td[@title='Forward']"));
		driver.click(wbElementForward);
		LoggerUtil.debug("End of clickForwardButton.");
	}

	/**
	 * This method is used to click Back button.
	 * 
	 * @param driver
	 * @param level
	 * @throws Exception
	 */
	@Override
	public void clickBackButton(Driver driver) throws Exception {
		LoggerUtil.debug("Start of clickForwardButton.");
		WebElement wbElementForward = driver.findElement(By.xpath("//div[@id='pageHeadDiv']//td[@title='Back']"));
		driver.click(wbElementForward);
		LoggerUtil.debug("End of clickForwardButton.");
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
		driver.scrollToElement(element);
	}
}