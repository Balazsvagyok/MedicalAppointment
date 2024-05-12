package com.example.medicalappointment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.view.menu.MenuView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class DoctorItemAdapter extends RecyclerView.Adapter<DoctorItemAdapter.ViewHolder> implements Filterable {
    private ArrayList<DoctorItem> mDoctorItemsData;
    private ArrayList<DoctorItem> mDoctorItemsDataAll;
    private Context mContext;
    private int lastPosition = -1;

    public DoctorItemAdapter(Context context, ArrayList<DoctorItem> itemsData) {
        this.mDoctorItemsData = itemsData;
        this.mDoctorItemsDataAll = itemsData;
        this.mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(DoctorItemAdapter.ViewHolder holder, int position) {
        DoctorItem currentItem = mDoctorItemsData.get(position);

        holder.bindTo(currentItem);

        if(holder.getBindingAdapterPosition() > lastPosition) {
            Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.slide_in_row);
            holder.itemView.startAnimation(animation);
            lastPosition = holder.getBindingAdapterPosition();
        }
    }

    @Override
    public int getItemCount() {
        return mDoctorItemsData.size();
    }

    @Override
    public Filter getFilter() {
        return doctorFilter;
    }

    private Filter doctorFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            ArrayList<DoctorItem> filteredList = new ArrayList<>();
            FilterResults results = new FilterResults();

            if (constraint == null || constraint.length() == 0) {
                results.count = mDoctorItemsDataAll.size();
                results.values = mDoctorItemsDataAll;
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();

                for (DoctorItem item : mDoctorItemsDataAll) {
                    if (item.getName().toLowerCase().contains(filterPattern)) {
                        filteredList.add(item);
                    }
                }

                results.count = filteredList.size();
                results.values = filteredList;
            }

            return results;
        }

        @SuppressLint("NotifyDataSetChanged")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            mDoctorItemsData = (ArrayList) results.values;
            notifyDataSetChanged();
        }
    };

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView mTitleText;
        private TextView mInfoText;
        private TextView mPhoneText;
        private RatingBar mRatingBar;

        public ViewHolder(View itemView) {
            super(itemView);

            mTitleText = itemView.findViewById(R.id.itemTitle);
            mInfoText = itemView.findViewById(R.id.subTitle);
            mPhoneText = itemView.findViewById(R.id.phoneNumber);
            mRatingBar = itemView.findViewById(R.id.ratingBar);

            itemView.findViewById(R.id.reservation).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("Activity", "A foglalás sikeresen megtörtént.");
                }
            });
        }

        public void bindTo(DoctorItem currentItem) {
            mTitleText.setText(currentItem.getName());
            mInfoText.setText(currentItem.getInfo());
            mPhoneText.setText(currentItem.getPhone());
            mRatingBar.setRating(currentItem.getRatedInfo());

            // Glide.with(mContext).load(currentItem.getImageResource()).into(mItemIgame);
        }
    }
}
