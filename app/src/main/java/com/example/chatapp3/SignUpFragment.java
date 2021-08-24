package com.example.chatapp3;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.chatapp3.databinding.FragmentLoginBinding;
import com.example.chatapp3.databinding.FragmentSignUpBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import static android.content.ContentValues.TAG;

public class SignUpFragment extends Fragment {
    private FragmentSignUpBinding binding;
    private FirebaseAuth mAuth;
    static ArrayList<User> value2 = new ArrayList<User>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();

        /*LoginFragment.myRef2.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                value2 = (ArrayList<User>) snapshot.getValue();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });*/

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentSignUpBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.SignButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(binding.personUsernameSignUp.getText().toString().isEmpty()){
                    Toast.makeText(requireContext(), "kullanici adi bos gecilemez.",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(binding.personEmailSignUp.getText().toString().isEmpty() || binding.personPasswordSignUp.getText().toString().isEmpty()){
                    Toast.makeText(requireContext(), "email veya sifre bos gecilemez.",Toast.LENGTH_SHORT).show();
                    return;
                }

                mAuth.createUserWithEmailAndPassword(binding.personEmailSignUp.getText().toString(),binding.personPasswordSignUp.getText().toString()).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        //kullanıcı olusturuldu.
                        User user = new User(binding.personUsernameSignUp.getText().toString(), binding.personEmailSignUp.getText().toString(), binding.personPasswordSignUp.getText().toString());
                        if(value2==null){
                            value2 = new ArrayList<User>();
                        }
                        value2.add(user);
                        LoginFragment.myRef2.setValue(value2);
                        Toast.makeText(requireContext(),"kullanici olusturuldu!",Toast.LENGTH_SHORT).show();
                        NavDirections action = SignUpFragmentDirections.actionSignUpFragmentToNewRoomFragment();
                        Navigation.findNavController(view).navigate(action);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        //hata
                        Toast.makeText(requireContext(), e.getLocalizedMessage(),Toast.LENGTH_LONG).show();
                    }
                });
            }
        });

        binding.backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavDirections action = SignUpFragmentDirections.actionSignUpFragmentToLoginFragment();
                Navigation.findNavController(view).navigate(action);
            }
        });
    }
    
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}