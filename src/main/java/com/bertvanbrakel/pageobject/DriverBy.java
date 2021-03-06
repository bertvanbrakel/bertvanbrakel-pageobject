package com.bertvanbrakel.pageobject;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchFrameException;
import org.openqa.selenium.WebDriver;

import com.bertvanbrakel.pageobject.LookupParser.ParseCallback;

public abstract class DriverBy {

	public abstract WebDriver find(WebDriver d);
	
	/**
	 * javascript= window.name= window.title= head.title=
	 * 
	 * @param expression
	 * @return
	 * @throws ParseException
	 */
	public static List<DriverBy> expression(String expression) throws ParseException {
		final List<DriverBy> bys = new ArrayList<DriverBy>();
		ParseCallback callback = new ParseCallback() {
			@Override
			public void found(String name, String value) throws ParseException {
				name = name.toLowerCase();
				bys.add(textToMatcher(name, value));
			}
		};
		LookupParser.parse(expression, callback);
		return bys;
	}

	private static DriverBy textToMatcher(final String name,
			final String value) {
		DriverBy by;
		if ("windowTitle".equals(name)) {
			by = DriverBy.windowTitle(value);
		} else if ("headTitle".equals(name)) {
			by = DriverBy.headTitle(value);
		} else if ("windowName".equals(name)) {
			by = DriverBy.windowName(value);
		} else if ("url".equals(name)) {
			by = DriverBy.url(value);
		} else if ("frame".equals(name)) {
			by = DriverBy.frame(value);
		} else if ("source".equals(name)) {
			by = DriverBy.source(value);
		} else if ("javascript".equals(name)) {
			by = DriverBy.javascript(value);
		} else {
			throw new ParseException(String.format(
					"Don't know how to lookup window by '%s'", name));
		}
		return by;
	}
	
	/**
	 * Match by window title. Aka what is currently displayed in the browsers window bar
	 * 
	 * @param value
	 * @return
	 */
	public static DriverBy windowTitle(final String value) {
		final StringMatcher valueMatcher = StringMatcher.expression(value);
		return new DriverBy() {
			@Override
			public WebDriver find(WebDriver d) {
				return valueMatcher.matches(d.getTitle())?d:null;
			}
			@Override
			public String toString(){
				return String.format("DriverBy[windowTitle='%s']", value);
			}
		};
	}

	/**
	 * Match by the title tag in the head. This can be different to the window title as the title
	 * could of changed dynamically
	 * 
	 * @param value
	 * @return
	 */
	public static DriverBy headTitle(final String value) {
		final StringMatcher valueMatcher = StringMatcher.expression(value);
		return new DriverBy() {
			@Override
			public WebDriver find(WebDriver d) {
				try {
					return valueMatcher.matches(d.findElement(
							By.xpath("/html/head/title")).getText())?d:null;
				} catch (NoSuchElementException e) {
					// ignore. Obviously not a match
				}
				return null;
			}
			@Override
			public String toString(){
				return String.format("DriverBy[headTitle='%s']", value);
			}
		};
	}

	/**
	 * Match by the windows url
	 * 
	 * @param value
	 * @return
	 */
	public static DriverBy url(final String value) {
		final StringMatcher valueMatcher = StringMatcher.expression(value);
		return new DriverBy() {
			@Override
			public WebDriver find(WebDriver d) {
				return valueMatcher.matches(d.getCurrentUrl())?d:null;
			}
			@Override
			public String toString(){
				return String.format("DriverBy[url='%s']", value);
			}
		};
	}

	/**
	 * Match by the page source
	 * 
	 * @param value
	 * @return
	 */
	public static DriverBy source(final String value) {
		final StringMatcher valueMatcher = StringMatcher.expression(value);
		return new DriverBy() {
			@Override
			public WebDriver find(WebDriver d) {
				return valueMatcher.matches(d.getPageSource())?d:null;
			}
			@Override
			public String toString(){
				return String.format("DriverBy[source='%s']", value);
			}
		};
	}
	
	public static DriverBy frame(final String value) {
		return new DriverBy() {
			@Override
			public WebDriver find(WebDriver d) {
				try {
					return d.switchTo().frame(value);
				} catch(NoSuchFrameException e){
					// ignore. Obviously not a match
					//throw new FinderException(String.format( "Can't find frame '%s'",value), e);
				}
				return null;
			}
			@Override
			public String toString(){
				return String.format("DriverBy[frame='%s']", value);
			}
		};
	}

	/**
	 * Match by some javascript expression. Matches if the expression returns 't','true','1', false
	 * in all other cases, including if the browser is not javascript capable
	 * 
	 * @param value
	 * @return
	 */
	public static DriverBy javascript(final String value) {
		return new DriverBy() {
			@Override
			public WebDriver find(WebDriver d) {
				if (d instanceof JavascriptExecutor) {
					Object ret = ((JavascriptExecutor) d)
							.executeScript(value);
					if (ret != null) {
						String s = ret.toString().toLowerCase();
						if ("t".equals(s) || "true".equals(s)
								|| "1".equals(s)) {
							return d;
						}
					}
				}
				return null;
			}
			@Override
			public String toString(){
				return String.format("DriverBy[javascript='%s']", value);
			}
		};
	}

	/**
	 * Match by the windows name, as returned in javascript by window.name
	 * 
	 * @param value
	 * @return
	 */
	public static DriverBy windowName(final String value) {
		final StringMatcher valueMatcher = StringMatcher.expression(value);
		return new DriverBy() {
			@Override
			public WebDriver find(WebDriver d) {
				if (d instanceof JavascriptExecutor) {
					Object windowName = ((JavascriptExecutor) d)
							.executeScript("window.name");
					if (windowName != null && valueMatcher.matches(windowName.toString())){
						return d;
					}
				}
				return null;
			}
			@Override
			public String toString(){
				return String.format("DriverBy[windowName='%s']", value);
			}
		};
	};
}