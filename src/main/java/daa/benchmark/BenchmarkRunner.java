package daa.benchmark;

import daa.metrics.Metrics;
import daa.structures.DynamicArray;
import daa.structures.IntList;
import daa.structures.MinHeap;
import daa.structures.MyLinkedList;

import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.function.Supplier;

public class BenchmarkRunner {
    static final long SEED = 42;
    static final int[] SIZES = {100, 1000, 10_000, 100_000};
    static final int WARMUP = 3, RUNS = 5;
    static final int GETS = 10_000, QUERIES = 1_000, UPDATES = 1_000;

    static final List<String> NAMES = List.of("DynamicArray", "MyLinkedList");
    static final List<Supplier<IntList>> FACTORIES = List.of(DynamicArray::new, MyLinkedList::new);

    static long sink;
    static boolean warmingUp;

    interface Once { long[] go(); }

    static long[] measure(Once o) {
        if (warmingUp) { o.go(); return null; }
        for (int i = 0; i < WARMUP; i++) o.go();
        long[] t = new long[RUNS];
        long[] last = null;
        for (int i = 0; i < RUNS; i++) {
            System.gc();
            last = o.go();
            t[i] = last[0];
        }
        Arrays.sort(t);
        return new long[]{t[RUNS / 2], last[1], last[2], last[3]};
    }

    static void row(PrintWriter out, String w, String v, String s, int n, long[] r) {
        if (r == null) return;
        String line = String.format(Locale.ROOT, "%s,%s,%s,%d,%.6f,%d,%d,%d",
                w, v, s, n, r[0] / 1e6, r[1], r[2], r[3]);
        out.println(line);
        System.out.println(line);
    }

    static int[] data(Random rnd, int n) {
        int[] d = new int[n];
        for (int i = 0; i < n; i++) d[i] = rnd.nextInt(1_000_000_000); // always >= 0
        return d;
    }

    static IntList filled(Supplier<IntList> f, int[] d) {
        IntList l = f.get();
        for (int x : d) l.add(x);
        l.metrics().reset();
        return l;
    }

    static long[] result(long nanos, Metrics m) {
        return new long[]{nanos, m.steps, m.moves, m.comparisons};
    }

    public static void main(String[] args) throws Exception {
        Path path = Path.of(args.length > 0 ? args[0] : "results/results.csv");
        if (path.getParent() != null) Files.createDirectories(path.getParent());

        warmingUp = true;
        for (int n : new int[]{100, 1000, 10_000}) runAll(null, n);
        warmingUp = false;

        try (PrintWriter out = new PrintWriter(path.toFile())) {
            out.println("workload,variant,structure,n,time_ms,steps,moves,comparisons");
            for (int n : SIZES) runAll(out, n);
        }
        System.out.println("Wrote " + path);
    }

    static void runAll(PrintWriter out, int n) {
        w1(out, n);
        w2(out, n);
        w3(out, n, "head");
        w3(out, n, "middle");
        w4(out, n);
    }

    static void w1(PrintWriter out, int n) {
        Random rnd = new Random(SEED);
        int[] d = data(rnd, n);
        int[] idx = new int[GETS];
        for (int i = 0; i < GETS; i++) idx[i] = rnd.nextInt(n);
        for (int k = 0; k < FACTORIES.size(); k++) {
            Supplier<IntList> f = FACTORIES.get(k);
            row(out, "W1", "-", NAMES.get(k), n, measure(() -> {
                IntList l = filled(f, d);
                long t0 = System.nanoTime();
                long s = 0;
                for (int i : idx) s += l.get(i);
                long t = System.nanoTime() - t0;
                sink += s;
                return result(t, l.metrics());
            }));
        }
    }

    static void w2(PrintWriter out, int n) {
        Random rnd = new Random(SEED);
        int[] d = data(rnd, n);
        int[] q = new int[QUERIES];
        for (int i = 0; i < QUERIES; i++)
            q[i] = (i % 2 == 0) ? d[rnd.nextInt(n)]
                                : -1 - rnd.nextInt(1_000_000_000);
        for (int k = 0; k < FACTORIES.size(); k++) {
            Supplier<IntList> f = FACTORIES.get(k);
            row(out, "W2", "-", NAMES.get(k), n, measure(() -> {
                IntList l = filled(f, d);
                long t0 = System.nanoTime();
                int found = 0;
                for (int x : q) if (l.contains(x)) found++;
                long t = System.nanoTime() - t0;
                sink += found;
                return result(t, l.metrics());
            }));
        }
    }

    static void w3(PrintWriter out, int n, String variant) {
        Random rnd = new Random(SEED);
        int[] d = data(rnd, n);
        int[] ins = new int[UPDATES];
        for (int i = 0; i < UPDATES; i++) ins[i] = rnd.nextInt(1_000_000_000);
        final int pos = variant.equals("head") ? 0 : n / 2;
        for (int k = 0; k < FACTORIES.size(); k++) {
            Supplier<IntList> f = FACTORIES.get(k);
            row(out, "W3", variant, NAMES.get(k), n, measure(() -> {
                IntList l = filled(f, d);
                long t0 = System.nanoTime();
                for (int x : ins) l.add(pos, x);
                long s = 0;
                for (int i = 0; i < UPDATES; i++) s += l.remove(pos);
                long t = System.nanoTime() - t0;
                sink += s;
                return result(t, l.metrics());
            }));
        }
    }

    static void w4(PrintWriter out, int n) {
        Random rnd = new Random(SEED);
        int[] d = data(rnd, n);
        row(out, "W4", "-", "MinHeap", n, measure(() -> {
            MinHeap h = new MinHeap();
            long t0 = System.nanoTime();
            for (int x : d) h.insert(x);
            int prev = Integer.MIN_VALUE;
            boolean ok = true;
            for (int i = 0; i < n; i++) {
                int m = h.extractMin();
                if (m < prev) ok = false;
                prev = m;
            }
            long t = System.nanoTime() - t0;
            if (!ok) throw new AssertionError("W4: extractMin order violated, n=" + n);
            return result(t, h.metrics());
        }));
    }
}
