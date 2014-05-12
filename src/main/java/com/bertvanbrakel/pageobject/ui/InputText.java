package com.bertvanbrakel.pageobject.ui;

import com.bertvanbrakel.pageobject.WebElementFinder;

public class InputText extends Element {

	public InputText(WebElementFinder finder) {
		super(finder);
	}
	
	public InputText set(CharSequence text){
		find().sendKeys(text);
		return this;
	}
	

}
