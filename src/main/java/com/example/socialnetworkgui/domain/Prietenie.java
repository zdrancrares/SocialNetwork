package com.example.socialnetworkgui.domain;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;


public class Prietenie extends Entity<Tuple<Long,Long>> {

    private LocalDateTime date;
    private Utilizator user1;
    private Utilizator user2;

    private String pattern = "yyyy-MM-dd HH:mm";

    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);

    public Prietenie(Utilizator user1, Utilizator user2) {
        this.user1 = user1;
        this.user2 = user2;
        this.date = LocalDateTime.now();
    }
    /**
     *
     * @return the date when the friendship was created
     */
    public LocalDateTime getDate() {
        return date;
    }
    public void setDate(LocalDateTime date){
        this.date = date;
    }

    // Getters and Setters for Friendship's attributes
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

    @Override
    public String toString(){
        return this.getId() + " -> " + this.getUser1().getFirstName() + " " + this.getUser1().getLastName() +
                ", " + this.getUser2().getFirstName() + " " + this.getUser2().getLastName() + ", created at: " +
                date.format(formatter);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Prietenie))
            return false;
        Prietenie p = (Prietenie) o;
        return id.equals(p.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getUser1(), getUser2());
    }
}
