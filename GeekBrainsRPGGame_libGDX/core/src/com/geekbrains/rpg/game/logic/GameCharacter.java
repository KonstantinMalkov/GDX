package com.geekbrains.rpg.game.logic;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.geekbrains.rpg.game.logic.utils.MapElement;
import com.geekbrains.rpg.game.screens.utils.Assets;

import java.util.ArrayList;
import java.util.List;

public abstract class GameCharacter implements MapElement {
    public enum State {
        IDLE, MOVE, ATTACK, PURSUIT, RETREAT
    }

    static final int WIDTH = 60;
    static final int HEIGHT = 60;

    protected GameController gc;

    protected TextureRegion[][] textures;
    protected TextureRegion textureHp;

    protected State state;
    protected float stateTimer;

    protected GameCharacter lastAttacker;
    protected GameCharacter target;

    protected Vector2 position;
    protected Vector2 dst;
    protected Vector2 tmp;
    protected Vector2 tmp2;

    protected Circle area;

    protected float lifetime;
    protected float attackTime;
    protected float walkTime;
    protected float timePerFrame;
    protected float damageTimer;

    protected float visionRadius;
    protected float speed;
    protected int hp, hpMax;
    protected int coins;

    protected Weapon weapon;
    protected CharType charType;

    protected int countExperience;

    protected List<Weapon.WeaponClass> allowedWeaponTypes;

    public enum CharType{
        HUMAN,UNDEAD,GOBLIN,DEMON,ELF;

        public static CharType fromString(String in) {
            switch (in) {
                case "HUMAN":
                    return HUMAN;
                case "UNDEAD":
                    return UNDEAD;
                case "GOBLIN":
                    return GOBLIN;
                case "DEMON":
                    return DEMON;
                case "ELF":
                    return ELF;
                default:
                    throw new RuntimeException("Unknown monster class");
            }
        }

    }

    public CharType getCharType() {
        return charType;
    }

    public void addCoins(int amount) {
        coins += amount;
    }

    @Override
    public float getY() {
        return position.y;
    }

    public int getCellX() {
        return (int) (position.x / Map.CELL_WIDTH);
    }

    public int getCellY() {
        return (int) (position.y / Map.CELL_HEIGHT);
    }

    public Weapon getWeapon() {
        return weapon;
    }

    public int restoreHp(float percent) {
        int amount = (int) (hpMax * percent);
        if ((hp + amount) > hpMax) {
            amount = hpMax - hp;
        }
        hp += amount;
        return amount;
    }

    public void changePosition(float x, float y) {
        position.set(x, y);
        if (position.x < 0.1f) {
            position.x = 0.1f;
        }
        if (position.y < 0.1f) {
            position.y = 0.1f;
        }
        if (position.x > Map.MAP_CELLS_WIDTH * 80 - 1) {
            position.x = Map.MAP_CELLS_WIDTH * 80 - 1;
        }
        if (position.y > Map.MAP_CELLS_HEIGHT * 80 - 1) {
            position.y = Map.MAP_CELLS_HEIGHT * 80 - 1;
        }
        area.setPosition(position.x, position.y);
    }

    public void changePosition(Vector2 newPosition) {
        changePosition(newPosition.x, newPosition.y);
    }

    public Vector2 getPosition() {
        return position;
    }

    public Circle getArea() {
        return area;
    }

    public boolean isAlive() {
        return hp > 0;
    }

    public GameCharacter(GameController gc, int hpMax, float speed) {
        this.gc = gc;
        this.textureHp = Assets.getInstance().getAtlas().findRegion("hp");
        this.tmp = new Vector2(0.0f, 0.0f);
        this.tmp2 = new Vector2(0.0f, 0.0f);
        this.dst = new Vector2(0.0f, 0.0f);
        this.position = new Vector2(0.0f, 0.0f);
        this.area = new Circle(0.0f, 0.0f, 15);
        this.hpMax = hpMax;
        this.hp = this.hpMax;
        this.speed = speed;
        this.state = State.IDLE;
        this.stateTimer = 1.0f;
        this.timePerFrame = 0.2f;
        this.target = null;
        this.allowedWeaponTypes = new ArrayList();
        this.countExperience = 0;
    }

    public int getCurrentFrameIndex() {
        return (int) (walkTime / timePerFrame) % textures[0].length;
    }

    public void update(float dt) {
        lifetime += dt;
        damageTimer -= dt;
        if (damageTimer <= 0.0f) {
            damageTimer = 0.0f;
            // если урона нет, то каждые несколько секунд герои восстанавливаются
            if(hp < hpMax &&  lifetime > 1.0f) {
                hp += 1;
                lifetime = 0.0f;
            }
        }
        if (state == State.ATTACK) {
            dst.set(target.getPosition());
        }
        if (state == State.MOVE || state == State.RETREAT || (state == State.ATTACK && this.position.dst(target.getPosition()) > weapon.getRange() - 10)) {
            moveToDst(dt);
        }
        if (state == State.ATTACK && this.position.dst(target.getPosition()) < weapon.getRange()) {
            attackTime += dt;
            if (attackTime > weapon.getSpeed()) {
                attackTime = 0.0f;
                if (weapon.getType() == Weapon.Type.MELEE) {
                    tmp.set(target.position).sub(position);
                    gc.getSpecialEffectsController().setupSwordSwing(position.x, position.y, tmp.angle());
                    int plusToDamage = this.gc.getPlusToDamageFromExperience(this.countExperience);
                    target.takeDamage(this, weapon.generateDamage(plusToDamage));
                }
                if (weapon.getType() == Weapon.Type.RANGED && target != null) {
                    int plusToDamage = this.gc.getPlusToDamageFromExperience(this.countExperience);
                    gc.getProjectilesController().setup(this, position.x, position.y, target.getPosition().x, target.getPosition().y, weapon.generateDamage(plusToDamage));
                }
            }
        }
        slideFromWall(dt);
    }

    public void moveToDst(float dt) {
        tmp.set(dst).sub(position).nor().scl(speed);
        tmp2.set(position);
        walkTime += dt;
        if (position.dst(dst) > speed * dt) {
            changePosition(position.x + tmp.x * dt, position.y + tmp.y * dt);
        } else {
            changePosition(dst);
            state = State.IDLE;
        }
        if (!gc.getMap().isGroundPassable(getCellX(), getCellY())) {
            changePosition(tmp2.x + tmp.x * dt, tmp2.y);
            if (!gc.getMap().isGroundPassable(getCellX(), getCellY())) {
                changePosition(tmp2.x, tmp2.y + tmp.y * dt);
                if (!gc.getMap().isGroundPassable(getCellX(), getCellY())) {
                    changePosition(tmp2);
                }
            }
        }
    }

    public boolean takeDamage(GameCharacter attacker, int amount) {
        lastAttacker = attacker;
        hp -= amount;
        damageTimer += 0.4f;
        if (damageTimer > 1.0f) {
            damageTimer = 1.0f;
        }
        if (hp <= 0) {
            onDeath();
            return true;
        } else {
            // если персонажа атакуют и он не находиться в состоянии
            // атаки другого персонажа, то нужно защищаться
            if (this.state != State.ATTACK && this.charType != CharType.HUMAN){
                state = State.ATTACK;
                target = lastAttacker;
                stateTimer = 10.0f;
            }
        }
        gc.getInfoController().setupAnyAmount(position.x, position.y,
                this.charType != CharType.HUMAN ? Color.ORANGE : Color.RED,
                "-", amount);
        return false;
    }

    public void resetAttackState() {
        dst.set(position);
        state = State.IDLE;
        target = null;
    }

    public void slideFromWall(float dt) {
        if (!gc.getMap().isGroundPassable(position)) {
            tmp.set(position).sub(getCellX() * Map.CELL_WIDTH + Map.CELL_WIDTH / 2, getCellY() * Map.CELL_HEIGHT + Map.CELL_HEIGHT / 2).nor().scl(60.0f);
            changePosition(position.x + tmp.x * dt, position.y + tmp.y * dt);
        }
    }

    public void onDeath() {
        for (int i = 0; i < gc.getAllCharacters().size(); i++) {
            GameCharacter gameCharacter = gc.getAllCharacters().get(i);
            if (gameCharacter.target == this) {
                gameCharacter.resetAttackState();
                if (this.countExperience == 0) {
                    gameCharacter.countExperience = gameCharacter.countExperience + 1;
                } else {
                    gameCharacter.countExperience = gameCharacter.countExperience + this.countExperience;
                }
            }
        }
    }

    @Override
    public void render(SpriteBatch batch, BitmapFont font) {
        TextureRegion currentRegion = textures[0][getCurrentFrameIndex()];
        if (dst.x > position.x) {
            if (currentRegion.isFlipX()) {
                currentRegion.flip(true, false);
            }
        } else {
            if (!currentRegion.isFlipX()) {
                currentRegion.flip(true, false);
            }
        }
        // отрисовка героя
        batch.setColor(1.0f, 1.0f - damageTimer, 1.0f - damageTimer, 1.0f);
        batch.draw(currentRegion, position.x - 30, position.y - 15, 30, 30, 60, 60, 1.0f, 1.0f, 0);
        batch.setColor(1.0f, 1.0f, 1.0f, 1.0f);

        batch.setColor(0.2f, 0.2f, 0.2f, 1.0f);
        batch.draw(textureHp, position.x - 32, position.y + 48, 64, 14);
        batch.setColor(1.0f, 1.0f, 1.0f, 1.0f);
        float n = (float) hp / hpMax;
        float shock = damageTimer * 5.0f;
        batch.setColor(1.0f - n, n, 0.0f, 1.0f); // 1.0f - n, n, 0.0f, 1.0f
        //batch.setColor(0.0f, 0.0f, 0.0f, 1.0f);
        batch.draw(textureHp, position.x - 32 + MathUtils.random(-shock, shock), position.y + 48 + MathUtils.random(-shock, shock), 60 * ((float) hp / hpMax), 10);
        batch.setColor(1.0f, 1.0f, 1.0f, 1.0f);
        font.draw(batch, String.valueOf(hp+"/"+ countExperience), position.x - 30 + MathUtils.random(-shock, shock), position.y + 60 + MathUtils.random(-shock, shock), 60, 1, false);
    }
}
