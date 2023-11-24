package com.example.socialnetworkgui.repository;

import com.example.socialnetworkgui.DTO.FriendRequestDTO;
import com.example.socialnetworkgui.domain.FriendRequest;
import com.example.socialnetworkgui.domain.Tuple;
import com.example.socialnetworkgui.exceptions.RepositoryExceptions;

import java.util.ArrayList;
import java.util.Optional;

public interface FriendRequestRepository extends Repository<Tuple<Long,Long>, FriendRequestDTO>{
    ArrayList<FriendRequestDTO> loadAllRequests(Long id);

    Optional<FriendRequestDTO> updateStatus(Long idFrom, Long idTo, String status) throws RepositoryExceptions;

    Optional<FriendRequestDTO> createFriendRequest(Tuple<Long, Long> id) throws RepositoryExceptions;

    boolean findFriendRequest(Long idFrom, Long idTo);

    String findStatus(Long fromId, Long toId);
}
