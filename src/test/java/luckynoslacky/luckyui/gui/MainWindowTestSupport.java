package luckynoslacky.luckyui.gui;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;

import org.testfx.api.FxRobot;

import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import luckynoslacky.CommandResult;
import luckynoslacky.LuckyNoSlacky;
import luckynoslacky.luckyresponse.LuckyNoQuips;

/** Shared FXML fixtures, node lookups, and chatbot doubles for GUI tests. */
abstract class MainWindowTestSupport {
    protected Stage stage;

    /** Starts a test with the production chatbot and FXML window. */
    protected void startWindow(Stage stage) throws IOException {
        this.stage = stage;
        loadWindow(new LuckyNoSlacky());
    }

    /** Returns the message text from a dialogue row's text container. */
    protected String getMessageText(DialogueBox dialogue, int dialogueIndex) {
        VBox messageContainer = (VBox) dialogue.getChildren().get(dialogueIndex);
        return messageContainer.getChildren().stream()
                .map(this::findVisibleMessageText)
                .filter(text -> !text.isEmpty())
                .findFirst()
                .orElseThrow();
    }

    /** Finds the first visible response text. */
    private String findVisibleMessageText(Node node) {
        if (node instanceof Label label) {
            return label.getText();
        }
        if (node instanceof Parent parent) {
            return parent.getChildrenUnmodifiable().stream()
                    .map(this::findVisibleMessageText)
                    .filter(text -> !text.isEmpty())
                    .findFirst()
                    .orElse("");
        }
        return "";
    }

    /** Returns the command input from the production FXML scene. */
    protected TextField lookupTextField() {
        return (TextField) stage.getScene().lookup("#userInput");
    }

    /** Returns the Send button from the production FXML scene. */
    protected Button lookupButton() {
        return (Button) stage.getScene().lookup("#sendButton");
    }

    /** Returns the conversation history from the production FXML scene. */
    protected ScrollPane lookupScrollPane() {
        return (ScrollPane) stage.getScene().lookup("#scrollPane");
    }

    /** Replaces the current window content with a supplied test chatbot. */
    protected void showWindowWithChatbot(FxRobot robot, LuckyNoSlacky chatbot) {
        robot.interact(() -> {
            try {
                loadWindow(chatbot);
            } catch (IOException exception) {
                throw new IllegalStateException(
                        "Unable to load the test window.", exception);
            }
        });
    }

    /** Replaces the current window with a chatbot and test exit behavior. */
    protected void showWindowWithChatbot(
            FxRobot robot,
            LuckyNoSlacky chatbot,
            MainWindow.ExitScheduler exitScheduler,
            Runnable exitAction) {
        robot.interact(() -> {
            try {
                loadWindow(chatbot, new MainWindow(exitScheduler, exitAction));
            } catch (IOException exception) {
                throw new IllegalStateException(
                        "Unable to load the test window.", exception);
            }
        });
    }

    /** Loads the production window with the supplied chatbot. */
    private void loadWindow(LuckyNoSlacky chatbot) throws IOException {
        loadWindow(chatbot, new MainWindow());
    }

    /** Loads the production window with a supplied controller. */
    private void loadWindow(
            LuckyNoSlacky chatbot,
            MainWindow controller) throws IOException {
        FXMLLoader loader = new FXMLLoader(
                MainWindow.class.getResource("/view/MainWindow.fxml"));
        loader.setControllerFactory(type -> {
            if (type == MainWindow.class) {
                return controller;
            }
            throw new IllegalArgumentException(
                    "Unexpected FXML controller type.");
        });
        Parent root = loader.load();
        controller.setChatbot(chatbot);
        stage.setScene(new Scene(root, 400.0, 600.0));
        stage.show();
    }

    /** Verifies that a node's rendered bounds remain inside a scene. */
    protected static void assertNodeWithinScene(Node node, Scene scene) {
        Bounds nodeBounds = node.localToScene(node.getBoundsInLocal());
        assertTrue(nodeBounds.getMinX() >= -1.0);
        assertTrue(nodeBounds.getMinY() >= -1.0);
        assertTrue(nodeBounds.getMaxX() <= scene.getWidth() + 1.0);
        assertTrue(nodeBounds.getMaxY() <= scene.getHeight() + 1.0);
    }

    /** Simulates a chatbot whose task data could not be loaded. */
    protected static final class LoadFailingChatbot extends LuckyNoSlacky {
        @Override
        public boolean hasLoadError() {
            return true;
        }
    }

    /** Simulates a full task list for a capacity-error GUI test. */
    protected static final class CapacityFullChatbot extends LuckyNoSlacky {
        @Override
        public boolean hasLoadError() {
            return false;
        }

        @Override
        public CommandResult getResponse(String userInput) {
            return new CommandResult(
                    LuckyNoQuips.taskLimitMessage(), false);
        }
    }

    /** Simulates a chatbot that returns short responses for scroll testing. */
    protected static final class ScrollingChatbot extends LuckyNoSlacky {
        @Override
        public boolean hasLoadError() {
            return false;
        }

        @Override
        public CommandResult getResponse(String userInput) {
            return new CommandResult("scroll response", false);
        }
    }

    /** Simulates a chatbot response that requests application exit. */
    protected static final class GoodbyeChatbot extends LuckyNoSlacky {
        @Override
        public boolean hasLoadError() {
            return false;
        }

        @Override
        public CommandResult getResponse(String userInput) {
            return new CommandResult(LuckyNoQuips.goodbye(), true);
        }
    }

    /** Supplies deterministic chatbot responses to GUI presentation tests. */
    protected static final class StubChatbot extends LuckyNoSlacky {
        private final boolean loadError;
        private final CommandResult response;

        StubChatbot(boolean loadError, CommandResult response) {
            super();
            this.loadError = loadError;
            this.response = response;
        }

        @Override
        public boolean hasLoadError() {
            return loadError;
        }

        @Override
        public CommandResult getResponse(String userInput) {
            return response;
        }
    }

    /** Records delayed exit scheduling so tests can trigger it deterministically. */
    protected static final class RecordingExitScheduler
            implements MainWindow.ExitScheduler {
        private Duration delay;
        private Runnable exitAction;
        private int scheduleCount;

        /** Records a scheduled exit action and its delay. */
        @Override
        public void schedule(Duration delay, Runnable exitAction) {
            this.delay = delay;
            this.exitAction = exitAction;
            scheduleCount++;
        }

        /** Returns how many delayed exits have been scheduled. */
        protected int getScheduleCount() {
            return scheduleCount;
        }

        /** Returns the delay from the most recent scheduled exit. */
        protected Duration getDelay() {
            return delay;
        }

        /** Runs the recorded action to simulate the completed delay. */
        protected void trigger() {
            exitAction.run();
        }
    }
}
