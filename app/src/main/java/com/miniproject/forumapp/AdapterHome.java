package com.miniproject.forumapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.imageview.ShapeableImageView;

import java.util.ArrayList;
import java.util.List;

import me.fahmisdk6.avatarview.AvatarView;

public class AdapterHome extends RecyclerView.Adapter<AdapterHome.MyViewHolder> implements Filterable {

    List<ModelHome> list;
    List<ModelHome> listFiltered;
    Context context;
    private OnItemClickListener mListener;

    public interface OnItemClickListener{
        void onViewClick(List<ModelHome> l, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        mListener = listener;
    }


    public AdapterHome(Context context, List<ModelHome> list) {
        this.context = context;
        this.list = list;
        this.listFiltered = list;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_homerecycler,parent,false);
        return new MyViewHolder(view,mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        ModelHome m = listFiltered.get(position);

        holder.mTopicName.setText(m.getTopicname());
        holder.mDesp.setText(m.getDesp());
        holder.mDP.bind(m.getTopicname(),null);
        holder.imageView.setImageResource(Keys.getImage(m.getCategory()));
        if (m.getFollowers()==1)
            holder.mFollowers.setText(m.getFollowers()+" follower");
        else
            holder.mFollowers.setText(m.getFollowers()+" followers");

    }

    @Override
    public int getItemCount() {
        return listFiltered.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView mTopicName,mDesp,mFollowers;
        ImageView imageView;
        AvatarView mDP;
        LinearLayout mLL;


        public MyViewHolder(@NonNull View itemView,final OnItemClickListener listener) {
            super(itemView);

            mTopicName = itemView.findViewById(R.id.itemhome_topicname);
            mDesp = itemView.findViewById(R.id.itemhome_desp);
            mLL = itemView.findViewById(R.id.itemhome_container);
            mFollowers = itemView.findViewById(R.id.itemhome_followers);
            mDP = itemView.findViewById(R.id.itemhome_dp);
            imageView = itemView.findViewById(R.id.imageView);


            mLL.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onViewClick(listFiltered,getAdapterPosition());
                }
            });

        }
    }

    @Override
    public Filter getFilter(){
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    listFiltered = list;
                } else {
                    List<ModelHome> filteredList = new ArrayList<>();
                    for (ModelHome row : list) {
                        if (row.getTopicname().toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(row);
                        }
                    }

                    listFiltered = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = listFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                listFiltered = (ArrayList<ModelHome>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }
}
