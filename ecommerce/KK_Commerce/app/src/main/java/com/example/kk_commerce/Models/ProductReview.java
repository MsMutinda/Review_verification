package com.example.kk_commerce.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ProductReview {
    @SerializedName("id")
    @Expose
    private String id;

    @SerializedName("buyer_name")
    @Expose
    private String buyer_name;

    @SerializedName("verified_purchase")
    @Expose
    private String verified_purchase;

    @SerializedName("comment")
    @Expose
    private String comment;

    @SerializedName("rating")
    @Expose
    private String rating;

    @SerializedName("review_verified")
    @Expose
    private String review_verified;

    public String getReview_verified() {
        return review_verified;
    }

    public void setReview_verified(String review_verified) {
        this.review_verified = review_verified;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBuyer_name() {
        return buyer_name;
    }

    public void setBuyer_name(String buyer_name) {
        this.buyer_name = buyer_name;
    }

    public String getVerified_purchase() {
        return verified_purchase;
    }

    public void setVerified_purchase(String verified_purchase) {
        this.verified_purchase = verified_purchase;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }
}
