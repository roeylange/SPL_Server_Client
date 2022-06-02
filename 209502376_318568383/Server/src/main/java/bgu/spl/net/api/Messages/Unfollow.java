package bgu.spl.net.api.Messages;

import bgu.spl.net.api.MessageImpl;

public class Unfollow extends MessageImpl {
    private String followerUserName;
    private String followedUserName;

    public Unfollow(String followerUserName, String followingUserName) {
        super((short) 4);
        this.followerUserName = followerUserName;
        this.followedUserName = followingUserName;
    }

    public String getFollowerUserName() {
        return followerUserName;
    }

    public void setFollowerUserName(String followerUserName) {
        this.followerUserName = followerUserName;
    }

    public String getFollowedUserName() {
        return followedUserName;
    }

    public void setFollowedUserName(String followedUserName) {
        this.followedUserName = followedUserName;
    }

}
