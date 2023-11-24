package com.example.socialnetworkgui.repository;

import com.example.socialnetworkgui.DTO.FriendRequestDTO;
import com.example.socialnetworkgui.config.DatabaseConnectionConfig;
import com.example.socialnetworkgui.domain.FriendRequest;
import com.example.socialnetworkgui.domain.Tuple;
import com.example.socialnetworkgui.domain.Utilizator;
import com.example.socialnetworkgui.exceptions.RepositoryExceptions;

import java.sql.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Optional;

public class FriendRequestDBRepository implements FriendRequestRepository{

    @Override
    public Optional<FriendRequestDTO> findOne(Tuple<Long, Long> id) throws RepositoryExceptions {
        return Optional.empty();
    }

    @Override
    public Iterable<FriendRequestDTO> findAll() {
        return null;
    }

    @Override
    public Optional<FriendRequestDTO> save(FriendRequestDTO entity) throws RepositoryExceptions {
        return Optional.empty();
    }

    @Override
    public Optional<FriendRequestDTO> delete(Tuple<Long, Long> longLongTuple) throws RepositoryExceptions {
        return Optional.empty();
    }

    @Override
    public Optional<FriendRequestDTO> update(FriendRequestDTO entity) throws RepositoryExceptions {
        return Optional.empty();
    }

    @Override
    public ArrayList<FriendRequestDTO> loadAllRequests(Long id) {
        ArrayList<FriendRequestDTO> friends = new ArrayList<>();
        String findAllRequests = """
                select
                    F.fromid,
                    U.first_name,
                    U.last_name,
                    F.status
                from friendrequests F
                JOIN users u on u.id = f.fromid where F.toid = ?""";
        try(Connection connection = DriverManager.getConnection(DatabaseConnectionConfig.DB_URL,
                DatabaseConnectionConfig.DB_USER, DatabaseConnectionConfig.DB_PASS);
            PreparedStatement getRequestsStatement = connection.prepareStatement(findAllRequests);

        ){
            getRequestsStatement.setLong(1, id);
            ResultSet requestsResultSet = getRequestsStatement.executeQuery();
            while(requestsResultSet.next()){
                String firstName = requestsResultSet.getString(2);
                String lastName = requestsResultSet.getString(3);
                Long newId = requestsResultSet.getLong(1);
                String status = requestsResultSet.getString(4);
                FriendRequestDTO dto = new FriendRequestDTO(firstName, lastName, status);
                Tuple<Long, Long> idBun = new Tuple<>(newId, id);
                dto.setId(idBun);
                friends.add(dto);
            }
        }catch(SQLException e){
            System.out.println(e.getMessage());
        }
        return friends;
    }

    @Override
    public Optional<FriendRequestDTO> updateStatus(Long idFrom, Long idTo, String status) throws RepositoryExceptions{
        if (!findFriendRequest(idFrom, idTo)){
            throw new RepositoryExceptions("Nu exista aceasta cerere.");
        }
        String updateStatusQuery = """
                UPDATE friendrequests
                SET status = ?
                WHERE fromid = ? AND toid = ?;
                """;
        Optional<FriendRequestDTO> result = Optional.of(new FriendRequestDTO());
        try (Connection connection = DriverManager.getConnection(DatabaseConnectionConfig.DB_URL,
                DatabaseConnectionConfig.DB_USER, DatabaseConnectionConfig.DB_PASS);
             PreparedStatement updateStatement = connection.prepareStatement(updateStatusQuery, Statement.RETURN_GENERATED_KEYS)
        ){
            updateStatement.setString(1,status);
            updateStatement.setLong(2,idFrom);
            updateStatement.setLong(3,idTo);
            if (updateStatement.executeUpdate() > 0){
                ResultSet resultSet = updateStatement.getGeneratedKeys();
                if (resultSet.next()) {
                    result = Optional.empty();
                }
            }
        }catch(SQLException e){
            System.out.println(e.getMessage());
        }
        return result;
    }

    @Override
    public Optional<FriendRequestDTO> createFriendRequest(Tuple<Long, Long> id) throws RepositoryExceptions {
        if (findFriendRequest(id.getLeft(), id.getRight())){
            throw new RepositoryExceptions("Exista deja o cerere.");
        }
        String insertQuery = """
                insert into friendrequests(fromid, toid) values (?, ?)
                """;
        Optional<FriendRequestDTO> result = Optional.of(new FriendRequestDTO());
        try(Connection connection = DriverManager.getConnection(DatabaseConnectionConfig.DB_URL,
                DatabaseConnectionConfig.DB_USER, DatabaseConnectionConfig.DB_PASS);
            PreparedStatement insertStatement = connection.prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS);
        ){
            insertStatement.setLong(1, id.getLeft());
            insertStatement.setLong(2, id.getRight());
            if (insertStatement.executeUpdate() > 0){
                return Optional.empty();
            }
            else{
                return Optional.of(new FriendRequestDTO());
            }
        }catch (SQLException e){
            System.out.println(e.getMessage());
        }
        return result;
    }

    @Override
    public boolean findFriendRequest(Long idFrom, Long idTo) {
        String findQuery = "select * from friendrequests where fromid=? and toid=?";
        try(Connection connection = DriverManager.getConnection(DatabaseConnectionConfig.DB_URL,
                DatabaseConnectionConfig.DB_USER, DatabaseConnectionConfig.DB_PASS);
            PreparedStatement getRequestsStatement = connection.prepareStatement(findQuery);
        ){
            getRequestsStatement.setLong(1, idFrom);
            getRequestsStatement.setLong(2, idTo);
            ResultSet requestsResultSet = getRequestsStatement.executeQuery();
            return requestsResultSet.next();
        }catch(SQLException e){
            System.out.println(e.getMessage());
        }
        return false;
    }

    @Override
    public String findStatus(Long fromId, Long toId) {
        String findQuery = """
                select
                    f.status
                from users u1
                join friendrequests f on u1.id = f.fromid and u1.id = ?
                join users u2 on u2.id = f.toid and u2.id = ?""";
        try(Connection connection = DriverManager.getConnection(DatabaseConnectionConfig.DB_URL,
                DatabaseConnectionConfig.DB_USER, DatabaseConnectionConfig.DB_PASS);
            PreparedStatement getRequestsStatement = connection.prepareStatement(findQuery);
        ){
            getRequestsStatement.setLong(1, fromId);
            getRequestsStatement.setLong(2, toId);
            ResultSet requestsResultSet = getRequestsStatement.executeQuery();
            if(requestsResultSet.next()){
                return requestsResultSet.getString(1);
            }
        }catch(SQLException e){
            System.out.println(e.getMessage());
        }
        return "";
    }
}
