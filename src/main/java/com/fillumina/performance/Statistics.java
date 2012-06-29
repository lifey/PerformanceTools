package com.fillumina.performance;

import java.io.Serializable;
import java.util.Collection;

/**
 * Calculates statistics over a set of data.
 * The values are not retained and all statistics
 * are calculated on the run so its memory footprint is fixed whatever amount
 * of data is collected.
 *
 * @author fra
 */
public class Statistics implements Serializable {
    private static final long serialVersionUID = 1L;

    private long count;
    private double sum;
    private double max = Double.MIN_VALUE;
    private double min = Double.MAX_VALUE;
    private double M2, mean;

    public static Statistics[] createArray(final int size) {
        final Statistics[] array = new Statistics[size];
        for (int i=0; i<size; i++) {
            array[i] = new Statistics();
        }
        return array;
    }

    public Statistics(final double... values) {
        addAll(values);
    }

    public Statistics(final Collection<? extends Number> collection) {
        addAll(collection);
    }

    public final void addAll(final double... values) {
        for (double value: values) {
            add(value);
        }
    }

    public final void addAll(final Collection<? extends Number> collection) {
        for (Number value: collection) {
            add(value.doubleValue());
        }
    }

    public void add(final double value) {
        count++;
        sum += value;
        if (value > max) {
            max = value;
        }
        if (value < min) {
            min = value;
        }
        calculateVariance(value);
    }

    public double max() {
        return max;
    }

    public double min() {
        return min;
    }

    public long count() {
        return count;
    }

    public double sum() {
        return sum;
    }

    public double average() {
        return mean;
    }

    public double variance() {
        return M2 / count;
    }

    public double standardDeviation() {
        return Math.sqrt(variance());
    }

    // TODO: do you really need this?
    public void clear() {
        count = 0;
        sum = 0;
        min = Double.MAX_VALUE;
        max = Double.MIN_VALUE;
        M2 = 0;
        mean = 0;
    }

    /**
     * This is a running algorithm to calculate the variance.
     * See
     * <a href='http://en.wikipedia.org/wiki/Algorithms_for_calculating_variance'>
     * Wikipedia: Algorithm for calculating variance</a>:
     * <code><pre>
        def online_variance(data):
            n = 0
            mean = 0
            M2 = 0

            for x in data:
                n = n + 1
                delta = x - mean
                mean = mean + delta/n
                M2 = M2 + delta*(x - mean)

            variance_n = M2/n
            variance = M2/(n - 1)
            return (variance, variance_n)
    * </pre></code>
    */
    private void calculateVariance(final double x) {
        final double delta = x - mean;
        mean += delta / count;
        M2 += delta * (x - mean);
    }
}
