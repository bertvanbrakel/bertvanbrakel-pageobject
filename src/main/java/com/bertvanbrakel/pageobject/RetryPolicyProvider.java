package com.bertvanbrakel.pageobject;

import static  com.bertvanbrakel.lang.Check.*;
import static org.hamcrest.Matchers.*;
import org.junit.*;
import static org.junit.Assert.*;
import static org.junit.Assume.*;
import static org.junit.matchers.JUnitMatchers.*;

public interface RetryPolicyProvider {

    RetryPolicy getRetryPolicy();

}
