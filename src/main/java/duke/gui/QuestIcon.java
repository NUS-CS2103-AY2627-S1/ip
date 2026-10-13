package duke.gui;

import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;

/** Draws Wangsa's original compass emblem using JavaFX primitives, without external artwork. */
final class QuestIcon extends StackPane {
    /** Creates a fixed-size compass, with a contrasting palette for the user's messages. */
    QuestIcon(double diameter, boolean isUser) {
        setMinSize(diameter, diameter);
        setPrefSize(diameter, diameter);
        setMaxSize(diameter, diameter);
        Circle face = new Circle(diameter / 2 - 2, Color.web(isUser ? "#d4473f" : "#f5ddaa"));
        face.setStroke(Color.web("#303a51"));
        face.setStrokeWidth(2);
        double point = diameter * 0.30;
        double waist = diameter * 0.10;
        Polygon needle = new Polygon(0, -point, waist, 0, 0, point, -waist, 0);
        needle.setFill(Color.web(isUser ? "#fff5e5" : "#303a51"));
        needle.setRotate(isUser ? 35 : -35);
        Circle pivot = new Circle(diameter * 0.045, Color.web("#d4473f"));
        getChildren().addAll(face, needle, pivot);
    }
}
