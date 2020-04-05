package com.geekbrains.rpg.game.logic;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.geekbrains.rpg.game.logic.utils.ObjectPool;
import com.geekbrains.rpg.game.screens.utils.Assets;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;

public class MonstersController extends ObjectPool<Monster> {
    private GameController gc;
    private float innerTimer;
    private float spawnPeriod;
    private ArrayList<Monster> prototypes;
    private ArrayList<ArrayList<Object>> arrRelations;

    @Override
    protected Monster newObject() {
        return new Monster(gc);
    }

    public MonstersController(GameController gc, int initialCount) {
        this.gc = gc;
        this.spawnPeriod = 5.0f;
        this.loadPrototypes();
        for (int i = 0; i < initialCount; i++) {
            generate();
        }
        // загрузим отношения между монстрами
        loadRelations();
    }

    public void generate(){
        Monster out = getActiveElement();
        out.copyFrom(prototypes.get(MathUtils.random(0,prototypes.size()-1)));
        int x = MathUtils.random(Map.CELL_WIDTH, gc.getMap().getHeightLimit()-Map.CELL_WIDTH);
        int y = MathUtils.random(Map.CELL_HEIGHT, gc.getMap().getHeightLimit()-Map.CELL_HEIGHT);
        out.changePosition(x,y);
        out.weapon = gc.getWeaponsController().getOneFromAnyPrototype(out.allowedWeaponTypes);
        out.countExperience = 0;
    }

    public void update(float dt) {
        innerTimer += dt;
        if (innerTimer > spawnPeriod) {
            innerTimer = 0.0f;
            generate();
        }
        for (int i = 0; i < getActiveList().size(); i++) {
            getActiveList().get(i).update(dt);
        }
        checkPool();
    }

    public void loadPrototypes(){
        prototypes = new ArrayList();
        BufferedReader reader = null;
        try {
            reader = Gdx.files.internal("data/monsters.csv").reader(8192);
            reader.readLine();
            String line = null;
            while ((line = reader.readLine()) != null){
                prototypes.add(new Monster(gc, line));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void loadRelations(){

        this.arrRelations = new ArrayList();

        BufferedReader reader = null;
        try {
            reader = Gdx.files.internal("data/relations.csv").reader(8192);
            String line = null;
            // считываю первую строку с названиями монстров
            line = reader.readLine();
            String[] monstersNames = line.split(",");
            for (String s : monstersNames) {
                // если первое название ZERO (не монстр), то пропустим
                if (s.trim().equals("ZERO")) {continue;}
                // добавляю массив в котором первый элемент, тип монстра
                ArrayList<Object> arr = new ArrayList();
                arr.add(GameCharacter.CharType.fromString(s));
                arr.add("null");
                arrRelations.add(arr);
            }
            // далее идут отношения (-1 атака, 0 нейтралитет)
            while ((line = reader.readLine()) != null){
                // разделим строку (GOBLIN,-1 ,0 ,0 ,0 ,-1)
                String[] monstersRelations = line.split(",");

                GameCharacter.CharType subMonsterClass = GameCharacter.CharType.fromString(monstersRelations[0].trim());
                ArrayList<Object> arr = new ArrayList();
                for (int i = 1; i < monstersRelations.length; i++) {
                    this.arrRelations.get(i-1).add(subMonsterClass);
                    this.arrRelations.get(i-1).add(monstersRelations[i].trim());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public ArrayList<ArrayList<Object>> getArrRelations() {
        return arrRelations;
    }
}
