package com.example.fyp;

import android.os.Parcel;
import android.os.Parcelable;

public class Product implements Parcelable {
    // Existing fields
    public String title, price, description, dimensions, imageName, modelUrl;
    public int quantity = 1;

    public Product() { }

    public Product(String title, String price, String description, String dimensions, String imageName, String modelUrl) {
        this.title = title;
        this.price = price;
        this.description = description;
        this.dimensions = dimensions;
        this.imageName = imageName;
        this.modelUrl = modelUrl;
    }

    protected Product(Parcel in) {
        title = in.readString();
        price = in.readString();
        description = in.readString();
        dimensions = in.readString();
        imageName = in.readString();
        modelUrl = in.readString();
        quantity = in.readInt();
    }

    public static final Creator<Product> CREATOR = new Creator<Product>() {
        @Override
        public Product createFromParcel(Parcel in) {
            return new Product(in);
        }

        @Override
        public Product[] newArray(int size) {
            return new Product[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(price);
        dest.writeString(description);
        dest.writeString(dimensions);
        dest.writeString(imageName);
        dest.writeString(modelUrl);
        dest.writeInt(quantity);
    }
}
