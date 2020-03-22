package com.geekbrains.rpg.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.geekbrains.rpg.game.logic.GameController;
import com.geekbrains.rpg.game.logic.WorldRenderer;
import com.geekbrains.rpg.game.screens.utils.Assets;

public class GameScreen extends AbstractScreen {
    private Stage stage;
    private GameController gc;
    private WorldRenderer worldRenderer;
    private boolean pressPause;

    public GameScreen(SpriteBatch batch) {
        super(batch);
    }

    @Override
    public void show() {
        gc = new GameController();
        worldRenderer = new WorldRenderer(gc, batch);
        createGui();
        pressPause = false;
    }

    @Override
    public void render(float delta) {
        gc.update(delta);
        worldRenderer.render();
        stage.draw();
    }

    @Override
    public void pause(){
        if (!pressPause) {
            Gdx.graphics.setContinuousRendering(false);
            Gdx.graphics.requestRendering();
            pressPause = true;
        } else {
            Gdx.graphics.setContinuousRendering(true);
            Gdx.graphics.requestRendering();
            pressPause = false;
        }

    }

    @Override
    public void resume(){
        Gdx.graphics.setContinuousRendering(true);
        Gdx.graphics.requestRendering();
    }

    public void createGui() {
        stage = new Stage();
        Gdx.input.setInputProcessor(stage);
        Skin skin = new Skin();
        skin.addRegions(Assets.getInstance().getAtlas());

        BitmapFont font14 = Assets.getInstance().getAssetManager().get("fonts/font24.ttf");
        TextButton.TextButtonStyle menuBtnStyle = new TextButton.TextButtonStyle(
                skin.getDrawable("shortButton"), null, null, font14);

        TextButton btnPause = new TextButton("Pause", menuBtnStyle);
        btnPause.setPosition(800, 600);
        TextButton btnExitGame = new TextButton("Exit", menuBtnStyle);
        btnExitGame.setPosition(900, 600);

        btnPause.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                pause();
            }
        });

        btnExitGame.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.exit();
            }
        });

        stage.addActor(btnPause);
        stage.addActor(btnExitGame);
        skin.dispose();
    }
}
