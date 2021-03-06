package ixigo.nitin.com.buyhatkehiring.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import ixigo.nitin.com.buyhatkehiring.Modal.ThreadItem;
import ixigo.nitin.com.buyhatkehiring.R;


public class ThreadAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static String TAG = ThreadAdapter.class.getSimpleName();

    private String userId = "1";
    private int SELF = 100;
    private static String today;

    private Context mContext;
    private ArrayList<ThreadItem> ThreadItemArrayList;

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView ThreadItem, timestamp;

        public ViewHolder(View view) {
            super(view);
            ThreadItem = (TextView) itemView.findViewById(R.id.message);
            timestamp = (TextView) itemView.findViewById(R.id.timestamp);
        }
    }


    public ThreadAdapter(Context mContext, ArrayList<ThreadItem> ThreadItemArrayList, String userId) {
        this.mContext = mContext;
        this.ThreadItemArrayList = ThreadItemArrayList;
        this.userId = userId;

        Calendar calendar = Calendar.getInstance();
        today = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;


        if (viewType == SELF) {

            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_item_aichat, parent, false);
        } else {

            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_item_chat, parent, false);
        }


        return new ViewHolder(itemView);
    }


    @Override
    public int getItemViewType(int position) {
        ThreadItem ThreadItem = ThreadItemArrayList.get(position);
        if (ThreadItem.getUser().equalsIgnoreCase(userId)) {
            return SELF;
        }

        return position;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        ThreadItem ThreadItem = ThreadItemArrayList.get(position);
        ((ViewHolder) holder).ThreadItem.setText(ThreadItem.getMessage());

        // String timestamp = getTimeStamp(ThreadItem.getCreatedAt());
        DateFormat df = new SimpleDateFormat("EEE, d MMM yyyy, HH:mm");
        if (ThreadItem.getCreatedAt() != null && !ThreadItem.getCreatedAt().isEmpty()) {
            String timestamp = df.format(Long.parseLong(ThreadItem.getCreatedAt()));

            ((ViewHolder) holder).timestamp.setText(timestamp);
        } else {
            String timestamp = df.format(Calendar.getInstance().getTime());

            ((ViewHolder) holder).timestamp.setText(timestamp);
        }
    }

    @Override
    public int getItemCount() {
        return ThreadItemArrayList.size();
    }


}
