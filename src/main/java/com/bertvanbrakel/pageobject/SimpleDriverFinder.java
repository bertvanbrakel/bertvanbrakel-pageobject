package com.bertvanbrakel.pageobject;

import org.openqa.selenium.WebDriver;

public class SimpleDriverFinder implements Finder<WebDriver> {

	private final WebDriver driver;

	public SimpleDriverFinder(WebDriver driver) {
		super();
		this.driver = driver;
	}

	@Override
	public WebDriver find() {
		return driver;
	}

	@Override
	public WebDriver find(RetryPolicyProvider policyProvider) {
		return driver;
	}

}
