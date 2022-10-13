module pl.ziabski.imageproc {
    requires javafx.controls;
    requires javafx.fxml;
    requires kotlin.stdlib;


    opens pl.ziabski.imageproc to javafx.fxml;
    exports pl.ziabski.imageproc;
}