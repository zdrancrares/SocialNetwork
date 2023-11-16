package com.example.socialnetworkgui.utils.events;

import com.example.socialnetworkgui.domain.Utilizator;

public class UserChangeEvent implements Event{
    private ChangeEventType type;
    private Utilizator data, oldData;
    public UserChangeEvent(ChangeEventType type, Utilizator data){
        this.type = type;
        this.data = data;
    }
    public UserChangeEvent(ChangeEventType type, Utilizator data, Utilizator oldData){
        this.type = type;
        this.data = data;
        this.oldData = oldData;
    }
    public ChangeEventType getType(){
        return this.type;
    }

    public Utilizator getData() {
        return this.data;
    }
    public Utilizator getOldData(){
        return this.oldData;
    }
}
