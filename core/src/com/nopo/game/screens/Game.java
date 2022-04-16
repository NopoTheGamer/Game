package com.nopo.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.TimeUtils;

public class Game extends com.badlogic.gdx.Game {

    public SpriteBatch batch;
    public BitmapFont font;
    public Texture cursor;
    public Texture black;
    static long lastCatched = -1;
    static Rectangle pointer;
    public static boolean usePointer = true;

    public void create() {
        batch = new SpriteBatch();
        // Uses a ttf font so i can scale it up without it looking like doody water
        // well i mean it still does
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("OpenSans-Regular.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 30;
        parameter.borderWidth = 1;
        parameter.color = Color.WHITE;
        parameter.shadowOffsetX = 3;
        parameter.shadowOffsetY = 3;
        parameter.shadowColor = new Color(0, 0, 0, 1);
        font = generator.generateFont(parameter);
        generator.dispose();


        cursor = new Texture(Gdx.files.internal("pointer.png"));
        black = new Texture(Gdx.files.internal("black.png"));
        pointer = new Rectangle(-10, -10, 32, 32);


        this.setScreen(new MainMenuScreen(this));
    }

    public void render() {
        super.render(); // important!
    }

    public void dispose() {
        batch.dispose();
        font.dispose();
    }

    /** Sets cursor position to the current mouse position
     *  To set up cursor put
     * Game.cursorPos(Game.pointer, touchPos);
     * game.batch.draw(game.cursor, Game.pointer.x, Game.pointer.y, Game.pointer.width, Game.pointer.height);
     * in your render method, with draw being in the batch.begin()
     * */
    public static void cursorPos(Rectangle pointer, Vector3 touchPos) {
        pointer.x = touchPos.x - 15;
        pointer.y = touchPos.y - 15;
        if ((pointer.x > 800 || pointer.y > 480 || pointer.x < 0 || pointer.y < 0) || (Gdx.input.isKeyPressed(Input.Keys.ALT_LEFT))) {
            Gdx.input.setCursorCatched(false);
        } else if (TimeUtils.millis() - lastCatched > 3000 || lastCatched == -1) {
            if (usePointer) {
                Gdx.input.setCursorCatched(true);
            }
            lastCatched = TimeUtils.millis();
        }
    }

    public static void setUpTouchPos(Vector3 touchPos, OrthographicCamera camera) {
        touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
        camera.unproject(touchPos);
    }

}
