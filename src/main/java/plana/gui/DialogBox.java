package plana.gui;

import java.io.IOException;
import java.util.Collections;

import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import plana.command.CommandType;

/**
 * Represents a chat bubble containing a speaker marker and message text.
 */
public class DialogBox extends HBox {
    private static final double MINIMUM_MESSAGE_WIDTH = 220;
    private static final double USER_MESSAGE_WIDTH_RATIO = 0.72;
    private static final double PLANA_MESSAGE_WIDTH_RATIO = 0.88;
    private static final double USER_MESSAGE_MAXIMUM_WIDTH = 420;
    private static final double PLANA_MESSAGE_MAXIMUM_WIDTH = 620;
    private static final double USER_MESSAGE_HORIZONTAL_SPACE = 12;
    private static final double PLANA_MESSAGE_HORIZONTAL_SPACE = 48;

    @FXML
    private Label dialog;

    @FXML
    private ImageView displayPicture;

    private DialogBox(String text, Avatar avatar) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/view/DialogBox.fxml"));
            fxmlLoader.setController(this);
            fxmlLoader.setRoot(this);
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to load Plana's dialog bubble.", exception);
        }

        dialog.setText(text);
        displayPicture.setImage(avatar.getImage());
    }

    private void flip() {
        ObservableList<Node> children = FXCollections.observableArrayList(getChildren());
        Collections.reverse(children);
        getChildren().setAll(children);
        setAlignment(Pos.TOP_LEFT);
        dialog.getStyleClass().add("reply-label");
        dialog.getStyleClass().add("plana-label");
        configureMessageWidth(PLANA_MESSAGE_WIDTH_RATIO, PLANA_MESSAGE_MAXIMUM_WIDTH,
                PLANA_MESSAGE_HORIZONTAL_SPACE);
    }

    private void changeDialogStyle(CommandType commandType, boolean isError) {
        if (isError) {
            dialog.getStyleClass().add("error-label");
            return;
        }

        if (commandType == null) {
            dialog.getStyleClass().add("welcome-label");
            return;
        }

        switch (commandType) {
            case TODO:
            case DEADLINE:
            case EVENT:
                dialog.getStyleClass().add("add-label");
                break;
            case MARK:
            case UNMARK:
                dialog.getStyleClass().add("marked-label");
                break;
            case DELETE:
                dialog.getStyleClass().add("delete-label");
                break;
            default:
                break;
        }
    }

    /**
     * Creates a right-aligned chat bubble for the user's message.
     *
     * @param text user message to display.
     * @return right-aligned user dialog bubble.
     */
    public static DialogBox getUserDialog(String text) {
        DialogBox dialogBox = new DialogBox(text, Avatar.COOL);
        dialogBox.getChildren().remove(dialogBox.displayPicture);
        dialogBox.setAlignment(Pos.TOP_RIGHT);
        dialogBox.dialog.getStyleClass().add("user-label");
        dialogBox.configureMessageWidth(USER_MESSAGE_WIDTH_RATIO, USER_MESSAGE_MAXIMUM_WIDTH,
                USER_MESSAGE_HORIZONTAL_SPACE);
        return dialogBox;
    }

    /**
     * Creates a left-aligned chat bubble for a Plana response.
     *
     * @param text Plana response to display.
     * @param commandType command type used to style the response bubble.
     * @return left-aligned Plana dialog bubble.
     */
    public static DialogBox getPlanaDialog(String text, CommandType commandType) {
        return getPlanaDialog(text, commandType, false);
    }

    /**
     * Creates a left-aligned Plana response bubble with a status-specific style.
     *
     * @param text Plana response to display.
     * @param commandType command type used to style a successful response.
     * @param isError whether the response describes a command error.
     * @return left-aligned Plana response bubble.
     */
    public static DialogBox getPlanaDialog(String text, CommandType commandType, boolean isError) {
        DialogBox dialogBox = new DialogBox(text, Avatar.forResponse(commandType, isError));
        dialogBox.flip();
        dialogBox.changeDialogStyle(commandType, isError);
        if (commandType == CommandType.HELP && !isError) {
            dialogBox.formatHelpText(text);
        }
        return dialogBox;
    }

    /**
     * Applies rich text formatting to the help message so commands are easier to scan.
     *
     * @param helpText help message to format.
     */
    private void formatHelpText(String helpText) {
        TextFlow helpFlow = new TextFlow();
        helpFlow.prefWidthProperty().bind(dialog.maxWidthProperty().subtract(20));
        helpFlow.maxWidthProperty().bind(dialog.maxWidthProperty().subtract(20));
        for (String line : helpText.split("\\R", -1)) {
            Text helpLine = new Text(line + "\n");
            helpLine.setFont(Font.font("Monospaced", isCommandLine(line)
                    ? FontWeight.BOLD : FontWeight.NORMAL, 12));
            helpFlow.getChildren().add(helpLine);
        }
        dialog.setText("");
        dialog.setGraphic(helpFlow);
        dialog.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
    }

    /**
     * Limits a message width according to the available conversation width.
     *
     * @param widthRatio proportion of the available dialog row used by the message.
     * @param maximumWidth largest width the message can occupy.
     * @param horizontalSpace space reserved for padding and an optional avatar.
     */
    private void configureMessageWidth(double widthRatio, double maximumWidth, double horizontalSpace) {
        dialog.maxWidthProperty().bind(Bindings.min(maximumWidth,
                Bindings.max(MINIMUM_MESSAGE_WIDTH, widthProperty().multiply(widthRatio).subtract(horizontalSpace))));
    }

    /**
     * Checks whether a help line contains a section heading or command syntax.
     *
     * @param line help line to inspect.
     * @return true when the line should be emphasized.
     */
    private boolean isCommandLine(String line) {
        String trimmedLine = line.trim();
        return trimmedLine.endsWith("commands:")
                || trimmedLine.startsWith("[/")
                || (line.startsWith("  ") && !line.startsWith("      "));
    }

    /**
     * Creates a left-aligned Plana dialog bubble for the startup banner.
     *
     * @param text Plana's startup banner and decorative art.
     * @return left-aligned banner bubble.
     */
    public static DialogBox getPlanaBannerDialog(String text) {
        DialogBox dialogBox = new DialogBox(text, Avatar.COOL);
        dialogBox.flip();
        dialogBox.dialog.setWrapText(false);
        dialogBox.dialog.getStyleClass().add("banner-label");
        return dialogBox;
    }

    /**
     * Represents the visual state that accompanies a Plana response.
     */
    private enum Avatar {
        COOL("/images/plana-cool.png"),
        CONFUSED("/images/plana-confused.png"),
        TASK_SUCCESS("/images/plana-task-success.png");

        private final Image image;

        /**
         * Creates an avatar from a packaged image resource.
         *
         * @param resourcePath absolute classpath location of the avatar image.
         */
        Avatar(String resourcePath) {
            var resource = Main.class.getResource(resourcePath);
            if (resource == null) {
                throw new IllegalStateException("Unable to load Plana avatar: " + resourcePath);
            }
            image = new Image(resource.toExternalForm());
        }

        /**
         * Returns the image shown for this avatar state.
         *
         * @return packaged avatar image.
         */
        Image getImage() {
            return image;
        }

        /**
         * Returns the avatar appropriate for a command response.
         *
         * @param commandType parsed type of the command that produced the response.
         * @param isError whether Plana could execute the command.
         * @return avatar that expresses the response state.
         */
        static Avatar forResponse(CommandType commandType, boolean isError) {
            if (isError && commandType == CommandType.UNKNOWN) {
                return CONFUSED;
            }
            if (!isError && isPlannerOperation(commandType)) {
                return TASK_SUCCESS;
            }
            return COOL;
        }

        private static boolean isPlannerOperation(CommandType commandType) {
            if (commandType == null) {
                return false;
            }
            return switch (commandType) {
                case TODO, DEADLINE, EVENT, LIST, ON, FIND, DELETE, MARK, UNMARK, CLIENT -> true;
                default -> false;
            };
        }
    }
}
