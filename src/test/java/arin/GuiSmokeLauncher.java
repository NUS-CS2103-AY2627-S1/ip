package arin;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Collectors;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * Runs a real-window smoke test against the production GUI from an empty working directory.
 * Run with assertions enabled and the release JAR before test classes on the classpath.
 */
public final class GuiSmokeLauncher {
    private GuiSmokeLauncher() {
    }

    /**
     * Launches the smoke-test application without invoking Java's JavaFX launcher shortcut.
     *
     * @param args Optional {@code capture} argument to keep the verified window open.
     * @throws IOException If bundled native libraries cannot be extracted.
     */
    public static void main(String[] args) throws IOException {
        NativeLibraries.prepare();
        Application.launch(SmokeApplication.class, args);
    }

    /**
     * Drives production controls on the JavaFX application thread.
     */
    public static class SmokeApplication extends Application {
        @Override
        public void start(Stage stage) throws Exception {
            if (Files.exists(Path.of("data", "arin.txt"))) {
                throw new IllegalStateException("Run this smoke test in an empty folder to protect your tasks.");
            }
            new Main().start(stage);
            Platform.runLater(() -> checkWindow(stage));
        }

        /**
         * Checks command entry, all task types, edits, sorting, errors, restart, and layout.
         */
        private void checkWindow(Stage stage) {
            try {
                Scene scene = stage.getScene();
                assert stage.getTitle().equals("Arin");
                assert messages(scene).contains("Loaded 0 tasks");
                send(scene, "todo read a book", true);
                send(scene, "deadline submit iP /by 2026-09-18", false);
                send(scene, "event team meeting /from 2pm /to 4pm", false);
                send(scene, "mark 1", false);
                send(scene, "unmark 1", false);
                send(scene, "sort deadline", false);
                send(scene, "find book", false);
                send(scene, "delete 3", false);
                send(scene, "mark 0", false);
                assert !scene.getRoot().lookupAll(".error-message").isEmpty();
                ((Button) scene.lookup("#helpButton")).fire();
                assert messages(scene).contains("todo <description>");
                send(scene, "sort name", false);
                assert Files.readAllLines(Path.of("data", "arin.txt")).size() == 2;

                // Construct a fresh production window to verify reloading saved data.
                new Main().start(stage);
                scene = stage.getScene();
                assert messages(scene).contains("Loaded 2 tasks");
                send(scene, "list", true);
                assert messages(scene).contains("[D][ ] submit iP");
                assert messages(scene).contains("[T][ ] read a book");
                stage.setWidth(440);
                stage.setHeight(480);
                scene.getRoot().applyCss();
                scene.getRoot().layout();
                assert scene.lookup("#commandInput").getBoundsInParent().getWidth() > 100;
                assert scene.lookup("#sendButton").isVisible();
                stage.setWidth(640);
                stage.setHeight(728);
                System.out.println("GUI_SMOKE_PASS: input, Enter, Send, Help, tasks, errors, resize, restart");
                if (!getParameters().getRaw().contains("capture")) {
                    send(scene, "bye", false);
                    assert scene.lookup("#commandInput").isDisabled();
                }
            } catch (Throwable error) {
                error.printStackTrace();
                System.exit(1);
            }
        }

        private void send(Scene scene, String command, boolean shouldPressEnter) {
            TextField input = (TextField) scene.lookup("#commandInput");
            input.setText(command);
            if (shouldPressEnter) {
                input.fireEvent(new ActionEvent());
            } else {
                ((Button) scene.lookup("#sendButton")).fire();
            }
            assert input.getText().isEmpty();
        }

        private String messages(Scene scene) {
            return scene.getRoot().lookupAll(".message-text").stream().map(node -> ((Label) node).getText())
                    .collect(Collectors.joining("\n"));
        }
    }
}
