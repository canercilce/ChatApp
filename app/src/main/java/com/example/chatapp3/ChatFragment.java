package com.example.chatapp3;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.storage.StorageManager;
import android.provider.DocumentsContract;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.chatapp3.databinding.FragmentChatBinding;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static android.app.Activity.RESULT_OK;

public class ChatFragment extends Fragment {
    private FragmentChatBinding binding;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth mAuth;
    private ChatRecyclerAdapter adapter = new ChatRecyclerAdapter();
    private ArrayList<Chat> chats = new ArrayList<Chat>();
    private String path;
    private String checker ="", myUrl="";
    DatabaseReference databaseRef;
    DatabaseReference rootRef;
    private StorageTask uploadTask;
    private Uri fileUri;
    private ProgressDialog loadingBar;
    Object user;
    HashMap<String,String> userHashmap;
    static String username;
    public boolean isClickedToRoomInfo = false;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseFirestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        setHasOptionsMenu(true);
        rootRef = FirebaseDatabase.getInstance().getReference();
        loadingBar = new ProgressDialog(requireActivity());
        user = EnterNewRoomId.getUserbyEmail(mAuth.getCurrentUser().getEmail());
        userHashmap = (HashMap<String, String>) user;
        username = userHashmap.get("username");

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
        else if(item.getItemId()==R.id.odaBilgisi){
            isClickedToRoomInfo = true;
            /*HashMap<String,Object> hashMapRoom = EnterRoomIdFragment.getHashMapByRoomName(path);
            System.out.println(hashMapRoom);*/
            NavDirections action = ChatFragmentDirections.actionChatFragmentToRoomInfoFragment();
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
    public void onDestroy() {
        super.onDestroy();
        odadanCikar(username);
    }

    @Override
    public void onPause() {
        System.out.println(isClickedToRoomInfo);
        super.onPause();
        if(isClickedToRoomInfo){
        }
        else{
            odadanCikar(username);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        odayaEkle(username);
    }

    @Override
    public void onStop() {
        super.onStop();
        System.out.println(isClickedToRoomInfo);
        if(isClickedToRoomInfo){
            isClickedToRoomInfo = false;
        }
        else{
            odadanCikar(username);
        }


    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.chatRecycler.setAdapter(adapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(requireContext());
        linearLayoutManager.setStackFromEnd(true);
        binding.chatRecycler.setLayoutManager(linearLayoutManager);

        if(EnterNewRoomId.cameFromEnterNewRoomId){
            path = EnterNewRoomId.odaIsmi;
            EnterNewRoomId.cameFromEnterNewRoomId = false;
        }
        else if(EnterRoomIdFragment.cameFromEnterRoomIdFragment){
            path= EnterRoomIdFragment.roomName;
            EnterRoomIdFragment.cameFromEnterRoomIdFragment=false;
        }

        else if(RoomInfoFragment.cameFromRoomInfoFragment){
            path = RoomInfoFragment.roomname;
            RoomInfoFragment.cameFromRoomInfoFragment = false;
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
                hashMap.put("image","");
                hashMap.put("messageType","text");
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
                                String imageLink = documentSnapshot.get("image").toString();
                                String messageType = documentSnapshot.get("messageType").toString();
                                Chat chat = null;
                                if(messageType.equals("text")){
                                    chat = new Chat(text,username,mail,messageType);
                                }
                                else if(messageType.equals("image")){
                                    chat = new Chat(imageLink,username,mail,messageType);
                                }

                                chats.add(chat);
                                adapter.chats = chats;
                            }
                        }
                        adapter.notifyDataSetChanged();
                        //binding.chatRecycler.smoothScrollToPosition(binding.chatRecycler.getAdapter().getItemCount());
                    }
                }

            }
        });

        binding.addFilesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CharSequence options[] = new CharSequence[]{
                        "Resim",
                };
                AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
                builder.setTitle("ekle");
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if(i==0){
                            checker = "image";
                            Intent intent = new Intent();
                            intent.setAction(Intent.ACTION_GET_CONTENT);
                            intent.setType("image/*");
                            startActivityForResult(intent.createChooser(intent,"resim sec"),555);

                        }
                        if(i==1){
                            checker = "pdf";
                        }
                        if(i==2){
                            checker = "docx";
                        }
                    }
                });
                builder.show();
            }
        });
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String finalPath = path;
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(finalPath);
        if(requestCode==555 && resultCode==RESULT_OK && data!=null && data.getData()!=null){
            loadingBar.setTitle("Belge Gonderimi");
            loadingBar.setMessage("Belge gonderiliyor...");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();

            fileUri = data.getData();
            if(!checker.equals("image")){

            }
            else if(checker.equals("image")){
                StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("Image Files");
                String messageSenderRef = "x";
                String messageReceiverRef = "y";
                DatabaseReference userMessageKeyRef = rootRef.child("Messages").child("messageSenderID").child("messageReceiverID").push();
                final String messagePushID = userMessageKeyRef.getKey();
                StorageReference filepath = storageReference.child(messagePushID + "." + "jpg");
                uploadTask = filepath.putFile(fileUri);
                uploadTask.continueWithTask(new Continuation() {
                    @Override
                    public Object then(Task task) throws Exception {
                        if(!task.isSuccessful()){
                            throw task.getException();
                        }
                        return filepath.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                            if(task.isSuccessful()){
                                Uri downloadUrl = task.getResult();
                                myUrl = downloadUrl.toString();
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
                                HashMap<String,Object> hashMap2 = new HashMap<String, Object>();
                                hashMap2.put("text","");
                                hashMap2.put("image", myUrl);
                                hashMap2.put("userMail", userMail);
                                hashMap2.put("username",username);
                                hashMap2.put("date",date);
                                hashMap2.put("messageType","image");
                                firebaseFirestore.collection(finalPath).add(hashMap2).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
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

                                Map messageTextBody = new HashMap();
                                messageTextBody.put("message",myUrl);
                                messageTextBody.put("name",fileUri.getLastPathSegment());
                                messageTextBody.put("type",checker);
                                messageTextBody.put("messageID",messagePushID);

                                Map messageBodyDetails = new HashMap();
                                messageBodyDetails.put(messageSenderRef+"/"+messagePushID,messageTextBody);
                                messageBodyDetails.put(messageReceiverRef+"/"+messagePushID,messageTextBody);

                                rootRef.updateChildren(messageBodyDetails).addOnCompleteListener(new OnCompleteListener() {
                                    @Override
                                    public void onComplete(@NonNull Task task) {
                                        if(task.isSuccessful()){
                                            loadingBar.dismiss();
                                            Toast.makeText(requireContext(), "basarili", Toast.LENGTH_SHORT).show();
                                        }
                                        else{
                                            loadingBar.dismiss();
                                            Toast.makeText(requireContext(), "basarisiz", Toast.LENGTH_SHORT).show();
                                        }

                                    }
                                });
                            }
                    }
                });
            }
            else{
                loadingBar.dismiss();
                Toast.makeText(requireContext(), "Nothing Selected.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public static void odadanCikar(String userName){
        String roomname ="";
        if(EnterRoomIdFragment.roomName==null){
            roomname = EnterNewRoomId.odaIsmi;
        }
        else{
            roomname = EnterRoomIdFragment.roomName;
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

        odadakiler.remove(userName);

        LoginFragment.myRef.setValue(EnterNewRoomId.value);
    }

    public void odayaEkle(String userName){
        String roomname ="";
        if(EnterRoomIdFragment.roomName==null){
            roomname = EnterNewRoomId.odaIsmi;
        }
        else{
            roomname = EnterRoomIdFragment.roomName;
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

        if(!odadakiler.contains(userName)){
            odadakiler.add(userName);
        }

        LoginFragment.myRef.setValue(EnterNewRoomId.value);
    }
}