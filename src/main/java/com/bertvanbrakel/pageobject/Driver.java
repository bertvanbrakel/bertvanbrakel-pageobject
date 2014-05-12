package com.bertvanbrakel.pageobject;

import org.openqa.selenium.WebDriver;

public class Driver implements Provider<WebDriver> {

	private final WebDriver driver;
	
	public Driver(WebDriver driver){
		this.driver = driver;
	}
	
	@Override
	public WebDriver get() {
		return driver;
	}
	
}