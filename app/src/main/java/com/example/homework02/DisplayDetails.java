package com.example.homework02;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class DisplayDetails extends AppCompatActivity {

    TextView track_tv;
    TextView genre_tv;
    TextView album_tv;

    TextView artist_tv;
    TextView trackPrice_tv;
    TextView albumPrice_tv;


    Button finish_btn;
    ImageView song_iv;
    private static final String TAG="demo";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_details);

        setTitle("iTunes Music Search");
        track_tv = findViewById(R.id.track_tv);
        genre_tv = findViewById(R.id.genre_tv);
        album_tv = findViewById(R.id.album_tv);
        artist_tv = findViewById(R.id.artist_tv);
        trackPrice_tv = findViewById(R.id.trackPrice_tv);
        albumPrice_tv = findViewById(R.id.albumPrice_tv);
        finish_btn = findViewById(R.id.finish_btn);
        song_iv = findViewById(R.id.song_iv);

        Intent intent = getIntent();
        Music music = (Music) intent.getSerializableExtra("music_item");

//        String trackName = music.track_name;
//        String genre = music.genre;
//        String album = music.album;
//        String artist = music.artist;
//        Double trackPrice = music.track_price;
//        Double albumPrice = music.album_price;
//        String prevUrl = music.artwork_url;

        Picasso.get().load(music.artwork_url).into(song_iv);
        track_tv.setText("Track: " + music.track_name);
        genre_tv.setText("Genre: " + music.genre);
        album_tv.setText("Album: " + music.album);
        artist_tv.setText("Artist: " + music.artist);
        trackPrice_tv.setText("Track Price: $" + music.track_price);
        albumPrice_tv.setText("Album Price: $" + music.album_price);


        finish_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }
}
