
package com.example.wallpapers.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Comments {

    @SerializedName("page")
    @Expose
    private Integer page;
    @SerializedName("perpage")
    @Expose
    private Integer perpage;
    @SerializedName("total")
    @Expose
    private String total;
    @SerializedName("pages")
    @Expose
    private Integer pages;
    @SerializedName("photo_id")
    @Expose
    private String photoId;
    @SerializedName("comment")
    @Expose
    private List<Comment_> comment = null;

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getPerpage() {
        return perpage;
    }

    public void setPerpage(Integer perpage) {
        this.perpage = perpage;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public Integer getPages() {
        return pages;
    }

    public void setPages(Integer pages) {
        this.pages = pages;
    }

    public String getPhotoId() {
        return photoId;
    }

    public void setPhotoId(String photoId) {
        this.photoId = photoId;
    }

    public List<Comment_> getComment() {
        return comment;
    }

    public void setComment(List<Comment_> comment) {
        this.comment = comment;
    }

}
