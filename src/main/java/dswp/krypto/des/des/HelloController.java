package dswp.krypto.des.des;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.stage.FileChooser;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.ThreadLocalRandom;

public class HelloController {

    private final DES des = new DES();
    private int keyInputBytes = 0;
    private byte[] fileBytes;

    @FXML private Label labelStatus;

    @FXML private Button buttonEncode;
    @FXML private TextArea fieldPlaintext;
    @FXML void encodeMessage(ActionEvent event) throws Exception {
        // set key if it was not set
        if (fieldKey.getText().isEmpty())
            generateKey(event);

        // get text field input
        byte[] plaintext = fieldPlaintext.getText().getBytes(StandardCharsets.UTF_8);

        // display encoded file
        byte[] encoded = des.encode(plaintext);
        fileBytes = encoded;
        fieldCiphertext.setText(Lib.byteArrayToHexString(encoded));

        // update status
        labelStatus.setText("Wiadomość z pola tesktowego zakodowana.");
    }

    @FXML private Button buttonDecode;
    @FXML private TextArea fieldCiphertext;
    @FXML void decodeMessage(ActionEvent event) throws Exception {
        // get ciphertext input
        byte[] ciphertext = Lib.hexStringToByteArray(fieldCiphertext.getText());

        // display decoded file
        fileBytes = des.decode(ciphertext);
        String decoded = new String(fileBytes);
        fieldPlaintext.setText(decoded);

        labelStatus.setText("Wiadomość z pola tekstowego odkodowana.");
    }

    @FXML private Button buttonGenerateKey;
    @FXML private TextField fieldKey;
    @FXML void generateKey(ActionEvent event) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 8; i++) {
            char b = (char) ThreadLocalRandom.current().nextInt(35, 123);
            sb.append(b);
        }

        des.setKeyString(sb.toString());
        fieldKey.setText(sb.toString());

        System.out.println("buttonGenerateKey: ustawiono nowy klucz dla DES: " + fieldKey.getText());
        labelStatus.setText("Ustawiono nowy klucz.");
    }

    @FXML void keyCheckLimit(KeyEvent event) {
        if (fieldKey.getText().isEmpty())
            return;

        String key = event.getCharacter();

        // pomijaj enter
        if (key.getBytes(StandardCharsets.UTF_8)[0] == 0x0d)
            return;

        // nie pozwalaj na wprowadzanie znakow jezeli juz mamy 8 bajtow
        if (keyInputBytes + key.getBytes(StandardCharsets.UTF_8).length > 8) {
            String text = fieldKey.getText().substring(0, fieldKey.getText().length() - 1);
            fieldKey.setText(text);
        }

        keyInputBytes = fieldKey.getText().getBytes(StandardCharsets.UTF_8).length;
    }

    @FXML void onEnterSetKey(ActionEvent event) {
        if (!des.setKeyString(fieldKey.getText())) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("DES - Błąd");
            alert.setHeaderText("Niepoprawny klucz");
            alert.setContentText("Podany klucz jest niepoprawny.");
            alert.show();
            return;
        }

        System.out.println("fieldkey: ustawiono nowy klucz dla DES: " + fieldKey.getText());
        labelStatus.setText("Ustawiono nowy klucz.");
    }

    @FXML private Button buttonOpenPlaintext;
    @FXML void openPlaintextFile(ActionEvent event) throws Exception {
        // set key if it was not set
        if (fieldKey.getText().isEmpty())
            fieldKey.setText("0x01234567890abcdef");

        // get file path
        Path plaintextFilepath = Lib.getFileFromExplorer("Podaj plik do zaszyfrowania.");

        System.out.println("buttonOpenPlaintext: podany plik: " + plaintextFilepath);

        byte[] fileContent = Files.readAllBytes(plaintextFilepath);
        fileBytes = des.encode(fileContent);
        String encodedHexstring = Lib.byteArrayToHexString(fileBytes);

        // display encoded file
        fieldCiphertext.setText(encodedHexstring);

        System.out.println("buttonOpenPlaintext: zaszyfrowano wiadomosc z pliku");
        labelStatus.setText("Wiadomość z pliku zakodowana.");
    }

    @FXML private Button buttonOpenCiphertext;
    @FXML void openCiphertextFile(ActionEvent event) throws Exception {
        // assuming ciphertext file is a text file of a hex string

        // get file path
        Path ciphertextFilepath = Lib.getFileFromExplorer("Podaj zaszyfrowany plik.");
        System.out.println("buttonOpenCiphertext: podany plik: " + ciphertextFilepath);

        byte[] fileContent = Files.readAllBytes(ciphertextFilepath);
        fileBytes = des.decode(fileContent);
        String decodedHexstring = new String(fileBytes);

        // display decoded file
        fieldPlaintext.setText(decodedHexstring);

        System.out.println("buttonOpenCiphertext: odszyfrowano wiadomosc z pliku");
        labelStatus.setText("Wiadomość z pliku odkodowana.");
    }

    @FXML private Button buttonPlaintextSaveToFile;
    @FXML void savePlaintextToFile(ActionEvent event) {
        if (fileBytes.length == 0) {
            System.out.println("buttonPlaintextSaveToFile: brak wiadomosci w polu tesktowym");
            labelStatus.setText("Brak tesktu w polu zdeszyfrowanej wiadomości");
            return;
        }

        FileChooser fc = new FileChooser();
        File file = fc.showSaveDialog(buttonPlaintextSaveToFile.getScene().getWindow());

        //Set extension filter for text files
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("TXT files (*.txt)", "*.txt");
        fc.getExtensionFilters().add(extFilter);

        if (file == null) {
            labelStatus.setText("Nie podano pliku do zapisu.");
            return;
        }

        saveToFile(file, fileBytes);
    }

    @FXML private Button buttonCiphertextSaveToFile;
    @FXML void saveCiphertextToFile(ActionEvent event) {
        if (fileBytes.length == 0) {
            System.out.println("buttonCiphertextSaveToFile: brak wiadomosci w polu tesktowym");
            labelStatus.setText("Brak tesktu w polu zaszyfrowanej wiadomości");
            return;
        }

        FileChooser fc = new FileChooser();
        File file = fc.showSaveDialog(buttonCiphertextSaveToFile.getScene().getWindow());

        //Set extension filter for text files
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("TXT files (*.txt)", "*.txt");
        fc.getExtensionFilters().add(extFilter);

        if (file == null) {
            labelStatus.setText("Nie podano pliku do zapisu");
            return;
        }

        saveToFile(file, fileBytes);
    }

    private void saveToFile(File file, byte[] data) {
        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(data);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
