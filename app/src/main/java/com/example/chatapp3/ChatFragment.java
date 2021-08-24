package com.example.chatapp3;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.chatapp3.databinding.FragmentChatBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class ChatFragment extends Fragment {
    private FragmentChatBinding binding;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth mAuth;
    private ChatRecyclerAdapter adapter = new ChatRecyclerAdapter();
    private ArrayList<Chat> chats = new ArrayList<Chat>();
    private String path;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseFirestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        setHasOptionsMenu(true);

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.my_menu,menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==R.id.logout){
            Intent intent = new Intent(getActivity(),pop_activity.class);
            startActivity(intent);

        }
        else if(item.getItemId()==R.id.anaSayfa){
            NavDirections action = ChatFragmentDirections.actionChatFragmentToNewRoomFragment();
            NavHostFragment navHostFragment = (NavHostFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.fragmentContainerView);
            NavController navController = navHostFragment.getNavController();
            navController.navigate(action);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentChatBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);
        binding.chatRecycler.setAdapter(adapter);
        binding.chatRecycler.setLayoutManager(new LinearLayoutManager(requireContext()));

        if(EnterNewRoomId.cameFromEnterNewRoomId){
            path = EnterNewRoomId.odaIsmi;
            EnterNewRoomId.cameFromEnterNewRoomId = false;
        }
        else if(EnterRoomIdFragment.cameFromEnterRoomIdFragment){
            path= EnterRoomIdFragment.roomName;
            EnterRoomIdFragment.cameFromEnterRoomIdFragment=false;
        }

        String finalPath = path;
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(finalPath);

        binding.sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String chatText = binding.chatText.getText().toString();
                String userMail = Objects.requireNonNull(mAuth.getCurrentUser()).getEmail();
                FieldValue date= FieldValue.serverTimestamp();
                String username ="";

                for (Object u: SignUpFragment.value2) {

                    String mail = ((HashMap<String, String>) u).get("email");

                    if (mail.equals(userMail)){
                        username = ((HashMap<String, String>) u).get("username");
                        break;
                    }
                }
                HashMap<String,Object> hashMap = new HashMap<String, Object>();
                hashMap.put("text",chatText);
                hashMap.put("userMail",userMail);
                hashMap.put("date",date);
                hashMap.put("username",username);

                firebaseFirestore.collection(finalPath).add(hashMap).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        binding.chatText.setText("");
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        Toast.makeText(requireContext(),e.getLocalizedMessage(),Toast.LENGTH_LONG).show();
                        binding.chatText.setText("");
                    }
                });
            }
        });

        firebaseFirestore.collection(finalPath).orderBy("date", Query.Direction.ASCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot value, FirebaseFirestoreException error) {
                if(error!=null){
                    Toast.makeText(requireContext(),error.getLocalizedMessage(),Toast.LENGTH_LONG).show();
                }
                else{
                    if(value!=null){
                        if(value.isEmpty()){
                            Toast.makeText(requireContext(),"mesaj yok",Toast.LENGTH_LONG).show();
                        }
                        else{
                            List<DocumentSnapshot> documents = value.getDocuments();
                            chats.clear();
                            for(DocumentSnapshot documentSnapshot: documents){
                                String text = documentSnapshot.get("text").toString();
                                String mail = documentSnapshot.get("userMail").toString();
                                String username = documentSnapshot.get("username").toString();
                                Chat chat = new Chat(text,username,mail);
                                chats.add(chat);
                                adapter.chats = chats;
                            }
                        }
                        adapter.notifyDataSetChanged();
                    }
                }

            }
        });
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}