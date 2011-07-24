package com.bertvanbrakel.pageobject;

import static com.bertvanbrakel.lang.Check.checkNotNull;

import org.apache.commons.lang.StringUtils;
public class LookupParser {

	private String chars;
	private int pos;
	private static final String QUOTES = "'`\"";
	private static final char EQUALS = '=';
	private static final char PAIR_SEP = ',';

	private LookupParser() {
	}

	public static interface ParseCallback {
		public void found(String name, String value) throws ParseException;
	}

	public final static void parse(String expression, ParseCallback callback){
		checkNotNull("parseCallback", callback);
		LookupParser parser = new LookupParser();
		parser.internalParse(expression, callback);
	}
	
	private void internalParse(String expression, ParseCallback callback) {
		if (StringUtils.isEmpty(expression)) {
			return;
		}
		chars = expression;
		pos = 0;
		while (hasMore()) {
			String name = readValueUntilOrEnd(EQUALS);
			expect(EQUALS);
			next();
			String value = readValueUntilOrEnd(PAIR_SEP);
			if (hasMore()) {
				expect(PAIR_SEP);
				next();
			}
			try {
				callback.found(name, value);
			} catch (ParseException e) {
				
				throw new ParseException(
						String.format(
								"Error parsing name/values '%s' at position %s. Invalid name/value pair. Name  is '%s', value is '%s'",
								chars, pos, name, value));
			}
		}
	}

	private void expect(char expectChar) {
		if (!canRead()) {
			throwError(String
					.format("expected '%s' after name part, instead got to end of expression",
							expectChar));
		}
		if (read() != expectChar) {
			throwError(String.format(
					"expected '%s' after name part, instead got '%s'",
					expectChar, read()));
		}
	}

	private String readValueUntilOrEnd(char untilChar) {
		readWhiteSpace();
		StringBuilder sb = new StringBuilder();
		char quoteChar = Character.MAX_VALUE;
		boolean inQuote = false;
		loop: while (canRead()) {
			char c = read();
			if (inQuote) {
				if (c == quoteChar) {
					// end the quote
					next();
					break loop;
				} else {
					// carry on
					sb.append(c);
				}
			} else if (isQuote(c)) {
				if (sb.length() == 0) {
					// start of value, so allow quote
					inQuote = true;
					quoteChar = c;
				} else {
					//allow quote char as normal and don't make is special
					sb.append(c);
//					// only allow quote chars to be in other quotes or at the
//					// start/end of name/values
//					throwError(String
//							.format("Illegal quote character '%s', expect it to be the first part of the name or value, or within other quotes. Name or value so far is '%s'",
//									c, sb.toString()));
				}
			} else if (c == untilChar) {
				// reached end
				break loop;
			} else {
				sb.append(c);
			}
			next();
		}
		readWhiteSpace();
		return sb.toString();
	}

	private void throwError(String msg) {
		if (canRead()) {
			throw new ParseException(
					String.format(
							"Error parsing name/values '%s' at position %s, character '%s'. Error is: %s",
							chars, pos, read(), msg));
		} else {
			throw new ParseException(
					String.format(
							"Error parsing name/values '%s'. At end of expression'. Error is: %s",
							chars, msg));
		}
	}
	
	private boolean canRead() {
		return pos < chars.length();
	}
	
	private char read() {
		return chars.charAt(pos);
	}

	private void next() {
		pos++;
	}

	private boolean hasMore() {
		return pos < chars.length() - 1;
	}
	
	private void readWhiteSpace() {
		while (canRead()) {
			char c = read();
			if (!Character.isWhitespace(c)) {
				return;
			}
			next();
		}
	}

	private boolean isQuote(char c) {
		return QUOTES.indexOf(c) != -1;
	}

}
