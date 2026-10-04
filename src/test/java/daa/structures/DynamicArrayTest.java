package daa.structures;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Random;
import org.junit.jupiter.api.Test;

class DynamicArrayTest {

    @Test
    void randomOperationsMatchArrayList() {
        for (long seed = 1; seed <= 20; seed++) {
            Random rnd = new Random(seed);
            DynamicArray mine = new DynamicArray();
            ArrayList<Integer> ref = new ArrayList<>();
            for (int step = 0; step < 3000; step++) {
                int op = rnd.nextInt(5);
                if (op == 0) {
                    int v = rnd.nextInt(50);
                    mine.add(v); ref.add(v);
                } else if (op == 1) {
                    int idx = rnd.nextInt(ref.size() + 1), v = rnd.nextInt(50);
                    mine.add(idx, v); ref.add(idx, v);
                } else if (op == 2 && !ref.isEmpty()) {
                    int idx = rnd.nextInt(ref.size());
                    assertEquals((int) ref.remove(idx), mine.remove(idx));
                } else if (op == 3 && !ref.isEmpty()) {
                    int idx = rnd.nextInt(ref.size());
                    assertEquals((int) ref.get(idx), mine.get(idx));
                } else {
                    int v = rnd.nextInt(60);
                    assertEquals(ref.contains(v), mine.contains(v));
                }
                assertEquals(ref.size(), mine.size());
            }
            for (int i = 0; i < ref.size(); i++) assertEquals((int) ref.get(i), mine.get(i));
        }
    }

    @Test
    void growsPastInitialCapacityKeepingOrder() {
        DynamicArray a = new DynamicArray();
        for (int i = 0; i < 10_000; i++) a.add(i * 3);
        assertEquals(10_000, a.size());
        for (int i = 0; i < 10_000; i++) assertEquals(i * 3, a.get(i));
    }

    @Test
    void emptyArray() {
        DynamicArray a = new DynamicArray();
        assertEquals(0, a.size());
        assertFalse(a.contains(0));
        assertThrows(IndexOutOfBoundsException.class, () -> a.get(0));
        assertThrows(IndexOutOfBoundsException.class, () -> a.remove(0));
        assertThrows(IndexOutOfBoundsException.class, () -> a.add(1, 5));
        a.add(0, 5);
        assertEquals(5, a.get(0));
    }

    @Test
    void singleElement() {
        DynamicArray a = new DynamicArray();
        a.add(7);
        assertEquals(1, a.size());
        assertEquals(7, a.get(0));
        assertTrue(a.contains(7));
        assertFalse(a.contains(8));
        assertEquals(7, a.remove(0));
        assertEquals(0, a.size());
        assertFalse(a.contains(7));
    }

    @Test
    void duplicateValues() {
        DynamicArray a = new DynamicArray();
        for (int i = 0; i < 5; i++) a.add(4);
        a.add(2, 9);
        a.add(9);
        assertTrue(a.contains(4));
        assertEquals(4, a.remove(0));
        assertEquals(6, a.size());
        assertTrue(a.contains(4));
        for (int i = 0; i < 5; i++) a.remove(0);
        assertEquals(1, a.size());
        assertFalse(a.contains(4));
        assertTrue(a.contains(9));
    }

    @Test
    void firstAndLastIndex() {
        DynamicArray a = new DynamicArray();
        for (int i = 1; i <= 5; i++) a.add(i);
        a.add(0, 0);
        a.add(a.size(), 6);
        assertEquals(0, a.get(0));
        assertEquals(6, a.get(a.size() - 1));
        assertEquals(0, a.remove(0));
        assertEquals(6, a.remove(a.size() - 1));
        assertEquals(5, a.size());
        assertEquals(1, a.get(0));
        assertEquals(5, a.get(4));
    }

    @Test
    void invalidIndexThrows() {
        DynamicArray a = new DynamicArray();
        a.add(1); a.add(2); a.add(3);
        assertThrows(IndexOutOfBoundsException.class, () -> a.get(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> a.get(3));
        assertThrows(IndexOutOfBoundsException.class, () -> a.remove(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> a.remove(3));
        assertThrows(IndexOutOfBoundsException.class, () -> a.add(-1, 0));
        assertThrows(IndexOutOfBoundsException.class, () -> a.add(4, 0));
        assertEquals(3, a.size());
        assertEquals(1, a.get(0));
        assertEquals(3, a.get(2));
    }

    @Test
    void countersAreCountedInsideMethods() {
        DynamicArray a = new DynamicArray();
        for (int i = 0; i < 8; i++) a.add(i);
        a.metrics().reset();
        a.get(3);
        assertEquals(1, a.metrics().steps);
        a.metrics().reset();
        a.contains(100);
        assertEquals(8, a.metrics().steps);
        assertEquals(8, a.metrics().comparisons);
        a.metrics().reset();
        a.add(0, 99);
        assertEquals(8, a.metrics().moves);
    }
}
