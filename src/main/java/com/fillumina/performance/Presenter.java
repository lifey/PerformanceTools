package com.fillumina.performance;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author fra
 */
public class Presenter implements Serializable {
    private static final long serialVersionUID = 1L;

    private final PerformanceData performance;
    private String message;

    public Presenter(final PerformanceData performance) {
        this.performance = performance;
    }

    public Presenter(final PerformanceTimer pt) {
        this.performance = pt.getPerformance();
    }

    public Presenter addMessage(final String message) {
        this.message = message;
        return this;
    }

    // TODO: put an intermediate actor here to prepare the data that the presenter shoud only show
    // TODO: automatize time unit
    public OutputHolder getComparisonString(final TimeUnit unit) {
        final StringBuilder buf = createStringBuilder();

        final long fastest = getFastest();
        final int longer = getLongerMessageSize();

        for (Map.Entry<String, Long> entry: performance.getTimeMap().entrySet()) {
            final Long msg = entry.getValue();
            final Long elapsedNano = entry.getValue();

            final double elapsed = elapsedNano / performance.getIterations();
            final double percentageValue = msg * 1.0D / fastest * 100;

            buf.append(equilizeLength(entry.getKey(), longer))
                    .append(" :")
                    .append(TimeUnitHelper.formatUnit(elapsed, unit))
                    .append("\t")
                    .append(formatPercentage(percentageValue))
                    .append("\n");
        }
        return new OutputHolder(buf.toString());
    }

    private StringBuilder createStringBuilder() {
        final StringBuilder builder = new StringBuilder();
        if (message != null) {
            builder.append(message).append('\n');
        }
        return builder;
    }

    private String formatPercentage(final double percentageValue) {
        return String.format("\t%10.2f %%", percentageValue);
    }

    public OutputHolder getIncrementsString(final TimeUnit unit) {
        final StringBuilder buf = createStringBuilder();
        final long totalTime = getTotal();
        final int longer = getLongerMessageSize();

        int index = 0;
        for (Map.Entry<String, Long> entry: performance.getTimeMap().entrySet()) {
            final Long elaplsedNano = entry.getValue();
            final String msg = entry.getKey();

            final double avgNano = elaplsedNano * 1.0D / performance.getIterations();
            final double percentageValue = elaplsedNano * 1.0D / totalTime * 100;

            buf.append(equilizeLength(msg, longer))
                    .append(String.format("\t%4d :", index))
                    .append(String.format("\t%10.2f ns", avgNano))
                    .append("\t")
                    .append(formatPercentage(percentageValue))
                    .append("\n");

            index++;
        }
        buf.append(equilizeLength("*", longer)).append("     :")
                .append(String.format("\t%10.2f ns", totalTime * 1.0D / performance.getIterations()));
        return new OutputHolder(buf.toString());
    }

    public String toCsvString(final long executions) {
        StringBuilder buf = createStringBuilder();
        buf.append(String.format("%15d", executions));
        buf.append(CsvHelper.toCsvString(performance.getPercentages()));
        return buf.toString();
    }

    private int getLongerMessageSize() {
        int longer = 0;
        for (String msg: performance.getTimeMap().keySet()) {
            final int length = msg.length();
            if (length > longer) {
                longer = length;
            }
        }
        return longer;
    }

    private static String equilizeLength(final String str, final int len) {
        final int length = str.length();
        if (length < len) {
            final char[] carray = new char[len - length];
            Arrays.fill(carray, ' ');
            return str + new String(carray);
        }
        return str;
    }

    private long getFastest() {
        return Math.round(performance.getStatistics().max());
    }

    private long getTotal() {
        return Math.round(performance.getStatistics().sum());
    }

}
