package daa.structures;

import daa.metrics.Metrics;

public class DynamicArray implements IntList {
    private int[] data;
    private int size;
    private final Metrics metrics = new Metrics();

    public DynamicArray() { data = new int[10]; }

    @Override public Metrics metrics() { return metrics; }

    @Override public int size() { return size; }

    @Override
    public void add(int x) {
        if (size == data.length) grow();
        data[size++] = x;
    }

    @Override
    public void add(int index, int x) {
        if (index < 0 || index > size)
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        if (size == data.length) grow();
        for (int i = size; i > index; i--) {
            data[i] = data[i - 1];
            metrics.moves++;
        }
        data[index] = x;
        size++;
    }

    @Override
    public int remove(int index) {
        checkIndex(index);
        int removed = data[index];
        metrics.steps++;
        for (int i = index; i < size - 1; i++) {
            data[i] = data[i + 1];
            metrics.moves++;
        }
        size--;
        return removed;
    }

    @Override
    public int get(int index) {
        checkIndex(index);
        metrics.steps++;
        return data[index];
    }

    @Override
    public boolean contains(int x) {
        for (int i = 0; i < size; i++) {
            metrics.steps++;
            metrics.comparisons++;
            if (data[i] == x) return true;
        }
        return false;
    }

    private void checkIndex(int index) {
        if (index < 0 || index >= size)
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
    }

    private void grow() {
        int[] bigger = new int[data.length * 2];
        for (int i = 0; i < size; i++) {
            bigger[i] = data[i];
            metrics.moves++;
        }
        data = bigger;
    }
}
