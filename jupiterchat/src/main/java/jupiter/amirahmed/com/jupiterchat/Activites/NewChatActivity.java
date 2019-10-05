package jupiter.amirahmed.com.jupiterchat.Activites;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jupiter.amirahmed.com.jupiterchat.Adapters.ChatAdapter;
import jupiter.amirahmed.com.jupiterchat.Models.ChatMessage;
import jupiter.amirahmed.com.jupiterchat.R;

public class NewChatActivity extends AppCompatActivity {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private ChatAdapter adapter;

    EditText editText;

    ImageView sendbutton;

    RecyclerView recyclerView;

    String userName,senderImage,receiverName,receiverImage;

    LinearLayout chatlayout,aboutalayout;

    TextView abouttochat;

    Button startnow;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newchat);

        chatlayout = findViewById(R.id.chatlayout);
        aboutalayout = findViewById(R.id.aboulayout);

        abouttochat = findViewById(R.id.abouttostarttext);

        startnow = findViewById(R.id.startnowbutton);

        editText = findViewById(R.id.edittext);
        sendbutton = findViewById(R.id.sendbutton);

        Bundle extras = getIntent().getExtras();
        assert extras != null;

        userName = extras.getString("userName");
        senderImage = extras.getString("senderImage");
        receiverName = extras.getString("receiverName");
        receiverImage = extras.getString("receiverImage");

        aboutalayout.setVisibility(View.VISIBLE);
        chatlayout.setVisibility(View.GONE);

        startnow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                aboutalayout.setVisibility(View.GONE);
                chatlayout.setVisibility(View.VISIBLE);

                // Just if i want to chat with someone for the first time
                addChat();

            }
        });



        setUpRecyclerView();

        sendbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!editText.getText().toString().trim().isEmpty())
                {
                    sendMessage(editText.getText().toString().trim());
                }
            }
        });
    }

    public void addChat()
    {

        Map<String, Object> conversation = new HashMap<>();
        conversation.put("name", userName+"-"+receiverName);
        conversation.put("sender", userName);
        conversation.put("receiver", receiverName);
        conversation.put("receiverImage", receiverImage);
        conversation.put("timeStamp", FieldValue.serverTimestamp());

        db.collection("Conversations").document(userName+"-"+receiverName).set(conversation).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

                sendMessage("Hello " + receiverName);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
            }
        });

    }

    public void sendMessage(final String message)
    {

        if(!message.isEmpty())
        {

            List<String> seenArray = new ArrayList<>();
            seenArray.add(userName);

            Map<String, Object> messages = new HashMap<>();
            messages.put("sender", userName);
            messages.put("senderImage", senderImage);
            messages.put("seenArray",seenArray);
            messages.put("message", message);
            messages.put("timeStamp", FieldValue.serverTimestamp());

            db.collection("Conversations").document(userName +"-"+receiverName).collection("Messages").document().set(messages).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {

                    recyclerView.scrollToPosition(adapter.getItemCount() - 1);

                    editText.setText("");
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    assert imm != null;
                    imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);

                    lastMessage(message);

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                }
            });
        }

    }

    public void lastMessage(String last)
    {
        Map<String, Object> conversation = new HashMap<>();
        conversation.put("name", userName +"-"+receiverName);
        conversation.put("sender", userName);
        conversation.put("receiver", receiverName);
        conversation.put("lastMessage", last);
        conversation.put("timeStamp", FieldValue.serverTimestamp());

        db.collection("Conversations").document(userName +"-"+receiverName).update(conversation).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
            }
        });
    }

    private void setUpRecyclerView() {

        Query query3 = db.collection("Conversations").document(userName +"-"+receiverName).collection("Messages").orderBy("timeStamp", Query.Direction.ASCENDING);


        FirestoreRecyclerOptions<ChatMessage> options = new FirestoreRecyclerOptions.Builder<ChatMessage>()
                .setQuery(query3, ChatMessage.class)
                .build();

        adapter = new ChatAdapter(options);

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}
