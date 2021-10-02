package com.example.chatapp3;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.chatapp3.databinding.FragmentEnterRoomIdBinding;
import com.example.chatapp3.databinding.FragmentNewRoomBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class EnterRoomIdFragment extends Fragment {
    private FragmentEnterRoomIdBinding binding;
    private FirebaseAuth mAuth;
    private FirebaseFirestore firebaseFirestore;
    static String roomName;
    static boolean cameFromEnterRoomIdFragment = false;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseFirestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        setHasOptionsMenu(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("ChatApp3");
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
            NavDirections action = EnterRoomIdFragmentDirections.actionEnterRoomIdFragmentToNewRoomFragment();
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
        binding = FragmentEnterRoomIdBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavDirections action = EnterRoomIdFragmentDirections.actionEnterRoomIdFragmentToNewRoomFragment();
                Navigation.findNavController(view).navigate(action);
            }
        });

        binding.button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                roomName = binding.editText.getText().toString();
                HashMap<String,Object> room = getHashMapByRoomName(roomName);
                System.out.println(room);
                if((EnterNewRoomId.value).contains(room)){
                    HashMap<String,Object> hashMap = new HashMap<String, Object>();
                    firebaseFirestore.collection(roomName).add(hashMap).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            cameFromEnterRoomIdFragment = true;
                            HashMap<String,Object> hashMap_room = (HashMap<String,Object>) room;
                            if(!(hashMap_room.get("odaSifresi").equals(""))){
                                NavDirections action = EnterRoomIdFragmentDirections.actionEnterRoomIdFragmentToEnterRoomPasswordFragment();
                                Navigation.findNavController(view).navigate(action);
                            }
                            else{
                                NavDirections action = EnterRoomIdFragmentDirections.actionEnterRoomIdFragmentToChatFragment();
                                Navigation.findNavController(view).navigate(action);
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(Exception e) {
                            Toast.makeText(requireContext(),e.getLocalizedMessage(),Toast.LENGTH_LONG).show();
                        }
                    });
                }
                else{
                    Toast.makeText(requireContext(),"Bu isimde bir oda yok.",Toast.LENGTH_SHORT).show();
                    binding.editText.setText("");
                    return;
                }
            }
        });

    }

    public static Room getRoomByRoomName(String roomName){
        String roomname ="";
        for (Object room: EnterNewRoomId.value) {
            if(room.getClass().toString().equals("class com.example.chatapp3.Room")){
                roomname = ((Room) room).getOdaIsmi();
                if(roomName.equals(roomname)){
                    return (Room) room;
                }
            }
        }
        return null;
    }

    public static HashMap<String,Object> getHashMapByRoomName(String roomName){
        String roomname ="";

        for(Object room: EnterNewRoomId.value) {
            if(room.getClass().toString().equals("class java.util.HashMap")){
                roomname = (String) ((HashMap<String, Object>) room).get("odaIsmi");
                if (roomName.equals(roomname)) {
                    return (HashMap<String, Object>) room;
                }
            }
        }
        return null;
    }
}