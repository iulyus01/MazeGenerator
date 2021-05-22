package com.iulyus01.mazegenerator.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.iulyus01.mazegenerator.Info;
import com.iulyus01.mazegenerator.MainClass;
import com.iulyus01.mazegenerator.DrawingManager;
import com.iulyus01.mazegenerator.ui.MazeScreenUI;

public class MazeScreen implements Screen {

    private final SpriteBatch batch;
    private final ShapeRenderer shapeRenderer;

    private final DrawingManager drawingManager;

    private final MazeScreenUI mazeScreenUi;


    public MazeScreen(MainClass app) {

        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
        Viewport viewport = new ScreenViewport();
        Camera camera = new OrthographicCamera(Info.W, Info.H);

        shapeRenderer.setProjectionMatrix(batch.getProjectionMatrix());

        viewport.setCamera(camera);

        int menuTopSize = 40;
        int menuBottomSize = 60;
        drawingManager = new DrawingManager(0, 0, Info.W, Info.H - menuTopSize - menuBottomSize);

        mazeScreenUi = new MazeScreenUI(app, drawingManager, batch, viewport, menuTopSize, menuBottomSize);
    }

    @Override
    public void show() {

    }

    private void update(float delta) {
        mazeScreenUi.update(delta);
        drawingManager.update(delta);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glEnable(GL30.GL_BLEND);
        update(delta);


        ScreenUtils.clear(Info.colorGreyLighten5);

        drawingManager.draw(shapeRenderer, batch);
        mazeScreenUi.draw();
    }

    @Override
    public void resize(int width, int height) {
        Info.W = Gdx.graphics.getWidth();
        Info.H = Gdx.graphics.getHeight();

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        batch.dispose();
        shapeRenderer.dispose();
        mazeScreenUi.dispose();
    }
}
