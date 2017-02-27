package ixigo.nitin.com.buyhatkehiring.Util;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import ixigo.nitin.com.buyhatkehiring.Exceptions.NullCursorException;
import ixigo.nitin.com.buyhatkehiring.Modal.Message;
import ixigo.nitin.com.buyhatkehiring.R;

/**
 * Created by apple on 26/02/17.
 */

public class CommonGetters {
    
    
    Context context;
    Activity activity;
    
    public CommonGetters(Context context){
        
        this.context=context;
        
    }

    /**
     * Note that passing activity in constructor argument shall be avoided but in certain cases we have to do this
     * for eg we need activity instance for startManagingCursor() method as it will tie cursor lifecycle with the 
     * activity life cycle. We could have put this method in the activity itself but for the sake of good architecture and 
     * separation of concern we have not done that.
     * @param activity
     */
    public CommonGetters(Activity activity){
        
        this.activity=activity;
        
    }


    /**
     * retrieves all messages.
     * @return
     */
    public  List<Message> getAllMessage() {
        List<Message> messageList = new ArrayList<Message>();
        HashMap<String, Message> messageMapping = new HashMap<>();
        Message messageInstance = new Message();
        Uri message = Uri.parse("content://sms/");
        ContentResolver cr = activity.getContentResolver();

        Cursor cursor = cr.query(message, null, null, null, null);
        activity.startManagingCursor(cursor);
        if (cursor != null) {
            int totalMessage = cursor.getCount();

            if (cursor.moveToFirst()) {
                for (int i = 0; i < totalMessage; i++) {

                    messageInstance = makeMessageInstance(cursor);


                    if (messageInstance.getAddress() != null && !messageInstance.getAddress().isEmpty()) {
                        if (!messageMapping.containsKey(messageInstance.getAddress()))
                            messageMapping.put(messageInstance.getAddress(), messageInstance);
                    } else {

                        messageList.add(messageInstance);
                    }


                    cursor.moveToNext();

                  //  System.out.println(messageInstance);

                }


                messageList.addAll(new ArrayList<Message>(messageMapping.values()));
                sortMessagesWihTime(messageList);
            }


        } else {

            throw new NullCursorException();

        }


        return messageList;


    }

    /**
     * Sort messages in decending order of time
     * @param messageList
     */
    private void sortMessagesWihTime(List<Message> messageList) {
        Collections.sort(messageList, new Comparator<Message>() {
            public int compare(Message s1, Message s2) {

                if(s1.getTime()<s2.getTime()) {

                    return 1;

                }else if(s1.getTime()>s2.getTime()){

                    return -1;

                }else{

                    return 0;
                }
            }
        });
    }

    /**
     * make message instance from cursor
     * @param cursor
     * @return
     */
    @NonNull
    private Message makeMessageInstance(Cursor cursor) {
        Message messageInstance;
        messageInstance = new Message();
        messageInstance.setId(Long.parseLong(cursor.getString(cursor.getColumnIndexOrThrow("_id"))));
        messageInstance.setAddress(cursor.getString(cursor
                .getColumnIndexOrThrow("address")));
        messageInstance.setMessage(cursor.getString(cursor.getColumnIndexOrThrow("body")));
        messageInstance.setReadState(Integer.parseInt(cursor.getString(cursor.getColumnIndex("read"))));
        messageInstance.setTime(Long.parseLong(cursor.getString(cursor.getColumnIndexOrThrow("date"))));
        if (cursor.getString(cursor.getColumnIndexOrThrow("type")).contains("1")) {
            messageInstance.setFolderName("inbox");
        } else {
            messageInstance.setFolderName("sent");
        }
        return messageInstance;
    }


    /**
     * get messages with search
     * @param query
     * @return
     */
    public List<Message> getAllMessageWithSearch(String query) {


        long st= System.currentTimeMillis();
        List<Message> lstMessage = new ArrayList<Message>();
        HashMap<String, Message> smsMaps = new HashMap<>();
        Message objMessage = new Message();
        Uri message = Uri.parse("content://sms/");
        ContentResolver cr = activity.getContentResolver();

        Cursor cursor = cr.query(message, null, null, null, null);
        activity.startManagingCursor(cursor);

       // System.out.println("Time "+(System.currentTimeMillis()-st));

        if (cursor != null) {
            int totalMessage = cursor.getCount();

            if (cursor.moveToFirst()) {
                for (int i = 0; i < totalMessage; i++) {

                    objMessage = makeMessageInstance(cursor);
                    if (objMessage.getMessage().matches("(?i:.*" + query + ".*)") || objMessage.getAddress().matches("(?i:.*" + query + ".*)")) {
                        lstMessage.add(objMessage);
                    }


                    cursor.moveToNext();
                }

            }


        } else {
        }


        return lstMessage;


    }


    public String getFormatedDate(Long date){

        // String timestamp = getTimeStamp(message.getCreatedAt());
        DateFormat df = new SimpleDateFormat("d MMM");
        if (date != null) {
            String timestamp = df.format(date);

            return timestamp;

        } else {

            //TODO: put customexception


            return "";


             }

    }


    /**
     *
     * @param context
     * @param address
     * @return name of contact
     */
    public String getContactName(Context context, String address) {
        ContentResolver cr = context.getContentResolver();
        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(address));
        Cursor cursor = cr.query(uri, new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME, ContactsContract.PhoneLookup._ID}, null, null, null);
        if (cursor == null) {
            return null;
        }
        String contactName = null;
        if (cursor.moveToFirst()) {
            contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME));
        }
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }

        return contactName;
    }


    /**
     *
     * @param context
     * @param phoneNumber
     * @return returns Bitmap of contact
     */
    public Bitmap getContactBitmap(Context context, String phoneNumber) {
        ContentResolver cr = context.getContentResolver();
        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber));
        Cursor cursor = cr.query(uri, new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME, ContactsContract.PhoneLookup._ID}, null, null, null);
        String contactId = null;
        Bitmap photo = null;
        if (cursor == null) {
            return null;
        }
        if (cursor.moveToFirst()) {

            contactId = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.PhoneLookup._ID));


            try {
                InputStream inputStream = ContactsContract.Contacts.openContactPhotoInputStream(context.getContentResolver(),
                        ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, new Long(contactId)));

                if (inputStream != null) {
                    photo = BitmapFactory.decodeStream(inputStream);
                    inputStream.close();
                }


            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }

        return photo;
    }
    
}
