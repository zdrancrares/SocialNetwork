package com.example.repository;

import com.example.config.DatabaseConnectionConfig;
import com.example.domain.Prietenie;
import com.example.domain.Tuple;
import com.example.domain.Utilizator;
import com.example.domain.validators.Validator;
import com.example.exceptions.RepositoryExceptions;

import javax.xml.crypto.Data;
import java.sql.*;
import java.util.*;
import java.util.function.BinaryOperator;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class UserDBRepository implements Repository<Long, Utilizator> {
    private final Validator<Utilizator> validator;
    public UserDBRepository(Validator<Utilizator> validator){
        this.validator = validator;
    }
    @Override
    public Optional<Utilizator> findOne(Long aLong) throws RepositoryExceptions {
        Predicate<Long> isNull = Objects::isNull;
        if (isNull.test(aLong)) {
            throw new RepositoryExceptions("ID-ul nu poate fi null.");
        }
        String findUser = """
                SELECT U.*, UF.*
                FROM users U
                         LEFT JOIN friendships F ON F.userid1 = U.id OR F.userid2 = U.id
                         LEFT JOIN users UF ON (F.userid1 = UF.id AND F.userid2 = U.id) OR (F.userid2 = UF.id AND F.userid1 = U.id)
                WHERE U.id = ?;""";
        try(Connection connection = DriverManager.getConnection(DatabaseConnectionConfig.DB_URL,
                DatabaseConnectionConfig.DB_USER, DatabaseConnectionConfig.DB_PASS);
            PreparedStatement getUserStatement = connection.prepareStatement(findUser);
        ){
            getUserStatement.setLong(1, aLong);
            ResultSet resultSet = getUserStatement.executeQuery();
            if (resultSet.next()) {
                Utilizator user = new Utilizator(null, null);
                user.setId(resultSet.getLong(1));
                user.setFirstName(resultSet.getString(2));
                user.setLastName(resultSet.getString(3));

                ArrayList<Utilizator> friends = new ArrayList<>();

                if (resultSet.getString(5) != null) {
                    Utilizator friend = new Utilizator(null, null);
                    friend.setId(resultSet.getLong(4));
                    friend.setFirstName(resultSet.getString(5));
                    friend.setLastName(resultSet.getString(6));
                    friends.add(friend);

                    while (resultSet.next()) {
                        friend = new Utilizator(null, null);
                        friend.setId(resultSet.getLong(4));
                        friend.setFirstName(resultSet.getString(5));
                        friend.setLastName(resultSet.getString(6));
                        friends.add(friend);
                    }
                }
                user.setFriends(friends);
                return Optional.of(user);
            }
        }catch(SQLException e){
            System.out.println(e.getMessage());
        }
        return Optional.empty();
    }

    @Override
    public Iterable<Utilizator> findAll() {
        HashSet<Utilizator> users = new HashSet<>();
        String selectUsersStatement = "select * from users";
        try(Connection connection = DriverManager.getConnection(DatabaseConnectionConfig.DB_URL,
                DatabaseConnectionConfig.DB_USER, DatabaseConnectionConfig.DB_PASS);
            PreparedStatement getUsersStatement = connection.prepareStatement(selectUsersStatement);
            ResultSet usersResultSet = getUsersStatement.executeQuery();
        ){
            while(usersResultSet.next()){
                String firstName = usersResultSet.getString("first_name");
                String lastName = usersResultSet.getString("last_name");
                Long id = usersResultSet.getLong("id");
                Utilizator currentUser = new Utilizator(firstName, lastName);
                currentUser.setId(id);
                users.add(currentUser);
            }
        }catch(SQLException e){
            System.out.println(e.getMessage());
        }
        return users;
    }

    @Override
    public Optional<Utilizator> save(Utilizator entity) throws RepositoryExceptions {
        Predicate<Utilizator> isNull = Objects::isNull;
        if (isNull.test(entity)){
            throw new RepositoryExceptions("Utilizatorul nu poate sa fie null");
        }
        validator.validate(entity);
        String insertSqlStatement = "insert into users(first_name,last_name) values(?,?)";
        Optional<Utilizator> result = Optional.empty();
        try(Connection connection = DriverManager.getConnection(DatabaseConnectionConfig.DB_URL,
                DatabaseConnectionConfig.DB_USER, DatabaseConnectionConfig.DB_PASS);
            PreparedStatement insertStatement = connection.prepareStatement(insertSqlStatement, Statement.RETURN_GENERATED_KEYS);
        ){
            insertStatement.setString(1, entity.getFirstName());
            insertStatement.setString(2, entity.getLastName());
            if (insertStatement.executeUpdate() > 0){
                ResultSet resultSet = insertStatement.getGeneratedKeys();
                resultSet.next();
                Long id = resultSet.getLong("id");
                entity.setId(id);
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
    public Optional<Utilizator> delete(Long aLong) throws RepositoryExceptions {
        String deleteSqlStatement = "delete from users where id=?";
        try(Connection connection = DriverManager.getConnection(DatabaseConnectionConfig.DB_URL,
                DatabaseConnectionConfig.DB_USER, DatabaseConnectionConfig.DB_PASS);
            PreparedStatement statement = connection.prepareStatement(deleteSqlStatement);
        ){
            statement.setLong(1,aLong);
            Optional<Utilizator> toBeDeleted = findOne(aLong);
            if (toBeDeleted.isPresent()) {
                Utilizator user = toBeDeleted.get();
                if (statement.executeUpdate() > 0){
                    user.setId(-1L);
                    return Optional.of(user);
                }
                else{
                    System.out.println("Delete failed");
                }
            }
        }catch(SQLException e){
            System.out.println(e.getMessage());
        }
        return Optional.empty();
    }

    @Override
    public Optional<Utilizator> update(Utilizator entity) throws RepositoryExceptions {
        Predicate<Utilizator> isNull = Objects::isNull;
        if (isNull.test(entity)){
            throw new RepositoryExceptions("Entitatea nu poate sa fie null.");
        }
        validator.validate(entity);
        Optional<Utilizator> result = Optional.empty();
        String updateSqlStatement = "update users set first_name=?, last_name=? where id=?";
        try (Connection connection = DriverManager.getConnection(DatabaseConnectionConfig.DB_URL,
                DatabaseConnectionConfig.DB_USER, DatabaseConnectionConfig.DB_PASS);
             PreparedStatement updateStatement = connection.prepareStatement(updateSqlStatement, Statement.RETURN_GENERATED_KEYS)
        ){
            updateStatement.setString(1,entity.getFirstName());
            updateStatement.setString(2,entity.getLastName());
            updateStatement.setLong(3,entity.getId());
            if (updateStatement.executeUpdate() > 0){
                ResultSet resultSet = updateStatement.getGeneratedKeys();
                if (resultSet.next()) {
                    result = Optional.empty();
                }
                else{
                    result = Optional.of(entity);
                }
            }
        }catch(SQLException e){
            System.out.println(e.getMessage());
        }
        return result;
    }
}
