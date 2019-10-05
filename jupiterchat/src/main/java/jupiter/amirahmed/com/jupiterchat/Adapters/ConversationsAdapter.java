package jupiter.amirahmed.com.jupiterchat.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.github.curioustechizen.ago.RelativeTimeTextView;


import java.util.List;

import jupiter.amirahmed.com.jupiterchat.Activites.ContinueChatActivity;
import jupiter.amirahmed.com.jupiterchat.Models.ConversationItem;
import jupiter.amirahmed.com.jupiterchat.R;

public class ConversationsAdapter extends RecyclerView.Adapter<ConversationsAdapter.ConversationsViewHolder> {

    private List<ConversationItem> conversationItems;

    private Context context;

    private String userName,senderImage;

    public ConversationsAdapter(List<ConversationItem> conversationItems,String userName,String senderImage) {
        this.conversationItems = conversationItems;
        this.userName = userName;
        this.senderImage = senderImage;

    }

    @NonNull
    @Override
    public ConversationsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {

        context = parent.getContext();

        View view;

        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.menu_item, parent, false);

        return new ConversationsAdapter.ConversationsViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull final ConversationsViewHolder holder, @SuppressLint("RecyclerView") final int position) {

        if(conversationItems.get(position).getSender().equals(userName))
        {
            holder.username.setText(conversationItems.get(position).getReceiver());
        }else
        {
            holder.username.setText(conversationItems.get(position).getSender());
        }

        holder.lastmessage.setText(conversationItems.get(position).getLastMessage());

        holder.date.setReferenceTime(conversationItems.get(position).getTimestamp().getTime());

        Glide.with(context).load(conversationItems.get(position).getReceiverImage()).placeholder(R.drawable.ic_person_black_24dp).into(holder.userimage);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(context , ContinueChatActivity.class);
                intent.putExtra("ConversationName",conversationItems.get(position).getName());

                if(conversationItems.get(position).getSender().equals(userName))
                {
                    intent.putExtra("Sender",conversationItems.get(position).getSender());
                    intent.putExtra("Receiver",conversationItems.get(position).getReceiver());

                    intent.putExtra("SenderImage",senderImage);
                    intent.putExtra("ReceiverImage",conversationItems.get(position).getReceiverImage());

                }else
                {
                    intent.putExtra("Sender",conversationItems.get(position).getReceiver());
                    intent.putExtra("Receiver",conversationItems.get(position).getSender());

                    intent.putExtra("SenderImage",conversationItems.get(position).getReceiverImage());
                    intent.putExtra("ReceiverImage",senderImage);
                }


                context.startActivity(intent);
            }
        });
    }


    @Override
    public int getItemCount() {
        return conversationItems.size();
    }

    class ConversationsViewHolder extends RecyclerView.ViewHolder {

        ImageView userimage;
        TextView username,lastmessage;
        RelativeTimeTextView date;

        ConversationsViewHolder(@NonNull View itemView) {
            super(itemView);

            userimage = itemView.findViewById(R.id.userimage);
            username = itemView.findViewById(R.id.username);
            lastmessage = itemView.findViewById(R.id.lastmessage);
            date = itemView.findViewById(R.id.date);
        }
    }
}
