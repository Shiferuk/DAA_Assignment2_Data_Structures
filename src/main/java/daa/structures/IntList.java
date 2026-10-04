package daa.structures;

import daa.metrics.Counted;

public interface IntList extends Counted {
    int size();

    void add(int x);

    void add(int index, int x);

    int remove(int index);

    int get(int index);

    boolean contains(int x);
}
