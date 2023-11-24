package com.example.socialnetworkgui.DTO;

import com.example.socialnetworkgui.domain.Entity;
import com.example.socialnetworkgui.domain.Tuple;

public class FriendRequestDTO extends Entity<Tuple<Long, Long>> {
    private String firstName;
    private String lastName;
    private String status;
    public FriendRequestDTO(){};
    public FriendRequestDTO(String firstName, String lastName, String status) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.status = status;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}
