package com.fillumina.performance.producer.suite;

import com.fillumina.performance.producer.LoopPerformancesHolder;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public interface ParametrizedExecutor<P> {

    /**
     * Executes the given test against the previously added parameters.
     * The default name for the test will be {@code null}.
     *
     * @return the same performance given to the consumer.
     */
    LoopPerformancesHolder executeTest(
            final ParametrizedRunnable<? extends P> test);

    /**
     * Executes the given named test against the previously added parameters.
     *
     * @return the same performance given to the consumer.
     */
    @SuppressWarnings(value = "unchecked")
    LoopPerformancesHolder executeTest(final String name,
            final ParametrizedRunnable<? extends P> test);

    LoopPerformancesHolder ignoreTest(
            final ParametrizedRunnable<? extends P> test);

    LoopPerformancesHolder ignoreTest(final String name,
            final ParametrizedRunnable<? extends P> test);

}
