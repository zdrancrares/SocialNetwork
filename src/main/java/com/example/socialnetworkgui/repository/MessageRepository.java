package com.example.socialnetworkgui.repository;


import com.example.socialnetworkgui.DTO.MessageDTO;
import com.example.socialnetworkgui.domain.Message;
import com.example.socialnetworkgui.exceptions.RepositoryExceptions;

import java.util.Optional;

public interface MessageRepository extends Repository<Long, Message>{
    Iterable<MessageDTO> loadUsersChats(Long iduser1, Long iduser2);
}
