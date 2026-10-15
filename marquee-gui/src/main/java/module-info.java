module org.cs2103t.marquee.gui {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.cs2103t.marquee.core;

    opens org.cs2103t.marquee.gui.ui to javafx.fxml;
}
