package com.example.ashwamedh.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ashwamedh.R;
import com.example.ashwamedh.model.Attendance;

import java.util.List;

public class AllAttendanceRecyclerViewAdapter extends RecyclerView.Adapter<AllAttendanceRecyclerViewAdapter.ViewHolder> {
    private List<Attendance> attendanceList;
    private Context context;

    public AllAttendanceRecyclerViewAdapter(List<Attendance> attendanceList, Context context) {
        this.attendanceList = attendanceList;
        this.context = context;
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

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ProgressBar progressBar;
        private TextView nameTextView;
        private TextView percentageTextView;

        public ViewHolder(@NonNull View itemView, Context ctx) {
            super(itemView);
            context = ctx;
            progressBar = itemView.findViewById(R.id.progressBar_attendance);
            nameTextView = itemView.findViewById(R.id.attendance_row_name);
            percentageTextView = itemView.findViewById(R.id.attendance_row_percentage);
        }
    }
}
