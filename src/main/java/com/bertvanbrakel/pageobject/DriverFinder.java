package com.bertvanbrakel.pageobject;

import static com.bertvanbrakel.lang.Check.checkNoNullItems;
import static com.bertvanbrakel.lang.Check.checkNotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.openqa.selenium.WebDriver;

public class DriverFinder implements Finder<WebDriver> {

	private final RetryPolicyProvider retryProvider;
	private final List<DriverBy> bys;
	private final Provider<WebDriver> driverProvider;

	public DriverFinder(Provider<WebDriver> driverProvider,
			final RetryPolicyProvider retryProvider, final String driverExp) {
		this(driverProvider, retryProvider, DriverLookupParser.parse(driverExp));
	}

	public DriverFinder(Provider<WebDriver> driverProvider,
			final RetryPolicyProvider retryProvider, final DriverBy... matchers) {
		this(driverProvider, retryProvider, arrToList(matchers));
	}

	public DriverFinder(Provider<WebDriver> driverProvider,
			final RetryPolicyProvider retryProvider,
			final Collection<DriverBy> bys) {
		checkNotNull("retryPolicyProvider", retryProvider);
		checkNoNullItems("matchers", bys);
		checkNotNull("driverProvider", driverProvider);

		this.retryProvider = retryProvider;
		this.driverProvider = driverProvider;
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
