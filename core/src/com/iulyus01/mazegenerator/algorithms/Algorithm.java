package com.iulyus01.mazegenerator.algorithms;

public interface Algorithm {

    void reset();
    void create(int startX, int startY, int sleepTime);
    void display();
    int[][] gridToMaze();

    void setSize(int mazeWidth, int mazeHeight);

    void pause();
    boolean isPaused();
    void resume();

}
