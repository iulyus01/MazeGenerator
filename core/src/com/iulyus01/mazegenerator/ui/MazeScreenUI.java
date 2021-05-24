package com.iulyus01.mazegenerator.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
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
    private final ShapeRenderer shapeRenderer;

    private Label runningLabel;
    private final TextField.TextFieldFilter numberFilter = (textField, c) -> c >= '0' && c <= '9';

    private final String[] algorithmArray = new String[]{
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
    private final String[] algorithm3DArray = new String[]{
            "Hunt-and-Kill "
    };
    private final String[] exportFormatArray = new String[]{
            "PNG",
            "JPG",
            "SVG",
            "JSON",
            "TXT"
    };
    private final String[] toggleSolverArray = new String[]{
            "Show solver",
            "Hide solver"
    };

    private final int menuTopSize;
    private final float menuSideSize;
    private final float buttonWidth;
    private final float buttonHeight;
    private final int padding = 5;

    private int labelCounter;
    private float labelCounterDelay;
    private final float labelCounterDelayMax = .4f;

    public MazeScreenUI(MainClass app, DrawingManager drawingManager, SpriteBatch batch, ShapeRenderer shapeRenderer, Viewport viewport, int menuTopSize, float menuSideSize) {
        this.app = app;
        this.drawingManager = drawingManager;
        this.shapeRenderer = shapeRenderer;

        this.menuTopSize = menuTopSize;
        this.menuSideSize = menuSideSize;

        this.buttonWidth = 100;
//        this.buttonWidth = menuSideSize / 4f;
        this.buttonHeight = 40;
//        this.buttonHeight = menuTopSize;

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
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(Info.colorCyan.r, Info.colorCyan.g, Info.colorCyan.b, .2f);
        shapeRenderer.rect(0, 0, menuSideSize, Info.H);
        shapeRenderer.rect(menuSideSize, Info.H - menuTopSize, Info.W - menuSideSize * 2, menuTopSize);
        shapeRenderer.rect(Info.W - menuSideSize, 0, menuSideSize, Info.H);
        shapeRenderer.end();

        stage.draw();

    }

    public void dispose() {

    }

    private void createUI() {
        Skin skin = app.assetManager.get("FlatSkin.json", Skin.class);
        Skin skinReversed = app.assetManager.get("FlatSkinReversed.json", Skin.class);

        Table table = new Table();
        table.setFillParent(true);
        table.left().top();

        table.add(createLeftMenu(skin, skinReversed)).top().width(menuSideSize);
        table.add(createTopMenu(skin, skinReversed)).top().growX();
        table.add(createRightMenu(skin, skinReversed)).top().width(menuSideSize);

        stage.addActor(table);
        stage.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                if(x > menuSideSize && x < Info.W - menuSideSize && y > menuTopSize)
                    drawingManager.getMaze().clicked();
            }
        });

    }

    private Table createLeftMenu(Skin skin, Skin skinReversed) {
        Table table = new Table();

        runningLabel = new Label("", app.uiStyles.getLabelStyle());

        table.add(createBackTable(skin)).fillX().height(buttonHeight).top().pad(padding, 0, 50, 0).row();
        table.add(createDimensionsTable(skin, skinReversed)).fillX().height(buttonHeight).padBottom(padding).row();
        table.add(createDelayTable(skin, skinReversed)).fillX().height(buttonHeight).padBottom(padding).row();
        table.add(createAlgorithmTable(skin, skinReversed)).fillX().height(buttonHeight).padBottom(padding).row();
        table.add(runningLabel).height(buttonHeight * 2);

        return table;
    }

    private Table createTopMenu(Skin skin, Skin skinReversed) {
        Table table = new Table();

        TextButton showMazeButton = new TextButton("Test", skin);
        TextButton resetButton = new TextButton("Reset", skin);
        TextButton newMazeButton = new TextButton("New Maze", app.uiStyles.getTextButtonStyle());
        TextButton pauseButton = new TextButton("Pause", skin);
        TextButton resumeButton = new TextButton("Resume", skin);


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


        Label label = new Label(null, skin);

        table.add(label).expandX();
        table.add(showMazeButton).width(buttonWidth).height(menuTopSize).padRight(padding);
        table.add(resetButton).width(buttonWidth).height(menuTopSize).padRight(padding);
        table.add(newMazeButton).width(buttonWidth * 1.5f).height(menuTopSize).padRight(padding);
        table.add(pauseButton).width(buttonWidth).height(menuTopSize).padRight(padding);
        table.add(resumeButton).width(buttonWidth).height(menuTopSize).padRight(padding);
        table.add(label).expandX().row();
        table.top();

        return table;
    }

    private Table createRightMenu(Skin skin, Skin skinReversed) {
        Table table = new Table();

        table.add(createExportTable(skin)).fillX().height(buttonHeight).pad(padding, 0, padding, 0).row();
        table.add(createShowSolverTable(skin)).fillX().height(buttonHeight).padBottom(padding).row();
        table.add(createSetLocationsTable(skin)).fillX().height(buttonHeight).padBottom(padding).row();
        table.add(createStartSolverButton(skin)).fillX().height(buttonHeight).padBottom(buttonHeight + padding * 2).row();
        table.add(createRenderDepthTable(skin)).fillX().padBottom(padding).row();

        return table;

    }

    private Actor createBackTable(Skin skin) {
        Table table = new Table();

        TextButton backButton = new TextButton("BACK", skin);
//        backButton.setWidth(buttonWidth);
//        backButton.setHeight(buttonHeight);
//        backButton.setX(0);
//        backButton.setY(Info.H - buttonHeight);
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                app.setScreen(new MainMenuScreen(app));
            }
        });

        float part = (menuSideSize - padding * 2);
        table.add(backButton).width(part).growY().padLeft(padding).padRight(padding);

        return table;
    }

    private Actor createDimensionsTable(Skin skin, Skin skinReversed) {
        Table table = new Table();

        Label widthLabel = new Label("Maze width", app.uiStyles.getLabelStyle());
        Label heightLabel = new Label("Maze height", skinReversed);
        Label depthLabel = new Label("Maze depth", skinReversed);
        TextField widthInput = new TextField("11", skin);
        TextField heightInput = new TextField("11", skin);
        TextField depthInput = new TextField("11", skin);


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

        float part = (menuSideSize - padding * 4) / 3f;
        table.add(widthInput).width(part).growY().padLeft(padding).padRight(padding);
        table.add(heightInput).width(part).growY().padRight(padding);
        table.add(depthInput).width(part).growY().padRight(padding);

        return table;
    }

    private Actor createDelayTable(Skin skin, Skin skinReversed) {
        Table table = new Table();

        Label delayLabel = new Label("delay", skinReversed);
        TextField delayInput = new TextField("100", skin);

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

        float part = (menuSideSize - padding * 3) / 2f;
        table.add(delayLabel).width(part).growY().padLeft(padding).padRight(padding);
        table.add(delayInput).width(part).growY().padRight(padding);

        return table;
    }

    private Actor createAlgorithmTable(Skin skin, Skin skinReversed) {
        Table table = new Table();

        CheckBox box = new CheckBox("3D", skinReversed);
        SelectBox<String> selectBox = new SelectBox<>(skin);

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

        float part = (menuSideSize - padding * 3) / 4f;
        table.add(box).width(part).growY().padLeft(padding).padRight(padding);
        table.add(selectBox).width(part * 3).growY().padRight(padding);

        return table;
    }

    private Actor createExportTable(Skin skin) {
        Table table = new Table();

        TextButton exportButton = new TextButton("EXPORT", skin);
        SelectBox<String> selectBox = new SelectBox<>(skin);

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

        float part = (menuSideSize - padding * 3) / 5f;
        table.add(exportButton).width(part * 3).growY().padLeft(padding).padRight(padding);
        table.add(selectBox).width(part * 2).growY().padRight(padding);

        return table;
    }

    private Actor createShowSolverTable(Skin skin) {
        Table table = new Table();

        TextButton showSolverButton = new TextButton("Show solver", skin);

        showSolverButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                drawingManager.getMaze().toggleSolver();
                boolean isShowing = drawingManager.getMaze().getSolver().isShowing();
                showSolverButton.setText(toggleSolverArray[isShowing ? 1 : 0]);
            }
        });

        float part = (menuSideSize - padding * 2);
        table.add(showSolverButton).width(part).growY().padLeft(padding).padRight(padding);

        return table;
    }

    private Actor createSetLocationsTable(Skin skin) {
        Table table = new Table();

        TextButton setStartButton = new TextButton("S", skin);
        TextButton setStopButton = new TextButton("F", skin);

        setStartButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                drawingManager.getMaze().setStart();
            }
        });

        setStopButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                drawingManager.getMaze().setFinish();
            }
        });


        float part = (menuSideSize - padding * 3) / 2f;
        table.add(setStartButton).width(part).growY().padLeft(padding).padRight(padding);
        table.add(setStopButton).width(part).growY().padRight(padding);

        return table;
    }

    private Actor createStartSolverButton(Skin skin) {
        Table table = new Table();

        TextButton startSolverButton = new TextButton("Start", skin);
        TextButton resetSolverButton = new TextButton("Reset", skin);

        startSolverButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                drawingManager.getMaze().getSolver().setRunning(true);
            }
        });

        resetSolverButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                drawingManager.getMaze().getSolver().reset();

            }
        });

        float part = (menuSideSize - padding * 3) / 2f;
        table.add(startSolverButton).width(part).growY().padLeft(padding).padRight(padding);
        table.add(resetSolverButton).width(part).growY().padRight(padding);

        return table;
    }

    private Actor createRenderDepthTable(Skin skin) {
        Table table = new Table();

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

        table.add(renderDepthUpButton).width(60).height(40).padBottom(padding).row();
        table.add(renderDepthInput).width(60).height(30).padBottom(padding).row();
        table.add(renderDepthDownButton).width(60).height(40).row();

        return table;
    }
}
