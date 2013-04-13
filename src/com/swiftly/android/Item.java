package com.swiftly.android;

public class Item {
    public String name;
    public String barcode;
    public Integer wid;
    public String webImgPath;
    public String localImgPath;
    public Float price;
    public Integer cartQuantity;

    public Item(String name, String barcode, Integer wid, String webImgPath,
                String localImgPath, Float price, Integer cartQuantity) {
        this.wid = wid;
        this.name = name;
        this.barcode = barcode;
        this.webImgPath = webImgPath;
        this.localImgPath = localImgPath;
        this.price = price;
        this.cartQuantity = cartQuantity;
    }
}