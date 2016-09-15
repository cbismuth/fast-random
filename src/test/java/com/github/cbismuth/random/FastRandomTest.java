/*
 * The MIT License (MIT)
 * Copyright (c) 2016 Christophe Bismuth
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.github.cbismuth.random;

import com.github.cbismuth.random.exception.TooMuchDuplicatesException;
import com.google.common.base.Stopwatch;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static java.lang.System.currentTimeMillis;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertTrue;
import static org.slf4j.LoggerFactory.getLogger;

public class FastRandomTest {

    private static final Logger LOGGER = getLogger(FastRandomTest.class);

    private static final int RANDOM_ARRAY_LENGTH = 1000;
    private static final int SOURCE_ARRAY_LENGTH = 1000000;
    private static final int SOURCE_MAX_VALUE = Integer.MAX_VALUE;

    private Object[] ids;

    @Before
    public void setUp() {
        final Random random = new Random(currentTimeMillis());

        ids = new Object[SOURCE_ARRAY_LENGTH];
        for (int i = 0; i < SOURCE_ARRAY_LENGTH; i++) {
            ids[i] = random.nextInt(SOURCE_MAX_VALUE);
        }
    }

    @Test
    public void testRandomOfLength_noob() throws Exception {
        final Stopwatch stopwatch = Stopwatch.createStarted();
        final Object[] randomized = FastRandom.noobRandomOfLength(ids, RANDOM_ARRAY_LENGTH);
        stopwatch.stop();

        LOGGER.info(String.format("testRandomOfLength_noob executed in %d ms.", stopwatch.elapsed(TimeUnit.MILLISECONDS)));

        checkExpectedLength(randomized, RANDOM_ARRAY_LENGTH);
        checkNoDuplicates(randomized);
        checkBounds(randomized);
    }

    @Test
    public void testRandomOfLength_quick() throws Exception {
        final Stopwatch stopwatch = Stopwatch.createStarted();
        final Object[] randomized = FastRandom.quickRandomOfLength_withoutHash(ids, RANDOM_ARRAY_LENGTH);
        stopwatch.stop();

        LOGGER.info(String.format("testRandomOfLength_quick executed in %d ms.", stopwatch.elapsed(TimeUnit.MILLISECONDS)));

        checkExpectedLength(randomized, RANDOM_ARRAY_LENGTH);
        checkNoDuplicates(randomized);
        checkBounds(randomized);
    }

    @Test
    public void testRandomOfLength_quickestWithHash() throws Exception {
        final Stopwatch stopwatch = Stopwatch.createStarted();
        final Object[] randomized = FastRandom.quickRandomOfLength_withHash(ids, RANDOM_ARRAY_LENGTH);
        stopwatch.stop();

        LOGGER.info(String.format("testRandomOfLength_quick executed in %d ms.", stopwatch.elapsed(TimeUnit.MILLISECONDS)));

        checkExpectedLength(randomized, RANDOM_ARRAY_LENGTH);
        checkNoDuplicates(randomized);
        checkBounds(randomized);
    }

    @Test(expected = TooMuchDuplicatesException.class)
    public void testRandomOfLength_withTooMuchDuplicates() throws Exception {
        // limits
        final int randomArrayLength = 31;
        final int sourceArrayLength = 32;
        final int sourceMaxValue = 3;

        final Random _random = new Random(currentTimeMillis());

        final Object[] _ids = new Object[sourceArrayLength];
        for (int i = 0; i < sourceArrayLength; i++) {
            _ids[i] = _random.nextInt(sourceMaxValue);
        }

        FastRandom.quickRandomOfLength_withoutHash(_ids, randomArrayLength);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRandomOfLength_withZeroRandomArrayLength() throws Exception {
        // limits
        final int randomArrayLength = 0;
        final int sourceArrayLength = 32;
        final int sourceMaxValue = 3;

        final Random _random = new Random(currentTimeMillis());

        final Object[] _ids = new Object[sourceArrayLength];
        for (int i = 0; i < sourceArrayLength; i++) {
            _ids[i] = _random.nextInt(sourceMaxValue);
        }

        FastRandom.quickRandomOfLength_withoutHash(_ids, randomArrayLength);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRandomOfLength_withTooLargeRandomArrayLength() throws Exception {
        // limits
        final int randomArrayLength = 33;
        final int sourceArrayLength = 32;
        final int sourceMaxValue = 3;

        final Random _random = new Random(currentTimeMillis());

        final Object[] _ids = new Object[sourceArrayLength];
        for (int i = 0; i < sourceArrayLength; i++) {
            _ids[i] = _random.nextInt(sourceMaxValue);
        }

        FastRandom.quickRandomOfLength_withoutHash(_ids, randomArrayLength);
    }

    private void checkExpectedLength(final Object[] randomized, final int expectedLength) {
        assertTrue(randomized.length == expectedLength);
    }

    private final Set<Object> buffer = new HashSet<>(RANDOM_ARRAY_LENGTH);

    private void checkNoDuplicates(final Object[] randomized) {
        buffer.clear();
        buffer.addAll(asList(randomized));

        assertTrue(buffer.size() == randomized.length);
    }

    private void checkBounds(final Object[] randomized) {
        for (final Object random : randomized) {
            assertTrue((Integer) random >= 0);
            assertTrue((Integer) random < SOURCE_MAX_VALUE);
        }
    }

}
