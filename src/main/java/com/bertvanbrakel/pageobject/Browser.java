package com.bertvanbrakel.pageobject;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;

public enum Browser {
	FIREFOX,IE,CHROME,HTMLUNIT;
	
	public Driver createProvider(){
		return new Driver(createDriver());
	}
	
	public WebDriver createDriver(){
		switch(this){
		case FIREFOX: return new FirefoxDriver();
		case IE: return new InternetExplorerDriver();
		case CHROME: return new ChromeDriver();
		case HTMLUNIT: HtmlUnitDriver d = new HtmlUnitDriver();
			d.setJavascriptEnabled(true);
			return d;
		}
		throw new PageObjectException(String.format("Don't know browser type '%s'", this));
	}
}
