package com.iulyus01.mazegenerator.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.iulyus01.mazegenerator.DrawingManager;
import com.iulyus01.mazegenerator.Info;
import com.iulyus01.mazegenerator.MainClass;
import com.iulyus01.mazegenerator.algorithms.*;
import com.iulyus01.mazegenerator.screens.MainMenuScreen;

public class MazeScreenUI {

    private final MainClass app;
    private final DrawingManager drawingManager;

    private final Stage stage;

    private Label runningLabel;
    private final TextField.TextFieldFilter numberFilter = (textField, c) -> c >= '0' && c <= '9';

    private final String[] algorithmArray = new String[] {
            "Recursive backtracking",
            "Kruskal",
            "Prim",
            "Aldous Broder",
            "Wilson",
            "Hunt-and-Kill",
            "Growing Tree Random",
            "Growing Tree Oldest",
            "Growing Tree Newest",
            "Growing Tree Custom"
    };
    private final String[] algorithm3DArray = new String[] {
            "Hunt-and-Kill "
    };
    private final String[] exportFormatArray = new String[] {
            "PNG",
            "JPG",
            "SVG",
            "JSON",
            "TXT"
    };

    private final int menuTopSize;
    private final int menuBottomSize;
    private final int buttonWidth;
    private final int buttonHeight;

    private int labelCounter;
    private float labelCounterDelay;
    private final float labelCounterDelayMax = .4f;

    public MazeScreenUI(MainClass app, DrawingManager drawingManager, SpriteBatch batch, Viewport viewport, int menuTopSize, int menuBottomSize) {
        this.app = app;
        this.drawingManager = drawingManager;

        this.menuTopSize = menuTopSize;
        this.menuBottomSize = menuBottomSize;

        this.buttonWidth = 100;
        this.buttonHeight = 40;

        stage = new Stage(viewport, batch);

        Gdx.input.setInputProcessor(stage);

        createUI();

    }

    public void update(float delta) {
        labelCounterDelay += delta;
        if(labelCounterDelay > labelCounterDelayMax) {
            labelCounter = (labelCounter + 1) % 4;
            labelCounterDelay = 0;
        }

        StringBuilder labelText;
        switch(drawingManager.getMaze().getAlgState()) {
            case WAITING:
                labelText = new StringBuilder("Waiting");
                break;
            case RUNNING:
                labelText = new StringBuilder("Running");
                for(int i = 0; i < labelCounter; i++) labelText.append(".");
                break;
            case PAUSED:
                labelText = new StringBuilder("Paused");
                break;
            default:
                labelText = new StringBuilder();
        }
        runningLabel.setText(labelText);


        stage.act(delta);
    }

    public void draw() {
        stage.draw();
    }

    public void dispose() {

    }

    private void createUI() {
        Skin skin = app.assetManager.get("FlatSkin.json", Skin.class);
        Skin skinReversed = app.assetManager.get("FlatSkinReversed.json", Skin.class);



        Table table = new Table();
//        table.setDebug(true);
        table.setFillParent(true);
        table.left().top();

        TextButton exitButton = new TextButton("BACK", skin);
        exitButton.setWidth(buttonWidth);
        exitButton.setHeight(buttonHeight);
        exitButton.setX(0);
        exitButton.setY(Info.H - buttonHeight);
        exitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                app.setScreen(new MainMenuScreen(app));
            }
        });

        runningLabel = new Label("", app.uiStyles.getLabelStyle());


        table.add(createTopMenu(skin, skinReversed)).height(buttonHeight).expandX().row();
        table.add(runningLabel).height(buttonHeight * 2).row();
        table.add(createRightMenu(skin, skinReversed)).right().padRight(25).growY().row();
        table.add(createBottomMenu(skin, skinReversed)).height(buttonHeight * 1.5f).expandX();

        stage.addActor(table);
        stage.addActor(exitButton);
        stage.addActor(createExportMenu(skin, skinReversed));

    }

    private Table createTopMenu(Skin skin, Skin skinReversed) {
        Table table = new Table();
        table.left().top();

        Label widthLabel = new Label("Maze width", app.uiStyles.getLabelStyle());
        Label heightLabel = new Label("Maze height", skinReversed);
        Label depthLabel = new Label("Maze depth", skinReversed);
        Label delayLabel = new Label("delay", skinReversed);
        TextField widthInput = new TextField("11", skin);
        TextField heightInput = new TextField("11", skin);
        TextField depthInput = new TextField("11", skin);
        TextField delayInput = new TextField("100", skin);
        CheckBox box = new CheckBox("3D", skinReversed);
        SelectBox<String> selectBox = new SelectBox<>(skin);

        widthLabel.setAlignment(Align.center);

        widthInput.setAlignment(Align.center);
        widthInput.setTextFieldFilter(numberFilter);
        widthInput.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                String text = widthInput.getText();
                drawingManager.getMaze().setMazeWidth(text.isEmpty() ? 3 : Integer.parseInt(text));
            }
        });


        heightLabel.setAlignment(Align.center);

        heightInput.setAlignment(Align.center);
        heightInput.setTextFieldFilter(numberFilter);
        heightInput.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                String text = heightInput.getText();
                drawingManager.getMaze().setMazeHeight(text.isEmpty() ? 3 : Integer.parseInt(text));
            }
        });


        depthLabel.setAlignment(Align.center);

        depthInput.setAlignment(Align.center);
        depthInput.setTextFieldFilter(numberFilter);
        depthInput.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                String text = depthInput.getText();
                drawingManager.getMaze().setMazeDepth(text.isEmpty() ? 3 : Integer.parseInt(text));
            }
        });


        delayLabel.setAlignment(Align.center);

        delayInput.setAlignment(Align.center);
        delayInput.setTextFieldFilter(numberFilter);
        delayInput.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                String text = delayInput.getText();
                drawingManager.getMaze().setStepDelay(text.isEmpty() ? 0 : Integer.parseInt(text));
            }
        });


        box.setChecked(false);
        box.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if(box.isChecked()) selectBox.setItems(algorithm3DArray);
                else selectBox.setItems(algorithmArray);
            }
        });

        selectBox.setItems(algorithmArray);
        selectBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                int index = selectBox.getSelectedIndex();

                if(!box.isChecked()) {
                    if(index == 0) drawingManager.getMaze().setAlgorithm(RecursiveBacktracking.class);
                    else if(index == 1) drawingManager.getMaze().setAlgorithm(Kruskal.class);
                    else if(index == 2) drawingManager.getMaze().setAlgorithm(PrimSimplified.class);
                    else if(index == 3) drawingManager.getMaze().setAlgorithm(AldousBroder.class);
                    else if(index == 4) drawingManager.getMaze().setAlgorithm(Wilson.class);
                    else if(index == 5) drawingManager.getMaze().setAlgorithm(HuntAndKill.class);
                    else if(index == 6) drawingManager.getMaze().setAlgorithm(GrowingTreeRandom.class);
                    else if(index == 7) drawingManager.getMaze().setAlgorithm(GrowingTreeOldest.class);
                    else if(index == 8) drawingManager.getMaze().setAlgorithm(GrowingTreeNewest.class);
                    else if(index == 9) drawingManager.getMaze().setAlgorithm(GrowingTreeCustom.class);
                } else {
                    if(index == 0) drawingManager.getMaze().setAlgorithm(HuntAndKill3D.class);

                }
                // TODO add other algorithms
            }
        });


        Label label = new Label(null, skin);

        float inputWidth = buttonWidth / 4f * 3;
        table.add(label).expandX();
        table.add(widthInput).width(inputWidth).padRight(1).growY();
        table.add(heightInput).width(inputWidth).padRight(1).growY();
        table.add(depthInput).width(inputWidth).growY();
        table.add(delayLabel).width(buttonWidth).growY();
        table.add(delayInput).width(inputWidth).growY();
        table.add(box).width(buttonWidth / 2f).growY();
        table.add(selectBox).width(buttonWidth * 2.3f).growY();
        table.add(label).expandX();

        return table;
    }

    private Table createBottomMenu(Skin skin, Skin skinReversed) {
        Table table = new Table();

        TextButton showMazeButton = new TextButton("Test", skin);
        TextButton resetButton = new TextButton("Reset", skin);
        TextButton newMazeButton = new TextButton("New Maze", app.uiStyles.getTextButtonStyle());
        TextButton pauseButton = new TextButton("Pause", skin);
        TextButton resumeButton = new TextButton("Resume", skin);
        TextButton solveButton = new TextButton("Solve", skin);


        showMazeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                drawingManager.getMaze().showMaze();
            }
        });

        resetButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                drawingManager.getMaze().reset();
            }
        });

        newMazeButton.setStyle(skin.get("default", TextButton.TextButtonStyle.class));
        newMazeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                drawingManager.getMaze().newMaze();
            }
        });

        pauseButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                drawingManager.getMaze().pause();
            }
        });

        resumeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                drawingManager.getMaze().resume();
            }
        });

        solveButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                drawingManager.getMaze().solve();
            }
        });


        Label label = new Label(null, skin);

        table.add(label).expandX();
        table.add(showMazeButton).width(buttonWidth).padRight(1).growY();
        table.add(resetButton).width(buttonWidth).padRight(1).growY();
        table.add(newMazeButton).width(buttonWidth * 1.5f).padRight(1).growY();
        table.add(pauseButton).width(buttonWidth).padRight(1).growY();
        table.add(resumeButton).width(buttonWidth).growY();
        table.add(solveButton).width(buttonWidth).growY();
        table.add(label).expandX();

        return table;
    }

    private Table createRightMenu(Skin skin, Skin skinReversed) {
        Table table = new Table();
        table.right();

        TextField renderDepthInput = new TextField("1", skin);
        ImageButton renderDepthUpButton = new ImageButton(app.uiStyles.createImageButtonStyle(
                app.assetManager.get("GoUpButton.png", Texture.class),
                app.assetManager.get("GoUpButtonOver.png", Texture.class),
                app.assetManager.get("GoUpButtonDown.png", Texture.class)
        ));
        ImageButton renderDepthDownButton = new ImageButton(app.uiStyles.createImageButtonStyle(
                app.assetManager.get("GoDownButton.png", Texture.class),
                app.assetManager.get("GoDownButtonOver.png", Texture.class),
                app.assetManager.get("GoDownButtonDown.png", Texture.class)
        ));

        renderDepthInput.setAlignment(Align.center);
        renderDepthInput.setTextFieldFilter(numberFilter);
        renderDepthInput.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                String text = renderDepthInput.getText();
                drawingManager.getMaze().setRenderDepth(text.isEmpty() ? 0 : Integer.parseInt(text));
            }
        });


        renderDepthUpButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                int depth = drawingManager.getMaze().getRenderDepth();
                drawingManager.getMaze().setRenderDepth(depth + 1);
                renderDepthInput.setText(String.valueOf(depth + 1));
            }
        });

        renderDepthDownButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                int depth = drawingManager.getMaze().getRenderDepth();
                drawingManager.getMaze().setRenderDepth(depth == 0 ? 0 : depth - 1);
                renderDepthInput.setText(String.valueOf(depth == 0 ? 0 : depth - 1));
            }
        });

        Label label = new Label(null, skin);

        table.add(label).expandY().row();
        table.add(renderDepthUpButton).width(60).height(40).padBottom(10).row();
        table.add(renderDepthInput).width(60).height(30).padBottom(10).row();
        table.add(renderDepthDownButton).width(60).height(40).row();
        table.add(label).expandY();

        return table;
    }

    private Table createExportMenu(Skin skin, Skin skinReversed) {
        Table table = new Table();
        table.setFillParent(true);
        table.top().right();
        table.setWidth(buttonWidth / 4f * 7);
        table.setHeight(buttonHeight);

        TextButton exportButton = new TextButton("EXPORT", skin);
        SelectBox<String> selectBox = new SelectBox<String>(skin);

        exportButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                drawingManager.getMaze().export(exportFormatArray[selectBox.getSelectedIndex()]);
                System.out.println(exportFormatArray[selectBox.getSelectedIndex()]);
            }
        });

        selectBox.setItems(exportFormatArray);
        selectBox.setAlignment(Align.center);

        table.add(exportButton).width(buttonWidth).height(buttonHeight).padRight(1);
        table.add(selectBox).width(buttonWidth / 4f * 3).height(buttonHeight);

        return table;
    }
}
