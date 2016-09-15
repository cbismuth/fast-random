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
import org.apache.commons.lang3.ArrayUtils;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public final class FastRandom {

    /**
     * Extracts random values from a source array the noob way:
     * remove unwanted elements from source array with a split & join.
     *
     * @param src the source array
     * @param p the length of the random array to return
     * @param <T> the type of elements from the source array
     *
     * @return an array with random elements from the source array without duplicates
     *
     * @throws IllegalAccessException if p == 0 or p > src.length
     */
    public static <T> T[] noobRandomOfLength(T[] src, final int p) throws IllegalAccessException {
        checkPreconditions(src, p);

        @SuppressWarnings("unchecked") final
        T[] dest = (T[]) new Object[p];
        int indexOfCurrentDestValueToSet = 0;

        // while destination array is not filled
        while (indexOfCurrentDestValueToSet != p && src.length > 0) {
            final int pickedIndex = convertRandomValueToIndex(src);

            final T pickedElement = src[pickedIndex];

            final boolean isDuplicate = isDuplicate_withoutHash(dest, indexOfCurrentDestValueToSet, pickedElement);

            // add to destination array if not a duplicate
            if (!isDuplicate) {
                dest[indexOfCurrentDestValueToSet] = pickedElement;
                indexOfCurrentDestValueToSet++;
            }

            // NOOB part - remove processed element from source element with a split & join
            final T[] left = Arrays.copyOfRange(src, 0, pickedIndex);
            final T[] right = Arrays.copyOfRange(src, pickedIndex + 1, src.length);
            src = ArrayUtils.addAll(left, right);
        }

        if (indexOfCurrentDestValueToSet != p) {
            throw new TooMuchDuplicatesException("Not enough distinct values in source array!");
        }

        return dest;
    }

    /**
     * Extracts random values from a source array the quick way:
     * in-place push picked elements to the end of the array
     * and duplicates are searched by iterating through destination array.
     *
     * @param src the source array
     * @param p the length of the random array to return
     * @param <T> the type of elements from the source array
     *
     * @return an array with random elements from the source array without duplicates
     *
     * @throws IllegalAccessException if p == 0 or p > src.length
     */
    public static <T> T[] quickRandomOfLength_withoutHash(final T[] src, final int p) throws IllegalAccessException {
        checkPreconditions(src, p);

        @SuppressWarnings("unchecked") final
        T[] dest = (T[]) new Object[p];
        int indexOfCurrentDestValueToSet = 0;

        int nbOfRandomCalls = 0;

        // while destination array is not filled
        while (indexOfCurrentDestValueToSet != p && nbOfRandomCalls < src.length) {
            final int pickedIndex = convertRandomValueToIndex(src);

            final T pickedElement = src[pickedIndex];

            final boolean isDuplicate = isDuplicate_withoutHash(dest, indexOfCurrentDestValueToSet, pickedElement);

            // add to destination array if not a duplicate
            if (!isDuplicate) {
                dest[indexOfCurrentDestValueToSet] = pickedElement;
                indexOfCurrentDestValueToSet++;
            }

            // SMART part - swap processed element if duplicate or not
            swapWithLastUnpickedElement(src, pickedIndex, nbOfRandomCalls);
            nbOfRandomCalls++;
        }

        if (indexOfCurrentDestValueToSet != p) {
            throw new TooMuchDuplicatesException("Not enough distinct values in source array!");
        }

        return dest;
    }

    /**
     * Extracts random values from a source array the quickest way:
     * in-place push picked elements to the end of the array
     * and duplicates are searched from their hash code.
     *
     * @param src the source array
     * @param p the length of the random array to return
     * @param <T> the type of elements from the source array
     *
     * @return an array with random elements from the source array without duplicates
     *
     * @throws IllegalAccessException if p == 0 or p > src.length
     */
    public static <T> T[] quickRandomOfLength_withHash(final T[] src, final int p) throws IllegalAccessException {
        checkPreconditions(src, p);

        @SuppressWarnings("unchecked") final
        T[] dest = (T[]) new Object[p];
        int indexOfCurrentDestValueToSet = 0;

        int nbOfRandomCalls = 0;

        final Set<Integer> hashCodes = new HashSet<>(p);

        // while destination array is not filled
        while (indexOfCurrentDestValueToSet != p && nbOfRandomCalls < src.length) {
            final int pickedIndex = convertRandomValueToIndex(src);

            final T pickedElement = src[pickedIndex];

            final int hashCode = pickedElement.hashCode();
            final boolean isDuplicate = hashCodes.contains(hashCode);

            // add to destination array if not a duplicate
            if (!isDuplicate) {
                dest[indexOfCurrentDestValueToSet] = pickedElement;
                hashCodes.add(hashCode);
                indexOfCurrentDestValueToSet++;
            }

            // SMART part - swap processed element if duplicate or not
            swapWithLastUnpickedElement(src, pickedIndex, nbOfRandomCalls);
            nbOfRandomCalls++;
        }

        if (indexOfCurrentDestValueToSet != p) {
            throw new TooMuchDuplicatesException("Not enough distinct values in source array!");
        }

        return dest;
    }

    /**
     * Swaps a randomly picked element with the
     * last unpicked element in a source array.
     *
     * @param src the source array
     * @param randomElementIndex the index of the randomly picked element to swap
     * @param offset the number of elements already moved to the end of the source array
     */
    private static <T> void swapWithLastUnpickedElement(final T[] src, final int randomElementIndex, final int offset) {
        final int indexOfLastUnpickedElement = src.length - 1 - offset;
        final T tmp = src[randomElementIndex];
        src[randomElementIndex] = src[indexOfLastUnpickedElement];
        src[indexOfLastUnpickedElement] = tmp;
    }

    private static <T> void checkPreconditions(final T[] src, final int p) throws IllegalAccessException {
        if (p == 0) {
            throw new IllegalArgumentException("Random array length cannot be equal to 0!");
        }

        if (p > src.length) {
            throw new IllegalArgumentException("Random array length cannot be greater than source array length!");
        }
    }

    private static <T> int convertRandomValueToIndex(final T[] src) {
        // x = axis of random values
        // y = axis of index values
        // known points are (0, 0) and (1, n)
        // y = ax + b = ax + 0 = ax
        // a = (y2 - y1) / (x2 - x1) = (n - 0) / (1 - 0) = n

        // down casting is a quick & fair way to floor value
        return (int) (src.length * Math.random());
    }

    private static <T> boolean isDuplicate_withoutHash(final T[] dest, final int indexOfCurrentDestValueToSet, final T pickedElement) {
        boolean isDuplicate = false;
        for (int j = 0; j < indexOfCurrentDestValueToSet; j++) {
            if (dest[j].equals(pickedElement)) {
                isDuplicate = true;
                break;
            }
        }
        return isDuplicate;
    }

    private FastRandom() {
        // UTILITY CLASS
    }

}
