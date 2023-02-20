package com.example.inzynierka;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import java.util.ArrayList;


public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.MyViewHolder> {
    Context context;
    ArrayList<ScoreHistory> list;


    public HistoryAdapter(Context context, ArrayList<ScoreHistory> list){
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.history_layout, parent, false);

        return new MyViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        ScoreHistory scoreHistory = list.get(position);
        holder.data.setText(scoreHistory.getData());
        holder.score.setText(scoreHistory.getScore() + " points");





            holder.view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(holder.data.getContext(),DetailsActivity.class);
                    intent.putExtra("udata",scoreHistory.getData());
                    intent.putExtra("uscore",scoreHistory.getScore());
                    intent.putExtra("uwindSpeed",scoreHistory.getWindSpeed() + "M/s");
                    intent.putExtra("ugun",scoreHistory.getGun());
                    intent.putExtra("uimage", scoreHistory.getImageUrl());

                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    holder.data.getContext().startActivity(intent);


                }
            });

    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView data, score, windSpeed;
        View view;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            data = itemView.findViewById(R.id.TextDate);
            score = itemView.findViewById(R.id.TextScore);
            windSpeed = itemView.findViewById(R.id.WindText);
            view = itemView;

        }
    }
}

