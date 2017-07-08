package com.example.chirag.exoplayer.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Song {

@SerializedName("id")
@Expose
private int id;
@SerializedName("song_path")
@Expose
private String songPath;
@SerializedName("images")
@Expose
private String images;
@SerializedName("created_at")
@Expose
private String createdAt;
@SerializedName("updated_at")
@Expose
private String updatedAt;

public int getId() {
return id;
}

public void setId(int id) {
this.id = id;
}

public String getSongPath() {
return songPath;
}

public void setSongPath(String songPath) {
this.songPath = songPath;
}

public String getImages() {
return images;
}

public void setImages(String images) {
this.images = images;
}

public String getCreatedAt() {
return createdAt;
}

public void setCreatedAt(String createdAt) {
this.createdAt = createdAt;
}

public String getUpdatedAt() {
return updatedAt;
}

public void setUpdatedAt(String updatedAt) {
this.updatedAt = updatedAt;
}

}