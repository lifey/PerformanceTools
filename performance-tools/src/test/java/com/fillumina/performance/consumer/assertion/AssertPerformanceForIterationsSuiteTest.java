package com.fillumina.performance.consumer.assertion;

import com.fillumina.performance.producer.FakeLoopPerformancesCreator;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class AssertPerformanceForIterationsSuiteTest {

    @Test
    public void shouldCheckTheAssertions() {
        final AssertPerformanceForIterationsSuite assertion =
                new AssertPerformanceForIterationsSuite();

        assertion.forIterations(10)
                .assertPercentageFor("First").sameAs(10F);

        assertion.forIterations(100)
                .assertPercentageFor("First").sameAs(20F);

        assertion.consume("Some message", FakeLoopPerformancesCreator.parse(10,
                new Object[][] {
                    {"First", 10},
                    {"Full", 100}
                }));

        assertion.consume("Some message", FakeLoopPerformancesCreator.parse(100,
                new Object[][] {
                    {"First", 20},
                    {"Full", 100}
                }));
    }

    @Test
    public void shouldRiseAnAssertionErrorIfNotMatching() {
        final AssertPerformanceForIterationsSuite assertion =
                new AssertPerformanceForIterationsSuite();

        assertion.forIterations(10)
                .assertPercentageFor("First").sameAs(10F);

        assertion.forIterations(100)
                .assertPercentageFor("First").sameAs(20F);

        assertion.consume("First Round", FakeLoopPerformancesCreator.parse(10,
                new Object[][] {
                    {"First", 10},
                    {"Full", 100}
                }));

        try {
            assertion.consume("Second Round", FakeLoopPerformancesCreator.parse(100,
                new Object[][] {
                    {"First", 30},
                    {"Full", 100}
                }));
            fail();
        } catch (AssertionError e) {
            assertEquals("Second Round 'First' expected equals to 20.00 %, " +
                    "found 30.00 % with a tolerance of 7.0 %",
                    e.getMessage());
        }
    }
}
