package it.unibo.oop.workers02;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MultiThreadedSumMatrix implements SumMatrix {

    private final int nThread;

    public MultiThreadedSumMatrix(final int nThread) {
        this.nThread = nThread;
    }

    @Override
    public double sum(final double[][] matrix) {
        final List<Worker> workers = new ArrayList<>(nThread);
        final double[] flatMatrix = Arrays.stream(matrix)
                .flatMapToDouble(Arrays::stream)
                .toArray();
        final int elem = flatMatrix.length / nThread;
        for (int i = 0, start = 0; i < nThread; i++, start = start + elem) {
            workers.add(new Worker(flatMatrix, start, elem, nThread, i));
        }

        for (final Worker w : workers) {
            w.start();
        }

        double sum = 0;
        for (final Worker w : workers) {
            try {
                w.join();
                sum += w.getResult();
            } catch (InterruptedException e) {
                throw new IllegalStateException(e);
            }
        }
        return sum;
    }

    private static class Worker extends Thread {

        private final double[] matrix;
        private final int startPos;
        private final int elem;
        private final int nThread;
        private final int numberOfThread;
        private double res;

        private Worker(final double[] matrix, final int startPos, final int elem, final int nThread,
                final int numberOfThread) {
            super();
            this.matrix = matrix;
            this.startPos = startPos;
            this.elem = elem;
            this.nThread = nThread;
            this.numberOfThread = numberOfThread;
        }

        @Override
        public void run() {
            for (int i = startPos; i < startPos + elem || (i < matrix.length && numberOfThread == nThread - 1); i++) {
                this.res = this.res + this.matrix[i];
            }
        }

        private double getResult() {
            return this.res;
        }
    }
}
