package com.example.socialnetworkgui.controller;

import com.example.socialnetworkgui.domain.Utilizator;
import com.example.socialnetworkgui.service.UserService;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;


public class EditUserController {
    @FXML
    private TextField textFieldId;
    @FXML
    private TextField textFieldFirstName;
    @FXML
    private TextField textFieldLastName;
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
        }
    }

    public void setFields(Utilizator user){

    }
}
