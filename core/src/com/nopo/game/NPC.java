package com.nopo.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

public class NPC {
    public String name = "";
    public int x = -100;
    public int y = -100;
    public int startDialogue = -1;
    public int endDialogue = -1;
    static FileHandle dialogueFile = Gdx.files.external("epicnopogame/dialogue.text");
    public int dialogueLine = 0;

    public NPC() {

    }

    public NPC(String name, int x, int y, int startDialogue, int endDialogue) {
        this.name = name;
        this.x = x;
        this.y = y;
        this.startDialogue = startDialogue;
        this.endDialogue = endDialogue;
    }

    public static String getDialogue(int i, NPC npc) {
        if (dialogueFile.exists()) {
            String dialogue = dialogueFile.readString();
            return Utils.getLine(dialogue, i + npc.startDialogue);
        } else throw new RuntimeException("Dialogue file does not exist\nThis should not happen");
    }

    public static void writeDialogue() {
        dialogueFile.writeString("I am a npc\n", false);
        dialogueFile.writeString("test2\n", true);
        dialogueFile.writeString("testicals\n", true);
        dialogueFile.writeString("1111\n", true);
        dialogueFile.writeString("22222\n", true);
    }
}
