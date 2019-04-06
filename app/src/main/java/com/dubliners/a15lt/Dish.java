package com.dubliners.a15lt;

import java.util.List;

public class Dish {
    private String creator;
    private String creator_uid;
    private String dishName;
    private String voteCount;
    private List<String> voterList;

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getCreator_uid() {
        return creator_uid;
    }

    public void setCreator_uid(String creator_uid) {
        this.creator_uid = creator_uid;
    }

    public String getDishName() {
        return dishName;
    }

    public void setDishName(String dishName) {
        this.dishName = dishName;
    }

    public String getVoteCount() {
        return voteCount;
    }

    public void setVoteCount(String voteCount) {
        this.voteCount = voteCount;
    }

    public List getVoterList() {
        return voterList;
    }

    public void setVoterList(List<String> voterList) {
        this.voterList = voterList;
    }
}
