package com.geekbrains.rpg.game.logic;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.geekbrains.rpg.game.logic.utils.Consumable;
import com.geekbrains.rpg.game.logic.utils.MapElement;
import com.geekbrains.rpg.game.logic.utils.Poolable;
import com.geekbrains.rpg.game.screens.utils.Assets;

public class UsefullThings implements MapElement, Poolable, Consumable {
    private Vector2 position;
    private TextureRegion texture;
    private boolean active;

    public UsefullThings() {
        this.position = new Vector2(0, 0);
    }

    public void setPosition(float x, float y) {
        this.position.set(x, y);
    }

    public Vector2 getPosition() {
        return position;
    }

    public void setup() {
        if (MathUtils.random(1,2) == 1) {
            this.texture = Assets.getInstance().getAtlas().findRegion("coin");
        } else {
            texture = Assets.getInstance().getAtlas().findRegion("apple");
        }
    }

    @Override
    public void consume(GameCharacter gameCharacter) {
        gameCharacter.setHp(gameCharacter.getHpMax());
        active = true;
    }

    @Override
    public void render(SpriteBatch batch, BitmapFont font) {
        batch.draw(texture, position.x - 32*2, position.y - 32*2);
    }

    @Override
    public int getCellX() {
        return (int) (position.x / 80);
    }

    @Override
    public int getCellY() {
        return (int) (position.y / 80);
    }

    @Override
    public boolean isActive() {
        return active;
    }
}
