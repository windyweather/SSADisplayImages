module net.windyweather.ssadisplayimages {
    requires javafx.controls;
    requires javafx.fxml;
    requires plexus.utils;


    opens net.windyweather.ssadisplayimages to javafx.fxml;
    exports net.windyweather.ssadisplayimages;
}