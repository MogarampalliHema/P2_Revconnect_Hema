package com.rev.app.dto;

public class NotificationPreferenceDTO {

    private boolean connectionRequests = true;
    private boolean connectionAccepted = true;
    private boolean newFollowers = true;
    private boolean postLikes = true;
    private boolean postComments = true;
    private boolean postShares = true;

    // Getters and Setters
    public boolean isConnectionRequests() {
        return connectionRequests;
    }

    public void setConnectionRequests(boolean connectionRequests) {
        this.connectionRequests = connectionRequests;
    }

    public boolean isConnectionAccepted() {
        return connectionAccepted;
    }

    public void setConnectionAccepted(boolean connectionAccepted) {
        this.connectionAccepted = connectionAccepted;
    }

    public boolean isNewFollowers() {
        return newFollowers;
    }

    public void setNewFollowers(boolean newFollowers) {
        this.newFollowers = newFollowers;
    }

    public boolean isPostLikes() {
        return postLikes;
    }

    public void setPostLikes(boolean postLikes) {
        this.postLikes = postLikes;
    }

    public boolean isPostComments() {
        return postComments;
    }

    public void setPostComments(boolean postComments) {
        this.postComments = postComments;
    }

    public boolean isPostShares() {
        return postShares;
    }

    public void setPostShares(boolean postShares) {
        this.postShares = postShares;
    }
}
