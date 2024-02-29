package com.example.socialprojectgui.repository.Database;

import com.example.socialprojectgui.DTO.MessageDTO;
import com.example.socialprojectgui.domain.FriendRequest;
import com.example.socialprojectgui.domain.Tuple;
import com.example.socialprojectgui.domain.User;
import com.example.socialprojectgui.repository.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class FriendRequestDBRepo implements Repository<Integer,FriendRequest> {
    String url = "";
    String username = "";
    String password = "";
    Connection connection = null;

    public FriendRequestDBRepo(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.establishConnection();

    }
    private void establishConnection()
    {
        try
        {
            Class.forName("org.postgresql.Driver");
            this.connection = DriverManager.getConnection(this.url, this.username, this.password);
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public Optional<FriendRequest> save(FriendRequest entity) {
        String insertSQL = "INSERT INTO friendrequests (fromwhom,towhom,status) VALUES (?,?,?)";
        try(PreparedStatement preparedStatement = this.connection.prepareStatement(insertSQL))
        {
                preparedStatement.setInt(1, ((FriendRequest) entity).getIdFromWhom());
                preparedStatement.setInt(2,((FriendRequest) entity).getIdToWhom());
                preparedStatement.setString(3,((FriendRequest) entity).getStatus());
                int rowsAffected = preparedStatement.executeUpdate();


        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Optional.of(entity);
    }

    @Override
    public Optional<FriendRequest> delete(Integer id) {
        String deleteSQL = "DELETE FROM friendrequests WHERE idrequest = ? ";
        try(PreparedStatement preparedStatement = this.connection.prepareStatement(deleteSQL))
        {

            preparedStatement.setInt(1,id);
            int rowsAffected = preparedStatement.executeUpdate();


        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Optional.empty();
    }

    @Override
    public Optional<FriendRequest> findOne(Integer id) {
        //TODO de facut functia asta pentru find
        String deleteSQL = "DELETE FROM friendrequests WHERE fromwhom = ? AND towhom = ?  ";
        try(PreparedStatement preparedStatement = this.connection.prepareStatement(deleteSQL))
        {

            preparedStatement.setInt(1,id);
            int rowsAffected = preparedStatement.executeUpdate();


        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Optional.empty();
    }

    @Override
    public Optional<FriendRequest> update(FriendRequest entity) {
        String updateSQL = "UPDATE friendrequests SET fromwhom = ?,towhom = ?,status = ? WHERE fromwhom = ? AND towhom = ?";
        try(PreparedStatement preparedStatement = this.connection.prepareStatement(updateSQL))
        {
            preparedStatement.setInt(1, ((FriendRequest) entity).getIdFromWhom());
            preparedStatement.setInt(2,((FriendRequest) entity).getIdToWhom());
            preparedStatement.setString(3,((FriendRequest) entity).getStatus());
            preparedStatement.setInt(4,((FriendRequest) entity).getIdFromWhom());
            preparedStatement.setInt(5,((FriendRequest) entity).getIdToWhom());
            int rowsAffected = preparedStatement.executeUpdate();


        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Optional.of(entity);
    }

    @Override
    public Iterable<FriendRequest> findAll() {
        String selectSQL = "SELECT idrequest,status,id,\"lastName\",\"firstName\" FROM friendrequests INNER JOIN \"Users\" AS u on fromwhom = u.id" ;
        String selectSQL2 = "SELECT  idrequest,status,id,\"lastName\",\"firstName\" FROM friendrequests INNER JOIN \"Users\" AS u on towhom = u.id" ;
        List<FriendRequest> friendRequestList = new ArrayList<>();
        try(PreparedStatement preparedStatement = this.connection.prepareStatement(selectSQL);
        PreparedStatement preparedStatement1 = this.connection.prepareStatement(selectSQL2))
        {
            ResultSet resultSet = preparedStatement.executeQuery();
            ResultSet resultSet1 = preparedStatement1.executeQuery();
            while(resultSet.next() && resultSet1.next())
            {
                Integer id = resultSet.getInt("idrequest");
                Integer idFrom = resultSet.getInt("id");
                String lNameFromWhom = resultSet.getString("lastName");
                String fNameFromWhom = resultSet.getString("firstName");
                String status = resultSet.getString("status");


                Integer idTo = resultSet1.getInt("id");
                String lNameToWhom = resultSet1.getString("lastName");
                String fNameToWhom = resultSet1.getString("firstName");

                FriendRequest friendRequest = new FriendRequest(idFrom,idTo,lNameFromWhom+" "+fNameFromWhom,lNameToWhom+" "+fNameToWhom,status);
                friendRequest.setId(id);
                friendRequestList.add(friendRequest);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return friendRequestList;
    }

    @Override
    public List<MessageDTO> findAll(Tuple<User, User> id) {
        return null;
    }

    @Override
    public Iterable<FriendRequest> findByUserId(Integer idUser)
    {
        String selectSQL = "SELECT idrequest,status,id,\"lastName\",\"firstName\" FROM friendrequests INNER JOIN \"Users\" AS u on fromwhom = ?" ;
        String selectSQL2 = "SELECT  idrequest,status,id,\"lastName\",\"firstName\" FROM friendrequests INNER JOIN \"Users\" AS u on towhom = ?" ;
        List<FriendRequest> friendRequestList = new ArrayList<>();
        try(PreparedStatement preparedStatement = this.connection.prepareStatement(selectSQL);
            PreparedStatement preparedStatement1 = this.connection.prepareStatement(selectSQL2))
        {

            preparedStatement.setInt(1, idUser);
            preparedStatement1.setInt(1, idUser);
            ResultSet resultSet = preparedStatement.executeQuery();
            ResultSet resultSet1 = preparedStatement1.executeQuery();
            while(resultSet.next() && resultSet1.next())
            {
                Integer id = resultSet.getInt("idrequest");
                Integer idFrom = resultSet.getInt("id");
                String lNameFromWhom = resultSet.getString("lastName");
                String fNameFromWhom = resultSet.getString("firstName");
                String status = resultSet.getString("status");


                Integer idTo = resultSet1.getInt("id");
                String lNameToWhom = resultSet1.getString("lastName");
                String fNameToWhom = resultSet1.getString("firstName");

                FriendRequest friendRequest = new FriendRequest(idFrom,idTo,lNameFromWhom+" "+fNameFromWhom,lNameToWhom+" "+fNameToWhom,status);
                friendRequest.setId(id);
                friendRequestList.add(friendRequest);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return friendRequestList;
    }
    @Override
    public Optional<Tuple<Integer, String>> findByEmail(String email) {
        return Optional.empty();
    }
}
