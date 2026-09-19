package mrchatbot.ui;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

/**
 * Displays a fixed-size circular portrait beside a chat message.
 */
public class ProfileAvatar extends StackPane {
    private static final int SIZE = 40;

    /**
     * Creates a robot portrait for the chatbot or a person portrait for the user.
     */
    public ProfileAvatar(boolean isUser) {
        setMinSize(SIZE, SIZE);
        setPrefSize(SIZE, SIZE);
        setMaxSize(SIZE, SIZE);
        setAccessibleText(isUser ? "User profile picture" : "Mr Chatbot profile picture");

        Canvas canvas = new Canvas(SIZE, SIZE);
        GraphicsContext graphics = canvas.getGraphicsContext2D();
        graphics.setFill(Color.web(isUser ? "#dbeafe" : "#ccfbf1"));
        graphics.fillOval(0, 0, SIZE, SIZE);
        if (isUser) {
            drawUser(graphics);
        } else {
            drawRobot(graphics);
        }
        getChildren().add(canvas);
    }

    /**
     * Draws a friendly robot with an antenna and a smiling face.
     */
    private void drawRobot(GraphicsContext graphics) {
        graphics.setStroke(Color.web("#0f766e"));
        graphics.setLineWidth(2);
        graphics.strokeLine(20, 8, 20, 12);
        graphics.setFill(Color.web("#14b8a6"));
        graphics.fillOval(17.5, 4, 5, 5);
        graphics.setFill(Color.web("#0f766e"));
        graphics.fillRoundRect(7, 12, 26, 21, 10, 10);
        graphics.fillRoundRect(3, 18, 4, 10, 3, 3);
        graphics.fillRoundRect(33, 18, 4, 10, 3, 3);
        graphics.setFill(Color.WHITE);
        graphics.fillOval(12, 18, 5, 5);
        graphics.fillOval(23, 18, 5, 5);
        graphics.setStroke(Color.web("#99f6e4"));
        graphics.strokePolyline(new double[]{15, 18, 22, 25}, new double[]{27, 29, 29, 27}, 4);
    }

    /**
     * Draws a person with dark hair and a blue shirt inside the circular background.
     */
    private void drawUser(GraphicsContext graphics) {
        graphics.setFill(Color.web("#2563eb"));
        graphics.fillRoundRect(8, 26, 24, 11, 10, 10);
        graphics.setFill(Color.web("#1e3a8a"));
        graphics.fillOval(10, 6, 20, 23);
        graphics.setFill(Color.web("#f6c9a5"));
        graphics.fillOval(13, 11, 14, 17);
        graphics.setFill(Color.web("#1e3a8a"));
        graphics.fillRoundRect(11, 7, 18, 8, 7, 7);
        graphics.fillOval(16, 18, 2, 2);
        graphics.fillOval(23, 18, 2, 2);
        graphics.setStroke(Color.web("#b86e50"));
        graphics.setLineWidth(1.5);
        graphics.strokeLine(19, 23, 22, 23);
    }
}
