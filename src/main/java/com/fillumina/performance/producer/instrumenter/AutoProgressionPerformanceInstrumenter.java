package com.fillumina.performance.producer.instrumenter;

import com.fillumina.performance.consumer.PerformanceConsumer;
import com.fillumina.performance.producer.timer.LoopPerformancesHolder;
import java.io.Serializable;
import java.util.concurrent.TimeUnit;

/**
 * Increments the iterations up to the point where the performances
 * stabilize. It then produces
 * statistics based on the average results of the last iterations. It
 * produces accurate measures but may be very long to execute.
 *
 * @author fra
 */
public class AutoProgressionPerformanceInstrumenter
        implements Serializable,
        PerformanceInstrumenter<AutoProgressionPerformanceInstrumenter> {
    private static final long serialVersionUID = 1L;

    public static final int MINIMUM_ITERATIONS = 1000;
    public static final int MAXIMUM_MAGNITUDE = 10;
    public static final int SAMPLE_PER_MAGNITUDE = 10;

    private final ProgressionPerformanceInstrumenter progressionSerie;
    private final double maxStandardDeviation;

    public static class Builder
            extends AbstractIstrumenterBuilder<Builder,
                    AutoProgressionPerformanceInstrumenter>
            implements Serializable {
        private static final long serialVersionUID = 1L;

        private double maxStandardDeviation = 1.5D;

        public Builder() {
            super();
            setBaseAndMagnitude(1_000, 8);
            setSamplePerIterations(10);
            setTimeout(5, TimeUnit.SECONDS);
        }

        @Override
        public AutoProgressionPerformanceInstrumenter build() {
            check();
            if (maxStandardDeviation <= 0) {
                throw new IllegalArgumentException(
                        "maxStandardDeviation cannot be less than 0: " +
                        maxStandardDeviation);
            }
            return new AutoProgressionPerformanceInstrumenter(this);
        }

        /**
         * Reasonable values are between 0.4 and 1.5. If the value is too
         * low the sequence may not stabilize and the algorithm may
         * consequently not stop, if it is too high the results
         * may be grossly inaccurate.
         */
        public Builder setMaxStandardDeviation(final double maxStandardDeviation) {
            this.maxStandardDeviation = maxStandardDeviation;
            return this;
        }
    }

    public AutoProgressionPerformanceInstrumenter(final Builder builder) {
        this.maxStandardDeviation = builder.maxStandardDeviation;
        this.progressionSerie = new ProgressionPerformanceInstrumenter(builder) {
            private static final long serialVersionUID = 1L;

            @Override
            protected boolean stopIterating(SequencePerformances serie) {
                return AutoProgressionPerformanceInstrumenter.this
                        .stopIterating(serie);
            }
        };
    }

    private boolean stopIterating(final SequencePerformances serie) {
        return serie.calculateMaximumStandardDeviation() < maxStandardDeviation;
    }

    @Override
    public AutoProgressionPerformanceInstrumenter
            addPerformanceConsumer(final PerformanceConsumer... consumers) {
        progressionSerie.addPerformanceConsumer(consumers);
        return this;
    }

    @Override
    public LoopPerformancesHolder executeSequence() {
        return progressionSerie.executeSequence();
    }

}
