package com.bertvanbrakel.pageobject.ui;

import org.openqa.selenium.WebElement;

import com.bertvanbrakel.pageobject.Finder;

public class Form extends Element {

	public Form(Finder<WebElement> finder) {
		super(finder);
	}
	
	public void submit(){
		find().submit();
	}
}
