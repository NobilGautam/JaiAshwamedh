package com.example.ashwamedh.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ashwamedh.R;
import com.example.ashwamedh.model.Attendance;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Objects;

public class AllAttendanceRecyclerViewAdapter extends RecyclerView.Adapter<AllAttendanceRecyclerViewAdapter.ViewHolder> {
    private OnAttendanceClickListener onAttendanceClickListener;
    private List<Attendance> attendanceList;
    private Context context;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference profilePictureCollection = db.collection("Profile Pictures");

    public AllAttendanceRecyclerViewAdapter(List<Attendance> attendanceList, Context context, OnAttendanceClickListener onAttendanceClickListener) {
        this.attendanceList = attendanceList;
        this.context = context;
        this.onAttendanceClickListener = onAttendanceClickListener;
    }

    @NonNull
    @Override
    public AllAttendanceRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.attendance_row, parent,false);

        return new ViewHolder(view, context);
    }

    @Override
    public void onBindViewHolder(@NonNull AllAttendanceRecyclerViewAdapter.ViewHolder holder, int position) {
        Attendance attendance = attendanceList.get(position);
        holder.nameTextView.setText(attendance.getUsername());
        holder.nameTextViewTop.setText(attendance.getUsername());
        int daysPresent = attendance.getDaysPresent();
        int totalDays = attendance.getTotalDays();
        int percentage = (daysPresent*100)/totalDays;
        holder.percentageTextView.setText(percentage+"%");
        holder.progressBar.setProgress(percentage);

    }

    @Override
    public int getItemCount() {
        return attendanceList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private ProgressBar progressBar;
        private TextView nameTextView;
        private TextView nameTextViewTop;
        private TextView percentageTextView;
        private ImageView profilePicture;

        OnAttendanceClickListener onAttendanceClickListener;

        public ViewHolder(@NonNull View itemView, Context ctx) {
            super(itemView);

            this.onAttendanceClickListener = AllAttendanceRecyclerViewAdapter.this.onAttendanceClickListener;

            context = ctx;
            progressBar = itemView.findViewById(R.id.progressBar_attendance);
            nameTextView = itemView.findViewById(R.id.attendance_row_name);
            nameTextViewTop = itemView.findViewById(R.id.attendance_row_name_top);
            percentageTextView = itemView.findViewById(R.id.attendance_row_percentage);
            profilePicture = itemView.findViewById(R.id.profile_img_holder_AR);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int id = view.getId();
            switch (id) {
                case R.id.individual_attendance_row:
                    Attendance attendance = attendanceList.get(getAdapterPosition());
                    String userId = attendance.getUserId();
                    onAttendanceClickListener.OnAttendanceClick(userId, "");
            }
        }
    }
}
