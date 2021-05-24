package com.iulyus01.mazegenerator;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class DrawingManager {

    private final Maze maze;
//    private ShapeRenderer shapeRenderer;

//    private final int screenWidth;
//    private final int screenHeight;

    public DrawingManager(int x, int y, int screenWidth, int screenHeight) {
//        this.screenWidth = screenWidth;
//        this.screenHeight = screenHeight;

        maze = new Maze(screenWidth, screenHeight, x, y, 11, 11, 11);
    }

    public void update(float delta) {
        maze.update(delta);
    }

    public void draw(ShapeRenderer shapeRenderer, SpriteBatch batch) {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(Info.colorRed);

        maze.draw(shapeRenderer, batch);

        shapeRenderer.end();

    }

    public Maze getMaze() {
        return maze;
    }
}
