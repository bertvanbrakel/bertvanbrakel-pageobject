package com.bertvanbrakel.pageobject;

import static org.codemucker.lang.Check.checkNotNull;
import static org.codemucker.lang.Converter.toListEmptyIfNull;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;

import com.bertvanbrakel.pageobject.ui.Widget;
import com.google.inject.Inject;
import com.google.inject.Injector;

/**
 * A base page object which creates finder and widgets. 
 *
 */
public abstract class BasePageObject implements SearchContext {
    
	private final Class<?>[] ARGS_FINDER = new Class<?>[]{Finder.class};
	private final Class<?>[] ARGS_WEBELE_FINDER = new Class<?>[]{WebElementFinder.class};
	
    protected abstract SearchContext getSearchContext();
    
	protected <W extends Widget> W find(Class<W> klass, final By... lookups) {
        return internalCreateWidget(klass, null, toListEmptyIfNull(lookups));
    }

    protected <W extends Widget> W find(Class<W> klass,final List<By> lookups) {
        return internalCreateWidget(klass, null, lookups);
    }
    
    protected <W extends Widget> W find(Class<W> klass,final String lookupExpression) {
        return internalCreateWidget(klass, null, WebElementBy.expression(lookupExpression));
    }

    protected <W extends Widget> W find(Class<W> klass, final RetryPolicyProvider retryProvider, final By... lookups) {
        return internalCreateWidget(klass, retryProvider,toListEmptyIfNull(lookups));
    }

    protected <W extends Widget> W find(Class<W> klass, final RetryPolicyProvider retryPolicyProvider, final List<By> lookups) {
        return internalCreateWidget(klass, retryPolicyProvider,lookups);
    }
    
    protected <W extends Widget> W find(Class<W> klass, final RetryPolicyProvider retryPolicyProvider, final String lookupExpression) {
        return internalCreateWidget(klass, retryPolicyProvider, WebElementBy.expression(lookupExpression));
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
    	
    	WebElementFinder finder = new WebElementFinder(this,retryPolicyProvider,lookups);
    	return createWidget(widgetClass, finder);
    }

	private <W extends Widget> W createWidget(Class<W> widgetClass,WebElementFinder finder) {
		Object[] args = null;
		Constructor<W> ctor = getCtorOrNull(widgetClass,ARGS_FINDER);
		if(ctor == null){
			ctor = getCtorOrNull(widgetClass,ARGS_WEBELE_FINDER);
		}
		if( ctor != null){
			args = new Object[]{finder};
		}
		if( ctor == null){
			throw new PageObjectException("Couldn't create widget class " + widgetClass.getName() + " as no valid constructor found (taking a Finder<WebElement> ctor)");
		}
		
		W widget;
		try {
			widget = ctor.newInstance(args);
		} catch (InstantiationException | IllegalAccessException
				| IllegalArgumentException | InvocationTargetException e) {
			throw new PageObjectException("Couldn't create widget class " + widgetClass.getName(),e);
		}
    	return widget;
	}
	
	private <W> Constructor<W> getCtorOrNull(Class<W> widgetClass,Class<?>[] args){
		try {
			return (Constructor<W>)widgetClass.getConstructor(args);
		} catch (NoSuchMethodException | SecurityException e) {
			return null;
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
    protected RetryPolicyProvider createRetryFor(final long maxWaitTimeMs,final int numAttempts){
        return new FixedRetryPolicyProvider(maxWaitTimeMs,numAttempts);
    }
    
}