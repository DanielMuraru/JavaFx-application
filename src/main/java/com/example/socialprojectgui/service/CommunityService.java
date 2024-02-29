package com.example.socialprojectgui.service;


import java.sql.PreparedStatement;
import java.time.LocalDate;
import java.util.*;

import com.example.socialprojectgui.domain.Entity;
import com.example.socialprojectgui.domain.Friendship;
import com.example.socialprojectgui.domain.User;
import com.example.socialprojectgui.repository.Repository;

class Graph
{
    Map<User,List<User>> listOfFriends;
    private Repository repository;
    public Graph(Repository repo) {
        listOfFriends = new HashMap<>();
        this.repository = repo;

        updateGraph();
    }

    public void updateGraph() {
        Iterable<Friendship> friendships = this.repository.findAll();

        friendships.forEach(
                x -> {
                    List<User> users = new ArrayList<>();
                    friendships.forEach(y -> {
                        if (x.getId().getLeft() == y.getId().getLeft())
                            users.add((User) y.getId().getRight());

                    });
                    listOfFriends.put((User) x.getId().getLeft(), users);
                }
        );

    }
}
public class CommunityService<ID, E extends Entity<ID>> {


    public Map<Integer, User> viz;
    private Integer number;

    private UserService service;
    private Graph graph;

    /**
     * Constructor for ServiceCommunity
     *
     * @param repository:project's repository
     */
    public CommunityService(Repository repository,UserService service) {
        this.number = 0;
        this.viz = new HashMap<>();
        graph = new Graph(repository);
    }

    /**
     * Simple DFS algorithm
     *
     * @param u:User,source node
     */
    public void DFS(User u) {
        viz.put(u.getId(), u);
        if(this.graph.listOfFriends.containsKey(u)) {
            List<User> friendsOF = this.graph.listOfFriends.get(u);
            for (User user : friendsOF) {
                if (!viz.containsKey(user.getId())) {
                    DFS(user);
                }
            }
        }
    }

    /**
     * Calculates the number of communities
     *
     * @return :Integer-number of communities
     */
    public Integer numberOfCommunities() {
        viz.clear();
        this.number = 0;
        this.graph.listOfFriends.forEach((user,friends)->{
            if(!viz.containsKey(user.getId()))
            {
                number++;
                DFS(user);
            }
        });
        /*for (User user : listofUsers) {
            if (!viz.containsKey(user.getId())) {
                number++;
                DFS(user);

            }
        }*/

        return number;
    }

    /**
     * Simple DFS alghoritm for extracting a list of all components of a graph
     *
     * @param u:User,source node
     * @param vis:Map,for   tracking the visited nodes
     */
    public void DFS_COMMUNITIES(User u, Map<Integer, User> vis) {
        vis.put(u.getId(), u);
        List<User> friendsOf = this.graph.listOfFriends.get(u);

        if(this.graph.listOfFriends.containsKey(u)) {
            for (User uu : friendsOf) {
                if (!vis.containsKey(uu.getId())) {
                    DFS_COMMUNITIES(uu, vis);

                }
            }
        }

    }

    /**
     * DFS alghoritm for finding the maximum length of a path in a graph
     *
     * @param u:User,source node
     * @param vis:Map,for   tracking visited nodes
     * @return :int,maximum length
     */
    public int DFS_MOST(User u, Map<Integer, User> vis) {
        int max = -1;
        List<User> friendsOf = this.graph.listOfFriends.get(u);

        if(this.graph.listOfFriends.containsKey(u)) {
            for (User uu : friendsOf) {
                if (!vis.containsKey(uu.getId())) {
                    vis.put(uu.getId(), uu);
                    int l = DFS_MOST(u, vis);
                    if (l > max)
                        max = l;
                    vis.remove(uu.getId());

                }
            }
        }

        return max + 1;
    }

    /**
     * Determines the most sociable communitty
     *
     * @return :List,list of the most sociable communitty users
     */
    public List<User> mostSociableCommunity() {
//        int max = 0;
        this.graph.updateGraph();
        List<List<User>> users = new ArrayList<>();
        Map<Integer, User> visited = new HashMap<>();
        this.graph.listOfFriends.forEach(
                (user,friends)->{
                    if(!visited.containsKey(user.getId()))
                    {
                        DFS_COMMUNITIES(user, visited);
                        List<User> list3 = new ArrayList<>(visited.values());
                        visited.clear();
                        users.add(list3);
                    }
                }
        );
        /*listOfusers.forEach(u -> {
            if (!visited.containsKey(u.getId())) {
                DFS_COMMUNITIES(u, visited);
                List<User> list3 = new ArrayList<>(visited.values());
                visited.clear();
                users.add(list3);
            }
        });*/
        /*for (User u : listOfusers) {
            if (!visited.containsKey(u.getId())) {
                DFS_COMMUNITIES(u, visited);
                List<User> list3 = new ArrayList<>(visited.values());
                visited.clear();
                users.add(list3);
            }

        }*/
        List<User> list2 = new ArrayList<>();
        visited.clear();
        users.forEach(list ->
        {
            list.forEach(u -> {
                int max = 0;
                int l = DFS_MOST(u, visited);
                if (l > max) {
                    max = l;
                    list2.clear();
                    list2.addAll(list);
                }
            });
        });
        /*int max=0;
        for (List<User> list : users) {
            for (User u : list) {
                int l = DFS_MOST(u, visited);
                if (l > max) {
                    max = l;
                    list2.clear();
                    list2.addAll(list);
                }
            }

        }*/
        return list2;
    }
}
