package com.brubaker.d308.UI;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.brubaker.d308.R;
import com.brubaker.d308.entities.Excursion;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class ExcursionListAdapter extends RecyclerView.Adapter<ExcursionListAdapter.ExcursionViewHolder> {

    private List<Excursion> excursionList;

    public ExcursionListAdapter(List<Excursion> excursionList) {
        this.excursionList = excursionList;
    }

    @NonNull
    @Override
    public ExcursionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.excursion_item, parent, false);
        return new ExcursionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExcursionViewHolder holder, int position) {
        Excursion excursion = excursionList.get(position);
        holder.excursionTitle.setText(excursion.getTitle());
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        holder.excursionDate.setText(dateFormat.format(excursion.getDate()));
    }

    @Override
    public int getItemCount() {
        return excursionList.size();
    }

    static class ExcursionViewHolder extends RecyclerView.ViewHolder {
        TextView excursionTitle;
        TextView excursionDate;

        public ExcursionViewHolder(@NonNull View itemView) {
            super(itemView);
            excursionTitle = itemView.findViewById(R.id.excursion_title);
        }
    }
}
