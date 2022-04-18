package com.nopo.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.nopo.game.Config;
import com.nopo.game.NPC;
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
    Texture npcBackground;
    Texture rightArrow;
    Texture teleporterSpriteMap;

    Rectangle player;
    Rectangle menuHud;
    Rectangle menuHudOption1;
    Rectangle menuHudOption2;
    Rectangle selectedOptionArrow;
    Array<Rectangle> sandTiles;
    Array<Rectangle> rockTiles;
    Array<Rectangle> tpTiles;
    Array<Rectangle> tpTiles2;

    NPC npc1 = new NPC("Balls in yo jaw", 3, 3, 1, 3);
    NPC npc2 = new NPC("sword dude", 10, 2, 3, 5);

    TeleportTiles teleportTile = new TeleportTiles(1, 1, 9, 5);
    TeleportTiles teleportTile2 = new TeleportTiles(16, 1, 27, 1);
    TeleportTiles teleportTile3 = new TeleportTiles(29, 4, 37, 14);

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

    float stateTime = 0f;
    private static final int TELEPORTER_FRAMES = 4;
    Animation<TextureRegion> teleportAnimation;

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
        npcBackground = new Texture(Gdx.files.internal("npc_background.png"));
        rightArrow = new Texture(Gdx.files.internal("right_arrow.png"));
        teleporterSpriteMap = new Texture(Gdx.files.internal("teleporter.png"));

        camera = new OrthographicCamera(viewportWidth, viewportHeight * (screenHeight / screenWidth));
        camera.setToOrtho(false, viewportWidth, viewportHeight * (screenHeight / screenWidth));
        camera.position.set(Config.cameraX, Config.cameraY, 0);
        camera.zoom = .7f;
        camera.update();

        player = new Rectangle((float) getXAsCoords(Config.playerX), (float) getYAsCoords(Config.playerY), 64, 64);
        menuHud = new Rectangle(25, 400, 200, 100);
        menuHudOption1 = new Rectangle(10, 55, menuHud.width - 40, menuHud.height - 77);
        menuHudOption2 = new Rectangle(10, 55, menuHud.width - 40, menuHud.height - 77);
        selectedOptionArrow = new Rectangle(menuHud.x - 15, menuHud.y + 74, 18, 17);

        TextureRegion[][] tmp = TextureRegion.split(teleporterSpriteMap, teleporterSpriteMap.getWidth() / TELEPORTER_FRAMES, teleporterSpriteMap.getHeight() / TELEPORTER_FRAMES);

        // Place the regions into a 1D array in the correct order, starting from the top
        // left, going across first. The Animation constructor requires a 1D array.
        TextureRegion[] walkFrames = new TextureRegion[TELEPORTER_FRAMES * TELEPORTER_FRAMES];
        int index = 0;
        for (int i = 0; i < TELEPORTER_FRAMES; i++) {
            for (int j = 0; j < TELEPORTER_FRAMES; j++) {
                walkFrames[index++] = tmp[i][j];
                if (index > 12) break;
            }
        }
        teleportAnimation = new Animation<TextureRegion>(0.025f, walkFrames);
    }


    private void handleInput(float delta) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            menuOpen = !menuOpen;
            if (!Utils.isEmpty(debugX)) {
                System.out.println("x: {" + Utils.removeEnd(debugX, ", ") + "}");
                System.out.println("y: {" + Utils.removeEnd(debugY, ", ") + "}");
            }
            debugX = "";
            debugY = "";
        }
        if (menuOpen) return;


        cameraOffsetX = (camera.position.x - playerCameraOffsetX);
        cameraOffsetY = (camera.position.y - playerCameraOffsetY);
        if (Gdx.input.isKeyJustPressed(Input.Keys.W)) {
            if (player.y - (int) cameraOffsetY >= 256) {
                camera.translate(0, 64, 0);
            }
            changePos(0, 1);
            lastDirection = LastDirection.UP;
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.A)) {
            if (player.x - (int) cameraOffsetX <= 64) {
                camera.translate(-64, 0, 0);
            }
            changePos(-1, 0);
            lastDirection = LastDirection.LEFT;
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.S)) {
            if (player.y - (int) cameraOffsetY <= 64) {
                camera.translate(0, -64, 0);
            }
            changePos(0, -1);
            lastDirection = LastDirection.DOWN;
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.D)) {
            if (player.x - (int) cameraOffsetX >= 448) {
                camera.translate(64, 0, 0);
            }
            changePos(1, 0);
            lastDirection = LastDirection.RIGHT;
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.CONTROL_LEFT)) {
            debugX += getXAsGrid() + ", ";
            debugY += getYAsGrid() + ", ";
        }

        collisionWithRectangleArray(rockTiles);


        //System.out.println(player.y - cameraOffsetY + " " + camera.position.y);
        //System.out.println(player.x - cameraOffsetX + " " + (player.y - cameraOffsetY));
        if (player.x - cameraOffsetX < -32) {
            camera.position.x -= 32 * (Utils.interp(Interpolation.exp10Out, delta) * 1.5);
        } else if (player.x - cameraOffsetX > 544) {
            camera.position.x += 32 * (Utils.interp(Interpolation.exp10Out, delta) * 1.5);
        }
        if (player.y - cameraOffsetY < -26) {
            camera.position.y -= 32 * (Utils.interp(Interpolation.exp10Out, delta) * 1.5);
        } else if (player.y - cameraOffsetY > 358) {
            camera.position.y += 32 * (Utils.interp(Interpolation.exp10Out, delta) * 1.5);
        }
        player.x = MathUtils.clamp(player.x, 64, WORLD_WIDTH - 96);
        player.y = MathUtils.clamp(player.y, 128, WORLD_HEIGHT - 192);
        camera.position.x = MathUtils.clamp(camera.position.x, (viewportWidth * camera.zoom) / 2f, WORLD_WIDTH - (viewportWidth * camera.zoom) / 2f);
        camera.position.y = MathUtils.clamp(camera.position.y, ((viewportHeight * camera.zoom) / 2f) - 6, WORLD_HEIGHT - (viewportHeight * camera.zoom) / 2f);
    }

    @Override
    public void render(float delta) {
        spawnRocks(new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 10, 10, 10, 10, 10, 10, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21}, new int[]{6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 5, 4, 7, 8, 9, 10, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11});
        spawnSand();

        handleInput(delta);
        Game.setUpTouchPos(touchPos, camera);

        ScreenUtils.clear(0, 0, 0, 1); // used to clear the screen.
        stateTime += Gdx.graphics.getDeltaTime() / 2.5; // Accumulate elapsed animation time

        Game.cursorPos(Game.pointer, touchPos);

        camera.update();
        game.batch.setProjectionMatrix(camera.combined);

        game.batch.begin();

        drawSand();
        drawRock();

        teleportTile(teleportTile);
        teleportTile(teleportTile2);
        teleportTile(teleportTile3);

        game.font30.draw(game.batch, "clown", 100, 200);

        game.batch.draw(playerTexture, player.x, player.y, player.width, player.height);
        drawNPC(npc1);
        drawNPC(npc2);

        menuOpen(menuOpen, delta);

        game.drawCursor();

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
        teleporterSpriteMap.dispose();
    }

    private void spawnSand() {
        sandTiles = new Array<Rectangle>();
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

    private void drawSand() {
        int i = 0;
        for (Rectangle sand : sandTiles) {
            i++;
            if (i % 2 == 0) {
                game.batch.draw(sandTile, sand.x, sand.y, sand.width, sand.height);
            } else {
                game.batch.draw(testTile, sand.x, sand.y, sand.width, sand.height);
            }
        }
    }

    private void drawRock() {
        for (Rectangle rock : rockTiles) {
            game.batch.draw(rockTile, rock.x, rock.y, rock.width, rock.height);
        }
    }

    private void spawnRocks(int[] rockPositionsX, int[] rockPositionsY) {
        rockTiles = new Array<Rectangle>();
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

    private float lockXHud(float x) {
        return (camera.position.x - leftMostCamera) + x;
    }

    private float lockYHud(float y) {
        return (camera.position.y - bottomMostCamera) + y;
    }

    private void menuOpen(boolean enabled, float delta) {
        boolean optionOne = false;
        boolean optionTwo = false;

        if (!enabled) return;

        if (Gdx.input.isTouched() && Game.pointer.overlaps(menuHudOption2)) optionOne = true;
        if (Gdx.input.isTouched() && Game.pointer.overlaps(menuHudOption1)) optionTwo = true;
        menuHud.x = lockXHud(25);
        menuHud.y = lockYHud(400);
        menuHudOption1.x = menuHud.x + 10;
        menuHudOption1.y = menuHud.y + 70;
        menuHudOption2.x = menuHud.x + 10;
        menuHudOption2.y = menuHud.y + 45;

        if (Gdx.input.isKeyJustPressed(Input.Keys.W)) {
            selectedOptionArrow.y += 25;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.S)) {
            selectedOptionArrow.y -= 25;
        }
        int selectedOption = (int) (474 - selectedOptionArrow.y) / 25;
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER) || Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            System.out.println();
            switch (selectedOption) {
                case 0 -> optionOne = true;
                case 1 -> optionTwo = true;
            }
        }
//        if (selectedOption < 0) selectedOptionArrow.y = 449;
//        if (selectedOption > 1) selectedOptionArrow.y = 474; // math clamp with wrap
        selectedOptionArrow.y = Utils.clampWithWrap(selectedOptionArrow.y, 449, 474); // I LOVE RANDOM ASS NUMBERS

        game.batch.draw(menuBackground, menuHud.x, menuHud.y, menuHud.width, menuHud.height);
        game.batch.draw(rightArrow, lockXHud(selectedOptionArrow.x + (Utils.interp(Interpolation.swing, delta) * 6)), lockYHud(selectedOptionArrow.y), 18, 17);
        game.batch.draw(game.blackTransparent, menuHudOption1.x, menuHudOption1.y, menuHudOption1.width, menuHudOption1.height);
        game.batch.draw(game.blackTransparent, menuHudOption2.x, menuHudOption2.y, menuHudOption2.width, menuHudOption2.height);

        game.font23.draw(game.batch, "Save and quit", menuHudOption1.x, menuHudOption1.y + menuHudOption1.height);
        game.font23.draw(game.batch, "Settings", menuHudOption2.x, menuHudOption2.y + menuHudOption2.height);

        Game.lastScreen = Game.LastScreen.GAME;
        save();
        if (optionOne) {
            game.setScreen(new MainMenuScreen(game));
        } else if (optionTwo) {
            game.setScreen(new ConfigScreen(game));
        }
    }

    private void drawNPC(NPC npc) {
        game.batch.draw(playerTexture, getXAsCoords(npc.x), getYAsCoords(npc.y), 64, 64);
        if (getXAsGrid() == npc.x && getYAsGrid() == npc.y) {
            if (npc.dialogueLine < npc.endDialogue - (npc.startDialogue - 1)) {
                game.batch.draw(npcBackground, lockXHud(25), lockYHud(140), (960 * .7f /*672f*/) - 50 /*622f*/, 50);
                game.font23.draw(game.batch, npc.name, lockXHud(25) + 10, lockYHud(140) + 73);
                game.font18.draw(game.batch, NPC.getDialogue(npc.dialogueLine, npc), lockXHud(25) + 10, lockYHud(140) + 25);
                if ((Gdx.input.isKeyJustPressed(Input.Keys.SPACE))) {
                    npc.dialogueLine++;
                }
            }
        } else if (npc.dialogueLine == 0 || npc.dialogueLine >= npc.endDialogue - (npc.startDialogue - 1)) {
            npc.dialogueLine = 0;
        }
    }

    TextureRegion currentFrame;

    static class TeleportTiles {
        int x1, x2, y1, y2;
        boolean shouldTP = false;

        TeleportTiles(int x1, int y1, int x2, int y2) {
            this.x1 = x1;
            this.x2 = x2;
            this.y1 = y1;
            this.y2 = y2;
            this.shouldTP = false;
        }
    }

    private void teleportTile(TeleportTiles tile) {
        tpTiles = new Array<Rectangle>();
        tpTiles2 = new Array<Rectangle>();
        Rectangle tp = new Rectangle();
        tp.x = getXAsCoords(tile.x1);
        tp.y = getYAsCoords(tile.y1);
        tp.width = 64;
        tp.height = 64;
        tpTiles.add(tp);
        Rectangle tp2 = new Rectangle();
        tp2.height = 64;
        tp2.width = 64;
        tp2.x = getXAsCoords(tile.x2);
        tp2.y = getYAsCoords(tile.y2);
        tpTiles2.add(tp2);
        if (getXAsGrid() == tile.x1 && getYAsGrid() == tile.y1) {
            if (tile.shouldTP) {
                player.x = getXAsCoords(tile.x2);
                player.y = getYAsCoords(tile.y2);
            }
            tile.shouldTP = false;
        } else if (getXAsGrid() == tile.x2 && getYAsGrid() == tile.y2) {
            if (tile.shouldTP) {
                player.x = getXAsCoords(tile.x1);
                player.y = getYAsCoords(tile.y1);
            }
            tile.shouldTP = false;
        } else {
            tile.shouldTP = true;
        }
        if (teleportAnimation.getKeyFrame(stateTime, true) != null) {
            currentFrame = teleportAnimation.getKeyFrame(stateTime, true);
        } else {
            stateTime = 0;
        }
        for (Rectangle tpTile : tpTiles) {
            game.batch.draw(currentFrame, tpTile.x, tpTile.y, tpTile.width, tpTile.height);
        }
        for (Rectangle tpTile : tpTiles2) {
            game.batch.draw(currentFrame, tpTile.x, tpTile.y, tpTile.width, tpTile.height);
        }
    }
}
