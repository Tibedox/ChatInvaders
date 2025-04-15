package ru.tibedox.chatinvaders;

import com.google.gson.annotations.SerializedName;

public class DataFromDB {
    @SerializedName("id")
    int id;

    @SerializedName("name")
    String name;

    @SerializedName("message")
    String message;

    @SerializedName("created")
    String created;
}
