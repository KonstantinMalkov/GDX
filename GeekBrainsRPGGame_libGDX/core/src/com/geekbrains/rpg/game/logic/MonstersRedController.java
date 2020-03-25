package com.geekbrains.rpg.game.logic;

import com.geekbrains.rpg.game.logic.utils.ObjectPool;

public class MonstersRedController extends ObjectPool<MonsterRed> {
    private GameController gc;
    private float innerTimer;
    private float spawnPeriod;// время порождения новых монстров

    @Override
    protected MonsterRed newObject() {
        return new MonsterRed(gc);
    }

    public MonstersRedController(GameController gc, int initialCount) {
        this.gc = gc;
        this.spawnPeriod = 5.0f;
        for (int i = 0; i < initialCount; i++) {
            getActiveElement().generateMe();
        }
    }

    public void update(float dt) {
        innerTimer += dt;
        if (innerTimer > spawnPeriod) {
            innerTimer = 0.0f;
            getActiveElement().generateMe();
        }
        for (int i = 0; i < getActiveList().size(); i++) {
            getActiveList().get(i).update(dt);
        }
        checkPool();
    }
}
