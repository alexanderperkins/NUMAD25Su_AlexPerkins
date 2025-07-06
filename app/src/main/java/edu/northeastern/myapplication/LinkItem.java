package edu.northeastern.myapplication;

import android.os.Parcel;
import android.os.Parcelable;

public class LinkItem implements Parcelable {
    private String name;
    private String url;

    // Constructor
    public LinkItem(String name, String url) {
        this.name = name;
        this.url = url;
    }

    // Getters
    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }

    // Setters (needed for editing)
    public void setName(String name) {
        this.name = name;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    // Implement Parcelable

    protected LinkItem(Parcel in) {
        name = in.readString();
        url = in.readString();
    }

    public static final Creator<LinkItem> CREATOR = new Creator<LinkItem>() {
        @Override
        public LinkItem createFromParcel(Parcel in) {
            return new LinkItem(in);
        }

        @Override
        public LinkItem[] newArray(int size) {
            return new LinkItem[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(url);
    }
}