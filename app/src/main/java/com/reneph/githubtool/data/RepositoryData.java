package com.reneph.githubtool.data;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Robert on 09.03.2016.
 */
public class RepositoryData {
    private int id = -1;
    private String repositoryName = "";
    private String repositoryDescription = "";
    private String subscribersURL = "";
    private int forks = 0;
    private int subscribers = 0;
    private UserData owner = null;
    private final List<UserData> subscriberList = new ArrayList<>();

    public RepositoryData(JSONObject object) throws JSONException{
        subscriberList.clear();

        try {
            setId(object.getInt("id"));
            setRepositoryName(object.getString("name"));
            setRepositoryDescription(object.getString("description"));
            setSubscribersURL(object.getString("subscribers_url"));
            setForks(object.getInt("forks"));

            if(object.has("subscribers_count")) {
                setSubscribers(object.getInt("subscribers_count"));
            }

            JSONObject repoOwner = object.getJSONObject("owner");
            owner = new UserData(repoOwner);
        } catch (JSONException e) {
            e.printStackTrace();
            setId(-1);
            throw e;
        }
    }

    public void parseSubscribersList(JSONArray objects) {
        subscriberList.clear();

        for (int i = 0; i < objects.length(); i++) {
            try {
                UserData subscriber = new UserData(objects.getJSONObject(i));

                if (subscriber.getId() > -1) {
                    subscriberList.add(subscriber);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getRepositoryName() {
        return repositoryName;
    }

    public void setRepositoryName(String repositoryName) {
        this.repositoryName = repositoryName;
    }

    public String getRepositoryDescription() {
        return repositoryDescription;
    }

    public void setRepositoryDescription(String repositoryDescription) {
        this.repositoryDescription = repositoryDescription;
    }

    public String getSubscribersURL() {
        return subscribersURL;
    }

    public void setSubscribersURL(String subscribersURL) {
        this.subscribersURL = subscribersURL;
    }

    public UserData getOwner() {
        return owner;
    }

    public List<UserData> getSubscriberList() {
        return subscriberList;
    }

    public int getForks() {
        return forks;
    }

    public void setForks(int forks) {
        this.forks = forks;
    }

    public int getSubscribers() {
        return subscribers;
    }

    public void setSubscribers(int subscribers) {
        this.subscribers = subscribers;
    }
}
