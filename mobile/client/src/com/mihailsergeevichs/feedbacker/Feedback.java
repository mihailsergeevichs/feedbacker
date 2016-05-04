package com.mihailsergeevichs.feedbacker;

import java.util.Date;

public class Feedback {

    private String id;

    private Date date;

    private String quality;

    private String speed;

    private String price;

    private String commentary;

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getQuality() {
        return quality;
    }

    public void setQuality(String quality) {
        this.quality = quality;
    }

    public String getSpeed() {
        return speed;
    }

    public void setSpeed(String speed) {
        this.speed = speed;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getCommentary() {
        return commentary;
    }

    public void setCommentary(String commentary) {
        this.commentary = commentary;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Feedback)) return false;

        Feedback feedback = (Feedback) o;

        if (!getDate().equals(feedback.getDate())) return false;
        if (!getQuality().equals(feedback.getQuality())) return false;
        if (!getSpeed().equals(feedback.getSpeed())) return false;
        if (!getPrice().equals(feedback.getPrice())) return false;
        return getCommentary().equals(feedback.getCommentary());

    }

    @Override
    public int hashCode() {
        int result = getDate().hashCode();
        result = 31 * result + getQuality().hashCode();
        result = 31 * result + getSpeed().hashCode();
        result = 31 * result + getPrice().hashCode();
        result = 31 * result + getCommentary().hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "Feedback{" +
                "date=" + date +
                ", quality='" + quality + '\'' +
                ", speed='" + speed + '\'' +
                ", price='" + price + '\'' +
                ", commentary='" + commentary + '\'' +
                '}';
    }
}
