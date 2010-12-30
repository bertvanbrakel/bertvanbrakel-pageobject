package com.bertvanbrakel.pageobject;

import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.By;

import com.bertvanbrakel.pageobject.LookupParser.ParseCallback;

public class WebElementLookupParser {
	public static List<By> parse(String expression) throws ParseException {
		final List<By> bys = new ArrayList<By>();
		ParseCallback callback = new ParseCallback() {
			@Override
			public void found(String name, String value) throws ParseException {
				name = name.toLowerCase();
				bys.add(textToBy(name,value));
			}
		};
		LookupParser.parse(expression, callback);
		return bys;
	}
	
	protected static By textToBy(String  name,String value){
		By by;
		if ("id".equals(name)) {
			by = By.id(value);
		} else if ("className".equals(name)) {
			by = By.className(value);
		} else if ("cssSelector".equals(name)) {
			by = By.cssSelector(value);
		} else if ("linkText".equals(name)) {
			by = By.linkText(value);
		} else if ("name".equals(name)) {
			by = By.name(value);
		} else if ("tagName".equals(name)) {
			by = By.tagName(value);
		} else if ("xpath".equals(name)) {
			by = By.xpath(value);
		} else if ("partialLinkText".equals(name)) {
			by = By.partialLinkText(value);
		} else {
			throw new ParseException(String.format(
					"Don't know how to lookup element by '%s'", name));
		}
		return by;
	}
}
