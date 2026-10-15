package luckynoslacky.luckyui.gui;

import java.io.IOException;
import java.util.stream.Collectors;

import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import luckynoslacky.luckyresponse.LuckyNoQuips;

/** Drives the production GUI from an isolated subprocess test. */
public final class LuckyNoGuiProcessProbe extends Application {
    private static final String BYE_SCENARIO = "bye";
    private static final String CREATE_SCENARIO = "create";
    private static final String LIST_SCENARIO = "list";
    private static final String PERSISTED_DESCRIPTION =
            "increment six gui persistence";

    private static String scenario;

    /** Creates the JavaFX subprocess probe. */
    public LuckyNoGuiProcessProbe() {
    }

    /**
     * Starts the probe with the requested GUI scenario.
     *
     * @param args scenario name supplied to the subprocess
     */
    public static void main(String[] args) {
        if (args.length != 1) {
            throw new IllegalArgumentException("Exactly one scenario is required.");
        }
        scenario = args[0];
        Application.launch(LuckyNoGuiProcessProbe.class, args);
    }

    /**
     * Starts the production GUI before running the selected scenario.
     *
     * @param stage primary JavaFX stage
     * @throws IOException if the production GUI cannot be initialized
     */
    @Override
    public void start(Stage stage) throws IOException {
        new LuckyNoGui().start(stage);
        Platform.runLater(() -> runScenario(stage.getScene().getRoot()));
    }

    /** Runs one scenario against the fully initialized production window. */
    private static void runScenario(Parent root) {
        switch (scenario) {
            case BYE_SCENARIO -> runByeScenario(root);
            case CREATE_SCENARIO -> runCreateScenario(root);
            case LIST_SCENARIO -> runListScenario(root);
            default -> throw new IllegalArgumentException(
                    "Unknown GUI probe scenario: " + scenario);
        }
    }

    /** Submits bye and reports the visible state before the delayed exit. */
    private static void runByeScenario(Parent root) {
        TextField input = getInput(root);
        submitCommand(input, "bye");
        System.out.println(
                "GOODBYE_RESULT=" + findLastMessage(root)
                        + "|inputEmpty=" + input.getText().isEmpty()
                        + "|inputDisabled=" + input.isDisabled());
        System.out.flush();
    }

    /** Creates a task through the GUI and exits after the save completes. */
    private static void runCreateScenario(Parent root) {
        TextField input = getInput(root);
        submitCommand(input, "todo " + PERSISTED_DESCRIPTION);
        System.out.println("TASK_CREATED=" + findLastMessage(root)
                .contains(PERSISTED_DESCRIPTION));
        System.out.flush();
        exitAfterBriefDelay();
    }

    /** Lists the task loaded by a fresh GUI process. */
    private static void runListScenario(Parent root) {
        TextField input = getInput(root);
        submitCommand(input, "list");
        System.out.println("LOAD_ERROR=" + hasLoadError(root));
        System.out.println("LIST_RESPONSE=" + findLastMessage(root)
                .replace("\n", "\\n"));
        System.out.flush();
        exitAfterBriefDelay();
    }

    /** Returns the command field from the production window. */
    private static TextField getInput(Parent root) {
        return (TextField) root.lookup("#userInput");
    }

    /** Submits one command through the production text-field action handler. */
    private static void submitCommand(TextField input, String command) {
        input.setText(command);
        input.fireEvent(new ActionEvent());
    }

    /** Allows pending JavaFX callbacks to finish before stopping the process. */
    private static void exitAfterBriefDelay() {
        PauseTransition pause = new PauseTransition(Duration.millis(100.0));
        pause.setOnFinished(event -> Platform.exit());
        pause.play();
    }

    /** Checks whether the startup dialogue contains a load-error response. */
    private static boolean hasLoadError(Parent root) {
        VBox dialogueContainer = (VBox) root.lookup("#dialogContainer");
        return dialogueContainer.getChildren().stream()
                .map(node -> (DialogueBox) node)
                .anyMatch(dialogue -> dialogueContains(dialogue,
                        LuckyNoQuips.loadErrorMessage()));
    }

    /** Checks whether one dialogue row contains the supplied message. */
    private static boolean dialogueContains(DialogueBox dialogue, String message) {
        for (Node child : dialogue.getChildren()) {
            if (child instanceof VBox messageContainer) {
                for (Node messageNode : messageContainer.getChildren()) {
                    if (messageNode instanceof Label label
                            && label.getText().equals(message)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /** Returns the text in the latest dialogue row. */
    private static String findLastMessage(Parent root) {
        VBox dialogueContainer = (VBox) root.lookup("#dialogContainer");
        DialogueBox latestDialogue = (DialogueBox) dialogueContainer.getChildren()
                .get(dialogueContainer.getChildren().size() - 1);
        return findVisibleText(latestDialogue);
    }

    /** Collects visible labels from a dialogue. */
    private static String findVisibleText(Node node) {
        if (node instanceof Label label) {
            return label.getText();
        }
        if (node instanceof Parent parent) {
            return parent.getChildrenUnmodifiable().stream()
                    .map(LuckyNoGuiProcessProbe::findVisibleText)
                    .filter(text -> !text.isEmpty())
                    .collect(Collectors.joining("\n"));
        }
        return "";
    }
}
