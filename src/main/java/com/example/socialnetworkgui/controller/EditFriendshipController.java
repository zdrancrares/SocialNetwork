package com.example.socialnetworkgui.controller;

import com.example.socialnetworkgui.DTO.FriendshipDTO;
import com.example.socialnetworkgui.domain.Prietenie;
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
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
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

    ObservableList<FriendshipDTO> model = FXCollections.observableArrayList();

    Stage dialogStage;
    Utilizator user;

    private UserService userService;
    private FriendshipService friendshipService;

    public void setFriendshipService(UserService userService, FriendshipService friendshipService, Stage stage, Utilizator user){
        this.userService = userService;
        this.friendshipService = friendshipService;
        this.dialogStage = stage;
        this.user = user;
        userService.addObserver(this);
        friendshipService.addObserver(this);
        initModel();
    }

    @FXML
    public void initialize(){
        friendIdColumn.setCellValueFactory(new PropertyValueFactory<FriendshipDTO, Long>("id"));
        firstNameColumn.setCellValueFactory(new PropertyValueFactory<FriendshipDTO, String>("firstName"));
        lastNameColumn.setCellValueFactory(new PropertyValueFactory<FriendshipDTO, String>("lastName"));
        friendsFromColumn.setCellValueFactory(new PropertyValueFactory<FriendshipDTO, LocalDateTime>("friendsFrom"));
        tableView.setItems(model);
    }
    public void initModel() {
        try {
            Iterable<FriendshipDTO> friends = userService.loadUserFriendsDTO(user.getId());
            List<FriendshipDTO> friendsList = StreamSupport.stream(friends.spliterator(), false).toList();
            model.setAll(friendsList);
        }catch(Exception e){
            System.out.println(e.getMessage());
        }

    }

    @Override
    public void update(UserChangeEvent userChangeEvent){
        initModel();
    }
}
