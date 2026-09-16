package kibo.gui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.net.URL;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import kibo.Kibo;

/**
 * Tests the main JavaFX window and its command-handling controls.
 */
public class MainWindowTest {
    private static final int JAVAFX_TIMEOUT_SECONDS = 5;

    @BeforeAll
    static void startJavaFx() throws InterruptedException {
        if (!Platform.isFxApplicationThread()) {
            Platform.startup(() -> {
            });
        }
    }

    @AfterAll
    static void stopJavaFx() {
        Platform.exit();
    }

    @Test
    void handleUserInput_listAndBye_displaysDialogsAndDisablesInput() throws Exception {
        FutureTask<Void> testActions = new FutureTask<>(() -> {
            URL mainWindowResource = MainWindowTest.class.getResource("/view/MainWindow.fxml");
            FXMLLoader fxmlLoader = new FXMLLoader(mainWindowResource);
            fxmlLoader.load();
            MainWindow mainWindow = fxmlLoader.getController();
            mainWindow.setKibo(new Kibo());

            VBox dialogContainer = (VBox) fxmlLoader.getNamespace().get("dialogContainer");
            TextField userInput = (TextField) fxmlLoader.getNamespace().get("userInput");
            Button sendButton = (Button) fxmlLoader.getNamespace().get("sendButton");

            assertEquals(1, dialogContainer.getChildren().size());

            userInput.setText("list");
            userInput.fireEvent(new ActionEvent());
            assertEquals(3, dialogContainer.getChildren().size());

            userInput.setText("bye");
            userInput.fireEvent(new ActionEvent());
            assertEquals(5, dialogContainer.getChildren().size());
            assertTrue(userInput.isDisabled());
            assertTrue(sendButton.isDisabled());
            return null;
        });

        Platform.runLater(testActions);
        testActions.get(JAVAFX_TIMEOUT_SECONDS, TimeUnit.SECONDS);
    }

    @Test
    void handleUserInput_errorsThenSuccess_highlightsOnlyErrors() throws Exception {
        FutureTask<Void> testActions = new FutureTask<>(() -> {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/MainWindow.fxml"));
            AnchorPane root = loader.load();
            new Scene(root);
            MainWindow mainWindow = loader.getController();
            mainWindow.setKibo(new Kibo());
            VBox dialogs = (VBox) loader.getNamespace().get("dialogContainer");
            TextField input = (TextField) loader.getNamespace().get("userInput");
            Button sendButton = (Button) loader.getNamespace().get("sendButton");

            root.applyCss();
            Label welcome = (Label) dialogs.getChildren().getFirst().lookup("#dialog");
            Label avatar = (Label) dialogs.getChildren().getFirst().lookup("#avatar");
            assertTrue(welcome.getText().contains("your pocket cheerleader"));
            assertEquals(Color.web("#fff9ec"), root.getBackground().getFills().getFirst().getFill());
            assertEquals(Color.web("#f8cb55"), avatar.getBackground().getFills().getFirst().getFill());

            String[] invalidCommands = {"blah", "todo", "schedule tomorrow", "mark one", ""};
            for (String command : invalidCommands) {
                input.setText(command);
                input.fireEvent(new ActionEvent());
                root.applyCss();

                Label error = (Label) dialogs.getChildren().getLast().lookup("#dialog");
                assertTrue(error.getStyleClass().contains("error-label"));
                assertTrue(error.getText().startsWith("Error\n"));
                assertEquals(Color.web("#8a1c13"), error.getTextFill());
                assertEquals(Color.web("#fff1f0"), error.getBackground().getFills().getFirst().getFill());
                assertFalse(input.isDisabled());
                assertFalse(sendButton.isDisabled());
            }

            input.setText("list");
            input.fireEvent(new ActionEvent());
            root.applyCss();
            Label reply = (Label) dialogs.getChildren().getLast().lookup("#dialog");
            assertFalse(reply.getStyleClass().contains("error-label"));
            assertTrue(reply.getText().contains("Here's your lineup. One step at a time!"));
            assertEquals(Color.WHITE, reply.getBackground().getFills().getFirst().getFill());
            return null;
        });

        Platform.runLater(testActions);
        testActions.get(JAVAFX_TIMEOUT_SECONDS, TimeUnit.SECONDS);
    }

    @Test
    void setKibo_loadingError_highlightsErrorAndDisablesInput() throws Exception {
        FutureTask<Void> testActions = new FutureTask<>(() -> {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/MainWindow.fxml"));
            loader.load();
            MainWindow mainWindow = loader.getController();
            String message = "The saved task on line 1 has an invalid format.";
            mainWindow.setKibo(new Kibo() {
                @Override
                public String getLoadingErrorMessage() {
                    return message;
                }
            });

            VBox dialogs = (VBox) loader.getNamespace().get("dialogContainer");
            Label error = (Label) dialogs.getChildren().getLast().lookup("#dialog");
            assertEquals("Error\n" + message, error.getText());
            assertTrue(error.getStyleClass().contains("error-label"));
            assertEquals(2, dialogs.getChildren().size());
            assertTrue(((TextField) loader.getNamespace().get("userInput")).isDisabled());
            assertTrue(((Button) loader.getNamespace().get("sendButton")).isDisabled());
            return null;
        });

        Platform.runLater(testActions);
        testActions.get(JAVAFX_TIMEOUT_SECONDS, TimeUnit.SECONDS);
    }
}
