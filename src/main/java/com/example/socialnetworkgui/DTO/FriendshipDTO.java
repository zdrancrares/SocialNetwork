package com.example.socialnetworkgui.DTO;

import java.time.LocalDateTime;

public class FriendshipDTO {
    private String firstName;
    private String lastName;
    private LocalDateTime friendsFrom;
    private Long id;

    public FriendshipDTO(Long id, String firstName, String lastName, LocalDateTime friendsFrom){
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.friendsFrom = friendsFrom;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public LocalDateTime getFriendsFrom() {
        return friendsFrom;
    }

    public void setFriendsFrom(LocalDateTime friendsFrom) {
        this.friendsFrom = friendsFrom;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String toString(){
        return this.getId() + " " + this.lastName + " " + this.firstName + " " + this.friendsFrom;
    }
}
