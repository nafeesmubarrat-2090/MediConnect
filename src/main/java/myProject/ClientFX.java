package myProject;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientFX {
    private static Stage existing;

    public static void openHelpChat() {
        Platform.runLater(() -> {
            if (existing != null) {
                existing.toFront();
                return;
            }
            Stage stage = new Stage();
            existing = stage;
            stage.setTitle("AidAccess Help Chat");

            TextArea messages = new TextArea();
            messages.setEditable(false);
            messages.setWrapText(true);
            ScrollPane scroll = new ScrollPane(messages);
            scroll.setFitToWidth(true);

            Label status = new Label("Connecting to support...");

            TextField input = new TextField();
            input.setPromptText("Type a message and press Send");
            Button send = new Button("Send");
            send.setDisable(true);

            HBox inputRow = new HBox(8, input, send);
            inputRow.setAlignment(Pos.CENTER_LEFT);
            HBox.setHgrow(input, javafx.scene.layout.Priority.ALWAYS);

            VBox layout = new VBox(10, status, scroll, inputRow);
            layout.setPadding(new Insets(10));

            BorderPane root = new BorderPane(layout);
            Scene scene = new Scene(root, 420, 320);
            stage.setScene(scene);
            stage.show();

            new Thread(() -> connect(messages, input, send, status)).start();

            stage.setOnCloseRequest(e -> existing = null);
        });
    }

    private static void connect(TextArea messages, TextField input, Button send, Label status) {
        try (Socket socket = new Socket("127.0.0.1", 6001);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

            Platform.runLater(() -> {
                status.setText("Connected to support");
                send.setDisable(false);
                messages.appendText("Connected to help desk.\n");
            });

            send.setOnAction(e -> {
                String text = input.getText();
                if (text == null || text.isBlank()) return;
                String payload = "User: " + text;
                out.println(payload);
                Platform.runLater(() -> messages.appendText(payload + "\n"));
                input.clear();
                messages.setScrollTop(Double.MAX_VALUE);
            });

            String line;
            while ((line = in.readLine()) != null) {
                String msg = line;
                Platform.runLater(() -> {
                    messages.appendText(msg + "\n");
                    messages.setScrollTop(Double.MAX_VALUE);
                });
            }
        } catch (IOException e) {
            Platform.runLater(() -> {
                status.setText("Disconnected from support");
                send.setDisable(true);
                messages.appendText("Disconnected from help desk.\n");
            });
        }
    }
}
