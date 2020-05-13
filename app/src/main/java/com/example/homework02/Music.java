package com.example.homework02;

import androidx.annotation.Nullable;

import java.io.Serializable;
import java.util.Date;

public class Music implements Serializable {
    String track_name;
    String genre;
    String artist;
    String album;
    String artwork_url;
    double track_price;
    double album_price;
    Date release_date;


    public Music(@Nullable String track_name, @Nullable String genre, @Nullable String artist, @Nullable String album, @Nullable String artwork_url, @Nullable double track_price, @Nullable double album_price, @Nullable Date release_date) {
        this.track_name = track_name;
        this.genre = genre;
        this.artist = artist;
        this.album = album;
        this.artwork_url = artwork_url;
        this.track_price = track_price;
        this.album_price = album_price;
        this.release_date = release_date;
    }

    public Date getRelease_date() {
        return release_date;
    }

    public double getTrack_price() {
        return track_price;
    }

    @Override
    public String toString() {
        return "Music{" +
                "track_name='" + track_name + '\'' +
                ", genre='" + genre + '\'' +
                ", artist='" + artist + '\'' +
                ", album='" + album + '\'' +
                ", track_price=" + track_price +
                ", album_price=" + album_price +
                '}';
    }

}
