package com.fillumina.performance.consumer;

import com.fillumina.performance.producer.AbstractPerformanceProducer;

/**
 * Allows multiple consumers to get statistics from a single performance test.
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class TeePerformanceConsumer
        extends AbstractPerformanceProducer<TeePerformanceConsumer> {
    private static final long serialVersionUID = 1L;

    public static TeePerformanceConsumer createFrom(
            final PerformanceConsumer... consumers) {
        return new TeePerformanceConsumer(consumers);
    }

    public TeePerformanceConsumer() {
    }

    public TeePerformanceConsumer(final PerformanceConsumer... consumers) {
        super(consumers);
    }
}
