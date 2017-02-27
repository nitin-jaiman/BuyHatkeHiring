package ixigo.nitin.com.buyhatkehiring;

import android.Manifest;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.MetadataChangeSet;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.List;

import ixigo.nitin.com.buyhatkehiring.Adapter.MessageAdapter;
import ixigo.nitin.com.buyhatkehiring.Modal.Message;
import ixigo.nitin.com.buyhatkehiring.Util.CommonGetters;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener{


    private static final int PERMISSION_SMS_READ_REQUEST_CODE = 1;
    private static final int PERMISSION_SMS_SEND_REQUEST_CODE = 2;
    private static final int REQUEST_CODE_RESOLUTION = 3;
    private static final int REQUEST_CODE_CREATOR = 4;
    CommonGetters commonGetters;
    private RecyclerView recyclerView;
    private MessageAdapter messageAdapter;
    private FloatingActionButton send;
    MaterialSearchView searchView;
    Toolbar toolbar;

    private GoogleApiClient mGoogleApiClient;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        commonGetters=new CommonGetters(this);


        recyclerView = (RecyclerView) findViewById(R.id.recyclerViewMsg);
        send = (FloatingActionButton) findViewById(R.id.floatingActionButtonSendSms);
        searchView= (MaterialSearchView) findViewById(R.id.search_view);
        toolbar= (Toolbar) findViewById(R.id.toolbar);
        requestRuntimePermission();

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Messages");


        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.RECEIVE_SMS) == PackageManager.PERMISSION_GRANTED) {

                    startActivity(new Intent(MainActivity.this, SendSmsActivity.class));


                }else{

                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_SMS, Manifest.permission.READ_CONTACTS, Manifest.permission.RECEIVE_SMS}, PERMISSION_SMS_READ_REQUEST_CODE);


                }


            }
        });

        initializeSearchView();


    }


    /**
     * this method initializes searchview and adds listeners to it
     */
    private void initializeSearchView(){




        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(final String query) {


                new AsyncTask<String, Void, List<Message>>() {
                    @Override
                    protected List<Message> doInBackground(String... params) {

                        String querry=params[0];
                        List<Message> messageList=  commonGetters.getAllMessageWithSearch(querry);


                        return messageList;

                    }

                    @Override
                    protected void onPostExecute(List<Message> messages) {

                        messageAdapter = new MessageAdapter(messages,MainActivity.this);
                        recyclerView.setAdapter(messageAdapter);
                    }
                }.execute(query);



                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                new AsyncTask<String, Void, List<Message>>() {
                    @Override
                    protected List<Message> doInBackground(String... params) {

                        String querry=params[0];
                        List<Message> messageList=  commonGetters.getAllMessageWithSearch(querry);


                        return messageList;

                    }

                    @Override
                    protected void onPostExecute(List<Message> messages) {

                        messageAdapter = new MessageAdapter(messages,MainActivity.this);
                        recyclerView.setAdapter(messageAdapter);
                    }
                }.execute(newText);


                return false;
            }
        });

        searchView.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() {
                //Do some magic
            }

            @Override
            public void onSearchViewClosed() {

                List<Message> messageList=  commonGetters.getAllMessageWithSearch("");

                messageAdapter = new MessageAdapter(messageList,MainActivity.this);
                recyclerView.setAdapter(messageAdapter);


            }
        });

    }

    /**
     * Requests runtime permission
     */
    private void requestRuntimePermission(){


        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_SMS, Manifest.permission.READ_CONTACTS, Manifest.permission.RECEIVE_SMS}, PERMISSION_SMS_READ_REQUEST_CODE);


            return;
        }else{
            buildAdapter();

        }

    }

    /**
     * Make Adapter
     */
    private void buildAdapter(){

       List<Message> listMessage= commonGetters.getAllMessage();

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        messageAdapter = new MessageAdapter(listMessage,this);
        recyclerView.setAdapter(messageAdapter);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);

        MenuItem item = menu.findItem(R.id.action_search);
        searchView.setMenuItem(item);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id=item.getItemId();

        if(id==R.id.backup){


            connectWithGoogle();

        }


        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {


            case PERMISSION_SMS_READ_REQUEST_CODE: {
                if (grantResults.length > 0 && grantResults[1] == PackageManager.PERMISSION_GRANTED) {

                    buildAdapter();


                } else {


                    Toast.makeText(MainActivity.this, "Permission required to access messages", Toast.LENGTH_SHORT).show();

                }
            }
            break;

            case PERMISSION_SMS_SEND_REQUEST_CODE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startActivity(new Intent(MainActivity.this, SendSmsActivity.class));

                } else {


                    Toast.makeText(MainActivity.this, "Permission required to access messages", Toast.LENGTH_SHORT).show();

                }
            }
            break;


        }

    }


    private void connectWithGoogle(){

        if (mGoogleApiClient == null) {

            /**
             * Create the API client and bind it to an instance variable.
             * We use this instance as the callback for connection and connection failures.
             * Since no account name is passed, the user is prompted to choose.
             */
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(Drive.API)
                    .addScope(Drive.SCOPE_FILE)
                    .addConnectionCallbacks(MainActivity.this)
                    .addOnConnectionFailedListener(this)
                    .build();
        }

        mGoogleApiClient.connect();


    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d("coonection", "coonected");
        Drive.DriveApi.newDriveContents(mGoogleApiClient)
                .setResultCallback(driveContentsCallback);
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d("coonection", "suspended" + i);
    }

    @Override
    protected void onPause() {
        if (mGoogleApiClient != null) {

            // disconnect Google API client connection
            mGoogleApiClient.disconnect();
        }

        super.onPause();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        if (!connectionResult.hasResolution()) {

            // show the localized error dialog.
            GoogleApiAvailability.getInstance().getErrorDialog(this, connectionResult.getErrorCode(), 0).show();
            return;
        }

        /**
         *  The failure has a resolution. Resolve it.
         *  Called typically when the app is not yet authorized, and an  authorization
         *  dialog is displayed to the user.
         */

        try {

            connectionResult.startResolutionForResult(this, REQUEST_CODE_RESOLUTION);

        } catch (IntentSender.SendIntentException e) {


        }
    }


    public void showMessage(String message) {
        Log.d("message", message);
        Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
    }

    final private ResultCallback<DriveApi.DriveContentsResult> driveContentsCallback = new
            ResultCallback<DriveApi.DriveContentsResult>() {
                @Override
                public void onResult(DriveApi.DriveContentsResult result) {
                    if (!result.getStatus().isSuccess()) {
                        showMessage("Error while trying to create new file contents");
                        return;
                    }
                    final DriveContents driveContents = result.getDriveContents();

                    // Perform I/O off the UI thread.
                    new Thread() {
                        @Override
                        public void run() {
                            // write content to DriveContents
                            OutputStream outputStream = driveContents.getOutputStream();
                            Writer writer = new OutputStreamWriter(outputStream);
                            try {
                                List<Message> smslist = commonGetters.getAllMessage();
                                writer.write("Messages");
                                if (smslist != null && smslist.size() > 0) {
                                    for (int i = 0; i < smslist.size(); i++) {
                                        writer.append("From: " + smslist.get(i).getAddress() + "\n");
                                        writer.append("At: " + smslist.get(i).getTime() + "\n");
                                        writer.append("Message: " + smslist.get(i).getMessage() + "\n\n");

                                    }


                                }

                                writer.close();
                            } catch (IOException e) {
                            e.printStackTrace();

                            }

                            MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
                                    .setTitle("smsfile")
                                    .setMimeType("text/plain")
                                    .setStarred(true).build();

                            // create a file on root folder
                            Drive.DriveApi.getRootFolder(mGoogleApiClient)
                                    .createFile(mGoogleApiClient, changeSet, driveContents)
                                    .setResultCallback(fileCallback);
                        }
                    }.start();
                }
            };

    final private ResultCallback<DriveFolder.DriveFileResult> fileCallback = new
            ResultCallback<DriveFolder.DriveFileResult>() {
                @Override
                public void onResult(DriveFolder.DriveFileResult result) {
                    if (!result.getStatus().isSuccess()) {
                        showMessage("Error while trying to create the file");
                        return;
                    }
                    showMessage("Created a file with content: " + result.getDriveFile().getDriveId());
                }
            };

}
