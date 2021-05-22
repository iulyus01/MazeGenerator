package com.iulyus01.mazegenerator.algorithms;

import com.iulyus01.mazegenerator.Info;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class HuntAndKill3D implements Algorithm3D {

    private int[][][] grid;
    private int gridWidth;
    private int gridHeight;
    private int gridDepth;
    private int mazeWidth;
    private int mazeHeight;
    private int mazeDepth;

    private final int N = 1;
    private final int S = 2;
    private final int E = 4;
    private final int W = 8;
    private final int U = 16;
    private final int D = 32;
    private final int[] cardinals = new int[]{N, S, E, W, U, D};

    private final Map<Integer, Integer> DX = Map.of(N, 0, S, 0, E, 1, W, -1, U, 0, D, 0);
    private final Map<Integer, Integer> DY = Map.of(N, -1, S, 1, E, 0, W, 0, U, 0, D, 0);
    private final Map<Integer, Integer> DZ = Map.of(N, 0, S, 0, E, 0, W, 0, U, 1, D, -1);
    private final Map<Integer, Integer> OPPOSITE = Map.of(N, S, S, N, E, W, W, E, U, D, D, U);

    volatile private boolean paused = false;

    public HuntAndKill3D(int mazeWidth, int mazeHeight, int mazeDepth) {
        this.mazeWidth = mazeWidth;
        this.mazeHeight = mazeHeight;
        this.mazeDepth = mazeDepth;
        gridWidth = (mazeWidth - 1) / 2;
        gridHeight = (mazeHeight - 1) / 2;
        gridDepth = (mazeDepth - 1) / 2;
        grid = new int[gridDepth][gridHeight][gridWidth];

    }

    @Override
    public void reset() {
        grid = new int[gridDepth][gridHeight][gridWidth];
        for(int i = 0; i < gridDepth; i++) {
            for(int j = 0; j < gridHeight; j++) {
                Arrays.fill(grid[i][j], 0);
            }
        }
    }

    private Info.Triple<Integer, Integer, Integer> walk(int x, int y, int z) {
        List<Integer> directions = IntStream.rangeClosed(0, cardinals.length - 1).mapToObj(value -> cardinals[value]).collect(Collectors.toList());
        Collections.shuffle(directions);

        for(int dir : directions) {
            int nextX = x + DX.get(dir);
            int nextY = y + DY.get(dir);
            int nextZ = z + DZ.get(dir);

            if(nextX >= 0 && nextY >= 0 && nextZ >= 0 && nextX < gridWidth && nextY < gridHeight && nextZ < gridDepth && grid[nextZ][nextY][nextX] == 0) {
                grid[z][y][x] |= dir;
                grid[nextZ][nextY][nextX] |= OPPOSITE.get(dir);

                return new Info.Triple<>(nextX, nextY, nextZ);
            }
        }

        return null;
    }

    private Info.Triple<Integer, Integer, Integer> hunt() {
        for(int z = 0; z < gridDepth; z++) {
//            display();
            for(int y = 0; y < gridHeight; y++) {
                for(int x = 0; x < gridWidth; x++) {
                    if(grid[z][y][x] != 0) continue;

                    List<Integer> neighbors = new ArrayList<>();
                    if(z > 0 && grid[z - 1][y][x] != 0) neighbors.add(D);
                    if(y > 0 && grid[z][y - 1][x] != 0) neighbors.add(N);
                    if(x > 0 && grid[z][y][x - 1] != 0) neighbors.add(W);
                    if(z + 1 < gridDepth && grid[z + 1][y][x] != 0) neighbors.add(U);
                    if(x + 1 < gridWidth && grid[z][y][x + 1] != 0) neighbors.add(E);
                    if(y + 1 < gridHeight && grid[z][y + 1][x] != 0) neighbors.add(S);

                    if(neighbors.size() == 0) continue;
                    int direction = neighbors.get((int) (Math.random() * neighbors.size()));
                    int nextX = x + DX.get(direction);
                    int nextY = y + DY.get(direction);
                    int nextZ = z + DZ.get(direction);

                    grid[z][y][x] |= direction;
                    grid[nextZ][nextY][nextX] |= OPPOSITE.get(direction);

                    return new Info.Triple<>(x, y, z);
                }
            }
        }

        return null;
    }

    @Override
    synchronized public void create(int startX, int startY, int startZ, int sleepTime) {
        int x = (int) (Math.random() * gridWidth);
        int y = (int) (Math.random() * gridHeight);
        int z = (int) (Math.random() * gridDepth);

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

            Info.Triple<Integer, Integer, Integer> triple = walk(x, y, z);
            if(triple == null) {
                triple = hunt();
                if(triple == null) break;

            }
            x = triple.first;
            y = triple.second;
            z = triple.third;
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
                if(grid[0][y][x] == 0 && y + 1 < gridHeight && grid[0][y + 1][x] == 0) builder.append(" ");
                else builder.append(((grid[0][y][x] & S) != 0) ? " " : "_");

                if(grid[0][y][x] == 0 && x + 1 < gridWidth && grid[0][y][x + 1] == 0)
                    builder.append((y + 1 < gridHeight && (grid[0][y + 1][x] == 0 || grid[0][y + 1][x + 1] == 0)) ? " " : "_");
                else if((grid[0][y][x] & E) != 0)
                    builder.append((((grid[0][y][x] | grid[0][y][x + 1]) & S) != 0) ? " " : "_");
                else builder.append("|");
            }
            builder.append("\n");
        }

        System.out.println(builder);
    }

    @Override
    public int[][][] gridToMaze() {
        int[][][] maze = new int[mazeDepth][mazeHeight][mazeWidth];

        for(int i = 0; i < mazeDepth; i++)
            for(int j = 0; j < mazeHeight; j++)
                for(int k = 0; k < mazeWidth; k++) {
                    maze[i][j][k] = 1;
                }

        for(int z = 0; z < gridDepth; z++) {
            for(int y = 0; y < gridHeight; y++) {
                for(int x = 0; x < gridWidth; x++) {
                    maze[z * 2 + 1][y * 2 + 1][x * 2 + 1] = 0;
                    maze[z * 2 + 1][y * 2 + 2][x * 2 + 1] = (grid[z][y][x] & S) != S ? 1 : 0;
                    maze[z * 2 + 1][y * 2 + 1][x * 2 + 2] = (grid[z][y][x] & E) != E ? 1 : 0;
                    maze[z * 2 + 1][y * 2 + 2][x * 2 + 2] = 1;


                    maze[z * 2 + 2][y * 2 + 1][x * 2 + 1] = (grid[z][y][x] & U) == U ? 0 : 1;
                    maze[z * 2 + 2][y * 2 + 2][x * 2 + 2] = 1; // +1 +1
                    maze[z * 2 + 2][y * 2 + 1][x * 2 + 2] = 1; // +1 +1
                    maze[z * 2 + 2][y * 2 + 2][x * 2 + 1] = 1; // +1 +1


                    if((grid[z][y][x] & U) == U) maze[z * 2 + 1][y * 2 + 1][x * 2 + 1] = 2;
                    if((grid[z][y][x] & D) == D) maze[z * 2 + 1][y * 2 + 1][x * 2 + 1] = 3;
                    if((grid[z][y][x] & (U | D)) == (U | D)) maze[z * 2 + 1][y * 2 + 1][x * 2 + 1] = 4;

                }
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
    public void setSize(int mazeWidth, int mazeHeight, int mazeDepth) {
        this.mazeWidth = mazeWidth;
        this.mazeHeight = mazeHeight;
        this.mazeDepth = mazeDepth;
        gridWidth = (mazeWidth - 1) / 2;
        gridHeight = (mazeHeight - 1) / 2;
        gridDepth = (mazeDepth - 1) / 2;
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
