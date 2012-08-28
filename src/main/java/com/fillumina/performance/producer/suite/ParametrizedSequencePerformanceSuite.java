package com.fillumina.performance.producer.suite;

import com.fillumina.performance.producer.AbstractPerformanceProducer;
import com.fillumina.performance.producer.timer.InitializableRunnable;
import com.fillumina.performance.producer.timer.LoopPerformances;
import com.fillumina.performance.producer.timer.PerformanceTimer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author fra
 */
public class ParametrizedSequencePerformanceSuite<P,S>
        extends AbstractPerformanceProducer<ParametrizedSequencePerformanceSuite<P,S>> {
    private static final long serialVersionUID = 1L;

    private final PerformanceTimer performanceTimer;
    private final List<ObjectMatrixInnerRunnable> runnables = new ArrayList<>();
    private ParametrizedSequenceRunnable<P,S> callable;
    private Iterable<S> sequence;

    public ParametrizedSequencePerformanceSuite(
            final PerformanceTimer performanceTimer) {
        this.performanceTimer = performanceTimer;
    }

    public ParametrizedSequencePerformanceSuite<P,S> addObjectToTest(
            final String message, final P t) {
        final ObjectMatrixInnerRunnable sir = new ObjectMatrixInnerRunnable(t);
        runnables.add(sir);
        performanceTimer.addTest(message, sir);
        return this;
    }

    public ParametrizedSequencePerformanceSuite<P,S> setSequence(
            final Iterable<S> iterable) {
        this.sequence = iterable;
        return this;
    }

    public ParametrizedSequencePerformanceSuite<P,S> setSequence(
            final Iterator<S> iterator) {
        this.sequence = new Iterable<S>() {

            @Override
            public Iterator<S> iterator() {
                return iterator;
            }

        };
        return this;
    }

    public PerformanceTimer execute(final int loops,
            final ParametrizedSequenceRunnable<P,S> test) {
        setTest(test);
        for (final S sequenceItem: sequence) {
            for (ObjectMatrixInnerRunnable sir: runnables) {
                sir.setSequenceItem(sequenceItem);
            }
            final LoopPerformances loopPerformances =
                    performanceTimer.iterate(loops).getLoopPerformances();
            processConsumers(sequenceItem.toString(), loopPerformances);
        }
        return performanceTimer;
    }

    private void setTest(final ParametrizedSequenceRunnable<P,S> callable) {
        this.callable = callable;
    }

    public class ObjectMatrixInnerRunnable implements InitializableRunnable {
        private final P param;
        private S sequenceItem;

        private ObjectMatrixInnerRunnable(final P param) {
            this.param = param;
        }

        private void setSequenceItem(final S sequenceItem) {
            this.sequenceItem = sequenceItem;
        }

        @Override
        public void init() {
            callable.setUp(param, sequenceItem);
        }

        @Override
        public void run() {
            callable.call(param, sequenceItem);
        }
    }
}
