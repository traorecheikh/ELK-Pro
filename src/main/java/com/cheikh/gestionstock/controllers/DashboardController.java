package com.cheikh.gestionstock.controllers;

import com.cheikh.gestionstock.HelloApplication;
import com.cheikh.gestionstock.services.UserServices;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.application.Platform;

import java.io.IOException;

import static com.cheikh.gestionstock.services.EntityManagerUtils.getEM;
import static com.cheikh.gestionstock.services.UserServices.checkSession;

public class DashboardController {
    private final UserServices us = new UserServices();

    @FXML
    private Label idText;

    @FXML
    @SuppressWarnings("CallToPrintStackTrace")
    public void initialize() throws IOException {
        Platform.runLater(() -> {
            try {
                Stage stage = (Stage) idText.getScene().getWindow();

                if (checkSession() == null) {
                    getEM();
                    HelloApplication.change(stage, "login");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    @FXML
    public void logoutClick() throws IOException {
        us.Logout();
        Stage stage = (Stage) idText.getScene().getWindow();
        HelloApplication.change(stage,"login");
    }

    @FXML
    public void utilisateurClick() throws IOException {
        Stage stage = (Stage) idText.getScene().getWindow();
        HelloApplication.change(stage,"gestion-utilisateur");
    }
}
