package com.miniproject.forumapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.imageview.ShapeableImageView;

import java.util.List;

public class AdapterSuggestion extends RecyclerView.Adapter<AdapterSuggestion.MyViewHolder>{

    List<ModelPeople> list;
    Context context;
    private OnItemClickListener mListener;

    public interface OnItemClickListener{
        void onClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        mListener = listener;
    }


    public AdapterSuggestion(Context context, List<ModelPeople> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_sqr_people,parent,false);
        return new MyViewHolder(view,mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.mName.setText(list.get(position).getName());
        holder.mDP.setImageResource(Keys.getAvatar(list.get(position).getAvatar()));
        holder.mMail.setText(list.get(position).getMail());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView mName,mMail;
        ShapeableImageView mDP;
        LinearLayout ll;

        public MyViewHolder(@NonNull View itemView,final OnItemClickListener listener) {
            super(itemView);

            mName = itemView.findViewById(R.id.name_sqr_people);
            mMail = itemView.findViewById(R.id.mail_sqr_people);
            mDP = itemView.findViewById(R.id.avatar_sqr_people);
            ll = itemView.findViewById(R.id.con_sqr_people);

            ll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos = getAdapterPosition();
                    if (pos!=RecyclerView.NO_POSITION){
                        listener.onClick(pos);
                    }
                }
            });
        }
    }
}

