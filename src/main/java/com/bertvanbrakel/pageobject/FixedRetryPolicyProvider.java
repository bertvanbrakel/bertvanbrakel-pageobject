package com.bertvanbrakel.pageobject;

import static com.bertvanbrakel.lang.Check.checkTrue;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * A retry provider which returns policies based on fixed max wait time, and a fixed
 * number of retries. Time between retries is determined by max time divided by
 * number of times to try. 
 * 
 * @author bert
 *
 */
public class FixedRetryPolicyProvider implements RetryPolicyProvider {

    private final long maxTimeToWaitForInMs;
    private final int maxAttempts;

    public FixedRetryPolicyProvider(final long maxTimeMs, final int maxAttempts) {
        checkTrue("maxTime", maxTimeMs, maxTimeMs > 0, "greater than 0");
        checkTrue("maxAttempts", maxAttempts, maxAttempts > 0, "greater than 0");

        this.maxTimeToWaitForInMs = maxTimeMs;
        this.maxAttempts = maxAttempts;
    }

    @Override
    public RetryPolicy getRetryPolicy() {
        return new SimpleRetryPolicy(maxTimeToWaitForInMs, maxAttempts);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(13, 3, this);
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

    static class SimpleRetryPolicy implements RetryPolicy {

        private final Object LOCK = new Object();

        private final long maxTimeToWaitForInMs;
        private final int maxAttempts;
        private final long startTimeMs;

        private final long endTimeMs;
        private final long delayIntervalMs;

        public SimpleRetryPolicy(final long maxTimeToWaitForInMs, final int maxAttempts) {
            checkTrue("maxTimeToWaitForInMs", maxTimeToWaitForInMs, maxTimeToWaitForInMs > 0, "greater than 0");
            checkTrue("maxAttempts", maxAttempts, maxAttempts > 0, "greater than 0");

            // this is for better toString data
            this.maxTimeToWaitForInMs = maxTimeToWaitForInMs;
            this.maxAttempts = maxAttempts;

            // this is what we will be using
            this.startTimeMs = System.currentTimeMillis();
            this.endTimeMs = startTimeMs + maxTimeToWaitForInMs;
            this.delayIntervalMs = maxTimeToWaitForInMs / maxAttempts;
        }

        @Override
        public boolean isRetry() {
            return endTimeMs - System.currentTimeMillis() > 0;
        }

        @Override
        public void waitUntilNextAttempt() {
            synchronized (LOCK) {
                try {
                    LOCK.wait(delayIntervalMs);
                } catch (final InterruptedException e) {
                    // carry on
                }
            }
        }

        @Override
        public int hashCode() {
            return HashCodeBuilder.reflectionHashCode(7, 3, this);
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
