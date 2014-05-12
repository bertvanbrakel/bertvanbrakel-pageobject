package com.bertvanbrakel.pageobject;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class PageObject extends BasePageObject implements Provider<WebDriver> {

	private final Provider<WebDriver> driverFinder;

    public PageObject(Provider<WebDriver> driverFinder) {
        this.driverFinder = driverFinder;
    }
  
	public static DriverFinder driver(WebDriver driver,String driverExp) {
		return new DriverFinder(driver,driverExp);
	}
	
	public static DriverFinder driver(WebDriver driver,RetryPolicyProvider retryProvider, String driverExp) {
		return new DriverFinder(driver,driverExp);
	}
	
	public static DriverFinder driver(Provider<WebDriver> driver,final String driverExp) {
		return new DriverFinder(driver,driverExp);
	}
	
	public static DriverFinder driver(Provider<WebDriver> driver,RetryPolicyProvider retryProvider,String driverExp) {
		return new DriverFinder(driver, retryProvider, driverExp);
	}

	public static DriverFinder driver(Provider<WebDriver> driver,RetryPolicyProvider retryProvider, DriverBy... matchers) {
		return new DriverFinder(driver, retryProvider, matchers);
	}
	
    public void reload(){
    	get().navigate().refresh();
    }

	@Override
	public WebElement findElement(By by){
		return get().findElement(by);
	}

	@Override
	public List<WebElement> findElements(By by) {
		return get().findElements(by);
	}

	public WebDriver get() {
		return driverFinder.get();
	}

	@Override
	protected SearchContext getSearchContext() {
		return driverFinder.get();
	}
}
