package com.geekbrains.rpg.game.logic;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.geekbrains.rpg.game.logic.utils.Poolable;
import com.geekbrains.rpg.game.screens.utils.Assets;

import java.util.ArrayList;
import java.util.List;

public class Monster extends GameCharacter implements Poolable {
    private String title;

    @Override
    public boolean isActive() {
        return hp > 0;
    }

    public Monster(GameController gc) {
        super(gc, 30, 40.0f);
        this.changePosition(800.0f, 300.0f);
        //this.charType = CharType.MONSTER;
        this.dst.set(this.position);
        this.visionRadius = 160.0f;
        this.allowedWeaponTypes.add(Weapon.WeaponClass.SWORD);
        this.weapon = gc.getWeaponsController().getOneFromAnyPrototype(allowedWeaponTypes);
        this.countExperience = 0;
    }

    public Monster(GameController gc, String line) {
        super(gc, 30, 40.0f);
        String[] tokens = line.split(",");
        this.charType = charType.fromString(tokens[0].trim());
        this.title = tokens[1].trim();
        this.hpMax = Integer.parseInt(tokens[2].trim());
        this.speed = Integer.parseInt(tokens[3].trim());
        String str = title.trim().toLowerCase();
        this.textures = new TextureRegion(Assets.getInstance().getAtlas().findRegion(str)).split(60, 60);
        this.visionRadius = 160.0f;
        allowedWeaponTypes.add(Weapon.WeaponClass.fromString(tokens[4].trim()));
        this.weapon = gc.getWeaponsController().getOneFromAnyPrototype(allowedWeaponTypes);
        this.countExperience = 0;
    }

    public void generateMe() {
        do {
            changePosition(MathUtils.random(0, 1280), MathUtils.random(0, 720));
        } while (!gc.getMap().isGroundPassable(position));
        hpMax = 80;
        hp = hpMax;
    }

    public void copyFrom(Monster from){
        this.title = from.title;
        this.charType = from.charType;
        this.hpMax = from.hpMax;
        this.hp = from.hp;
        this.speed = from.speed;
        this.textures = from.textures;
        this.damageTimer = 0.0f;
        this.allowedWeaponTypes = from.allowedWeaponTypes;
        this.forgeMe();
    }

    public void forgeMe(){
        float hpBonus = 1.0f;
        for (int i = 0; i < 10; i++) {
            if (MathUtils.random(100) < 15){
                hpBonus += 0.2;
            }
        }
        this.hpMax *= hpBonus;
        this.hp = hpMax;
    }

    @Override
    public void onDeath() {
        super.onDeath();
        gc.getWeaponsController().setup(position.x, position.y);
        gc.getPowerUpsController().setup(position.x, position.y);
    }

    public void update(float dt) {
        super.update(dt);
        stateTimer -= dt;
        // если время вышло, то сбрасываем цель
        if (stateTimer < 0.0f) {
            if (state == State.ATTACK) {
                target = null;
            }
            // решаем нужно ли куда-то идти
            state = State.values()[MathUtils.random(0, 1)];
            if (state == State.MOVE) {
                dst.set(MathUtils.random(Map.MAP_CELLS_WIDTH*Map.CELL_WIDTH), MathUtils.random(Map.MAP_CELLS_HEIGHT*Map.CELL_HEIGHT));
            }
            // заводим таймер (стоим/идем)
            stateTimer = MathUtils.random(2.0f, 5.0f);
        }

        // найду отношения текущего монстра к остальным типам персонажей
        ArrayList arrThisRelations = null;
        ArrayList arrRelations = gc.getMonstersController().getArrRelations();
        for (Object obj : arrRelations){
            // сравниваю класс текущего монстра
            if (this.charType == ((ArrayList) obj).get(0)){
                arrThisRelations = ((ArrayList) obj);
            }
        }

        if (state != State.RETREAT) {
            // получаю всех персонажей
            List<GameCharacter> allCharacters = gc.getAllCharacters();
            for (int i = 0; i < allCharacters.size(); i++) {
                String rel = "";
                // получаю конкретного персонажа
                GameCharacter c = allCharacters.get(i);
                if (this.position.dst(c.getPosition()) < visionRadius) {
                    for (int ch = 2; ch < arrThisRelations.size(); ch = ch + 2) {
                        if (c.charType == arrThisRelations.get(ch)) {
                            rel = (String) arrThisRelations.get(ch + 1);
                            if (rel.equals("-1")) {
                                state = State.ATTACK;
                                target = c;//gc.getHero();
                                stateTimer = 10.0f;
                            }
                            break;
                        }
                    }
                }
            }
        }
        // если здоровья меньше 20 процентов - отступаем
        if (hp < hpMax * 0.2 && state != State.RETREAT) {
            state = State.RETREAT;
            stateTimer = 1.0f;
            dst.set(position.x + MathUtils.random(100, 200) * Math.signum(position.x - lastAttacker.position.x),
                    position.y + MathUtils.random(100, 200) * Math.signum(position.y - lastAttacker.position.y));
        }
    }
}
