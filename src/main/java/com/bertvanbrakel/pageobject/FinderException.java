package com.bertvanbrakel.pageobject;


public class FinderException extends PageObjectException {

	private static final long serialVersionUID = 248740297149498813L;

	public FinderException(final String msg) {
        super(msg);
    }

	public FinderException(String msg, Throwable t) {
		super(msg, t);
	}

	public FinderException(Throwable t) {
		super(t);
	}

}
