package com.nopo.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.TimeUtils;
import com.nopo.game.screens.Game;

import java.util.Iterator;

public class DropGame implements Screen {

    final Game game;

    Texture dropImage;
    Texture playerTexture;
    Texture sandTile;
    Sound dropSound;
    Music rainMusic;
    OrthographicCamera camera;
    Rectangle player;
    Array<Rectangle> raindrops;
    Array<Rectangle> sandTiles;
    long lastDropTime;
    int dropsGathered;
    boolean bozo;
    float bgColour = (float) Math.random();
    Vector3 touchPos = new Vector3();
    float gameWidth = 800;
    float gameHeight = 480;

    public DropGame(final Game game) {
        this.game = game;

        // load the images for the droplet and the bucket, 64x64 pixels each
        dropImage = new Texture(Gdx.files.internal("drop.png"));
        playerTexture = new Texture(Gdx.files.internal("dude.png"));
        sandTile = new Texture(Gdx.files.internal("sand.png"));

        // load the drop sound effect and the rain background "music"
        //dropSound = Gdx.audio.newSound(Gdx.files.internal("waterdrop24.wav"));
        //rainMusic = Gdx.audio.newMusic(Gdx.files.internal("undertreeinrain.mp3"));
        //rainMusic.setLooping(true);

        // create the camera and the SpriteBatch
        camera = new OrthographicCamera();
        camera.setToOrtho(false, gameWidth, gameHeight);

        // create a Rectangle to logically represent the bucket
        player = new Rectangle();
        player.x = 800 / 2 - 64 / 2; // center the bucket horizontally
        player.y = 20; // bottom left corner of the bucket is 20 pixels above
        // the bottom screen edge
        player.width = 64;
        player.height = 64;

        // create the raindrops array and spawn the first raindrop
        raindrops = new Array<Rectangle>();
        spawnRaindrop();

    }

    private void spawnRaindrop() {
        Rectangle raindrop = new Rectangle();
        raindrop.x = MathUtils.random(0, 800 - 64);
        raindrop.y = 480;
        raindrop.width = 64;
        raindrop.height = 64;
        raindrops.add(raindrop);
        lastDropTime = TimeUtils.nanoTime();
    }
    private void spawnSand() {
        for (int i = 0; i < 800; i += 64) {
            for (int ii = 0; ii < 480; ii += 64) {
                Rectangle sand = new Rectangle();
                sand.x = i;
                sand.y = ii;
                sand.width = 64;
                sand.height = 64;
                sandTiles.add(sand);
            }
        }
    }

    @Override
    public void render(float delta) {
        touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
        camera.unproject(touchPos);
        // clear the screen with a dark blue color. The
        // arguments to clear are the red, green
        // blue and alpha component in the range [0,1]
        // of the color to be used to clear the screen.
        ScreenUtils.clear(bgColour, .3f, bgColour, 1);

        // tell the camera to update its matrices.
        camera.update();

        // tell the SpriteBatch to render in the
        // coordinate system specified by the camera.
        game.batch.setProjectionMatrix(camera.combined);

        // begin a new batch and draw the bucket and
        // all drops
        game.batch.begin();
        sandTiles = new Array<Rectangle>();
        spawnSand();
        for (Rectangle sand : sandTiles) {
            game.batch.draw(sandTile, sand.x, sand.y, sand.width, sand.height);
        }
        game.font.draw(game.batch, "Score: " + dropsGathered, 0, 480);
        if (bozo) {
            game.font.draw(game.batch, "clown", 100, 200);
        }
        game.batch.draw(playerTexture, player.x, player.y, player.width, player.height);

        for (Rectangle raindrop : raindrops) {
            game.batch.draw(dropImage, raindrop.x, raindrop.y);
        }
        game.batch.end();

        // process user input
        if (Gdx.input.isTouched() && touchPos.x < 100) {
            System.out.println(touchPos.x);
            player.x = touchPos.x - 64 / 2;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.A)) {
            System.out.println(player.x);
            player.x -= 64;
            System.out.println(player.x);
            //camera.translate(-500 * Gdx.graphics.getDeltaTime(), 0);
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.D)) {
            player.x += 64;
            //camera.translate(500 * Gdx.graphics.getDeltaTime(), 0);
        }

        // make sure the bucket stays within the screen bounds
        if (player.x < 0) player.x = 0;
        if (player.x > 800 - 64) player.x = 800 - 64;
        if (dropsGathered < 0) {
            game.setScreen(new GameOverScreen(game));
            dispose();
        }
        // check if we need to create a new raindrop
        if (TimeUtils.nanoTime() - lastDropTime > 1000000000) spawnRaindrop();

        // move the raindrops, remove any that are beneath the bottom edge of
        // the screen or that hit the bucket. In the later case we increase the
        // value our drops counter and add a sound effect.
        Iterator<Rectangle> iter = raindrops.iterator();
        while (iter.hasNext()) {
            Rectangle raindrop = iter.next();
            raindrop.y -= 200 * Gdx.graphics.getDeltaTime();
            if (raindrop.y + 64 < 0) {
                iter.remove();
                dropsGathered--;
                bozo = true;
            }
            if (raindrop.overlaps(player)) {
                dropsGathered++;
                dropSound.play();
                iter.remove();
                bozo = false;
            }
        }
    }

    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void show() {
        // start the playback of the background music
        // when the screen is shown
        rainMusic.play();
    }

    @Override
    public void hide() {
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void dispose() {
        dropImage.dispose();
        playerTexture.dispose();
        dropSound.dispose();
        rainMusic.dispose();
    }

}
