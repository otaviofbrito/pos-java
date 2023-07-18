module com.example.demo {
    requires javafx.controls;
    requires javafx.fxml;


    opens pdv.gui to javafx.fxml;
    exports pdv.gui;
}