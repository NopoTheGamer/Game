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
import com.nopo.game.Config;
import com.nopo.game.NPC;

public class Game extends com.badlogic.gdx.Game {

    public SpriteBatch batch;
    public BitmapFont font30;
    public BitmapFont font23;
    public BitmapFont font18;
    public Texture cursor;
    public Texture black;
    public Texture blackTransparent;
    static long lastCatched = -1;
    static Rectangle pointer;

    enum LastScreen {
        MAIN_MENU,
        GAME,
        CONFIG
    }

    public static LastScreen lastScreen = LastScreen.MAIN_MENU;

    public void create() {
        batch = new SpriteBatch();
        Config.loadConfig();
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
        font30 = generator.generateFont(parameter);
        parameter.size = 23;
        parameter.shadowOffsetX = 1;
        parameter.shadowOffsetY = 1;
        font23 = generator.generateFont(parameter);
        parameter.size = 18;
        font18 = generator.generateFont(parameter);
        generator.dispose();


        cursor = new Texture(Gdx.files.internal("pointer.png"));
        black = new Texture(Gdx.files.internal("black.png"));
        blackTransparent = new Texture(Gdx.files.internal("trans_black.png"));
        pointer = new Rectangle(-10, -10, 32, 32);

        NPC.writeDialogue();

        this.setScreen(new MainMenuScreen(this));
    }

    public void render() {
        super.render(); // important!
    }

    public void dispose() {
        batch.dispose();
        font30.dispose();
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
        if ((pointer.x > 800 || pointer.y > 490 || pointer.x < -20 || pointer.y < -15) || (Gdx.input.isKeyPressed(Input.Keys.ALT_LEFT))) {
            Gdx.input.setCursorCatched(false);
        } else if (TimeUtils.millis() - lastCatched > 3000 || lastCatched == -1) {
            if (Config.usePointer) {
                Gdx.input.setCursorCatched(true);
            }
            lastCatched = TimeUtils.millis();
        }
    }

    public static void setUpTouchPos(Vector3 touchPos, OrthographicCamera camera) {
        touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
        camera.unproject(touchPos);
    }

    public void drawCursor() {
        if (Config.usePointer) {
            batch.draw(cursor, Game.pointer.x, Game.pointer.y, Game.pointer.width, Game.pointer.height);
        }
    }

}
