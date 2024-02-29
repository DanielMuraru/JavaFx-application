package com.example.socialprojectgui.repository.Database;


import com.example.socialprojectgui.DTO.MessageDTO;
import com.example.socialprojectgui.domain.*;
import com.example.socialprojectgui.repository.Exceptions.DBExceptions;
import com.example.socialprojectgui.repository.Page;
import com.example.socialprojectgui.repository.Pageable;
import com.example.socialprojectgui.repository.PagingRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.sql.*;

public class FriendshipDBRepo implements PagingRepository<Tuple<User,User>,Friendship> {

    String url;
    String username;
    String password;
    Connection connection = null;
    Statement statement = null;

    public FriendshipDBRepo(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.establishConnection();
    }

    private void establishConnection() {
        try {
            Class.forName("org.postgresql.Driver");
            this.connection = DriverManager.getConnection(this.url, this.username, this.password);
            System.out.println("Connection established");
        } catch (ClassNotFoundException | SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    @Override
    public Optional<Friendship> save(Friendship entity) {
        String insertSQL="INSERT INTO \"Friends\"(\"idUser\",\"idFriend\",\"FriendsFrom\") VALUES (?,?,?)";
        try {
            PreparedStatement preparedStatement= connection.prepareStatement(insertSQL);
            User u1 = (User) entity.getId().getLeft();
            User u2 = (User) entity.getId().getRight();

            preparedStatement.setInt(1,u1.getId());
            preparedStatement.setInt(2,u2.getId());
            preparedStatement.setDate(3, Date.valueOf(entity.getFriendsFrom()));
            preparedStatement.executeUpdate();
            preparedStatement.close();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }


        return Optional.of(entity);
    }


    @Override
    public Optional<Friendship> delete(Tuple<User,User> id) {

        String deleteSQL="DELETE FROM \"Friends\" WHERE (\"idUser\" = ? AND \"idFriend\" = ?) OR (\"idUser\" = ? AND \"idFriend\" = ?)";
        try {
            PreparedStatement preparedStatement= connection.prepareStatement(deleteSQL);
            User u1 = (User) id.getLeft();
            User u2 = (User) id.getRight();
            preparedStatement.setInt(1, u1.getId());
            preparedStatement.setInt(2, u2.getId());

            preparedStatement.setInt(3, u2.getId());
            preparedStatement.setInt(4, u1.getId());
            preparedStatement.executeUpdate();
            preparedStatement.close();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }


        return Optional.empty();
    }

    private Optional<Friendship> extractEntity(Tuple id, LocalDate localDate) {

        Friendship friendship = new Friendship();
        friendship.setId(id);
        friendship.setFriendsFrom(localDate);
        return Optional.ofNullable(friendship);

    }

    @Override
    public Optional<Friendship> findOne(Tuple id) {
        LocalDate localDate;
        String selectSQL = "SELECT * FROM \"Friends\" WHERE (\"idUser\" = ? AND \"idFriend\" = ?) OR (\"idUser\" = ? AND \"idFriend\" = ?)";
        ResultSet resultSet = null;

        try {
            PreparedStatement preparedStatement = this.connection.prepareStatement(selectSQL);
            //preparedStatement.setInt(1, (int) id);
            User u1 = (User) id.getLeft();
            User u2 = (User) id.getRight();
            preparedStatement.setInt(1, u1.getId());
            preparedStatement.setInt(2, u2.getId());
            preparedStatement.setInt(3, u2.getId());
            preparedStatement.setInt(4, u1.getId());
            resultSet = preparedStatement.executeQuery();
            if(resultSet.next()) {
                localDate = resultSet.getDate("FriendsFrom").toLocalDate();
                return this.extractEntity(id, localDate);
            }
            preparedStatement.close();


        } catch (SQLException e) {
            if (e.getErrorCode() == 0)
                throw new DBExceptions(e.getMessage());
        }


        return Optional.empty();

    }


    @Override
    public Optional<Friendship> update(Friendship entity) {
        return Optional.empty();
    }

    @Override
    public Iterable<Friendship> findAll() {
        String selectSQL="SELECT \"id\",\"lastName\",\"firstName\",\"email\",\"password\",\"idFriend\",\"FriendsFrom\" FROM \"Users\" INNER JOIN \"Friends\" ON id = \"idUser\" OR \"id\" = \"idFriend\"  ";
        List<Friendship> friendships=new ArrayList<>();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(selectSQL);
            ResultSet resultSet = preparedStatement.executeQuery();
            while(resultSet.next())
            {
                Integer user1Id=resultSet.getInt("id");
                String lastName = resultSet.getString("lastName");
                String firstName = resultSet.getString("firstName");
                String email = resultSet.getString("email");
                String password = resultSet.getString("password");
                User u1 = new User(firstName,lastName, email, password);
                u1.setId(user1Id);

                resultSet.next();

                Integer user2Id=resultSet.getInt("id");
                String lastName2 = resultSet.getString("lastName");
                String firstName2 = resultSet.getString("firstName");
                String email2 = resultSet.getString("email");
                String password2 = resultSet.getString("password");
                User u2 = new User(firstName2,lastName2, email2, password2);
                u2.setId(user2Id);
                LocalDate localDate = resultSet.getDate("FriendsFrom").toLocalDate();


                Optional<Friendship> u= this.extractEntity(new Tuple(u1,u2),localDate);
                friendships.add(u.get());
            }


        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return friendships;
    }
    @Override
    public Page<Friendship> findAll(Pageable p, Integer id)
    {
        String selectSQL="SELECT \"id\",\"lastName\",\"firstName\",\"email\",\"password\",\"idFriend\",\"FriendsFrom\" FROM \"Users\" INNER JOIN \"Friends\" ON id = \"idUser\" OR \"id\" = \"idFriend\" WHERE \"idUser\" = ? or \"idFriend\" = ? LIMIT ? OFFSET ?";
        String count = "SELECT COUNT(*) FROM (SELECT \"id\",\"lastName\",\"firstName\",\"email\",\"password\",\"idFriend\",\"FriendsFrom\" FROM \"Users\" INNER JOIN \"Friends\" ON id = \"idUser\" OR \"id\" = \"idFriend\" WHERE \"idUser\" = ? or \"idFriend\" = ?)";
        List<Friendship> friendships=new ArrayList<>();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(selectSQL);
            preparedStatement.setInt(1, id);
            preparedStatement.setInt(2, id);
            preparedStatement.setInt(3, p.getPageSize()*2);
            preparedStatement.setInt(4, p.getPageSize() * p.getPageNumber()*2);
            PreparedStatement countStatement = connection.prepareStatement(count);
            countStatement.setInt(1, id);
            countStatement.setInt(2, id);
            ResultSet countResult = countStatement.executeQuery();


            ResultSet resultSet = preparedStatement.executeQuery();
            while(resultSet.next())
            {
                Integer user1Id=resultSet.getInt("id");
                String lastName = resultSet.getString("lastName");
                String firstName = resultSet.getString("firstName");
                String email = resultSet.getString("email");
                String password = resultSet.getString("password");
                User u1 = new User(firstName,lastName, email, password);
                u1.setId(user1Id);

                resultSet.next();

                Integer user2Id=resultSet.getInt("id");
                String lastName2 = resultSet.getString("lastName");
                String firstName2 = resultSet.getString("firstName");
                String email2 = resultSet.getString("email");
                String password2 = resultSet.getString("password");
                User u2 = new User(firstName2,lastName2, email2, password2);
                u2.setId(user2Id);
                LocalDate localDate = resultSet.getDate("FriendsFrom").toLocalDate();


                Optional<Friendship> u= this.extractEntity(new Tuple(u1,u2),localDate);
                friendships.add(u.get());
            }

            int totalCount = 0;
            if(countResult.next())
                totalCount = countResult.getInt("count")/2;
            return new Page<>(friendships, totalCount);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    @Override
    public List<MessageDTO> findAll(Tuple<User, User> id) {
        return null;
    }

    @Override
    public Optional<Tuple<Integer,String>> findByEmail(String email) {
        return null;
    }

    @Override
    public Iterable<FriendRequest> findByUserId(Integer idUser) {
        return null;
    }



}

