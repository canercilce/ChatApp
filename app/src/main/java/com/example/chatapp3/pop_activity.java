package com.example.chatapp3;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentActivity;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.fragment.NavHostFragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class pop_activity extends AppCompatActivity implements View.OnClickListener {

    Button geri;
    static Button cikis;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pop);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        getWindow().setLayout((int)(width*0.8),(int)(height*0.6));

        geri= (Button) findViewById(R.id.geri);
        cikis= (Button) findViewById(R.id.cikis);

        geri.setOnClickListener(this);
        cikis.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if(view.getId()==R.id.geri){
            finish();
        }
        else if(view.getId()==R.id.cikis){
            finish();
            LoginFragment.mAuth.signOut();
            Intent intent = new Intent(pop_activity.this, MainActivity.class);
            startActivity(intent);
        }

    }
}