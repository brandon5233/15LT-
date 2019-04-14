package com.dubliners.a15lt;

import java.util.List;

public class Movie {
    private String creator;
    private String creator_uid;
    private String movieName;
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

    public String getMovieName() {
        return movieName;
    }

    public void setMovieName(String movieName) {
        this.movieName = movieName;
    }

    public String getVoteCount() {
        return voteCount;
    }

    public void setVoteCount(String voteCount) {
        this.voteCount = voteCount;
    }

    public List<String> getVoterList() {
        return voterList;
    }

    public void setVoterList(List<String> voterList) {
        this.voterList = voterList;
    }



}
