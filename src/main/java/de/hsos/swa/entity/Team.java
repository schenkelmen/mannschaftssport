package de.hsos.swa.entity;

import java.util.ArrayList;
import java.util.List;

public class Team {
    private String id;
    private String name;
    private String category;
    private String managerId;
    private List<String> playerIds = new ArrayList<>();

    public Team() {}
    public Team(String id, String name, String category, String managerId, List<String> playerIds) {
        this.id       = id;
        this.name     = name;
        this.category = category;
    }

    public String getId() { return id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getManagerId() { return managerId; }
    public void setManagerId(String managerId) { this.managerId = managerId; }

    public List<String> getPlayerIds() { return playerIds; }
    public void setPlayerIds(List<String> playerIds) { this.playerIds = playerIds; }
}