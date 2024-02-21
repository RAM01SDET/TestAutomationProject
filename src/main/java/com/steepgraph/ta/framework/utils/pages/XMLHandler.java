package com.steepgraph.ta.framework.utils.pages;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.Stack;

import org.w3c.dom.Document;

public class XMLHandler extends DefaultHandler {
	public final static String LINE_NUMBER_KEY_NAME = "lineNumber";
	private Document xMLDocument;
	private Locator locator;
	final Stack<Element> elementStack = new Stack<Element>();
	final StringBuilder textBuffer = new StringBuilder();
	@Override
	public void setDocumentLocator(final Locator locator) {
		this.locator = locator; // Save the locator, so that it can be used later for line tracking when
								// traversing nodes.
	}
	public XMLHandler(Document document) {
		this.xMLDocument = document;
	}
	@Override
	public void startElement(final String uri, final String localName, final String qName,
			final Attributes attributes) throws SAXException {
		addTextIfNeeded();
		final Element el = xMLDocument.createElement(qName);
		for (int i = 0; i < attributes.getLength(); i++) {
			el.setAttribute(attributes.getQName(i), attributes.getValue(i));
		}
		el.setUserData(LINE_NUMBER_KEY_NAME, String.valueOf(this.locator.getLineNumber()), null);
		elementStack.push(el);
	}
	
	@Override
	public void endElement(final String uri, final String localName, final String qName) {
		addTextIfNeeded();
		final Element closedEl = elementStack.pop();
		if (elementStack.isEmpty()) { // Is this the root element?
			xMLDocument.appendChild(closedEl);
		} else {
			final Element parentEl = elementStack.peek();
			parentEl.appendChild(closedEl);
		}
	}


	// Outputs text accumulated under the current node
	private void addTextIfNeeded() {
		if (textBuffer.length() > 0) {
			final Element el = elementStack.peek();
			final Node textNode = xMLDocument.createTextNode(textBuffer.toString());
			el.appendChild(textNode);
			textBuffer.delete(0, textBuffer.length());
		}
	}

	

}
