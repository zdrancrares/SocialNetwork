package com.example.socialnetworkgui.domain;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class Message extends Entity<Long>{
    private Utilizator from;
    private ArrayList<Utilizator> to;
    private String content;
    private LocalDateTime date;
    private Message reply;
    public Message(Utilizator from, ArrayList<Utilizator> to, String content, LocalDateTime date){
        this.from = from;
        this.to = to;
        this.content = content;
        this.date = date;
        this.reply = null;
    }

    public Utilizator getFrom() {
        return from;
    }

    public void setFrom(Utilizator from) {
        this.from = from;
    }

    public ArrayList<Utilizator> getTo() {
        return to;
    }

    public void setTo(ArrayList<Utilizator> to) {
        this.to = to;
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

    public Message getReply() {
        return reply;
    }

    public void setReply(Message reply) {
        this.reply = reply;
    }
}
