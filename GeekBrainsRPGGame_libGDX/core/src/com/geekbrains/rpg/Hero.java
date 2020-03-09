package com.geekbrains.rpg;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

public class Hero {
    private Texture texture;
    private Texture texturePointer;
    private Vector2 position; // позиция героя
    private Vector2 dst; // точка назначения
    private float lifetime;
    private float speed;

    public Hero(){
        this.texture = new Texture("hero.png");
        this.texturePointer = new Texture("pointer.png");
        this.position = new Vector2(100, 100);
        this.dst = new Vector2(position);
        this.speed = 100.0f;
    }

    public void render(SpriteBatch batch){
        batch.draw(texturePointer, dst.x - 32, dst.y - 32, 32, 32, 64, 64,1,1,lifetime,0,0,64,64,false,false);
        batch.draw(texture, position.x - 32, position.y - 32);//, 32, 32, 64, 64, 1, 1
    }

    public void update(float dt){
        lifetime += dt;
        if (Gdx.input.justTouched()){
            dst.set(Gdx.input.getX(), 620 - Gdx.input.getY());
        }
        if (position.x > dst.x){
            position.x -= speed * dt;
        }
        if (position.x < dst.x){
            position.x += speed * dt;
        }
        if (position.y > dst.y){
            position.y -= speed * dt;
        }
        if (position.y < dst.y){
            position.y += speed * dt;
        }
    }

}
