package com.example.s3k_user1.appzonas;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.example.s3k_user1.appzonas.fragment.StoreFragment;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MiFirebaseMessagingService extends FirebaseMessagingService {

    public static final String TAG = "NOTICIAS";
    public String titulo ="";
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        String from = remoteMessage.getFrom();
        Log.w(TAG, "Mensaje recibido de: " + from);
        String ntBody="0";
        String ntTitle="0";
        String ntData="0";

        String codigo = "0";
        String nombre = "0";

        if (remoteMessage.getNotification() != null) {
            Log.w(TAG, "Notificacion Data: " + remoteMessage.getData().toString());
            Log.w(TAG, "Notificacion Body: " + remoteMessage.getNotification().getBody());
            Log.w(TAG, "Notificacion Title: " + remoteMessage.getNotification().getTitle());
            titulo = remoteMessage.getNotification().getTitle();
            //MapsActivity.
            ntTitle = remoteMessage.getNotification().getTitle();
            ntBody = remoteMessage.getNotification().getBody();

        }

        if (remoteMessage.getData().size() > 0) {
            Log.w(TAG, "Data: " + remoteMessage.getData());
            codigo = remoteMessage.getData().get("codigo");
            nombre = remoteMessage.getData().get("nombreUsuario");
        }
        mostrarNotificacion(ntTitle,ntBody,codigo,nombre);
    }

    private void mostrarNotificacion(String title, String body,String codigo, String nombre) {

        Intent intent = new Intent(this, StoreFragment.class);

        Intent intents = new Intent("com.example.s3k_user1.appzonas_FCM");

        intents.putExtra("codigo", codigo);
        intents.putExtra("nombreUsuario", nombre);

        LocalBroadcastManager localBroadcastManager =LocalBroadcastManager.getInstance(this);
        localBroadcastManager.sendBroadcast(intents);

        intent.putExtra("codigo", codigo);
        intent.putExtra("nombreUsuario", nombre);

        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
        //FirebaseMessaging.getInstance().subscribeToTopic("all");
        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);



        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_event)
                .setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(true)

                .setSound(soundUri)
                .setContentIntent(pendingIntent);


        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, notificationBuilder.build());

    }

}