package com.iulyus01.mazegenerator;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.assets.AssetManager;
import com.iulyus01.mazegenerator.algorithms.*;
import com.iulyus01.mazegenerator.api.ApiHandler;
import com.iulyus01.mazegenerator.screens.LoadingScreen;
import com.iulyus01.mazegenerator.ui.UIStyles;
import spark.Spark;

public class MainClass extends Game {

	public AssetManager assetManager;
	public UIStyles uiStyles;

	@Override
	public void create () {
		assetManager = new AssetManager();
		Info.init();

//		Graphics.DisplayMode mode = Gdx.graphics.getDisplayMode();

//		Gdx.graphics.setUndecorated(true);
//		Gdx.graphics.setWindowedMode(Gdx.graphics.getDisplayMode().width, Gdx.graphics.getDisplayMode().height);
//		Gdx.graphics.setWindowedMode(Gdx.graphics.getDisplayMode().width, Gdx.graphics.getDisplayMode().height);
//		Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());


		setScreen(new LoadingScreen(this));

//		Algorithm alg = new GrowingTreeRandom(11, 11);
//		alg.reset();
//		alg.create(0, 0, 0);
//		alg.gridToMaze();

		Algorithm3D alg3D = new HuntAndKill3D(5, 5, 5);
		alg3D.reset();
		alg3D.create(0, 0, 0, 0);
		int[][][] m = alg3D.gridToMaze();
		for(int z = 0; z < 5; z++) {
			for(int y = 0; y < 5; y++) {
				for(int x = 0; x < 5; x++) {
					if(m[z][y][x] == 0) System.out.print(" ");
					else if(m[z][y][x] == 1) System.out.print("#");
					else if(m[z][y][x] == 2) System.out.print("/");
					else if(m[z][y][x] == 3) System.out.print("\\");
					else if(m[z][y][x] == 4) System.out.print("|");
				}
				System.out.println();
			}
			System.out.println();
		}


		Spark.get("/:alg/:width/:height", (request, response) -> {
			ApiHandler apiHandler = new ApiHandler(request.params(":alg"), request.params(":width"), request.params(":height"));
			return apiHandler.getResponse();
		});

	}

	@Override
	public void render () {
		super.render();
	}
	
	@Override
	public void dispose () {
//		assetManager.dispose();
	}
}
