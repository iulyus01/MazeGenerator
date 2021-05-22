package com.iulyus01.mazegenerator.algorithms;

public interface Algorithm3D {

    void reset();
    void create(int startX, int startY, int startZ, int sleepTime);
    void display();
    int[][][] gridToMaze();

    void setSize(int mazeWidth, int mazeHeight, int mazeDepth);

    void pause();
    boolean isPaused();
    void resume();

}
