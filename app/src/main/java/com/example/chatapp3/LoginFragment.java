package com.example.chatapp3;

import android.app.Notification;
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
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.chatapp3.databinding.FragmentLoginBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.common.reflect.TypeToken;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.gson.Gson;

import java.lang.reflect.Type;
import java.util.ArrayList;

import static android.content.ContentValues.TAG;
import static android.content.Context.MODE_PRIVATE;

public class LoginFragment extends Fragment {
    private FragmentLoginBinding binding;
    static FirebaseAuth mAuth;
    static FirebaseUser currentUser;
    FirebaseDatabase database;
    FirebaseDatabase database2;
    static DatabaseReference myRef;
    static DatabaseReference myRef2;
    static String shared= "shared preferences";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        /*if(!MyApp.calledBefore){
            loadData();
            MyApp.calledBefore=true;
        }
        saveData();*/

        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        System.out.println(currentUser);
        if(currentUser!=null){
            NavDirections action = LoginFragmentDirections.actionLoginFragmentToNewRoomFragment();
            NavHostFragment navHostFragment = (NavHostFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.fragmentContainerView);
            NavController navController = navHostFragment.getNavController();
            navController.navigate(action);
        }

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("odalar");
        database2 = FirebaseDatabase.getInstance();
        myRef2 = database2.getReference("kullanicilar");
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("ChatApp3");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentLoginBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        /*myList.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                String value = snapshot.getValue(String.class);
                Log.d(TAG, "Value is: " + value);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
        */
        binding.SignButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavDirections action = LoginFragmentDirections.actionLoginFragmentToSignUpFragment();
                Navigation.findNavController(view).navigate(action);

            }
        });

        binding.loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(binding.personEmail.getText().toString().isEmpty() || binding.personPassword.getText().toString().isEmpty()){
                    Toast.makeText(requireContext(), "email veya sifre bos gecilemez.",Toast.LENGTH_SHORT).show();
                    return;
                }
                mAuth.signInWithEmailAndPassword(binding.personEmail.getText().toString(),binding.personPassword.getText().toString()).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        NavDirections action = LoginFragmentDirections.actionLoginFragmentToNewRoomFragment();
                        Navigation.findNavController(view).navigate(action);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        Toast.makeText(requireContext(), e.getLocalizedMessage(),Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    /*private void loadData() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(shared, MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("odalar", null);
        Type type = new TypeToken<ArrayList<String>>() {}.getType();
        EnterNewRoomId.value = gson.fromJson(json, type);
        if (EnterNewRoomId.value == null) {
            EnterNewRoomId.value = new ArrayList<String>();
        }
    }

    private void saveData() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(shared, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(EnterNewRoomId.value);
        editor.putString("odalar", json);

        editor.apply();

    }*/
}