package com.example.socialnetworkgui.utils.observer;

import com.example.socialnetworkgui.DTO.FriendshipDTO;
import com.example.socialnetworkgui.domain.Entity;
import com.example.socialnetworkgui.utils.events.Event;

public interface Observer<E extends Event> {
    void update(E e);
}
