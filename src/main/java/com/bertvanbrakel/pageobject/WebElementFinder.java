package com.bertvanbrakel.pageobject;

import static org.codemucker.lang.Check.checkNotNull;

import java.lang.ref.WeakReference;
import java.util.Arrays;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebElement;

public class WebElementFinder implements Finder<WebElement>, Provider<WebElement> {
	
	private static final RetryPolicyProvider DEFAULT_RETRY = new FixedRetryPolicyProvider(1000, 10);
	
	/**
	 * How we walk the DOM to find the element
	 */
	private final List<By> lookups;
	
	/**
	 * Provides the retry policy when we can't find the element
	 */
	public RetryPolicyProvider retryProvider = DEFAULT_RETRY;

	/**
	 * A reference to the element once we have found it. Used to prevent us
	 * having to walk the DOM each time
	 */
	private WeakReference<WebElement> cachedElement;

	public SearchContext startingEle;
	
	public WebElementFinder(SearchContext startingEle,RetryPolicyProvider retryProvider, String expression) {
		this(startingEle,retryProvider,WebElementBy.expression(expression));
	}
	
	public WebElementFinder(SearchContext startingEle,RetryPolicyProvider retryProvider, final List<By> lookups) {
		this.startingEle = startingEle;
		this.retryProvider = retryProvider==null?DEFAULT_RETRY:retryProvider;
		this.lookups = lookups;
	}
	
	public void setRetryPolicy(RetryPolicyProvider provider){
		this.retryProvider = provider==null?DEFAULT_RETRY:provider;
	}
	
	@Override
	public WebElement get() {
		return find();
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
		ele = internalFindElementBy(startingEle, retryProvider, lookups);
		cachedElement = new WeakReference<WebElement>(ele);
		return ele;
	}

	private static WebElement internalFindElementBy(final SearchContext startingEle, final RetryPolicyProvider retryProvider, final List<By> bys) {
		WebElement ele = null;
		// initially have this as null as we only bother creating one when we
		// fail
		RetryPolicy policy = null;
		int walked = 0;
		for (final By by : bys) {
			boolean found = false;
			while( !found ){
				try {
					ele = (ele == null ? startingEle.findElement(by) : ele.findElement(by));
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

	@Override
	public WebElement findElement(By by) {
		return get().findElement(by);
	}

	@Override
	public List<WebElement> findElements(By by) {
		return get().findElements(by);
	}
}
