package com.example.m_hike.model;

public class Photo {

    private int id;
    private int observationId;
    private String photoPath;
    private String createdAt;
    private String deletedAt;

    public Photo() {}
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public int getObservationId() {
        return observationId;
    }
    public void setObservationId(int observationId) {
        this.observationId = observationId;
    }
    public String getPhotoPath() {
        return photoPath;
    }
    public void setPhotoPath(String photoPath) {
        this.photoPath = photoPath;
    }
    public String getCreatedAt() {
        return createdAt;
    }
    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
    public String getDeletedAt() {
        return deletedAt;
    }
    public void setDeletedAt(String deletedAt) {
        this.deletedAt = deletedAt;
    }
}