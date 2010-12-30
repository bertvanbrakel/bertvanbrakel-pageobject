package com.bertvanbrakel.pageobject;

import static com.bertvanbrakel.lang.Check.checkNoNullItems;
import static com.bertvanbrakel.lang.Check.checkNotNull;

import java.lang.ref.WeakReference;
import java.util.Arrays;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class WebElementFinder implements Finder<WebElement> {
	/**
	 * How we walk the DOm to find the element
	 */
	private final List<By> lookups;
	/**
	 * Provides the retry policy when we can't find the element
	 */
	private final RetryPolicyProvider retryProvider;

	/**
	 * A reference to the element once we have found it. Used to prevent us
	 * havign to walk the DOM each time
	 */
	private WeakReference<WebElement> cachedElement;

	/**
	 * Finder to provide the driver to find the element in
	 */
	private final Finder<WebDriver> driverFinder;

	public WebElementFinder(Finder<WebDriver> driverFinder,
			final RetryPolicyProvider retryProvider, final By... lookups) {
		checkNotNull("driverFinder", driverFinder);
		checkNotNull("retryPolicyProvider", retryProvider);
		checkNoNullItems("lookups", lookups);
		this.driverFinder = driverFinder;
		this.retryProvider = retryProvider;
		this.lookups = Arrays.asList(lookups);
	}

	public WebElementFinder(Finder<WebDriver> driverFinder,
			final RetryPolicyProvider retryProvider, final List<By> lookups) {
		checkNotNull("driverFinder", driverFinder);
		checkNotNull("retryPolicyProvider", retryProvider);
		checkNoNullItems("lookups", lookups);
		this.driverFinder = driverFinder;
		this.retryProvider = retryProvider;
		this.lookups = lookups;
	}

	@Override
	public WebElement find() {
		return internalFind(this.retryProvider);
	}

	@Override
	public WebElement find(RetryPolicyProvider retryProvider) {
		checkNotNull("retryPolicyProvider", retryProvider);
		return internalFind(retryProvider);
	}

	private final WebElement internalFind(RetryPolicyProvider retryProvider) {
		WebElement ele;
		if (cachedElement != null) {
			ele = cachedElement.get();
			if (!isStale(ele)) {
				return ele;
			}
		}
		ele = internalFindElementBy(driverFinder, retryProvider, lookups);
		cachedElement = new WeakReference<WebElement>(ele);
		return ele;
	}

	private static WebElement internalFindElementBy(
			final Finder<WebDriver> driverFinder,
			final RetryPolicyProvider retryProvider, final List<By> bys) {
		final WebDriver driver = driverFinder.find();
		WebElement ele = null;
		// initially have this as null as we only bother creating one when we
		// fail
		RetryPolicy policy = null;
		int walked = 0;
		for (final By by : bys) {
			boolean found = false;
			while( !found ){
				try {
					ele = (ele == null ? driver.findElement(by) : ele.findElement(by));
					found = true;
				} catch (NoSuchElementException e) {
					if (policy == null) {
						policy = retryProvider.getRetryPolicy();
					}
					if (policy.isRetry()) {
						policy.waitUntilNextAttempt();
					} else {
						throw new FinderException(
								String.format(
										"Could not find element defined by path %s, instead found up to %s",
										Arrays.toString(bys.toArray()), Arrays
												.toString(bys.subList(0, walked)
														.toArray())));
					}
				}
				
			}
			walked++;
		}
		return ele;
	}

	/**
	 * Return true if the element is null or detached from the dom
	 * 
	 * @param ele
	 * @return
	 */
	private static boolean isStale(final WebElement ele) {
		if (ele == null) {
			return true;
		} else {
			try {
				ele.getTagName();
			} catch (final StaleElementReferenceException e) {
				return true;
			}
		}
		return false;
	}

}
