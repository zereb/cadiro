module cadiro {
    requires javafx.controls;
    requires javafx.fxml;
    requires jnativehook;
    requires java.desktop;
    requires java.base;
    requires java.logging;
    requires java.net.http;

    opens free.zereb to javafx.fxml;
    exports free.zereb;
}