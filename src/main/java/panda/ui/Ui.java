package panda.ui;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * Provides a consistent way for Panda to send and receive formatted messages.
 */
public final class Ui {
    private static final String MESSAGE_DIVIDER = "____________________________________________________________";
    private static final String MESSAGE_TYPE_APPLICATION_ERROR = "PANDA_MESSAGE_TYPE:APPLICATION_ERROR";
    private static final String MESSAGE_TYPE_EXIT_CONFIRMATION = "PANDA_MESSAGE_TYPE:EXIT_CONFIRMATION";
    private static final String MESSAGE_TYPE_NORMAL = "PANDA_MESSAGE_TYPE:NORMAL";
    private static final String MESSAGE_TYPE_PARSING_ERROR = "PANDA_MESSAGE_TYPE:PARSING_ERROR";

    private static PrintWriter outputWriter = createOutputWriter(OutputStream.nullOutputStream());

    /**
     * Represents the visual category of a message.
     */
    public enum MessageType {
        NORMAL,
        PARSING_ERROR,
        APPLICATION_ERROR,
        EXIT_CONFIRMATION
    }

    /**
     * Represents message text together with its visual category.
     *
     * @param type Visual category of the message.
     * @param text Text displayed to the user.
     */
    public record UiMessage(MessageType type, String text) {
    }

    private Ui() {
        // Utility class: prevent accidental instantiation.
    }

    /**
     * Directs typed messages to the supplied graphical-interface output stream.
     *
     * @param outputStream Stream that receives Panda's messages.
     */
    public static synchronized void directOutputTo(OutputStream outputStream) {
        outputWriter = createOutputWriter(Objects.requireNonNull(outputStream));
    }

    /**
     * Prints the message in a standardized format and flushes it to the configured stream.
     *
     * @param message Text to display.
     */
    public static synchronized void printMessage(String message) {
        writeMessage(message, MessageType.NORMAL);
    }

    /**
     * Prints a parsing error in a standardized format and flushes it to the configured stream.
     *
     * @param message Parsing error text to display.
     */
    public static synchronized void printParsingError(String message) {
        writeMessage(message, MessageType.PARSING_ERROR);
    }

    /**
     * Prints an application error in a standardized format and flushes it to the configured stream.
     *
     * @param message Application error text to display.
     */
    public static synchronized void printApplicationError(String message) {
        writeMessage(message, MessageType.APPLICATION_ERROR);
    }

    /**
     * Prints a request for the frontend to confirm termination.
     *
     * @param message Save result and exit-confirmation text to display.
     */
    public static synchronized void printExitConfirmation(String message) {
        writeMessage(message, MessageType.EXIT_CONFIRMATION);
    }

    /**
     * Prints a message and its type for the graphical interface.
     *
     * @param message Text to display.
     * @param messageType Visual category of the message.
     */
    private static void writeMessage(String message, MessageType messageType) {
        outputWriter.println(MESSAGE_DIVIDER);
        outputWriter.println(getMessageTypeMarker(messageType));
        outputWriter.println(message);
        outputWriter.println(MESSAGE_DIVIDER);
        outputWriter.flush();
    }

    /**
     * Returns the stream marker for a message type.
     *
     * @param messageType Visual category of the message.
     * @return Stream marker representing the message type.
     */
    private static String getMessageTypeMarker(MessageType messageType) {
        return switch (messageType) {
            case NORMAL -> MESSAGE_TYPE_NORMAL;
            case PARSING_ERROR -> MESSAGE_TYPE_PARSING_ERROR;
            case APPLICATION_ERROR -> MESSAGE_TYPE_APPLICATION_ERROR;
            case EXIT_CONFIRMATION -> MESSAGE_TYPE_EXIT_CONFIRMATION;
        };
    }

    /**
     * Reads the next complete typed Panda message from a buffered character stream.
     *
     * @param inputReader Reader connected to Panda's response stream.
     * @return Next typed message, or {@code null} after the stream closes.
     * @throws IOException If the response stream cannot be read.
     */
    public static UiMessage readNextUiMessage(BufferedReader inputReader) throws IOException {
        Objects.requireNonNull(inputReader);
        StringBuilder message = new StringBuilder();
        MessageType messageType = MessageType.NORMAL;
        boolean isReadingFirstLine = false;
        boolean isReadingMessage = false;

        String line;
        while ((line = inputReader.readLine()) != null) {
            if (line.equals(MESSAGE_DIVIDER)) {
                if (isReadingMessage) {
                    return new UiMessage(messageType, message.toString());
                }
                isReadingMessage = true;
                isReadingFirstLine = true;
            } else if (isReadingMessage) {
                if (isReadingFirstLine && line.equals(MESSAGE_TYPE_PARSING_ERROR)) {
                    messageType = MessageType.PARSING_ERROR;
                } else if (isReadingFirstLine && line.equals(MESSAGE_TYPE_APPLICATION_ERROR)) {
                    messageType = MessageType.APPLICATION_ERROR;
                } else if (isReadingFirstLine && line.equals(MESSAGE_TYPE_EXIT_CONFIRMATION)) {
                    messageType = MessageType.EXIT_CONFIRMATION;
                } else if (!(isReadingFirstLine && line.equals(MESSAGE_TYPE_NORMAL))) {
                    if (!message.isEmpty()) {
                        message.append(System.lineSeparator());
                    }
                    message.append(line);
                }
                isReadingFirstLine = false;
            }
        }
        return null;
    }

    /**
     * Creates an automatically flushing writer for an output stream.
     *
     * @param outputStream Stream to wrap.
     * @return Writer configured to use UTF-8.
     */
    private static PrintWriter createOutputWriter(OutputStream outputStream) {
        return new PrintWriter(outputStream, true, StandardCharsets.UTF_8);
    }
}
