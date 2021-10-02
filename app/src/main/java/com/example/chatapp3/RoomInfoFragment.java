package com.example.chatapp3;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.chatapp3.databinding.FragmentChatBinding;
import com.example.chatapp3.databinding.FragmentRoomInfoBinding;
import com.google.firebase.auth.FirebaseAuth;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class RoomInfoFragment extends Fragment {
    static FragmentRoomInfoBinding binding;
    private FirebaseAuth mAuth;
    private OnlineUsersRecyclerAdapter adapter = new OnlineUsersRecyclerAdapter();
    private ArrayList<String> usernames = new ArrayList<String>();
    static boolean cameFromRoomInfoFragment;
    static String roomname ="";
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        setHasOptionsMenu(true);
        cameFromRoomInfoFragment = true;

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.my_menu2,menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        HashMap<String,String> user = (HashMap<String,String>) EnterNewRoomId.getUserbyEmail(mAuth.getCurrentUser().getEmail());

        if(!binding.odaKurucusu2.getText().toString().equals(user.get("username"))){
            MenuItem item = menu.findItem(R.id.odayiSil);
            item.setVisible(false);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==R.id.geriSembolu){
            NavDirections action = RoomInfoFragmentDirections.actionRoomInfoFragmentToChatFragment();
            NavHostFragment navHostFragment = (NavHostFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.fragmentContainerView);
            NavController navController = navHostFragment.getNavController();
            navController.navigate(action);
        }
        else if(item.getItemId()==R.id.odayiSil){
            Intent intent = new Intent(getActivity(),pop_activity_odayiSil.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentRoomInfoBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.OnlineUsersRecyclerView.setAdapter(adapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(requireContext());
        binding.OnlineUsersRecyclerView.setLayoutManager(linearLayoutManager);

        if(EnterRoomIdFragment.roomName==null){
            roomname = EnterNewRoomId.odaIsmi;
            Room room = EnterRoomIdFragment.getRoomByRoomName(roomname);
            binding.roomName2.setText(room.getOdaIsmi());
            binding.roomPassword2.setText(room.getOdaSifresi());
            String usernameOfOdaKurucusu = ((HashMap<String,String>) room.getOdaKurucusu()).get("username");
            binding.odaKurucusu2.setText(usernameOfOdaKurucusu);
            binding.kurulmaTarihi2.setText(room.getKurulmaTarihi()+" (GMT+3)");
        }
        else{
            roomname = EnterRoomIdFragment.roomName;
            HashMap<String,Object> roomHashMap = (HashMap<String, Object>) EnterRoomIdFragment.getHashMapByRoomName(roomname);
            assert roomHashMap != null;
            binding.roomName2.setText(roomHashMap.get("odaIsmi").toString());
            binding.roomPassword2.setText(roomHashMap.get("odaSifresi").toString());
            HashMap<String,String> odaKurucusu = (HashMap<String,String>) roomHashMap.get("odaKurucusu");
            binding.odaKurucusu2.setText(odaKurucusu.get("username"));
            binding.kurulmaTarihi2.setText(roomHashMap.get("kurulmaTarihi").toString()+" (GMT+3)");

        }

        ArrayList<String> odadakiler;
        HashMap<String,Object> hashMapRoom = EnterRoomIdFragment.getHashMapByRoomName(roomname);

        if(hashMapRoom==null){
            Room room = EnterRoomIdFragment.getRoomByRoomName(roomname);
            odadakiler = (ArrayList<String>) room.getOdadakiler();
        }
        else{
            odadakiler = (ArrayList<String>) hashMapRoom.get("odadakiler");
        }

        usernames = (ArrayList<String>) odadakiler.clone();
        if(usernames.contains("*")){
            usernames.remove("*");
        }
        adapter.usernames = usernames;
    }
}