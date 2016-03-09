package com.reneph.githubtool.util;

import com.reneph.githubtool.data.RepositoryData;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Robert on 09.03.2016.
 */
public class JSONUtil {
    public static String buildQueryURI(String query){
        return "https://api.github.com/search/repositories?q="+query;
    }

    public static RepositoryData parseJSONToRepository(JSONObject object){
        RepositoryData entry = new RepositoryData();

        try {
            entry.setId(object.getInt("id"));
            entry.setRepositoryName(object.getString("name"));
            entry.setRepositoryDescription(object.getString("description"));
            entry.setForks(object.getInt("forks"));

            JSONObject repoOwner = object.getJSONObject("owner");
            if(repoOwner != null){
                entry.setImageURI(repoOwner.getString("avatar_url"));
            }

            return entry;
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }
}
