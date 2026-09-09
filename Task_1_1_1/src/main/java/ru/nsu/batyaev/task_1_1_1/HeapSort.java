package ru.nsu.batyaev.task_1_1_1;

/**
 * Реализация пирамидальной сортировки.
 */
public class HeapSort {

    /**
     * Сортирует массив методом пирамидальной сортировки.
     *
     * @param arr массив, который нужно отсортировать
     */
    public static void heapsort(int[] arr) {
        int n = arr.length;

        // Строим кучу
        for (int i = n / 2 - 1; i >= 0; i--) {
            heapify(arr, n, i);
        }

        // Сортируем
        for (int i = n - 1; i > 0; i--) {
            int temp = arr[0];
            arr[0] = arr[i];
            arr[i] = temp;

            heapify(arr, i, 0);
        }
    }

    /**
     * Восстанавливает свойство кучи для элемента.
     *
     * @param arr массив
     * @param n размер кучи
     * @param i индекс текущего элемента
     */
    private static void heapify(int[] arr, int n, int i) {
        int largest = i;
        int left = 2 * i + 1;
        int right = 2 * i + 2;

        if (left < n && arr[left] > arr[largest]) {
            largest = left;
        }

        if (right < n && arr[right] > arr[largest]) {
            largest = right;
        }

        if (largest != i) {
            int temp = arr[i];
            arr[i] = arr[largest];
            arr[largest] = temp;

            heapify(arr, n, largest);
        }
    }
}