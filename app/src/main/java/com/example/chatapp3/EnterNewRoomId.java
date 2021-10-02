package com.example.chatapp3;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.chatapp3.databinding.FragmentEnterNewRoomIdBinding;
import com.example.chatapp3.databinding.FragmentEnterRoomIdBinding;
import com.example.chatapp3.databinding.FragmentNewRoomBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import static android.content.ContentValues.TAG;

public class EnterNewRoomId extends Fragment {
    private FragmentEnterNewRoomIdBinding binding;
    private FirebaseAuth mAuth;
    private FirebaseFirestore firebaseFirestore;
    static String odaIsmi;
    static String odaSifresi;
    static Object odaKurucusu;
    static ArrayList<String> odadakiler;
    static boolean cameFromEnterNewRoomId = false;
    static ArrayList<Room> value = new ArrayList<Room>();
    static boolean izin=true;
    private ArrayList<String> odaIsimleri = new ArrayList<String>();


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseFirestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        setHasOptionsMenu(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("ChatApp3");
        /*LoginFragment.myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                value = (ArrayList<String>) snapshot.getValue();
                System.out.println(value);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });*/
        //System.out.println(value);//once bunu cagırıyor
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.my_menu,menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        MenuItem item = menu.findItem(R.id.odaBilgisi);
        item.setVisible(false);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==R.id.logout){
            Intent intent = new Intent(getActivity(),pop_activity.class);
            startActivity(intent);
        }
        else if(item.getItemId()==R.id.anaSayfa){
            NavDirections action = EnterNewRoomIdDirections.actionEnterNewRoomIdToNewRoomFragment();
            NavHostFragment navHostFragment = (NavHostFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.fragmentContainerView);
            NavController navController = navHostFragment.getNavController();
            navController.navigate(action);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentEnterNewRoomIdBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavDirections action = EnterNewRoomIdDirections.actionEnterNewRoomIdToNewRoomFragment();
                Navigation.findNavController(view).navigate(action);
            }
        });

        binding.button4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                odaIsmi = binding.editText2.getText().toString();
                odaSifresi = binding.optionalPassword.getText().toString();
                odaKurucusu = getUserbyEmail(mAuth.getCurrentUser().getEmail());
                odadakiler = new ArrayList<String>();
                odadakiler.add("*");

                if(odaIsmi.isEmpty()){
                    Toast.makeText(requireContext(),"oda ismi bos olmamalidir.",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(value==null){
                    value = new ArrayList<Room>();
                }
                for (Object o:value) {
                    String odaName = ((HashMap<String, String>) o).get("odaIsmi");
                    odaIsimleri.add(odaName);
                }
                if(odaIsimleri.contains(odaIsmi)){
                    Toast.makeText(requireContext(),"Bu isimde baska bir oda var.",Toast.LENGTH_SHORT).show();
                    binding.editText2.setText("");
                    return;
                }
                HashMap<String,Object> hashMap = new HashMap<String, Object>();

                SimpleDateFormat formatter= new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                Date date = new Date(System.currentTimeMillis());
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(date);
                String formatDateTime = formatter.format(calendar.getTime());

                Room room = new Room(odaKurucusu,odaIsmi,odaSifresi, formatDateTime, odadakiler);
                value.add(room);
                System.out.println(room.getOdadakiler());
                /*RoomInfoFragment.binding.roomName2.setText(odaIsmi);
                RoomInfoFragment.binding.roomPassword2.setText(odaSifresi);
                String usernameOfOdaKurucusu = ((HashMap<String,String>) odaKurucusu).get("username");
                RoomInfoFragment.binding.odaKurucusu2.setText(usernameOfOdaKurucusu);*/
                LoginFragment.myRef.setValue(value);
                if(izin){

                    firebaseFirestore.collection(odaIsmi).add(hashMap).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            cameFromEnterNewRoomId =true;
                            NavDirections action = EnterNewRoomIdDirections.actionEnterNewRoomIdToChatFragment();
                            Navigation.findNavController(view).navigate(action);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(Exception e) {
                            Toast.makeText(requireContext(),e.getLocalizedMessage(),Toast.LENGTH_LONG).show();
                        }
                    });
                }
                izin=true;
            }
        });
    }

    public static Object getUserbyEmail(String email){
        for (Object u:SignUpFragment.value2) {
            String mail = ((HashMap<String, String>) u).get("email");
            if(email.equals(mail)){
                return u;
            }
        }
        return null;
    }
}