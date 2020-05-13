package com.example.homework02;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class MusicAdapter extends RecyclerView.Adapter<MusicAdapter.ViewHolder> {

    private ArrayList<Music> mData = new ArrayList<>();
    private OnItemListener mOnItemListener;

    public MusicAdapter(ArrayList<Music> mData, OnItemListener mOnItemListener) {
        this.mData = mData;
        this.mOnItemListener = mOnItemListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.music_item, parent, false);
        return new ViewHolder(view, mOnItemListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Music music = mData.get(position);

        holder.artist_tv.setText(music.artist);
        holder.track_tv.setText(music.track_name);
        holder.price_tv.setText("$" + Double.toString(music.track_price));
        holder.date_tv.setText(new SimpleDateFormat("MM-dd-yyyy", Locale.US).format(music.release_date));
        holder.music = music;
    }

    @Override
    public int getItemCount() {
        return this.mData.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView artist_tv, track_tv, price_tv, date_tv;
        Music music;
        OnItemListener onItemListener;

        public ViewHolder(@NonNull final View itemView, OnItemListener onItemListener) {
            super(itemView);
            artist_tv = itemView.findViewById(R.id.artist_tv);
            track_tv = itemView.findViewById(R.id.track_tv);
            price_tv = itemView.findViewById(R.id.price_tv);
            date_tv = itemView.findViewById(R.id.date_tv);
            this.onItemListener = onItemListener;

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onItemListener.onItemClick(getAdapterPosition());
        }
    }
}
