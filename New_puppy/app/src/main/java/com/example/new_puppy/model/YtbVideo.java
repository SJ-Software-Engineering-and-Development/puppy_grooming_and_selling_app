package com.example.new_puppy.model;

public class YtbVideo {
    private int id;
    private String url;
    private String title;
    private String published_time;
    private String duration;
    private int views;
    private int likes;
    private int comments;

    public YtbVideo() {
    }

    public YtbVideo(int id, String url, String title, String published_time, String duration, int views, int likes, int comments) {
        this.id = id;
        this.url = url;
        this.title = title;
        this.published_time = published_time;
        this.duration = duration;
        this.views = views;
        this.likes = likes;
        this.comments = comments;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPublished_time() {
        return published_time;
    }

    public void setPublished_time(String published_time) {
        this.published_time = published_time;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public int getViews() {
        return views;
    }

    public void setViews(int views) {
        this.views = views;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public int getComments() {
        return comments;
    }

    public void setComments(int comments) {
        this.comments = comments;
    }

    @Override
    public String toString() {
        return "YtbVideo{" +
                "id=" + id +
                ", url='" + url + '\'' +
                ", title='" + title + '\'' +
                ", published_time='" + published_time + '\'' +
                ", duration='" + duration + '\'' +
                ", views='" + views + '\'' +
                ", likes=" + likes +
                ", comments=" + comments +
                '}';
    }
}
