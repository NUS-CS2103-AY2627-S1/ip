package anaconda.gui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import anaconda.Anaconda;
import anaconda.testutil.JavaFxTestSupport;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

/**
 * Tests command submission through the actual window with isolated task storage.
 */
public class MainWindowTest {
    @TempDir
    Path temporaryDirectory;

    @BeforeAll
    public static void setUpToolkit() throws InterruptedException {
        JavaFxTestSupport.startToolkit();
    }

    @Test
    public void setAnaconda_submitCommands_rendersDistinctRowsAndClearsInput() throws Exception {
        JavaFxTestSupport.runOnFxThread(() -> {
            FXMLLoader loader = new FXMLLoader(MainWindow.class.getResource("/View/MainWindow.fxml"));
            AnchorPane root = loader.load();
            loader.<MainWindow>getController().setAnaconda(new Anaconda(temporaryDirectory.resolve("tasks.txt")));
            new Scene(root);
            TextField input = (TextField) root.lookup("#userInput");
            Button send = (Button) root.lookup("#sendButton");
            ScrollPane scrollPane = (ScrollPane) root.lookup("#scrollPane");
            VBox dialogs = (VBox) scrollPane.getContent();
            assertTrue(scrollPane.vvalueProperty().isBound());
            assertEquals(1, dialogs.getChildren().size());
            DialogBox greeting = (DialogBox) dialogs.getChildren().getFirst();
            assertTrue(greeting.getStyleClass().contains("success"));
            Label greetingText = (Label) greeting.getChildren().get(1);
            assertEquals("Yo, it's Anaconda. What do you need?", greetingText.getText());
            assertTrue(greeting.getChildren().getFirst() instanceof ImageView);

            for (String command : new String[] {"todo read book", "todo", "nonsense", "clear", "undo", "bye"}) {
                input.setText(command);
                if (command.equals("todo read book")) {
                    input.fireEvent(new ActionEvent());
                } else {
                    send.fire();
                }
                assertEquals("", input.getText());
            }

            root.applyCss();
            root.layout();
            String[] expectedStyles = {"success", "warning", "error", "success", "success", "success"};
            String[] expectedStatuses = {"OK", "Check input", "Error", "OK", "OK", "OK"};
            String[] expectedColors = {"#174d35", "#ffe082", "#b3261e", "#174d35", "#174d35", "#174d35"};
            String[] expectedReplyColors = {"#d9ffe2", "#fff3c4", "#ffe4e1", "#d9ffe2", "#d9ffe2", "#d9ffe2"};
            assertEquals(13, dialogs.getChildren().size());
            for (int i = 1; i < dialogs.getChildren().size(); i += 2) {
                DialogBox command = (DialogBox) dialogs.getChildren().get(i);
                assertFalse(command.getChildren().stream().anyMatch(ImageView.class::isInstance));
                DialogBox reply = (DialogBox) dialogs.getChildren().get(i + 1);
                assertTrue(reply.getChildren().get(0) instanceof ImageView);
                assertTrue(command.getStyleClass().contains(expectedStyles[i / 2]));
                assertEquals(expectedStatuses[i / 2], ((Label) command.getChildren().get(2)).getText());
                assertTrue(reply.getStyleClass().contains(expectedStyles[i / 2]));
                assertEquals(Color.web(expectedColors[i / 2]), command.getBackground().getFills().get(0).getFill());
                Label replyText = (Label) reply.getChildren().get(1);
                assertEquals(Color.web(expectedReplyColors[i / 2]),
                        replyText.getBackground().getFills().get(0).getFill());
            }
            DialogBox added = (DialogBox) dialogs.getChildren().get(2);
            assertTrue(((Label) added.getChildren().get(1)).getText().contains("read book"));
            DialogBox warning = (DialogBox) dialogs.getChildren().get(4);
            assertTrue(((Label) warning.getChildren().get(1)).getText().contains("cannot be empty"));
            assertTrue(((Label) warning.getChildren().get(1)).getText().contains("Format: todo DESCRIPTION"));
            assertTrue(((Label) warning.getChildren().get(1)).getText().contains("Example: todo read book"));
            DialogBox error = (DialogBox) dialogs.getChildren().get(6);
            assertTrue(((Label) error.getChildren().get(1)).getText().contains("Hang on."));
            assertTrue(((Label) error.getChildren().get(1)).getText().contains("You can ask for help."));
            DialogBox cleared = (DialogBox) dialogs.getChildren().get(8);
            assertEquals("Fine. Everything's gone.", ((Label) cleared.getChildren().get(1)).getText());
            DialogBox restored = (DialogBox) dialogs.getChildren().get(10);
            assertTrue(((Label) restored.getChildren().get(1)).getText().contains("1.[T][ ] read book"));
            DialogBox goodbye = (DialogBox) dialogs.getChildren().get(12);
            assertEquals(0.5, goodbye.getChildren().get(0).getOpacity());
            return null;
        });
    }

    @Test
    public void setAnaconda_duplicateAddition_rendersPurpleBadgeAndReplyThenAllowsUndo() throws Exception {
        JavaFxTestSupport.runOnFxThread(() -> {
            FXMLLoader loader = new FXMLLoader(MainWindow.class.getResource("/View/MainWindow.fxml"));
            AnchorPane root = loader.load();
            loader.<MainWindow>getController().setAnaconda(new Anaconda(temporaryDirectory.resolve("tasks.txt")));
            new Scene(root, 400, 600);
            TextField input = (TextField) root.lookup("#userInput");
            Button send = (Button) root.lookup("#sendButton");
            VBox dialogs = (VBox) ((ScrollPane) root.lookup("#scrollPane")).getContent();
            for (String command : new String[] {"todo book", "todo book", "undo"}) {
                input.setText(command);
                send.fire();
            }
            root.applyCss();
            root.layout();
            DialogBox duplicate = (DialogBox) dialogs.getChildren().get(3);
            assertTrue(duplicate.getStyleClass().contains("duplicate"));
            assertFalse(duplicate.getStyleClass().contains("success"));
            assertEquals("Duplicate", ((Label) duplicate.getChildren().get(2)).getText());
            assertEquals(Color.web("#6f42a6"), duplicate.getBackground().getFills().getFirst().getFill());
            DialogBox reply = (DialogBox) dialogs.getChildren().get(4);
            Label replyText = (Label) reply.getChildren().get(1);
            assertEquals(Color.web("#f1e7fb"), replyText.getBackground().getFills().getFirst().getFill());
            assertTrue(replyText.getText().contains("This task already exists."));
            assertTrue(replyText.getText().contains("We can undo"));
            DialogBox undo = (DialogBox) dialogs.getChildren().get(5);
            assertEquals("OK", ((Label) undo.getChildren().get(2)).getText());
            assertEquals(Color.web("#174d35"), undo.getBackground().getFills().getFirst().getFill());
            Label undoText = (Label) ((DialogBox) dialogs.getChildren().get(6)).getChildren().get(1);
            assertTrue(undoText.getText().contains("1.[T][ ] book"));
            assertFalse(undoText.getText().contains("2.[T]"));
            return null;
        });
    }

    @Test
    public void setAnaconda_corruptedFile_showsRedStartupWarning() throws Exception {
        Path file = temporaryDirectory.resolve("corrupt.txt");
        Files.writeString(file, "corrupted data");
        JavaFxTestSupport.runOnFxThread(() -> {
            FXMLLoader loader = new FXMLLoader(MainWindow.class.getResource("/View/MainWindow.fxml"));
            AnchorPane root = loader.load();
            loader.<MainWindow>getController().setAnaconda(new Anaconda(file));
            VBox dialogs = (VBox) ((ScrollPane) root.lookup("#scrollPane")).getContent();
            assertEquals(2, dialogs.getChildren().size());
            DialogBox warning = (DialogBox) dialogs.getChildren().getLast();
            assertTrue(warning.getStyleClass().contains("error"));
            assertTrue(((Label) warning.getChildren().get(1)).getText().contains("Starting with a new empty list."));
            return null;
        });
    }

    @Test
    public void initialize_resizeWindow_keepsInputBarAlignedAndSeparateFromConversation() throws Exception {
        JavaFxTestSupport.runOnFxThread(() -> {
            FXMLLoader loader = new FXMLLoader(MainWindow.class.getResource("/View/MainWindow.fxml"));
            AnchorPane root = loader.load();
            new Scene(root);
            TextField input = (TextField) root.lookup("#userInput");
            Button send = (Button) root.lookup("#sendButton");
            HBox inputBar = (HBox) root.lookup("#inputBar");
            ScrollPane scrollPane = (ScrollPane) root.lookup("#scrollPane");
            assertEquals("Enter a command\u2026", input.getPromptText());

            for (int[] size : new int[][] {{400, 200}, {400, 600}, {760, 700}}) {
                root.resize(size[0], size[1]);
                root.applyCss();
                root.layout();
                assertEquals(input.getFont().getFamily(), send.getFont().getFamily());
                assertEquals(14.0, input.getFont().getSize());
                assertEquals(14.0, send.getFont().getSize());
                assertEquals(input.getHeight(), send.getHeight());
                assertEquals(input.getLayoutY(), send.getLayoutY());
                assertTrue(input.getWidth() >= 280);
                assertTrue(input.getBoundsInParent().getMaxX() < send.getBoundsInParent().getMinX());
                assertTrue(send.getBoundsInParent().getMaxX() <= inputBar.getWidth());
                assertTrue(scrollPane.getBoundsInParent().getMaxY() <= inputBar.getLayoutY());
                assertEquals(size[1], inputBar.getBoundsInParent().getMaxY());
            }
            return null;
        });
    }
}
