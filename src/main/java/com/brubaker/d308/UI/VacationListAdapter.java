package com.brubaker.d308.UI;
import com.brubaker.d308.R;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.brubaker.d308.entities.Vacation;

import java.util.List;

public class VacationListAdapter extends RecyclerView.Adapter<VacationListAdapter.VacationViewHolder> {
    private List<Vacation> vacationList;

    // Field will store the listener that is passed from the VacationList activity
    private OnItemClickListener onItemClickListener;

    // Constructor: Receiving list of vacations to the adapter
    public VacationListAdapter(List<Vacation> vacationList) {
        this.vacationList = vacationList;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    // Creating callback interface
    public interface OnItemClickListener {
        void onItemClick(Vacation vacation);
    }

    // Creating and returning a ViewHolder for a single item
    @NonNull
    @Override
    public VacationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflating the item layout, vacation_item_list.xml
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.vacation_item_list, parent, false);
        return new VacationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VacationViewHolder holder, int position) {
        Vacation vacation = vacationList.get(position); // Get the vacation at this position
        holder.vacationTitle.setText(vacation.getTitle()); // Set the name in the TextView

        // set click listener for entire itemView
        holder.itemView.setOnClickListener(v -> {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(vacation); // notify listener of the click
            }
        });
    }

    @Override
    public int getItemCount() {
        return vacationList.size();
    }

    static class VacationViewHolder extends RecyclerView.ViewHolder {
        TextView vacationTitle;

        public VacationViewHolder(@NonNull View itemView) {
            super(itemView);
            // Find the TextView for the vacation name
            vacationTitle = itemView.findViewById(R.id.vacation_title);
        }
    }

}
