package com.steepgraph.ta.framework.common;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;

public class PrerequisiteValidator {

	public static final String JAVASet1 = null;

	public static final String PREF_KEY = "org.username";

	/**
	 * 
	 * @param location path in the registry
	 * @param key      registry key
	 * @return registry value or null if not found
	 */

	public static final String readRegistry(String location, String key) {
		try {
			// Run reg query, then read output with StreamReader (internal class)
			Process process = Runtime.getRuntime().exec("reg query " + '"' + location + "\" /v " + key);

			StreamReader reader = new StreamReader(process.getInputStream());
			reader.start();
			process.waitFor();
			reader.join();
			String output = reader.getResult();

			// Output has the following format:
			// \n<Version information>\n\n<key>\t<registry type>\t<value>
			if (!output.contains("    ")) {
				return null;
			}

			// Parse out the value
			String[] parsed = output.split("    ");
			return parsed[parsed.length - 1];
		} catch (Exception e) {
			return null;
		}

	}

	static class StreamReader extends Thread {

		private InputStream is;

		private StringWriter sw = new StringWriter();

		public StreamReader(InputStream is) {
			this.is = is;
		}

		public void run() {
			try {
				int c;
				while ((c = is.read()) != -1)
					sw.write(c);

			} catch (IOException e) {
			}
		}

		public String getResult() {
			return sw.toString();
		}

	}

	public static void main(String[] args) throws IOException {

		// Create Log file for all pre-condition checks
		File file = new File("D:\\Amit\\Temp\\PreConditionofAutomation.txt");
		if (file.createNewFile()) {
			System.out.println("File is created!");
		} else {
			System.out.println("File already exists.");
		}

		// Write Content
		FileWriter writer = new FileWriter(file);
		writer.write("\r\n");
		writer.write("PreConditions of Internet Explorer for Execution of Automation which are missing:");

		String Javaversion = System.getProperty("java.version");
		if (!Javaversion.contains("1.8"))

		{
			writer.write("\r\n");
			writer.write("Java 8 must be installed to run framework");
		} else {

			// Java 8 is installed
		}

		String javaHome = System.getenv("JAVA_HOME");
		if (javaHome == null) {
			writer.write("\r\n");
			writer.write("Please set JAVA_HOME environment variable.\n");

		} else {
			// JAVA_HOME is set
		}

		// Check Path variable of System
		String path = System.getenv("PATH");
		if (path == null) {
			writer.write("\r\n");
			writer.write("Please set Path environment variable.\n");

		} else {
			// Path is set
		}

		// To check protected zone setting of internet explorer
		String Zone1 = PrerequisiteValidator.readRegistry(
				"HKEY_CURRENT_USER\\Software\\Microsoft\\Windows\\CurrentVersion\\Internet Settings\\Zones\\1", "2500")
				.trim();

		if (Zone1 == null) {
			writer.write("\r\n");
			writer.write("Please set value of Zone 1");
		} else {
			// Protected Zone 1 value is set
		}

		String Zone2 = PrerequisiteValidator.readRegistry(
				"HKEY_CURRENT_USER\\Software\\Microsoft\\Windows\\CurrentVersion\\Internet Settings\\Zones\\2", "2500")
				.trim();
		if (Zone2 == null) {
			writer.write("\r\n");
			writer.write("Please set value of Zone 2");
		} else {
			// Protected Zone 2 value is set
		}

		String Zone3 = PrerequisiteValidator.readRegistry(
				"HKEY_CURRENT_USER\\Software\\Microsoft\\Windows\\CurrentVersion\\Internet Settings\\Zones\\3", "2500")
				.trim();
		if (Zone3 == null) {
			writer.write("\r\n");
			writer.write("Please set value of Zone 3");
		} else {
			// Protected Zone 3 value is set
		}

		String Zone4 = PrerequisiteValidator.readRegistry(
				"HKEY_CURRENT_USER\\Software\\Microsoft\\Windows\\CurrentVersion\\Internet Settings\\Zones\\4", "2500")
				.trim();
		if (Zone4 == null) {
			writer.write("\r\n");
			writer.write("Please set value of Zone 4");
		} else {
			// Protected Zone 4 value is set
		}

		// checks all zone have same value

		// String expectedZoneValue = "0x3";

		if (Zone1.equals(Zone2) && Zone1.equals(Zone3) && Zone1.equals(Zone4)) {

			// Protected zone of internet explorer have same value

		} else {
			writer.write("\r\n");
			writer.write("Please set same value for protected zones");

		}

		// To check FEATURE_BFCACHE key is available in
		String BFCACHE_Key = PrerequisiteValidator.readRegistry(
				"HKEY_CURRENT_USER\\SOFTWARE\\Microsoft\\Internet Explorer\\MAIN\\FeatureControl\\FEATURE_BFCACHE",
				"iexplore.exe").trim();
		if (BFCACHE_Key == null) {
			writer.write("\r\n");
			writer.write("Please register FEATURE BFCACHE key");
		} else {
			// FEATURE BFCACHE key is available
		}

		// To check Zoom level setting of internet explorer

		String Zoomlevel = PrerequisiteValidator
				.readRegistry("HKEY_CURRENT_USER\\Software\\Microsoft\\Internet Explorer\\Zoom", "ZoomFactor").trim();
		System.out.println(Zoomlevel);

		if (Zoomlevel == null) {
			writer.write("\r\n");
			writer.write("Please set value of Zoom");
		} else if (!Zoomlevel.equalsIgnoreCase("0x186a0")) {
			writer.write("\r\n");
			writer.write("Zoom value is not 100%");
		} else {
			// Zoom value is 100%
		}

		writer.close();
	}

}