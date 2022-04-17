package com.nopo.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.ScreenUtils;
import com.nopo.game.Config;

public class ConfigScreen implements Screen {

    final Game game;

    OrthographicCamera camera;

    Texture background;

    Rectangle backButton;
    Rectangle cursorButton;

    Vector3 touchPos = new Vector3();

    static float viewportWidth = 960;
    static float viewportHeight = 960;

    public ConfigScreen(final Game game) {
        this.game = game;


        camera = new OrthographicCamera();
        camera.setToOrtho(false, 800, 480);
        camera.update();

        background = new Texture(Gdx.files.internal("settings_background.png"));

        backButton = new Rectangle(30, 30, 60, 60);
        cursorButton = new Rectangle(30, 100, 300, 30);
    }

    @Override
    public void render(float delta) {
        Game.setUpTouchPos(touchPos, camera);
        Game.cursorPos(Game.pointer, touchPos);

        ScreenUtils.clear(0, 0, 0.2f, 1);

        camera.update();
        game.batch.setProjectionMatrix(camera.combined);

        game.batch.begin();
        // Background
        game.batch.draw(background, 0, 0, 800, 480);
        // Button
        game.batch.draw(game.black, backButton.x, backButton.y, backButton.width, backButton.height);
        game.batch.draw(game.black, cursorButton.x, cursorButton.y, cursorButton.width, cursorButton.height);
        // Text
        game.font30.draw(game.batch, "Config", 350, 450);
        game.font30.draw(game.batch, "Use custom cursor?", cursorButton.x, cursorButton.y + cursorButton.height);
        // Cursor
        if (Config.usePointer) {
            game.batch.draw(game.cursor, Game.pointer.x, Game.pointer.y, Game.pointer.width, Game.pointer.height);
        }
        game.batch.end();

        if (Gdx.input.isTouched() && backButton.overlaps(Game.pointer)) {
            switch (Game.lastScreen) {
                case MAIN_MENU -> game.setScreen(new MainMenuScreen(game));
                case GAME -> game.setScreen(new GameScreen(game));
            }
            Game.lastScreen = Game.LastScreen.CONFIG;
            dispose();
        } else if (Gdx.input.justTouched() && cursorButton.overlaps(Game.pointer)) {
            Config.usePointer = !Config.usePointer;
            Gdx.input.setCursorCatched(Config.usePointer);
            Config.writeConfig();
        }
    }

    @Override
    public void show() {
        Gdx.input.setCursorCatched(Config.usePointer);
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

    @Override
    public void dispose() {

    }
}
