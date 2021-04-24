package com.iulyus01.mazegenerator;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class UI {

    private Stage stage;

    private BitmapFont font;

    public UI() {
        Viewport viewport = new ScreenViewport();
        stage = new Stage(viewport);

        Gdx.input.setInputProcessor(stage);

    }
}
