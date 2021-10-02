package com.example.chatapp3;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class OnlineUsersRecyclerAdapter extends RecyclerView.Adapter<OnlineUsersRecyclerAdapter.ChatHolder>{
    List<String> usernames= new ArrayList<String>();

    class ChatHolder extends RecyclerView.ViewHolder{
        public ChatHolder(View itemView) {
            super(itemView);
        }
    }

    @Override
    public ChatHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_row,parent,false);
        return new OnlineUsersRecyclerAdapter.ChatHolder(view);
    }

    @Override
    public void onBindViewHolder(OnlineUsersRecyclerAdapter.ChatHolder holder, int position) {
        String username = usernames.get(position);
        TextView textView= holder.itemView.findViewById(R.id.recyclerText);
        textView.setText(username);
    }

    @Override
    public int getItemCount() {
        return usernames.size();
    }
}
