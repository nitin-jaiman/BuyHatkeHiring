package ixigo.nitin.com.buyhatkehiring.Receivers;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.telephony.SmsMessage;

import ixigo.nitin.com.buyhatkehiring.MainActivity;
import ixigo.nitin.com.buyhatkehiring.R;

/**
 * Created by apple on 26/02/17.
 */

public class SMSBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        SmsMessage[] msgs = null;
        String str = "";
        if (bundle != null) {

            Object[] pdus = (Object[]) bundle.get("pdus");
            msgs = new SmsMessage[pdus.length];
            for (int i = 0; i < msgs.length; i++) {

                str = makeMessageString(msgs, str, pdus, i);



            }
            sendNotification(context, str);


        }
    }

    /**
     * Use StringBuilder if lot of concatination is happening as its faster than StringBuffer and mutable
     * @param msgs
     * @param str
     * @param pdus
     * @param i
     * @return
     */
    @NonNull
    private String makeMessageString(SmsMessage[] msgs, String str, Object[] pdus, int i) {


        msgs[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
        StringBuilder stringBuilder=new StringBuilder(str);

        stringBuilder.append("SMS from Phone No: " + msgs[i].getOriginatingAddress())
                .append("\n" + "Message is: ")
                .append(msgs[i].getMessageBody().toString())
        .append("\n");

        return stringBuilder.toString();
    }

    /**
     * make a pendingintent for notification
     * @param context
     * @param str
     */
    private void sendNotification(Context context, String str) {
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle("New Message")
                        .setContentText(str);

        Intent notificationIntent = new Intent(context, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(contentIntent);

        // Add as notification
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(0, builder.build());
    }
}
