package com.example.chatapp3;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.AsyncListDiffer;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class ChatRecyclerAdapter extends RecyclerView.Adapter<ChatRecyclerAdapter.ChatHolder> {

    int VİEW_TYPE_SENT = 1;
    int VİEW_TYPE_RECEİVED = 2;
    class ChatHolder extends RecyclerView.ViewHolder{
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
        if(chat.mail.equals(FirebaseAuth.getInstance().getCurrentUser().getEmail().toString())){
            return VİEW_TYPE_SENT;
        }
        else {
            return VİEW_TYPE_RECEİVED;
        }
    }

    @Override
    public ChatHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType==VİEW_TYPE_RECEİVED){
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
        TextView textView= holder.itemView.findViewById(R.id.recyclerText);
        textView.setText(chats.get(position).username+": "+chats.get(position).text);
    }

    @Override
    public int getItemCount() {
        return chats.size();
    }


}
