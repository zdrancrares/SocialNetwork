package com.example.socialnetworkgui.domain;

import java.io.Serializable;
import java.util.Objects;

public class Entity<ID> implements Serializable {

   // private static final long serialVersionUID = 7331115341259248461L;
    protected ID id;

    // Getters and Setters for entity's attributes
    public ID getId() {
        return id;
    }
    public void setId(ID id) {
        this.id = id;
    }
    /**
     * Tests the equality between two entities
     * @param o the object we compare with
     * @return true - if the entities are equal
     *          otherwise it returns false
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Entity)) return false;
        Entity<?> entity = (Entity<?>) o;
        return getId().equals(entity.getId());
    }

    /**
     * generates the hashCode for an entity based on ID
     * @return the hashCode of the entity(int)
     */
    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }

    /**
     * toString Method for a default entity
     * @return the string with the entity's id
     */
    @Override
    public String toString() {
        return "Entity{" +
                "id=" + id +
                '}';
    }
}