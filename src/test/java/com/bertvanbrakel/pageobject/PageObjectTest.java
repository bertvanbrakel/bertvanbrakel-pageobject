package com.bertvanbrakel.pageobject;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import junit.framework.AssertionFailedError;

import org.junit.experimental.theories.Theory;
import org.openqa.selenium.WebDriver;

import com.bertvanbrakel.pageobject.ui.Element;


public class PageObjectTest extends AbstractContentTest {

//    @BeforeClass
//    public static void classSetupA() throws Exception {
//        setPort(8080);
//        classSetup();
//    }

	@Theory
    public void test_simple_find_elements(final Browser b) {
        serveBodyContent("/test_simple_find_elements", "<div id='a'>A<div id='b'>B</div></div>" );

        final WebDriver driver = b.loadDriver();
        driver.get(getBaseHttpUrl() + "/test_simple_find_elements");

        final TestPageMap page = new TestPageMap(new SimpleDriverFinder(driver));
        assertThat(page.getDivB().getText(), is(equalTo("B")));
        page.getDivB().assertTextEquals("B");

        assertThat(page.getDivA().getText(), is(equalTo("AB")));
        page.getDivA().assertTextEquals("AB");


        assertThrowsError( new Runnable() {
            @Override
            public void run() {
                page.getDivB().assertTextEquals("A");
            }
        });
    }

    @Theory
    public void test_frame_switching(final Browser b) {
        serveBodyContent("/test_frame_switching/a.html", "<div id='div'><a href='b.html'>A click to go to B</a></div>" );
        serveBodyContent("/test_frame_switching/b.html", "<div id='div'><a href='a.html'>B click to go to A</a></div>" );

    	servePageContent("/test_frame_switching/main.html", "<html><frameset cols='50%,50%'><frame name='a' src='a.html' >No frames A</frame><frame name='b' src='b.html'>No frames B</frame></frameset></html>" );

        final WebDriver driver = b.loadDriver();
        driver.get(getBaseHttpUrl() + "/test_frame_switching/main.html");

        final FramePageMap page = new FramePageMap(driver);

        final APageMap pageA = page.gotoA();
        assertThat(pageA.getDivA().getText(), is(equalTo("A click to go to B")));

        final BPageMap pageB = page.gotoB();
        assertThat(pageB.getDivB().getText(), is(equalTo("B click to go to A")));
    }

    @Theory
    public void test_frame_reload(final Browser b) {
        serveBodyContent("/test_page_reload/a.html", "<div id='div'><a onclick=\"javascript:parent.frames['b'].location=parent.frames['b'].location\">A click to reload B</a></div>" );
        serveBodyContent("/test_page_reload/b.html", "<div id='div'><a href='a.html' onclick='javascript:'>B click to go to A</a><script type='text/javascript'>document.writeln('Time:' + new Date() )</script></div>" );

    	servePageContent("/test_page_reload/main.html", "<html><frameset cols='50%,50%'><frame name='a' src='a.html' >No frames A</frame><frame name='b' src='b.html'>No frames B</frame></frameset></html>" );

        final WebDriver driver = b.loadDriver();
        driver.get(getBaseHttpUrl() + "/test_page_reload/main.html");

        final FramePageMap page = new FramePageMap(driver);


        final BPageMap pageB = page.gotoB();
        assertThat(pageB.getDivB().getText(), is(equalTo("B click to go to A")));

        final APageMap pageA = page.gotoA();
        assertThat(pageA.getDivA().getText(), is(equalTo("A click to reload B")));

        pageA.getDivA().click();
        assertThat(pageB.getDivB().getText(), is(equalTo("B click to go to A")));

    }

    private void assertThrowsError(final Runnable r){
        boolean caught = false;
        try {
            r.run();
        } catch (final AssertionError e){
            caught = true;
        }
        if( !caught){
            throw new AssertionFailedError( "Expected call to throw an Assertion Error" );
        }
    }

    private static class TestPageMap extends PageObject {

        public TestPageMap(final Finder<WebDriver> driverFinder) {
            super(driverFinder);
        }

        public TestPageMap(final Finder<WebDriver> driverFinder,
                final RetryPolicyProvider retryPolicyProvider) {
            super(driverFinder, retryPolicyProvider);
        }

        public Element getDivA() {
            return find(Element.class,retryFor(50,10),"id=a");
        }

        public Element getDivB() {
        	return find(Element.class,"id=b");
        }
    }

    private static class FramePageMap {

    	private final PageObject page;

		public FramePageMap(final WebDriver driver) {
			this.page = new PageObject(driver);
		}

		public APageMap gotoA(){
			return new APageMap(new DriverFinder(page, page.getRetryPolicyProvider(), "frame=a"));
		}

		public BPageMap gotoB(){
			return new BPageMap(new DriverFinder(page, page.getRetryPolicyProvider(), "frame=b"));
		}

    }

    private static class APageMap {

    	private final PageObject page;

		public APageMap(final DriverFinder finder) {
			this.page = new PageObject(finder);
		}
        public Element getDivA() {
            return page.find(Element.class,"id=div,xpath=a");
        }

    }

    private static class BPageMap {

    	private final PageObject page;

		public BPageMap(final DriverFinder finder) {
			this.page = new PageObject(finder);
		}

        public Element getDivB() {
            return page.find(Element.class,"id=div,xpath=a");
        }

    }
}
