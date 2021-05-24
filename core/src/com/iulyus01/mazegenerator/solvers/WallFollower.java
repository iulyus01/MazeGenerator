package com.iulyus01.mazegenerator.solvers;

import com.iulyus01.mazegenerator.Info;

import java.util.Map;

public class WallFollower extends Solver {

    private int lastStep;

    private final Map<Integer, Integer> RIGHT_OF = Map.of(N, E, E, S, S, W, W, N);
    private final Map<Integer, Integer> LEFT_OF = Map.of(N, W, W, S, S, E, E, N);

    public WallFollower(int mazeWidth, int mazeHeight) {
        super(mazeWidth, mazeHeight);

    }

    @Override
    public void step() {
        if(isFinished) return;
        if(x == finishPoint.first && y == finishPoint.second) {
            isFinished = true;
            return;
        }

        int nextX;
        int nextY;
        if(lastStep == 0) {
            for(int cardinal : cardinals) {
                nextX = x + DX.get(cardinal);
                nextY = y + DY.get(cardinal);


//                System.out.println("test: next " + cardinal + ": " + nextX + " " + nextY + " " + maze[nextY][nextX]);
                if(maze[nextY][nextX] == 1) continue;

                x = nextX;
                y = nextY;

                lastStep = cardinal;
                break;
            }
//            System.out.println("test: next: " + x + " " + y + " " + maze[nextY][nextX]);
        } else {
            int dir = RIGHT_OF.get(lastStep);
            nextX = x + DX.get(dir);
            nextY = y + DY.get(dir);
            for(int i = 0; maze[nextY][nextX] == 1 && i < 4; i++) {
                dir = LEFT_OF.get(dir);
                nextX = x + DX.get(dir);
                nextY = y + DY.get(dir);
            }

//            System.out.println("test: next " + dir + ": " + nextX + " " + nextY + " " + maze[nextY][nextX]);
            if(maze[nextY][nextX] == 1) return;
            x = nextX;
            y = nextY;

            lastStep = dir;

        }
        pathList.add(new Info.Pair<>(x, y));
    }
}
