package com.bertvanbrakel.pageobject;


public interface Finder<E> {

	/**
	 * Find our element, retrying if required.
	 * @return
	 */
	public E find();
	
	public E find(RetryPolicyProvider policyProvider);
	

}