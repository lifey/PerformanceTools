package com.fillumina.performance.producer.timer;

import com.fillumina.performance.PerformanceTimerFactory;
import com.fillumina.performance.consumer.assertion.AssertPerformance;
import com.fillumina.performance.consumer.viewer.StringTableViewer;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class RunnableSinkTest {
    private static final String DEAD_CODE = "dead code";
    private static final String REFERENCE = "reference";
    private static final String SINKED = "sinked";

    private boolean printOut = false;

    public static void main(final String[] args) {
        final RunnableSinkTest test = new RunnableSinkTest();
        test.printOut = true;
        test.shouldEliminateDeadCode();
    }

    @Test
    public void shouldEliminateDeadCode() {
        final PerformanceTimer pt =
                PerformanceTimerFactory.createSingleThreaded();

        pt.addTest(DEAD_CODE, new RunnableSink() {
            private double d = 0d;

            @Override
            public Object sink() {
                // the following line is evicted
                double x = sinTaylor(d);
                d += 0.01;
                return null;
            }
        });

        pt.addTest(SINKED, new RunnableSink() {
            private double d = 0d;

            @Override
            public Object sink() {
                double x = sinTaylor(d);
                d += 0.01;
                return x;
            }
        });

        pt.addTest(REFERENCE, new RunnableSink() {
            private double d = 0d;

            @Override
            public Object sink() {
                d += 0.01;
                return null;
            }
        });

        if (printOut) {
            pt.addPerformanceConsumer(StringTableViewer.INSTANCE);
        }

        pt.addPerformanceConsumer(AssertPerformance.withTolerance(10)
                .assertTest(DEAD_CODE).sameAs(REFERENCE)
                .assertTest(SINKED).slowerThan(DEAD_CODE));

        pt.warmup(100_000);

        pt.iterate(100_000);
    }

    // for some reason the call to Math.sin() is not evicted even if dead
    // so this is the taylor expantion around 0 of sin(x)
    private double sinTaylor(final double d) {
        return d -
                pow(d, 3) / 6 +
                pow(d, 5) / 120 -
                pow(d, 7) / 5040 +
                pow(d, 9) / 362880 -
                pow(d, 11) / 39916800;
    }

    private static double pow(final double x, final int exponent) {
        if (exponent == 0) {
            return 1;
        }
        double result = x;
        for (int i=1; i<exponent; i++) {
            result *= x;
        }
        return result;
    }

    @Test
    public void shouldCalculatePow() {
        assertEquals(9, pow(3, 2), 0);
        assertEquals(8, pow(2, 3), 0);
        assertEquals(1, pow(12, 0), 0);
    }
}
