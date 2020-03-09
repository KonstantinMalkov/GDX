package com.geekbrains.rpg;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

public class GeekBrainsRPGGame extends ApplicationAdapter {
	private SpriteBatch batch;
	private Texture textureGrass;
	private Hero hero;

	@Override
	public void create () {
		batch = new SpriteBatch();
		hero = new Hero();
		textureGrass = new Texture("grass.png");
	}

	@Override
	public void render () {
		float dt = Gdx.graphics.getDeltaTime();
		update(dt);
		Gdx.gl.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.begin();
		for (int i = 0; i < 16; i++) {
			for (int j = 0; j < 9; j++) {
				batch.draw(textureGrass, i * 80, j * 80);
			}
		}
		hero.render(batch);
		batch.end();
	}

	public void update(float dt){
		hero.update(dt);
	}

	@Override
	public void dispose () {
		batch.dispose();
	}
}
