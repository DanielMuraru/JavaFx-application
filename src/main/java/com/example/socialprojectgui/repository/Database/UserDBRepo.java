package com.example.socialprojectgui.repository.Database;

import com.example.socialprojectgui.DTO.MessageDTO;
import com.example.socialprojectgui.domain.*;
import com.example.socialprojectgui.repository.Exceptions.DBExceptions;
import com.example.socialprojectgui.repository.Repository;

import java.sql.Date;
import java.time.LocalDate;
import java.util.*;
import java.sql.*;

public class UserDBRepo<ID, E extends Entity<ID>> implements Repository<ID, E> {

    String url = "jdbc:postgresql://localhost:5432/ProjectDB";
    String username = "postgres";
    String password = "Aluire1@2";
    Connection connection = null;
    Statement statement = null;

    public UserDBRepo() {
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
    public Optional<E> save(E entity) {
        try {
            if (entity.getClass() == User.class) {
                PreparedStatement preparedStatement = this.connection.prepareStatement("INSERT INTO \"Users\"(\"id\",\"lastName\",\"firstName\",email,password) VALUES (?,?,?,?,?)");
                String lastName = ((User) entity).getLastName();
                String firstName = ((User) entity).getFirstName();
                preparedStatement.setInt(1, (Integer) entity.getId());
                preparedStatement.setString(2, lastName);
                preparedStatement.setString(3, firstName);
                preparedStatement.setString(4, ((User) entity).getUserEmail());
                preparedStatement.setString(5, ((User) entity).getUserPassword());
                int affected_rows = preparedStatement.executeUpdate();
                if (affected_rows == 0) {
                    preparedStatement.close();
                    throw new DBExceptions("User could not be saved");
                }
            }


        } catch (SQLException e) {
            if (e.getErrorCode() == 0)
                throw new DBExceptions("Id already exists");
        }


        return Optional.of(entity);
    }


    @Override
    public Optional<E> delete(ID id) {

        String deleteSQL = "DELETE FROM \"Users\" WHERE \"id\" = " + id.toString();

        try {
            PreparedStatement preparedStatement = this.connection.prepareStatement(deleteSQL);
//            preparedStatement.setInt(1,  id);
            preparedStatement.executeUpdate();
            preparedStatement.close();

        } catch (SQLException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }


        return Optional.empty();
    }
    public void addF(User u , User ot, LocalDate str)
    {
        String insertSQL="INSERT INTO \"Friends\"(\"idUser\",\"idFriend\",\"FriendsFrom\") VALUES (?,?,?)";
        try {
            PreparedStatement preparedStatement= connection.prepareStatement(insertSQL);
            preparedStatement.setInt(1,u.getId());
            preparedStatement.setInt(2,ot.getId());
            preparedStatement.setDate(3, Date.valueOf(str));
            preparedStatement.executeUpdate();
            preparedStatement.close();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public void removeF(User u ,User ot)
    {
        String deleteSQL="DELETE FROM \"Friends\" WHERE \"idUser\" = ? AND \"idFriend\" = ?";
        try {
            PreparedStatement preparedStatement= connection.prepareStatement(deleteSQL);
            preparedStatement.setInt(1,u.getId());
            preparedStatement.setInt(2,ot.getId());
            preparedStatement.executeUpdate();
            preparedStatement.close();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public Map<LocalDate,List<User>> getF(User u)
    {
        String selectSQL="SELECT * FROM \"Friends\" WHERE \"idUser\" = ?";
        List<User> uus=new ArrayList<>();
        Map<LocalDate,List<User>> users=new HashMap<>();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(selectSQL);
            preparedStatement.setInt(1,u.getId());
            ResultSet resultSet = preparedStatement.executeQuery();
            String[] args=new String[2];
            while(resultSet.next())
            {
                Integer id=resultSet.getInt(2);
                LocalDate date=resultSet.getDate(4).toLocalDate();
                Optional<E> uu=this.findOne((ID) id);
//                users.add((User) uu.get());
                uus.add((User) uu.get());
                users.put(date,uus);
            }
            preparedStatement.close();
            selectSQL="SELECT * FROM \"Friends\" WHERE \"idFriend\" = ? ";
            PreparedStatement preparedStatement1= connection.prepareStatement(selectSQL);
            preparedStatement1.setInt(1,u.getId());
            resultSet = preparedStatement1.executeQuery();
            while(resultSet.next())
            {
                Integer id=resultSet.getInt(1);
                LocalDate date=resultSet.getDate(4).toLocalDate();
                Optional<E> uu=this.findOne((ID) id);
                uus.add((User) uu.get());
                users.put(date,uus);
            }


        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return users;
    }
    private Optional<E> extractEntity(ID id, String[] args) {
        if (id.getClass() == Integer.class) {
            User u = new User(args[1], args[0], args[2], args[3]);
            u.setId((Integer) id);
            return Optional.of((E) u);
        }
        if (id.getClass() == Tuple.class) {
            E friendship = (E) new Friendship();
            friendship.setId(id);
            return Optional.ofNullable(friendship);
        }
        return Optional.empty();
    }

    @Override
    public Optional<E> findOne(ID id) {
        String[] args = new String[4];
        String selectSQL = "SELECT * FROM \"Users\" WHERE id = " + id.toString();

        ResultSet resultSet = null;

        try {
            PreparedStatement preparedStatement = this.connection.prepareStatement(selectSQL);

            resultSet = preparedStatement.executeQuery();
            resultSet.next();
            if (id.getClass() == Integer.class) {
                args[0] = resultSet.getString(2);
                args[1] = resultSet.getString(3);
                args[2] = resultSet.getString(4);
                args[3] = resultSet.getString(5);
                preparedStatement.close();
            }

            return (Optional<E>) this.extractEntity(id, args);

        } catch (SQLException e) {
            if (e.getErrorCode() == 0)
                throw new DBExceptions("User not found");
        }

        return Optional.empty();
    }


    @Override
    public Optional<E> update(E entity) {
        String updateSQL="UPDATE \"Users\" SET \"lastName\" = ? , \"firstName\" = ? , \"email\" = ? , \"password\" = ? WHERE \"id\" = ?";
        try {
            PreparedStatement preparedStatement=connection.prepareStatement(updateSQL);
            preparedStatement.setString(1, ((User) entity).getLastName());
            preparedStatement.setString(2,((User) entity).getFirstName());
            preparedStatement.setString(3,((User) entity).getUserEmail());
            preparedStatement.setString(4,((User) entity).getUserPassword());
            preparedStatement.setInt(5, (Integer) entity.getId());
            int rows=preparedStatement.executeUpdate();
            if(rows == 0)
            {
                throw new DBExceptions("User update failed");
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Optional.of(entity);
    }

    @Override
    public Iterable<E> findAll() {
        String selectSQL="SELECT * FROM \"Users\" ";
        List<User> users=new ArrayList<>();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(selectSQL);
            ResultSet resultSet = preparedStatement.executeQuery();
            String[] args=new String[4];
            while(resultSet.next())
            {
                Integer id=resultSet.getInt(1);
                String lastName = resultSet.getString(2);
                String firstName = resultSet.getString(3);
                String email = resultSet.getString(4);
                String password = resultSet.getString(5);
                args[1]=lastName;
                args[0]=firstName;
                args[2] = email;
                args[3] = password;
                Optional<User> u= (Optional<User>) this.extractEntity((ID) id,args);
                users.add(u.get());
            }


        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return (Iterable<E>) users;
    }

    @Override
    public List<MessageDTO> findAll(Tuple<User, User> id) {
        return null;
    }

    @Override
    public Optional<Tuple<Integer,String>> findByEmail(String email)
    {
        String findS = "SELECT * FROM \"Users\" WHERE email = ?";
        try
        {
            PreparedStatement preparedStatement = connection.prepareStatement(findS);
            preparedStatement.setString(1,email);
            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next())
                return Optional.of(new Tuple<Integer,String>(resultSet.getInt("id"),resultSet.getString("password")));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Optional.empty();
    }

    @Override
    public Iterable<FriendRequest> findByUserId(Integer idUser) {
        return null;
    }

}
