module dswp.krypto.des.des {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires java.desktop;

    opens dswp.krypto.des.des to javafx.fxml;
    exports dswp.krypto.des.des;
}