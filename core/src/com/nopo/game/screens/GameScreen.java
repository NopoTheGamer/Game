package com.nopo.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.nopo.game.Config;
import com.nopo.game.Utils;

public class GameScreen implements Screen {

    final Game game;

    OrthographicCamera camera;
    Vector3 touchPos = new Vector3();

    Texture playerTexture;
    Texture sandTile;
    Texture testTile;
    Texture rockTile;
    Texture menuBackground;

    Rectangle player;
    Rectangle menuHud;
    Rectangle menuHudOption1;
    Rectangle menuHudOption2;
    Array<Rectangle> sandTiles;
    Array<Rectangle> rockTiles;

    enum LastDirection {
        LEFT, RIGHT, UP, DOWN
    }

    LastDirection lastDirection = LastDirection.DOWN;
    static final int WORLD_WIDTH = 5000;
    static final int WORLD_HEIGHT = 5000;
    static float viewportWidth = 960;
    static float viewportHeight = 960;
    final int playerCameraOffsetX = 304;
    final int playerCameraOffsetY = 176;
    float cameraOffsetX;
    float cameraOffsetY;
    float leftMostCamera = 336;
    float bottomMostCamera = 330;
    float screenWidth = Gdx.graphics.getWidth();

    float screenHeight = Gdx.graphics.getHeight();

    boolean menuOpen = false;

    String debugX = "";
    String debugY = "";

    public GameScreen(final Game game) {
        this.game = game;

        playerTexture = new Texture(Gdx.files.internal("player.png"));
        sandTile = new Texture(Gdx.files.internal("sand.png"));
        testTile = new Texture(Gdx.files.internal("test.png"));
        rockTile = new Texture(Gdx.files.internal("rock.png"));
        menuBackground = new Texture(Gdx.files.internal("menu_background.png"));

        camera = new OrthographicCamera(viewportWidth, viewportHeight * (screenHeight / screenWidth));
        camera.setToOrtho(false, viewportWidth, viewportHeight * (screenHeight / screenWidth));
        camera.position.set(Config.cameraX, Config.cameraY, 0);
        camera.zoom = .7f;
        camera.update();

        player = new Rectangle((float) getXAsCoords(Config.playerX), (float) getYAsCoords(Config.playerY), 64, 64);
        menuHud = new Rectangle(25, 400, 200, 100);
        menuHudOption1 = new Rectangle(10, 55, menuHud.width - 20, menuHud.height - 65);
    }


    private void handleInput() {
        cameraOffsetX = (camera.position.x - playerCameraOffsetX);
        cameraOffsetY = (camera.position.y - playerCameraOffsetY);
        if (Gdx.input.isKeyJustPressed(Input.Keys.A)) {
            if (player.x - (int) cameraOffsetX <= 64) {
                camera.translate(-64, 0, 0);
            }
            changePos(-1, 0);
            lastDirection = LastDirection.LEFT;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.D)) {
            if (player.x - (int) cameraOffsetX >= 448) {
                camera.translate(64, 0, 0);
            }
            changePos(1, 0);
            lastDirection = LastDirection.RIGHT;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.S)) {
            if (player.y - (int) cameraOffsetY <= 64) {
                camera.translate(0, -64, 0);
            }
            changePos(0, -1);
            lastDirection = LastDirection.DOWN;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.W)) {
            if (player.y - (int) cameraOffsetY >= 256) {
                camera.translate(0, 64, 0);
            }
            changePos(0, 1);
            lastDirection = LastDirection.UP;
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.CONTROL_LEFT)) {
            debugX += getXAsGrid() + ", ";
            debugY += getYAsGrid() + ", ";
        }

        collisionWithRectangleArray(rockTiles);

//        System.out.println("player x: " + player.x);
//        System.out.println("camera x: " + (camera.position.x - playerCameraOffsetX));
//        System.out.println(cameraOffsetX);
//        System.out.println("player y: " + player.y);
//        System.out.println("camera y: " + (camera.position.y - playerCameraOffsetY));
//        System.out.println();
//        System.out.println(player.x - (camera.position.x - playerCameraOffsetX));

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            menuOpen = !menuOpen;
            save();
            if (!Utils.isEmpty(debugX)) {
                System.out.println("x: {" + Utils.removeEnd(debugX, ", ") + "}");
                System.out.println("y: {" + Utils.removeEnd(debugY, ", ") + "}");
            }
            debugX = "";
            debugY = "";
        }

        if (Gdx.input.isTouched() && menuOpen && Game.pointer.overlaps(menuHudOption1)) {
            save();
            game.setScreen(new MainMenuScreen(game));
        }

        player.x = MathUtils.clamp(player.x, 64, WORLD_WIDTH - 96);
        player.y = MathUtils.clamp(player.y, 128, WORLD_HEIGHT - 192);
        camera.position.x = MathUtils.clamp(camera.position.x, (viewportWidth * camera.zoom) / 2f, WORLD_WIDTH - (viewportWidth * camera.zoom) / 2f);
        camera.position.y = MathUtils.clamp(camera.position.y, ((viewportHeight * camera.zoom) / 2f) - 6, WORLD_HEIGHT - (viewportHeight * camera.zoom) / 2f);


    }

    @Override
    public void render(float delta) {
        rockTiles = new Array<Rectangle>();
        sandTiles = new Array<Rectangle>();
        spawnRocks(new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 10, 10, 10, 10, 10}, new int[]{6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 5, 4, 3, 2, 1});
        spawnSand();

        handleInput();
        Game.setUpTouchPos(touchPos, camera);

        ScreenUtils.clear(0, 0, 0, 1); // used to clear the screen.

        Game.cursorPos(Game.pointer, touchPos);

        camera.update();

        game.batch.setProjectionMatrix(camera.combined);
        game.batch.begin();

        int i = 0;
        for (Rectangle sand : sandTiles) {
            i++;
            if (i % 2 == 0) {
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
        if (menuOpen) {
            menuHud.x = (camera.position.x - leftMostCamera) + 25;
            menuHud.y = (camera.position.y - bottomMostCamera) + 400;
            menuHudOption1.x = menuHud.x + 10;
            menuHudOption1.y = menuHud.y + 55;
            game.batch.draw(menuBackground, menuHud.x, menuHud.y, menuHud.width, menuHud.height);
            game.batch.draw(game.blackTransparent, menuHudOption1.x, menuHudOption1.y, menuHudOption1.width, menuHudOption1.height);
            game.font.draw(game.batch, "Main menu?", menuHudOption1.x, menuHudOption1.y + menuHudOption1.height);
        }

        if (Config.usePointer) {
            game.batch.draw(game.cursor, Game.pointer.x, Game.pointer.y, Game.pointer.width, Game.pointer.height);
        }
        game.batch.end();
    }

    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void show() {
        Gdx.input.setCursorCatched(Config.usePointer);
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

    private void spawnRocks(int[] rockPositionsX, int[] rockPositionsY) {
        if (rockPositionsX.length == rockPositionsY.length) {
            for (int i = 0; i < rockPositionsX.length; i++) {
                Rectangle rock = new Rectangle();
                rock.x = getXAsCoords(rockPositionsX[i]);
                rock.y = getYAsCoords(rockPositionsY[i]);
                rock.width = 64;
                rock.height = 64;
                rockTiles.add(rock);
            }
        } else {
            throw new RuntimeException("Rocks X and Y arrays are not the same length!");
        }
        for (int i = 0; i < WORLD_WIDTH; i++) {
            Rectangle rock = new Rectangle();
            rock.x = getXAsCoords(i);
            rock.y = getYAsCoords(0);
            rock.width = 64;
            rock.height = 64;
            rockTiles.add(rock);
        }
        for (int i = 0; i < WORLD_HEIGHT; i++) {
            Rectangle rock = new Rectangle();
            rock.x = getXAsCoords(0);
            rock.y = getYAsCoords(i);
            rock.width = 64;
            rock.height = 64;
            rockTiles.add(rock);
        }
    }

    private void save() {
        Config.playerX = getXAsGrid();
        Config.playerY = getYAsGrid();
        Config.cameraX = camera.position.x;
        Config.cameraY = camera.position.y;
        Config.writeConfig();
    }

    private void collisionWithRectangleArray(Array<Rectangle> rectArray) {
        for (Rectangle recs : rectArray) {
            if (player.overlaps(recs)) {
                if (lastDirection == LastDirection.LEFT) {
                    changePos(1, 0);
                } else if (lastDirection == LastDirection.RIGHT) {
                    changePos(-1, 0);
                } else if (lastDirection == LastDirection.UP) {
                    changePos(0, -1);
                } else if (lastDirection == LastDirection.DOWN) {
                    changePos(0, 1);
                }
            }
        }
    }

    private int getXAsCoords(int x) {
        return x * 64;
    }

    private int getYAsCoords(int y) {
        return (y + 2) * 64;
    }

    private void changePos(int x, int y) {
        player.x += x * 64;
        player.y += y * 64;
    }

    private int getXAsGrid() {
        return (int) player.x / 64;
    }

    private int getYAsGrid() {
        return (int) (player.y / 64) - 2;
    }
}
