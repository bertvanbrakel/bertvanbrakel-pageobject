package com.bertvanbrakel.pageobject.ui;

import com.bertvanbrakel.pageobject.WebElementFinder;


public class Select extends Element {

	public Select(WebElementFinder finder) {
		super(finder);
	}
	
	public Select selectByText(String text){
		getSelect().selectByVisibleText(text);
		return this;
	}
	
	public Select selectByValue(String val){
		getSelect().selectByValue(val);
		return this;
	}
	
	private org.openqa.selenium.support.ui.Select getSelect(){
		return new org.openqa.selenium.support.ui.Select(find());
	}
	
}
