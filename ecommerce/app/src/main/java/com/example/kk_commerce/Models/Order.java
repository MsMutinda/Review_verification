package com.example.kk_commerce.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Order {
    @SerializedName("id")
    @Expose
    private String id;

    @SerializedName("buyer")
    @Expose
    private String buyer;

    @SerializedName("ordered")
    @Expose
    private String ordered;

    @SerializedName("ordered_date")
    @Expose
    private String ordered_date;

    @SerializedName("delivered")
    @Expose
    private String delivered;

    @SerializedName("billing_address")
    @Expose
    private String billing_address;

    @SerializedName("payment")
    @Expose
    private String payment;

    @SerializedName("unique_ref")
    @Expose
    private String unique_ref;

    @SerializedName("product_count")
    @Expose
    private String product_count;

    @SerializedName("total_amount")
    @Expose
    private String total_amount;

    @SerializedName("review_status")
    @Expose
    private String review_status;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBuyer() {
        return buyer;
    }

    public void setBuyer(String buyer) {
        this.buyer = buyer;
    }

    public String getOrdered() {
        return ordered;
    }

    public void setOrdered(String ordered) {
        this.ordered = ordered;
    }

    public String getDelivered() {
        return delivered;
    }

    public void setDelivered(String delivered) {
        this.delivered = delivered;
    }

    public String getBilling_address() {
        return billing_address;
    }

    public void setBilling_address(String billing_address) {
        this.billing_address = billing_address;
    }

    public String getPayment() {
        return payment;
    }

    public void setPayment(String payment) {
        this.payment = payment;
    }

    public String getUnique_ref() {
        return unique_ref;
    }

    public void setUnique_ref(String unique_ref) {
        this.unique_ref = unique_ref;
    }

    public String getProduct_count() {
        return product_count;
    }

    public void setProduct_count(String product_count) {
        this.product_count = product_count;
    }

    public String getTotal_amount() {
        return total_amount;
    }

    public void setTotal_amount(String total_amount) {
        this.total_amount = total_amount;
    }

    public String getReview_status() {
        return review_status;
    }

    public void setReview_status(String review_status) {
        this.review_status = review_status;
    }

    public String getOrdered_date() {
        return ordered_date;
    }

    public void setOrdered_date(String ordered_date) {
        this.ordered_date = ordered_date;
    }
}
