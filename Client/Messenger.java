package ChillChat.Client;

import ChillChat.Client.Console.ConsoleClient;
import ChillChat.Client.Utilites.Constants;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.effect.Glow;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.util.Duration;

import static ChillChat.Client.Utilites.Constants.TEXT_DISAPPEAR_TIME;

class Messenger {

    private Scene clientScene;
    private ConsoleClient consoleClient;
    private ClientWindow clientWindow;

    private VBox messengerBox;

    private CustomConsole console;
    private TextField inputField;
    private GaussianBlur textBlur;


    Messenger(ConsoleClient consoleClient, StackPane centralPane, Scene clientScene, Integer color, Client client, ClientWindow clientWindow) {

        this.clientWindow = clientWindow;
        this.clientScene = clientScene;

        clientScene.setOnKeyPressed(event -> {
            if (event.getCode().equals(KeyCode.ENTER)){
                flushTextFromField();
            }

            if (event.getCode().equals(KeyCode.ESCAPE)){
                clientWindow.goToLoginScreen(false);
            }
        });

        this.consoleClient = consoleClient;
        messengerBox = new VBox();

        if (Constants.DEBUG)
            messengerBox.setStyle("-fx-border-color: yellow");

        messengerBox.prefHeightProperty().bind(centralPane.heightProperty());
        messengerBox.prefWidthProperty().bind(centralPane.widthProperty());
        messengerBox.maxHeightProperty().bind(centralPane.heightProperty());
        messengerBox.maxWidthProperty().bind(centralPane.widthProperty());

        StackPane consolePane = new StackPane();

        if (Constants.DEBUG)
            consolePane.setStyle("-fx-border-color: orange");


        Glow textGlow = new Glow();
        textGlow.setLevel(1);
        textBlur = new GaussianBlur();
        textBlur.setRadius(0.0);

        inputField = new TextField();
        inputField.setFont(new Font("Courier New", 19));
        inputField.setEffect(textBlur);
        textBlur.setInput(textGlow);

        changeInterfaceColor(color);

        inputField.prefWidthProperty().bind(centralPane.widthProperty());

        consolePane.prefHeightProperty().bind(messengerBox.heightProperty().subtract(inputField.heightProperty()));
        consolePane.maxHeightProperty().bind(messengerBox.heightProperty().subtract(inputField.heightProperty()));
        consolePane.prefWidthProperty().bind(messengerBox.widthProperty());
        consolePane.maxWidthProperty().bind(messengerBox.widthProperty());

        console = new CustomConsole(consolePane, client);

        consolePane.getChildren().add(console.getBox());

        messengerBox.getChildren().addAll(consolePane, inputField);

    }

    private void changeInterfaceColor(Integer color) {

        String webColorString = "";

        switch (color){
            case 1:
                webColorString = "#f44336";
                break;
            case 2:
                webColorString = "#3f51b5";
                break;
            case 3:
                webColorString = "#29b6f6";
                break;
            case 4:
                webColorString = "#ff5722";
                break;
            case 5:
                webColorString = "#4caf50";
                break;
            case 6:
                webColorString = "#8bc34a";
                break;
            case 7:
                webColorString = "#ffeb3b";
                break;
            case 8:
                webColorString = "#ec407a";
                break;
            default:
                webColorString = "#546e7a";
                break;
        }

        inputField.setStyle("-fx-background-color: transparent;-fx-text-inner-color: " + webColorString + ";");

    }

    private void flushTextFromField() {

        String text = inputField.getText();

        if (text.isEmpty())
            return;
        if (text.length() > 255)
            sendMessage(text.substring(0, 255));
        else
            sendMessage(text);

        animatedEreaseText();

    }

    private void animatedEreaseText() {

        Timeline textDissapear = new Timeline();
        textDissapear.getKeyFrames().add(new KeyFrame(Duration.seconds(0.0), new KeyValue(textBlur.radiusProperty(), 0.0)));
        textDissapear.getKeyFrames().add(new KeyFrame(Duration.seconds(TEXT_DISAPPEAR_TIME), new KeyValue(textBlur.radiusProperty(), 20.0)));
        textDissapear.play();
        textDissapear.setOnFinished(e -> {
            inputField.setText("");
            textBlur.setRadius(0);
        });

    }

    Node getContainer() {
        return messengerBox;
    }

    void displayMessage(String name, String text, Integer color) {
        console.textAppend(name, text, color);
    }

    private void sendMessage(String text) {

        consoleClient.sendMessage(text);
    }

    void displayServerMessage(String text) {
        console.serverMessageAppend(text);
    }

}
