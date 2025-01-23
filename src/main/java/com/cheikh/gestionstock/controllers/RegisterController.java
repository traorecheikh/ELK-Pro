package com.cheikh.gestionstock.controllers;

import com.cheikh.gestionstock.HelloApplication;
import com.cheikh.gestionstock.models.User;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

import static com.cheikh.gestionstock.controllers.LoginController.us;

public class RegisterController {
    @SuppressWarnings("exports")
    @FXML
    public TextField prenom;
    @SuppressWarnings("exports")
    @FXML
    public TextField nom;
    @SuppressWarnings("exports")
    @FXML
    public TextField email;
    @SuppressWarnings("exports")
    @FXML
    public PasswordField password;

    @FXML
    public void validerClick() throws IOException {
        String prnm = this.prenom.getText();
        String nm = this.nom.getText();
        String mail = this.email.getText();
        String pass = this.password.getText();
        User user = User.builder().prenom(prnm).nom(nm).email(mail).password(pass).build();
        int result = us.Register(user);
        switch (result) {
            case 0 ->                 {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Inscription Reussi");
                    alert.show();
                    Stage stage = (Stage) this.prenom.getScene().getWindow();
                    HelloApplication.change(stage,"login");
                }
            case 2 ->                 {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Erreur");
                    alert.setContentText("l'email existe deja");
                    alert.showAndWait();
                }
            default ->                 {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Erreur");
                    alert.setContentText("Erreur durant l'inscription");
                    alert.showAndWait();
                }
        }
    }
    @FXML
    public void loginClick() throws IOException {
        Stage stage  = (Stage) this.email.getScene().getWindow();
        HelloApplication.change(stage,"login");
    }
}
