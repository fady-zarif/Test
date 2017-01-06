package com.tromke.mydrive.Models;

/**
 * Created by Devrath on 27-09-2016.
 */

public class ItemContentData {

    private String name;
    private String content;
    private String proofname;

    public ItemContentData(String name, String content, String proofname) {
        this.name = name;
        this.content = content;
        this.proofname = proofname;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getProofname() {
        return proofname;
    }

    public void setProofname(String proofname) {
        this.proofname = proofname;
    }
}
