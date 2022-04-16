package com.nopo.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.nopo.game.screens.Game;

public class GameScreen implements Screen {

    final Game game;


    Texture playerTexture;
    Texture sandTile;
    Texture testTile;
    Texture rockTile;
    OrthographicCamera camera;
    Rectangle player;
    Array<Rectangle> sandTiles;
    Array<Rectangle> rockTiles;
    Array<Rectangle> playerCollision;
    Vector3 touchPos = new Vector3();
    static final int WORLD_WIDTH = 5000;
    static final int WORLD_HEIGHT = 5000;
    static float viewportWidth = 960;
    static float viewportHeight = 960;
    final int playerCameraOffsetX = 304;
    final int playerCameraOffsetY = 176;
    float cameraOffsetX;
    float cameraOffsetY;
    int coolandepiccounter = 0;
    int[] rocksX = new int[]{2, 3, 4, 5, 6};
    int[] rocksY = new int[]{1, 3, 4, 5, 6};

    public GameScreen(final Game game) {
        this.game = game;

        playerTexture = new Texture(Gdx.files.internal("player.png"));
        sandTile = new Texture(Gdx.files.internal("sand.png"));
        testTile = new Texture(Gdx.files.internal("test.png"));
        rockTile = new Texture(Gdx.files.internal("rock.png"));
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


    private void handleInput() {
        playerCollision = new Array<Rectangle>();
        spawnPlayers();
        //TODO make this work
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
            if (player.y - (int) cameraOffsetY <= 64) {
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


        player.x = MathUtils.clamp(player.x, 0, WORLD_WIDTH - 96);
        player.y = MathUtils.clamp(player.y, 128, WORLD_HEIGHT - 192);
        camera.position.x = MathUtils.clamp(camera.position.x, (viewportWidth * camera.zoom) / 2f, WORLD_WIDTH - (viewportWidth * camera.zoom) / 2f);
        camera.position.y = MathUtils.clamp(camera.position.y, ((viewportHeight * camera.zoom) / 2f) - 6, WORLD_HEIGHT - (viewportHeight * camera.zoom) / 2f);


    }

    @Override
    public void render(float delta) {
        handleInput();
        Game.setUpTouchPos(touchPos, camera);
        // used to clear the screen.
        ScreenUtils.clear(0, 0, 0, 1);

        Game.cursorPos(Game.pointer, touchPos);

        camera.update();
        // tell the SpriteBatch to render in the
        // coordinate system specified by the camera.
        game.batch.setProjectionMatrix(camera.combined);
        game.batch.begin();
        sandTiles = new Array<Rectangle>();
        spawnSand();
        rockTiles = new Array<Rectangle>();
        spawnRocks();
        coolandepiccounter = 0;
        for (Rectangle sand : sandTiles) {
            coolandepiccounter++;
            if (coolandepiccounter % 2 == 0) {
                game.batch.draw(sandTile, sand.x, sand.y, sand.width, sand.height);
            } else {
                game.batch.draw(testTile, sand.x, sand.y, sand.width, sand.height);
            }
        }
        for (Rectangle rock : rockTiles) {
            game.batch.draw(rockTile, rock.x, rock.y, rock.width, rock.height);
        }
        game.font.draw(game.batch, "clown", 100, 200);
        game.batch.draw(playerTexture, player.x, player.y, player.width, player.height);

        if (Game.usePointer) {
            game.batch.draw(game.cursor, Game.pointer.x, Game.pointer.y, Game.pointer.width, Game.pointer.height);
        }
        game.batch.end();
    }

    @Override
    public void resize(int width, int height) {
//        camera.viewportWidth = viewportWidth;                 // Viewport of 30 units!
//        camera.viewportHeight = (viewportHeight * height / width); // Lets keep things in proportion.
//        camera.update();
    }

    @Override
    public void show() {
        Gdx.input.setCursorCatched(false);
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

    private void spawnRocks() {
        if (rocksY.length == rocksX.length) {
            for (int i = 0; i < rocksX.length; i++) {
                Rectangle rock = new Rectangle();
                rock.x = (rocksX[i] - 1) * 64;
                rock.y = (rocksY[i] + 1) * 64;
                rock.width = 64;
                rock.height = 64;
                rockTiles.add(rock);
            }
        } else {
            throw new RuntimeException("Rocks X and Y arrays are not the same length!");
        }
    }

    private void spawnPlayers() {
        for (int i = 0; i < 4; i++) {
            Rectangle playerCollisionRec = new Rectangle();
            switch (i) {
                case 0 -> {
                    playerCollisionRec.x = player.x;
                    playerCollisionRec.y = player.y - 64;
                }
                case 1 -> {
                    playerCollisionRec.x = player.x + 64;
                    playerCollisionRec.y = player.y;
                }
                case 2 -> {
                    playerCollisionRec.x = player.x - 64;
                    playerCollisionRec.y = player.y;
                }
                case 3 -> {
                    playerCollisionRec.x = player.x;
                    playerCollisionRec.y = player.y + 64;
                }
            }
            playerCollisionRec.width = 64;
            playerCollisionRec.height = 64;
            playerCollision.add(playerCollisionRec);
        }
    }
}
