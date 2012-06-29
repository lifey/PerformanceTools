package com.fillumina.performance;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
// TODO: use a strategy inside the same PerformanceTimes: it's cleaner
public class MultiThreadPerformanceTimer extends AbstractPerformanceTimer
        implements Serializable {
    private static final long serialVersionUID = 1L;

    private final int concurrencyLevel;
    private final int taskNumber;
    private final int timeout;
    private final TimeUnit unit;

    public static class Builder {
        private int concurrencyLevel = -1;
        private int taskNumber;
        private int timeout;
        private TimeUnit unit;

        public Builder setConcurrencyLevel(final int concurrencyLevel) {
            this.concurrencyLevel = concurrencyLevel;
            return this;
        }

        public Builder setTaskNumber(final int taskNumber) {
            this.taskNumber = taskNumber;
            return this;
        }

        public Builder setTimeout(final int timeout, final TimeUnit unit) {
            this.timeout = timeout;
            this.unit = unit;
            return this;
        }

        public MultiThreadPerformanceTimer build() {
            if (taskNumber < 0 || timeout < 0 ||
                    unit == null) {
                throw new IllegalArgumentException();
            }
            return new MultiThreadPerformanceTimer(this);
        }
    }

    private MultiThreadPerformanceTimer(final Builder builder) {
        this.concurrencyLevel = builder.concurrencyLevel;
        this.taskNumber = builder.taskNumber;
        this.timeout = builder.timeout;
        this.unit = builder.unit;
    }


    @Override
    protected void executeTests(final int times,
            final Map<String, Runnable> executors,
            final Performance timeMap) {
        for (Map.Entry<String, Runnable> entry: executors.entrySet()) {
            final String msg = entry.getKey();
            final Runnable runnable = entry.getValue();

            final List<IteratingRunnable> tasks = createTasks(runnable, times);

            final long time = System.nanoTime();

            iterateOn(tasks);

            timeMap.add(msg, System.nanoTime() - time);
        }
    }

    private void iterateOn(List<IteratingRunnable> tasks) {
        final ExecutorService executor = createExecutor();

        for (IteratingRunnable task: tasks) {
            executor.execute(task);
        }

        executor.shutdown();

        boolean alreadyTerminated = false;
        try {
            alreadyTerminated = executor.awaitTermination(timeout, unit);
        } catch (InterruptedException e) {
            throw createTaskTookTooLongException(timeout, unit, e);
        }
        if (!alreadyTerminated) {
            throw createTaskTookTooLongException(timeout, unit, null);
        }
    }

    private RuntimeException createTaskTookTooLongException(
            final int timeout, final TimeUnit unit, final Exception e) {
        return new RuntimeException("Task took longer than maximum time allowed " +
                 "to complete: " + timeout + " " + unit, e);
    }

    private ExecutorService createExecutor() {
        if (concurrencyLevel < 1) {
            return Executors.newCachedThreadPool();
        }
        return Executors.newFixedThreadPool(concurrencyLevel);
    }

    private static class IteratingRunnable implements Runnable {
        private final Runnable test;
        private final long iterations;

        public IteratingRunnable(Runnable test, int iterations) {
            this.test = test;
            this.iterations = iterations;
        }

        @Override
        public void run() {
            for (long i=0; i<iterations; i++) {
                test.run();
            }
        }
    }

    private List<IteratingRunnable> createTasks(
            final Runnable runnable, final int iterations) {
        final List<IteratingRunnable> list = new ArrayList<>(taskNumber);

        for(long i=0; i<taskNumber; i++) {
            list.add(new IteratingRunnable(runnable, iterations));
        }

        return list;
    }

}
