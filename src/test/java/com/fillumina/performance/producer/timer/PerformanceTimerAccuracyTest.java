package com.fillumina.performance.producer.timer;

import com.fillumina.performance.PerformanceTimerBuilder;
import com.fillumina.performance.consumer.viewer.StringTableViewer;
import com.fillumina.performance.consumer.assertion.AssertPerformance;
import java.util.concurrent.TimeUnit;
import org.junit.Test;
import static com.fillumina.performance.utils.PerformanceTimeHelper.*;

/**
 * @author fra
 */
public class PerformanceTimerAccuracyTest {
    private int loops = 2_500;
    private boolean printOut = false;

    public static void main(final String[] args) {
        PerformanceTimerAccuracyTest test = new PerformanceTimerAccuracyTest();
        test.printOut = true;
        test.loops = 10_000;
        test.shouldSingleThreadBeAccurate();
        test.shouldMultiThreadingBeAccurateUsingOnlyOneThread();
        test.shouldMultiThreadingBeAccurate();
    }

    @Test
    public void shouldSingleThreadBeAccurate() {
        final PerformanceTimer pt = PerformanceTimerBuilder.createSingleThread();

        setTests(pt);

        pt.iterate(loops);

        printOutPercentages("SINGLE", pt);

        assertPerformance(pt);
    }

    @Test
    public void shouldMultiThreadingBeAccurateUsingOnlyOneThread() {
        final PerformanceTimer pt = PerformanceTimerBuilder.createMultiThread()
                .setConcurrencyLevel(1)
                .setWorkerNumber(1)
                .setTimeout(10, TimeUnit.SECONDS)
                .build();

        setTests(pt);

        pt.iterate(loops);

        printOutPercentages("MULTI (single thread)", pt);

        assertPerformance(pt);
    }

    @Test
    public void shouldMultiThreadingBeAccurate() {
        final  int cpus = Runtime.getRuntime().availableProcessors();

        final PerformanceTimer pt = PerformanceTimerBuilder.createMultiThread()
                .setConcurrencyLevel(cpus)
                .setWorkerNumber(cpus)
                .setTimeout(10, TimeUnit.SECONDS)
                .build();

        setTests(pt);

        pt.iterate(loops);

        printOutPercentages("MULTI (" + cpus + " threads)", pt);

        assertPerformance(pt);

    }

    private void setTests(final PerformanceTimer pt) {
        pt.addTest("null", new Runnable() {

            @Override
            public void run() {
            }
        });

        pt.addTest("single", new Runnable() {

            @Override
            public void run() {
                sleepMicroseconds(100);
            }
        });

        pt.addTest("double", new Runnable() {

            @Override
            public void run() {
                sleepMicroseconds(200);
            }
        });

        pt.addTest("triple", new Runnable() {

            @Override
            public void run() {
                sleepMicroseconds(300);
            }
        });
    }

    private void printOutPercentages(final String message,
            final PerformanceTimer pt) {
        if (printOut) {
            pt.use(new StringTableViewer())
                .setMessage(message)
                .getTable()
                .println();
        }
    }

    private void assertPerformance(final PerformanceTimer pt) {
        pt.use(new AssertPerformance())
            .setPercentageTolerance(10) // super safe

            .assertPercentageFor("null").equals(0)
            .assertPercentageFor("single").equals(33)
            .assertPercentageFor("double").equals(66)
            .assertPercentageFor("triple").equals(100);
    }

}