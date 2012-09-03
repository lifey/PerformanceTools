package com.fillumina.performance.producer.instrumenter;

import com.fillumina.performance.producer.timer.AbstractPerformanceTimer;
import com.fillumina.performance.producer.timer.PerformanceTimerInstrumenter;
import java.io.Serializable;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author fra
 */
public abstract class AbstractIstrumenterBuilder
            <T extends PerformanceTimerInstrumenter, V extends PerformanceInstrumenter<?>>
        implements PerformanceTimerInstrumenter, Serializable {
    private static final long serialVersionUID = 1L;

    private AbstractPerformanceTimer performanceTimer;
    private long[] iterationsProgression;
    private int samples;
    private long timeout;

    public abstract V build();

    protected void check() {
        if (iterationsProgression == null || iterationsProgression.length == 0) {
            throw new IllegalArgumentException(
                    "no iteration progression specified: " +
                    Arrays.toString(iterationsProgression));
        }
        if (getSamples() <= 0) {
            throw new IllegalArgumentException(
                    "cannot manage negative or 0 samples: " + getSamples());
        }
        if (getTimeout() <= 0) {
            throw new IllegalArgumentException(
                    "cannot manage negative or 0 timeout: " + getTimeout());
        }
    }

    /** Mandatory. */
    @Override
    @SuppressWarnings("unchecked")
    public T instrument(final AbstractPerformanceTimer performanceTimer) {
        this.performanceTimer = performanceTimer;
        return (T) this;
    }

    /**
     * Alternative to
     * {@link #setBaseAndMagnitude(long[]) }.
     */
    @Deprecated // shoulden't be here
    @SuppressWarnings("unchecked")
    public T setIterationProgression(final long... iterationsProgression) {
        this.iterationsProgression = iterationsProgression;
        return (T) this;
    }

    /**
     * Alternative to
     * {@link #setIterationsProgression(long[]) }.
     */
    @Deprecated // shoulden't be here
    @SuppressWarnings("unchecked")
    public T setBaseAndMagnitude(final int baseTimes,
            final int maximumMagnitude) {
        iterationsProgression = new long[maximumMagnitude];
        for (int magnitude = 0; magnitude < maximumMagnitude;
                magnitude++) {
            iterationsProgression[magnitude] = calculateLoops(baseTimes,
                    magnitude);
        }
        return (T) this;
    }

    /** Optional, default to 10 samples per iteration. */
    @SuppressWarnings("unchecked")
    public T setSamplePerIterations(final int samples) {
        this.samples = samples;
        return (T) this;
    }

    /** Optional, default to 10 seconds. */
    @SuppressWarnings("unchecked")
    public T setTimeout(final long timeout,
            final TimeUnit unit) {
        this.timeout = TimeUnit.NANOSECONDS.convert(timeout, unit);
        return (T) this;
    }

    private static long calculateLoops(final int baseTimes, final int magnitude) {
        return Math.round(baseTimes * Math.pow(10, magnitude));
    }

    public long[] getIterationsProgression() {
        return iterationsProgression;
    }

    public AbstractPerformanceTimer getPerformanceTimer() {
        return performanceTimer;
    }

    public int getSamples() {
        return samples;
    }

    public long getTimeout() {
        return timeout;
    }
}
