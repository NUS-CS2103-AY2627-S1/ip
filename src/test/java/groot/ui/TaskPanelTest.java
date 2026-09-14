package groot.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import groot.testutil.FxTestRuntime;
import groot.testutil.IsolatedProcess;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Tests the live task panel, resizing, and independent conversation scrolling.
 */
public class TaskPanelTest {
    @TempDir
    public Path directory;

    @Test
    public void panel_savedTasksAndCommands_refreshesWithoutChangingTaskOrder() throws Exception {
        Files.createDirectories(directory.resolve("data"));
        Files.writeString(directory.resolve("data/groot.txt"),
                "T | 0 | Read notes\nD | 1 | Report | 2026-09-18\nE | 0 | Meeting | Fri 2pm | Fri 3pm\n");
        assertEquals("passed\n", IsolatedProcess.run(directory, PanelScenario.class, ""));
    }

    /**
     * Keeps the same stage across several JavaFX pulses to verify scrolling and layout.
     */
    public static class PanelScenario {
        private static Stage stage;
        private static Parent root;

        /**
         * Exercises the view-only panel through actual command submissions.
         */
        public static void main(String[] args) throws Exception {
            FxTestRuntime.run(() -> {
                stage = new Stage();
                new Main().start(stage);
                root = stage.getScene().getRoot();
                assertEquals("3 tasks · 1 completed", ((Label) root.lookup("#taskCount")).getText());
                assertEquals(3, cards().getChildren().size());
                assertTrue(cardText().contains("Read notes"));
                assertTrue(cardText().contains("Due Sep 18 2026"));
                assertTrue(cardText().contains("Fri 2pm → Fri 3pm"));
                assertTrue(cards().lookupAll(".button").isEmpty());
                String before = cardText();
                for (String command : new String[]{"find Report", "sort -r", "unknown", "delete 99"}) {
                    send(command);
                    assertEquals(before, cardText());
                }
                verifyFailedSaves();
                send("mark 1");
                assertEquals("3 tasks · 2 completed", ((Label) root.lookup("#taskCount")).getText());
                send("unmark 1");
                send("delete 1");
                assertEquals(2, cards().getChildren().size());
                assertTrue(cardText().startsWith("01"));
                for (int i = 0; i < 12; i++) {
                    send("todo A long description that wraps comfortably inside a narrow task card " + i);
                }
                stage.setWidth(800);
                stage.setHeight(600);
            }, () -> {
                root.applyCss();
                root.layout();
                ScrollPane chat = (ScrollPane) root.lookup("#scrollPane");
                ScrollPane tasks = (ScrollPane) root.lookup("#taskScroll");
                assertEquals(chat.getVmax(), chat.getVvalue());
                chat.setVvalue(0);
                tasks.setVvalue(1);
                assertEquals(0, chat.getVvalue());
                assertFalse(chat.vvalueProperty().isBound());
                assertFalse(tasks.vvalueProperty().isBound());
                Label description = (Label) cards().lookup(".task-description");
                assertTrue(description.isWrapText());
                assertTrue(description.getWidth() <= cards().getWidth());
                assertTrue(root.lookup("#userInput").getBoundsInParent().getWidth() > 100);
                assertTrue(root.lookup("#sendButton").getBoundsInParent().getWidth() >= 74);
                send("help");
            }, () -> {
                assertEquals(1, ((ScrollPane) root.lookup("#scrollPane")).getVvalue());
                for (int i = 0; i < 14; i++) {
                    send("delete 1");
                }
                assertEquals("0 tasks · 0 completed", ((Label) root.lookup("#taskCount")).getText());
                assertTrue(cardText().contains("Room to grow"));
                stage.close();
            });
            System.out.println("passed");
        }

        private static VBox cards() {
            return (VBox) root.lookup("#taskContainer");
        }

        /**
         * Reads labels in display order to compare the visible panel before and after commands.
         */
        private static String cardText() {
            return labelText(cards());
        }

        /**
         * Traverses child nodes in their layout order instead of relying on lookup-set ordering.
         */
        private static String labelText(Node node) {
            if (node instanceof Label label) {
                return label.getText() + "|";
            }
            StringBuilder text = new StringBuilder();
            if (node instanceof Parent parent) {
                parent.getChildrenUnmodifiable().forEach(child -> text.append(labelText(child)));
            }
            return text.toString();
        }

        /**
         * Makes saves fail and checks the refreshed panel reflects the rolled-back model.
         */
        private static void verifyFailedSaves() {
            Path data = Path.of("data/groot.txt");
            Path backup = Path.of("data/backup.txt");
            String before = cardText();
            try {
                Files.move(data, backup);
                Files.createDirectory(data);
                try {
                    for (String command : new String[]{"todo rejected", "mark 1", "unmark 2", "delete 2"}) {
                        send(command);
                        assertEquals(before, cardText());
                        assertEquals("3 tasks · 1 completed", ((Label) root.lookup("#taskCount")).getText());
                        VBox dialogs = (VBox) root.lookup("#dialogContainer");
                        assertTrue(labelText(dialogs.getChildren().getLast()).contains("Error:"));
                    }
                } finally {
                    Files.delete(data);
                    Files.move(backup, data);
                }
            } catch (IOException error) {
                throw new UncheckedIOException(error);
            }
        }

        private static void send(String command) {
            TextField input = (TextField) root.lookup("#userInput");
            Button send = (Button) root.lookup("#sendButton");
            input.setText(command);
            send.fire();
        }
    }
}
