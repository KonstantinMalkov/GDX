package com.geekbrains.rpg.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.geekbrains.rpg.GeekBrainsRPGGame;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width = 1180;
		config.height = 620;
		new LwjglApplication(new GeekBrainsRPGGame(), config);
	}
}
