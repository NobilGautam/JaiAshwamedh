package com.example.ashwamedh.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ashwamedh.R;
import com.example.ashwamedh.model.AdminConfirmation;
import com.example.ashwamedh.model.Attendance;
import com.example.ashwamedh.model.Confirmation;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;
import java.util.Objects;

public class ManageAttendanceRecyclerViewAdapter extends RecyclerView.Adapter<ManageAttendanceRecyclerViewAdapter.ViewHolder> {
    private List<Confirmation> practiceList;
    private Context context;
    private OnAttendanceClickListener onAttendanceClickListener;

    private CollectionReference collectionReference = FirebaseFirestore.getInstance().collection("Users");

    public ManageAttendanceRecyclerViewAdapter(List<Confirmation> practiceList, Context context, OnAttendanceClickListener onAttendanceClickListener) {
        this.practiceList = practiceList;
        this.context = context;
        this.onAttendanceClickListener = onAttendanceClickListener;
    }

    @NonNull
    @Override
    public ManageAttendanceRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.manage_attendance_row, parent, false);
        return new ViewHolder(view, context);
    }

    @Override
    public void onBindViewHolder(@NonNull ManageAttendanceRecyclerViewAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Confirmation confirmation = practiceList.get(position);
        holder.nameTextView.setText(confirmation.getUsername());
        if (confirmation.getRemarkOrReason() == "") {
            holder.remarkOrReasonTextView.setText("null");
        }else {
            holder.remarkOrReasonTextView.setText(confirmation.getRemarkOrReason());
        }
        if (Objects.equals(confirmation.getConfirmation(), "present")) {
            holder.confirmation.setImageResource(R.drawable.ic_baseline_check_24);
        } else if (Objects.equals(confirmation.getConfirmation(), "absent")){
            holder.confirmation.setImageResource(R.drawable.ic_baseline_close_24);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Confirmation confirmation = practiceList.get(position);
                String userId = confirmation.getUserId();
                String username = confirmation.getUsername();
                onAttendanceClickListener.OnAttendanceClick(userId, username);
            }
        });
    }

    @Override
    public int getItemCount() {
        return practiceList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView nameTextView;
        private ImageView confirmation;
        private TextView remarkOrReasonTextView;

        OnAttendanceClickListener onAttendanceClickListener;

        public ViewHolder(@NonNull View itemView, Context ctx) {
            super(itemView);

            this.onAttendanceClickListener = ManageAttendanceRecyclerViewAdapter.this.onAttendanceClickListener;

            context = ctx;
            nameTextView = itemView.findViewById(R.id.manage_attendance_name_textview);
            confirmation = itemView.findViewById(R.id.manage_attendance_confirmation);
            remarkOrReasonTextView = itemView.findViewById(R.id.manage_attendance_remark_reason_textview);
        }

        @Override
        public void onClick(View view) {
            int id = view.getId();
            switch (id){
                case R.id.manage_attendance_row:
                Log.d("from recycler view", "onClick: " + "row clicked");
                Confirmation confirmation = practiceList.get(getAdapterPosition());
                String userId = confirmation.getUserId();
                String username = confirmation.getUsername();
                onAttendanceClickListener.OnAttendanceClick(userId, username);
            }
        }
    }
}
