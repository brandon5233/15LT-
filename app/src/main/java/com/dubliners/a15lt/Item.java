package com.dubliners.a15lt;

public class Item {
    private String creator;
    private String creator_uid;
    private String itemName;
    private boolean bought;

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

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public boolean isBought() {
        return bought;
    }

    public void setBought(boolean bought) {
        this.bought = bought;
    }


}
