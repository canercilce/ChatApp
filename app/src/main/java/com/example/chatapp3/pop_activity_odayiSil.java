package com.example.chatapp3;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.HashMap;

public class pop_activity_odayiSil extends AppCompatActivity implements View.OnClickListener{

    Button geri2;
    Button odaSil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pop_odayi_sil);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        getWindow().setLayout((int)(width*0.8),(int)(height*0.6));

        geri2 = (Button) findViewById(R.id.geri2);
        odaSil = (Button) findViewById(R.id.odayiSil_button);
        geri2.setOnClickListener(this);
        odaSil.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if(view.getId()==R.id.geri2){
            finish();
        }
        else if(view.getId()==R.id.odayiSil_button){
            finish();
            HashMap<String,Object> hashMap_room = EnterRoomIdFragment.getHashMapByRoomName(RoomInfoFragment.roomname);
            EnterNewRoomId.value.remove(hashMap_room);
            LoginFragment.myRef.setValue(EnterNewRoomId.value);
            Toast.makeText(getApplicationContext(),"Oda silindi.",Toast.LENGTH_SHORT).show();
            LoginFragment.mAuth.signOut();
            Intent intent = new Intent(pop_activity_odayiSil.this, MainActivity.class);
            startActivity(intent);
        }
    }
}