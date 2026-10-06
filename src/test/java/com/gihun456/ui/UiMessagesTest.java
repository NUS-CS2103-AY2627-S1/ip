package com.gihun456.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class UiMessagesTest {
    @Test
    public void getTaskCountMessage_formatsDifferentCounts() {
        assertEquals("Player 456, you now have 0 games in your list.", UiMessages.getTaskCountMessage(0));
        assertEquals("Player 456, you now have 1 games in your list.", UiMessages.getTaskCountMessage(1));
        assertEquals("Player 456, you now have 12 games in your list.", UiMessages.getTaskCountMessage(12));
    }
}
