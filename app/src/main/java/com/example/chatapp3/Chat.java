package com.example.chatapp3;

import android.widget.ImageView;

public class Chat {
    String message;
    String username;
    String mail;
    String messageType;

    Chat(String message,String username, String mail, String messageType){
        this.message= message;
        this.username= username;
        this.mail = mail;
        this.messageType = messageType;
    }


}
