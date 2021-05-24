package com.iulyus01.mazegenerator.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.ScreenUtils;
import com.iulyus01.mazegenerator.Info;
import com.iulyus01.mazegenerator.MainClass;
import com.iulyus01.mazegenerator.ui.DynamicBackground;

public class MainMenuScreen implements Screen {

    private final MainClass app;

    private final DynamicBackground background;
    private final Stage stage;
    private final ShapeRenderer shapeRenderer;

    public MainMenuScreen(MainClass app) {
        this.app = app;

        background = new DynamicBackground(14, .8f);
        stage = new Stage();
        shapeRenderer = new ShapeRenderer();

        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void show() {
        Label titleLabel = new Label(Info.applicationTitle, app.uiStyles.getTitleLabelStyle());

        TextButton randomButton = new TextButton("Random", app.uiStyles.getTextButtonStyle());
        TextButton advancedButton = new TextButton("Advanced", app.uiStyles.getTextButtonStyle());
        advancedButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                app.setScreen(new MazeScreen(app));
            }
        });
        TextButton exitButton = new TextButton("Exit", app.uiStyles.getTextButtonStyle());
        exitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                Gdx.app.exit();
            }
        });


        Table table = new Table();
        table.setFillParent(true);
        table.add(titleLabel).padBottom(180).row();
        table.add(randomButton).padBottom(20).row();
        table.add(advancedButton).padBottom(20).row();
        table.add(exitButton).padBottom(200);

        stage.addActor(table);

    }

    private void update(float delta) {
        background.update(delta);
        stage.act(delta);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glEnable(GL30.GL_BLEND);
        ScreenUtils.clear(Info.colorBlueLighten5);

        update(delta);

        background.draw(shapeRenderer);
        stage.draw();
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
