package odysseus;

import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

/**
 * Controls the JavaFX conversation view for Odysseus.
 */
public class MainWindow {
    private static final double FIRST_DIALOG_POSITION = 1.0;
    private static final double MASTHEAD_LINE_INSET = 84;
    private static final double ROUTE_LINE_HEIGHT = 11;
    private static final double ROUTE_LINE_PRIMARY_WAVE_LENGTH = 31;
    private static final double ROUTE_LINE_PRIMARY_AMPLITUDE = 3;
    private static final double ROUTE_LINE_SECONDARY_WAVE_LENGTH = 11;
    private static final double ROUTE_LINE_SECONDARY_AMPLITUDE = 0.8;
    private static final int ROUTE_LINE_STEP = 4;
    private static final int ROUTE_MARKER_DIAMETER = 8;
    private static final int ROUTE_MARKER_VERTICAL_POSITION = 7;
    private static final double ROUTE_LINE_WIDTH = 1.1;
    private static final String WELCOME_MESSAGE = "Ahoy, traveler. I keep the small obligations that"
            + " would otherwise drift away. Tell me what belongs in the ship's log.";
    private static final Color ROUTE_COLOR = Color.web("#A7442B");

    @FXML
    private ScrollPane scrollPane;
    @FXML
    private VBox dialogContainer;
    @FXML
    private TextField userInput;
    @FXML
    private Canvas voyageLine;
    @FXML
    private VBox masthead;

    private Odysseus odysseus;

    /**
     * Sets up automatic scrolling and the masthead's voyage route.
     */
    @FXML
    public void initialize() {
        dialogContainer.heightProperty().addListener((observable, oldHeight, newHeight) ->
                scrollPane.setVvalue(FIRST_DIALOG_POSITION));
        voyageLine.widthProperty().addListener((observable, oldWidth, newWidth) -> drawVoyageLine());
        voyageLine.widthProperty().bind(masthead.widthProperty().subtract(MASTHEAD_LINE_INSET));
        drawVoyageLine();
    }

    /**
     * Injects the chatbot that answers commands entered in this window.
     */
    public void setOdysseus(Odysseus odysseus) {
        this.odysseus = odysseus;
    }

    /**
     * Shows the first Odysseus message once the chatbot has been injected.
     */
    public void showWelcome() {
        addOdysseusDialog(WELCOME_MESSAGE);
        userInput.requestFocus();
    }

    /**
     * Adds the traveler command and Odysseus response to the conversation.
     */
    @FXML
    private void handleUserInput() {
        assert odysseus != null : "Odysseus must be injected before user input is handled";
        String command = userInput.getText().trim();
        if (command.isEmpty()) {
            return;
        }
        dialogContainer.getChildren().add(DialogBox.getUserDialog(command));
        addOdysseusDialog(odysseus.getConversationResponse(command));
        userInput.clear();
    }

    /**
     * Adds a left-aligned Odysseus entry to the ship's log.
     */
    private void addOdysseusDialog(String message) {
        addOdysseusDialog(new ConversationResponse(message, false));
    }

    /**
     * Adds an Odysseus entry using the response's presentation intent.
     */
    private void addOdysseusDialog(ConversationResponse response) {
        DialogBox dialog = response.isCommandError()
                ? DialogBox.getErrorDialog(response.message())
                : DialogBox.getOdysseusDialog(response.message());
        dialogContainer.getChildren().add(dialog);
    }

    /**
     * Draws the intentionally imperfect route line in the masthead.
     */
    private void drawVoyageLine() {
        double width = voyageLine.getWidth();
        GraphicsContext graphics = voyageLine.getGraphicsContext2D();
        graphics.clearRect(0, 0, width, voyageLine.getHeight());
        graphics.setStroke(ROUTE_COLOR);
        graphics.setFill(ROUTE_COLOR);
        graphics.setLineWidth(ROUTE_LINE_WIDTH);
        graphics.beginPath();
        for (int position = 0; position <= width; position += ROUTE_LINE_STEP) {
            double height = ROUTE_LINE_HEIGHT
                    + Math.sin(position / ROUTE_LINE_PRIMARY_WAVE_LENGTH) * ROUTE_LINE_PRIMARY_AMPLITUDE
                    + Math.sin(position / ROUTE_LINE_SECONDARY_WAVE_LENGTH) * ROUTE_LINE_SECONDARY_AMPLITUDE;
            if (position == 0) {
                graphics.moveTo(position, height);
            } else {
                graphics.lineTo(position, height);
            }
        }
        graphics.stroke();
        graphics.fillOval(0, ROUTE_MARKER_VERTICAL_POSITION, ROUTE_MARKER_DIAMETER, ROUTE_MARKER_DIAMETER);
        graphics.fillOval(Math.max(0, width - ROUTE_MARKER_DIAMETER), ROUTE_MARKER_VERTICAL_POSITION,
                ROUTE_MARKER_DIAMETER, ROUTE_MARKER_DIAMETER);
    }
}
