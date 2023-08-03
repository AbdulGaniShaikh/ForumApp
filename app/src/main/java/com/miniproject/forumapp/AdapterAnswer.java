package com.miniproject.forumapp;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.imageview.ShapeableImageView;

import java.util.List;

public class AdapterAnswer extends RecyclerView.Adapter<AdapterAnswer.MyViewHolder> {

    Context context;
    List<ModelAnswer> list;
    private AdapterAnswer.OnItemClickListener mListener;
    AlertDialog.Builder builder;

    public AdapterAnswer(Context context, List<ModelAnswer> list) {
        this.context = context;
        this.list = list;
    }

    public interface OnItemClickListener{
        void onViewClick(int position);
        void onAnswer(int position);
    }

    public void setOnItemClickListener(AdapterAnswer.OnItemClickListener listener){
        mListener = listener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_answer, parent, false);
        return new AdapterAnswer.MyViewHolder(v, mListener);
    }


    @Override
    public void onBindViewHolder(@NonNull AdapterAnswer.MyViewHolder holder, int position) {

        ModelAnswer q = list.get(position);
        holder.mName.setText(q.getName());
        holder.mAnswer.setText(q.getAnswer());
        holder.mEmail.setText(q.getMail());
        holder.timestamp.setText(q.getTime());
        holder.mDP.setImageResource(Keys.getAvatar(q.getAvatar()));

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        TextView mAnswer, mName, mEmail,timestamp,delQuestionBtn,viewAnswersBtn;
        ShapeableImageView mDP;

        public MyViewHolder(@NonNull View itemView, final AdapterAnswer.OnItemClickListener listener) {
            super(itemView);

            mAnswer= itemView.findViewById(R.id.answer);
            mName = itemView.findViewById(R.id.name_answer);
            mEmail = itemView.findViewById(R.id.mail_answer);
            timestamp = itemView.findViewById(R.id.timestamp_answer);
            mDP = itemView.findViewById(R.id.avatar_answer);
            
        }
    }
}