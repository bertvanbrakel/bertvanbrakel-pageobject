package com.bertvanbrakel.pageobject;

public class PageObjectException extends RuntimeException {

	private static final long serialVersionUID = -4573245067199622790L;

	public PageObjectException(String msg, Throwable t) {
		super(msg, t);
	}

	public PageObjectException(String msg) {
		super(msg);
	}

	public PageObjectException(Throwable t) {
		super(t);
	}

}
