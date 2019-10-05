package jupiter.amirahmed.com.jupiterchat.Activites;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import java.util.List;
import java.util.Objects;

import jupiter.amirahmed.com.jupiterchat.Adapters.ConversationsAdapter;
import jupiter.amirahmed.com.jupiterchat.Models.ConversationItem;
import jupiter.amirahmed.com.jupiterchat.R;
import jupiter.amirahmed.com.jupiterchat.Utils.TinyDB;

public class ConversationsActivity extends AppCompatActivity {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    RecyclerView recyclerView;

    List<ConversationItem> list = new ArrayList<>();

    ConversationsAdapter adapter;

    String userName,senderImage;

    TinyDB tinyDB;

    // Act as a Home Activity for Chats

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversations);

        tinyDB = new TinyDB(this);

        Bundle extras = getIntent().getExtras();
        assert extras != null;

        userName = extras.getString("userName");
        senderImage = extras.getString("senderImage");

        tinyDB.putString("userName",userName);

        setup();

    }

    public void setup()
    {

        db.collection("Conversations").orderBy("timeStamp", Query.Direction.DESCENDING).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {

                    for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                        if(document.getId().contains(userName))
                        {
                            ConversationItem item = new ConversationItem();

                            Date currentTime = Calendar.getInstance().getTime();

                            item.setName(document.getId());
                            item.setReceiver(Objects.requireNonNull(document.get("receiver")).toString());
                            item.setSender(Objects.requireNonNull(document.get("sender")).toString());
                            item.setLastMessage(Objects.requireNonNull(document.get("lastMessage")).toString());
                            try {
                                item.setTimestamp(Objects.requireNonNull(document.getDate("timeStamp")));
                            }catch (Exception e)
                            {
                                item.setTimestamp(currentTime);
                            }


                            list.add(item);
                        }

                    }

                    adapter = new ConversationsAdapter(list,userName,senderImage);

                    recyclerView = findViewById(R.id.rv);
                    recyclerView.setHasFixedSize(true);

                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ConversationsActivity.this);

                    recyclerView.setLayoutManager(linearLayoutManager);
                    recyclerView.setAdapter(adapter);


                } else {
                    Log.d("t", "Error getting documents: ", task.getException());
                }
            }
        });

    }
}
