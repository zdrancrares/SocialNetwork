package com.example.socialnetworkgui.DTO;

import java.time.LocalDateTime;

public class MessageDTO {
    private String content;
    private LocalDateTime date;
    private String firstName1;
    private String lastName1;
    private String firstName2;
    private String lastName2;
    public MessageDTO(String content, LocalDateTime date, String firstName1, String lastName1, String firstName2, String lastName2){
        this.content = content;
        this.date = date;
        this.firstName1 = firstName1;
        this.lastName1 = lastName1;
        this.firstName2 = firstName2;
        this.lastName2 = lastName2;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public String getFirstName1() {
        return firstName1;
    }

    public void setFirstName1(String firstName1) {
        this.firstName1 = firstName1;
    }

    public String getLastName1() {
        return lastName1;
    }

    public void setLastName1(String lastName1) {
        this.lastName1 = lastName1;
    }

    public String getFirstName2() {
        return firstName2;
    }

    public void setFirstName2(String firstName2) {
        this.firstName2 = firstName2;
    }

    public String getLastName2() {
        return lastName2;
    }

    public void setLastName2(String lastName2) {
        this.lastName2 = lastName2;
    }
    @Override
    public String toString(){
        return this.getDate() + " | " + this.getContent() + " | " + this.getFirstName1() + " " + this.getLastName1() + " | " + this.getFirstName2() + " " + this.getLastName2();
    }
}
