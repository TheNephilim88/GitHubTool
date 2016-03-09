package com.reneph.githubtool.data;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Robert on 09.03.2016.
 */
public class UserData {
    private int id = -1;
    private String avatar_url = "";
    private String name = "";

    public UserData(JSONObject object){
        try {
            setId(object.getInt("id"));
            setName(object.getString("login"));
            setAvatarUrl(object.getString("avatar_url"));
        } catch (JSONException e) {
            e.printStackTrace();
            setId(-1);
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAvatarUrl() {
        return avatar_url;
    }

    public void setAvatarUrl(String avatar_url) {
        this.avatar_url = avatar_url;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
