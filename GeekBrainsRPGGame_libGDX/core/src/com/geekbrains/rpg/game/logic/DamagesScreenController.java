package com.geekbrains.rpg.game.logic;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.geekbrains.rpg.game.logic.utils.ObjectPool;

public class DamagesScreenController extends ObjectPool<DamageScreen> {
    private GameController gc;

    @Override
    protected DamageScreen newObject() {
        return new DamageScreen(this.gc);
    }

    public DamagesScreenController(GameController gc) {
        this.gc = gc;
    }

    public void setup(float x, float y, int countDam) {
        getActiveElement().setup(x, y, countDam);
    }

    public void render(SpriteBatch batch, BitmapFont font) {
        for (int i = 0; i < getActiveList().size(); i++) {
            getActiveList().get(i).render(batch, font);
        }
    }

    public void update(float dt) {
        for (int i = 0; i < getActiveList().size(); i++) {
            getActiveList().get(i).update(dt);
        }
        checkPool();
    }
}
