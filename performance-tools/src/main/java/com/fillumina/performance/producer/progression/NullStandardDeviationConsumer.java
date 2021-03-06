package com.fillumina.performance.producer.progression;

import java.io.Serializable;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class NullStandardDeviationConsumer
        implements StandardDeviationConsumer, Serializable {
    private static final long serialVersionUID = 1L;

    public static final NullStandardDeviationConsumer INSTANCE =
            new NullStandardDeviationConsumer();

    private NullStandardDeviationConsumer() {}

    @Override
    public void consume(long iterations, long samples, double stdDev) {
        // do nothing
    }
}
