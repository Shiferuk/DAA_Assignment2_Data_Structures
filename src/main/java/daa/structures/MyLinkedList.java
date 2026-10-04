package daa.structures;

import daa.metrics.Metrics;

public class MyLinkedList implements IntList {
    private static class Node {
        int val;
        Node prev, next;
        Node(int val) { this.val = val; }
    }

    private Node head, tail;
    private int size;
    private final Metrics metrics = new Metrics();

    @Override public Metrics metrics() { return metrics; }

    @Override public int size() { return size; }

    @Override
    public void add(int x) {
        Node n = new Node(x);
        if (tail == null) {
            head = tail = n;
            metrics.moves += 2;                    // head, tail
        } else {
            tail.next = n;
            n.prev = tail;
            tail = n;
            metrics.moves += 3;                    // tail.next, n.prev, tail
        }
        size++;
    }

    @Override
    public void add(int index, int x) {
        if (index < 0 || index > size)
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        if (index == size) { add(x); return; }
        Node at = nodeAt(index);
        Node n = new Node(x);
        n.next = at;
        n.prev = at.prev;
        if (at.prev == null) head = n; else at.prev.next = n;
        at.prev = n;
        metrics.moves += 4;
        size++;
    }

    @Override
    public int remove(int index) {
        checkIndex(index);
        Node n = nodeAt(index);
        if (n.prev == null) head = n.next; else n.prev.next = n.next;
        if (n.next == null) tail = n.prev; else n.next.prev = n.prev;
        metrics.moves += 2;                        // (head | prev.next), (tail | next.prev)
        size--;
        return n.val;
    }

    @Override
    public int get(int index) {
        checkIndex(index);
        return nodeAt(index).val;
    }

    @Override
    public boolean contains(int x) {
        for (Node cur = head; cur != null; cur = cur.next) {
            metrics.comparisons++;                 // compare cur.val with x
            if (cur.val == x) return true;
            metrics.steps++;                       // move to the next node
        }
        return false;
    }

    private void checkIndex(int index) {
        if (index < 0 || index >= size)
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
    }

    private Node nodeAt(int index) {
        Node cur;
        if (index < size / 2) {
            cur = head;
            for (int i = 0; i < index; i++) {
                cur = cur.next;
                metrics.steps++;                   // one move to the next node
            }
        } else {
            cur = tail;
            for (int i = size - 1; i > index; i--) {
                cur = cur.prev;
                metrics.steps++;                   // one move to the previous node
            }
        }
        return cur;
    }
}
