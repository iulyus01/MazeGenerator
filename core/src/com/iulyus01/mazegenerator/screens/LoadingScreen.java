package com.iulyus01.mazegenerator.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGeneratorLoader;
import com.badlogic.gdx.graphics.g2d.freetype.FreetypeFontLoader;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.ScreenUtils;
import com.iulyus01.mazegenerator.Info;
import com.iulyus01.mazegenerator.MainClass;
import com.iulyus01.mazegenerator.ui.UIStyles;

public class LoadingScreen implements Screen {

    private final MainClass app;

    private final ShapeRenderer shapeRenderer;

    private float dotDelay;
    private final float dotDelayMax = .2f;
    private float dotNr = 0;
    private final float dotNrMax = 5;
    private float loadingDelay;
    private float loadingDelayMax = 1000;

    public LoadingScreen(MainClass app) {
        this.app = app;

        shapeRenderer = new ShapeRenderer();

        queueAssets();

    }

    @Override
    public void show() {
        System.out.println("loading screen show");
    }

    private void update(float delta) {
//        if(app.assetManager.update() && loadingDelay > loadingDelayMax) {
        if(app.assetManager.update()) {
            app.uiStyles = new UIStyles(app);
            app.setScreen(new MainMenuScreen(app));
        }

        dotDelay += delta;
        loadingDelay += delta * 1000;
        if(dotDelay >= dotDelayMax) {
            dotDelay = 0;
            dotNr++;
            dotNr %= (dotNrMax + 1);
        }
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(Color.BLACK);

        update(delta);


        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        for(int i = 0; i < dotNr; i++) {
            shapeRenderer.rect(Info.W / 2f - 65 + i * 30, Info.H / 2f, 10, 10);
        }
        shapeRenderer.rectLine(Info.W / 2f - 65, Info.H / 2f - 30, Info.W / 2f - 65 + 130, Info.H / 2f - 30, 1);
        shapeRenderer.rectLine(Info.W / 2f - 65, Info.H / 2f - 20, Info.W / 2f - 65 + 130, Info.H / 2f - 20, 1);
        shapeRenderer.rectLine(Info.W / 2f - 65, Info.H / 2f - 30, Info.W / 2f - 65, Info.H / 2f - 20, 1);
        shapeRenderer.rectLine(Info.W / 2f - 65 + 130, Info.H / 2f - 30, Info.W / 2f - 65 + 130, Info.H / 2f - 20, 1);
        shapeRenderer.rect(Info.W / 2f - 65, Info.H / 2f - 30, app.assetManager.getProgress() * 130f, 10);

        shapeRenderer.end();
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
        shapeRenderer.dispose();
    }

    private void queueAssets() {
        app.assetManager.load("FlatSkin.json", Skin.class);
        app.assetManager.load("FlatSkinReversed.json", Skin.class);

        app.assetManager.load("GoUpButton.png", Texture.class);
        app.assetManager.load("GoUpButtonOver.png", Texture.class);
        app.assetManager.load("GoUpButtonDown.png", Texture.class);
        app.assetManager.load("GoDownButton.png", Texture.class);
        app.assetManager.load("GoDownButtonOver.png", Texture.class);
        app.assetManager.load("GoDownButtonDown.png", Texture.class);


        FileHandleResolver resolver = new InternalFileHandleResolver();
        app.assetManager.setLoader(FreeTypeFontGenerator.class, new FreeTypeFontGeneratorLoader(resolver));
        app.assetManager.setLoader(BitmapFont.class, ".ttf", new FreetypeFontLoader(resolver));

        // load to fonts via the generator (implicitely done by the FreetypeFontLoader).
        // Note: you MUST specify a FreetypeFontGenerator defining the ttf font file name and the size
        // of the font to be generated. The names of the fonts are arbitrary and are not pointing
        // to a file on disk (but must end with the font's file format '.ttf')!
        FreetypeFontLoader.FreeTypeFontLoaderParameter size1Params = new FreetypeFontLoader.FreeTypeFontLoaderParameter();
        size1Params.fontFileName = "DoppioOne.ttf";
        size1Params.fontParameters.size = 22;
        app.assetManager.load("DoppioOne.ttf", BitmapFont.class, size1Params);

        FreetypeFontLoader.FreeTypeFontLoaderParameter size2Params = new FreetypeFontLoader.FreeTypeFontLoaderParameter();
        size2Params.fontFileName = "DoppioOne.ttf";
        size2Params.fontParameters.size = 30;
        app.assetManager.load("DoppioOneSize30.ttf", BitmapFont.class, size2Params);

        // we also load a "normal" font generated via Hiero
//        app.assetManager.load("data/default.fnt", BitmapFont.class);
    }
}
