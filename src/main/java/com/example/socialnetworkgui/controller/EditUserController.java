package com.example.socialnetworkgui.controller;

import com.example.socialnetworkgui.domain.Utilizator;
import com.example.socialnetworkgui.exceptions.RepositoryExceptions;
import com.example.socialnetworkgui.exceptions.ServiceExceptions;
import com.example.socialnetworkgui.service.UserService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;


public class EditUserController {
    @FXML
    private TextField textFieldId;
    @FXML
    private TextField textFieldFirstName;
    @FXML
    private TextField textFieldLastName;
    @FXML
    private TextField textFieldEmail;
    @FXML
    private TextField textFieldPassword;
    @FXML
    private Label passwordLabel;
    private UserService service;
    Stage dialogStage;
    Utilizator user;

    public void setService(UserService service, Stage stage, Utilizator utilizator){
        this.service = service;
        this.dialogStage = stage;
        this.user = utilizator;
        if (utilizator != null){
            setFields(utilizator);

            textFieldId.setEditable(false);
            textFieldEmail.setEditable(false);

            textFieldPassword.setVisible(false);
            passwordLabel.setVisible(false);
        }
        else {
            this.textFieldId.setAlignment(Pos.CENTER);
            this.textFieldId.setText("X");
            //this.textFieldId.setText(utilizator.getId()); -toString()
            this.textFieldFirstName.setText("");
            this.textFieldLastName.setText("");
            textFieldId.setEditable(false);

        }
    }

    public void setFields(Utilizator user){
        this.textFieldId.setText(user.getId().toString());
        this.textFieldFirstName.setText(user.getFirstName());
        this.textFieldLastName.setText(user.getLastName());
        this.textFieldEmail.setText(user.getEmail());
    }
    @FXML
    public void handleCancelButton(ActionEvent event){
        this.dialogStage.close();
    }
    @FXML
    public void handleClearButton(ActionEvent event){
        this.textFieldFirstName.setText("");
        this.textFieldLastName.setText("");
    }
    @FXML
    public void handleSaveButton(ActionEvent event) {
        String firstName = this.textFieldFirstName.getText();
        String lastName = this.textFieldLastName.getText();
        String email = this.textFieldEmail.getText();
        String password = this.textFieldPassword.getText();

        if (user == null){
            try {
                this.service.addEntity(firstName, lastName, email, password);
                MessageAlert.showMessage(dialogStage, Alert.AlertType.CONFIRMATION, "Save details", "Utilizatorul a fost adaugat cu succes.");
                dialogStage.close();
            }catch (Exception e){
                MessageAlert.showMessage(dialogStage, Alert.AlertType.ERROR, "Save error", e.getMessage());
            }
        }
        else{
            try{
                this.service.updateEntity(user.getId(), firstName, lastName);
                MessageAlert.showMessage(dialogStage, Alert.AlertType.CONFIRMATION, "Update details", "Utilizatorul a fost modificat cu succes.");
                dialogStage.close();
            }catch(Exception e){
                MessageAlert.showMessage(dialogStage, Alert.AlertType.ERROR, "Update error", e.getMessage());
            }
        }
    }
}
