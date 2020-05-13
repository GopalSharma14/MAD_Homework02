package com.example.homework02;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.method.ArrowKeyMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.channels.MulticastChannel;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;
import java.util.stream.Collectors;

public class MainActivity extends AppCompatActivity implements OnItemListener {
    private static final String TAG = "demo";

    private EditText artist_name_et;

    private TextView progress_tv;
    private TextView loading_tv;

    private Switch filter_switch;

    private ProgressBar progressbar;

    private SeekBar seekbar;

    private Button search_button;
    private Button reset_button;

    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;

    ArrayList<Music> data = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("iTunes Music Search");

        artist_name_et = findViewById(R.id.artist_name_et);

        progress_tv = findViewById(R.id.progress_tv);
        loading_tv = findViewById(R.id.loading_tv);
        loading_tv.setVisibility(View.INVISIBLE);

        filter_switch = findViewById(R.id.filter_switch);

        search_button = findViewById(R.id.search_button);
        reset_button = findViewById(R.id.reset_button);

        progressbar = findViewById(R.id.progressbar);
        progressbar.setVisibility(View.INVISIBLE);

        seekbar = findViewById(R.id.seekbar);
        progress_tv.setText(Integer.toString(seekbar.getProgress()));

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        mAdapter = new MusicAdapter(data, this);
        recyclerView.setAdapter(mAdapter);


        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progress_tv.setText("" + progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        search_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!artist_name_et.getText().toString().equals("")) {
                    new GetArtists(artist_name_et.getText().toString(), seekbar.getProgress()).execute();
                } else {
                    Toast.makeText(MainActivity.this, "Please enter a search term", Toast.LENGTH_SHORT).show();
                }
            }
        });


        reset_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                artist_name_et.setText(null);
                data.clear();
                mAdapter = new MusicAdapter(data, MainActivity.this);
                recyclerView.setAdapter(mAdapter);
                seekbar.setProgress(seekbar.getMin());
            }
        });

        filter_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!data.isEmpty()) {
                    if (filter_switch.isChecked()) {
                        Collections.sort(data, new Comparator<Music>() {
                            @Override
                            public int compare(Music o1, Music o2) {
                                return o1.getRelease_date().compareTo(o2.getRelease_date());
                            }
                        });
                        mAdapter = new MusicAdapter(data, MainActivity.this);
                        recyclerView.setAdapter(mAdapter);
                    }
                    else {
                        Collections.sort(data, new Comparator<Music>() {
                            @Override
                            public int compare(Music o1, Music o2) {
                                return Double.compare(o1.getTrack_price(), o2.getTrack_price());
                            }
                        });
                        mAdapter = new MusicAdapter(data, MainActivity.this);
                        recyclerView.setAdapter(mAdapter);
                    }
                }
            }
        });

        findViewById(R.id.textView4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filter_switch.setChecked(false);
            }
        });

        findViewById(R.id.textView5).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filter_switch.setChecked(true);
            }
        });

    }

    @Override
    public void onItemClick(int pos) {
        Log.d("demo", "clicked" + data.get(pos));
        Intent intent = new Intent(MainActivity.this, DisplayDetails.class);
        intent.putExtra("music_item", data.get(pos));
        startActivity(intent);
    }


    class GetArtists extends AsyncTask<String, Void, ArrayList<Music>> {
        String search_term;
        int limit;

        public GetArtists(String search_term, int limit) {
            this.search_term = search_term;
            this.limit = limit;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            recyclerView.setAdapter(new MusicAdapter(new ArrayList<Music>(), MainActivity.this));
            loading_tv.setVisibility(View.VISIBLE);
            progressbar.setVisibility(View.VISIBLE);
        }

        @Override
        protected ArrayList<Music> doInBackground(String... strings) {
            HttpURLConnection con = null;
            ArrayList<Music> music = new ArrayList<>();
            SimpleDateFormat newFormat = new SimpleDateFormat("yyyy-MM-DD", Locale.US);

            try {
                URL url = new URL("https://itunes.apple.com/search?term=" + search_term
                        + "&limit=" + limit);
                con = (HttpURLConnection) url.openConnection();
                con.connect();

                if (con.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    String json = IOUtils.toString(con.getInputStream(), "UTF_8.");
                    JSONObject root = new JSONObject(json);
                    JSONArray articles = root.getJSONArray("results");
                    for (int i = 0; i < limit && i < articles.length(); i++) {
                        JSONObject articleJSON = articles.getJSONObject(i);

                        String date = articleJSON.getString("releaseDate");
                        Log.d("demo", articleJSON.getString("collectionName") + " " + articleJSON.getString("artistName"));
                        Music track = new Music(articleJSON.getString("trackName"),
                                articleJSON.getString("primaryGenreName"),
                                articleJSON.getString("artistName"),
                                articleJSON.getString("collectionName"),
                                articleJSON.getString("artworkUrl100"),
                                articleJSON.getDouble("trackPrice"),
                                articleJSON.getDouble("collectionPrice"),
                                newFormat.parse(date.split("T")[0].toString()));

                        music.add(track);
                    }
                }
            } catch (UnsupportedEncodingException ex) {
                ex.printStackTrace();
            } catch (IOException ex) {
                ex.printStackTrace();
            } catch (JSONException ex) {
                ex.printStackTrace();
            } catch (ParseException e) {
                e.printStackTrace();
            } finally {
                con.disconnect();
            }

            return music;
        }

        @Override
        protected void onPostExecute(ArrayList<Music> music) {
            super.onPostExecute(music);
            Log.d(TAG, "" + music);
            Log.d(TAG, "" + music.size());

            data = music;
            Collections.sort(data, new Comparator<Music>() {
                @Override
                public int compare(Music o1, Music o2) {
                    return o1.getRelease_date().compareTo(o2.getRelease_date());
                }
            });
            mAdapter = new MusicAdapter(data, MainActivity.this);
            recyclerView.setAdapter(mAdapter);

            mAdapter = new MusicAdapter(data, MainActivity.this);
            recyclerView.setAdapter(mAdapter);
            loading_tv.setVisibility(TextView.INVISIBLE);
            progressbar.setVisibility(ProgressBar.INVISIBLE);
        }
    }
}
