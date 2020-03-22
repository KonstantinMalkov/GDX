package com.geekbrains.rpg.game.logic;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.geekbrains.rpg.game.screens.ScreenManager;

import java.util.ArrayList;
import java.util.List;

public class GameController {
    private ProjectilesController projectilesController;
    private PowerUpsController powerUpsController;
    private MonstersController monstersController;
    private WeaponsController weaponsController;
    private SpecialEffectsController specialEffectsController;
    private List<GameCharacter> allCharacters;
    private Map map;
    private Hero hero;
    private Vector2 tmp, tmp2;
    private Vector2 mouse;
    private float worldTimer;

    public Vector2 getMouse() {
        return mouse;
    }

    public float getWorldTimer() {
        return worldTimer;
    }

    public SpecialEffectsController getSpecialEffectsController() {
        return specialEffectsController;
    }

    public PowerUpsController getPowerUpsController() {
        return powerUpsController;
    }

    public List<GameCharacter> getAllCharacters() {
        return allCharacters;
    }

    public Hero getHero() {
        return hero;
    }

    public MonstersController getMonstersController() {
        return monstersController;
    }

    public Map getMap() {
        return map;
    }

    public ProjectilesController getProjectilesController() {
        return projectilesController;
    }

    public WeaponsController getWeaponsController() {
        return weaponsController;
    }

    public GameController() {
        this.allCharacters = new ArrayList<>();
        this.projectilesController = new ProjectilesController(this);
        this.weaponsController = new WeaponsController(this);
        this.powerUpsController = new PowerUpsController(this);
        this.hero = new Hero(this);
        this.map = new Map();
        this.monstersController = new MonstersController(this, 25);
        this.specialEffectsController = new SpecialEffectsController();
        this.tmp = new Vector2(0, 0);
        this.tmp2 = new Vector2(0, 0);
        this.mouse = new Vector2(0, 0);
    }

    public void update(float dt) {
        mouse.set(Gdx.input.getX(), Gdx.input.getY());
        ScreenManager.getInstance().getViewport().unproject(mouse);

        worldTimer += dt;
        allCharacters.clear();
        allCharacters.add(hero);
        allCharacters.addAll(monstersController.getActiveList());

        hero.update(dt);
        monstersController.update(dt);
        checkCollisions();
        projectilesController.update(dt);
        weaponsController.update(dt);
        powerUpsController.update(dt);
        specialEffectsController.update(dt);
    }

    public void collideUnits(GameCharacter u1, GameCharacter u2) {
        if (u1.getArea().overlaps(u2.getArea())) {
            tmp.set(u1.getArea().x, u1.getArea().y);
            tmp.sub(u2.getArea().x, u2.getArea().y);
            float halfInterLen = ((u1.getArea().radius + u2.getArea().radius) - tmp.len()) / 2.0f;
            tmp.nor();

            tmp2.set(u1.getPosition()).mulAdd(tmp, halfInterLen);
            if (map.isGroundPassable(tmp2)) {
                u1.changePosition(tmp2);
            }

            tmp2.set(u2.getPosition()).mulAdd(tmp, -halfInterLen);
            if (map.isGroundPassable(tmp2)) {
                u2.changePosition(tmp2);
            }
        }
    }

    public void checkCollisions() {
        for (int i = 0; i < monstersController.getActiveList().size(); i++) {
            Monster m = monstersController.getActiveList().get(i);
            collideUnits(hero, m);
        }
        for (int i = 0; i < monstersController.getActiveList().size() - 1; i++) {
            Monster m = monstersController.getActiveList().get(i);
            for (int j = i + 1; j < monstersController.getActiveList().size(); j++) {
                Monster m2 = monstersController.getActiveList().get(j);
                collideUnits(m, m2);
            }
        }
        for (int i = 0; i < weaponsController.getActiveList().size(); i++) {
            Weapon w = weaponsController.getActiveList().get(i);
            if (hero.getPosition().dst(w.getPosition()) < 20) {
                w.consume(hero);
            }
        }

        for (int i = 0; i < projectilesController.getActiveList().size(); i++) {
            Projectile p = projectilesController.getActiveList().get(i);
            if (!map.isAirPassable(p.getCellX(), p.getCellY())) {
                p.deactivate();
                continue;
            }
            if (p.getPosition().dst(hero.getPosition()) < 18 && p.getOwner() != hero) {
                p.deactivate();
                hero.takeDamage(p.getOwner(), p.getDamage());
            }
            for (int j = 0; j < monstersController.getActiveList().size(); j++) {
                Monster m = monstersController.getActiveList().get(j);
                if (p.getOwner() == m) {
                    continue;
                }
                if (p.getPosition().dst(m.getPosition()) < 18) {
                    p.deactivate();
                    m.takeDamage(p.getOwner(), p.getDamage());
                }
            }
        }

        for (int i = 0; i < powerUpsController.getActiveList().size(); i++) {
            PowerUp p = powerUpsController.getActiveList().get(i);
            if (p.getPosition().dst(hero.getPosition()) < 24) {
                p.consume(hero);
            }
        }
    }
}
