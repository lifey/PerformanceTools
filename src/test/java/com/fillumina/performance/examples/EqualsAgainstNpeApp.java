package com.fillumina.performance.examples;

import com.fillumina.performance.assertion.AssertPerformance;
import com.fillumina.performance.timer.PerformanceTimer;
import com.fillumina.performance.timer.PerformanceTimerBuilder;
import com.fillumina.performance.sequence.ProgressionSequence;
import com.fillumina.performance.view.StringCsvViewer;
import com.fillumina.performance.view.StringTableViewer;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author fra
 */
public class EqualsAgainstNpeApp {
    private static final Check TEST = new Check(new Object());
    private static final Check NULL = new Check(null);

    public static void main(final String[] args) {
        new EqualsAgainstNpeApp().test();
    }

    private static abstract class AlternateRunnable implements Runnable {
        private int value = 0;
        protected Object getAlternateNUll() {
            value++;
//            if (value % 2 == 0) {
//                return this; // class cast exception
//            }
//            if (value % 11 == 0) {
//                return NULL; // null pointer exception
//            }
            return TEST;
        }
    }

    public void test() {
        final PerformanceTimer pt = PerformanceTimerBuilder.createSingleThread();

        pt.addTest("standard equals", new AlternateRunnable() {

            @Override
            public void run() {
                final Object obj = getAlternateNUll();
                if (TEST.equalsOld(obj)) {
                    if (obj == NULL) {
                        throw new RuntimeException();
                    }
                }
            }
        });

        pt.addTest("trycatch equals", new AlternateRunnable() {

            @Override
            public void run() {
                final Object obj = getAlternateNUll();
                if (TEST.equalsTryCatch(obj)) {
                    if (obj == NULL) {
                        throw new RuntimeException();
                    }
                }
            }
        });

        new ProgressionSequence(pt)
                .setTimeout(10, TimeUnit.SECONDS)
                .setOnIterationPerformanceConsumer(new StringCsvViewer())
                .setPerformanceConsumer(new StringTableViewer())
                .serie(1000_000, 3, 10)
                .use(new AssertPerformance())
                .assertTest("standard equals").equalsTo("trycatch equals");

    }

    private static class Check {
        private final Object object;

        public Check(final Object object) {
            this.object = object;
        }

        public boolean equalsOld(final Object obj) {
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final Check other = (Check) obj;
            if (!Objects.equals(this.object, other.object)) {
                return false;
            }
            return true;
        }

        // because most of the time equals is checked against same type, not null objects
        // this method is faster because it avoids the checks.
        public boolean equalsTryCatch(final Object obj) {
            try {
                final Check other = (Check) obj;
                if (!Objects.equals(this.object, other.object)) {
                    return false;
                }
                return true;
            } catch (ClassCastException | NullPointerException ex) {
                //assert !(obj instanceof Check);
                return false;
            }
        }
    }
}
