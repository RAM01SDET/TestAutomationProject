package com.steepgraph.ta.framework.utils.pages;

import java.io.File;
import java.net.URL;
import java.util.Map;

import org.openqa.selenium.Platform;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.UnexpectedAlertBehaviour;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.ie.InternetExplorerOptions;
import org.openqa.selenium.remote.BrowserType;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.LocalFileDetector;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.safari.SafariDriver;
import org.openqa.selenium.safari.SafariOptions;

import com.browserup.bup.BrowserUpProxy;
import com.browserup.bup.BrowserUpProxyServer;
import com.browserup.bup.client.ClientUtil;
import com.browserup.bup.proxy.CaptureType;
import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.steepgraph.ta.framework.Constants;
import com.steepgraph.ta.framework.enums.Browsers;

/**
 * Utility class for browser related activities
 * 
 * @author Steepgraph Systems
 */
public class BrowserUtil {

	private static final String BROWSER_BINARY = "browser.binary";

	private BrowserUpProxy proxy;
	boolean IsAllowInSecureSites = false;

	/**
	 * Method to get WebDriver object
	 * 
	 * @author Steepgraph Systems
	 * @param browserName
	 * @return WebDriver
	 * @throws Exception
	 */
	public WebDriver getDriver(String browserName, PropertyUtil propertyUtil, Map<String, String> parameterMap)
			throws Exception {
		LoggerUtil.debug("Start of getDriver");

		String strIsAllowInSecureSites = propertyUtil.getProperty(Constants.PROPERTY_KEY_ALLOW_IN_SECURE_SITES);

		if (strIsAllowInSecureSites != null && strIsAllowInSecureSites.equalsIgnoreCase("true")) {
			IsAllowInSecureSites = true;
		}

		boolean isBrowserUpProxy = false;
		browserName = browserName.toLowerCase();
		LoggerUtil.debug("browserName : " + browserName);
		WebDriver driver;
		LoggerUtil.debug("Driver object null. Creating new driver object.");

		String strWebDrivePath = propertyUtil.getProperty(Constants.PROPERTY_KEY_BROWSER_WEBDRIVER_PATH);
		LoggerUtil.debug("strWebDrivePath : " + strWebDrivePath);

		File file = new File(strWebDrivePath);
		if (file.exists()) {
			strWebDrivePath = file.getAbsolutePath();
		} else {
			throw new Exception("No WebDriver found at location: " + file.getAbsolutePath());
		}

		if (parameterMap.containsKey("proxy")) {
			if (parameterMap.get("proxy").equalsIgnoreCase("true"))
				isBrowserUpProxy = true;
		}
		String strIsProxyKey = propertyUtil.getProperty(Constants.PROPERTY_KEY_BROWSER_PROXY);
		if (strIsProxyKey != null && strIsProxyKey.equalsIgnoreCase("true"))
			isBrowserUpProxy = true;

		String strIsHeadLess = propertyUtil.getProperty(Constants.PROPERTY_KEY_BROWSER_HEADLESS);
		LoggerUtil.debug("strIsHeadLess : " + strIsHeadLess);

		String strRemoteDriver = propertyUtil.getProperty(Constants.PROPERTY_KEY_BROWSER_REMOTE);
		LoggerUtil.debug("strRemoteDriver : " + strRemoteDriver);

		String strPrivateMode = propertyUtil.getProperty(Constants.PROPERTY_KEY_BROWSER_PRIVATE_MODE);
		boolean privateMode = false;
		if ("true".equals(strPrivateMode)) {
			privateMode = true;
		}
		LoggerUtil.debug("privateMode : " + privateMode);
		Browsers browser = Browsers.valueOf(browserName);
		LoggerUtil.debug("strRemoteDriver : " + strRemoteDriver);
		if (strRemoteDriver != null && "true".equalsIgnoreCase(strRemoteDriver)) {

			String strPlatform = propertyUtil.getProperty(Constants.PROPERTY_KEY_BROWSER_REMOTE_PLATFORM);
			if (strPlatform == null || "".equals(strPlatform))
				throw new Exception(Constants.PROPERTY_KEY_BROWSER_REMOTE_PLATFORM
						+ " key not defined or empty in TestAutomationFramework.properties");

			LoggerUtil.debug("strPlatform : " + strPlatform);

			String strNodeUrl = propertyUtil.getProperty(Constants.PROPERTY_KEY_BROWSER_REMOTE_NODEURL);
			if (strNodeUrl == null || "".equals(strNodeUrl))
				throw new Exception(Constants.PROPERTY_KEY_BROWSER_REMOTE_NODEURL
						+ " key not defined or empty in TestAutomationFramework.properties");

			LoggerUtil.debug("strNodeUrl : " + strNodeUrl);
			@SuppressWarnings("deprecation")
			String platformKey = CapabilityType.PLATFORM;

			switch (browser) {
			case internetexplorer:
				InternetExplorerOptions ieOptions = getIEOptions(privateMode, propertyUtil, isBrowserUpProxy);
				ieOptions.setCapability(platformKey, Platform.fromString(strPlatform));
				ieOptions.setCapability(CapabilityType.PLATFORM_NAME, Platform.fromString(strPlatform));
				ieOptions.setCapability(CapabilityType.BROWSER_NAME, "internet explorer");
				RemoteWebDriver remoteDriver = new RemoteWebDriver(new URL(strNodeUrl), ieOptions);
				remoteDriver.setFileDetector(new LocalFileDetector());
				driver = remoteDriver;
				break;

			case firefox:
				FirefoxOptions ffOptions = getFirefoxOptions(privateMode, propertyUtil, isBrowserUpProxy);
				ffOptions.setCapability(platformKey, Platform.fromString(strPlatform));
				ffOptions.setCapability(CapabilityType.PLATFORM_NAME, Platform.fromString(strPlatform));
				ffOptions.setCapability(CapabilityType.BROWSER_NAME, "firefox");
				remoteDriver = new RemoteWebDriver(new URL(strNodeUrl), ffOptions);
				remoteDriver.setFileDetector(new LocalFileDetector());
				driver = remoteDriver;
				break;

			case chrome:
				ChromeOptions chromeOptions = getChromeOptions(privateMode, propertyUtil, isBrowserUpProxy);
				chromeOptions.setCapability(platformKey, Platform.fromString(strPlatform));
				chromeOptions.setCapability(CapabilityType.PLATFORM_NAME, Platform.fromString(strPlatform));
				chromeOptions.setCapability(CapabilityType.BROWSER_NAME, "chrome");
				remoteDriver = new RemoteWebDriver(new URL(strNodeUrl), chromeOptions);
				remoteDriver.setFileDetector(new LocalFileDetector());
				driver = remoteDriver;
				break;

			case edgelegacy:
			case edge:
				EdgeOptions edgeOptions = getEdgeOptions(privateMode, propertyUtil, isBrowserUpProxy);
				edgeOptions.setCapability(platformKey, Platform.fromString(strPlatform));
				edgeOptions.setCapability(CapabilityType.PLATFORM_NAME, Platform.fromString(strPlatform));
				edgeOptions.setCapability(CapabilityType.BROWSER_NAME, "edge");
				remoteDriver = new RemoteWebDriver(new URL(strNodeUrl), edgeOptions);
				remoteDriver.setFileDetector(new LocalFileDetector());
				driver = remoteDriver;
				break;

			case safari:
				SafariOptions safariOptions = getSafariOptions(propertyUtil, isBrowserUpProxy);
				safariOptions.setCapability(platformKey, Platform.fromString(strPlatform));
				safariOptions.setCapability(CapabilityType.PLATFORM_NAME, Platform.fromString(strPlatform));
				safariOptions.setCapability(CapabilityType.BROWSER_NAME, "safari");
				remoteDriver = new RemoteWebDriver(new URL(strNodeUrl), safariOptions);
				remoteDriver.setFileDetector(new LocalFileDetector());
				driver = remoteDriver;
				break;

			default:
				throw new Exception("browser key is missing in Browser.properties.");
			}

		} else if (strIsHeadLess == null || "".equals(strIsHeadLess) || "false".equalsIgnoreCase(strIsHeadLess)) {
			switch (browser) {
			case internetexplorer:
				System.setProperty("webdriver.ie.driver", strWebDrivePath);
				InternetExplorerOptions ieOptions = getIEOptions(privateMode, propertyUtil, isBrowserUpProxy);
				driver = new InternetExplorerDriver(ieOptions);
				LoggerUtil.debug("Created driver object of: " + browserName);
				break;

			case firefox:
				System.setProperty("webdriver.gecko.driver", strWebDrivePath);
				FirefoxOptions firefoxOptions = getFirefoxOptions(privateMode, propertyUtil, isBrowserUpProxy);
				driver = new FirefoxDriver(firefoxOptions);
				LoggerUtil.debug("Created driver object of: " + browserName + ".");
				break;

			case chrome:
				System.setProperty("webdriver.chrome.driver", strWebDrivePath);
				ChromeOptions chromeOptions = getChromeOptions(privateMode, propertyUtil, isBrowserUpProxy);
				driver = new ChromeDriver(chromeOptions);
				LoggerUtil.debug("Created driver object of: " + browserName + ".");
				break;

			case edgelegacy:
			case edge:
				System.setProperty("webdriver.edge.driver", strWebDrivePath);
				EdgeOptions edgeOptions = getEdgeOptions(privateMode, propertyUtil, isBrowserUpProxy);
				driver = new EdgeDriver(edgeOptions);
				LoggerUtil.debug("Created driver object of: " + browserName + ".");
				break;

			case safari:
				System.setProperty("webdriver.safari.driver", strWebDrivePath);
				SafariOptions safariOptions = getSafariOptions(propertyUtil, isBrowserUpProxy);
				driver = new SafariDriver(safariOptions);
				LoggerUtil.debug("Created driver object of: " + browserName + ".");
				break;

			default:
				throw new Exception("browser key is missing in TestAutomationFramework.properties.");
			}
		} else {
			HtmlUnitDriver unitDriver;

			DesiredCapabilities capabilitiesHU = DesiredCapabilities.htmlUnit();
			capabilitiesHU.setCapability(CapabilityType.ForSeleniumServer.ENSURING_CLEAN_SESSION, true);
			capabilitiesHU.setCapability(CapabilityType.UNEXPECTED_ALERT_BEHAVIOUR, UnexpectedAlertBehaviour.ACCEPT);
			capabilitiesHU.setJavascriptEnabled(true);

			switch (browser) {
			case internetexplorer:
				capabilitiesHU.setVersion(BrowserType.IE);
				unitDriver = new HtmlUnitDriver(capabilitiesHU);
				break;

			case firefox:
				capabilitiesHU.setVersion(BrowserType.FIREFOX);
				unitDriver = new HtmlUnitDriver(capabilitiesHU);
				break;
			case edgelegacy:
			case edge:
				capabilitiesHU.setVersion(BrowserType.EDGE);
				unitDriver = new HtmlUnitDriver(capabilitiesHU);
				break;

			case safari:
				capabilitiesHU.setVersion(BrowserType.SAFARI);
				unitDriver = new HtmlUnitDriver(capabilitiesHU);
				break;

			default:
				unitDriver = new HtmlUnitDriver(BrowserVersion.INTERNET_EXPLORER);
				break;
			}

			unitDriver.setJavascriptEnabled(true);
			driver = unitDriver;
			if (browser == Browsers.chrome) {
				System.setProperty("webdriver.chrome.driver", strWebDrivePath);
				ChromeOptions options = new ChromeOptions();
				options.addArguments("--headless", "--disable-gpu", "--window-size=1920,1200",
						"--ignore-certificate-errors", "--silent");
				driver = new ChromeDriver(options);
			}

			LoggerUtil.debug("Created driver object of: " + browserName + ".");

		}

		LoggerUtil.debug("End of getDriver");
		return driver;
	}

	public InternetExplorerOptions getIEOptions(boolean privateMode, PropertyUtil propertyUtil,
			boolean isBrowserUpProxy) throws Exception {
		InternetExplorerOptions ieOptions = new InternetExplorerOptions();
		ieOptions.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);
		// clean IE cache before before launching browser.
		// ieOptions.setCapability(CapabilityType.ForSeleniumServer.ENSURING_CLEAN_SESSION,
		// true); //not working
		ieOptions.setCapability("ie.ensureCleanSession", true);
		ieOptions.setCapability(CapabilityType.ELEMENT_SCROLL_BEHAVIOR, 1);
		ieOptions.setCapability("logLevel", "ERROR");
		ieOptions.setCapability(CapabilityType.UNEXPECTED_ALERT_BEHAVIOUR, UnexpectedAlertBehaviour.DISMISS);
		ieOptions.setCapability(InternetExplorerDriver.INTRODUCE_FLAKINESS_BY_IGNORING_SECURITY_DOMAINS, true);
		ieOptions.setCapability(InternetExplorerDriver.IGNORE_ZOOM_SETTING, true);
		ieOptions.setCapability(InternetExplorerDriver.ENABLE_PERSISTENT_HOVERING, false);
		if (isBrowserUpProxy)
			ieOptions.setCapability(CapabilityType.PROXY, getProxy());

		String requireFocus = propertyUtil.getProperty(Constants.PROPERTY_KEY_BROWSER_IE_REQUIREFOCUS);
		boolean isRequireFocus = true;
		if (requireFocus != null && "false".equalsIgnoreCase(requireFocus))
			isRequireFocus = false;
		LoggerUtil.debug("requireFocus : " + isRequireFocus);
		ieOptions.setCapability(InternetExplorerDriver.REQUIRE_WINDOW_FOCUS, isRequireFocus);

		String nativeEvents = propertyUtil.getProperty(Constants.PROPERTY_KEY_BROWSER_NATIVE_EVENTS);
		boolean enableNativeEvents = true;
		if (nativeEvents != null && "false".equalsIgnoreCase(nativeEvents))
			enableNativeEvents = false;
		LoggerUtil.debug("enableNativeEvents : " + enableNativeEvents);
		ieOptions.setCapability(CapabilityType.HAS_NATIVE_EVENTS, enableNativeEvents);

		if (privateMode) {
			ieOptions.setCapability(InternetExplorerDriver.FORCE_CREATE_PROCESS, true);
			ieOptions.addCommandSwitches("-private");
		}
		return ieOptions;
	}

	public FirefoxOptions getFirefoxOptions(boolean privateMode, PropertyUtil propertyUtil, boolean isBrowserUpProxy)
			throws Exception {
		FirefoxOptions firefoxOptions = new FirefoxOptions();
		firefoxOptions.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);
		firefoxOptions.setCapability(CapabilityType.ACCEPT_INSECURE_CERTS, IsAllowInSecureSites);
		firefoxOptions.setCapability(CapabilityType.ELEMENT_SCROLL_BEHAVIOR, 1);
		firefoxOptions.setCapability(CapabilityType.ForSeleniumServer.ENSURING_CLEAN_SESSION, true);
		firefoxOptions.setCapability("logLevel", "ERROR");
		firefoxOptions.setCapability(CapabilityType.UNEXPECTED_ALERT_BEHAVIOUR, UnexpectedAlertBehaviour.DISMISS);
		if (isBrowserUpProxy)
			firefoxOptions.setCapability(CapabilityType.PROXY, getProxy());

		String strMarionette = propertyUtil.getProperty(Constants.PROPERTY_KEY_BROWSER_FIREFOX_MARIONETTE);
		boolean marionette = false;
		if (strMarionette != null && "true".equalsIgnoreCase(strMarionette))
			marionette = true;
		LoggerUtil.debug("marionette : " + marionette);
		firefoxOptions.setCapability(FirefoxDriver.MARIONETTE, marionette);

		String nativeEvents = propertyUtil.getProperty(Constants.PROPERTY_KEY_BROWSER_NATIVE_EVENTS);
		boolean enableNativeEvents = true;
		if (nativeEvents != null && "false".equalsIgnoreCase(nativeEvents))
			enableNativeEvents = false;
		LoggerUtil.debug("enableNativeEvents : " + enableNativeEvents);
		firefoxOptions.setCapability(CapabilityType.HAS_NATIVE_EVENTS, enableNativeEvents);

		String firefoxExePath = propertyUtil.getProperty(BROWSER_BINARY);
		if (firefoxExePath != null && "true".equalsIgnoreCase(firefoxExePath))
			firefoxOptions.setBinary(firefoxExePath);

		if (privateMode) {
			firefoxOptions.addArguments("-private");
			firefoxOptions.addPreference("browser.privatebrowsing.autostart", true);
		}
		LoggerUtil.debug("firefoxExePath : " + firefoxExePath);

		return firefoxOptions;
	}

	public ChromeOptions getChromeOptions(boolean privateMode, PropertyUtil propertyUtil, boolean isBrowserUpProxy)
			throws Exception {
		ChromeOptions chromeOptions = new ChromeOptions();
		chromeOptions.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);
		chromeOptions.setCapability(CapabilityType.ACCEPT_INSECURE_CERTS, IsAllowInSecureSites);
		chromeOptions.setCapability(CapabilityType.ELEMENT_SCROLL_BEHAVIOR, 1);
		chromeOptions.setCapability(CapabilityType.ForSeleniumServer.ENSURING_CLEAN_SESSION, true);
		chromeOptions.setCapability("logLevel", "ERROR");
		chromeOptions.setCapability(CapabilityType.UNEXPECTED_ALERT_BEHAVIOUR, UnexpectedAlertBehaviour.DISMISS);
		if (isBrowserUpProxy)
			chromeOptions.setCapability(CapabilityType.PROXY, getProxy());

		String nativeEvents = propertyUtil.getProperty(Constants.PROPERTY_KEY_BROWSER_NATIVE_EVENTS);
		boolean enableNativeEvents = true;
		if (nativeEvents != null && "false".equalsIgnoreCase(nativeEvents))
			enableNativeEvents = false;
		LoggerUtil.debug("enableNativeEvents : " + enableNativeEvents);

		chromeOptions.setCapability(CapabilityType.HAS_NATIVE_EVENTS, enableNativeEvents);

		String chromeExePath = propertyUtil.getProperty(BROWSER_BINARY);
		if (chromeExePath != null && "true".equalsIgnoreCase(chromeExePath))
			chromeOptions.setBinary(chromeExePath);

		if (privateMode) {
			chromeOptions.addArguments("--incognito");
		}
		LoggerUtil.debug("chromeExePath : " + chromeExePath);

		return chromeOptions;
	}

	public EdgeOptions getEdgeOptions(boolean privateMode, PropertyUtil propertyUtil, boolean isBrowserUpProxy)
			throws Exception {
		EdgeOptions edgeOptions = new EdgeOptions();
		edgeOptions.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);
		edgeOptions.setCapability(CapabilityType.ACCEPT_INSECURE_CERTS, IsAllowInSecureSites);
		edgeOptions.setCapability(CapabilityType.ELEMENT_SCROLL_BEHAVIOR, 1);
		edgeOptions.setCapability(CapabilityType.ForSeleniumServer.ENSURING_CLEAN_SESSION, true);
		edgeOptions.setCapability("logLevel", "ERROR");
		edgeOptions.setCapability(CapabilityType.UNEXPECTED_ALERT_BEHAVIOUR, UnexpectedAlertBehaviour.DISMISS);
		if (isBrowserUpProxy)
			edgeOptions.setCapability(CapabilityType.PROXY, getProxy());

		String nativeEvents = propertyUtil.getProperty(Constants.PROPERTY_KEY_BROWSER_NATIVE_EVENTS);
		boolean enableNativeEvents = true;
		if (nativeEvents != null && "false".equalsIgnoreCase(nativeEvents))
			enableNativeEvents = false;
		LoggerUtil.debug("enableNativeEvents : " + enableNativeEvents);

		edgeOptions.setCapability(CapabilityType.HAS_NATIVE_EVENTS, enableNativeEvents);

		if (privateMode) {
			edgeOptions.setCapability("ms:inPrivate", true);
		}

		return edgeOptions;
	}

	public SafariOptions getSafariOptions(PropertyUtil propertyUtil, boolean isBrowserUpProxy) throws Exception {
		SafariOptions safariOptions = new SafariOptions();
		safariOptions.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);
		// ACCEPT_INSECURE_CERTS:Not supported in Safari 12 and above, need to manually
		// add the certificate in Mac System Keychain
		safariOptions.setCapability(CapabilityType.ELEMENT_SCROLL_BEHAVIOR, 1);
		safariOptions.setCapability(CapabilityType.ForSeleniumServer.ENSURING_CLEAN_SESSION, true);
		safariOptions.setCapability("logLevel", "ERROR");
		safariOptions.setCapability(CapabilityType.UNEXPECTED_ALERT_BEHAVIOUR, UnexpectedAlertBehaviour.DISMISS);
		if (isBrowserUpProxy)
			safariOptions.setCapability(CapabilityType.PROXY, getProxy());

		String nativeEvents = propertyUtil.getProperty(Constants.PROPERTY_KEY_BROWSER_NATIVE_EVENTS);
		boolean enableNativeEvents = true;
		if (nativeEvents != null && "false".equalsIgnoreCase(nativeEvents))
			enableNativeEvents = false;
		LoggerUtil.debug("enableNativeEvents : " + enableNativeEvents);

		safariOptions.setCapability(CapabilityType.HAS_NATIVE_EVENTS, enableNativeEvents);

		return safariOptions;
	}

	private Proxy getProxy() {

		// start the proxy
		this.proxy = new BrowserUpProxyServer();
		this.proxy.setTrustAllServers(true);
		this.proxy.start();
		this.proxy.enableHarCaptureTypes(CaptureType.REQUEST_CONTENT, CaptureType.RESPONSE_CONTENT);
		// get the Selenium proxy object
		return ClientUtil.createSeleniumProxy(this.proxy);
	}

	public BrowserUpProxy getBrowserProxy() {
		return this.proxy;
	}
}