package com.geekbrains.rpg.game.logic;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.geekbrains.rpg.game.logic.utils.Consumable;
import com.geekbrains.rpg.game.logic.utils.MapElement;
import com.geekbrains.rpg.game.logic.utils.Poolable;

public class DamageScreen implements Poolable, MapElement {
    private GameController gc;
    private Vector2 position;
    private Vector2 velocity;
    private float time;
    private boolean active;
    public int countDamage;
    private StringBuilder strBuilder;

    public Vector2 getPosition() {
        return position;
    }

    public DamageScreen(GameController gc) {
        this.gc = gc;
        this.position = new Vector2(0, 0);
        this.velocity = new Vector2(0, 0);
        this.strBuilder = new StringBuilder();
        this.active = false;
    }

    public void setup(float x, float y, int countDam) {
        position.set(x, y);
        velocity.set(MathUtils.random(-60.0f, 60.0f), MathUtils.random(0.1f, 60.0f));
        time = 0.0f;
        countDamage = countDam;
        active = true;
    }

    public void update(float dt) {
        time += dt;
        position.mulAdd(velocity, dt);
        if (time > 1.0f) {
            active = false;
        }
    }

    @Override
    public void render(SpriteBatch batch, BitmapFont font) {
        strBuilder.setLength(0);
        strBuilder.append("-"+countDamage);
        font.draw(batch, strBuilder, position.x, position.y);
    }

    @Override
    public int getCellX() {
        return (int) (position.x / Map.CELL_WIDTH);
    }

    @Override
    public int getCellY() {
        return (int) (position.y / Map.CELL_HEIGHT);
    }

    @Override
    public float getY() {
        return position.y;
    }

    @Override
    public boolean isActive() {
        return active;
    }
}
