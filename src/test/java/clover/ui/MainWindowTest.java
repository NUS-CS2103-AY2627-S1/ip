package clover.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import clover.Main;
import clover.command.CommandResponseStyle;

/** Tests the forest-sprite reactions added to JavaFX command responses. */
class MainWindowTest {

    @Test
    void applicationTitle_returnsClover() {
        assertEquals("Clover", Main.APPLICATION_TITLE);
    }

    @Test
    void addForestSpriteReaction_responseStyles_returnsMatchingShortReaction() {
        assertEquals("Task added", MainWindow.addForestSpriteReaction(
                "Task added", CommandResponseStyle.TASK_ADDED));
        assertEquals("Learner added",
                MainWindow.addForestSpriteReaction("Learner added", CommandResponseStyle.TUTOREE_ADDED));
        assertEquals("Task completed", MainWindow.addForestSpriteReaction(
                "Task completed", CommandResponseStyle.TASK_MARKED));
        assertEquals("Task removed", MainWindow.addForestSpriteReaction(
                "Task removed", CommandResponseStyle.TASK_DELETED));
        assertEquals("Invalid command", MainWindow.addForestSpriteReaction(
                "Invalid command", CommandResponseStyle.ERROR));
        assertEquals("The grove has gathered your quests", MainWindow.addForestSpriteReaction(
                "The grove has gathered your quests", CommandResponseStyle.STANDARD));
    }
}
