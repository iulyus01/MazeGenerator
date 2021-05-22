package com.iulyus01.mazegenerator.algorithms;

import com.iulyus01.mazegenerator.Info;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class HuntAndKill implements Algorithm {

    private int[][] grid;
    private int gridWidth;
    private int gridHeight;
    private int mazeWidth;
    private int mazeHeight;

    private final int N = 1;
    private final int S = 2;
    private final int E = 4;
    private final int W = 8;
    private final int[] cardinals = new int[]{N, S, E, W};

    private final Map<Integer, Integer> DX = Map.of(N, 0, S, 0, E, 1, W, -1);
    private final Map<Integer, Integer> DY = Map.of(N, -1, S, 1, E, 0, W, 0);
    private final Map<Integer, Integer> OPPOSITE = Map.of(N, S, S, N, E, W, W, E);

    volatile private boolean paused = false;

    public HuntAndKill(int mazeWidth, int mazeHeight) {
        this.mazeWidth = mazeWidth;
        this.mazeHeight = mazeHeight;
        gridWidth = (mazeWidth - 1) / 2;
        gridHeight = (mazeHeight - 1) / 2;
        grid = new int[gridHeight][gridWidth];

    }

    @Override
    public void reset() {
        grid = new int[gridHeight][gridWidth];
        for(int i = 0; i < gridHeight; i++) {
            Arrays.fill(grid[i], 0);
        }
    }

    private Info.Pair<Integer, Integer> walk(int x, int y) {
        List<Integer> directions = IntStream.rangeClosed(0, 3).mapToObj(value -> cardinals[value]).collect(Collectors.toList());
        Collections.shuffle(directions);

        for(int dir : directions) {
            int nextX = x + DX.get(dir);
            int nextY = y + DY.get(dir);

            if(nextX >= 0 && nextY >= 0 && nextY < gridHeight && nextX < gridWidth && grid[nextY][nextX] == 0) {
                grid[y][x] |= dir;
                grid[nextY][nextX] |= OPPOSITE.get(dir);

                return new Info.Pair<>(nextX, nextY);
            }
        }

        return null;
    }

    private Info.Pair<Integer, Integer> hunt() {
        for(int y = 0; y < gridHeight; y++) {
//            display();

            for(int x = 0; x < gridWidth; x++) {
                if(grid[y][x] != 0) continue;

                List<Integer> neighbors = new ArrayList<>();
                if(y > 0 && grid[y - 1][x] != 0) neighbors.add(N);
                if(x > 0 && grid[y][x - 1] != 0) neighbors.add(W);
                if(x + 1 < gridWidth && grid[y][x + 1] != 0) neighbors.add(E);
                if(y + 1 < gridHeight && grid[y + 1][x] != 0) neighbors.add(S);

                if(neighbors.size() == 0) continue;
                int direction = neighbors.get((int) (Math.random() * neighbors.size()));
                int nextX = x + DX.get(direction);
                int nextY = y + DY.get(direction);

                grid[y][x] |= direction;
                grid[nextY][nextX] |= OPPOSITE.get(direction);

                return new Info.Pair<>(x, y);
            }
        }

        return null;
    }

    @Override
    synchronized public void create(int startX, int startY, int sleepTime) {
        int x = (int) (Math.random() * gridWidth);
        int y = (int) (Math.random() * gridHeight);

        while(true) {
            if(paused) {
                try {
                    wait();
                } catch(InterruptedException e) {
                    System.out.println("test: wait interrupted exception");
                    return;
                }
            }
            if(Thread.currentThread().isInterrupted()) return;

            try {
                Thread.sleep(sleepTime);
            } catch(Exception e) {
                System.out.println("test: sleep interrupted");
                return;
            }

//            display();

            Info.Pair<Integer, Integer> pair = walk(x, y);
            if(pair == null) {
                pair = hunt();
                if(pair == null) break;

            }
            x = pair.first;
            y = pair.second;
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
                if(grid[y][x] == 0 && y + 1 < gridHeight && grid[y + 1][x] == 0) builder.append(" ");
                else builder.append(((grid[y][x] & S) != 0) ? " " : "_");

                if(grid[y][x] == 0 && x + 1 < gridWidth && grid[y][x + 1] == 0) builder.append((y + 1 < gridHeight && (grid[y + 1][x] == 0 || grid[y + 1][x + 1] == 0)) ? " " : "_");
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

//    private static class Pair<A, B> {
//        A first;
//        B second;
//
//        public Pair(A first, B second) {
//            this.first = first;
//            this.second = second;
//        }
//    }
}
