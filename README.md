# DAA Assignment 2 – Nurdaulet Abdulla, SE2516

Dynamic array, doubly linked list and binary min-heap implemented from scratch with primitive `int`
(no `java.util` collections, no boxing), with operation counters, a benchmark and JUnit 5 tests.

## Layout
    src/main/java/daa/structures   DynamicArray, MyLinkedList (both implement IntList), MinHeap
    src/main/java/daa/metrics      Metrics (steps / moves / comparisons), Counted
    src/main/java/daa/benchmark    BenchmarkRunner (W1-W4)
    src/test/java/daa/structures   JUnit 5 tests (33)
    results/results.csv            benchmark output (seed 42)
    results/plots/                 one figure per workload; results/make_plots.py redraws them
    REPORT.md                      complexity table, loop invariant proofs, plots, discussion
    run_benchmark.sh               ONE command: compile, run, write results.csv and plots

## Run
    ./run_benchmark.sh             # needs JDK 11+; plots need python3 + matplotlib
    mvn test                       # JUnit 5 tests (JDK 17+, Maven)

Without Maven, compile `src/main/java` and `src/test/java` with the JUnit 5 console launcher jar and run
`java -jar junit-platform-console-standalone.jar execute -cp <classes> --scan-classpath <test-classes>`.

## Counters (what is counted)
- steps: one array-cell read, or one move to the next/previous node
- moves: one element shifted/copied in an array (a heap swap = 2), or one link (pointer) update
- comparisons: one comparison of two elements

They are incremented inside the methods. The same seed gives identical counts on every run; times vary slightly.

## Git workflow
Branches: `main` (working code only, tag `v1.0`), `feature/array`, `feature/list`, `feature/heap`, `feature/metrics`.
Commit style: `feat(array): add dynamic resizing`, `test(heap): check heap property`, `docs(report): add plots`.
    git log --graph --oneline --all
