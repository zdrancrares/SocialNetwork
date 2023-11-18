package com.example.socialnetworkgui.controller;

import com.example.socialnetworkgui.DTO.FriendshipDTO;
import com.example.socialnetworkgui.domain.Prietenie;
import com.example.socialnetworkgui.domain.Tuple;
import com.example.socialnetworkgui.domain.Utilizator;
import com.example.socialnetworkgui.exceptions.RepositoryExceptions;
import com.example.socialnetworkgui.exceptions.ServiceExceptions;
import com.example.socialnetworkgui.service.FriendshipService;
import com.example.socialnetworkgui.service.UserService;
import com.example.socialnetworkgui.utils.events.UserChangeEvent;
import com.example.socialnetworkgui.utils.observer.Observable;
import com.example.socialnetworkgui.utils.observer.Observer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

public class EditFriendshipController implements Observer<UserChangeEvent> {
    @FXML
    TableView<FriendshipDTO> tableView;
    @FXML
    TableColumn<FriendshipDTO, Long> friendIdColumn;
    @FXML
    TableColumn<FriendshipDTO, String> firstNameColumn;
    @FXML
    TableColumn<FriendshipDTO, String> lastNameColumn;
    @FXML
    TableColumn<FriendshipDTO, LocalDateTime> friendsFromColumn;
    @FXML
    TextField firstNameTextField;
    @FXML
    TextField lastNameTextField;
    @FXML
    TextField idTextField;
    @FXML
    TextField monthTextField;
    @FXML
    Button reloadButton;

    ObservableList<FriendshipDTO> model = FXCollections.observableArrayList();

    Stage dialogStage;
    Utilizator user;

    private UserService userService;
    private FriendshipService friendshipService;

    public void setFriendshipService(UserService userService, FriendshipService friendshipService, Stage stage, Utilizator user) throws ServiceExceptions, RepositoryExceptions{
        this.userService = userService;
        this.friendshipService = friendshipService;
        this.dialogStage = stage;
        this.user = user;

        userService.addObserver(this);
        friendshipService.addObserver(this);

        initModel(false);

        firstNameTextField.setEditable(false);
        lastNameTextField.setEditable(false);
        tableView.getSelectionModel().selectedItemProperty().addListener((observable, old, newValue) -> {
            if (newValue != null){
                idTextField.setText(newValue.getId().toString());
                firstNameTextField.setText(newValue.getFirstName());
                lastNameTextField.setText(newValue.getLastName());
            }
        });
    }

    @FXML
    public void initialize(){
        friendIdColumn.setCellValueFactory(new PropertyValueFactory<FriendshipDTO, Long>("id"));
        firstNameColumn.setCellValueFactory(new PropertyValueFactory<FriendshipDTO, String>("firstName"));
        lastNameColumn.setCellValueFactory(new PropertyValueFactory<FriendshipDTO, String>("lastName"));
        friendsFromColumn.setCellValueFactory(new PropertyValueFactory<FriendshipDTO, LocalDateTime>("friendsFrom"));
        tableView.setItems(model);
    }
    public void initModel(boolean filter) {
        if (filter){
            int month = 0;
            try {
                month = Integer.parseInt(monthTextField.getText());
                if (month < 1 || month > 12){
                    throw new IllegalArgumentException();
                }
            }catch(Exception e){
                MessageAlert.showErrorMessage(null, "Campul 'month' trebuie sa fie un numar intreg intre 1 si 12.");
                return;
            }
            try{
                Iterable<FriendshipDTO> list = userService.loadUserFriendsMonth(user.getId(), month);
                List<FriendshipDTO> friends = StreamSupport.stream(list.spliterator(), false).toList();
                model.setAll(friends);
                return;
            }catch (Exception e){
                MessageAlert.showErrorMessage(null, e.getMessage());
            }
        }

        try {
            Iterable<FriendshipDTO> friends = userService.loadUserFriendsDTO(user.getId());
            List<FriendshipDTO> friendsList = StreamSupport.stream(friends.spliterator(), false).toList();
            model.setAll(friendsList);
        }catch(Exception e){
            MessageAlert.showErrorMessage(null ,e.getMessage());
        }

    }

    @Override
    public void update(UserChangeEvent userChangeEvent){
        initModel(false);
    }

    @FXML
    public void handleAddFriendship(ActionEvent event){
        Long id = null;
        try {
            id = Long.parseLong(idTextField.getText());
        }catch(Exception e){
            MessageAlert.showErrorMessage(null, "Campul ID trebuie sa fie un numar intreg.");
        }
        try {
            Optional<Utilizator> user1 = userService.getEntity(user.getId());
            Optional<Utilizator> user2 = userService.getEntity(id);
            if (user1.isPresent() && user2.isPresent()) {
                friendshipService.addEntity(user1.get(),user2.get());
                MessageAlert.showMessage(null, Alert.AlertType.CONFIRMATION, "Saving details", "Prietenia a fost formata cu succes.");
            }
        }catch(Exception e){
            MessageAlert.showErrorMessage(null, e.getMessage());
        }
    }

    @FXML
    public void handleDeleteFriendship(ActionEvent event){
        Long id = null;
        try {
            id = Long.parseLong(idTextField.getText());
        }catch(Exception e){
            MessageAlert.showErrorMessage(null, "Campul ID trebuie sa fie un numar intreg.");
        }
        try{
            Tuple<Long, Long> friendshipID = new Tuple<>(user.getId(), id);
            friendshipService.deleteEntity(friendshipID);
            MessageAlert.showMessage(null, Alert.AlertType.CONFIRMATION, "Deletion details", "Prietenia a fost stearsa cu succes.");
        }catch(Exception e){
            MessageAlert.showErrorMessage(null, e.getMessage());
        }
    }

    @FXML
    public void handleFilterFriendships(ActionEvent event){
        initModel(true);
        reloadButton.setStyle("-fx-background-color: #FF6863;");
    }

    @FXML
    public void handleReload(ActionEvent event){
        initModel(false);
        reloadButton.setStyle("-fx-background-color: #AFE1AF;");
    }
}