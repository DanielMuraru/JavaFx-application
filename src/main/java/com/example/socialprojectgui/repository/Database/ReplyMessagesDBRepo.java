package com.example.socialprojectgui.repository.Database;


import com.example.socialprojectgui.DTO.MessageDTO;
import com.example.socialprojectgui.domain.*;
import com.example.socialprojectgui.repository.Repository;

import java.sql.*;
import java.util.List;
import java.util.Optional;

public class ReplyMessagesDBRepo implements Repository<Tuple<Integer, List<Integer>>, ReplyMessage> {
    private String url;
    private String username;
    private String password;
    private Connection connection;
    public ReplyMessagesDBRepo(String url, String username, String password)
    {
        this.url = url;
        this.username = username;
        this.password = password;
        this.establishConnection();

    }
    private void establishConnection()
    {
        try {
            Class.forName("org.postgresql.Driver");
            this.connection = DriverManager.getConnection(this.url, this.username, this.password);
            System.out.println("Connection established!");
        } catch (ClassNotFoundException | SQLException e) {
            throw new RuntimeException(e);
        }

    }


    @Override
    public Optional<ReplyMessage> save(ReplyMessage entity) {
        String insertSQL = "INSERT INTO replymessages" +
                " (message,idmessage,date) VALUES (?,?,?)";
        try(PreparedStatement preparedStatement = this.connection.prepareStatement(insertSQL)) {

            preparedStatement.setString(1, entity.getMessage());
            preparedStatement.setInt(2, entity.getMessageToRespond());
            preparedStatement.setTimestamp(3, Timestamp.valueOf(entity.getDate()));
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Optional.empty();
    }

    @Override
    public Optional<ReplyMessage> delete(Tuple<Integer, List<Integer>> integerIntegerTuple) {

        return Optional.empty();
    }

    @Override
    public Optional<ReplyMessage> findOne(Tuple<Integer, List<Integer>> integerIntegerTuple) {
        return Optional.empty();
    }

    @Override
    public Optional<ReplyMessage> update(ReplyMessage entity) {
        return Optional.empty();
    }

    @Override
    public Iterable<ReplyMessage> findAll() {
        return null;
    }

    @Override
    public List<MessageDTO> findAll(Tuple<User, User> id) {
        return null;
    }

    @Override
    public Optional<Tuple<Integer, String>> findByEmail(String email) {
        return Optional.empty();
    }

    @Override
    public Iterable<FriendRequest> findByUserId(Integer idUser) {
        return null;
    }
}
