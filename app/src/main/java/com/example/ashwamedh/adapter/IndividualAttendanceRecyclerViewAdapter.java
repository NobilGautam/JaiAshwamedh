package com.example.ashwamedh.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ashwamedh.R;
import com.example.ashwamedh.model.Confirmation;

import java.util.List;
import java.util.Objects;

public class IndividualAttendanceRecyclerViewAdapter extends RecyclerView.Adapter<IndividualAttendanceRecyclerViewAdapter.ViewHolder> {
    private List<Confirmation> confirmationList;
    private Context context;

    public IndividualAttendanceRecyclerViewAdapter(List<Confirmation> confirmationList, Context context) {
        this.confirmationList = confirmationList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.individual_attendance_row, parent, false);
        return new ViewHolder(view, context);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Confirmation confirmation = confirmationList.get(position);
        holder.reasonOrRemarks.setText(confirmation.getRemarkOrReason());
        holder.dateTextView.setText(confirmation.getPracticeDate());
        if (Objects.equals(confirmation.getConfirmation(), "present")) {
            holder.confirmation.setImageResource(R.drawable.ic_baseline_check_24);
        } else if (Objects.equals(confirmation.getConfirmation(), "absent")){
            holder.confirmation.setImageResource(R.drawable.ic_baseline_close_24);
        }
    }

    @Override
    public int getItemCount() {
        return confirmationList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView dateTextView;
        private ImageView confirmation;
        private TextView reasonOrRemarks;

        public ViewHolder(@NonNull View itemView, Context ctx) {
            super(itemView);
            context = ctx;
            dateTextView = itemView.findViewById(R.id.indivAttendance_date_textview);
            confirmation = itemView.findViewById(R.id.indivAttendance_confirmation);
            reasonOrRemarks = itemView.findViewById(R.id.indivAttendance_remark_reason_textview);
        }
    }
}
