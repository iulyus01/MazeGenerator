package com.iulyus01.mazegenerator.solvers;

import com.iulyus01.mazegenerator.Info;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class Solver {

    protected List<Info.Pair<Integer, Integer>> pathList;

    protected int[][] maze;
    protected int mazeWidth;
    protected int mazeHeight;

    protected Info.Pair<Integer, Integer> startPoint;
    protected Info.Pair<Integer, Integer> finishPoint;

    protected int x;
    protected int y;

    protected boolean isFinished;

    protected final int N = 1;
    protected final int S = 2;
    protected final int E = 4;
    protected final int W = 8;
    protected final int[] cardinals = new int[]{E, S, W, N};

    protected final Map<Integer, Integer> DX = Map.of(N, 0, S, 0, E, 1, W, -1);
    protected final Map<Integer, Integer> DY = Map.of(N, -1, S, 1, E, 0, W, 0);

    private boolean isShowing;
    private boolean isRunning;
    private boolean isSelectingStartPoint;
    private boolean isSelectingFinishPoint;

    Solver(int mazeWidth, int mazeHeight) {
        this.mazeWidth = mazeWidth;
        this.mazeHeight = mazeHeight;
        maze = new int[mazeHeight][mazeWidth];

        pathList = new ArrayList<>();
        startPoint = new Info.Pair<>(1, 1);
        finishPoint = new Info.Pair<>(mazeWidth - 2, mazeHeight - 2);
        this.x = 1;
        this.y = 1;

        isFinished = false;
        isRunning = false;
        isShowing = false;

        pathList.add(new Info.Pair<>(1, 1));
    }

    public void reset() {
        System.out.println("test: reset " + startPoint.first + " " + startPoint.second);
        pathList.clear();
        x = startPoint.first;
        y = startPoint.second;

        isFinished = false;
        isRunning = false;

        pathList.add(new Info.Pair<>(1, 1));
    }

    public void setMaze(int[][] maze) {
        this.maze = maze;
    }

    public void setSize(int mazeWidth, int mazeHeight) {
        this.mazeWidth = mazeWidth;
        this.mazeHeight = mazeHeight;
        maze = new int[mazeHeight][mazeWidth];
    }

    public void setStart(int x, int y) {
        startPoint = new Info.Pair<>(x, y);
        this.x = x;
        this.y = y;
        pathList.set(0, new Info.Pair<>(x, y));
    }

    public void setFinish(int x, int y) {
        finishPoint = new Info.Pair<>(x, y);
    }

    public void setShowing(boolean showing) {
        isShowing = showing;
    }

    public void setRunning(boolean running) {
        isRunning = running;
    }

    public void setFinished(boolean finished) {
        isFinished = finished;
    }

    public void setSelectingStartPoint(boolean selectingStartPoint) {
        isSelectingStartPoint = selectingStartPoint;
    }

    public void setSelectingFinishPoint(boolean selectingFinishPoint) {
        isSelectingFinishPoint = selectingFinishPoint;
    }

    public Info.Pair<Integer, Integer> getStart() {
        return startPoint;
    }

    public Info.Pair<Integer, Integer> getFinish() {
        return finishPoint;
    }

    public List<Info.Pair<Integer, Integer>> getPathList() {
        return pathList;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public boolean isShowing() {
        return isShowing;
    }

    public boolean isRunning() {
        return isRunning;
    }

    public boolean isFinished() {
        return isFinished;
    }

    public boolean isSelectingStartPoint() {
        return isSelectingStartPoint;
    }

    public boolean isSelectingFinishPoint() {
        return isSelectingFinishPoint;
    }

    public abstract void step();
}
