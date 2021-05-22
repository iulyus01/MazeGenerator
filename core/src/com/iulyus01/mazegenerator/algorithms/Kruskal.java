package com.iulyus01.mazegenerator.algorithms;

import java.util.*;

public class Kruskal implements Algorithm {

    private int[][] grid;
    private int gridWidth;
    private int gridHeight;
    private int mazeWidth;
    private int mazeHeight;

    private Tree[][] sets;

    private final int N = 1;
    private final int S = 2;
    private final int E = 4;
    private final int W = 8;

    private final List<Triplet<Integer, Integer, Integer>> edgeList;

    private final Map<Integer, Integer> DX = Map.of(N, 0, S, 0, E, 1, W, -1);
    private final Map<Integer, Integer> DY = Map.of(N, -1, S, 1, E, 0, W, 0);
    private final Map<Integer, Integer> OPPOSITE = Map.of(N, S, S, N, E, W, W, E);

    volatile private boolean paused = false;

    public Kruskal(int mazeWidth, int mazeHeight) {
        this.mazeWidth = mazeWidth;
        this.mazeHeight = mazeHeight;
        gridWidth = (mazeWidth - 1) / 2;
        gridHeight = (mazeHeight - 1) / 2;
        edgeList = new ArrayList<>();
        grid = new int[gridHeight][gridWidth];

    }

    @Override
    public void reset() {
        grid = new int[gridHeight][gridWidth];
        sets = new Tree[gridHeight][gridWidth];

        for(int i = 0; i < gridHeight; i++) {
            Arrays.fill(grid[i], 0);

            for(int j = 0; j < gridWidth; j++) {
                sets[i][j] = new Tree();
            }
        }

        edgeList.clear();

        for(int y = 0; y < gridHeight; y++) {
            for(int x = 0; x < gridWidth; x++) {
                if(y > 0) edgeList.add(new Triplet<>(x, y, N));
                if(x > 0) edgeList.add(new Triplet<>(x, y, W));
            }
        }

        Collections.shuffle(edgeList);

    }

    @Override
    synchronized public void create(int startX, int startY, int sleepTime) {
        while(!edgeList.isEmpty()) {
            if(paused) {
                try {
                    wait();
                } catch(InterruptedException e) {
                    System.out.println("test: wait interrupted exception, paused: " + paused);
                    return;
                }
            }
            if(Thread.currentThread().isInterrupted()) return;


            int x = edgeList.get(edgeList.size() - 1).first;
            int y = edgeList.get(edgeList.size() - 1).second;
            int direction = edgeList.get(edgeList.size() - 1).third;
            edgeList.remove(edgeList.size() - 1);

            int nextX = x + DX.get(direction);
            int nextY = y + DY.get(direction);

            Tree set1 = sets[y][x];
            Tree set2 = sets[nextY][nextX];

            while(!set1.connected(set2)) {
                set1.connect(set2);
                grid[y][x] |= direction;
                grid[nextY][nextX] |= OPPOSITE.get(direction);

//                display();

                try {
                    Thread.sleep(sleepTime);
                } catch(Exception e) {
                    System.out.println("test: sleep interrupted");
                    return;
                }
            }
        }

    }

    @Override
    public void display() {
        StringBuilder builder = new StringBuilder();
        builder.append(" ");
        for(int i = 0; i < gridWidth * 2 - 1; i++) builder.append("_");
        builder.append("\n");
        for(int y = 0; y < gridHeight; y++) {
            builder.append("|");
            for(int x = 0; x < gridWidth; x++) {
                builder.append(((grid[y][x] & S) != 0) ? " " : "_");

                if((grid[y][x] & E) != 0)
                    builder.append((((grid[y][x] | grid[y][x + 1]) & S) != 0) ? " " : "_");
                else
                    builder.append("|");
            }
            builder.append("\n");
        }

        System.out.println(builder);
        System.out.println("test: " + gridWidth + " " + gridHeight);
    }

    @Override
    public int[][] gridToMaze() {
        int[][] maze = new int[mazeHeight][mazeWidth];

        for(int i = 0; i < mazeHeight; i++) for(int j = 0; j < mazeWidth; j++) maze[i][j] = 1;

        for(int i = 0; i < gridHeight; i++) {
            for(int j = 0; j < gridWidth; j++) {
                maze[i * 2 + 1][j * 2 + 1] = 0; // +0 +0
                maze[i * 2 + 2][j * 2 + 1] = (grid[i][j] & S) != S ? 1 : 0; // +1 +0 - down
                maze[i * 2 + 1][j * 2 + 2] = (grid[i][j] & E) != E ? 1 : 0; // +0 +1 - right
                maze[i * 2 + 2][j * 2 + 2] = 1; // +1 +1
            }
        }

//        for(int i = 0; i < mazeHeight; i++) {
//            for(int j = 0; j < mazeWidth; j++) {
//                System.out.print(maze[i][j] ? "# " : "* ");
//            }
//            System.out.println();
//        }

        return maze;
    }

    @Override
    public void setSize(int mazeWidth, int mazeHeight) {
        this.mazeWidth = mazeWidth;
        this.mazeHeight = mazeHeight;
        gridWidth = (mazeWidth - 1) / 2;
        gridHeight = (mazeHeight - 1) / 2;
    }

    @Override
    public void pause() {
        paused = true;
    }

    @Override
    public boolean isPaused() {
        return paused;
    }

    @Override
    public void resume() {
        System.out.println("test resume");
        paused = false;
    }

    private static class Tree {
        private Tree parent = null;

        private Tree getRoot() {
            return parent != null ? parent.getRoot() : this;
        }

        private boolean connected(Tree tree) {
            return getRoot() == tree.getRoot();
        }

        private void connect(Tree tree) {
            tree.getRoot().parent = this;
        }

    }

    private static class Triplet<A, B, C> {
        A first;
        B second;
        C third;

        public Triplet(A first, B second, C third) {
            this.first = first;
            this.second = second;
            this.third = third;
        }
    }

}
