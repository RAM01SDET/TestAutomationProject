package com.steepgraph.ta.framework.common.interfaces;

import java.util.Map;

import org.openqa.selenium.WebElement;

import com.steepgraph.ta.framework.common.pages.Driver;
import com.steepgraph.ta.framework.utils.interfaces.ICSVUtil;
import com.steepgraph.ta.framework.utils.interfaces.ICommonUtil;
import com.steepgraph.ta.framework.utils.pages.PropertyUtil;
import com.steepgraph.ta.framework.utils.pages.RegisterObjectUtil;

/**
 * Interface for Library class
 * 
 * @author Steepgraph Systems
 *
 */
public interface ILibrary {

	public ICommonUtil getCommonUtil() throws Exception;

	public void setCommonUtil(ICommonUtil commonUtilobj) throws Exception;

	void logIn(Driver driver, Map<String, String> attributeMap, PropertyUtil propertyUtil) throws Exception;

	void logOut(Driver driver, Map<String, String> attributeMap) throws Exception;

	void clickGlobalActionsMenu(Driver driver, Map<String, String> attributeMap) throws Exception;

	void switchToWindow(Driver driver, Map<String, String> attributeMap) throws Exception;

	void clickOverrideLink(Driver driver) throws Exception;

	void clickElement(Driver driver, Map<String, String> attributeMap) throws Exception;

	void switchToDefaultContent(Driver driver) throws Exception;

	void switchToParentWindow(Driver driver) throws Exception;

	void switchToSlideInWindow(Driver driver, Map<String, String> attributeMap) throws Exception;

	void handleAlert(Driver driver, Map<String, String> attributeMap) throws Exception;

	void clickPortalCommand(Driver driver, Map<String, String> attributeMap) throws Exception;

	void clickCategoryCommand(Driver driver, Map<String, String> attributeMap) throws Exception;

	void switchToFrame(Driver driver, Map<String, String> attributeMap) throws Exception;

	public void switchToContentFrame(Driver driver, Map<String, String> attributeMap) throws Exception;

	public void switchToDetailsDisplayFrame(Driver driver, Map<String, String> attributeMap) throws Exception;

	public void switchToFrameTableFrame(Driver driver, Map<String, String> attributeMap) throws Exception;

	public void switchToPortalDisplayFrame(Driver driver, Map<String, String> attributeMap) throws Exception;

	void selectIndentedTableRow(Driver driver, Map<String, String> attributeMap, String input) throws Exception;

	void uploadFileUsingDragAndDrop(Driver driver, Map<String, String> attributeMap) throws Exception;

	void inputText(Driver driver, Map<String, String> attributeMap, String text) throws Exception;

	void selectColor(Driver driver, Map<String, String> attributeMap) throws Exception;

	void maximiseWindow(Driver driver) throws Exception;

	public void clickRefreshButton(Driver driver, Map<String, String> attributeMap) throws Exception;

	public void clickBackButton(Driver driver) throws Exception;

	public void clickForwardButton(Driver driver) throws Exception;

	void wait(Driver driver, Map<String, String> attributeMap) throws Exception;

	public void Lifecycle(Driver driver, Map<String, String> attributeMap) throws Exception;

	void uploadFileUsingLocator(Driver driver, Map<String, String> attributeMap, String strSearchInputText)
			throws Exception;

	void uploadFileForDashboard(Driver driver, Map<String, String> attributeMap) throws Exception;

	void selectAllTableRows(Driver driver, Map<String, String> attributeMap) throws Exception;

	void downloadFileUsingCommand(Driver driver, Map<String, String> attributeMap, String browserName, String strInputFilePath) throws Exception;

	void downloadFileUsingIcon(Driver driver, Map<String, String> attributeMap, String browserName,  String strInputFilePath) throws Exception;

	public WebElement findElement(Driver driver, Map<String, String> attributeMap, Boolean bWrite) throws Exception;

	public WebElement findElement(Driver driver, Map<String, String> attributeMap, WebElement wbReferenceElement,
			Boolean bWrite) throws Exception;

	public void globalSearch(Driver driver, Map<String, String> attributeMap, String strSearchInputText)
			throws Exception;

	public void filterSearchForm(Driver driver, Map<String, String> attributeMap, String strSearchInputText)
			throws Exception;

	public void fieldTypeText(Driver driver, String strFieldlabel, String strSearchInputText,
			Map<String, String> attributeMap) throws Exception;

	public void fieldTypeTextChooser(Driver driver, String strFieldlabel, String strSearchInputText,
			Map<String, String> attributeMap) throws Exception;

	public void fieldTypeClick(Driver driver, String strFieldlabel, String strSearchInputText,
			Map<String, String> attributeMap) throws Exception;

	public void fieldTypeSelect(Driver driver, String strFieldlabel, String strSearchInputText,
			Map<String, String> attributeMap, String strSelection) throws Exception;

	public void fieldTypeObject(Driver driver, String strFieldlabel, String strSearchInputText,
			Map<String, String> attributeMap, String strSelection) throws Exception;

	public void fieldTypePerson(Driver driver, String strFieldlabel, String strSearchInputText,
			Map<String, String> attributeMap, String strSelection) throws Exception;

	public void fieldTypeDate(Driver driver, String strFieldlabel, String strSearchInputText,
			Map<String, String> attributeMap) throws Exception;

	public void filterSearchPerson(Driver driver, Map<String, String> attributeMap, String strSearchInputText)
			throws Exception;

	public void getDateFromDatePicker(Driver driver, String strFieldlabel, String strSearchInputText,
			String strCriteria) throws Exception;

	public void selectDateFromDatePicker(Driver driver, String strFieldlabel, String strSearchInputText,
			String strXPathDatePicker) throws Exception;

	public void action(Driver driver, Map<String, String> attributeMap) throws Exception;

	public void selectElement(Driver driver, Map<String, String> attributeMap, String strSelectValue) throws Exception;

	public void openChooser(Driver driver, Map<String, String> attributeMap) throws Exception;

	void applySearchFilter(Driver driver, Map<String, String> attributeMap) throws Exception;

	void resetSearchFilter(Driver driver, Map<String, String> attributeMap) throws Exception;

	public void selectAndSubmitSearch(Driver driver, Map<String, String> attributeMap, String selectObjects)
			throws Exception;

	public void selectAndSubmit6WTagsSearch(Driver driver, Map<String, String> attributeMap, String selectObjects)
			throws Exception;

	public void openSearchResult(Driver driver, Map<String, String> attributeMap, String strOpenObject)
			throws Exception;

	public void openActionToolbarMenu(Driver driver, Map<String, String> attributeMap) throws Exception;

	public void print(Driver driver, Map<String, String> attributeMap) throws Exception;

	void selectDate(Driver driver, Map<String, String> attributeMap, String strDate) throws Exception;

	public void registerObject(Driver driver, Map<String, String> attributeMap, String strKey) throws Exception;

	void scrollToElement(Driver driver, Map<String, String> attributeMap) throws Exception;

	void closeCurrentWindow(Driver driver) throws Exception;

	void editIndentedTableRow(Driver driver, Map<String, String> attributeMap, String strInput) throws Exception;

	void checkIndentedTableRow(Driver driver, Map<String, String> attributeMap) throws Exception;

	public void checkElement(Driver driver, Map<String, String> attributeMap) throws Exception;

	public void showNotification(Driver driver, Map<String, String> attributeMap) throws Exception;

	boolean ifCondition(Driver driver, Map<String, String> attributeMap, String strInputText) throws Exception;

	void scrollToElement(Driver driver, WebElement element) throws Exception;

	void downloadFileUsingLocator(Driver driver, Map<String, String> attributeMap, String browserName) throws Exception;

	void selectSecurityContext(Driver driver, Map<String, String> attributeMap) throws Exception;

	void clickWindowElement(Driver driver, Map<String, String> attributeMap) throws Exception;

	void selectDateFromDatePicker(Driver driver, String strSearchInputText, WebElement wbElement) throws Exception;

	boolean isValidDateFormat(String date) throws Exception;

	void clickMyDeskMenu(Driver driver, Map<String, String> attributeMap) throws Exception;

	void logInForCas(Driver driver, Map<String, String> attributeMap) throws Exception;

	void logInForNoCas(Driver driver, Map<String, String> attributeMap) throws Exception;

	public void setContent(Driver driver, Map<String, String> attributeMap, String strInputText) throws Exception;

	void inputWindowElement(Driver driver, Map<String, String> attributeMap, String input) throws Exception;

	public void assertTag(Driver driver, Map<String, String> attributeMap, String strInputText) throws Exception;

	void assertTag(Driver driver, Map<String, String> attributeMap, WebElement wbElement, String strInputText)
			throws Exception;

	void highLightElement(Driver driver, Map<String, String> attributeMap) throws Exception;

	public void mqlAssert(Driver driver, Map<String, String> attributeMap, String strInputText) throws Exception;

	void highLightElement(Driver driver, WebElement webElement, String style) throws Exception;

	void highLightElement(Driver driver, Map<String, String> attributeMap, WebElement webElement) throws Exception;

	public void sendKey(Driver driver, Map<String, String> attributeMap) throws Exception;

	public void assertAlert(Driver driver, Map<String, String> attributeMap, String inputText) throws Exception;

	public void assertDeletion(Driver driver, Map<String, String> attributeMap) throws Exception;

	void switchToParentFrame(Driver driver, Map<String, String> attributeMap) throws Exception;

	void assertListValues(Driver driver, Map<String, String> attributeMap, String input) throws Exception;

	public void openURL(Driver driver, Map<String, String> attributeMap, String strInputText) throws Exception;

	public void callWebService(Driver driver, Map<String, String> attributeMap, String strInputText) throws Exception;

	public void execute(Driver driver, Map<String, String> attributeMap, String strInputText) throws Exception;

	public void openDBConnection(Driver driver, Map<String, String> attributeMap, String strInputText,
			PropertyUtil propertyUtil) throws Exception;

	void assertDB(Driver driver, Map<String, String> attributeMap, String strInputText) throws Exception;

	void closeAllOtherWindow(Driver driver, Map<String, String> attributeMap) throws Exception;

	void assertFileExists(Driver driver, Map<String, String> attributeMap, String strInputText) throws Exception;

	void clickGlobalToolsMenu(Driver driver, Map<String, String> attributeMap) throws Exception;

	void assertIndentedTableRow(Driver driver, Map<String, String> attributeMap, String strInput) throws Exception;

	WebElement getIndentedTableCell(Driver driver, Map<String, String> attributeMap) throws Exception;

	WebElement getIndentedTableRow(Driver driver, Map<String, String> attributeMap) throws Exception;

	void runMql(Driver driver, Map<String, String> attributeMap, String strInput) throws Exception;

	public void deleteAllCookies(Driver driver, Map<String, String> attributeMap) throws Exception;

	void clickHomeMenu(Driver driver, Map<String, String> attributeMap) throws Exception;

	void selectWindowRegion(Driver driver, Map<String, String> attributeMap) throws Exception;

	public void switchToStartWindow(Driver driver, Map<String, String> attributeMap) throws Exception;

	void setStartWindow(Driver driver, Map<String, String> attributeMap) throws Exception;

	public void openCompassApp(Driver driver, Map<String, String> attributeMap) throws Exception;

	void selectOropenNewIndentedTableRow(Driver driver, Map<String, String> attributeMap, String strInputText)
			throws Exception;

	void registerIndentedTableObjects(Driver driver, Map<String, String> attributeMap) throws Exception;

	void assertPageLoadTime(Driver driver, Map<String, String> attributeMap) throws Exception;

	void dragAndDrop(Driver driver, Map<String, String> attributeMap, String browserName) throws Exception;

	public void selectSecurityContextFromTopBar(Driver driver, Map<String, String> attributeMap) throws Exception;

	public PropertyUtil getPropertyUtil();

	public void setPropertyUtil(PropertyUtil propertyUtil);

	public RegisterObjectUtil getRegisterUtil();

	public void setRegisterUtil(RegisterObjectUtil registerUtil);

	public IHandler getHandler();

	public void setHandler(IHandler handler);

	public String getValueForRegistration(Driver driver, Map<String, String> attributeMap) throws Exception;

	public void startPerformanceLogs(Driver driver);

	public void stopPerformanceLogs(Driver driver, Map<String, String> attributeMap, String msg) throws Exception;

	public void readUserInput(Driver driver) throws Exception;

	public void getValueFromCookies(Driver driver, Map<String, String> attributeMap) throws Exception;

	public void pinWidgetToDashboard(Driver driver, Map<String, String> attributeMap) throws Exception;

	public void deleteCurrentTabInDashboard(Driver driver, Map<String, String> attributeMap) throws Exception;

	public void selectContextMenuOption(Driver driver, Map<String, String> attributeMap) throws Exception;

	public void maximizeAndMinimize(Driver driver, Map<String, String> attributeMap) throws Exception;

	public void refreshBrowser(Driver driver, Map<String, String> attributeMap) throws Exception;

	public void clickNavigationPanelMenu(Driver driver, Map<String, String> attributeMap) throws Exception;

	public void openNewTabInDashBoard(Driver driver) throws Exception;

	public void validateTableExport(Driver driver, Map<String, String> attributeMap) throws Exception;

	public void readXML(Driver driver, Map<String, String> attributeMap, String strInputText) throws Exception;

	public void assertXML(Driver driver, Map<String, String> attributeMap, String strInputText) throws Exception;

	public void openNewToolbarInDashboard(Driver driver, Map<String, String> attributeMap) throws Exception;

	public void validateTableHeader(Driver driver, Map<String, String> attributeMap) throws Exception;

	public void clickDashboardCheckbox(Driver driver, Map<String, String> attributeMap) throws Exception;

	public void option(Driver driver, Map<String, String> attributeMap) throws Exception;

	public void validateBegin(Driver driver, Map<String, String> attributeMap) throws Exception;

	public void validateEnd(Driver driver, Map<String, String> attributeMap) throws Exception;

	public void validateBehaviour(Driver driver, Map<String, String> attributeMap) throws Exception;

	public void createDashboard(Driver driver, Map<String, String> attributeMap) throws Exception;

	public void deactive6Wsearch(Driver driver, Map<String, String> attributeMap) throws Exception;

	public void deletedashboard(Driver driver) throws Exception;

	public void assertDate(ICSVUtil csvUtil,Driver driver, Map<String, String> attributeMap, String strInputText) throws Exception;
}