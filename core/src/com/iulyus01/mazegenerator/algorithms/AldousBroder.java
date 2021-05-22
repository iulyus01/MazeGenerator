package com.iulyus01.mazegenerator.algorithms;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class AldousBroder implements Algorithm {

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

    private final int CURSOR = 64;



    private final Map<Integer, Integer> DX = Map.of(N, 0, S, 0, E, 1, W, -1);
    private final Map<Integer, Integer> DY = Map.of(N, -1, S, 1, E, 0, W, 0);
    private final Map<Integer, Integer> OPPOSITE = Map.of(N, S, S, N, E, W, W, E);

    volatile private boolean paused = false;

    public AldousBroder(int mazeWidth, int mazeHeight) {
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

    @Override
    synchronized public void create(int startX, int startY, int sleepTime) {
        int x = (int) (Math.random() * gridWidth);
        int y = (int) (Math.random() * gridHeight);
        int remaining = gridWidth * gridHeight - 1;

        while(remaining > 0) {
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

            List<Integer> directions = IntStream.rangeClosed(0, 3).mapToObj(value -> cardinals[value]).collect(Collectors.toList());
            Collections.shuffle(directions);

//            display();
            for(int dir : directions) {
                int nextX = x + DX.get(dir);
                int nextY = y + DY.get(dir);

                if(nextX >= 0 && nextY >= 0 && nextX < gridWidth && nextY < gridHeight) {
                    if(grid[nextY][nextX] == 0) {
                        grid[y][x] |= dir;
                        grid[nextY][nextX] |= OPPOSITE.get(dir);
                        remaining -= 1;
                        // probably delete
//                        grid[nextY][nextX] |= CURSOR;
                    }
                    // probably delete
//                    if((grid[y][x] & CURSOR) != 0) grid[y][x] ^= CURSOR;

                    x = nextX;
                    y = nextY;

//                    display();
                    break;
                }
            }
        }
//        display();

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
//                builder.append(((grid[y][x] & S) != 0) ? " " : ((grid[y][x] & CURSOR) != 0 ? "*" : "_"));

                if((grid[y][x] & E) != 0)
                    builder.append((((grid[y][x] | grid[y][x + 1]) & S) != 0) ? " " : "_");
//                    builder.append((((grid[y][x] | grid[y][x + 1]) & S) != 0) ? " " : ((grid[y][x] & CURSOR) != 0 ? "*" : "_"));
                else
                    builder.append("|");
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
}
