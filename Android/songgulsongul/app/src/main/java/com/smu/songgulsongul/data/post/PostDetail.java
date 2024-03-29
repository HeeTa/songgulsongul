package com.smu.songgulsongul.data.post;

import com.smu.songgulsongul.recycler_item.Comment;
import com.smu.songgulsongul.recycler_item.User;
import com.smu.songgulsongul.recycler_item.HashTag;
import com.smu.songgulsongul.recycler_item.ItemTag;
import com.smu.songgulsongul.recycler_item.Post;

import java.util.List;

public class PostDetail {

    User user;
    Post post;

    List<Comment> comments;
    List<ItemTag> itemTags;
    List<HashTag> hashTags;

    int likeNum;
    int likeOnset;
    int keepOnset;

    public User getUser() {
        return user;
    }

    public Post getPost() {
        return post;
    }


    public int getLikeNum() {
        return likeNum;
    }

    public int getLikeOnset() {
        return likeOnset;
    }

    public int getKeepOnset() {
        return keepOnset;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public List<ItemTag> getItemTags() {
        return itemTags;
    }

    public List<HashTag> getHashTags() {
        return hashTags;
    }


    public void setLikeNum(int likeNum) {
        this.likeNum = likeNum;
    }

    public void setLikeOnset(int likeOnset) {
        this.likeOnset = likeOnset;
    }

    public void setKeepOnset(int keepOnset) {
        this.keepOnset = keepOnset;
    }

}
