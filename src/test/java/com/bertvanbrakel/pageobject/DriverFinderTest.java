package com.bertvanbrakel.pageobject;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.junit.experimental.theories.Theory;
import org.openqa.selenium.WebDriver;

import com.bertvanbrakel.pageobject.ui.Element;

public class DriverFinderTest extends AbstractContentTest {
	
	
//    @BeforeClass
//    public static void classSetupA() throws Exception {
//        setPort(8080);
//        classSetup();
//    }

    @Theory
    public void test_find_iframe_by_url(Browser b) {
        serveBodyContent("/test_find_iframe_by_url/a.html", "<div id='div'>A div</div>" );
        serveBodyContent("/test_find_iframe_by_url/b.html", "<div id='div'>B div</div>" );
        servePageContent("/test_find_iframe_by_url/main.html", "<html>Main page<iframe name='a' src='a.html' >No frames A</iframe><iframe name='b' src='b.html'>No frames B</iframe></html>" );

        final WebDriver driver = b.createDriver();
        driver.get(getBaseHttpUrl() + "/test_find_iframe_by_url/main.html");

        final FramePageMap page = new FramePageMap(driver);
        
        APageMap pageA = page.gotoA();
        assertThat(pageA.getDivA().getText(), is(equalTo("A div")));
        
        BPageMap pageB = page.gotoB();
        assertThat(pageB.getDivB().getText(), is(equalTo("B div")));
        
        assertThat(pageA.getDivA().getText(), is(equalTo("A div")));
    }

    @Theory
    public void test_find_frame_by_url(Browser b) {
        serveBodyContent("/test_find_frame_by_url/a.html", "<div id='div'>A div</div>" );
        serveBodyContent("/test_find_frame_by_url/b.html", "<div id='div'>B div</div>" );
        serveBodyContent("/test_find_frame_by_url/c.html", "<html><frameset><frame name='a' src='d.html' /><frame name='b' src='e.html' /></frameset></html>" );
        serveBodyContent("/test_find_frame_by_url/d.html", "<div id='div'>D div</div>" );
        serveBodyContent("/test_find_frame_by_url/e.html", "<div id='div'>E div</div>" );
        
        servePageContent("/test_find_frame_by_url/main.html", "<html><frameset><frame name='c' src='c.html'/><frame name='a' src='a.html' /><frame name='b' src='b.html'/></frameset></html>" );

        final WebDriver driver = b.createDriver();
        driver.get(getBaseHttpUrl() + "/test_find_frame_by_url/main.html");

        final FramePageMap page = new FramePageMap(driver);
//        DriverUtils.javascript(driver, "return parent.document.location");
//        DriverUtils.javascript(driver, "return parent.frames.length");
//
//        System.out.println("driver.url=" + driver.getCurrentUrl());
//        driver.switchTo().frame("b");
//        System.out.println("driver.url=" + driver.getCurrentUrl());
        APageMap pageA = page.gotoA();
        assertThat(pageA.getDivA().getText(), is(equalTo("A div")));
        
        BPageMap pageB = page.gotoB();
        assertThat(pageB.getDivB().getText(), is(equalTo("B div")));
        
        assertThat(pageA.getDivA().getText(), is(equalTo("A div")));
    }
    
    private static class FramePageMap extends PageObject {

		public FramePageMap(WebDriver driver) {
			super(new DriverFinder(driver,"url=ant:*/main.html"));
		}
		
		public APageMap gotoA(){
			return new APageMap(this);
		}
		
		public BPageMap gotoB(){
			return new BPageMap(this);
		}
    }
    
    private static class APageMap extends PageObject {

		public APageMap(Provider<WebDriver> driver) {
			super(new DriverFinder(driver,"frame=a"));
		}
		
        public Element getDivA() {
            return find(Element.class,"id=div");
        }
    }
    
    private static class BPageMap extends PageObject {

		public BPageMap(Provider<WebDriver> driver) {
			super(new DriverFinder(driver, "frame=b"));
		}
		
        public Element getDivB() {
            return find(Element.class,"id=div");
        }
    }
}
