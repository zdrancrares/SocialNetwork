package com.example.socialnetworkgui.controller;

import com.example.socialnetworkgui.DTO.FriendRequestDTO;
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
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class EditFriendshipController implements Observer<UserChangeEvent> {
    @FXML
    TableView<FriendshipDTO> tableView;
    @FXML
    TableView<FriendRequestDTO> tableViewFriendRequests;
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
    TextField messageTextField;
    @FXML
    Button reloadButton;
    @FXML
    TableColumn<FriendRequestDTO, Long> friendIdColumnFriendRequest;
    @FXML
    TableColumn<FriendRequestDTO, String> friendFirstNameColumnFriendRequest;
    @FXML
    TableColumn<FriendRequestDTO, String> friendLastNameColumnFriendRequest;
    @FXML
    TableColumn<FriendRequestDTO, String> friendStatusColumnFriendRequest;

    ObservableList<FriendshipDTO> model = FXCollections.observableArrayList();

    ObservableList<FriendRequestDTO> modelFriendRequests = FXCollections.observableArrayList();

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
        initModelFriendRequest();

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

    public void initModelFriendRequest(){
        Iterable<FriendRequestDTO> friendRequests = userService.getAllFriendRequests(user.getId());
        //System.out.println(friendRequests);
        List<FriendRequestDTO> friendRequestsList = StreamSupport.stream(friendRequests.spliterator(), false).toList();
        modelFriendRequests.setAll(friendRequestsList);
    }

    @FXML
    public void initialize(){
        friendIdColumn.setCellValueFactory(new PropertyValueFactory<FriendshipDTO, Long>("id"));
        firstNameColumn.setCellValueFactory(new PropertyValueFactory<FriendshipDTO, String>("firstName"));
        lastNameColumn.setCellValueFactory(new PropertyValueFactory<FriendshipDTO, String>("lastName"));
        friendsFromColumn.setCellValueFactory(new PropertyValueFactory<FriendshipDTO, LocalDateTime>("friendsFrom"));
        tableView.setItems(model);


        friendIdColumnFriendRequest.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getId().getLeft()));
        //friendIdColumnFriendRequest.setCellValueFactory(new PropertyValueFactory<FriendRequestDTO, Long>("id"));
        friendFirstNameColumnFriendRequest.setCellValueFactory(new PropertyValueFactory<FriendRequestDTO, String>("firstName"));
        friendLastNameColumnFriendRequest.setCellValueFactory(new PropertyValueFactory<FriendRequestDTO, String>("lastName"));
        friendStatusColumnFriendRequest.setCellValueFactory(new PropertyValueFactory<FriendRequestDTO, String>("status"));
        tableViewFriendRequests.setItems(modelFriendRequests);
    }
    public void initModel(boolean filter) {
        tableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
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
        initModelFriendRequest();
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

    @FXML
    public void handleChatsButton(ActionEvent event) throws RepositoryExceptions{
        FriendshipDTO friend = tableView.getSelectionModel().getSelectedItem();

        if (friend == null){
            MessageAlert.showErrorMessage(null, "Nu ati selectat niciun prieten.");
            return;
        }

        Optional<Utilizator> utilizator2 = userService.getEntity(friend.getId());
        if (utilizator2.isPresent()) {
            showChatWindow(user, utilizator2.get());
        }
        else{
            MessageAlert.showErrorMessage(null, "Nu exista utilizator cu acest id.");
        }
    }

    @FXML
    public void handleSendMessage(ActionEvent actionEvent) throws RepositoryExceptions, ServiceExceptions{
        ObservableList<FriendshipDTO> friends = tableView.getSelectionModel().getSelectedItems();
        if (!friends.isEmpty()){
            if (Objects.equals(messageTextField.getText(), "")){
                MessageAlert.showErrorMessage(null, "Nu ati introdus niciun mesaj.");
                return;
            }
            ArrayList<Utilizator> to = new ArrayList<>();
            for (FriendshipDTO friend: friends){
                Utilizator user = new Utilizator(friend.getFirstName(), friend.getLastName());
                user.setId(friend.getId());
                to.add(user);
            }
            userService.addMessage(messageTextField.getText(), user.getId(), to);
            MessageAlert.showMessage(null, Alert.AlertType.CONFIRMATION, "Send message details", "Mesajul a fost trimis cu succes.");
            messageTextField.setText("");
        }
        else{
            MessageAlert.showErrorMessage(null, "Nu ati selectat niciun utilizator la care sa trimiteti.");
        }
    }

    @FXML
    public void handleSendFriendRequest(ActionEvent actionEvent){
        Long fromId = user.getId();
        Long toId = Long.parseLong(idTextField.getText());

        if (friendshipService.findFriendRequest(fromId, toId) || friendshipService.findFriendRequest(toId, fromId)){
            MessageAlert.showErrorMessage(null, "Exista deja o cerere de prietenie intre acesti utilizatori");
            return;
        }

        try {
            userService.sendFriendRequest(fromId, toId);
            MessageAlert.showMessage(null, Alert.AlertType.CONFIRMATION, "Send friend request details", "Cererea a fost trimisa cu succes.");
        }catch(Exception e){
            MessageAlert.showErrorMessage(null, e.getMessage());
        }
    }

    @FXML
    public void handleAcceptFriendRequest(ActionEvent actionEvent){
        FriendRequestDTO friendToAccept = tableViewFriendRequests.getSelectionModel().getSelectedItem();
        if (friendToAccept == null){
            MessageAlert.showErrorMessage(null, "Nu ati selectat niciun utilizator");
            return;
        }
        try{
            Utilizator user2 = new Utilizator(friendToAccept.getFirstName(), friendToAccept.getLastName());
            user2.setId(friendToAccept.getId().getLeft());
            friendshipService.addEntity(user, user2);
            MessageAlert.showMessage(null, Alert.AlertType.CONFIRMATION, "Friend request details", "Cererea a fost acceptata.");
        }catch(Exception e){
            MessageAlert.showErrorMessage(null, e.getMessage());
        }
    }

    @FXML
    public void handleRejectFriendRequest(ActionEvent actionEvent){
        FriendRequestDTO friendToReject = tableViewFriendRequests.getSelectionModel().getSelectedItem();
        if (friendToReject == null){
            MessageAlert.showErrorMessage(null, "Nu ati selectat niciun utilizator");
            return;
        }
        try{
            Utilizator user2 = new Utilizator(friendToReject.getFirstName(), friendToReject.getLastName());
            user2.setId(friendToReject.getId().getLeft());
            userService.rejectFriendRequest(user, user2);
            MessageAlert.showMessage(null, Alert.AlertType.CONFIRMATION, "Friend request details", "Cererea a fost respinsa.");
        }catch(Exception e){
            MessageAlert.showErrorMessage(null, e.getMessage());
        }
    }

    public void showChatWindow(Utilizator utilizator1, Utilizator utilizator2){
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("views/chats.fxml"));
            AnchorPane root = loader.load();
            Stage dialogStage = new Stage();

            Image icon = new Image(Objects.requireNonNull(getClass().getResourceAsStream("assets/socialAppIcon.png")));
            dialogStage.getIcons().add(icon);
            dialogStage.setTitle("Chats");
            dialogStage.initModality(Modality.WINDOW_MODAL);

            Scene scene = new Scene(root);
            dialogStage.setScene(scene);

            ChatController chatController = loader.getController();
            chatController.setChatService(userService,friendshipService, dialogStage, utilizator1, utilizator2);

            dialogStage.show();
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}
