package com.example.domain;


import com.example.domain.validators.ValidationException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Predicate;

public class Utilizator extends Entity<Long> {
    private String firstName;
    private String lastName;
    private ArrayList<Utilizator> friends;

    public Utilizator(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
        friends = new ArrayList<>();
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

    public List<Utilizator> getFriends() {
        return friends;
    }

    public void setFriends(ArrayList<Utilizator> friends){
        this.friends = friends;
    }

    /**
     * adds a friend in a user's friendlist
     * @param friend: the friend to be added
     * @throws ValidationException
     *          if the friend is null, or they are already friends or the friends have the same id
     */
    public void addFriend(Utilizator friend) throws ValidationException{
        if (friend == null){
            throw new ValidationException("Prietenul nu poate sa fie null.");
        }
        if (friends.contains(friend)){
            throw new ValidationException("Sunt deja prieteni.");
        }
        if (Objects.equals(this.getId(), friend.getId())){
            throw new ValidationException("Cei doi prieteni au acelasi id.");
        }
        friends.add(friend);
    }

    /**
     * removes a friend from a user's friendList
     * @param id: the id of the user to be removed
     * @throws ValidationException if the 2 users are not friends
     */
    public void removeFriend(Long id) throws ValidationException{
        Predicate<Utilizator> friendIdMatch = friend -> Objects.equals(friend.getId(), id);

        boolean result = friends.removeIf(friendIdMatch);

        if (!result) {
            throw new ValidationException("Nu sunt prieteni.");
        }
    }

    @Override
    public String toString() {
        return "Utilizator{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + "'}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Utilizator)) return false;
        Utilizator that = (Utilizator) o;
        return getFirstName().equals(that.getFirstName()) &&
                getLastName().equals(that.getLastName()) &&
                getFriends().equals(that.getFriends());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getFirstName(), getLastName(), getId());
    }
}