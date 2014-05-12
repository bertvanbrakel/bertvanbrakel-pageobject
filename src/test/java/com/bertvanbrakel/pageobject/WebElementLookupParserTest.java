package com.bertvanbrakel.pageobject;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.codemucker.jmatch.AList;
import org.codemucker.jmatch.AnInstance;
import org.codemucker.jmatch.Expect;
import org.junit.Test;
import org.openqa.selenium.By;

public class WebElementLookupParserTest {

    @Test
    public void test_parse_null_lookup() {
        assertEquals(new ArrayList<By>(), new ArrayList<By>(WebElementBy.expression(null)));
    }

    @Test
    public void test_parse_empty_lookup() {
        assertEquals(new ArrayList<By>(), new ArrayList<By>(WebElementBy.expression("")));
    }

    @Test
    public void test_parse_single_lookup() {
        Expect.that(WebElementBy.expression("id=a")).is(AList.withOnly(AnInstance.equalTo(By.id("a"))));
    }

    @Test
    public void test_parse_dual_lookup() {
        List<By> expect = new ArrayList<By>(Arrays.asList(By.id("a"),By.xpath("//div")));
        
       assertEquals(expect, new ArrayList<By>(WebElementBy.expression("id=a,xpath=//div")));
    }
    
    @Test
    public void test_textToBy() {
    	assertEquals(By.id("x"),WebElementBy.textToBy("id", "x"));
    	assertEquals(By.name("x"),WebElementBy.textToBy("name", "x"));
    	assertEquals(By.className("x"),WebElementBy.textToBy("className", "x"));
    	assertEquals(By.partialLinkText("x"),WebElementBy.textToBy("partialLinkText", "x"));
    	assertEquals(By.tagName("x"),WebElementBy.textToBy("tagName", "x"));
    	assertEquals(By.xpath("x"),WebElementBy.textToBy("xpath", "x"));
    	assertEquals(By.cssSelector("x"),WebElementBy.textToBy("cssSelector", "x"));
    }
    
    @Test(expected=ParseException.class)
    public void test_textToBy_throws_error() {
    	WebElementBy.textToBy("x", "y");
    }
}
