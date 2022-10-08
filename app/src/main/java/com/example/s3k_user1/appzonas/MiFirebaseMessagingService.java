package com.example.s3k_user1.appzonas;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.ProcessLifecycleOwner;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.s3k_user1.appzonas.services.OreoNotification;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.List;
import java.util.Map;

public class MiFirebaseMessagingService extends FirebaseMessagingService {

    //tutorial
    // https://www.codesolution.co.in/detail/post/firebase-notification-foreground-and-background-then-kill-application-with-oreo-update
    private static final String CHANNEL_ID = "Bestmarts";
    private static final String CHANNEL_NAME = "Bestmarts";
    private static final String TAG = "MessagingService";

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            sendNotification1(remoteMessage);
        }else{
            sendNotification(remoteMessage);
        }
    }


    private void sendNotification(RemoteMessage remoteMessage) {
        String title = null;
        String body = null;
        String codigo = null;
        String nombre = null;

        if (!isAppIsInBackground(getApplicationContext())) {
            //foreground app
            Log.e(" foreground", remoteMessage.getData().toString());

            //foreground app
            if (remoteMessage.getNotification() != null) {
                Log.w(TAG, "sendNotification Data: " + remoteMessage.getData().toString());
                Log.w(TAG, "sendNotification  Body: " + remoteMessage.getNotification().getBody());
                Log.w(TAG, "sendNotification Title: " + remoteMessage.getNotification().getTitle());

                //WebTokenActivity.
                title = remoteMessage.getNotification().getTitle();
                body = remoteMessage.getNotification().getBody();

            }

            if (remoteMessage.getData().size() > 0) {
                Log.w(TAG, "Data: " + remoteMessage.getData());
                codigo = remoteMessage.getData().get("codigo");
                nombre = remoteMessage.getData().get("nombreUsuario");
            }


            Intent resultIntent = new Intent(getApplicationContext() , WebTokenActivity.class);
            resultIntent.putExtra("codigo", codigo);
            resultIntent.putExtra("nombreUsuario", nombre);
            resultIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            //
//        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

//            PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(),
//                    0 /* Request code */, resultIntent,
//                    PendingIntent.FLAG_UPDATE_CURRENT);
//


        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, resultIntent, PendingIntent.FLAG_ONE_SHOT);
//        //FirebaseMessaging.getInstance().subscribeToTopic("all");
//        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);


            Intent intents = new Intent("com.example.s3k_user1.appzonas_FCM");

            intents.putExtra("codigo", codigo);
            intents.putExtra("nombreUsuario", nombre);

            LocalBroadcastManager localBroadcastManager =LocalBroadcastManager.getInstance(this);
            localBroadcastManager.sendBroadcast(intents);

            NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);

            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID);
            notificationBuilder.setAutoCancel(true)
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setWhen(System.currentTimeMillis())
                    .setSmallIcon(R.drawable.ic_chat_black)
                    .setBadgeIconType(NotificationCompat.BADGE_ICON_SMALL)
                    .setNumber(10)
                    .setTicker("Bestmarts")
                    .setContentTitle(title)
                    .setContentText(body)
                    .setContentInfo("Info")
                    .setContentIntent(pendingIntent);

            notificationManager.notify(1, notificationBuilder.build());
        }else{
            Log.e("background", remoteMessage.getData().toString());
            Map<String, String> data = remoteMessage.getData();
             title = data.get("codigo");

             body = data.get("nombreUsuario");
            Intent resultIntent = new Intent(getApplicationContext() , WebTokenActivity.class);
            resultIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(),
                    0 /* Request code */, resultIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
            NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);

            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID);
            notificationBuilder.setAutoCancel(true)
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setWhen(System.currentTimeMillis())
                    .setSmallIcon(R.drawable.ic_sync_black)
                    .setBadgeIconType(NotificationCompat.BADGE_ICON_SMALL)
                    .setNumber(10)
                    .setTicker("Bestmarts")
                    .setContentTitle(title)
                    .setContentText(body)
                    .setContentInfo("Info");
            notificationManager.notify(1, notificationBuilder.build());
        }
    }

    public static boolean isAppIsInBackground(Context context) {
        boolean isInBackground = true;
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
            List<ActivityManager.RunningAppProcessInfo> runningProcesses = am.getRunningAppProcesses();
            for (ActivityManager.RunningAppProcessInfo processInfo : runningProcesses) {
                if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    for (String activeProcess : processInfo.pkgList) {
                        if (activeProcess.equals(context.getPackageName())) {
                            isInBackground = false;
                        }
                    }
                }
            }
        } else {

            List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
            ComponentName componentInfo = taskInfo.get(0).topActivity;
            if (componentInfo.getPackageName().equals(context.getPackageName())) {
                isInBackground = false;
            }
        }

        return isInBackground;
    }

//    //Check if app is in background
//    ProcessLifecycleOwner.get().getLifecycle().getCurrentState() == Lifecycle.State.CREATED;
//
//    //Check if app is in foreground
//    ProcessLifecycleOwner.get().getLifecycle().getCurrentState().isAtLeast(Lifecycle.State.STARTED);

    public static boolean isAppinForegroundNewWay(){
        boolean isForeground=false;
        if(ProcessLifecycleOwner.get().getLifecycle().getCurrentState().isAtLeast(Lifecycle.State.STARTED)){
            isForeground=true;
        }else {
            isForeground=false;
        }
        return false;
    }

    public static boolean isAppinBackgroundNewWay(){
        boolean isBackground=false;
        if(ProcessLifecycleOwner.get().getLifecycle().getCurrentState() == Lifecycle.State.CREATED){
            isBackground=true;
            Log.e("EEE","appbackground");
        }else {
            isBackground=false;
        }
        return isBackground;
    }


    @SuppressLint("NewApi")
    private void sendNotification1(RemoteMessage remoteMessage) {
        String title = null;
        String body = null;
        String codigo = null;
        String nombre = null;
        if (!isAppinBackgroundNewWay()) {

            //foreground app
            if (remoteMessage.getNotification() != null) {
            Log.w(TAG, "Notificacion Data: " + remoteMessage.getData().toString());
            Log.w(TAG, "Notificacion Body: " + remoteMessage.getNotification().getBody());
            Log.w(TAG, "Notificacion Title: " + remoteMessage.getNotification().getTitle());

            //WebTokenActivity.
                title = remoteMessage.getNotification().getTitle();
                body = remoteMessage.getNotification().getBody();

            }

            if (remoteMessage.getData().size() > 0) {
                Log.w(TAG, "Data: " + remoteMessage.getData());
                codigo = remoteMessage.getData().get("codigo");
                nombre = remoteMessage.getData().get("nombreUsuario");
            }

            Log.e("remoteMessage", remoteMessage.getData().toString());

            Intent resultIntent = new Intent(getApplicationContext(), WebTokenActivity.class);
            resultIntent.putExtra("codigo", codigo);
            resultIntent.putExtra("nombreUsuario", nombre);
            resultIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            resultIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(),
                    0 /* Request code */, resultIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT);

            Intent intents = new Intent("com.example.s3k_user1.appzonas_FCM");

            intents.putExtra("codigo", codigo);
            intents.putExtra("nombreUsuario", nombre);

            LocalBroadcastManager localBroadcastManager =LocalBroadcastManager.getInstance(this);
            localBroadcastManager.sendBroadcast(intents);


            Uri defaultsound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

            OreoNotification oreoNotification = new OreoNotification(this);
            Notification.Builder builder = oreoNotification.getOreoNotification(
                    title,
                    body,
                    pendingIntent,
                    defaultsound,
                    (R.drawable.ic_location_on_black_24dp));

            int i = 0;
            oreoNotification.getManager().notify(i, builder.build());
        }else{
            Log.e("remoteMessage", remoteMessage.getData().toString());
             title = remoteMessage.getData().get("nombreUsuario");

             body = remoteMessage.getData().get("codigo");

             codigo = remoteMessage.getData().get("nombreUsuario");

            Intent resultIntent = new Intent(getApplicationContext(), WebTokenActivity.class);

            resultIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(),
                    0 /* Request code */, resultIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
            Uri defaultsound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

            OreoNotification oreoNotification = new OreoNotification(this);
            Notification.Builder builder = oreoNotification.getOreoNotification(
                    title,
                    body,
                    pendingIntent,
                    defaultsound,
                    (R.drawable.ic_notifications_black_24dp));

//            NotificationManager notificationManager =
//                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            int i = 0;
            oreoNotification.getManager().notify(i, builder.build());
        }

    }

//TODO METODO DE ENVIO DE NOTIFICACIONES ANTERIORES

//    public static final String TAG = "NOTICIAS";
//    public String titulo ="";
//    @Override
//    public void onMessageReceived(RemoteMessage remoteMessage) {
//        super.onMessageReceived(remoteMessage);
//
//        String from = remoteMessage.getFrom();
//        Log.w(TAG, "Mensaje recibido de: " + from);
//        String ntBody="0";
//        String ntTitle="0";
//        String ntData="0";
//
//        String codigo = "0";
//        String nombre = "0";
//
//        if (remoteMessage.getNotification() != null) {
//            Log.w(TAG, "Notificacion Data: " + remoteMessage.getData().toString());
//            Log.w(TAG, "Notificacion Body: " + remoteMessage.getNotification().getBody());
//            Log.w(TAG, "Notificacion Title: " + remoteMessage.getNotification().getTitle());
//            titulo = remoteMessage.getNotification().getTitle();
//            //WebTokenActivity.
//            ntTitle = remoteMessage.getNotification().getTitle();
//            ntBody = remoteMessage.getNotification().getBody();
//
//        }
//
//        if (remoteMessage.getData().size() > 0) {
//            Log.w(TAG, "Data: " + remoteMessage.getData());
//            codigo = remoteMessage.getData().get("codigo");
//            nombre = remoteMessage.getData().get("nombreUsuario");
//        }
//        mostrarNotificacion(ntTitle,ntBody,codigo,nombre);
//    }
//
//    private void mostrarNotificacion(String title, String body,String codigo, String nombre) {
//
//        Intent intent = new Intent(this, WebTokenActivity.class);
//
//        Intent intents = new Intent("com.example.s3k_user1.appzonas_FCM");
//
//        intents.putExtra("codigo", codigo);
//        intents.putExtra("nombreUsuario", nombre);
//
//        LocalBroadcastManager localBroadcastManager =LocalBroadcastManager.getInstance(this);
//        localBroadcastManager.sendBroadcast(intents);
//
//        intent.putExtra("codigo", codigo);
//        intent.putExtra("nombreUsuario", nombre);
//
//        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
//        //FirebaseMessaging.getInstance().subscribeToTopic("all");
//        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//
//
//
//        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
//                .setSmallIcon(R.drawable.ic_event)
//                .setContentTitle(title)
//                .setContentText(body)
//                .setAutoCancel(true)
//
//                .setSound(soundUri)
//                .setContentIntent(pendingIntent);
//
//
//        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//        notificationManager.notify(0, notificationBuilder.build());
//
//    }





}