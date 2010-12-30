package com.bertvanbrakel.pageobject.ui;

import static com.bertvanbrakel.lang.Check.checkNotNull;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import org.hamcrest.Matcher;
import org.openqa.selenium.WebElement;

import com.bertvanbrakel.pageobject.Finder;
import com.bertvanbrakel.pageobject.WebElementFinder;

public class Element implements Widget {

    private final Finder<WebElement> finder;

    public Element(final Finder<WebElement> finder){
    	checkNotNull("finder", finder);
        this.finder = finder;
    }

    private WebElement find(){
        return finder.find();
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

    public void click(){
    	find().click();
    }
}
