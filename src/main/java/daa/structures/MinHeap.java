package daa.structures;

import daa.metrics.Counted;
import daa.metrics.Metrics;

public class MinHeap implements Counted {
    private int[] heap;
    private int size;
    private final Metrics metrics = new Metrics();

    public MinHeap() { heap = new int[10]; }

    @Override public Metrics metrics() { return metrics; }

    public int size() { return size; }
    public boolean isEmpty() { return size == 0; }

    public void insert(int x) {
        if (size == heap.length) {
            int[] bigger = new int[heap.length * 2];
            for (int i = 0; i < size; i++) {
                bigger[i] = heap[i];
                metrics.moves++;                   // one element copied to the new array
            }
            heap = bigger;
        }
        heap[size] = x;
        int i = size++;
        while (i > 0) {
            int p = (i - 1) / 2;
            metrics.comparisons++;                 // compare heap[p] with heap[i]
            metrics.steps += 2;
            if (heap[p] <= heap[i]) break;
            swap(i, p);
            i = p;
        }
    }

    public int peekMin() {
        if (size == 0) throw new IllegalStateException("Heap is empty");
        metrics.steps++;
        return heap[0];
    }

    public int extractMin() {
        if (size == 0) throw new IllegalStateException("Heap is empty");
        int min = heap[0];
        metrics.steps += 2;
        heap[0] = heap[--size];
        bubbleDown();
        return min;
    }

    private void bubbleDown() {
        int i = 0;
        while (true) {
            int l = 2 * i + 1, r = l + 1, s = i;
            if (l < size) {
                metrics.comparisons++;
                metrics.steps += 2;
                if (heap[l] < heap[s]) s = l;
            }
            if (r < size) {
                metrics.comparisons++;
                metrics.steps += 2;
                if (heap[r] < heap[s]) s = r;
            }
            if (s == i) break;
            swap(i, s);
            i = s;
        }
    }

    boolean isValidHeap() {
        for (int i = 1; i < size; i++)
            if (heap[(i - 1) / 2] > heap[i]) return false;
        return true;
    }

    private void swap(int a, int b) {
        int t = heap[a]; heap[a] = heap[b]; heap[b] = t;
        metrics.moves += 2;                        // two cells written
    }
}
