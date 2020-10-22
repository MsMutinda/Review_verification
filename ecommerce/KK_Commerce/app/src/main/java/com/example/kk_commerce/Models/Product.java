package com.example.kk_commerce.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Product {
    @SerializedName("id")
    @Expose
    private String id;


    @SerializedName("name")
    @Expose
    private String name;

    @SerializedName("short_description")
    @Expose
    private String short_description;

    @SerializedName("description")
    @Expose
    private String description;

    @SerializedName("unit_price")
    @Expose
    private String unit_price;

    @SerializedName("stock")
    @Expose
    private String stock;

    @SerializedName("low_stock")
    @Expose
    private String low_stock;

    @SerializedName("category")
    @Expose
    private String category;

    @SerializedName("image1")
    @Expose
    private String image1;

    @SerializedName("image2")
    @Expose
    private String image2;

    @SerializedName("image3")
    @Expose
    private String image3;

    @SerializedName("image4")
    @Expose
    private String image4;


    public String getName() {
        return name;
    }

    public String getShort_description() {
        return short_description;
    }

    public String getDescription() {
        return description;
    }

    public String getUnit_price() {
        return unit_price;
    }

    public String getStock() {
        return stock;
    }

    public String getLow_stock() {
        return low_stock;
    }

    public String getCategory() {
        return category;
    }

    public String getImage1() {
        return image1;
    }

    public String getImage2() {
        return image2;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImage3() {
        return image3;
    }

    public String getImage4() {
        return image4;
    }
}
