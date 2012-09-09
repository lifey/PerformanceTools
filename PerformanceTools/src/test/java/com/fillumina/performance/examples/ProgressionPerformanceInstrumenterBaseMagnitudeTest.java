package com.fillumina.performance.examples;

import com.fillumina.performance.consumer.assertion.AssertPerformance;
import com.fillumina.performance.producer.timer.PerformanceTimer;
import com.fillumina.performance.PerformanceTimerBuilder;
import com.fillumina.performance.consumer.NullPerformanceConsumer;
import com.fillumina.performance.consumer.PerformanceConsumer;
import com.fillumina.performance.producer.progression.ProgressionPerformanceInstrumenter;
import com.fillumina.performance.consumer.viewer.StringCsvViewer;
import com.fillumina.performance.consumer.viewer.StringTableViewer;
import com.fillumina.performance.util.junit.JUnitPerformanceTestRunner;
import java.util.concurrent.TimeUnit;
import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 *
 * @author fra
 */
@RunWith(JUnitPerformanceTestRunner.class)
public class ProgressionPerformanceInstrumenterBaseMagnitudeTest {

    public static void main(final String[] args) {
        new ProgressionPerformanceInstrumenterBaseMagnitudeTest()
                .test(StringCsvViewer.CONSUMER, StringTableViewer.CONSUMER);
    }

    @Test
    public void shouldTheStringConcatenationBeFasterThanStringBuilder() {
        test(NullPerformanceConsumer.INSTANCE, NullPerformanceConsumer.INSTANCE);
    }

    public void test(final PerformanceConsumer iterationConsumer,
            final PerformanceConsumer resultConsumer) {
        final PerformanceTimer pt = PerformanceTimerBuilder.createSingleThread();

        pt.addTest("string concatenation", new Runnable() {

            @Override
            public void run() {
                final String str = "This " + "is " +
                        System.currentTimeMillis() +
                        "a " + "new " +
                        System.currentTimeMillis() +
                        "string.";
                assertString( str);
            }

        });

        pt.addTest("string builder", new Runnable() {

            @Override
            public void run() {
                final String str = new StringBuilder()
                    .append("This ")
                    .append("is ")
                    .append(System.currentTimeMillis())
                    .append("a ")
                    .append("new ")
                    .append(System.currentTimeMillis())
                    .append("string.")
                    .toString();
                assertString(str);
            }
        });

        pt.addPerformanceConsumer(iterationConsumer);

        pt.instrumentedBy(ProgressionPerformanceInstrumenter.builder())
                .setTimeout(10, TimeUnit.SECONDS)
                .setBaseAndMagnitude(1_000, 2)
                .setSamplesPerMagnitude(15)
                .build()
                .addPerformanceConsumer(resultConsumer)
                .addPerformanceConsumer(AssertPerformance.withTolerance(10)
                    .assertTest("string concatenation").equalsTo("string builder"))
                .execute();

    }

    private void assertString(final String str) {
        assertTrue(str.contains("This is"));
    }
}