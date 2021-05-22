package com.iulyus01.mazegenerator.algorithms;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class PrimSimplified implements Algorithm {

    private int[][] grid;
    private int gridWidth;
    private int gridHeight;
    private int mazeWidth;
    private int mazeHeight;

    private final int N = 1;
    private final int S = 2;
    private final int E = 4;
    private final int W = 8;
    private final int IN = 16;
    private final int FRONTIER = 32;

    private final List<Pair<Integer, Integer>> frontier;

    private final Map<Integer, Integer> OPPOSITE = Map.of(N, S, S, N, E, W, W, E);

    volatile private boolean paused = false;

    public PrimSimplified(int mazeWidth, int mazeHeight) {
        this.mazeWidth = mazeWidth;
        this.mazeHeight = mazeHeight;
        gridWidth = (mazeWidth - 1) / 2;
        gridHeight = (mazeHeight - 1) / 2;
        frontier = new ArrayList<>();
        grid = new int[gridHeight][gridWidth];

    }

    private void addFrontier(int x, int y) {
        if(x >= 0 && y >= 0 && y < gridHeight && x < gridWidth && grid[y][x] == 0) {
            grid[y][x] |= FRONTIER;
            frontier.add(new Pair<>(x, y));
        }
    }

    private void mark(int x, int y) {
        grid[y][x] |= IN;
        addFrontier(x - 1, y);
        addFrontier(x + 1, y);
        addFrontier(x, y - 1);
        addFrontier(x, y + 1);
    }

    private List<Pair<Integer, Integer>> getNeighbours(int x, int y) {
        List<Pair<Integer, Integer>> list = new ArrayList<>();
        if(x > 0 && (grid[y][x - 1] & IN) != 0) list.add(new Pair<>(x - 1, y));
        if(x + 1 < gridWidth && (grid[y][x + 1] & IN) != 0) list.add(new Pair<>(x + 1, y));
        if(y > 0 && (grid[y - 1][x] & IN) != 0) list.add(new Pair<>(x, y - 1));
        if(y + 1 < gridHeight && (grid[y + 1][x] & IN) != 0) list.add(new Pair<>(x, y + 1));

        return list;
    }

    private int direction(int fx, int fy, int tx, int ty) {
        if(fx < tx) return E;
        if(fx > tx) return W;
        if(fy < ty) return S;
        if(fy > ty) return N;
        return E;
    }

    private boolean empty(int cell) {
        return cell == 0 || cell == FRONTIER;
    }

    @Override
    public void reset() {
        grid = new int[gridHeight][gridWidth];
        for(int i = 0; i < gridHeight; i++) {
            Arrays.fill(grid[i], 0);
        }
        frontier.clear();
    }

    @Override
    synchronized public void create(int startX, int startY, int sleepTime) {
        mark((int) (Math.random() * gridWidth), (int) (Math.random() * gridHeight));
        while(!frontier.isEmpty()) {
            if(paused) {
                try {
                    wait();
                } catch(InterruptedException e) {
                    System.out.println("test: wait interrupted exception");
                    return;
                }
            }
            if(Thread.currentThread().isInterrupted()) return;

            int rand = (int) (Math.random() * frontier.size());
            int x = frontier.get(rand).first;
            int y = frontier.get(rand).second;
            frontier.remove(rand);

            List<Pair<Integer, Integer>> neighbours = getNeighbours(x, y);

            rand = (int) (Math.random() * neighbours.size());
            int nextX = neighbours.get(rand).first;
            int nextY = neighbours.get(rand).second;

            int dir = direction(x, y, nextX, nextY);
            grid[y][x] |= dir;
            grid[nextY][nextX] |= OPPOSITE.get(dir);

            mark(x, y);
//            display();

            try {
                Thread.sleep(sleepTime);
            } catch(Exception e) {
                System.out.println("test: sleep interrupted");
                return;
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
                if(empty(grid[y][x]) && y + 1 < gridHeight && empty(grid[y + 1][x])) builder.append(" ");
                else builder.append(((grid[y][x] & S) != 0) ? " " : "_");

                if(empty(grid[y][x]) && x + 1 < gridWidth && empty(grid[y][x + 1])) builder.append((y + 1 < gridHeight && (empty(grid[y + 1][x]) || empty(grid[y + 1][x + 1]))) ? " " : "_");
                else if ((grid[y][x] & E) != 0) builder.append((((grid[y][x] | grid[y][x + 1]) & S) != 0) ? " " : "_");
                else builder.append("|");
            }
            builder.append("\n");
        }

        System.out.println(builder);
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
        paused = false;
    }

    private static class Pair<A, B> {
        A first;
        B second;

        public Pair(A first, B second) {
            this.first = first;
            this.second = second;
        }
    }
}
