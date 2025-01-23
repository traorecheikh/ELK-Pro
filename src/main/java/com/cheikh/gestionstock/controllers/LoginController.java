package com.cheikh.gestionstock.controllers;

import com.cheikh.gestionstock.HelloApplication;
import com.cheikh.gestionstock.services.UserServices;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginController {
    @SuppressWarnings("exports")
    public static final UserServices us = new UserServices();
    @SuppressWarnings("exports")
    @FXML
    public TextField email;
    @SuppressWarnings("exports")
    @FXML
    public PasswordField password;

    @FXML
    public void validerClick() throws IOException {
        String mail = this.email.getText();
        String pass = this.password.getText();
        if (us.Login(mail,pass)){
            Stage stage  = (Stage) this.email.getScene().getWindow();
            HelloApplication.change(stage,"dashboard");
            return;
        }
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setContentText("email ou mot de passe incorrect");
    }

    @FXML
    public void registerClick() throws IOException {
       Stage stage  = (Stage) this.email.getScene().getWindow();
        HelloApplication.change(stage,"register");
    }
}
