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
import com.iulyus01.mazegenerator.ui.DynamicBackground;
import com.iulyus01.mazegenerator.ui.MazeScreenUI;

public class MazeScreen implements Screen {

    private final SpriteBatch batch;
    private final ShapeRenderer shapeRenderer;

    private final DrawingManager drawingManager;

    private final MazeScreenUI mazeScreenUi;

    private final DynamicBackground background;


    public MazeScreen(MainClass app) {
        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
        Viewport viewport = new ScreenViewport();
        Camera camera = new OrthographicCamera(Info.W, Info.H);

        shapeRenderer.setProjectionMatrix(batch.getProjectionMatrix());

        viewport.setCamera(camera);

        int menuTopSize = 60;
        float menuSideSize = Info.W / 8f;
        drawingManager = new DrawingManager(0, 0, (int) (Info.W - menuSideSize * 2), Info.H - menuTopSize);

        mazeScreenUi = new MazeScreenUI(app, drawingManager, batch, shapeRenderer, viewport, menuTopSize, menuSideSize);

        background = new DynamicBackground(8, .1f);

    }

    @Override
    public void show() {

    }

    private void update(float delta) {
        background.update(delta);
        mazeScreenUi.update(delta);
        drawingManager.update(delta);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glEnable(GL30.GL_BLEND);
        update(delta);

        ScreenUtils.clear(Info.colorGreyLighten5);

        background.draw(shapeRenderer);
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
