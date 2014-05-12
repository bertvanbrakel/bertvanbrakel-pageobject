package com.bertvanbrakel.pageobject.ui;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.hamcrest.Matcher;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebElement;

import com.bertvanbrakel.pageobject.BasePageObject;
import com.bertvanbrakel.pageobject.Finder;
import com.bertvanbrakel.pageobject.FinderException;
import com.bertvanbrakel.pageobject.PageObjectException;
import com.bertvanbrakel.pageobject.WebElementFinder;

public class Element extends BasePageObject implements Widget {

    private Finder<WebElement> elementFinder;

    public Element(Finder<WebElement> finder){
        this.elementFinder = finder;
    }

    public void waitTillVisible(){
    	waitTillVisible(10000);
    }
    
    public void waitTillVisible(long timeMs){
    	WebElement ele = find();
    	long start = System.currentTimeMillis();
    	while(!ele.isEnabled()){
    		Object obj = new Object();
    		synchronized (obj) {
    			try {
					obj.wait(timeMs);
				} catch (InterruptedException e) {
					throw new PageObjectException("Interrupted waiting for element to become anabled",e);
				}
    		}
    		if( System.currentTimeMillis() - start > timeMs){
    			throw new FinderException("waited " + timeMs + " ms for element to become visible");
    		}
    	}
    }
 
    @Override
	protected SearchContext getSearchContext() {
		return find();
	}
    
    protected WebElement find(){
        return elementFinder.find();
    }
    
    public void assertTextMatches(final Matcher<String> matcher) {
        assertThat( "Elements text does not match", getText(), matcher);
    }

    public void assertTextEquals(final String expect) {
        assertTextMatches(equalTo(expect));
    }

    public String getText() {
        return find().getText();
    }

    public void click2(){
    	//find().click();
    	find().sendKeys(Keys.ENTER);
    }

    public void click(){
    	find().click();
    	//find().sendKeys(Keys.ENTER);
    }

	@Override
	public WebElement findElement(By by) {
		return find().findElement(by);
	}

	@Override
	public List<WebElement> findElements(By by) {
		return find().findElements(by);
	}

	@Override
	public void setFinder(WebElementFinder finder) {
		this.elementFinder = finder;
	}
}
