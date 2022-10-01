package com.example.ashwamedh.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ashwamedh.R;
import com.example.ashwamedh.model.Script;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ScriptRecyclerViewAdapter extends RecyclerView.Adapter<ScriptRecyclerViewAdapter.ViewHolder> {
    private List<Script> scriptList;
    private Context context;
    private OnScriptClickListener onScriptClickListener;

    public ScriptRecyclerViewAdapter(List<Script> scriptList, Context context, OnScriptClickListener onScriptClickListener) {
        this.scriptList = scriptList;
        this.context = context;
        this.onScriptClickListener = onScriptClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.script_row, parent, false);
        return new ViewHolder(view, context);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Script script = scriptList.get(position);
        holder.scriptTitle.setText(script.getTitle());
        holder.scriptScene.setText("Scene(s): " + script.getScenes());
        holder.dateAdded.setText("Date added: " + script.getTimeAdded());
        holder.downloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String miliseconds = script.getCurrentTimeMiliSec();
                String title = script.getTitle();
                onScriptClickListener.onScriptClick(title, miliseconds);
            }
        });
    }

    @Override
    public int getItemCount() {
        return scriptList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView scriptTitle;
        private TextView scriptScene;
        private TextView dateAdded;
        private CardView downloadButton;

        OnScriptClickListener onScriptClickListener;
        public ViewHolder(@NonNull View itemView, Context ctx) {
            super(itemView);
            context = ctx;

            this.onScriptClickListener = ScriptRecyclerViewAdapter.this.onScriptClickListener;

            scriptTitle = itemView.findViewById(R.id.script_row_title);
            scriptScene = itemView.findViewById(R.id.script_row_scene);
            dateAdded = itemView.findViewById(R.id.script_row_date_added);
            downloadButton = itemView.findViewById(R.id.script_row_download_button);
        }
    }
}
