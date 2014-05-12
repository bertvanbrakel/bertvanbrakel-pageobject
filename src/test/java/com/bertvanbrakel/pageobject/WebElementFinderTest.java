package com.bertvanbrakel.pageobject;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.experimental.theories.Theory;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class WebElementFinderTest extends AbstractContentTest {

    @Theory
    public void ensure_finder_can_find(Browser b) {
        serveBodyContent("/ensure_finder_can_find", "<div id='a'>A<div id='b'>B</div></div>");

        final WebDriver driver = b.createDriver();
        driver.get(getBaseHttpUrl() + "/ensure_finder_can_find");
        final WebElementFinder finder = new WebElementFinder(driver,NoRetryPolicyProvider.INSTANCE, "id=b");
        final WebElement ele = finder.get();
        assertNotNull(ele);
        assertEquals( "B", ele.getText() );
    }

}
