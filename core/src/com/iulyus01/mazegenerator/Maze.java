package com.iulyus01.mazegenerator;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonWriter;
import com.iulyus01.mazegenerator.algorithms.Algorithm;
import com.iulyus01.mazegenerator.algorithms.Algorithm3D;
import com.iulyus01.mazegenerator.algorithms.HuntAndKill3D;
import com.iulyus01.mazegenerator.algorithms.RecursiveBacktracking;
import com.iulyus01.mazegenerator.api.ApiJsonObject;
import com.iulyus01.mazegenerator.solvers.Solver;
import com.iulyus01.mazegenerator.solvers.WallFollower;
import org.apache.batik.svggen.SVGGraphics2DIOException;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;

import org.apache.batik.svggen.SVGGraphics2D;
import org.apache.batik.dom.GenericDOMImplementation;

public class Maze {

    private Algorithm algorithm;
    private Algorithm newAlgorithm;
    private Algorithm3D algorithm3D;
    private Algorithm3D newAlgorithm3D;
    private Thread thread;

    private Solver solver;

    private final int screenWidth;
    private final int screenHeight;
    private final int margin = 100;

    private float x;
    private float y;
    private float width;
    private float height;
    private int mazeWidth;
    private int mazeHeight;
    private int mazeDepth;
    private int newMazeWidth;
    private int newMazeHeight;
    private int newMazeDepth;
    private int[][] maze;
    private int[][][] maze3D;

    private Color[] renderColors;
    private Info.AlgState state = Info.AlgState.WAITING;
    private float cellSize;
    private int renderDepth = 1;
    private int stepDelay = 100;
    private boolean is3D = false;
    private boolean isChangingDimension = false;

    private float solverStepDelay = 0;
    private final float solverStepDelayMax = 100;

    private boolean solverSelectPointAvailable = false;

    private int mouseOverCellX;
    private int mouseOverCellY;

    public Maze(int screenWidth, int screenHeight, int x, int y, int mazeWidth, int mazeHeight, int mazeDepth) {
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        this.x = x;
        this.y = y;
        this.mazeWidth = Math.max(mazeWidth | 1, 3);
        this.mazeHeight = Math.max(mazeHeight | 1, 3);
        this.mazeDepth = Math.max(mazeDepth | 1, 3);

        this.newMazeWidth = this.mazeWidth;
        this.newMazeHeight = this.mazeHeight;
        this.newMazeDepth = this.mazeDepth;

        init();

        reset();
    }

    private void init() {
        maze = new int[mazeHeight][mazeWidth];
        maze3D = new int[mazeDepth][mazeHeight][mazeWidth];

        setCellSize();

        algorithm = new RecursiveBacktracking(mazeWidth, mazeHeight);
        algorithm3D = new HuntAndKill3D(mazeWidth, mazeHeight, mazeDepth);

        System.out.println("test: init");
        // TODO gotta call init of the solver
        // to initialize the start and finish points
        solver = new WallFollower(mazeWidth, mazeHeight);

        renderColors = new Color[]{Info.colorPurple004, Info.colorPurple008, Info.colorPurple03, Info.colorRed, Info.colorPurple03, Info.colorPurple008, Info.colorPurple004};
    }

    private void setCellSize() {
        cellSize = Math.min((screenWidth - margin * 2f) / mazeWidth, (screenHeight - margin * 2f) / mazeHeight);
        width = cellSize * mazeWidth;
        height = cellSize * mazeHeight;
        x = Info.W / 2f - width / 2f;
        y = screenHeight / 2f - height / 2f;
//        y = margin;
        System.out.println("test: width: " + width + " height: " + height);
    }

    public void update(float delta) {
        if(thread != null && thread.isAlive()) {
            if(is3D) maze3D = algorithm3D.gridToMaze();
            else maze = algorithm.gridToMaze();
        }

        if((!is3D && solver.isShowing()) && solver.isRunning()) {
            if(!solver.isFinished()) {
                solver.setMaze(maze);
                solverStepDelay += delta * 1000;
                if(solverStepDelay >= solverStepDelayMax) {
                    solver.step();
                    solverStepDelay = 0;
                }
            } else {
                solver.setRunning(false);
            }
        }

        int mouseX = Gdx.input.getX();
        int mouseY = Info.H - Gdx.input.getY();
        // computing solverSelectPointAvailable
        if(!is3D && (solver.isSelectingStartPoint() || solver.isSelectingFinishPoint())) {
            if(mouseX > x && mouseY > y && mouseX < x + width && mouseY < y + height) {
                mouseOverCellX = (int) ((mouseX - x) / cellSize);
                mouseOverCellY = mazeHeight - (int) ((mouseY - y) / cellSize) - 1;
                solverSelectPointAvailable = maze[mouseOverCellY][mouseOverCellX] == 0;
            } else {
                mouseOverCellX = -1;
                mouseOverCellY = -1;
                solverSelectPointAvailable = false;
            }
        }
    }

    public void draw(ShapeRenderer shapeRenderer, SpriteBatch batch) {
        if(is3D) {
            for(int z = 0; z < mazeDepth; z++) {
                int depthOffset = z - renderDepth;
                float mazeX = this.x + depthOffset * (cellSize / 4f);
                float mazeY = this.y + depthOffset * (cellSize / 4f);

                int colorIndex;
                if(depthOffset > 0) colorIndex = Math.min(depthOffset, 3);
                else colorIndex = Math.max(depthOffset, -3);
                colorIndex += 3;

                shapeRenderer.setColor(renderColors[colorIndex]);

                for(int y = 0; y < mazeHeight; y++) {
                    for(int x = 0; x < mazeWidth; x++) {
                        if(z - 1 == renderDepth && renderDepth % 2 == 1) {
                            if(maze3D[z][y][x] == 1) continue;
                            float cellX = mazeX + x * cellSize;
                            float cellY = mazeY + (height - y * cellSize) - cellSize;
                            shapeRenderer.rectLine(cellX, cellY, cellX + cellSize, cellY, 1);
                            shapeRenderer.rectLine(cellX + cellSize, cellY, cellX + cellSize, cellY + cellSize, 1);
                            shapeRenderer.rectLine(cellX + cellSize, cellY + cellSize, cellX, cellY + cellSize, 1);
                            shapeRenderer.rectLine(cellX, cellY + cellSize, cellX, cellY, 1);
                            shapeRenderer.setColor(Info.colorPurple);
                            shapeRenderer.rect(cellX + cellSize / 4f * 3 / 2, cellY + cellSize / 4f * 3 / 2, cellSize / 4f, cellSize / 4f);
                            shapeRenderer.setColor(renderColors[colorIndex]);
                            continue;
                        }
                        if(maze3D[z][y][x] != 1) continue;
                        shapeRenderer.rect(mazeX + x * cellSize, mazeY + (height - y * cellSize) - cellSize, cellSize, cellSize);

                    }
                }
            }
        } else {
            for(int y = 0; y < mazeHeight; y++) {
                for(int x = 0; x < mazeWidth; x++) {
                    if(maze[y][x] == 0) continue;
                    shapeRenderer.setColor(Info.colorRed);
                    shapeRenderer.rect(this.x + x * cellSize, this.y + (height - y * cellSize) - cellSize, cellSize, cellSize);
                }
            }

            if(solver.isShowing()) {
                shapeRenderer.setColor(Info.colorGreen);
                shapeRenderer.rect(this.x + solver.getX() * cellSize + cellSize / 4f * 3 / 2, this.y + (height - solver.getY() * cellSize) - cellSize + cellSize / 4f * 3 / 2, cellSize / 4f, cellSize / 4f);

                shapeRenderer.setColor(Info.colorBlue03);
                shapeRenderer.rect(this.x + solver.getStart().first * cellSize + cellSize / 4f * 3 / 2, this.y + (height - solver.getStart().second * cellSize) - cellSize + cellSize / 4f * 3 / 2, cellSize / 4f, cellSize / 4f);

                shapeRenderer.setColor(Info.colorBlue03);
                shapeRenderer.rect(this.x + solver.getFinish().first * cellSize + cellSize / 4f * 3 / 2, this.y + (height - solver.getFinish().second * cellSize) - cellSize + cellSize / 4f * 3 / 2, cellSize / 4f, cellSize / 4f);

                if(solver.isSelectingStartPoint()) {
                    if(!is3D) {
                        if(solverSelectPointAvailable) {
                            shapeRenderer.setColor(Info.colorBlue03);
                            shapeRenderer.rect(this.x + mouseOverCellX * cellSize, this.y + (height - mouseOverCellY * cellSize) - cellSize, cellSize, cellSize);
                        }
                    }
                }
                if(solver.isSelectingFinishPoint()) {
                    if(!is3D) {
                        if(solverSelectPointAvailable) {
                            shapeRenderer.setColor(Info.colorBlue03);
                            shapeRenderer.rect(this.x + mouseOverCellX * cellSize, this.y + (height - mouseOverCellY * cellSize) - cellSize, cellSize, cellSize);
                        }
                    }
                }
            }
        }


    }

    synchronized public void newMaze() {
        if(isChangingDimension) {
            is3D = !is3D;
            isChangingDimension = false;
            System.out.println("test: changing dimensions");
        }

        if(is3D) {
            if(newAlgorithm3D != null && newAlgorithm3D != algorithm3D) algorithm3D = newAlgorithm3D;
            if(algorithm3D.isPaused()) resume();
        } else {
            if(newAlgorithm != null && newAlgorithm != algorithm) algorithm = newAlgorithm;
            if(algorithm.isPaused()) resume();
        }

        // this needs to be here
        // new maze sizes are set in reset()
        reset();

        thread = new Thread() {
            @Override
            public void run() {
                super.run();
                state = Info.AlgState.RUNNING;
                System.out.println("test: thread started");

                if(is3D) {
                    algorithm3D.create(0, 0, 0, stepDelay);
                    maze3D = algorithm3D.gridToMaze();
                } else {
                    algorithm.create(0, 0, stepDelay);
                    maze = algorithm.gridToMaze();
                }

                System.out.println("test: thread finished");
                state = Info.AlgState.WAITING;
            }

        };
        thread.start();

//        int[][] newMaze = algorithm.gridToMaze();

        if(is3D) transitionToNewMaze3D(algorithm3D.gridToMaze());
        else transitionToNewMaze(algorithm.gridToMaze());
    }

    private void transitionToNewMaze(int[][] newMaze) {
        // TODO fancy stuff
        // i can probably delete it tho
        maze = newMaze;
    }

    private void transitionToNewMaze3D(int[][][] newMaze) {
        // TODO fancy stuff
        // i can probably delete it tho
        maze3D = newMaze;
    }

    private void drawToExport(Graphics2D g2d) {
        g2d.setColor(new java.awt.Color(Info.colorRed.r, Info.colorRed.g, Info.colorRed.b));

        int cellSize = 50;
//        int height = mazeHeight * cellSize;
        for(int y = 0; y < mazeHeight; y++) {
            for(int x = 0; x < mazeWidth; x++) {
                if(!is3D) {
                    if(maze[y][x] == 1) g2d.fillRect(x * cellSize, y * cellSize, cellSize, cellSize);
                } else {
                    if(maze3D[renderDepth][y][x] != 1) continue;
                    g2d.fillRect(x * cellSize, y * cellSize, cellSize, cellSize);

                }
            }
        }
    }

    private void exportToPNGAndJPG(String path, String format) {
        int cellSize = 50;
        int width = cellSize * mazeWidth;
        int height = cellSize * mazeHeight;

        BufferedImage bufferedImage = new BufferedImage(width, height, format.equals("png") ? BufferedImage.TYPE_INT_ARGB : BufferedImage.TYPE_INT_RGB);

        Graphics2D g2d = bufferedImage.createGraphics();

        if(format.equals("jpg")) {
            g2d.setColor(new java.awt.Color(1f, 1f, 1f));
            g2d.fillRect(0, 0, width, height);
        }
        drawToExport(g2d);

        g2d.dispose();


        File file = new File(path + "." + format);
        try {
            ImageIO.write(bufferedImage, format, file);
        } catch(IOException e) {
            e.printStackTrace();
            System.out.println("test: export to png or jpg failed");
        }
    }

    private void exportToSVG(String path) {
        DOMImplementation domImpl = GenericDOMImplementation.getDOMImplementation();

        String svgNS = "http://www.w3.org/2000/svg";
        Document document = domImpl.createDocument(svgNS, "svg", null);

        SVGGraphics2D svgGenerator = new SVGGraphics2D(document);

        drawToExport(svgGenerator);

        try {
            OutputStream outputStream = new FileOutputStream(path + ".svg");
            Writer out = new OutputStreamWriter(outputStream, StandardCharsets.UTF_8);
            svgGenerator.stream(out, true);
        } catch(SVGGraphics2DIOException | FileNotFoundException e) {
            e.printStackTrace();
            System.out.println("test: export to svg failed");
        }

        svgGenerator.dispose();
    }

    private void exportToJSON(String path) {
        Json json = new Json();
        json.setOutputType(JsonWriter.OutputType.json);


        ApiJsonObject jsonObject = new ApiJsonObject(null, mazeWidth, mazeHeight);
        if(!is3D) jsonObject.maze = maze;
        else jsonObject.maze = maze3D[renderDepth];

        String jsonText = json.prettyPrint(jsonObject);

        FileOutputStream outputStream;
        try {
            outputStream = new FileOutputStream(path + ".json");
            byte[] strToBytes = jsonText.getBytes();
            outputStream.write(strToBytes);

            outputStream.close();
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    private void exportToTXT(String path) {
        StringBuilder builder = new StringBuilder();

        builder.append(mazeWidth);
        builder.append(' ');
        builder.append(mazeHeight);
        builder.append('\n');
        for(int i = 0; i < mazeHeight; i++) {
            for(int j = 0; j < mazeWidth; j++) {
                if(!is3D) builder.append(maze[i][j]);
                else builder.append(maze3D[renderDepth][i][j]);

                if(j < mazeWidth - 1) builder.append(' ');
            }
            builder.append('\n');
        }

        String text = builder.toString();
        FileOutputStream outputStream;
        try {
            outputStream = new FileOutputStream(path + ".txt");
            byte[] strToBytes = text.getBytes();
            outputStream.write(strToBytes);

            outputStream.close();
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    public void export(String format) {

        new Thread(() -> {
            FileDialog fileDialog = new FileDialog(new Frame(), "Save", FileDialog.SAVE);
            fileDialog.setVisible(true);

            String fileName = fileDialog.getDirectory() + fileDialog.getFile();
            if(fileDialog.getFile() == null) {
                return;
            }

            switch(format) {
                case "PNG":
                    exportToPNGAndJPG(fileName, "png");
                    break;
                case "JPG":
                    exportToPNGAndJPG(fileName, "jpg");
                    break;
                case "SVG":
                    exportToSVG(fileName);
                    break;
                case "JSON":
                    exportToJSON(fileName);
                    break;
                case "TXT":
                    exportToTXT(fileName);
                    break;
            }
        }).start();
    }

    public void clicked() {
        System.out.println("test: " + solver.isSelectingStartPoint() + " " + solver.isSelectingFinishPoint() + " " + solverSelectPointAvailable);
        if(solver.isSelectingStartPoint() && solverSelectPointAvailable) {
            solver.setStart(mouseOverCellX, mouseOverCellY);
            solver.setSelectingStartPoint(false);
        }
        if(solver.isSelectingFinishPoint() && solverSelectPointAvailable) {
            solver.setFinish(mouseOverCellX, mouseOverCellY);
            solver.setSelectingFinishPoint(false);
        }
    }

    public void setStart() {
        solver.setSelectingStartPoint(true);


    }

    public void setFinish() {
        solver.setSelectingFinishPoint(true);


    }

    public void toggleSolver() {
        if(!is3D) {
            solver.setShowing(!solver.isShowing());
        }
    }

    public Info.AlgState getAlgState() {
        return state;
    }

    public Solver getSolver() {
        return solver;
    }

    public int getRenderDepth() {
        return renderDepth;
    }

    public void setAlgorithm(Class<?> cls) {
        if(Algorithm3D.class.isAssignableFrom(cls)) {
            System.out.println("test: this is 3D");
            try {
                newAlgorithm3D = (Algorithm3D) cls.getConstructor(int.class, int.class, int.class).newInstance(mazeWidth, mazeHeight, mazeDepth);
                if(!is3D) isChangingDimension = true;
            } catch(InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                e.printStackTrace();
                newAlgorithm3D = null;
            }
        } else {
            System.out.println("test: this is 2D");
            try {
                newAlgorithm = (Algorithm) cls.getConstructor(int.class, int.class).newInstance(mazeWidth, mazeHeight);
                if(is3D) isChangingDimension = true;
            } catch(InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                e.printStackTrace();
                newAlgorithm = null;
            }
        }


    }

    public void setMazeWidth(int newWidth) {
        if(newWidth < 3) return;
        this.newMazeWidth = newWidth | 1;
    }

    public void setMazeHeight(int newHeight) {
        if(newHeight < 3) return;
        this.newMazeHeight = newHeight | 1;
    }

    public void setMazeDepth(int newDepth) {
        if(newDepth < 3) return;
        this.newMazeDepth = newDepth | 1;
    }

    public void setStepDelay(int stepDelay) {
        this.stepDelay = stepDelay;
    }

    public void setRenderDepth(int depth) {
        this.renderDepth = Math.max(Math.min(depth, mazeDepth - 1), 0);
    }

    public void pause() {
        if(is3D) algorithm3D.pause();
        else algorithm.pause();
        state = Info.AlgState.PAUSED;
    }

    public void resume() {
        if(is3D) {
            if(!algorithm3D.isPaused()) return;
            algorithm3D.resume();

            //noinspection SynchronizeOnNonFinalField
            synchronized(algorithm3D) {
                algorithm3D.notify();
            }
        } else {
            if(!algorithm.isPaused()) return;
            algorithm.resume();

            //noinspection SynchronizeOnNonFinalField
            synchronized(algorithm) {
                algorithm.notify();
            }
        }

        state = Info.AlgState.RUNNING;
    }

    public void reset() {
        if(thread != null) thread.interrupt();

        //noinspection StatementWithEmptyBody
        while(!(is3D ? algorithm3D.isPaused() : algorithm.isPaused()) && thread != null && thread.isAlive()) ;

        if(newMazeWidth != mazeWidth || newMazeHeight != mazeHeight || newMazeDepth != mazeDepth) {
            mazeWidth = newMazeWidth;
            mazeHeight = newMazeHeight;
            mazeDepth = newMazeDepth;
            System.out.println("test: " + mazeWidth + " " + newMazeWidth);
            if(is3D) algorithm3D.setSize(mazeWidth, mazeHeight, mazeDepth);
            else {
                algorithm.setSize(mazeWidth, mazeHeight);
                solver.setSize(mazeWidth, mazeHeight);
            }
            setCellSize();
        }

        if(is3D) {
            algorithm3D.reset();
            maze3D = algorithm3D.gridToMaze();
        } else {
            algorithm.reset();
            maze = algorithm.gridToMaze();
        }
    }

    public void showMaze() {
        if(is3D) {
            for(int z = 0; z < mazeDepth; z++) {
                for(int y = 0; y < mazeHeight; y++) {
                    for(int x = 0; x < mazeWidth; x++) {
                        if(maze3D[z][y][x] == 0) System.out.print(" ");
                        else if(maze3D[z][y][x] == 1) System.out.print("#");
                        else if(maze3D[z][y][x] == 2) System.out.print("/");
                        else if(maze3D[z][y][x] == 3) System.out.print("\\");
                        else if(maze3D[z][y][x] == 4) System.out.print("|");
                    }
                    System.out.println();
                }
                System.out.println();
            }
        } else {
            maze = algorithm.gridToMaze();
            for(int i = 0; i < mazeHeight; i++) {
                for(int j = 0; j < mazeWidth; j++) {
                    System.out.print(maze[i][j] == 0 ? "# " : "* ");
                }
                System.out.println();
            }
        }
    }

}
