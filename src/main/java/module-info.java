module com.monster.videotoaudio.vta {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.kordamp.bootstrapfx.core;
    requires java.desktop;
    requires javafx.media;

    opens com.monster.videotoaudio.vta to javafx.fxml;
    exports com.monster.videotoaudio.vta;
}