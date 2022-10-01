package com.example.ashwamedh.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ashwamedh.R;
import com.example.ashwamedh.model.Jingle;

import java.util.List;

public class JingleRecyclerViewAdapter extends RecyclerView.Adapter<JingleRecyclerViewAdapter.ViewHolder> {
    private Context context;
    private List<Jingle> jingleList;
    private OnJingleClickListener onJingleClickListener;

    public JingleRecyclerViewAdapter(Context context, List<Jingle> jingleList, OnJingleClickListener onJingleClickListener) {
        this.context = context;
        this.jingleList = jingleList;
        this.onJingleClickListener = onJingleClickListener;
    }

    @NonNull
    @Override
    public JingleRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.jingle_row, parent, false);
        return new ViewHolder(view, context);
    }

    @Override
    public void onBindViewHolder(@NonNull JingleRecyclerViewAdapter.ViewHolder holder, int position) {
        Jingle jingle = jingleList.get(position);
        holder.title.setText(jingle.getTitle().toUpperCase());
        holder.scene.setText("Scene: " + jingle.getScene());
        holder.dateAdded.setText("Date added: " + jingle.getDateAdded());
        holder.proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title = jingle.getTitle();
                String milliSec = jingle.getCurrentMilliSeconds();

                onJingleClickListener.onJingleClick(title, milliSec);
            }
        });
    }

    @Override
    public int getItemCount() {
        return jingleList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView title;
        private TextView scene;
        private TextView dateAdded;
        private CardView proceed;

        OnJingleClickListener onJingleClickListener;

        public ViewHolder(@NonNull View itemView, Context ctx) {
            super(itemView);
            context = ctx;

            this.onJingleClickListener = JingleRecyclerViewAdapter.this.onJingleClickListener;

            title = itemView.findViewById(R.id.jingle_row_title);
            scene = itemView.findViewById(R.id.jingle_row_scene);
            dateAdded = itemView.findViewById(R.id.jingle_row_date_added);
            proceed = itemView.findViewById(R.id.jingle_row_proceed_button);
        }
    }
}
