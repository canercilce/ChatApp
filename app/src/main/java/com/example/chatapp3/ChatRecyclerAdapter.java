package com.example.chatapp3;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.AsyncListDiffer;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ChatRecyclerAdapter extends RecyclerView.Adapter<ChatRecyclerAdapter.ChatHolder> {
    private FirebaseAuth mAuth;
    int VİEW_TYPE_SENT = 1;
    int VİEW_TYPE_RECEİVED = 2;
    int VİEW_TYPE_IMAGE_SENT =3;
    int VİEW_TYPE_IMAGE_RECEİVED =4;
    String fromMessageType;
    class ChatHolder extends RecyclerView.ViewHolder{
        public ImageView resim = itemView.findViewById(R.id.recyclerImage_right);
        public ImageView resim2 = itemView.findViewById(R.id.recyclerImage);
        public ChatHolder(View itemView) {
            super(itemView);

        }
    }

    DiffUtil.ItemCallback<Chat> diffUtil = new DiffUtil.ItemCallback<Chat>() {
        @Override
        public boolean areItemsTheSame(Chat oldItem, Chat newItem) {
            return oldItem==newItem;
        }

        @SuppressLint("DiffUtilEquals")
        @Override
        public boolean areContentsTheSame(Chat oldItem, Chat newItem) {
            return oldItem==newItem;
        }
    };

    AsyncListDiffer recyclerListDiffer= new AsyncListDiffer(this,diffUtil);
    List<Chat> chats= new ArrayList<Chat>();

    public List getRecyclerListDiffer() {
        return recyclerListDiffer.getCurrentList();
    }

    public void setRecyclerListDiffer(List list) {
        recyclerListDiffer.submitList(list);
    }

    @Override
    public int getItemViewType(int position) {
        Chat chat = chats.get(position);
        if(chat.messageType.equals("image")){
            if(chat.mail.equals(FirebaseAuth.getInstance().getCurrentUser().getEmail().toString())){
                return VİEW_TYPE_IMAGE_SENT;
            }
            else{
                return VİEW_TYPE_IMAGE_RECEİVED;
            }
        }
        if(chat.mail.equals(FirebaseAuth.getInstance().getCurrentUser().getEmail().toString())){
            return VİEW_TYPE_SENT;
        }
        else {
            return VİEW_TYPE_RECEİVED;
        }
    }

    @Override
    public ChatHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mAuth = FirebaseAuth.getInstance();
        if(viewType==VİEW_TYPE_IMAGE_RECEİVED){
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_row_image,parent,false);
            return new ChatHolder(view);
        }
        else if(viewType==VİEW_TYPE_IMAGE_SENT){
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_row_image_right,parent,false);
            return new ChatHolder(view);
        }

        else if(viewType==VİEW_TYPE_RECEİVED){
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_row,parent,false);
            return new ChatHolder(view);
        }
        else{
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_row_right,parent,false);
            return new ChatHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(ChatRecyclerAdapter.ChatHolder holder, int position) {
        String messageSenderID = mAuth.getCurrentUser().getEmail();
        Chat chat = chats.get(position);
        String fromUserID= chat.mail;
        fromMessageType = chat.messageType;

        if(fromMessageType.equals("image")){
            if(fromUserID.equals(messageSenderID)){
                TextView textView= holder.itemView.findViewById(R.id.sendPicture);
                textView.setText(chats.get(position).username+":");
                Picasso.get().load(chat.message).into(holder.resim);
            }
            else{
                TextView textView= holder.itemView.findViewById(R.id.receivePicture);
                textView.setText(chats.get(position).username+":");
                Picasso.get().load(chat.message).into(holder.resim2);
            }
        }
        else{
            TextView textView= holder.itemView.findViewById(R.id.recyclerText);
            textView.setText(chats.get(position).username+": "+chats.get(position).message);

        }

    }

    @Override
    public int getItemCount() {
        return chats.size();
    }


}
