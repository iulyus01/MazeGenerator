package com.iulyus01.mazegenerator.screens;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import com.iulyus01.mazegenerator.MainClass;

public class MainScreen implements Screen {

    private MainClass mainClass;

    private SpriteBatch batch;



    public MainScreen(MainClass mainClass) {
        this.mainClass = mainClass;

        batch = new SpriteBatch();

        System.out.println("test constructor");

    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
		ScreenUtils.clear(.24f, .31f, .56f, 1);

        System.out.println("test render " + delta);

    }

    @Override
    public void resize(int width, int height) {

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

    }
}
