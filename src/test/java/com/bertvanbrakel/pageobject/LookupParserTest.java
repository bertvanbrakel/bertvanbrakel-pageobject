package com.bertvanbrakel.pageobject;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.junit.Test;

import com.bertvanbrakel.pageobject.LookupParser.ParseCallback;

public class LookupParserTest {

    @Test
    public void test_parse_null_lookup() {
        parse_test(null,new ArrayList<Pair>());
   }

    @Test
    public void test_parse_empty_lookup() {
        parse_test("",new ArrayList<Pair>());
    }

    @Test
    public void test_parse_single_lookup() {
        parse_test("id=a",new Pair("id","a"));
    }

    @Test
    public void test_parse_dual_lookup() {
    	parse_test("id=a,xpath=//div",new Pair("id","a"),new Pair("xpath","//div"));
    }

    @Test
    public void test_parse_quoted_lookup() {
    	parse_test("id='a'",new Pair("id","a"));
    }
    
    @Test
    public void test_parse_ignored_quote_lookup() {
    	parse_test("xpath=//div[@class='foo']",new Pair("xpath","//div[@class='foo']"));
    }

    @Test
    public void test_parse_a_long_expression() {
        parse_test("id='a',name=123,xpath=//div[@class='foo' & @x=xyz]",new Pair("id","a"), new Pair("name","123"), new Pair("xpath","//div[@class='foo' & @x=xyz]"));
    }

    private void parse_test(String expression,Pair... pairs) {
    	List<Pair> list = new ArrayList<LookupParserTest.Pair>();
    	for(Pair p:pairs){
    		list.add(p);
    	}
    	parse_test(expression, list);
    }
    private void parse_test(String expression,Collection<Pair> expect) {
    	final ArrayList<Pair> found = new ArrayList<Pair>();
    	LookupParser.parse(expression, new ParseCallback() {
			
			@Override
			public void found(String name, String value) throws ParseException {
				found.add(new Pair(name,value));
			}
		});
        assertEquals(new ArrayList<Pair>(expect),found);        
   }
    
    private static class Pair {
    	public final String name;
    	public final String value;
		public Pair(String name, String value) {
			super();
			this.name = name;
			this.value = value;
		}
    	
	    @Override
	    public int hashCode() {
	        return HashCodeBuilder.reflectionHashCode(37, 3, this);
	    };

	    @Override
	    public boolean equals(final Object obj) {
	        return EqualsBuilder.reflectionEquals(this, obj);
	    };

	    @Override
	    public String toString() {
	        return ToStringBuilder.reflectionToString(this,
	                ToStringStyle.SHORT_PREFIX_STYLE);
	    }

    }

   
}
