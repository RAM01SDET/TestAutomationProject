package com.steepgraph.ta.framework.utils.pages;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.steepgraph.ta.framework.Constants;

/**
 * Class for parsing XML file from which test cases are executed
 * 
 * @author Steepgraph Systems
 */
public class TestCase {

	Node rootElement;

	Element currentNode;

	NodeList childNodes;

	int currentNodeIndex;

	int childNodeLength;

	String browser;

	String enoviaRelease;

	String url;

	String fileName;

	String lineNumber;

	public String getBrowser() throws Exception {
		return browser;
	}

	public void setBrowser(String browser) throws Exception {
		this.browser = browser;
	}

	public String getEnoviaRelease() throws Exception {
		return enoviaRelease;
	}

	public void setEnoviaRelease(String enoviaRelease) throws Exception {
		this.enoviaRelease = enoviaRelease;
	}

	public String getUrl() throws Exception {
		return url;
	}

	public void setUrl(String url) throws Exception {
		this.url = url;
	}

	public int getLength() throws Exception {
		return childNodeLength;
	}

	/**
	 * Method which parses XML file
	 * 
	 * @author Steepgraph Systems
	 * @param filepath
	 * @param lineNumber
	 * @return void
	 * @throws Exception
	 */
	public void parseXML(String filepath) throws Exception {
		File xmlFile = new File(filepath);
		fileName = xmlFile.getName();
		SAXParser xMLParser;
		Document document;

		try {
			// Create new instance of DocumentBuilderFactory
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			// Create new instance of DocumentBuilder
			DocumentBuilder builder = factory.newDocumentBuilder();
			// Get Document to parse
			SAXParserFactory factory1 = SAXParserFactory.newInstance();
			xMLParser = factory1.newSAXParser();
			document = builder.newDocument();
			FileInputStream is = new FileInputStream(xmlFile);
			xMLParser.parse(is, new XMLHandler(document));
		} catch (ParserConfigurationException e) {
			throw new RuntimeException("Exception occured while reading xml", e);
		}
		// Get root element of XML file
		rootElement = document.getDocumentElement();
		// Get child nodes of root node
		childNodes = rootElement.getChildNodes();
		// Get child nodes list length
		childNodeLength = childNodes.getLength();
		// Set current element index
		currentNodeIndex = -1;
		currentNode = null;

		// Get attributes of root node and set their values
		NamedNodeMap rootAttributeMap = rootElement.getAttributes();
		if (rootAttributeMap != null) {
			int length = rootAttributeMap.getLength();
			for (int j = 0; j < length; j++) {
				Attr attr = (Attr) rootAttributeMap.item(j);

				String attrName = attr.getNodeName();
				String attrValue = attr.getNodeValue();
				attrName = attrName.toLowerCase();

				if (Constants.PROPERTY_KEY_BROWSER_NAME.equalsIgnoreCase(attrName)) {
					browser = attrValue;
				} else if ("url".equalsIgnoreCase(attrName)) {
					url = attrValue;
				} else if ("enoviarelease".equalsIgnoreCase(attrName)) {
					enoviaRelease = attrValue;
				}
			}
		}
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getLineNumber() {
		return lineNumber;
	}

	public void setLineNumber(String lineNumber) {
		this.lineNumber = lineNumber;
	}

	/**
	 * Method which gets next element from XML file to parse
	 * 
	 * @author Steepgraph Systems
	 * @return String
	 * @throws Exception
	 */
	public String getNextElement() throws Exception {
		currentNodeIndex++;
		Node nextNode = null;
		if (currentNode == null && childNodeLength > 0)
			nextNode = childNodes.item(currentNodeIndex);
		else
			nextNode = currentNode.getNextSibling();

		while (nextNode != null) {
			if (nextNode.getNodeType() == Node.ELEMENT_NODE) {

				currentNode = (Element) nextNode;
				setLineNumber((String) currentNode.getUserData(XMLHandler.LINE_NUMBER_KEY_NAME));
				return currentNode.getTagName();
			}
			nextNode = nextNode.getNextSibling();
		}
		return null;
	}

	/**
	 * Method which returns the map of attributes of processed element from XML file
	 * 
	 * @author Steepgraph Systems
	 * @return Map
	 * @throws Exception
	 */
	public Map<String, String> getAttributes() throws Exception {

		Map<String, String> attributeMap = new HashMap<String, String>();

		NamedNodeMap nameNodeAttrMap = currentNode.getAttributes();
		if (nameNodeAttrMap != null) {
			int length = nameNodeAttrMap.getLength();
			for (int j = 0; j < length; j++) {
				Attr attr = (Attr) nameNodeAttrMap.item(j);

				String attrName = attr.getNodeName();
				if (!attributeMap.containsKey(attrName))
					attributeMap.put(attrName, attr.getNodeValue());
			}
		}
		return attributeMap;
	}

}