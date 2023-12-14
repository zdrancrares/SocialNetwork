package com.example.socialnetworkgui.repository;

import com.example.socialnetworkgui.config.DatabaseConnectionConfig;
import com.example.socialnetworkgui.domain.Utilizator;
import com.example.socialnetworkgui.domain.validators.Validator;
import com.example.socialnetworkgui.exceptions.RepositoryExceptions;
import com.example.socialnetworkgui.repository.paging.Page;
import com.example.socialnetworkgui.repository.paging.Pageable;
import com.example.socialnetworkgui.repository.paging.PagingRepository;

import java.sql.*;
import java.util.*;
import java.util.function.Predicate;

public class UserDBRepository implements PagingRepository<Long, Utilizator> {
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
                Utilizator user = new Utilizator(null, null, null, null);
                user.setId(resultSet.getLong(1));
                user.setFirstName(resultSet.getString(2));
                user.setLastName(resultSet.getString(3));
                user.setEmail(resultSet.getString(4));
                user.setPassword(resultSet.getString(5));

                ArrayList<Utilizator> friends = new ArrayList<>();

                if (resultSet.getString(7) != null) {
                    Utilizator friend = new Utilizator(null, null, null, null);
                    friend.setId(resultSet.getLong(6));
                    friend.setFirstName(resultSet.getString(7));
                    friend.setLastName(resultSet.getString(8));
                    friend.setEmail(resultSet.getString(9));
                    friend.setPassword(resultSet.getString(10));
                    friends.add(friend);

                    while (resultSet.next()) {
                        friend = new Utilizator(null, null, null, null);
                        friend.setId(resultSet.getLong(6));
                        friend.setFirstName(resultSet.getString(7));
                        friend.setLastName(resultSet.getString(8));
                        friend.setEmail(resultSet.getString(9));
                        friend.setPassword(resultSet.getString(10));
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
                String email = usersResultSet.getString("email");
                String password = usersResultSet.getString("password");
                Long id = usersResultSet.getLong("id");
                Utilizator currentUser = new Utilizator(firstName, lastName, email, password);
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
        String insertSqlStatement = "insert into users(first_name,last_name,email,password) values(?,?,?,?)";
        Optional<Utilizator> result = Optional.empty();
        try(Connection connection = DriverManager.getConnection(DatabaseConnectionConfig.DB_URL,
                DatabaseConnectionConfig.DB_USER, DatabaseConnectionConfig.DB_PASS);
            PreparedStatement insertStatement = connection.prepareStatement(insertSqlStatement, Statement.RETURN_GENERATED_KEYS);
        ){
            insertStatement.setString(1, entity.getFirstName());
            insertStatement.setString(2, entity.getLastName());
            insertStatement.setString(3, entity.getEmail());
            insertStatement.setString(4, entity.getPassword());
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

    private int returnNumberOfElements(){
        int number = 0;
        try (Connection connection = DriverManager.getConnection(DatabaseConnectionConfig.DB_URL,
                DatabaseConnectionConfig.DB_USER, DatabaseConnectionConfig.DB_PASS);
            PreparedStatement statement = connection.prepareStatement("select count(*) as count from users");
            ResultSet resultSet = statement.executeQuery();
        ){
            while(resultSet.next()){
                number = resultSet.getInt("count");
            }
        }catch(SQLException e){
            System.out.println(e.getMessage());
        }
        return number;
    }

    @Override
    public Page<Utilizator> findAll(Pageable pageable) {
        int numberOfElements = returnNumberOfElements();
        int limit = pageable.getPageSize();
        int offset = pageable.getPageSize() * pageable.getPageNumber();
        if (offset >= numberOfElements){
            return new Page<>(new HashSet<>(), numberOfElements);
        }
        HashSet<Utilizator> users = new HashSet<>();
        try(Connection connection = DriverManager.getConnection(DatabaseConnectionConfig.DB_URL,
                DatabaseConnectionConfig.DB_USER, DatabaseConnectionConfig.DB_PASS);
            PreparedStatement statement = connection.prepareStatement("select * from users limit ? offset ?");
        ){
            statement.setInt(1, limit);
            statement.setInt(2, offset);
            ResultSet usersResultSet = statement.executeQuery();
            while(usersResultSet.next()){
                String firstName = usersResultSet.getString("first_name");
                String lastName = usersResultSet.getString("last_name");
                String email = usersResultSet.getString("email");
                String password = usersResultSet.getString("password");
                Long id = usersResultSet.getLong("id");
                Utilizator currentUser = new Utilizator(firstName, lastName, email,password);
                currentUser.setId(id);
                users.add(currentUser);
            }
        }catch(SQLException e){
            System.out.println(e.getMessage());
        }
        return new Page<>(users, numberOfElements);
    }

    @Override
    public Optional<Utilizator> findUserByEmailPassword(String email, String password) {
        String selectUserStatement = "select * from users where email=? and password=?";
        try(Connection connection = DriverManager.getConnection(DatabaseConnectionConfig.DB_URL,
                DatabaseConnectionConfig.DB_USER, DatabaseConnectionConfig.DB_PASS);
            PreparedStatement getUserStatement = connection.prepareStatement(selectUserStatement);
        ){
            getUserStatement.setString(1, email);
            getUserStatement.setString(2, password);
            ResultSet userResultSet = getUserStatement.executeQuery();
            if(userResultSet.next()){
                String firstName = userResultSet.getString("first_name");
                String lastName = userResultSet.getString("last_name");
                Long id = userResultSet.getLong("id");
                Utilizator currentUser = new Utilizator(firstName, lastName, email, password);
                currentUser.setId(id);
                return Optional.of(currentUser);
            }
        }catch(SQLException e){
            System.out.println(e.getMessage());
        }
        return Optional.empty();
    }
}
