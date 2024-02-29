package com.example.socialprojectgui.repository.Database;

import com.example.socialprojectgui.DTO.MessageDTO;
import com.example.socialprojectgui.domain.*;
import com.example.socialprojectgui.repository.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MessagesDBRepo implements Repository<Tuple<Integer, List<Integer>>, Message> {
    private String url;
    private String username;
    private String password;
    private Connection connection;
    public MessagesDBRepo(String url, String username, String password)
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
    public Optional<Message> save(Message entity) {

        String insertSQL = "INSERT INTO messages" +
                " (sender,message,date) VALUES (?,?,?)";
        String insertSQL2 = "INSERT INTO repliersforonemessage (\"replierID\",\"senderID\") VALUES (?,?)";
        try(PreparedStatement preparedStatement2 = this.connection.prepareStatement(insertSQL2);
                PreparedStatement preparedStatement = this.connection.prepareStatement(insertSQL)) {

            preparedStatement.setInt(1, entity.getFromUser().getId());
            preparedStatement.setString(2, entity.getMessage());
            preparedStatement.setTimestamp(3,Timestamp.valueOf(entity.getDate()));
            preparedStatement.executeUpdate();
            preparedStatement2.setInt(2,entity.getFromUser().getId());
            String smth="SELECT COUNT(*) as exista FROM repliersforonemessage where \"replierID\" = ? and \"senderID\" = ?";
            PreparedStatement preparedStatement1 = this.connection.prepareStatement(smth);
            preparedStatement1.setInt(2,entity.getFromUser().getId());
            entity.getToUsers().forEach(replier->{
                try {

                    preparedStatement1.setInt(1,replier.getId());
                    ResultSet resultSet = preparedStatement1.executeQuery();
                    if(resultSet.next()) {
                        if(resultSet.getInt("exista") == 0) {
                            preparedStatement2.setInt(1, replier.getId());
                            preparedStatement2.executeUpdate();
                        }
                    }
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            });
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Optional.empty();
    }

    @Override
    public Optional<Message> delete(Tuple<Integer, List<Integer>> integerIntegerTuple) {
        String deleteSQL = "DELETE FROM messages WHERE sender = ? and replier = ?";
        try(PreparedStatement preparedStatement = this.connection.prepareStatement(deleteSQL))
        {
            //TODO De facult delete u
            preparedStatement.setInt(1, integerIntegerTuple.getLeft());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Optional.empty();
    }

    @Override
    public List<MessageDTO> findAll(Tuple<User, User> id) {
        //String selectSQL = "SELECT * FROM (SELECT m.sender,m.message,m.date,r.message  AS reply ,r.idmessage FROM messages AS m LEFT JOIN replymessages AS r ON m.ididentity = r.idmessage ORDER BY m.date,r.date) WHERE (sender = ? AND replier = ?) OR (sender = ? AND replier = ?) ";
        String selectSQL2 = "SELECT m.ididentity,m.sender,m.message,m.date,rm.\"replierID\" as replier FROM messages AS m INNER JOIN repliersforonemessage AS rm ON m.sender = rm.\"senderID\"  WHERE (m.sender = ? AND rm.\"replierID\" = ?) OR (m.sender = ? AND rm.\"replierID\" = ?) ORDER BY m.date";
        String selectSQL3 = "SELECT m.ididentity,m.sender,r.\"replierID\" as replier,m.message,rm.message as rply, m.date as msgdate, rm.date as rplydate, rm.idreplymessage FROM messages as m \n" +
                "INNER JOIN repliersforonemessage as r on m.sender = r.\"senderID\"\n" +
                "LEFT JOIN replymessages as rm on m.ididentity = rm.idmessage\n" +
                "WHERE (m.sender = ? AND r.\"replierID\" = ?) OR (m.sender = ? AND r.\"replierID\" = ?)\n" +
                "ORDER BY m.date,rm.date";
        String selectSQL ="SELECT * FROM (\n" +
                "SELECT\n" +
                "    m.ididentity,\n" +
                "    m.sender,\n" +
                "    r.\"replierID\" AS replier_id,\n" +
                "    m.message,\n" +
                "    NULL AS rply,  -- Placeholder for reply message, as this is the main message\n" +
                "    NULL AS idreplymessage,  -- Placeholder for reply message ID\n" +
                "    m.date AS msgdate\n" +
                "FROM\n" +
                "    messages AS m\n" +
                "INNER JOIN\n" +
                "    repliersforonemessage AS r ON m.sender = r.\"senderID\"\n" +
                "WHERE (m.sender = ? AND r.\"replierID\" = ?) OR (m.sender = ? AND r.\"replierID\" = ?)\n" +
                "\n" +
                "UNION\n" +
                "\n" +
                "-- Query for reply messages\n" +
                "SELECT\n" +
                "    m.ididentity,\n" +
                "    m.sender,\n" +
                "    NULL AS replier_id,  -- Placeholder for replier ID, as this is a reply message\n" +
                "    m.message AS main_message,\n" +
                "    rm.message AS rply,\n" +
                "    rm.idreplymessage,\n" +
                "    rm.date \n" +
                "FROM\n" +
                "    messages AS m\n" +
                "INNER JOIN\n" +
                "    replymessages AS rm ON m.ididentity = rm.idmessage\n" +
                "WHERE (m.sender = ? or m.sender = ?) \n" +
                "\n" +
                "   )\n" +
                "\n" +
                "ORDER BY msgdate;";

        List<MessageDTO> messages = new ArrayList<>();

        try(PreparedStatement preparedStatement = this.connection.prepareStatement(selectSQL))
        {
            preparedStatement.setInt(1,id.getLeft().getId());
            preparedStatement.setInt(2,id.getRight().getId());
            preparedStatement.setInt(3,id.getRight().getId());
            preparedStatement.setInt(4,id.getLeft().getId());
            preparedStatement.setInt(5,id.getRight().getId());
            preparedStatement.setInt(6,id.getLeft().getId());

            ResultSet resultSet = preparedStatement.executeQuery();
            while(resultSet.next())
            {
                String message = "";
                if(resultSet.getString("rply") == null || resultSet.getString("rply").isEmpty()) {
                    if (id.getLeft().getId() == resultSet.getInt("sender")) {
                        message = /*id.getLeft().getFirstName() + ": " + */resultSet.getString("message");
                        MessageDTO messageDTO = new MessageDTO(resultSet.getInt("ididentity"), message, resultSet.getTimestamp("msgdate").toLocalDateTime(),null);
                        messageDTO.setIdSender(id.getLeft().getId());
                        messageDTO.setIdReplier(id.getRight().getId());
                        messages.add(messageDTO);
                    } else if (id.getLeft().getId() == resultSet.getInt("replier_id")) {
                        message = /*id.getRight().getFirstName() + ": " + */resultSet.getString("message");
                        MessageDTO messageDTO = new MessageDTO(resultSet.getInt("ididentity"), message, resultSet.getTimestamp("msgdate").toLocalDateTime(),null);
                        messageDTO.setIdReplier(id.getLeft().getId());
                        messageDTO.setIdSender(id.getRight().getId());
                        messages.add(messageDTO);
                    }
                }
                else {
                    if (id.getRight().getId() == resultSet.getInt("sender")) {
                        message = /*id.getLeft().getFirstName() + ": " + */resultSet.getString("rply");
                        MessageDTO messageDTO = new MessageDTO(resultSet.getInt("idreplymessage"), message, resultSet.getTimestamp("msgdate").toLocalDateTime(), resultSet.getString("message"));
                        messageDTO.setIdSender(id.getRight().getId());
                        messageDTO.setIdReplier(id.getLeft().getId());
                        messages.add(messageDTO);
                    } else if (id.getLeft().getId() == resultSet.getInt("sender")) {
                        message = /*id.getRight().getFirstName() + ": " + */resultSet.getString("rply");
                        MessageDTO messageDTO = new MessageDTO(resultSet.getInt("idreplymessage"), message, resultSet.getTimestamp("msgdate").toLocalDateTime(), resultSet.getString("message"));
                        messageDTO.setIdSender(id.getLeft().getId());
                        messageDTO.setIdReplier(id.getRight().getId());
                        messages.add(messageDTO);
                    }
                }

                /*if(!(resultSet.getString("reply") == null))
                {
                    String message1 = "Other: " +resultSet.getString("reply");
                    messages.add(new MessageDTO(resultSet.getInt("idmessage"),message1, resultSet.getTimestamp("date").toLocalDateTime()));
                }*/
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return messages;
    }

    @Override
    public Optional<Message> findOne(Tuple<Integer, List<Integer>> integerIntegerTuple) {
        return Optional.empty();
    }

    @Override
    public Optional<Message> update(Message entity) {
        return Optional.empty();
    }

    @Override
    public Iterable<Message> findAll() {
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
