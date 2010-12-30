package com.bertvanbrakel.pageobject;


public interface RetryPolicy {

    public boolean isRetry();

    public void waitUntilNextAttempt();

}
