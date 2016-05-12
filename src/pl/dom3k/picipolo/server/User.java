package pl.dom3k.picipolo.server;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Januszek on 2016-05-10.
 */
public class User {
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User player = (User) o;
        return name.equals(player.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    public String getName() {
        return name;
    }

    private String name;
    private String id;
    private HashSet<Game> activeGames;

    public User(String name, String id) {
        this.name = name;
        this.id = id;
        activeGames = new HashSet<>();
    }

    public boolean compareID(String id)throws Exception{
        return this.id.equals(id);
    }

    public boolean addGame(Game game)throws Exception{
        return activeGames.add(game);
    }

    public boolean removeGame(Game game)throws Exception{
        return activeGames.remove(game);
    }

    public boolean compareName(String name)throws Exception{
        return this.name.equals(name);
    }

    public LinkedList<Game> getGames(){
        return new LinkedList<>(activeGames);
    }
}
