package jupiter.amirahmed.com.jupiterchat.Utils;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Objects;

public class ChatTools {

    public boolean IfExist(final String userName,final String receiverName)
    {

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        final boolean[] result = new boolean[1];

         db.collection("Conversations").orderBy("timeStamp", Query.Direction.DESCENDING).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {

                    for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                        if(document.getId().contains(userName) && document.getId().contains(receiverName))
                        {

                            result[0] = true;
                            break;
                        }else
                        {
                            result[0] = false;
                        }

                    }


                } else {
                    Log.d("t", "Error getting documents: ", task.getException());

                    result[0] = true;
                }
            }
        });

        return result[0];

    }
}
