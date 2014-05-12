package com.bertvanbrakel.pageobject;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;

public class DriverUtils {
	
//	public static int getNumFrames(WebDriver d){
//		if( d != null && d instanceof JavascriptExecutor){
//			Long l = (Long)((JavascriptExecutor)d).executeScript("return window.frames.length", "");
//			System.out.println("numFrames=" + l);
//			return l.intValue();
//		}
//		return 0;
//	}
//	
//	public static Collection<WebDriver> getFrames(WebDriver d){
//		Collection<WebDriver> col = new ArrayList<WebDriver>();
//		int numFrames = getNumFrames(d);
//		for( int i = 0; i < numFrames; i++){
//			col.add(d.switchTo().frame(i));
//			System.out.println("frame="+i);
//			System.out.println("handle=" + d.getWindowHandle());
//			System.out.println("handles=" + Arrays.toString(d.getWindowHandles().toArray()));
//		}
//		return col;
//	}
//	
	public static Object executeJavascript(WebDriver d, String js){
		if( d != null && d instanceof JavascriptExecutor){
			Object ret = ((JavascriptExecutor)d).executeScript(js, "");
			System.out.println(js);
			System.out.println("=" + ret);
			
			return ret;
		}
		return null;
	}
}
