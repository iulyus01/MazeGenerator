package com.iulyus01.mazegenerator.desktop;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.badlogic.gdx.backends.lwjgl.LwjglFrame;
import com.iulyus01.mazegenerator.MainClass;

public class DesktopLauncher {
	public static void main (String[] arg) {
		Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
		config.setMaximized(true);
		config.setWindowIcon("icon128.png", "icon32.png", "icon16.png");

		new Lwjgl3Application(new MainClass(), config);
	}
}
