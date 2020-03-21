package com.geekbrains.rpg.game.logic;

import com.geekbrains.rpg.game.logic.utils.ObjectPool;

public class UsefullThingsController extends ObjectPool<UsefullThings> {
    private GameController gc;

    @Override
    protected UsefullThings newObject() {
        return new UsefullThings();
    }

    public UsefullThingsController(GameController gc) {
        this.gc = gc;
    }

    public void setup(float x, float y) {
        UsefullThings u = getActiveElement();

        u.setup();
        u.setPosition(x, y);
    }

    public void update(float dt) {
        checkPool();
    }

}
