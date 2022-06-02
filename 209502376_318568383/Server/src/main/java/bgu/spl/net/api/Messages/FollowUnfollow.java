package bgu.spl.net.api.Messages;

import bgu.spl.net.api.MessageImpl;

public class FollowUnfollow extends MessageImpl {

    private short followOrUnfollow;
    private final String username;

    public FollowUnfollow(short followOrUnfollow, String username) {
        super((short) 4);
        this.followOrUnfollow = followOrUnfollow;
        this.username = username;
    }

    public void setFollowOrUnfollow(short tmp) { this.followOrUnfollow=tmp;}

    public short getFollowOrUnfollow() { return followOrUnfollow; }

    public String getUsername() { return username; }
}
