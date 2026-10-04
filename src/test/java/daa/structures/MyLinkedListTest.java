package daa.structures;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Random;
import org.junit.jupiter.api.Test;

class MyLinkedListTest {

    @Test
    void randomOperationsMatchArrayList() {
        for (long seed = 1; seed <= 20; seed++) {
            Random rnd = new Random(seed);
            MyLinkedList mine = new MyLinkedList();
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
    void emptyList() {
        MyLinkedList l = new MyLinkedList();
        assertEquals(0, l.size());
        assertFalse(l.contains(0));
        assertThrows(IndexOutOfBoundsException.class, () -> l.get(0));
        assertThrows(IndexOutOfBoundsException.class, () -> l.remove(0));
        assertThrows(IndexOutOfBoundsException.class, () -> l.add(1, 5));
        l.add(0, 5);
        assertEquals(5, l.get(0));
    }

    @Test
    void singleElement() {
        MyLinkedList l = new MyLinkedList();
        l.add(7);
        assertEquals(1, l.size());
        assertEquals(7, l.get(0));
        assertTrue(l.contains(7));
        assertFalse(l.contains(8));
        assertEquals(7, l.remove(0));
        assertEquals(0, l.size());
        assertFalse(l.contains(7));
        l.add(3);
        assertEquals(3, l.get(0));
    }

    @Test
    void duplicateValues() {
        MyLinkedList l = new MyLinkedList();
        for (int i = 0; i < 5; i++) l.add(4);
        l.add(2, 9);
        l.add(9);
        assertEquals(4, l.remove(0));
        assertEquals(6, l.size());
        assertTrue(l.contains(4));
        for (int i = 0; i < 5; i++) l.remove(0);
        assertEquals(1, l.size());
        assertFalse(l.contains(4));
        assertTrue(l.contains(9));
    }

    @Test
    void firstAndLastIndex() {
        MyLinkedList l = new MyLinkedList();
        for (int i = 1; i <= 5; i++) l.add(i);
        l.add(0, 0);
        l.add(l.size(), 6);
        assertEquals(0, l.get(0));
        assertEquals(6, l.get(l.size() - 1));
        assertEquals(0, l.remove(0));
        assertEquals(6, l.remove(l.size() - 1));
        assertEquals(5, l.size());
        assertEquals(1, l.get(0));
        assertEquals(5, l.get(4));
        l.add(5);
        assertEquals(5, l.get(l.size() - 1));
    }

    @Test
    void invalidIndexThrows() {
        MyLinkedList l = new MyLinkedList();
        l.add(1); l.add(2); l.add(3);
        assertThrows(IndexOutOfBoundsException.class, () -> l.get(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> l.get(3));
        assertThrows(IndexOutOfBoundsException.class, () -> l.remove(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> l.remove(3));
        assertThrows(IndexOutOfBoundsException.class, () -> l.add(-1, 0));
        assertThrows(IndexOutOfBoundsException.class, () -> l.add(4, 0));
        assertEquals(3, l.size());
        assertEquals(1, l.get(0));
        assertEquals(3, l.get(2));
    }

    @Test
    void countersAreCountedInsideMethods() {
        MyLinkedList l = new MyLinkedList();
        for (int i = 0; i < 9; i++) l.add(i);
        l.metrics().reset();
        l.get(0);
        assertEquals(0, l.metrics().steps);
        l.get(2);
        assertEquals(2, l.metrics().steps);
        l.metrics().reset();
        l.get(8);
        assertEquals(0, l.metrics().steps);
        l.metrics().reset();
        l.add(0, 99);
        assertEquals(4, l.metrics().moves);
        assertEquals(0, l.metrics().steps);
    }
}
