package com.example.socialnetworkgui.repository;

import com.example.socialnetworkgui.DTO.MessageDTO;
import com.example.socialnetworkgui.config.DatabaseConnectionConfig;
import com.example.socialnetworkgui.domain.Message;
import com.example.socialnetworkgui.domain.Tuple;
import com.example.socialnetworkgui.domain.Utilizator;
import com.example.socialnetworkgui.exceptions.RepositoryExceptions;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;

public class MessageDBRepository implements MessageRepository {
    @Override
    public Optional<Message> findOne(Long aLong) throws RepositoryExceptions {
        return Optional.empty();
    }
    @Override
    public Iterable<Message> findAll() {
        return null;
    }

    @Override
    public Optional<Message> save(Message entity) throws RepositoryExceptions {
        if (entity == null){
            throw new RepositoryExceptions("Mesajul nu poate sa fie null");
        }

        String insertSqlStatement = "insert into messages(content, fromid, date) values(?,?,?)";
        Optional<Message> result = Optional.empty();
        try(Connection connection = DriverManager.getConnection(DatabaseConnectionConfig.DB_URL,
                DatabaseConnectionConfig.DB_USER, DatabaseConnectionConfig.DB_PASS);
            PreparedStatement insertMessageStatement = connection.prepareStatement(insertSqlStatement, Statement.RETURN_GENERATED_KEYS);
        ){
            insertMessageStatement.setString(1, entity.getContent());
            insertMessageStatement.setLong(2, entity.getFrom().getId());
            insertMessageStatement.setTimestamp(3, Timestamp.valueOf(entity.getDate()));
            if (insertMessageStatement.executeUpdate() > 0){
                ResultSet resultSet = insertMessageStatement.getGeneratedKeys();
                resultSet.next();
                Long id = resultSet.getLong("messageid");
                entity.setId(id);

                //am creat mesajul, acum trebuie sa cream destination

                for (int i = 0; i < entity.getTo().size(); i++){
                    String insertSqlStatement2 = "insert into destinations(messageid, toid) values(?,?)";
                    try(
                    PreparedStatement insertStatement2 = connection.prepareStatement(insertSqlStatement2, Statement.RETURN_GENERATED_KEYS);
                    ){
                        insertStatement2.setLong(1, entity.getId());
                        insertStatement2.setLong(2, entity.getTo().get(i).getId());
                        if (insertStatement2.executeUpdate() <= 0) {
                            return Optional.of(entity);
                        }
                    }catch (SQLException e){
                        System.out.println(e.getMessage());
                    }
                }
                result = Optional.empty();
            }
            else{
                result = Optional.of(entity);
            }
        }catch (SQLException e){
            System.out.println(e.getMessage());
        }
        return result;
    }

    @Override
    public Optional<Message> delete(Long aLong) throws RepositoryExceptions {
        return Optional.empty();
    }

    @Override
    public Optional<Message> update(Message entity) throws RepositoryExceptions {
        return Optional.empty();
    }

    @Override
    public Iterable<MessageDTO> loadUsersChats(Long iduser1, Long iduser2) {
        ArrayList<MessageDTO> chats = new ArrayList<>();
        String selectMessagesStatement = "SELECT\n" +
                "    M.messageid,\n" +
                "    M.content,\n" +
                "    M.date,\n" +
                "    U1.first_name,\n" +
                "    U1.last_name,\n" +
                "    U2.first_name,\n" +
                "    U2.last_name\n" +
                "FROM\n" +
                "    users U1\n" +
                "    INNER JOIN Messages M ON (M.fromid = ? OR M.fromid=?) AND M.fromid = U1.id\n" +
                "    INNER JOIN Destinations D ON M.messageid = D.messageid\n" +
                "    INNER JOIN users U2 ON (D.toid = ? Or D.toid=?) AND D.toid = U2.id\n" +
                "ORDER BY M.date;\n";
        try(Connection connection = DriverManager.getConnection(DatabaseConnectionConfig.DB_URL,
                DatabaseConnectionConfig.DB_USER, DatabaseConnectionConfig.DB_PASS);
            PreparedStatement getChatsStatement = connection.prepareStatement(selectMessagesStatement);
        ){
            getChatsStatement.setLong(1, iduser1);
            getChatsStatement.setLong(2, iduser2);
            getChatsStatement.setLong(3, iduser1);
            getChatsStatement.setLong(4, iduser2);
            ResultSet chatsResultSet = getChatsStatement.executeQuery();
            while(chatsResultSet.next()){
                Long id = chatsResultSet.getLong(1);

                String content = chatsResultSet.getString(2);
                LocalDateTime date = chatsResultSet.getTimestamp(3).toLocalDateTime();

                String firstName1 = chatsResultSet.getString(4);
                String lastName1 = chatsResultSet.getString(5);

                String firstName2 = chatsResultSet.getString(6);
                String lastName2 = chatsResultSet.getString(7);

                MessageDTO chat = new MessageDTO(content, date, firstName1, lastName1, firstName2, lastName2);
                chat.setId(id);
                chats.add(chat);
            }
        }catch(SQLException e){
            System.out.println(e.getMessage());
        }
        return chats;
    }

    @Override
    public void reply(Message message, Long userMessageId) {
        String insertSqlStatement = "insert into messages(content, fromid, date) values(?,?,?)";
        Optional<Message> result = Optional.empty();
        try(Connection connection = DriverManager.getConnection(DatabaseConnectionConfig.DB_URL,
                DatabaseConnectionConfig.DB_USER, DatabaseConnectionConfig.DB_PASS);
            PreparedStatement insertMessageStatement = connection.prepareStatement(insertSqlStatement, Statement.RETURN_GENERATED_KEYS);
        ){
            insertMessageStatement.setString(1, message.getContent());
            insertMessageStatement.setLong(2, message.getFrom().getId());
            insertMessageStatement.setTimestamp(3, Timestamp.valueOf(message.getDate()));
            if (insertMessageStatement.executeUpdate() > 0){
                ResultSet resultSet = insertMessageStatement.getGeneratedKeys();
                resultSet.next();
                Long id = resultSet.getLong("messageid");
                message.setId(id);

                //am creat mesajul, acum trebuie sa cream destination

                String insertSqlStatement2 = "insert into destinations(messageid, toid) values(?,?)";
                try(PreparedStatement insertStatement2 = connection.prepareStatement(insertSqlStatement2, Statement.RETURN_GENERATED_KEYS);
                ){
                    insertStatement2.setLong(1, message.getId());
                    insertStatement2.setLong(2, message.getTo().get(0).getId());
                    insertStatement2.executeUpdate();

                    //acum cream reply

                    String insertSqlStatement3 = "insert into replies(messageid1, messageid2) values (?, ?);";
                    try(PreparedStatement insertStatement3 = connection.prepareStatement(insertSqlStatement3, Statement.RETURN_GENERATED_KEYS);
                    ){
                        insertStatement3.setLong(1, userMessageId);
                        insertStatement3.setLong(2, message.getId());
                        insertStatement3.executeUpdate();
                    }catch(SQLException e){
                        System.out.println(e.getMessage());
                    }
                }catch (SQLException e){
                    System.out.println(e.getMessage());
                }
            }
        }catch (SQLException e){
            System.out.println(e.getMessage());
        }
    }
}
