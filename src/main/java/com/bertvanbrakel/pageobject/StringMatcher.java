package com.bertvanbrakel.pageobject;

import java.util.regex.Pattern;

public abstract class StringMatcher {

	protected abstract boolean matches(String actual);
	
	protected static StringMatcher expression(final String expect) {
		if (expect.startsWith("exact:")) {
			return exact(expect.substring(6));
		} else if (expect.startsWith("re:")) {
			return regExp(expect.substring(3));
		} else if (expect.startsWith("ant:")) {
			return antExp(expect.substring(4));
		} else {
			return antExp(expect);
		}
	}
	
	public static StringMatcher exact(final String expect){
		return new StringMatcher() {
			@Override
			public boolean matches(String actual) {
				return expect.substring(6).equalsIgnoreCase(actual);
			}
		};
	}
	
	public static StringMatcher regExp(final String  expect){
		return new StringMatcher(){
			final private Pattern pattern = Pattern.compile(expect,Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
			@Override
			public boolean matches(String actual) {
				return pattern.matcher(actual).matches();
			}
		};
	}
	
	public static StringMatcher antExp(final String  expect){
		return new StringMatcher(){
			final private Pattern pattern = Pattern.compile(stringToAntRegExp(expect),Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
			@Override
			public boolean matches(String actual) {
				return pattern.matcher(actual).matches();
			}
		};
	}
	
	protected static String stringToAntRegExp(String s) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);
			switch (c) {
			case '*':
				sb.append(".*");
				break;
			case '?':
				sb.append(".{1}");
				break;
			case '.':
				sb.append("\\.");
				break;
			default:
				sb.append(c);
				break;
			}
		}
		return sb.toString();
	}
}