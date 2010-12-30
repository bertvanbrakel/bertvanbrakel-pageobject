package com.bertvanbrakel.pageobject;

public class ParseException extends PageObjectException {

	private static final long serialVersionUID = -6732519405111531019L;

	public ParseException(String msg, Throwable t) {
		super(msg, t);
	}

	public ParseException(String msg) {
		super(msg);
	}

	public ParseException(Throwable t) {
		super(t);
	}

}
