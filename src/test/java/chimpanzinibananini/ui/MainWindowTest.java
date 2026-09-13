package chimpanzinibananini.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import chimpanzinibananini.ChimpanziniBananini;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

/** Tests the farewell and delayed closing of the actual JavaFX window. */
class MainWindowTest {
    @TempDir
    private Path temporaryDirectory;

    @Test
    void handleUserInput_bye_showsFarewellThenClosesOnlyForValidCommand() throws Exception {
        CompletableFuture<Void> closed = new CompletableFuture<>();
        Platform.startup(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(MainWindow.class.getResource("/view/MainWindow.fxml"));
                Parent root = loader.load();
                ChimpanziniBananini chatbot = new ChimpanziniBananini(temporaryDirectory.resolve("tasks.txt"));
                loader.<MainWindow>getController().setChatbot(chatbot);
                Stage stage = new Stage();
                stage.setScene(new Scene(root));
                stage.show();
                TextField input = (TextField) root.lookup("#userInput");
                input.setText("bye now");
                input.fireEvent(new ActionEvent());

                // Wait beyond the close delay to verify that invalid bye commands leave the window open.
                PauseTransition check = new PauseTransition(Duration.seconds(2.5));
                check.setOnFinished(event -> {
                    try {
                        assertTrue(stage.isShowing());
                        long started = System.nanoTime();
                        stage.setOnHidden(hidden -> {
                            try {
                                assertTrue(System.nanoTime() - started >= TimeUnit.SECONDS.toNanos(1));
                                closed.complete(null);
                            } catch (Throwable failure) {
                                closed.completeExceptionally(failure);
                            }
                        });
                        input.setText("  bye  ");
                        input.fireEvent(new ActionEvent());

                        VBox dialogs = (VBox) root.lookup("#dialogContainer");
                        Label farewell = (Label) dialogs.getChildren().getLast().lookup("#dialog");
                        assertEquals(chatbot.getResponse("bye"), farewell.getText());
                        assertTrue(stage.isShowing());
                    } catch (Throwable failure) {
                        closed.completeExceptionally(failure);
                    }
                });
                check.play();
            } catch (Throwable failure) {
                closed.completeExceptionally(failure);
            }
        });
        try {
            closed.get(10, TimeUnit.SECONDS);
        } finally {
            Platform.exit();
        }
    }
}
