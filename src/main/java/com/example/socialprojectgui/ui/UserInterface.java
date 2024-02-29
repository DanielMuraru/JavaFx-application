package com.example.socialprojectgui.ui;

import com.example.socialprojectgui.domain.Friendship;
import com.example.socialprojectgui.domain.FriendshipException;
import com.example.socialprojectgui.domain.User;
import com.example.socialprojectgui.domain.validators.ValidationException;
import com.example.socialprojectgui.repository.Exceptions.DBExceptions;
import com.example.socialprojectgui.repository.Exceptions.RepoExceptions;
import com.example.socialprojectgui.service.CommunityService;
import com.example.socialprojectgui.service.UserService;

import java.util.InputMismatchException;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.function.Consumer;

public class UserInterface {
    String menu = "1.Add user\n2.Delete user\n3.Update user\n4.Find user\n5.Add friend\n6.Delete friend\n7.Number of communities\n8.The most sociable communitty\n9.All users\n10.Get friends\n0.Exit";
    UserService userService;
    CommunityService communityService;

    public UserInterface(UserService userService, CommunityService communityService) {
        this.userService = userService;
        this.communityService = communityService;
    }

    /**
     * Reads attributes of a possible user
     */
    public void addUser() {
        Scanner scanner = new Scanner(System.in);

        System.out.print("ID: ");
        Integer id = scanner.nextInt();
        System.out.println();

        System.out.print("First name: ");
        String firstName = scanner.next();
        System.out.println();

        System.out.print("Last name: ");
        String lastName = scanner.next();
        System.out.println();

        System.out.print("Email name: ");
        String email = scanner.next();
        System.out.println();

        System.out.print("pass name: ");
        String pass = scanner.next();
        System.out.println();



        userService.addUser(id, firstName, lastName, email, pass);
        System.out.println("User successfully added");


    }

    /**
     * Reads id of a possible existing user
     */
    public void removeUser() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("ID: ");
        Integer id = scanner.nextInt();
        userService.removeUser(id);
        Consumer<String> prnt=System.out::println;
        prnt.accept("User deleted successfully");
//        System.out.println("User deleted successfully");

    }

    /**
     * Reads attributes for a possible existing user
     */
    public void updateUser() {
        Scanner scanner = new Scanner(System.in);

        System.out.print("ID: ");
        Integer id = scanner.nextInt();
        System.out.println();

        System.out.print("First name: ");
        String firstName = scanner.next();
        System.out.println();

        System.out.print("Last name: ");
        String lastName = scanner.next();
        System.out.println();

        System.out.print("Email name: ");
        String email = scanner.next();
        System.out.println();

        System.out.print("pass name: ");
        String pass = scanner.next();
        System.out.println();

        userService.updateUser(id, firstName, lastName, email, pass);
        System.out.println("User successfully updated");
    }

    public void findUser()
    {
        Scanner scanner = new Scanner(System.in);

        System.out.print("ID: ");
        Integer id = scanner.nextInt();
        System.out.println();
        User u=userService.findUser(id);
        System.out.println(u);
    }

    /**
     * Reads ids of two possible existing users
     */
    public void addFriend() {
        Scanner scanner = new Scanner(System.in);

        System.out.print("ID_user: ");
        Integer id = scanner.nextInt();
        System.out.println();


        System.out.print("ID_friend: ");
        Integer id1 = scanner.nextInt();
        System.out.println();
        userService.addFriend(id, id1);
        System.out.println("Friend successfully added!");

    }

    /**
     * Reads ids of two possible existing users
     */
    public void removeFriend() {

        Scanner scanner = new Scanner(System.in);
        System.out.print("ID_user: ");
        Integer id = scanner.nextInt();
        System.out.println();


        System.out.print("ID_friend: ");
        Integer id1 = scanner.nextInt();
        System.out.println();
        userService.removeFriend(id, id1);

        System.out.println("Friend successfully deleted!");
    }

    /**
     * Prints the number of communities
     */
    public void numOfCommunitties() {
        System.out.println("Sunt " + communityService.numberOfCommunities() + " comunitati");
    }

    /**
     * Prints the users of the most sociable communitty
     */
    public void mostSociableCommunitty() {
        List<User> users = communityService.mostSociableCommunity();
        System.out.println("Cea mai sociabila comunitate este: " + users);
    }

    public void allEntities()
    {
        Iterable<User> entities= userService.getAll();
        for(User u:entities)
            System.out.println(u);
    }
    public void getFriends()
    {

        Scanner scanner = new Scanner(System.in);
        System.out.print("ID_user: ");
        Integer id = scanner.nextInt();
        System.out.print("Luna anului:");
        Integer luna = scanner.nextInt();
        System.out.println();
        List<Friendship> friendships=userService.getFriends(id,luna);
        if(!friendships.isEmpty())
            friendships.forEach(x->{
                User u = (User) x.getId().getRight();
                System.out.println(u.getLastName() + " | " + u.getFirstName() + " | " + x.getFriendsFrom().toString());
            });
        else System.out.println("User does not have any friends");


    }

    public void run() {
        while (true) {
            System.out.println(menu);
            try {
                Scanner scanner = new Scanner(System.in);
                int command = scanner.nextInt();

                switch (command) {
                    case 1:
                        this.addUser();
                        break;
                    case 2:
                        this.removeUser();
                        break;
                    case 3:
                        this.updateUser();
                        break;
                    case 4:
                        this.findUser();
                        break;
                    case 5:
                        this.addFriend();
                        break;
                    case 6:
                        this.removeFriend();
                        break;
                    case 7:
                        this.numOfCommunitties();
                        break;
                    case 8:
                        this.mostSociableCommunitty();
                        break;
                    case 9:
                        this.allEntities();
                        break;
                    case 10:
                        this.getFriends();
                        break;
                    case 0:
                        System.exit(0);
                }
            } catch (InputMismatchException e) {
                System.out.println("Input invalid!");
            } catch (RepoExceptions e) {
                System.out.println(e.getMessage());
            } catch (ValidationException e) {
                System.out.println(e.getMessage());
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
            } catch (FriendshipException e) {
                System.out.println(e.getMessage());
            }
            catch (DBExceptions e) {
                System.out.println(e.getMessage());
            }
            catch (RuntimeException e)
            {

                System.out.println(e.getMessage());
            }

        }
    }
}
