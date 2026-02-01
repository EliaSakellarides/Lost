package com.lostthesis.minigames;

public interface MiniGame {
    String getName();
    String getInstructions();
    MiniGameState getState();
    String handleButtonInput(String button);
    String handleTextInput(String text);
    String getCurrentDisplay();
    String getButtonALabel();
    String getButtonBLabel();
    String getButtonCLabel();
    void reset();
}
