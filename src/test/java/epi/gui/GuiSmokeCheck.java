package epi.gui;

import java.awt.image.BufferedImage;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import javax.imageio.ImageIO;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;

/**
 * Runs optional JavaFX scene checks without opening a window or reading personal task data.
 * Gradle's guiSmokeTest task supplies an isolated working directory and the preview destination.
 */
public final class GuiSmokeCheck {
    private GuiSmokeCheck() {
    }

    /**
     * Starts JavaFX once and propagates a failed scene check to Gradle.
     *
     * @param args Unused command-line arguments.
     * @throws Exception If JavaFX cannot start, a check fails, or a preview cannot be written.
     */
    public static void main(String[] args) throws Exception {
        Path reports = Path.of(System.getProperty("epi.guiReportDirectory"));
        Files.createDirectories(reports);
        Platform.startup(() -> { });
        Platform.setImplicitExit(false);
        FutureTask<Void> checks = new FutureTask<>(() -> {
            checkScenes(reports);
            return null;
        });
        try {
            Platform.runLater(checks);
            checks.get(30, TimeUnit.SECONDS);
        } finally {
            Platform.exit();
        }
    }

    /** Exercises the actual FXML controller before checking compact, default, and wide layouts. */
    private static void checkScenes(Path reports) throws Exception {
        BorderPane root = createScene(600, 700);
        submit(root, "todo read book", false);
        submit(root, "event discuss the project plan and prepare the final demonstration with the team"
                + " /from 2019-12-02 1400 /to 2019-12-02 1600", true);
        submit(root, "deadline return library books /by 2019-12-01 1800", false);
        submit(root, "mark 1", true);
        submit(root, "list", false);
        check(lastReply(root).getText().contains("1. [T][X] read book"), "Full-list numbering or status changed");
        check(lastReply(root).getText().split("\n").length == 4, "A list response must stay in one message");
        int rowCount = conversation(root).getChildren().size();
        TextField input = (TextField) root.lookup("#userInput");
        input.setText("   ");
        input.fireEvent(new ActionEvent());
        check(conversation(root).getChildren().size() == rowCount, "Blank input added a message");
        submit(root, "unknown-command", false);
        check(lastReply(root).getText().equals("I do not understand what that means, Human."),
                "The existing error response changed");
        System.out.println("PASS: Enter/Send dispatch, blank input, complete list response, and existing errors");

        checkSize(reports, "compact", 420, 480);
        checkSize(reports, "default", 600, 700);
        checkSize(reports, "wide", 1000, 700);
        checkResize();

        BorderPane stress = createScene(420, 480);
        submit(stress, "todo " + "a".repeat(200), false);
        submit(stress, "list", false);
        checkLayout(stress);
        savePreview(stress, reports.resolve("long-text.png"));
        System.out.println("PASS: a long unbroken description wraps at compact width");
        checkErrorHandling(reports);
        System.out.println("All GUI smoke checks passed. Scene previews: " + reports);
    }

    /** Checks semantic error cards and protected data using only this run's isolated task file. */
    private static void checkErrorHandling(Path reports) throws Exception {
        BorderPane root = createScene(420, 480);
        submit(root, "unknown-command", false);
        checkErrorCard(root);
        submit(root, "deadline book /by 2020-02-30 1200", true);
        checkErrorCard(root);
        submit(root, "event meeting /from 2020-01-01 1200 /to 2020-01-01 1200", false);
        checkErrorCard(root);
        savePreview(root, reports.resolve("errors.png"));
        submit(root, "todo Invalid date format! Meow!", true);
        check(conversation(root).getChildren().getLast().lookup(".error-message") == null,
                "Successful user text was mistaken for an error");
        check(lastReply(root).getText().contains("I have added this task:"), "Valid addition did not succeed");

        Path taskFile = Path.of("data/epi.txt");
        String damaged = "T | 1 | valid task\nbroken record\nT | 0 | last task\n";
        Files.writeString(taskFile, damaged);
        BorderPane recovered = createScene(600, 700);
        check(conversation(recovered).getChildren().size() == 2, "Expected greeting and one startup warning card");
        checkErrorCard(recovered);
        check(lastReply(recovered).getText().contains("line 2"), "The damaged line was not identified");
        check(lastReply(recovered).getText().contains("Repair it and restart Epi."), "Missing recovery guidance");
        submit(recovered, "list", false);
        check(lastReply(recovered).getText().contains("1. [T][X] valid task"), "Valid records were not recovered");
        check(lastReply(recovered).getText().contains("2. [T][ ] last task"), "Recovered ordering was lost");
        submit(recovered, "delete 1", true);
        checkErrorCard(recovered);
        check(Files.readString(taskFile).equals(damaged), "A damaged file was overwritten");
        savePreview(recovered, reports.resolve("storage-warning.png"));

        String repaired = "T | 0 | saved task\n";
        Files.writeString(taskFile, repaired);
        BorderPane unavailable = createScene(420, 480);
        check(conversation(unavailable).getChildren().size() == 1, "A repaired file still triggers warnings");
        Files.delete(taskFile);
        Files.createDirectory(taskFile);
        submit(unavailable, "mark 1", false);
        checkErrorCard(unavailable);
        check(lastReply(unavailable).getText().contains("No task changes were kept"), "Save failure was hidden");
        submit(unavailable, "list", true);
        check(lastReply(unavailable).getText().contains("1. [T][ ] saved task"), "A failed save changed live tasks");
        check(Files.isDirectory(taskFile), "An unavailable task path was replaced");
        Files.delete(taskFile);
        Files.writeString(taskFile, repaired);
        System.out.println("PASS: labelled error cards, error-like user text,"
                + " startup recovery, and failed-save rollback");
    }

    /** Verifies an error has an explicit label and usable layout, without relying on colour alone. */
    private static void checkErrorCard(BorderPane root) {
        Node row = conversation(root).getChildren().getLast();
        check(row.lookup(".error-message") != null, "An error is missing its distinct card style");
        check(((Label) row.lookup(".speaker")).getText().equals("Epi - needs attention"),
                "An error is missing its explicit label");
        checkLayout(root);
    }

    /** Loads the real layout and stylesheet into a scene of the requested size. */
    private static BorderPane createScene(double width, double height) throws Exception {
        FXMLLoader loader = new FXMLLoader(GuiSmokeCheck.class.getResource("/epi/gui/MainWindow.fxml"));
        BorderPane root = loader.load();
        new Scene(root, width, height);
        root.applyCss();
        root.layout();
        return root;
    }

    /** Sends input through either the Enter handler or the real Send button. */
    private static void submit(BorderPane root, String command, boolean useButton) {
        TextField input = (TextField) root.lookup("#userInput");
        int before = conversation(root).getChildren().size();
        input.setText(command);
        if (useButton) {
            Button send = (Button) root.lookup(".button");
            send.fire();
        } else {
            input.fireEvent(new ActionEvent());
        }
        root.applyCss();
        root.layout();
        check(conversation(root).getChildren().size() == before + 2, "Expected one user row and one reply row");
        check(input.getText().isEmpty(), "Input was not cleared after sending");
    }

    /** Verifies saved tasks and sorting are presented intact at each supported scene size. */
    private static void checkSize(Path reports, String name, double width, double height) throws Exception {
        BorderPane root = createScene(width, height);
        submit(root, "sort date", false);
        Label reply = lastReply(root);
        check(reply.getText().contains("3. [D][ ] return library books"), "Original sort numbering was lost");
        check(reply.getText().contains("1. [T][X] read book"), "Saved status was lost");
        check(reply.getText().split("\n").length == 4, "A sorted response was split into separate messages");
        checkLayout(root);
        if (width == 1000) {
            check(reply.getWidth() > 430, "A wide window still uses the old fixed message limit");
        }
        savePreview(root, reports.resolve(name + ".png"));
        System.out.println("PASS: " + name + " scene " + width + " x " + height);
    }

    /** Changes the same conversation's width in both directions without rebuilding its messages. */
    private static void checkResize() throws Exception {
        BorderPane root = createScene(600, 700);
        submit(root, "sort date", false);
        Label reply = lastReply(root);
        String originalText = reply.getText();
        for (double width : new double[] {400, 1000, 400}) {
            root.resize(width, 440);
            checkLayout(root);
            check(reply == lastReply(root), "Resizing replaced the reply rather than laying it out again");
            check(reply.getText().equals(originalText), "Resizing altered the response text");
            if (width == 1000) {
                check(reply.getWidth() > 430, "Existing reply did not expand on resize");
            }
        }
        System.out.println("PASS: the same conversation shrinks, grows, and shrinks without losing content");
    }

    /** Checks width bounds, full wrapped heights, avatar crops, and bottom scrolling in the real scene graph. */
    private static void checkLayout(BorderPane root) {
        root.applyCss();
        root.layout();
        VBox conversation = conversation(root);
        ScrollPane scrollPane = (ScrollPane) root.lookup("#scrollPane");
        check(conversation.getWidth() <= scrollPane.getViewportBounds().getWidth() + 1,
                "Conversation overflows horizontally");
        for (Node node : conversation.getChildren()) {
            DialogBox row = (DialogBox) node;
            Label text = (Label) row.lookup(".message-text");
            check(text.getWidth() > 0, "Message has no usable width");
            check(text.getHeight() + 1 >= text.prefHeight(text.getWidth()), "Wrapped response is vertically clipped");
            for (Node child : row.getChildren()) {
                check(child.getBoundsInParent().getMinX() >= -1, "A message starts outside its row");
                check(child.getBoundsInParent().getMaxX() <= row.getWidth() + 1, "A message exceeds its row");
                check(child.getBoundsInParent().getMaxY() <= row.getHeight() + 1, "A message overlaps the next row");
                if (child instanceof ImageView avatar) {
                    check(!avatar.getImage().isError(), "An avatar failed to load");
                    check(avatar.getFitWidth() <= 36, "An avatar is not compact");
                    check(avatar.getClip() instanceof Rectangle, "An avatar is missing its rounded clip");
                    check(avatar.getViewport().getWidth() == avatar.getViewport().getHeight(),
                            "Avatar crop is not square");
                }
            }
        }
        if (conversation.getHeight() > scrollPane.getViewportBounds().getHeight()) {
            check(scrollPane.getVvalue() >= 0.99, "New responses did not scroll into view");
        }
    }

    private static VBox conversation(BorderPane root) {
        return (VBox) root.lookup("#dialogContainer");
    }

    private static Label lastReply(BorderPane root) {
        return (Label) conversation(root).getChildren().getLast().lookup(".message-text");
    }

    /** Writes a JavaFX-rendered scene as a PNG; this is not a full desktop/window screenshot. */
    private static void savePreview(BorderPane root, Path destination) throws Exception {
        WritableImage snapshot = root.snapshot(null, null);
        BufferedImage image = new BufferedImage((int) snapshot.getWidth(), (int) snapshot.getHeight(),
                BufferedImage.TYPE_INT_ARGB);
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                image.setRGB(x, y, snapshot.getPixelReader().getArgb(x, y));
            }
        }
        ImageIO.write(image, "png", destination.toFile());
    }

    private static void check(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }
}
