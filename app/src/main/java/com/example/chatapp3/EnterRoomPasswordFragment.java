package com.example.chatapp3;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.chatapp3.databinding.FragmentEnterRoomIdBinding;
import com.example.chatapp3.databinding.FragmentEnterRoomPasswordBinding;
import com.google.firebase.auth.FirebaseAuth;

import java.util.HashMap;

public class EnterRoomPasswordFragment extends Fragment {
    private FragmentEnterRoomPasswordBinding binding;
    private FirebaseAuth mAuth;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentEnterRoomPasswordBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.buttonPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavDirections action = EnterRoomPasswordFragmentDirections.actionEnterRoomPasswordFragmentToEnterRoomIdFragment();
                Navigation.findNavController(view).navigate(action);
            }
        });

        binding.buttonPassword2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String password = binding.editTextOdaSifresi.getText().toString();
                Object room = EnterRoomIdFragment.getRoomByRoomName(EnterRoomIdFragment.roomName);
                System.out.println(room);
                HashMap<String,String> hashMap_room = (HashMap<String,String>) room;
                if(hashMap_room.get("odaSifresi").equals(password)){
                    NavDirections action = EnterRoomPasswordFragmentDirections.actionEnterRoomPasswordFragmentToChatFragment();
                    Navigation.findNavController(view).navigate(action);
                }
                else{
                    Toast.makeText(requireContext(),"yanlis sifre",Toast.LENGTH_SHORT).show();
                    binding.editTextOdaSifresi.setText("");
                    return;
                }
            }
        });
    }
}