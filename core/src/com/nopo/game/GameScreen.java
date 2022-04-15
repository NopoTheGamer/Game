package com.nopo.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;

public class GameScreen implements Screen {

    final Game game;


    Texture playerTexture;
    Texture sandTile;
    Texture testTile;
    OrthographicCamera camera;
    Rectangle player;
    Array<Rectangle> sandTiles;
    long lastDropTime;
    int dropsGathered;
    Vector3 touchPos = new Vector3();
    static final int WORLD_WIDTH = 5000;
    static final int WORLD_HEIGHT = 5000;
    static float viewportWidth = 960;
    static float viewportHeight = 960;
    private SpriteBatch batch;
    private Sprite mapSprite;
    final int playerCameraOffsetX = 304;
    final int playerCameraOffsetY = 176;
    float cameraOffsetX;
    float cameraOffsetY;
    int coolandepiccounter = 0;

    public GameScreen(final Game game) {
        this.game = game;

        playerTexture = new Texture(Gdx.files.internal("dude.png"));
        sandTile = new Texture(Gdx.files.internal("sand.png"));
        testTile = new Texture(Gdx.files.internal("test.png"));

        float w = Gdx.graphics.getWidth();
        float h = Gdx.graphics.getHeight();

        camera = new OrthographicCamera(viewportWidth, viewportHeight * (h / w));
        camera.setToOrtho(false, viewportWidth, viewportHeight * (h / w));
        camera.position.set(-500, -500, 0);
        camera.zoom = .7f;
        camera.update();

        player = new Rectangle();
        player.x = -500;
        player.y = -500;
        player.width = 64;
        player.height = 64;
    }

    private void spawnSand() {
        for (int i = 0; i < WORLD_WIDTH; i += 64) {
            for (int ii = 0; ii < WORLD_HEIGHT; ii += 64) {
                Rectangle sand = new Rectangle();
                sand.x = i;
                sand.y = ii;
                sand.width = 64;
                sand.height = 64;
                sandTiles.add(sand);
            }
        }
    }

    private void handleInput() {
        cameraOffsetX = (camera.position.x - playerCameraOffsetX);
        cameraOffsetY = (camera.position.y - playerCameraOffsetY);
        if (Gdx.input.isKeyJustPressed(Input.Keys.A)) {
            if (player.x - (int) cameraOffsetX <= 64) {
                camera.translate(-64, 0, 0);
            }
            player.x -= 64;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.D)) {
            if (player.x - (int) cameraOffsetX >= 448) {
                camera.translate(64, 0, 0);
            }
            player.x += 64;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.S)) {
            if (player.y - (int) cameraOffsetY <= 0) {
                camera.translate(0, -64, 0);
            }
            player.y -= 64;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.W)) {
            if (player.y - (int) cameraOffsetY >= 256) {
                camera.translate(0, 64, 0);
            }
            player.y += 64;
        }
        System.out.println("player x: " + player.x);
        System.out.println("camera x: " + (camera.position.x - playerCameraOffsetX));
        System.out.println(cameraOffsetX);
        System.out.println("player y: " + player.y);
        System.out.println("camera y: " + (camera.position.y - playerCameraOffsetY));
        System.out.println();
        System.out.println(player.x - (camera.position.x - playerCameraOffsetX));
        if (player.x < 0) {
            player.x = 32;
        }
        if (player.y < 97){
            player.y = 96 + 64;
        }
        camera.position.x = MathUtils.clamp(camera.position.x, (viewportWidth * camera.zoom) / 2f, WORLD_WIDTH - (viewportWidth * camera.zoom) / 2f);
        camera.position.y = MathUtils.clamp(camera.position.y, (viewportHeight * camera.zoom) / 2f, WORLD_HEIGHT - (viewportHeight * camera.zoom) / 2f);


    }

    @Override
    public void render(float delta) {
        handleInput();
        touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
        camera.unproject(touchPos);
        // used to clear the screen.
        ScreenUtils.clear(0, 0, 0, 1);

        // tell the camera to update its matrices.
        camera.update();

        // tell the SpriteBatch to render in the
        // coordinate system specified by the camera.
        game.batch.setProjectionMatrix(camera.combined);

        //Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        game.batch.begin();
        sandTiles = new Array<Rectangle>();
        spawnSand();
        coolandepiccounter = 0;
        for (Rectangle sand : sandTiles) {
            coolandepiccounter++;
            if (coolandepiccounter % 2 == 0) {
                game.batch.draw(sandTile, sand.x, sand.y, sand.width, sand.height);
            } else {
                game.batch.draw(testTile, sand.x, sand.y, sand.width, sand.height);
            }
        }
        game.font.draw(game.batch, "clown", 100, 200);
        game.batch.draw(playerTexture, player.x, player.y, player.width, player.height);
        game.batch.end();
    }

    @Override
    public void show() {

    }

    @Override
    public void resize(int width, int height) {
        camera.viewportWidth = viewportWidth;                 // Viewport of 30 units!
        camera.viewportHeight = viewportHeight * height/width; // Lets keep things in proportion.
        camera.update();
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
        sandTile.dispose();
        playerTexture.dispose();
    }
}
