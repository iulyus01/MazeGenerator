package com.iulyus01.mazegenerator.ui;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.iulyus01.mazegenerator.Info;

import java.util.ArrayList;
import java.util.List;

public class DynamicBackground {

    private final List<Square> squareList;

    public DynamicBackground(int squaresNr, float alpha) {
        squareList = new ArrayList<>();

        for(int i = 0; i < squaresNr; i++) {
            squareList.add(new Square(alpha));
        }
    }

    public void update(float delta) {
        for(Square square : squareList) square.update(delta);
    }

    public void draw(ShapeRenderer shapeRenderer) {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(Info.colorRed);
        for(Square square : squareList) square.draw(shapeRenderer);
        shapeRenderer.end();
    }

    private static class Square {
        private final List<Float> pathX;
        private final List<Float> pathY;

        private float x;
        private float y;
        private final float width;
        private final float height;
        private final float speed;
        private final float alpha;

        private final int length;
        private int direction;

        private final int[] dirX = new int[]{1, 0, -1, 0};
        private final int[] dirY = new int[]{0, -1, 0, 1};

        Square(float alpha) {
            this.alpha = alpha;
            x = (float) Math.random() * Info.W;
            y = (float) Math.random() * Info.H;
            width = 8;
            height = 8;
            speed = (float) Math.random() * 50 + 450;
            length = (int) (Math.random() * 5 + 35);

            pathX = new ArrayList<>();
            pathY = new ArrayList<>();

            direction = (int) (Math.random() * 4);
        }

        public void update(float delta) {
            // move
            x += dirX[direction] * (speed * delta);
            y += dirY[direction] * (speed * delta);
            pathX.add(x);
            pathY.add(y);
            if(pathX.size() > length) {
                pathX.remove(0);
                pathY.remove(0);
            }

            if(Math.random() < .03) {
                direction = (direction + ((int) (Math.random() * 3) - 1)) % 4;
                if(direction == -1) direction = 3;
            }

            // collision check
//            if(x + dirX[direction] * (speed * delta) - width / 2 < 0 ||
//                    x + dirX[direction] * (speed * delta) + width / 2 > Info.W ||
//                    y + dirY[direction] * (speed * delta) - height / 2 < 0 ||
//                    y + dirY[direction] * (speed * delta) + height / 2 > Info.H)
            if(x - width / 2 < 0 ||
                    x + width / 2 > Info.W ||
                    y - height / 2 < 0 ||
                    y + height / 2 > Info.H)
                direction = (direction + 2) % 4;

        }

        public void draw(ShapeRenderer shapeRenderer) {
            for(int i = 0; i < pathX.size() - 2; i++) {
                float alpha = ((float) i / length) / 2f;
                alpha *= this.alpha;
                shapeRenderer.setColor(Info.colorRed.r, Info.colorRed.g, Info.colorRed.b, alpha);
                float rectWidth = pathX.get(i + 2) - pathX.get(i);
                float rectHeight = pathY.get(i + 2) - pathY.get(i);
                rectWidth += width * (rectWidth < 0 ? -1 : 1);
                rectHeight += height * (rectHeight < 0 ? -1 : 1);
                float rectX = pathX.get(i) + width / 2f * (rectWidth < 0 ? 1 : -1);
                float rectY = pathY.get(i) + height / 2f * (rectHeight < 0 ? 1 : -1);
                shapeRenderer.rect(rectX, rectY, rectWidth, rectHeight);
            }

            shapeRenderer.setColor(Info.colorRed.r, Info.colorRed.g, Info.colorRed.b, alpha);
            shapeRenderer.rect(x - width / 2f, y - height / 2f, width, height);

        }
    }
}
