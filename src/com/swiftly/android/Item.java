package com.swiftly.android;

import org.json.JSONObject;

public class Item {
    public String name;
    public Integer barcode;
    public Integer wid;
    public String webImgPath;
    public String localImgPath;
    public Float price;
    public boolean isInCart;

    public Item(String name, Integer barcode, Integer wid, String webImgPath,
                String localImgPath, Float price, Boolean isInCart) {
        this.wid = wid;
        this.name = name;
        this.barcode = barcode;
        this.webImgPath = webImgPath;
        this.localImgPath = localImgPath;
        this.price = price;
        this.isInCart = isInCart;
    }
}
