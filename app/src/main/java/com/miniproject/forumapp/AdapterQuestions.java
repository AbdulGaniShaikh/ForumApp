package com.miniproject.forumapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.imageview.ShapeableImageView;

import java.util.ArrayList;
import java.util.List;

import me.fahmisdk6.avatarview.AvatarView;

public class AdapterQuestions extends RecyclerView.Adapter<AdapterQuestions.MyViewHolder> {

    Context context;
    List<ModelQuestion> list;
    private AdapterQuestions.OnItemClickListener mListener;
    AlertDialog.Builder builder;

    public AdapterQuestions(Context context, List<ModelQuestion> list) {
        this.context = context;
        this.list = list;
    }

    public interface OnItemClickListener{
        void onViewClick(int position);
        void onAnswer(int position);
    }

    public void setOnItemClickListener(AdapterQuestions.OnItemClickListener listener){
        mListener = listener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_question, parent, false);
        return new AdapterQuestions.MyViewHolder(v, mListener);
    }


    @Override
    public void onBindViewHolder(@NonNull AdapterQuestions.MyViewHolder holder, int position) {

        ModelQuestion q = list.get(position);
        holder.mName.setText(q.getName());
        holder.mQuestion.setText(q.getQuestion());
        holder.mEmail.setText(q.getMail());
        holder.timestamp.setText(q.getTime());
        holder.mDP.setImageResource(Keys.getAvatar(q.getAvatar()));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        TextView mQuestion, mName, mEmail,timestamp,delQuestionBtn,viewAnswersBtn;
        LinearLayout ll;
        ShapeableImageView mDP;

        public MyViewHolder(@NonNull View itemView, final AdapterQuestions.OnItemClickListener listener) {
            super(itemView);

            mQuestion= itemView.findViewById(R.id.question);
            mName = itemView.findViewById(R.id.name_question);
            mEmail = itemView.findViewById(R.id.mail_question);
            delQuestionBtn= itemView.findViewById(R.id.delete_question);
            viewAnswersBtn = itemView.findViewById(R.id.answers_question);
            timestamp = itemView.findViewById(R.id.timestamp_question);
            mDP = itemView.findViewById(R.id.avatar_question);
            ll = itemView.findViewById(R.id.con_question);

            builder = new AlertDialog.Builder(context);

            viewAnswersBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener!=null){
                        int position = getAdapterPosition();
                        if (position!=RecyclerView.NO_POSITION)
                        {
                            listener.onViewClick(position);
                        }
                    }
                }
            });

            ll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener!=null){
                        int position = getAdapterPosition();
                        if (position!=RecyclerView.NO_POSITION)
                        {
                            listener.onViewClick(position);
                        }
                    }
                }
            });


            delQuestionBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(listener!=null){
                        int position = getAdapterPosition();
                        if (position!=RecyclerView.NO_POSITION){
                            listener.onAnswer(position);
                        }
                    }
                }
            });
        }
    }
}