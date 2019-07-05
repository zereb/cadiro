module cadiro {
    requires javafx.controls;
    requires javafx.fxml;
    requires jnativehook;
    requires java.desktop;
    requires java.base;
    requires java.logging;

    opens org.zereb to javafx.fxml;
    exports org.zereb;
}