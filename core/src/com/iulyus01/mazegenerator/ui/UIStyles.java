package com.iulyus01.mazegenerator.ui;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGeneratorLoader;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.iulyus01.mazegenerator.Info;
import com.iulyus01.mazegenerator.MainClass;

public class UIStyles {

    private MainClass app;

    private final TextButton.TextButtonStyle textButtonStyle;
    private final Label.LabelStyle labelStyle;

    public UIStyles(MainClass app) {
        this.app = app;

//        FreeTypeFontGenerator generator = app.assetManager.get("DoppioOne.ttf", FreeTypeFontGenerator.class);
//        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
//        parameter.size = 24;

        textButtonStyle = new TextButton.TextButtonStyle();
        labelStyle = new Label.LabelStyle();
//        textButtonStyle.font = generator.generateFont(parameter);

        textButtonStyle.font = app.assetManager.get("DoppioOne.ttf");
        textButtonStyle.fontColor = Info.colorWhite;
        textButtonStyle.overFontColor = Info.colorCyan;
        textButtonStyle.downFontColor = Info.colorRed;

        labelStyle.font = app.assetManager.get("DoppioOneSize30.ttf");
        labelStyle.fontColor = Info.colorBlue;

//        Button.ButtonStyle style = new Button.ButtonStyle();
    }

    public TextButton.TextButtonStyle getTextButtonStyle() {
        return textButtonStyle;
    }

    public Label.LabelStyle getLabelStyle() {
        return labelStyle;
    }

    public ImageButton.ImageButtonStyle createImageButtonStyle(Texture up, Texture over, Texture down) {
        ImageButton.ImageButtonStyle style = new ImageButton.ImageButtonStyle();
//        style.imageUp = new NinePatchDrawable(up);
        style.imageUp = new TextureRegionDrawable(up);
        style.imageOver = new TextureRegionDrawable(over);
        style.imageDown = new TextureRegionDrawable(down);
        return style;
    }
}
