package com.example.socialnetworkgui.domain;

public class FriendRequest extends Entity<Tuple<Long, Long>>{
    private Utilizator user1;
    private Utilizator user2;
    private String status;
    public FriendRequest(Utilizator user1, Utilizator user2){
        this.user1 = user1;
        this.user2 = user2;
        this.status = "pending";
    }

    public Utilizator getUser1() {
        return user1;
    }

    public void setUser1(Utilizator user1) {
        this.user1 = user1;
    }

    public Utilizator getUser2() {
        return user2;
    }

    public void setUser2(Utilizator user2) {
        this.user2 = user2;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
