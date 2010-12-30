package com.bertvanbrakel.pageobject;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.runner.RunWith;

import com.bertvanbrakel.testserver.TestServer;
import com.bertvanbrakel.testserver.TestServlet;

@RunWith(Theories.class)
public abstract class AbstractContentTest {

	@DataPoints
	public static final Browser[] browsers = new Browser[]{Browser.HTMLUNIT};
	
    private static TestServer server;
    private static int serverPort  = 0;

    @BeforeClass
    public static void classSetup() throws Exception {
        server = new TestServer();
        if( serverPort > 0){
        	server.setHttpPort(serverPort);
        }
        server.start();
    }
    
    protected static void setPort(int port){
    	serverPort = port;
    }

    @AfterClass
    public static void classTearDown() {
        server.stop();
        serverPort = 0;
    }

    protected TestServer getServer(){
        return server;
    }

    protected void servePageContent(final String servletPath, final CharSequence content){
        server.addServlet(servletPath, new TestServlet() {
            private static final long serialVersionUID = -8435089373543974573L;

            @Override
            protected void doGet(final HttpServletRequest req,
                    final HttpServletResponse resp) throws ServletException,
                    IOException {
                resp.setStatus(HttpServletResponse.SC_OK);
                final PrintWriter w = resp.getWriter();
                w.write(content.toString());
                w.flush();
            }
        });
    }

    protected void serveBodyContent(final String servletPath, final CharSequence bodyContent){
        servePageContent(servletPath, "<html><head></head><body>" + bodyContent + "</body></html>");
    }

    protected String getBaseHttpUrl(){
        return server.getBaseHttpUrl();
    }
    
}
