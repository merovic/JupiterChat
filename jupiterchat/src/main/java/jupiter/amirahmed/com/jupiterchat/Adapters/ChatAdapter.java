package jupiter.amirahmed.com.jupiterchat.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.bumptech.glide.Glide;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.github.curioustechizen.ago.RelativeTimeTextView;

import java.util.Calendar;
import java.util.Date;

import jupiter.amirahmed.com.jupiterchat.Models.ChatMessage;
import jupiter.amirahmed.com.jupiterchat.R;
import jupiter.amirahmed.com.jupiterchat.Utils.TinyDB;

public class ChatAdapter extends FirestoreRecyclerAdapter<ChatMessage, ChatAdapter.NoteHolder> {

    private Context context;

    private TinyDB tinyDB;

    public ChatAdapter(@NonNull FirestoreRecyclerOptions<ChatMessage> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull final NoteHolder holder, int position, @NonNull final ChatMessage model) {

        holder.message.setText(model.getMessage());
        Glide.with(context).load(model.getUser_image()).placeholder(R.drawable.ic_person_black_24dp).into(holder.image);

        Date currentTime = Calendar.getInstance().getTime();

        try {

            holder.date.setReferenceTime(model.getTimestamp().getTime());

        }catch (Exception e)
        {

            holder.date.setReferenceTime(currentTime.getTime());
        }


        if(model.getSender().equals(tinyDB.getString("userName")))
        {
            holder.layout.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
            holder.message.setBackgroundResource(R.drawable.rounded_button);
        }else
        {
            holder.layout.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
            holder.message.setBackgroundResource(R.drawable.rounded_button2);
        }

    }


    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @Override
    public void onDataChanged() {
        super.onDataChanged();
    }


    @NonNull
    @Override
    public NoteHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        context = parent.getContext();

        tinyDB = new TinyDB(context);

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.note_item, parent, false);
        return new NoteHolder(v);
    }

    class NoteHolder extends RecyclerView.ViewHolder {

        LinearLayout layout;
        TextView message;
        ImageView image;
        RelativeTimeTextView date;

        NoteHolder(View itemView) {
            super(itemView);
            layout = itemView.findViewById(R.id.layout);
            message = itemView.findViewById(R.id.message);
            image = itemView.findViewById(R.id.image);
            date = itemView.findViewById(R.id.date);
        }
    }
}