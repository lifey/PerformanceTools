package com.fillumina.performance;

import com.fillumina.performance.producer.timer.MultiThreadPerformanceTestExecutor;
import com.fillumina.performance.producer.timer.MultiThreadPerformanceTestExecutorBuilder;
import com.fillumina.performance.producer.timer.PerformanceTimer;
import com.fillumina.performance.producer.timer.SingleThreadPerformanceTestExecutor;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author fra
 */
public class PerformanceTimerBuilder {

    public static PerformanceTimer createSingleThread() {
        return new PerformanceTimer(new SingleThreadPerformanceTestExecutor());
    }

    public static class MultiThreadBuilder {
        final MultiThreadPerformanceTestExecutorBuilder delegate;

        public MultiThreadBuilder(
                final MultiThreadPerformanceTestExecutorBuilder delegate) {
            this.delegate = delegate;
        }

        public MultiThreadBuilder setTimeout(int timeout, TimeUnit unit) {
            delegate.setTimeout(timeout, unit);
            return this;
        }

        public MultiThreadBuilder setWorkerNumber(int workerNumber) {
            delegate.setWorkerNumber(workerNumber);
            return this;
        }

        public MultiThreadBuilder setConcurrencyLevel(int concurrencyLevel) {
            delegate.setConcurrencyLevel(concurrencyLevel);
            return this;
        }

        public PerformanceTimer build() {
            final MultiThreadPerformanceTestExecutor testExecutor = delegate.build();
            return new PerformanceTimer(testExecutor);
        }
    }

    public static MultiThreadBuilder createMultiThread() {
        return new MultiThreadBuilder(
                new MultiThreadPerformanceTestExecutorBuilder());
    }

}