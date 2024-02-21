package com.steepgraph.ta.framework.common.pages;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;

import com.steepgraph.ta.framework.Constants;
import com.steepgraph.ta.framework.common.AssertionException;
import com.steepgraph.ta.framework.common.interfaces.IHandler;
import com.steepgraph.ta.framework.common.interfaces.ILibrary;
import com.steepgraph.ta.framework.utils.interfaces.ICommonUtil;
import com.steepgraph.ta.framework.utils.pages.LoggerUtil;
import com.steepgraph.ta.framework.utils.pages.PropertyUtil;
import com.steepgraph.ta.framework.utils.pages.RegisterObjectUtil;

/**
 * Class containing implementation code for Outlook Mail Tag
 * 
 * @author SteepGraph Systems
 */
public class OutlookLibrary {

	protected IHandler handler;

	protected ICommonUtil commonUtilobj = null;

	protected PropertyUtil propertyUtil = null;

	protected RegisterObjectUtil registerUtil;

	protected Map<String, Object> docObjectMap = null;

	protected boolean iOpenOutlook = true;

	public OutlookLibrary(PropertyUtil propertyUtil, RegisterObjectUtil registerUtil, ICommonUtil commonUtil) {

		this.propertyUtil = propertyUtil;
		this.registerUtil = registerUtil;
		this.commonUtilobj = commonUtil;
		docObjectMap = new HashMap<>();
	}

	/**
	 * @Method This method is used to login outllookmail.
	 * 
	 * @author SteepGraph Systems
	 * @param driver
	 * @return void
	 * @throws Exception
	 */
	public void outlookLogin(Driver driver, Map<String, String> attributeMap, ILibrary library) throws Exception {

		LoggerUtil.debug("Start of Outlook logIn.");

		String strUserName = attributeMap.get(Constants.INPUT_USERNAME);
		String strPassword = attributeMap.get(Constants.INPUT_PASSWORD);

		if (null == strUserName || strUserName.isEmpty())
			throw new AssertionException("username cannot be empty or blank.");

		if (null == strPassword || strPassword.isEmpty())
			throw new AssertionException("Password cannot be empty or blank.");

		try {

			// Signing screen click
			WebElement wbForSignScreen = driver
					.findElement(By.xpath("//div[@class='mectrl_header_text mectrl_truncate']"));
			driver.click(wbForSignScreen);
			// System.out.println("click on signing screen");

			// Username entry and click
			WebElement wbusername = driver.findElement(By.xpath("//input[@name='loginfmt']"));
			wbusername.clear();
			driver.writeText(wbusername, strUserName);
			wbusername = driver.findElement(By.xpath("//div/input[@type='submit']"));
			driver.click(wbusername);

			String strWait = attributeMap.get(Constants.ATTRIBUTE_WAIT);
			String finalStrWait = getTimeOut(strWait);
			LoggerUtil.debug("timeout : " + finalStrWait);

			HashMap<String, String> attributeMapForUserCheck = new HashMap<String, String>() {

				/**
				 * 
				 */
				private static final long serialVersionUID = 1016009681308605014L;

				{
					put(Constants.LOCATOR_TYPE, "xpath");
					put(Constants.LOCATOR_EXPRESSION, "//small[contains(text(),'" + strUserName + "')]");
					put(Constants.ATTRIBUTE_CRITERIA, "found");
					put(Constants.ATTRIBUTE_WAIT, finalStrWait);
				}
			};

			HashMap<String, String> attributeMapForSign = new HashMap<String, String>() {

				/**
				 * 
				 */
				private static final long serialVersionUID = -6383390785515375407L;

				{
					put(Constants.LOCATOR_TYPE, Constants.INPUTTYPE_XPATH);
					put(Constants.LOCATOR_EXPRESSION, "//div[contains(text(),'Enter password')]");
					put(Constants.ATTRIBUTE_CRITERIA, "found");
					put(Constants.ATTRIBUTE_WAIT, finalStrWait);
				}
			};

			if (library.ifCondition(driver, attributeMapForUserCheck, null)) {

				WebElement wbForExistAccount = driver
						.findElement(By.xpath("//small[contains(text(),'" + strUserName + "')]"));
				driver.click(wbForExistAccount);
			} else if (library.ifCondition(driver, attributeMapForSign, null)) {

				WebElement wbpassword = driver.findElement(By.xpath("//input[@name='passwd']"));
				wbpassword.clear();
				driver.writeText(wbpassword, strPassword);
				WebElement wbLogin = driver.findElement(By.xpath("//div/input[@type='submit']"));
				driver.click(wbLogin);

				attributeMapForSign.put(Constants.LOCATOR_EXPRESSION, "//div[contains(text(),'Stay signed in?')]");
				if (library.ifCondition(driver, attributeMapForSign, null)) {

					WebElement wbForSigning = driver.findElement(By.xpath("//input[@id='idBtn_Back']"));
					driver.click(wbForSigning);
				}

				attributeMapForSign.put(Constants.LOCATOR_EXPRESSION, "//div[contains(text(),'Pick an account')]");
				if (library.ifCondition(driver, attributeMapForSign, null)) {

					WebElement wbForExistAccount = driver
							.findElement(By.xpath("//small[contains(text(),'" + strUserName + "')]"));
					driver.click(wbForExistAccount);
				}
			}

			library.switchToWindow(driver, new HashMap<String, String>() {
				/**
				 * 
				 */
				private static final long serialVersionUID = -5335673339050222842L;

				{
					put("last", "true");
				}
			});
			attributeMapForUserCheck.put(Constants.LOCATOR_EXPRESSION, "//button[@id='O365_MainLink_Me']");

			if (!library.ifCondition(driver, attributeMapForUserCheck, null)) {
				throw new Exception("Failed to Login Outlook.");
			}

			LoggerUtil.debug("outlook get successfully login.");
		} catch (Exception ex) {
			ex.printStackTrace();
			if (iOpenOutlook) {
				iOpenOutlook = false;
				library.openURL(driver, new HashMap<String, String>() {

					/**
					 * 
					 */
					private static final long serialVersionUID = -8069647501781796801L;

					{
						put(Constants.ATTRIBUTE_TARGET, "self");
					}
				}, null);
				outlookLogin(driver, attributeMap, library);
			} else {
				iOpenOutlook = true;
				throw new AssertionException("Failed to Login Outlook.");
			}
		}
	}

	/**
	 * @Method This method is used to login outllookmail.
	 * 
	 * @author SteepGraph Systems
	 * @param driver
	 * @return void
	 * @throws Exception
	 */
	public void searchOutlookMail(Driver driver, Map<String, String> attributeMap) throws Exception {
		LoggerUtil.debug("Start of SearchOutlookMail.");
		String strMailSubject = attributeMap.get("searchText");
		String strSearchMailId = attributeMap.get("searchMailId");
		WebElement wbMailSearch = null;

		if (null == strMailSubject || strMailSubject.isEmpty())
			throw new AssertionException("Mail Subject cannot be empty or blank.");

		if (null == strSearchMailId || strSearchMailId.isEmpty())
			throw new AssertionException("Please provide valid email for search.");

		// Selenium-1568
		// Change in Xpath as '//div/div/input[@aria-label='Search']' is not exist
		try {
			wbMailSearch = driver.findElement(
					By.xpath("//div/div/input[@aria-label='Search for email, meetings, files and more.']"));
		} catch (Exception e) {
			LoggerUtil.debug(
					"WARNING !!! - XPath :'//div/div/input[@aria-label='Search for email, meetings, files and more.']'Not Found so Looking for old XPATH '//div/div/input[@aria-label='Search']'");
			wbMailSearch = driver.findElement(By.xpath("//div/div/input[@aria-label='Search']"));
		}
		wbMailSearch.sendKeys(Keys.chord(Keys.CONTROL, "a"));
		wbMailSearch.sendKeys(Keys.BACK_SPACE);
		driver.writeText(wbMailSearch, strMailSubject);
		wbMailSearch = driver
				.findElement(By.xpath("//div[@id='owaSearchBox_container']//i[@data-icon-name='SearchRegular']"));
		driver.click(wbMailSearch);

		List<WebElement> lstWbElementForSearchText = driver
				.findElements(By.xpath("//div[@id='groupHeaderTop results']/parent::div/div"));

		boolean blForAllResults = false;
		int iIndexForTopResult = 0;
		for (int iIndex = 0; iIndex < lstWbElementForSearchText.size(); iIndex++) {
			if (lstWbElementForSearchText.get(iIndex).getAttribute("id").equalsIgnoreCase("groupHeaderTop results")) {
				iIndexForTopResult = iIndex;
			} else if (lstWbElementForSearchText.get(iIndex).getAttribute("id")
					.equalsIgnoreCase("groupHeaderAll results")) {

				if (lstWbElementForSearchText.get(iIndex + 1).getAttribute("aria-label").contains(strSearchMailId)) {
					driver.click(lstWbElementForSearchText.get(iIndex + 1));
					blForAllResults = true;
					break;
				} else {
					throw new AssertionException("Please provide validate email id for search text.");
				}
			}
		}

		if (!blForAllResults) {
			WebElement wbSearchElement = driver.findElement(lstWbElementForSearchText.get(iIndexForTopResult + 1),
					By.xpath("//div/span[@title='" + strSearchMailId + "']"));
			driver.click(wbSearchElement);
		}

		LoggerUtil.debug("End of SearchOutlookMail.");
	}

	/**
	 * @Method This method is used to validate outlook email header.
	 * @param driver
	 * @param attributeMap
	 * @return void
	 * @throws AssertionException
	 */
	public void assertOutlookMailHeader(Driver driver, Map<String, String> attributeMap) throws AssertionException {
		LoggerUtil.debug("Start of AssertOutlookMailHeader");

		if (null == attributeMap || attributeMap.isEmpty())
			throw new AssertionException("Please provide validate attribute for validation.");

		String strSubject = attributeMap.get(Constants.ATTRIBUTE_SUBJECT);
		String strToCheckStringPresentInMail = attributeMap.get(Constants.ATTRIBUTE_VALIDATE_MSG_STRING);
		String strValidateMailHeader = attributeMap.get(Constants.ATTRIBUTE_VALIDATE_MAIL_HEADER);
		StringBuffer sbForMailHeader = new StringBuffer();

		if (null != strSubject) {
			try {
				LoggerUtil.debug("Validating for subject.");
				driver.findElement(By.xpath("//div[@title='" + strSubject + "']"));
				LoggerUtil.debug("Successfull validation for subject.");
			} catch (Exception ex) {
				throw new AssertionException("Assertion fail for this subject: " + strSubject);
			}
		}

		if (null != strValidateMailHeader && !strValidateMailHeader.isEmpty()) {

			List<WebElement> lstWbForMailHeader = driver
					.findElements(By.xpath("//div[@aria-label='Email message']/div[1]"));
			List<String> lstOfValidateHeader = new ArrayList<String>(
					Arrays.asList(strValidateMailHeader.split(Pattern.quote("|"))));

			for (WebElement WbFoeMailHeader : lstWbForMailHeader) {
				sbForMailHeader.append(WbFoeMailHeader.getText().trim());
				sbForMailHeader.append(" ");
			}

			for (String strValidateHeader : lstOfValidateHeader) {

				if (!sbForMailHeader.toString().contains(strValidateHeader))
					throw new AssertionException(
							"Please provide correct mail header to validate for " + strValidateHeader);
			}
			LoggerUtil.debug("Successfully validated the Mail Header.");
		}

		if (null != strToCheckStringPresentInMail && !strToCheckStringPresentInMail.isEmpty()) {

			WebElement wbElement = driver.findElement(By.xpath("//div[@aria-label='Message body']/div/div/div"));
			String strMessageBody = wbElement.getText().trim();
			List<String> lstOfCheckString = new ArrayList<String>(
					Arrays.asList(strToCheckStringPresentInMail.split(Pattern.quote("|"))));

			for (String strCheckString : lstOfCheckString) {

				if (!strMessageBody.contains(strCheckString)) {
					throw new AssertionException("Failed to validated email msg: " + strCheckString);
				}
			}
			LoggerUtil.debug("Successfully validated the Mail Message Body.");
		}
		LoggerUtil.debug("End of AssertOutlookMailHeader");
	}

	/**
	 * @method This method is used to logout .
	 * @param driver
	 * @param attributeMap
	 * @throws Exception
	 * @return void
	 * @throws AssertionException
	 */
	public void outlooklogout(Driver driver, Map<String, String> attributeMap, ILibrary library) throws Exception {

		LoggerUtil.debug("Logging out from outlook.");

		String strUserName = attributeMap.get(Constants.INPUT_USERNAME);

		if (null == strUserName || strUserName.isEmpty())
			throw new AssertionException("username cannot be empty or blank for outlookLogout.");

		String strWait = attributeMap.get(Constants.ATTRIBUTE_WAIT);
		String finalStrWait = getTimeOut(strWait);
		LoggerUtil.debug("timeout : " + finalStrWait);

		attributeMap.put(Constants.LOCATOR_TYPE, Constants.INPUTTYPE_XPATH);
		attributeMap.put(Constants.LOCATOR_EXPRESSION, "//small[contains(text(),'" + strUserName + "')]");
		attributeMap.put(Constants.ATTRIBUTE_CRITERIA, "found");
		attributeMap.put(Constants.ATTRIBUTE_WAIT, finalStrWait);

		WebElement wbElementForLogout = driver.findElement(By.xpath("//button[@id='O365_MainLink_Me']"));
		driver.click(wbElementForLogout);
		wbElementForLogout = driver
				.findElement(By.xpath("//div[@class='mectrl_currentAccount']/a[@id='mectrl_body_signOut']"));
		driver.click(wbElementForLogout);

		if (library.ifCondition(driver, attributeMap, null)) {
			WebElement wbForExistAccount = driver
					.findElement(By.xpath("//small[contains(text(),'" + strUserName + "')]"));
			driver.click(wbForExistAccount);
		}

		attributeMap.put(Constants.ATTRIBUTE_AFTER_TIME, "pass");
		attributeMap.put("for", "element");
		attributeMap.put(Constants.LOCATOR_EXPRESSION, "//div[contains(text(),'You signed out of your account')]");
		attributeMap.put(Constants.ATTRIBUTE_MODE, "selenium");
		attributeMap.put(Constants.ATTRIBUTE_TIME, finalStrWait);
		library.wait(driver, attributeMap);
		driver.waitForJavaScriptToLoad();
		driver.closeCurrentWindow();
		library.switchToParentWindow(driver);
		LoggerUtil.debug("Logout successfully...");
	}

	public String getTimeOut(String strTimeOut) throws Exception {
		PropertyUtil propertyutil = PropertyUtil.newInstance();

		if (strTimeOut == null || strTimeOut.equalsIgnoreCase("")) {
			LoggerUtil.debug(
					"As wait is not initialized so taking value from '3dx-tas.execution.step.timeout' as default wait");
			strTimeOut = propertyutil.getProperty(Constants.PROPERTY_KEY_EXECUTION_STEP_TIMEOUT);
		}
		if (!isNumeric(strTimeOut)) {
			throw new NumberFormatException("wait attribute is not specified properly.");
		}
		return strTimeOut;
	}

	private static boolean isNumeric(String str) {
		for (char c : str.toCharArray()) {
			if (!Character.isDigit(c)) {
				return false;
			}
		}
		return true;
	}
}
