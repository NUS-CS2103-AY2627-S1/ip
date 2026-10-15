package wwaffle.gui;

import java.util.List;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

/** Displays the launch greeting alongside the supplied mascot. */
public class WelcomeBox extends HBox {
    /**
     * Creates a welcome display from the application's existing welcome message.
     *
     * @param message Two-line greeting including the loaded task count.
     */
    public WelcomeBox(String message) {
        super(24);
        setAlignment(Pos.CENTER_LEFT);
        getStyleClass().add("welcome-box");
        Font.loadFont(WelcomeBox.class.getResourceAsStream("/fonts/Inter.ttf"), 18);

        Image image = new Image(WelcomeBox.class.getResourceAsStream("/images/minion-welcome.png"));
        ImageView mascot = new ImageView(image);
        mascot.setFitHeight(110);
        mascot.setPreserveRatio(true);
        mascot.setSmooth(true);
        mascot.setAccessibleText("A waving Minion");

        String[] lines = message.split("\n", 2);
        Text greeting = new Text(lines[0].replace("☕", ""));
        greeting.getStyleClass().add("welcome-title");
        String emojiFamily = List.of("Apple Color Emoji", "Segoe UI Emoji", "Noto Color Emoji")
                .stream().filter(Font.getFamilies()::contains).findFirst().orElse("System");
        Text coffee = new Text("☕");
        coffee.setFont(Font.font(emojiFamily, 24));
        TextFlow heading = new TextFlow(greeting, coffee);
        Label count = new Label(lines.length > 1 ? lines[1] : "");
        count.getStyleClass().add("welcome-count");
        count.setWrapText(true);
        VBox copy = new VBox(8, heading, count);
        copy.setMinWidth(0);
        copy.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(copy, Priority.ALWAYS);
        getChildren().addAll(mascot, copy);
    }
}
