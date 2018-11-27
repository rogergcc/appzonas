package com.example.s3k_user1.appzonas;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import org.json.JSONObject;

public class MiFirebaseInstanceIdService extends FirebaseInstanceIdService {

    public static final String TAG = "Mifirebase";
    public static String tokenD;
    public String imeiD;

    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();

        String token = FirebaseInstanceId.getInstance().getToken();

        Log.w(TAG, "Token Nuevo ID Service: " + token);
        //tokenD = token;
        //WebTokenActivity.TOKEN=token;
        //enviarTokenAlServidor(token);
    }
    public void ObtenerIMEI() {
        String myIMEI = "";

        TelephonyManager mTelephony = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        if (mTelephony.getDeviceId() != null) {
            myIMEI = mTelephony.getDeviceId();
        }
        Toast.makeText(this, "IMEI: "+myIMEI, Toast.LENGTH_SHORT).show();
    }
    public String obtenerIMEI() {

        TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        @SuppressLint("MissingPermission") String imei =tm.getDeviceId(); // Obtiene el imei  or  "352319065579474";
        return imei;

    }
    private void enviarTokenAlServidor(String token , String imei) {
        // Enviar token al servidor
        //DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        //reference.child(getString(R.string.db))
        String tokenDispositivo = token;
        Log.w(TAG,"Token Enviando Servidor:" +token + "IMEI: ");
        //String imei = obtenerIMEI();
        String URL = "http://192.168.1.34/legal/DispositivoUsuario/ActualizarTokenDispositivoDelUsuarioSegunImeiRegistradoJson?imei="+imei+"&token="+tokenDispositivo;

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                URL, (String) null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.w(TAG, response.toString());

                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.w(TAG, "Error: " + error.getMessage());

            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonObjReq);
    }



}