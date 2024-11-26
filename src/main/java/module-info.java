module dk.easv.mytunes {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.microsoft.sqlserver.jdbc;
    requires java.sql;
    requires java.naming;


    opens dk.easv.mytunes to javafx.fxml;
    exports dk.easv.mytunes;
    exports dk.easv.mytunes.ui;
    opens dk.easv.mytunes.ui to javafx.fxml;
}