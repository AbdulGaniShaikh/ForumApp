package com.miniproject.forumapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.imageview.ShapeableImageView;
import java.util.List;

public class AdapterFriendReq extends RecyclerView.Adapter<AdapterFriendReq.MyViewHolder>{

    List<ModelFriendReq> list;
    Context context;
    private OnItemClickListener mListener;

    public interface OnItemClickListener{
        void onAcceptClick(int position);
        void onDeleteClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        mListener = listener;
    }


    public AdapterFriendReq(Context context, List<ModelFriendReq> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_req_people,parent,false);
        return new MyViewHolder(view,mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.mName.setText(list.get(position).getName());
        holder.mDP.setImageResource(Keys.getAvatar(list.get(position).getAvatar()));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView mName,mAccept,mDelete;
        ShapeableImageView mDP;

        public MyViewHolder(@NonNull View itemView,final OnItemClickListener listener) {
            super(itemView);

            mName = itemView.findViewById(R.id.name_req_people);
            mAccept = itemView.findViewById(R.id.accept_req_people);
            mDelete = itemView.findViewById(R.id.delete_req_people);

            mDP = itemView.findViewById(R.id.avatar_req_people);


            mAccept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onAcceptClick(getAdapterPosition());
                }
            });

            mDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onDeleteClick(getAdapterPosition());
                }
            });

        }
    }
}
