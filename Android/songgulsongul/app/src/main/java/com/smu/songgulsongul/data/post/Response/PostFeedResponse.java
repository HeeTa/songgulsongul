package com.smu.songgulsongul.data.post.Response;

import com.google.gson.annotations.SerializedName;
import com.smu.songgulsongul.recycler_item.PostFeed;

import java.util.List;

public class PostFeedResponse {

    @SerializedName("code")
    private int code;

    @SerializedName("data")
    private List<PostFeed> data;

    public void addData(List<PostFeed> p) {
        for (PostFeed postFeed : p)
            data.add(postFeed);
    }

    public int getCode() {
        return code;
    }

    public List<PostFeed> getData() {
        return data;
    }
}
