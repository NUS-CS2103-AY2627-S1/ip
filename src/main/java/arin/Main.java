package arin;

import java.util.Objects;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

/**
 * Displays Arin's welcome window and explains how to open the task console.
 */
public class Main extends Application {
    /**
     * Shows the robot artwork with the product name and console instructions.
     *
     * @param stage Primary stage provided by JavaFX.
     */
    @Override
    public void start(Stage stage) {
        Label title = new Label("Hello, I'm Arin.");
        title.setFont(Font.font("System", FontWeight.BOLD, 28));
        title.setTextFill(Color.web("#145c82"));

        Label tagline = new Label("Your Wilderness Explorer task companion.");
        tagline.setFont(Font.font("System", 14));

        Image robotImage = new Image(Objects.requireNonNull(
                Main.class.getResource("/images/arin-avatar.jpg"), "Robot artwork is missing").toExternalForm());
        ImageView avatar = new ImageView(robotImage);
        avatar.setFitHeight(260);
        avatar.setPreserveRatio(true);
        avatar.setSmooth(true);
        avatar.setAccessibleText("Arin's white robot avatar with blue eyes");

        Label instructions = new Label("The task console is ready for your next adventure.\n"
                + "To manage tasks, open a terminal in the JAR folder and run:");
        instructions.setWrapText(true);
        instructions.setTextAlignment(TextAlignment.CENTER);
        instructions.setFont(Font.font("System", 13));

        Label command = new Label("java -ea -cp arin-all.jar arin.Arin");
        command.setFont(Font.font("Monospaced", 13));
        command.setStyle("-fx-background-color: #edf5fa; -fx-padding: 12; -fx-background-radius: 8;");

        Label notice = new Label("Welcome window only - enter task commands in the console.");
        notice.setFont(Font.font("System", 11));
        notice.setTextFill(Color.web("#52616b"));

        VBox content = new VBox(16, title, tagline, avatar, instructions, command, notice);
        content.setAlignment(Pos.CENTER);
        content.setPadding(new Insets(28));
        content.setStyle("-fx-background-color: white;");

        Scene scene = new Scene(content, 520, 600);
        stage.setTitle("Arin");
        stage.setMinWidth(520);
        stage.setMinHeight(620);
        stage.setScene(scene);
        stage.show();
    }
}
