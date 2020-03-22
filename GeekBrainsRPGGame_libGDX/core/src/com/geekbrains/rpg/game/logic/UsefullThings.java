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
    private int typeThings;

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
            this.typeThings = 1;
        } else {
            texture = Assets.getInstance().getAtlas().findRegion("apple");
            this.typeThings = 2;
        }
        this.active = true;
    }

    @Override
    public void consume(GameCharacter gameCharacter) {
        if (typeThings == 1){
            gameCharacter.gc.getHero().addCoins(MathUtils.random(1,5));
            active = false;
        }
        if (typeThings == 2) {
            gameCharacter.setHp(gameCharacter.getHp() + MathUtils.random(1,10));
            active = false;
        }
    }

    @Override
    public void render(SpriteBatch batch, BitmapFont font) {
        batch.draw(texture, position.x- 32*2, position.y- 32*2);//
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
