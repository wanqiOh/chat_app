package com.example.tractive_chat_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseListOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import java.util.Objects;

public class ChatActivity extends AppCompatActivity {

    ImageView sendImage;
    EditText userMessage;
    FirebaseAuth mAuth;
    DatabaseReference mChatData, mUserData;
    String username = "Wanqi";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        mAuth = FirebaseAuth.getInstance();

        sendImage = findViewById(R.id.send_image);
        userMessage = findViewById(R.id.user_message);

        mChatData = FirebaseDatabase.getInstance().getReference().child("chats");
        mUserData = FirebaseDatabase.getInstance().getReference().child(Objects.requireNonNull(mAuth.getUid()));

        displayChatMessage();

        mUserData.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                username = Objects.requireNonNull(dataSnapshot.child("username").getValue()).toString();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        sendImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(userMessage.getText() != null){
                    if(!userMessage.getText().toString().equals("") && username != null){
                        mChatData.push().setValue(new Model_Chat(userMessage.getText().toString(),username,System.currentTimeMillis()));
                    }
                }
            }
        });

    }

    private void displayChatMessage() {
        ListView listOfMessage = findViewById(R.id.list_message);
        //The error said the constructor expected FirebaseListOptions - here you create them:
        FirebaseListOptions<Model_Chat> options = new FirebaseListOptions.Builder<Model_Chat>()
                .setQuery(FirebaseDatabase.getInstance("https://chat-f35b3-default-rtdb.asia-southeast1.firebasedatabase.app").getReference().child("chats").limitToLast(100), Model_Chat.class)
                .setLayout(R.layout.list_msg)
                .build();
        FirebaseListAdapter<Model_Chat> adapter = new FirebaseListAdapter<Model_Chat> (options) {
            @Override
            protected void populateView(View v, Model_Chat model, int position) {
                TextView messageText, messageUser, messageTime;
                messageText = v.findViewById(R.id.message_text);
                messageUser = v.findViewById(R.id.message_user);
                messageTime = v.findViewById(R.id.message_time);

                messageUser.setText(model.getName());
                messageTime.setText(DateFormat.format("dd-MM-yyyy (HH:mm:ss)", model.getTimestamp()));
                messageText.setText(model.getMessage());
            }
        };
        listOfMessage.setAdapter(adapter);
    }


}
