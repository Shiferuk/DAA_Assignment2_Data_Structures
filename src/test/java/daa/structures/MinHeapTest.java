package daa.structures;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.PriorityQueue;
import java.util.Random;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class MinHeapTest {

    @Test
    void randomOperationsMatchPriorityQueue() {
        for (long seed = 1; seed <= 20; seed++) {
            Random rnd = new Random(seed);
            MinHeap mine = new MinHeap();
            PriorityQueue<Integer> ref = new PriorityQueue<>();
            for (int step = 0; step < 3000; step++) {
                if (rnd.nextInt(3) < 2 || ref.isEmpty()) {
                    int v = rnd.nextInt(200) - 100;
                    mine.insert(v); ref.add(v);
                } else {
                    assertEquals((int) ref.peek(), mine.peekMin());
                    assertEquals((int) ref.poll(), mine.extractMin());
                }
                assertEquals(ref.size(), mine.size());
                if (!ref.isEmpty()) assertEquals((int) ref.peek(), mine.peekMin());
            }
        }
    }

    @Test
    void emptyHeapThrowsIllegalState() {
        MinHeap h = new MinHeap();
        assertTrue(h.isEmpty());
        assertEquals(0, h.size());
        assertThrows(IllegalStateException.class, h::peekMin);
        assertThrows(IllegalStateException.class, h::extractMin);
    }

    @Test
    void singleElement() {
        MinHeap h = new MinHeap();
        h.insert(42);
        assertEquals(1, h.size());
        assertEquals(42, h.peekMin());
        assertEquals(42, h.extractMin());
        assertTrue(h.isEmpty());
        assertThrows(IllegalStateException.class, h::extractMin);
        h.insert(-1);
        assertEquals(-1, h.peekMin());
    }

    @Test
    void duplicateValues() {
        MinHeap h = new MinHeap();
        for (int i = 0; i < 20; i++) h.insert(5);
        h.insert(1); h.insert(1); h.insert(9);
        assertEquals(1, h.extractMin());
        assertEquals(1, h.extractMin());
        for (int i = 0; i < 20; i++) assertEquals(5, h.extractMin());
        assertEquals(9, h.extractMin());
        assertTrue(h.isEmpty());
    }

    @Test
    void extremeValues() {
        MinHeap h = new MinHeap();
        h.insert(Integer.MAX_VALUE); h.insert(Integer.MIN_VALUE); h.insert(0);
        assertEquals(Integer.MIN_VALUE, h.extractMin());
        assertEquals(0, h.extractMin());
        assertEquals(Integer.MAX_VALUE, h.extractMin());
    }

    @Test
    void peekDoesNotRemove() {
        MinHeap h = new MinHeap();
        h.insert(3); h.insert(1); h.insert(2);
        assertEquals(1, h.peekMin());
        assertEquals(1, h.peekMin());
        assertEquals(3, h.size());
    }

    @Test
    void heapPropertyHoldsAfterEveryOperation() {
        Random rnd = new Random(7);
        MinHeap h = new MinHeap();
        assertTrue(h.isValidHeap());
        for (int i = 0; i < 2000; i++) {
            h.insert(rnd.nextInt(300) - 150);
            assertTrue(h.isValidHeap(), "violated after insert #" + i);
        }
        while (!h.isEmpty()) {
            h.extractMin();
            assertTrue(h.isValidHeap(), "violated after extractMin, size=" + h.size());
        }
    }

    @Test
    void heapPropertyHoldsWithMixedOperations() {
        Random rnd = new Random(11);
        MinHeap h = new MinHeap();
        for (int i = 0; i < 5000; i++) {
            if (h.isEmpty() || rnd.nextBoolean()) h.insert(rnd.nextInt(100));
            else h.extractMin();
            assertTrue(h.isValidHeap(), "violated at step " + i);
        }
    }

    @Test
    void heapPropertyHoldsOnSortedInputs() {
        MinHeap asc = new MinHeap(), desc = new MinHeap();
        for (int i = 0; i < 500; i++) {
            asc.insert(i);
            desc.insert(500 - i);
            assertTrue(asc.isValidHeap());
            assertTrue(desc.isValidHeap());
        }
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3, 10, 11, 100, 1000, 10_000})
    void extractMinReturnsSortedOutput(int n) {
        Random rnd = new Random(42);
        int[] values = new int[n];
        MinHeap h = new MinHeap();
        for (int i = 0; i < n; i++) {
            values[i] = rnd.nextInt(1000) - 500;
            h.insert(values[i]);
        }
        int[] expected = values.clone();
        Arrays.sort(expected);
        int prev = Integer.MIN_VALUE;
        for (int i = 0; i < n; i++) {
            int m = h.extractMin();
            assertTrue(m >= prev, "output decreased at position " + i);
            assertEquals(expected[i], m);
            prev = m;
        }
        assertTrue(h.isEmpty());
    }

    @Test
    void peekMinCostsOneStep() {
        MinHeap h = new MinHeap();
        for (int i = 0; i < 100; i++) h.insert(100 - i);
        h.metrics().reset();
        h.peekMin();
        assertEquals(1, h.metrics().steps);
        assertEquals(0, h.metrics().comparisons);
        assertEquals(0, h.metrics().moves);
    }
}
