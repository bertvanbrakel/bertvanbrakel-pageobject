package com.bertvanbrakel.pageobject;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class SimpleDriverFinder implements Finder<WebDriver> {

	private final Provider<WebDriver> driver;

	public SimpleDriverFinder(WebDriver driver) {
		this(new Driver(driver));
	}

	public SimpleDriverFinder(Provider<WebDriver> driver) {
		super();
		this.driver = driver;
	}
	
	@Override
	public WebDriver find() {
		return driver.get();
	}

	@Override
	public WebDriver find(RetryPolicyProvider policyProvider) {
		return driver.get();
	}

	@Override
	public WebElement findElement(By by) {
		return driver.get().findElement(by);
	}

	@Override
	public List<WebElement> findElements(By by) {
		return driver.get().findElements(by);
	}

}
