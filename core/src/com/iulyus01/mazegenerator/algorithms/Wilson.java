package com.iulyus01.mazegenerator.algorithms;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Wilson implements Algorithm {

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
    private final int IN = 16;

    private final Map<Integer, Integer> DX = Map.of(N, 0, S, 0, E, 1, W, -1);
    private final Map<Integer, Integer> DY = Map.of(N, -1, S, 1, E, 0, W, 0);
    private final Map<Integer, Integer> OPPOSITE = Map.of(N, S, S, N, E, W, W, E);

    volatile private boolean paused = false;

    public Wilson(int mazeWidth, int mazeHeight) {
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
        grid[(int) (Math.random() * gridHeight)][(int) (Math.random() * gridWidth)] = IN;

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

            List<Triplet<Integer, Integer, Integer>> list = walk();
            for(Triplet<Integer, Integer, Integer> triplet : list) {
                int x = triplet.first;
                int y = triplet.second;
                int dir = triplet.third;
                int nextX = x + DX.get(dir);
                int nextY = y + DY.get(dir);

                grid[y][x] |= dir;
                grid[nextY][nextX] |= OPPOSITE.get(dir);

                remaining -= 1;
            }

            // TODO maybe move the sleep in the walk() function
            try {
                Thread.sleep(sleepTime);
            } catch(Exception e) {
                System.out.println("test: sleep interrupted");
                return;
            }
        }

    }

    private List<Triplet<Integer, Integer, Integer>> walk() {
        while(true) {
            int cx = (int) (Math.random() * gridWidth);
            int cy = (int) (Math.random() * gridHeight);
            if(grid[cy][cx] != 0) continue;

            Map<String, Integer> visits = new HashMap<>();
            visits.put(cx + "" + cy, 0);

            int startX = cx;
            int startY = cy;
            boolean walking = true;

            while(walking) {
//                display();

                walking = false;

                List<Integer> directions = IntStream.rangeClosed(0, 3).mapToObj(value -> cardinals[value]).collect(Collectors.toList());
                Collections.shuffle(directions);

                for(int dir : directions) {
                    int nextX = cx + DX.get(dir);
                    int nextY = cy + DY.get(dir);
                    if(nextX >= 0 && nextY >= 0 && nextY < gridHeight && nextX < gridWidth) {
                        visits.put(cx + "" + cy, dir);

                        if(grid[nextY][nextX] == 0) {
                            cx = nextX;
                            cy = nextY;
                            walking = true;
                        }
                        break;
                    }
                }
            }

            List<Triplet<Integer, Integer, Integer>> path = new ArrayList<>();
            int x = startX;
            int y = startY;
            while(visits.get(x + "" + y) != null) {
                int dir = visits.get(x + "" + y);
                path.add(new Triplet<>(x, y, dir));
                x = x + DX.get(dir);
                y = y + DY.get(dir);
            }

            return path;
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
