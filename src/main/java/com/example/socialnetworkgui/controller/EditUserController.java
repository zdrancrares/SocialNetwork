package com.example.socialnetworkgui.controller;

import com.example.socialnetworkgui.domain.Utilizator;
import com.example.socialnetworkgui.exceptions.RepositoryExceptions;
import com.example.socialnetworkgui.exceptions.ServiceExceptions;
import com.example.socialnetworkgui.service.FriendshipService;
import com.example.socialnetworkgui.service.UserService;
import com.example.socialnetworkgui.utils.events.UserChangeEvent;
import com.example.socialnetworkgui.utils.observer.Observer;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;
import java.util.Optional;


public class EditUserController {
    @FXML
    private TextField textFieldFirstName;
    @FXML
    private TextField textFieldLastName;

    private UserService service;
    private FriendshipService friendshipService;
    Stage stage;
    Utilizator user;

    public void setService(UserService service, FriendshipService friendshipService, Stage stage, Utilizator utilizator){
        this.service = service;
        this.friendshipService = friendshipService;
        this.stage = stage;
        this.user = utilizator;
        setFields(utilizator);
    }

    public void setFields(Utilizator user){
        this.textFieldFirstName.setText(user.getFirstName());
        this.textFieldLastName.setText(user.getLastName());
    }
    @FXML
    public void handleCancelButton(ActionEvent event){
        handleChangeToMainPage(user);
    }
    @FXML
    public void handleClearButton(ActionEvent event){
        this.textFieldFirstName.setText("");
        this.textFieldLastName.setText("");
    }

    @FXML
    public void handleUpdateProfile(ActionEvent event) {
        String firstName = this.textFieldFirstName.getText();
        String lastName = this.textFieldLastName.getText();
        Optional<Utilizator> utilizator = Optional.empty();
        try{
            this.service.updateEntity(user.getId(), firstName, lastName);
            MessageAlert.showMessage(stage, Alert.AlertType.CONFIRMATION, "Update details", "Profilul a fost modificat cu succes.");
            stage.close();
            utilizator = service.getEntity(user.getId());
            handleChangeToMainPage(utilizator.get());
        }catch(Exception e){
            MessageAlert.showMessage(stage, Alert.AlertType.ERROR, "Update error", e.getMessage());
        }
    }

    public void handleChangeToMainPage(Utilizator user){
        this.stage.close();
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("views/mainView.fxml"));
            VBox root = loader.load();
            Stage dialogStage = new Stage();

            Image icon = new Image(Objects.requireNonNull(getClass().getResourceAsStream("assets/socialAppIcon.png")));
            dialogStage.getIcons().add(icon);
            dialogStage.setTitle("Home Page");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            Scene scene = new Scene(root);
            dialogStage.setScene(scene);

            UserController userController = loader.getController();
            userController.setUserService(service, friendshipService, user, dialogStage);

            dialogStage.show();
        }catch (IOException e){
            System.out.println(e.getMessage());
        }
    }


}
