package com.jogjaraya.id.service;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.text.Html;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.jogjaraya.id.R;
import com.jogjaraya.id.activity.DetailAgendaActivity;
import com.jogjaraya.id.activity.DetailArtikelActivity;
import com.jogjaraya.id.activity.DetailJelajahActivity;
import com.jogjaraya.id.activity.ListAgendaActivity;
import com.jogjaraya.id.activity.MainActivity;

import java.util.EventListener;
import java.util.Map;
import java.util.Random;

public class FCMService extends FirebaseMessagingService {
    private static final String TAG = "FCMService";
    private static final String TITLE = "title";
    private static final String EMPTY = "";
    private static final String MESSAGE = "message";
    private static final String IMAGE = "image";
    private static final String ACTION = "action";
    private static final String ID_PESAN = "id_pesan";
    private static final String DATA = "data";
    private static final String ACTION_DESTINATION = "action_destination";
    private static final String ID = "id";
    private static final String BODY = "body";
    private static final String STREAM_ID = "stream_id";
    private static final String SLUG = "tulisan_slug";
    private static final String LISTING_ID = "listing_id";
    private static final String KODE_NOTIF = "kode_notif";
    private static final String EVENt_ID = "event_id";

    private static final String TAG1 = "MyFirebaseIdService";
    private static final String TOPIC_GLOBAL = "global";


    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG1, "Refreshed token: " + refreshedToken);

        // now subscribe to `global` topic to receive app wide notifications
        FirebaseMessaging.getInstance().subscribeToTopic(TOPIC_GLOBAL);

        sendRegistrationToServer(refreshedToken);
    }

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
  }

    private void sendRegistrationToServer(String token) {
        // TODO: Implement this method to send token to your app server.
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        onTokenRefresh();
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
            Map<String, String> data = remoteMessage.getData();
            handleData(data);

        } else if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
            handleNotification(remoteMessage.getNotification());
        }
        // Check if message contains a notification payload.

    }

    private void handleNotification(RemoteMessage.Notification RemoteMsgNotification) {
        String message = RemoteMsgNotification.getBody();
        String title = RemoteMsgNotification.getTitle();
        NotificationVO notificationVO = new NotificationVO();
        notificationVO.setTitle(title);
        notificationVO.setMessage(message);

        Intent resultIntent = new Intent(getApplicationContext(), MainActivity.class);
        NotificationUtils notificationUtils = new NotificationUtils(getApplicationContext());
        notificationUtils.displayNotification(notificationVO, resultIntent);
    }

    private void handleData(Map<String, String> data) {
        String title = data.get(TITLE);
        String message = data.get(MESSAGE);
        String iconUrl = data.get(IMAGE);
        String id = data.get(ID);
        String body = data.get(BODY);
        String stream_id = data.get(STREAM_ID);
        String slug = data.get(SLUG);
        String listing_id = data.get(LISTING_ID);
        String kode_notif = data.get(KODE_NOTIF);
        String event_id = data.get(EVENt_ID);

        NotificationVO notificationVO = new NotificationVO();
        notificationVO.setTitle(title);
        notificationVO.setMessage(message);
        notificationVO.setIconUrl(iconUrl);
        Intent resultIntent = null;
        NotificationManager notificationManager = (NotificationManager)
                getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
//        1 -> artikel/ensiklo
//        2 -> jelajah/direktory
//        3 -> agenda/event
//        4 -> umum
        if (kode_notif != null) {
            if (kode_notif.equalsIgnoreCase("1") ) {
                resultIntent = new Intent(getApplicationContext(), DetailArtikelActivity.class);
                resultIntent.putExtra("NotifId", "com.google.firebase.MESSAGING_EVENT");
                resultIntent.putExtra("slug", slug);
                resultIntent.putExtra("from", "artikel");
                resultIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            } else if (kode_notif.equalsIgnoreCase("2")) {
                resultIntent = new Intent(getApplicationContext(), DetailJelajahActivity.class);
                resultIntent.putExtra("NotifId", "com.google.firebase.MESSAGING_EVENT");
                resultIntent.putExtra("listing_id", listing_id);
                resultIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            }else if (kode_notif.equalsIgnoreCase("3")) {
                resultIntent = new Intent(getApplicationContext(), DetailAgendaActivity.class);
                resultIntent.putExtra("NotifId", "com.google.firebase.MESSAGING_EVENT");
                resultIntent.putExtra("event_id", event_id);
                resultIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            }else {
                resultIntent = new Intent(getApplicationContext(), MainActivity.class);
            }
        }

        Random random = new Random();
        int m = random.nextInt(9999 - 1000) + 1000;

        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), m, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Bitmap bigIcon = BitmapFactory.decodeResource(getApplicationContext().getResources(),
                R.mipmap.ic_logo);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext());
        builder.setContentIntent(pendingIntent);
        builder.setSmallIcon(R.mipmap.ic_logo);
        builder.setContentTitle(title);
        builder.setContentText(Html.fromHtml(message));

        builder.setPriority(NotificationCompat.PRIORITY_MAX);
        builder.setLargeIcon(bigIcon);
        builder.setSound(alarmSound);
        builder.setAutoCancel(true);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            String channelId = "com.racikafalah";
            builder.setChannelId(channelId);
            NotificationChannel mChannel = new NotificationChannel(channelId, "racikafalah", importance);
            notificationManager.createNotificationChannel(mChannel);
        }
        notificationManager.notify(m, builder.build());
    }


}

