package com.reneph.githubtool.util;

/**
 * Created by Robert on 09.03.2016.
 */
public class JSONUtil {
    public static String buildSearchQueryURI(String query){
        return "https://api.github.com/search/repositories?q="+query;
    }

    public static String buildRepositoryQueryURI(int repositoryId){
        return "https://api.github.com/repositories/"+repositoryId;
    }
}
