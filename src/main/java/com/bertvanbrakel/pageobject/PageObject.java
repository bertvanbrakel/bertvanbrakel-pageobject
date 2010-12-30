package com.bertvanbrakel.pageobject;

import static com.bertvanbrakel.lang.Check.checkNotNull;
import static com.bertvanbrakel.lang.Converter.toListEmptyIfNull;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import com.bertvanbrakel.pageobject.ui.Widget;

/**
 * A base page object which creates finder and widgets. 
 *
 * @author Bert van Brakel
 */
public class PageObject implements Provider<WebDriver> {

    private final Finder<WebDriver> driverFinder;
    private final RetryPolicyProvider retryPolicyProvider;

    //to not leak abstractions by needing to make this pageMap a retryProvider. Using this
    //method allows users to change the retry provider for this pageMap at any point and it should be
    //reflected in all finders created by this pageMap. As opposed to simply passing the retryProvider
    //directly
    private final RetryPolicyProvider retyPolicyProviderWrapper = new RetryPolicyProvider() {
        @Override
        public RetryPolicy getRetryPolicy() {
            return retryPolicyProvider.getRetryPolicy();
        }
    };

    public PageObject(final WebDriver driver) {
        this(new SimpleDriverFinder(driver),new FixedRetryPolicyProvider(3000,30));
    }
    
    public PageObject(final WebDriver driver, final RetryPolicyProvider retryPolicyProvider) {
        this(new SimpleDriverFinder(driver),retryPolicyProvider);
    }
    
    public PageObject(final Finder<WebDriver> driverFinder) {
        this(driverFinder,new FixedRetryPolicyProvider(3000,30));
    }

    public PageObject(final Finder<WebDriver> driverFinder, final RetryPolicyProvider retryPolicyProvider) {
        checkNotNull("driverFinder", driverFinder);
        checkNotNull("retryPolicyProvider", retryPolicyProvider);
        this.driverFinder = driverFinder;
        this.retryPolicyProvider = retryPolicyProvider;
    }

    protected <W extends Widget> W find(Class<W> klass, final By... lookups) {
        return internalCreateWidget(klass, null,toListEmptyIfNull(lookups));
    }

    protected <W extends Widget> W find(Class<W> klass,final List<By> lookups) {
        return internalCreateWidget(klass, null, lookups);
    }
    
    protected <W extends Widget> W find(Class<W> klass,final String lookupExpression) {
        return internalCreateWidget(klass, null, WebElementLookupParser.parse(lookupExpression));
    }

    protected <W extends Widget> W find(Class<W> klass, final RetryPolicyProvider retryProvider, final By... lookups) {
        return internalCreateWidget(klass, retryProvider,toListEmptyIfNull(lookups));
    }

    protected <W extends Widget> W find(Class<W> klass, final RetryPolicyProvider retryPolicyProvider, final List<By> lookups) {
        return internalCreateWidget(klass, retryPolicyProvider,lookups);
    }
    
    protected <W extends Widget> W find(Class<W> klass, final RetryPolicyProvider retryPolicyProvider, final String lookupExpression) {
        return internalCreateWidget(klass, retryPolicyProvider, WebElementLookupParser.parse(lookupExpression));
    }

    /**
     * Internal method all create methods delegate to, to prevent subclasses subverting this
     *
     * @param retryPolicyProvider
     * @param lookups
     * @return
     */
    private final <W extends Widget> W internalCreateWidget(Class<W> widgetClass,final RetryPolicyProvider retryPolicyProvider, final List<By> lookups) {
    	checkNotNull("widgetClass", widgetClass);
    	checkNotNull("lookups", lookups);
    	WebElementFinder finder = new WebElementFinder(driverFinder,retryPolicyProvider==null?retyPolicyProviderWrapper:retryPolicyProvider, lookups);
		
    	try {    		
    		Constructor<W> ctor = widgetClass.getConstructor(new Class[]{WebElementFinder.class});    		
    		return ctor.newInstance(new Object[]{finder});
    	} catch (SecurityException e) {
			throw new PageObjectException(String.format("Error creating widget of type %s, expected public ctor taking arg of type %s", widgetClass.getName(), WebElementFinder.class.getName()), e);
		} catch (NoSuchMethodException e) {
			throw new PageObjectException(String.format("Error creating widget of type %s, expected ctor taking an arg of type %s", widgetClass.getName(), WebElementFinder.class.getName()), e);
		} catch (IllegalArgumentException e) {
			throw new PageObjectException(String.format("Error creating widget of type %s, invoked public ctor taking arg of type %s", widgetClass.getName(), WebElementFinder.class.getName()), e);
		} catch (InstantiationException e) {
			throw new PageObjectException(String.format("Error creating widget of type %s, invoked public ctor taking arg of type %s", widgetClass.getName(), WebElementFinder.class.getName()), e);
		} catch (IllegalAccessException e) {
			throw new PageObjectException(String.format("Error creating widget of type %s, invoked public ctor taking arg of type %s", widgetClass.getName(), WebElementFinder.class.getName()), e);
		} catch (InvocationTargetException e) {
			throw new PageObjectException(String.format("Error creating widget of type %s, invoked public ctor taking arg of type %s", widgetClass.getName(), WebElementFinder.class.getName()), e);
		} 
    }

    /**
     * Helper method to create retryPolicies
     *
     * @param maxWaitTimeMs amount of time to wait for in miiliseconds
     * @param numAttempts the number of attempts to retry in time given
     *
     * @return
     */
    protected RetryPolicyProvider retryFor(final long maxWaitTimeMs,final int numAttempts){
        return new FixedRetryPolicyProvider(maxWaitTimeMs,numAttempts);
    }

    /**
     * Return the provider used. This is a wrapped instance of the one passed into the constructor to allow us
     * to change the provider in this instance and have it reflected everywhere
     *
     * @return
     */
    protected RetryPolicyProvider getRetryPolicyProvider(){
        return retyPolicyProviderWrapper;
    }

    protected WebDriver getDriver(){
    	return driverFinder.find();
    }
    
    public void reload(){
    	getDriver().navigate().refresh();
    }

	@Override
	public WebDriver get() {
		return driverFinder.find();
	}
}
