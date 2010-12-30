package com.bertvanbrakel.pageobject;

import java.util.ArrayList;
import java.util.List;

import com.bertvanbrakel.pageobject.LookupParser.ParseCallback;

public class DriverLookupParser {

	/**
	 * javascript= window.name= window.title= head.title=
	 * 
	 * @param expression
	 * @return
	 * @throws ParseException
	 */
	public static List<DriverBy> parse(String expression) throws ParseException {
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

	protected static DriverBy textToMatcher(final String name,
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

}
