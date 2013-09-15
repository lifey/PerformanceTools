package com.fillumina.performance.examples.junit;

import com.fillumina.performance.producer.suite.ParametrizedRunnable;
import com.fillumina.performance.producer.suite.ParametrizedPerformanceSuite;
import com.fillumina.performance.consumer.assertion.AssertPerformanceForExecutionSuite;
import com.fillumina.performance.util.junit.JUnitSuitePerformanceTemplate;
import com.fillumina.performance.templates.ProgressionConfigurator;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class MapSingleThreadedPerformanceTest
        extends JUnitSuitePerformanceTemplate<Map<Integer, String>> {

    private static final int MAX_CAPACITY = 128;

    private int maxCapacity;

    public static void main(final String[] args) {
        new MapSingleThreadedPerformanceTest().testWithOutput();
    }

    @Override
    public void init(final ProgressionConfigurator builder) {
        this.maxCapacity = MAX_CAPACITY;
        builder.setBaseIterations(1_000)
                .setMaxStandardDeviation(2)
                .setTimeoutSeconds(100);
    }

    @Override
    public void addObjects(
            final ParametrizedPerformanceSuite<Map<Integer, String>> suite) {

        suite.addParameter("HashMap",
                new HashMap<Integer, String>(maxCapacity));

        suite.addParameter("TreeMap",
                new TreeMap<Integer, String>());

        suite.addParameter("LinkedHashMap",
                new LinkedHashMap<Integer, String>(maxCapacity));

        suite.addParameter("WeakHashMap",
                new WeakHashMap<Integer, String>(maxCapacity));

        suite.addParameter("SynchronizedLinkedHashMap",
                Collections.synchronizedMap(
                    new LinkedHashMap<Integer, String>(maxCapacity)));

        suite.addParameter("ConcurrentHashMap",
                new ConcurrentHashMap<Integer, String>(maxCapacity));

        suite.addParameter("SynchronizedHashMap",
                Collections.synchronizedMap(
                    new HashMap<Integer, String>(maxCapacity)));
    }

    @Override
    public void executeTests(
            final ParametrizedPerformanceSuite<Map<Integer, String>> suite) {
        // adds a probability to read an element which is not there
        final int maxCapacityPlusOne = maxCapacity + 1;

        suite.executeTest("SEQUENTIAL READ", new FilledMapTest(maxCapacity) {

            @Override
            public void call(Map<Integer, String> map, int i) {
                map.put(i % maxCapacityPlusOne, "xyz");
            }
        });

        suite.executeTest("SEQUENTIAL WRITE", new MapTest(maxCapacity) {

            @Override
            public void call(Map<Integer, String> map, int i) {
                map.put(i % maxCapacityPlusOne, "xyz");
            }
        });

        suite.executeTest("RANDOM READ", new FilledMapTest(maxCapacity) {
            final Random rnd = new Random();

            @Override
            public void call(Map<Integer, String> map, int i) {
                map.get(rnd.nextInt(maxCapacityPlusOne));
            }
        });

        suite.executeTest("RANDOM WRITE",
                new ParametrizedRunnable<Map<Integer, String>>() {
            final Random rnd = new Random();

            @Override
            public void call(Map<Integer, String> map) {
                map.put(rnd.nextInt(maxCapacityPlusOne), "xyz");
            }
        });
    }

    @Override
    public void addAssertions(final AssertPerformanceForExecutionSuite ap) {

        ap.forExecution("SEQUENTIAL READ")
                .assertTest("TreeMap").slowerThan("HashMap");

        ap.forExecution("SEQUENTIAL WRITE")
                .assertTest("TreeMap").slowerThan("HashMap");

        ap.forExecution("RANDOM READ")
                .assertTest("TreeMap").slowerThan("HashMap");

        ap.forExecution("RANDOM WRITE")
                .assertTest("TreeMap").slowerThan("HashMap");
    }

    private static abstract class MapTest
            extends ParametrizedRunnable<Map<Integer, String>> {
        final int maxCapacity;
        int i=0;

        public MapTest(final int maxCapacity) {
            this.maxCapacity = maxCapacity;
        }

        @Override
        public void call(final Map<Integer, String> param) {
            call(param, i++ % maxCapacity);
        }

        public abstract void call(final Map<Integer, String> param, final int i);
    }

    private static abstract class FilledMapTest extends MapTest {

        public FilledMapTest(int maxCapacity) {
            super(maxCapacity);
        }

        @Override
        public void setUp(final Map<Integer, String> map) {
            fillUpMap(map, maxCapacity);
        }
    }

    private static void fillUpMap(final Map<Integer, String> map,
            final int maxCapacity) {
        map.clear();
        for (int i=0; i<maxCapacity; i++) {
            map.put(i, "xyz");
        }
    }
}
