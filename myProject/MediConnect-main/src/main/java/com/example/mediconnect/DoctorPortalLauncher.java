package com.example.mediconnect;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class DoctorPortalLauncher {
    private final Window owner;
    private final Path resourceRoot = Paths.get("MediConnect-main/src/main/resources");
    private Stage doctorStage;

    public DoctorPortalLauncher(Window owner) {
        this.owner = owner;
    }

    public void open() {
        if (doctorStage != null) {
            if (doctorStage.isShowing()) {
                doctorStage.toFront();
                doctorStage.requestFocus();
                return;
            }
            doctorStage = null;
        }

        try {
            URL fxmlUrl = resolveResource("com/example/mediconnect/landing.fxml");
            if (fxmlUrl == null) {
                throw new IOException("Doctor portal UI not found. Expected at " + resourceRoot.toAbsolutePath());
            }

            DatabaseManager.initializeDatabase();

            FXMLLoader loader = new FXMLLoader(fxmlUrl);
            Scene scene = new Scene(loader.load(), 900, 600);

            URL stylesheet = resolveResource("com/example/mediconnect/style.css");
            if (stylesheet != null) {
                scene.getStylesheets().add(stylesheet.toExternalForm());
            }

            doctorStage = new Stage();
            if (owner != null) {
                doctorStage.initOwner(owner);
            }
            doctorStage.setTitle("AidAccess - Doctor Portal");
            URL icon = resolveResource("icon.png");
            if (icon != null) {
                doctorStage.getIcons().add(new Image(icon.toExternalForm()));
            }
            doctorStage.setScene(scene);
            doctorStage.setOnCloseRequest(e -> DatabaseManager.close());
            doctorStage.setOnHidden(e -> doctorStage = null);
            doctorStage.show();
        } catch (Exception ex) {
            showError(ex);
        }
    }

    private URL resolveResource(String relativePath) throws MalformedURLException {
        URL classpathResource = DoctorPortalLauncher.class.getResource("/" + relativePath);
        if (classpathResource != null) {
            return classpathResource;
        }

        Path fileResource = resourceRoot.resolve(relativePath);
        if (Files.exists(fileResource)) {
            return fileResource.toUri().toURL();
        }
        return null;
    }

    private void showError(Exception ex) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Doctor Portal Error");
        alert.setHeaderText("Unable to open Doctor portal");
        alert.setContentText(ex.getMessage() != null ? ex.getMessage() : ex.toString());
        alert.showAndWait();
        ex.printStackTrace();
    }
}
