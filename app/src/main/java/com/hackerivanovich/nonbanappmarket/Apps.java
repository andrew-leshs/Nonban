package com.hackerivanovich.nonbanappmarket;

public class Apps {
    String pack;
    String id;

    public String getId() {
        return id;
    }
    String logo;
    String name;
    String company;
    String link;
    String likes;
    String dislikes;
    String preview;
    String down;

    public String getUid() {
        return uid;
    }

    String uid;

    public String getPack() {
        return pack;
    }

    public String getDown() {
        return down;
    }

    public String getLogo() {
        return logo;
    }

    public String getName() {
        return name;
    }

    public String getCompany() {
        return company;
    }

    public String getLink() {
        return link;
    }

    public String getLikes() {
        return likes;
    }

    public String getDislikes() {
        return dislikes;
    }
    public String getPreview() {
        return preview;
    }

    public Apps() {

    }

    public Apps(String pack, String id, String logo,
                String name, String company, String link,
                String likes, String dislikes, String preview, String down, String uid) {
        this.pack = pack;
        this.id = id;
        this.logo = logo;
        this.name = name;
        this.company = company;
        this.link = link;
        this.likes = likes;
        this.dislikes = dislikes;
        this.preview = preview;
        this.down = down;
        this.uid = uid;
    }
}
