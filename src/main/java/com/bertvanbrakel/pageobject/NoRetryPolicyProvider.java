package com.bertvanbrakel.pageobject;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * A retry provider which returns policies which never retry
 * 
 * @author bert
 *
 */
public class NoRetryPolicyProvider implements RetryPolicyProvider {

    public static final NoRetryPolicyProvider INSTANCE = new NoRetryPolicyProvider();

    private static final RetryPolicy POLICY = new RetryPolicy() {

        @Override
        public void waitUntilNextAttempt() {
        }

        @Override
        public boolean isRetry() {
            return false;
        }

        @Override
        public String toString() {
            return "NoRetryPolicy";
        }
    };

    @Override
    public RetryPolicy getRetryPolicy() {
        return POLICY;
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(15, 7, this);
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
