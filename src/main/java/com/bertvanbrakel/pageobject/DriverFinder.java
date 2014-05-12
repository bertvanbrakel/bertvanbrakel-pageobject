package com.bertvanbrakel.pageobject;

import static org.codemucker.lang.Check.checkNoNullItems;
import static org.codemucker.lang.Check.checkNotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class DriverFinder implements Finder<WebDriver>, Provider<WebDriver> {

	private static final RetryPolicyProvider DEFAULT_RETRY = new FixedRetryPolicyProvider(3000, 10);
	
	private final Provider<WebDriver> driverProvider;
	private final List<DriverBy> bys;
	private RetryPolicyProvider retryProvider = DEFAULT_RETRY;
	
	public DriverFinder(WebDriver driver,String driverExp) {
		this(driver, DEFAULT_RETRY, driverExp);
	}
	
	public DriverFinder(WebDriver driver,RetryPolicyProvider retryProvider, String driverExp) {
		this(new Driver(driver), DEFAULT_RETRY, DriverBy.expression(driverExp));
	}
	
	public DriverFinder(Provider<WebDriver> driver,final String driverExp) {
		this(driver, DEFAULT_RETRY, DriverBy.expression(driverExp));
	}
	
	public DriverFinder(Provider<WebDriver> driver,RetryPolicyProvider retryProvider,String driverExp) {
		this(driver, retryProvider, DriverBy.expression(driverExp));
	}

	public DriverFinder(Provider<WebDriver> driver,RetryPolicyProvider retryProvider, DriverBy... matchers) {
		this(driver, retryProvider, arrToList(matchers));
	}

	public DriverFinder(Provider<WebDriver> driver,RetryPolicyProvider retryProvider,Collection<DriverBy> bys) {
		checkNotNull("retryPolicyProvider", retryProvider);
		checkNoNullItems("matchers", bys);
		checkNotNull("driver", driver);

		this.retryProvider = retryProvider==null?DEFAULT_RETRY:retryProvider;
		this.driverProvider = driver;
		this.bys = new ArrayList<DriverBy>(bys);
	}
	
	private static <T> List<T> arrToList(T[] arr) {
		if (arr == null) {
			return null;
		}
		List<T> list = new ArrayList<T>(arr.length);
		for (T t : arr) {
			list.add(t);
		}
		return list;
	}

	@Override
	public WebElement findElement(By by) {
		return get().findElement(by);
	}

	@Override
	public List<WebElement> findElements(By by) {
		return get().findElements(by);
	}
	
	@Override
	public WebDriver get() {
		return find();
	}

	@Override
	public WebDriver find() {
		return internalFind(this.retryProvider);
	}
	
	@Override
	public WebDriver find(RetryPolicyProvider policyProvider) {
		checkNotNull("policyProvider", policyProvider);
		return internalFind(policyProvider);
	}

	private WebDriver internalFind(RetryPolicyProvider policyProvider) {
		WebDriver driver = driverProvider.get();
		//try to match current providers driver
		Collection<String> names = new ArrayList<String>();
		names.add(driver.getWindowHandle());
		WebDriver d = findBy(driver,bys);
		
		if (d == null) {
			for (String handle : driver.getWindowHandles()) {
				d = findBy(driver.switchTo().window(handle), bys);
				if (d != null) {
					break;
				}
			}
		}
		//todo:cache this driver, so we don't need to iterate driver later. Prevents window flickering
		if (d == null) {
			throw new FinderException(String.format(
					"Could not finder driver using '%s'",
					Arrays.toString(bys.toArray())));
		}
		return d;
	}
	

	private WebDriver findBy(WebDriver d, List<DriverBy> bys) {
		for (DriverBy by : bys) {
			d = by.find(d);
			if( d == null){
				return null;
			}
		}
		return d;
	}
}
