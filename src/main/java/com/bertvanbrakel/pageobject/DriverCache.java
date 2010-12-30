package com.bertvanbrakel.pageobject;

import org.openqa.selenium.WebDriver;

public interface DriverCache {
	public WebDriver getDriver();
	public void setDriver(WebDriver d);
}
