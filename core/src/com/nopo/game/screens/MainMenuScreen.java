package com.nopo.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.ScreenUtils;
import com.nopo.game.Config;

public class MainMenuScreen implements Screen {

    final Game game;

    OrthographicCamera camera;

    Vector3 touchPos = new Vector3();

    Rectangle newGameRec;
    Rectangle configRec;
    Texture background;
    long lastCatched = -1;
    boolean devKey = false;

    public MainMenuScreen(final Game game) {
        this.game = game;

        camera = new OrthographicCamera();
        camera.setToOrtho(false, 800, 480);

        newGameRec = new Rectangle(50, 350, 170, 30);
        configRec = new Rectangle(50, 300, 170, 35);

        background = new Texture(Gdx.files.internal("main_menu_background.png"));
    }

    @Override
    public void render(float delta) {
        Game.setUpTouchPos(touchPos, camera);
        devKey = Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT);

        Game.cursorPos(Game.pointer, touchPos);

        ScreenUtils.clear(0, 0, 0.2f, 1);

        camera.update();
        game.batch.setProjectionMatrix(camera.combined);

        game.batch.begin();
        // Background
        game.batch.draw(background, 0, 0, 800, 480);
        // Buttons
        game.batch.draw(game.black, newGameRec.x, newGameRec.y, newGameRec.width, newGameRec.height);
        game.batch.draw(game.black, configRec.x, configRec.y, configRec.width, configRec.height);

        // Text
        game.font30.draw(game.batch, "sick game bruv", 300, 450);
        game.font30.draw(game.batch, "New Game!", newGameRec.x, newGameRec.y + newGameRec.height);
        game.font30.draw(game.batch, "Settings", configRec.x, configRec.y + configRec.height);

        // Cursor
        if (Config.usePointer) {
            game.batch.draw(game.cursor, Game.pointer.x, Game.pointer.y, Game.pointer.width, Game.pointer.height);
        }


        game.batch.end();
        if (Gdx.input.justTouched() && devKey) {
            System.out.println("x: " + touchPos.x);
            System.out.println("y: " + touchPos.y);
            System.out.println("--------------------------------");
        }

        if (Gdx.input.isTouched() && newGameRec.overlaps(Game.pointer) && !devKey) {
            Game.lastScreen = Game.LastScreen.MAIN_MENU;
            game.setScreen(new GameScreen(game));
            dispose();
        } else if (Gdx.input.isTouched() && configRec.overlaps(Game.pointer) && !devKey) {
            Game.lastScreen = Game.LastScreen.MAIN_MENU;
            game.setScreen(new ConfigScreen(game));
        }
    }

    @Override
    public void show() {
        Gdx.input.setCursorCatched(Config.usePointer);
    }

    @Override
    public void dispose() {
        background.dispose();
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }
}
