package myProject;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class SupportAgentApp extends Application {
    private PrintWriter out;

    @Override
    public void start(Stage stage) {
        stage.setTitle("AidAccess Support Agent");

        TextArea messages = new TextArea();
        messages.setEditable(false);
        messages.setWrapText(true);
        ScrollPane scroll = new ScrollPane(messages);
        scroll.setFitToWidth(true);

        Label status = new Label("Connecting...");

        TextField input = new TextField();
        input.setPromptText("Reply to user");
        Button send = new Button("Send");
        send.setDisable(true);

        HBox inputRow = new HBox(8, input, send);
        inputRow.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(input, Priority.ALWAYS);

        VBox layout = new VBox(10, status, scroll, inputRow);
        layout.setPadding(new Insets(10));

        stage.setScene(new Scene(layout, 480, 360));
        stage.show();

        send.setOnAction(e -> {
            String text = input.getText();
            if (text == null || text.isBlank() || out == null) return;
            String payload = "Agent: " + text;
            out.println(payload);
            messages.appendText(payload + "\n");
            input.clear();
        });

        new Thread(() -> connect(messages, status, send)).start();
    }

    private void connect(TextArea messages, Label status, Button send) {
        try (Socket socket = new Socket("127.0.0.1", 6001);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter writer = new PrintWriter(socket.getOutputStream(), true)) {

            this.out = writer;
            Platform.runLater(() -> {
                status.setText("Connected as support agent");
                send.setDisable(false);
            });

            String line;
            while ((line = in.readLine()) != null) {
                String msg = line;
                Platform.runLater(() -> messages.appendText(msg + "\n"));
            }
        } catch (IOException e) {
            Platform.runLater(() -> status.setText("Disconnected from server"));
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
