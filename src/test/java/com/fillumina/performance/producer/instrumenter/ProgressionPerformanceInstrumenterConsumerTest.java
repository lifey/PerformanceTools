package com.fillumina.performance.producer.instrumenter;

import com.fillumina.performance.PerformanceTimerBuilder;
import com.fillumina.performance.producer.PerformanceConsumerTestHelper;

/**
 *
 * @author fra
 */
public class ProgressionPerformanceInstrumenterConsumerTest
        extends PerformanceConsumerTestHelper {

    @Override
    public void executePerformanceProducerWithConsumers(
            final PerformanceConsumerTestHelper.ConsumerExecutionChecker... consumers) {

        PerformanceTimerBuilder
                .createSingleThread()

                .addTest("example", new Runnable() {

                    @Override
                    public void run() {
                        // do nothing
                    }
                })

                .instrumentedBy(new ProgressionPerformanceInstrumenter.Builder())
                .setBaseAndMagnitude(1, 1)
                .build()
                .addPerformanceConsumer(consumers)
                .executeSequence();
    }

}