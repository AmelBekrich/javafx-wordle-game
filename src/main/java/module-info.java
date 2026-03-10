module com.wordle.wordlegame {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.kordamp.bootstrapfx.core;
    requires java.xml;
    requires java.desktop;

    opens com.wordle.wordlegame to javafx.fxml;
    exports com.wordle.wordlegame;
}