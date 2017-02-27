package ixigo.nitin.com.buyhatkehiring.Adapter;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import ixigo.nitin.com.buyhatkehiring.CustomViews.CircularImageView;
import ixigo.nitin.com.buyhatkehiring.Modal.Message;
import ixigo.nitin.com.buyhatkehiring.R;
import ixigo.nitin.com.buyhatkehiring.SendSmsActivity;
import ixigo.nitin.com.buyhatkehiring.Util.CommonGetters;

/**
 * Created by apple on 26/02/17.
 */

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.GatewayViewHolder> {

    private List<Message> messageList;
    Context context;
    CommonGetters commonGetters;

    public MessageAdapter(List<Message> messageList, Context context) {

        this.messageList = messageList;
        this.context = context;
        commonGetters = new CommonGetters(context);

    }


    @Override
    public GatewayViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item, parent, false);


        GatewayViewHolder dataObjectHolder = new GatewayViewHolder(view);


        return dataObjectHolder;
    }

    @Override
    public void onBindViewHolder(GatewayViewHolder holder, int position) {


        holder.imageView.setImageBitmap(null);

        final Message message = messageList.get(position);

        Bitmap contactphoto = commonGetters.getContactBitmap(context, message.getAddress());
        final String contactName = commonGetters.getContactName(context, message.getAddress());

        if (contactphoto != null) {
            holder.imageView.setImageBitmap(contactphoto);

        } else {

            holder.imageView.setImageResource(R.drawable.user);
        }

        if (contactName != null && !contactName.equals("")) {
            holder.title.setText(contactName);
        } else {
            holder.title.setText(message.getAddress());
        }
        holder.message.setText(message.getMessage());
        holder.createdAt.setText(commonGetters.getFormatedDate(message.getTime()));


        holder.frameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                context.startActivity(new Intent(context, SendSmsActivity.class).putExtra("name", contactName).putExtra("number", message.getAddress()));

            }
        });


    }


    @Override
    public int getItemCount() {
        return messageList.size();
    }

    public static class GatewayViewHolder extends RecyclerView.ViewHolder {

        public FrameLayout frameLayout;
        public CircularImageView imageView;
        public TextView title;
        public TextView message;
        public TextView createdAt;


        GatewayViewHolder(View itemView) {
            super(itemView);

            frameLayout = (FrameLayout) itemView.findViewById(R.id.parent_layout);
            imageView = (CircularImageView) itemView.findViewById(R.id.profilePic);
            title = (TextView) itemView.findViewById(R.id.title);
            message = (TextView) itemView.findViewById(R.id.message);
            createdAt = (TextView) itemView.findViewById(R.id.createdAt);

        }
    }

}
