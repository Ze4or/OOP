package ru.nsu.batyaev.task_1_1_1;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

class HeapSortTest {

    @Test
    void testSort() {
        int[] arr = {5, 4, 3, 2, 1};

        HeapSort.heapsort(arr);

        assertArrayEquals(
                new int[]{1, 2, 3, 4, 5},
                arr
        );
    }

    @Test
    void testEmpty() {
        int[] arr = {};

        HeapSort.heapsort(arr);

        assertArrayEquals(new int[]{}, arr);
    }

    @Test
    void testDuplicates() {
        int[] arr = {3, 1, 3, 2, 1};

        HeapSort.heapsort(arr);

        assertArrayEquals(
                new int[]{1, 1, 2, 3, 3},
                arr
        );
    }

    @Test
    void testNegativeNumbers() {
        int[] arr = {-2, 5, -1, 0};

        HeapSort.heapsort(arr);

        assertArrayEquals(
                new int[]{-2, -1, 0, 5},
                arr
        );
    }

    @Test
    void testAlreadySortedArray() {
        int[] arr = {1, 2, 3, 4, 5, 6};

        HeapSort.heapsort(arr);

        assertArrayEquals(
                new int[]{1, 2, 3, 4, 5, 6},
                arr
        );
    }
}