package com.bertvanbrakel.pageobject;

import org.openqa.selenium.SearchContext;
//TODO:merge with provider
public interface Finder<E> extends SearchContext { //, Provider<E> {

	public E find();
	public E find(RetryPolicyProvider policyProvider);
	

}