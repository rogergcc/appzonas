package com.example.s3k_user1.appzonas;

import android.Manifest;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.android.volley.AuthFailureError;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.s3k_user1.appzonas.AppUtils.BaseCrashHandler;
import com.example.s3k_user1.appzonas.Model.Zonas;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.iid.FirebaseInstanceId;
import com.pranavpandey.android.dynamic.toasts.DynamicToast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static android.Manifest.permission.READ_PHONE_NUMBERS;
import static android.Manifest.permission.READ_PHONE_STATE;
import static android.Manifest.permission.READ_SMS;

/**
 * type Maps activity.
 * SESION WEB POR NOTIFICACION
 * MAPA , IMEI, TOKEN WEB, NOTIFICACION
 */
public class WebTokenActivity extends AppCompatActivity implements
        OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final int FINE_LOCATION_PERMISSION_REQUEST = 1;
    private static final int CONNECTION_RESOLUTION_REQUEST = 2;
    private static final int MY_PERMISSIONS_REQUEST_READ_PHONE_STATE = 1;
    private static final int PERMISSION_REQUEST_CODE = 100;

    private GoogleApiClient mGoogleApiClient;
    private GoogleMap mMap;
    private Location mLastLocation;

    /**
     * User location.
     */
    LatLng userLocation;

    private Button btnIngresarSistema;
    private ImageButton btnImei;
    private TextView textView;

    private TextView txtImei;

    private TextView toke;

    private SupportMapFragment mapFragment;
    private List<Zonas> zonasList;

    private String tokenUser;
    //http://192.168.1.36
    //http://181.65.204.99:2222
//    public static String IP_APK =  "http://192.168.1.42";
    public static String IP_APK = "http://192.168.0.23";
    /**
     * Acceso habilitado.
     */
    public boolean accesoHabilitado = false;

    /**
     * The constant TAG.
     */
    public static final String TAG = "NOTICIAS";
    /**
     * The constant TOKEN.
     */
    public static String TOKEN = "T";
    /**
     * The Codigo usuario.
     */
    String codigoUsuario = "";
    /**
     * The Nombre usuario.
     */
    String nombreUsuario = "";

    private long timeCountInMilliSeconds = 1 * 60000;

    private enum TimerStatus {
        STARTED,
        STOPPED
    }

    private TimerStatus timerStatus = TimerStatus.STOPPED;
    private CountDownTimer countDownTimer;

    //TODO dialog para cambiar IP
    Dialog myDialogIP;
    Button botonAprobarIP;
    View vistaMaps;
    // fin dialog para cambiar IP

    TelephonyManager telephonyManager;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        myDialogIP = new Dialog(WebTokenActivity.this);
        myDialogIP.setContentView(R.layout.dialog_cambiar_ip);
        String msg = "";
        Fragment fragment;
        switch (item.getItemId()) {

            case R.id.documentos:
                msg = "Documentos";
                Intent intent1 = new Intent(this, SplashScreenActivity.class);
                startActivity(intent1);
                return true;

            case R.id.action_cambiar_ip:

                final EditText dialog_edt_ip = myDialogIP.findViewById(R.id.dialog_edt_ip);
                botonAprobarIP = myDialogIP.findViewById(R.id.btnAprobarIP);
                //botonCancelar = myDialog.findViewById(R.id.btnCancelar);
                dialog_edt_ip.setText(WebTokenActivity.IP_APK);
                myDialogIP.show();
                botonAprobarIP.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        WebTokenActivity.IP_APK = dialog_edt_ip.getText().toString();
                        dialog_edt_ip.setText(WebTokenActivity.IP_APK);
                        myDialogIP.hide();

                    }
                });

        }


        return super.onOptionsItemSelected(item);
    }

    private void reset() {
        stopCountDownTimer();
        startCountDownTimer();
    }

    //TODO PROGRESSBAR CUSTOM
    private TextView textViewTime;
    private ProgressBar progressBarCircle;
    private int TIEMPO_MINUTOS = 5;

    private void setTimerValues() {
        int time = 0;

        time = TIEMPO_MINUTOS;
        // assigning values after converting to milliseconds
        // assigning values after converting to milliseconds
        timeCountInMilliSeconds = time * 60 * 1000;
    }

    private void startStop() {

        //startCountDownTimer();
        //timerStatus = TimerStatus.STOPPED;

        if (timerStatus == TimerStatus.STOPPED) {

            // call to initialize the timer values
            setTimerValues();
            // call to initialize the progress bar values
            setProgressBarValues();
            // showing the reset icon
            //imageViewReset.setVisibility(View.VISIBLE);
            // changing play icon to stop icon
            //imageViewStartStop.setImageResource(R.drawable.icon_stop);
            // making edit text not editable
            //editTextMinute.setEnabled(false);
            // changing the timer status to started

            timerStatus = TimerStatus.STARTED;
            // call to start the count down timer
            startCountDownTimer();

        } else {

            // hiding the reset icon
            //imageViewReset.setVisibility(View.GONE);
            // changing stop icon to start icon
            //imageViewStartStop.setImageResource(R.drawable.icon_start);
            // making edit text editable
            //editTextMinute.setEnabled(true);
            // changing the timer status to stopped
            timerStatus = TimerStatus.STOPPED;
            stopCountDownTimer();

        }

    }

    private String hmsTimeFormatter(long milliSeconds) {

        String hms = String.format("%02d:%02d:%02d",
                TimeUnit.MILLISECONDS.toHours(milliSeconds),
                TimeUnit.MILLISECONDS.toMinutes(milliSeconds) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(milliSeconds)),
                TimeUnit.MILLISECONDS.toSeconds(milliSeconds) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(milliSeconds)));
        return hms;


    }

    private void setProgressBarValues() {

        progressBarCircle.setMax((int) timeCountInMilliSeconds / 1000);
        progressBarCircle.setProgress((int) timeCountInMilliSeconds / 1000);
    }

    private void startCountDownTimer() {

        countDownTimer = new CountDownTimer(timeCountInMilliSeconds, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

                textViewTime.setText("quedan " + hmsTimeFormatter(millisUntilFinished) + " segs ");

                progressBarCircle.setProgress((int) (millisUntilFinished / 1000));

            }

            @Override
            public void onFinish() {

                textViewTime.setText("Expirado");
                toke.setText("TOKEN");
                // call to initialize the progress bar values
                setProgressBarValues();
                // hiding the reset icon
                //imageViewReset.setVisibility(View.GONE);
                // changing stop icon to start icon
                //imageViewStartStop.setImageResource(R.drawable.icon_start);
                // making edit text editable
                //editTextMinute.setEnabled(true);
                // changing the timer status to stopped
                timerStatus = TimerStatus.STOPPED;
            }

        }.start();
        countDownTimer.start();
    }

    private void stopCountDownTimer() {
        countDownTimer.cancel();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case FINE_LOCATION_PERMISSION_REQUEST: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    findLocation();
                }
            }
            case PERMISSION_REQUEST_CODE:
                if (ActivityCompat.checkSelfPermission(this, READ_SMS) !=
                        PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                        READ_PHONE_NUMBERS) != PackageManager.PERMISSION_GRANTED &&
                        ActivityCompat.checkSelfPermission(this, READ_PHONE_STATE) !=
                                PackageManager.PERMISSION_GRANTED) {
                    return;
                } else {
                    //textView.setText(telephonyManager.getLine1Number());
                }
        }
    }


    /**
     * Obtener imei string.
     *
     * @return retorna el imei
     */
//    public String obtenerIMEI() {
//
//        TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
//
//
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            @SuppressLint("MissingPermission") String imei = tm.getImei();
//        } else {
//            @SuppressLint("MissingPermission") String imei = tm.getDeviceId();
//        }
//        return imei;
//
//    }
    @SuppressWarnings("deprecation")
    private String obtenerIMEI() {
        String IMEINumber = "";
        if (ActivityCompat.checkSelfPermission(this, READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
            TelephonyManager telephonyMgr = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                IMEINumber = telephonyMgr.getImei();
            } else {
                IMEINumber = telephonyMgr.getDeviceId();

            }
        }
        return IMEINumber;
    }

    @SuppressWarnings("deprecation")
    private String obtenerPhoneData() {

        return BaseCrashHandler.getInstance().getPhoneInfo();
    }

    /**
     * Obtener imei previamente permisos verificados.
     *
     * @return the string
     */
    public String obtenerImeiYVerificarPermisos() {

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            //Menores a Android 6.0
            return obtenerIMEI();
        } else {
            // Mayores a Android 6.0
            String imei = "";
            if (checkSelfPermission(READ_PHONE_STATE)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{READ_PHONE_STATE},
                        MY_PERMISSIONS_REQUEST_READ_PHONE_STATE);
                imei = "";
            } else {
                imei = obtenerIMEI();
            }

            return imei;

        }
    }

    /**
     *
     * Obtiene las Zonas de Trabajo y verifica si esta dentro de una Zona
     * de un usuario segun su codigo
     *
     * @param codigoUsuario codigo del usuario
     */
    public void obtenerDatosDelServicioZonasTrabajo(final String codigoUsuario) {
        //https://api.myjson.com/bins/wicz0
        String url = IP_APK + "/legal/ZonaTrabajo/ZonaTrabajoListarJsonExterno?id=" + codigoUsuario;
        zonasList = new ArrayList<>();

        //jsonObjectRequest=new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {


        JsonObjectRequest JsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                url, (String) null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        JSONArray jRoutes = null;
                        try {
                            jRoutes = response.getJSONArray("zonas");
                            for (int i = 0; i < jRoutes.length(); i++) {
                                try {
                                    JSONObject jsonObject = jRoutes.getJSONObject(i);

                                    Zonas zona = new Zonas();
                                    zona.setUsuarioID(jsonObject.getString("UsuarioID"));

                                    zona.setDescripcion(jsonObject.getString("Descripcion"));
                                    zona.setDireccion(jsonObject.getString("Direccion"));
                                    zona.setZonaTrabajoId(jsonObject.getString("ZonaTrabajoId"));
                                    zona.setLatitud(jsonObject.getString("Latitud"));
                                    zona.setLongitud(jsonObject.getString("Longitud"));

                                    zona.setDentroZona(jsonObject.getString("UbicadoZona"));
                                    zona.setRadio(jsonObject.getString("Radio"));


                                    zonasList.add(zona);
                                } catch (JSONException e) {
                                    e.printStackTrace();

                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                        final float[] distance = new float[2];


                        boolean habilitarAcceso = false;
                        MarkerOptions markerOptions;

                        String userObtenerID = "-";
                        String codigoZonaTrabajo = "0";
                        mMap.clear();
                        for (int i = 0; i < zonasList.size(); i++) {
                            final double lat = Double.parseDouble(zonasList.get(i).getLatitud());
                            final double lon = Double.parseDouble(zonasList.get(i).getLongitud());
                            final double radio = Double.parseDouble(zonasList.get(i).getRadio());
                            LatLng latLng = new LatLng(lat, lon);
                            markerOptions = new MarkerOptions();

                            markerOptions.position(latLng);
                            markerOptions.title(zonasList.get(i).getDescripcion());
                            //markerOptions.icon(BitmapDescriptorFactory.fromResource(R.mipmap.custom_marker));


                            //markerOptions.icon(BitmapDescriptorFactory.fromResource(R.mipmap.custom_marker));

                            final double[] radioDeZonaTrabajo = new double[1];
                            final MarkerOptions finalMarkerOptions = markerOptions;

                            mMap.addMarker(finalMarkerOptions.icon(BitmapDescriptorFactory.fromResource(R.mipmap.custom_marker)));
                            Circle circle = mMap.addCircle(new CircleOptions()
                                    .center(new LatLng(lat, lon))
                                    .radius(radio)
                                    .strokeColor(Color.RED)
                            );
                            double latituCirculo = circle.getCenter().latitude;
                            double loongCirculo = circle.getCenter().longitude;
                            Location.distanceBetween(userLocation.latitude, userLocation.longitude,
                                    latituCirculo, loongCirculo, distance);
                            radioDeZonaTrabajo[0] = circle.getRadius();

                            userObtenerID = zonasList.get(i).getUsuarioID();

                            if ((distance[0] < radio) && codigoUsuario.equals(userObtenerID)) {
                                habilitarAcceso = true;
                                codigoZonaTrabajo = zonasList.get(i).getZonaTrabajoId();
                            } else {

                                habilitarAcceso = (habilitarAcceso) ? habilitarAcceso : false;
                            }

                        }
                        String estas = "0";
                        boolean existeUsuario = false;
                        for (int i = 0; i < zonasList.size(); i++) {
                            if (zonasList.get(i).getUsuarioID().equals(codigoUsuario)) {
                                existeUsuario = true;
                                break;
                            }
                        }

                        if (existeUsuario == false)
                            estas = " sin Zona de Trabajo asignado";
                        else
                            estas = habilitarAcceso ? " Acceso Habilitado y Zona: " + codigoZonaTrabajo : "Se encuentra fuera de una Zona de Trabajo asignado";
                        String mensaje = "Usuario " + estas;
                        if (existeUsuario == false)
                            DynamicToast.makeError(getBaseContext(), mensaje, Toast.LENGTH_LONG).show();
                        else {
                            String imei = obtenerIMEI();

                            Geocoder gcd = new Geocoder(getApplicationContext(), Locale.getDefault());
                            List<Address> addresses = null;
                            try {
                                addresses = gcd.getFromLocation(userLocation.latitude, userLocation.longitude, 1);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            if (addresses.size() > 0) {
                                String countryName = addresses.get(0).getFeatureName() + ", " + addresses.get(0).getLocality() + ", " + addresses.get(0).getAdminArea() + ", " + addresses.get(0).getCountryName();
                            }


                            HistorialAccesoInsertarJsonExterno(codigoUsuario, imei,
                                    String.valueOf(userLocation.latitude),
                                    String.valueOf(userLocation.longitude),
                                    addresses.get(0).getCountryName(),
                                    addresses.get(0).getLocality(),
                                    addresses.get(0).getFeatureName());
                            if (habilitarAcceso) {
                                habilitarAccesoAZonaTrabajoUsuario(codigoZonaTrabajo, "1");

                                obtenerTokenDelUsuario(codigoUsuario);

                                startStop();
                                DynamicToast.makeSuccess(getBaseContext(), estas, Toast.LENGTH_LONG).show();

                            } else {
                                //habilitarAccesoAZonaTrabajoUsuario(codigoZonaTrabajo,"0");
                                toke.setText("Token");
                                DynamicToast.makeError(getBaseContext(), estas, Toast.LENGTH_LONG).show();
                            }
                        }
                        accesoHabilitado = habilitarAcceso;

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                if (error instanceof TimeoutError || error instanceof NoConnectionError) {

                    DynamicToast.makeWarning(getBaseContext(), "Error Tiempo de Respuesta, Vuelva ha iniciar sesión", Toast.LENGTH_LONG).show();
                }
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(JsonObjectRequest);

    }

    public void HistorialAccesoInsertarJsonExterno(final String UsuarioID, final String IMEI, final String Latitud, final String Longitud, final String Pais, final String Ciudad, final String Direccion) {


        //
        //Direccion.replaceAll(" ", "");
        String url = IP_APK + "/legal/HistorialAcceso/HistorialAccesoInsertarJsonExterno";
        //
        JSONObject js = new JSONObject();
        try {
            JSONObject params = new JSONObject();
            js.put("UsuarioID", UsuarioID);
            js.put("IMEI", IMEI);
            js.put("Latitud", Latitud);
            js.put("Longitud", Longitud);
            js.put("Pais", Pais);
            js.put("Ciudad", Ciudad);
            js.put("Direccion", Direccion);


            //js.put("data", jsonobject.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(
                Request.Method.POST, url, js,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.w(TAG, response.toString());


                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Log.w("Error.Response", error.getMessage());
                    }
                }
        ) {
            /*@Override
            protected Map<String, String> getParams()
            {
                Map<String,String> params = new HashMap<>();
                params.put("UsuarioID",UsuarioID);
                params.put("CodigoDispositivo",CodigoDispositivo);

                params.put("Latitud",Latitud);
                params.put("Longitud",Longitud);
                params.put("Pais",Pais);
                params.put("Ciudad",Ciudad);
                params.put("Direccion",Direccion);
                params.put("Exito",Exito);
                return params;
            }*/
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                //params.put("Content-Type","application/x-www-form-urlencoded");
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonObjReq);
    }

    /**
     * Habilitar acceso a zona trabajo usuario.
     *
     * @param zona                           the zona
     * @param habilitarDeshabilitarUbicacion the habilitar deshabilitar ubicacion
     */
    public void habilitarAccesoAZonaTrabajoUsuario(String zona, String habilitarDeshabilitarUbicacion) {
        Log.w(TAG, "zona: " + zona);
        String habilitarUbicacion = habilitarDeshabilitarUbicacion;
        //Actualiza en parametro ubicacion segun  el id del la Zona Trabajo
        //http://192.168.1.33/legal/ZonaTrabajo/ZonaTrabajoEditarSenEncuentraEnZonaSegunIdJson?id=1&ubicacion=1
        String url = IP_APK + "/legal/ZonaTrabajo/ZonaTrabajoEditarSenEncuentraEnZonaSegunIdJson?id=" + zona + "&ubicacion=" + habilitarUbicacion;

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                url, (String) null,
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

    /**
     * Enviar token de notificaciones (fcm) segun el imei registrado al servidor().
     *
     * @param token token para poder recibir notificaciones
     * @param imei  imei del movil android
     */
    public void enviarTokenAlServidor(String token, String imei) {


        String URL = IP_APK + "/legal/DispositivoUsuario/ActualizarTokenDispositivoDelUsuarioSegunImeiRegistradoJson?imei=" + imei + "&token=" + token;

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                URL, (String) null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.w(TAG, "actualizartoken" + response.toString());


                        try {
                            LayoutInflater inflater = getLayoutInflater();
                            View layout = inflater.inflate(R.layout.custom_toast,
                                    (ViewGroup) findViewById(R.id.custom_toast_layout));
                            TextView text = (TextView) layout.findViewById(R.id.text);
                            String respuestaActualizar = (response.getString("respuesta"));
                            if (respuestaActualizar.equals("true")) {
                                //Toast.makeText(WebTokenActivity.this, "Token Registrado Correctamente", Toast.LENGTH_SHORT).show();
                                DynamicToast.makeSuccess(getBaseContext(), "Token Registrado Correctamente", Toast.LENGTH_LONG).show();


                            } else
                                //Toast.makeText(WebTokenActivity.this, "Token No Registrado, IMEI no registrado en el Sistema\"", Toast.LENGTH_SHORT).show();
                                DynamicToast.makeError(getBaseContext(), "Token No Registrado, Verifique IMEI \n este registrado en el Sistema", Toast.LENGTH_LONG).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.w(TAG, "Error: " + error.getMessage());
                DynamicToast.makeWarning(getBaseContext(), "Error Tiempo de Respuesta, Token no registrado", Toast.LENGTH_LONG).show();

            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonObjReq);
    }

    /*Metod testigng guardar enviar token a DB*/
    public void enviarTokenATestDB(final String token, final String imei) {


        String URL = IP_APK + "/excelAJS/enviarnotificacion.php";

        JSONObject js = new JSONObject();
        try {
            JSONObject params = new JSONObject();

            js.put("nuevoToken", token);
            js.put("imei", imei);


            //js.put("data", jsonobject.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        //Log.w(TAG, "actualizartoken"+response.toString());


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // error
                Log.w("Error.Response", error.getMessage());
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                //Adding parameters to request
                params.put("nuevoToken", token);
                params.put("imei", imei);
                //returning parameter
                return params;
            }

        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    /**
     * Obtiene token de 4 digitos generados del servidor.
     *
     * @param codigoUsuario codigo del usuario
     */
    public void obtenerTokenDelUsuario(final String codigoUsuario) {
        String codigUsuario = codigoUsuario;
        //http://192.168.1.36/legal/Token/TokenListarJsonExterno?id_usuario=1
        String url = IP_APK + "/legal/Token/TokenListarJsonExterno?id_usuario=" + codigoUsuario;
        zonasList = new ArrayList<>();
        //jsonObjectRequest=new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
        JsonObjectRequest JsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                url, (String) null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        JSONArray jRoutes = null;
                        try {
                            TIEMPO_MINUTOS = Integer.parseInt(response.getString("tiempoexpiracion"));
                            timeCountInMilliSeconds = TIEMPO_MINUTOS * timeCountInMilliSeconds;
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        try {
                            jRoutes = response.getJSONArray("token");
                            for (int i = 0; i < jRoutes.length(); i++) {
                                try {
                                    JSONObject jsonObject = jRoutes.getJSONObject(i);

                                    tokenUser = jsonObject.getString("token");
                                    toke.setText(jsonObject.getString("token"));


                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Log.e("Volley", error.toString());
                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                    Toast.makeText(getBaseContext(), "Tiempo de Respuesta, Vuelva ha iniciar sesión", Toast.LENGTH_LONG).show();
                }
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(JsonObjectRequest);
    }

    private void findLocation() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    FINE_LOCATION_PERMISSION_REQUEST);
        } else {
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

            LatLng myLat = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
            // Add a marker in Sydney and move the camera


            mMap.setMyLocationEnabled(true);

            LocationManager service = (LocationManager)
                    getSystemService(LOCATION_SERVICE);
            Criteria criteria = new Criteria();

            String provider = service.getBestProvider(criteria, true);

            Location location = service.getLastKnownLocation(provider);

            //service.requestLocationUpdates(provider, 1, 1, (LocationListener) this);
            //service.requestLocationUpdates(provider, 1, 1, (LocationListener) this);

            userLocation = myLat;


            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 17.0f));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 17.0f));
        }
    }

    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }


    @Override
    protected void onResume() {
        super.onResume();

        buildGoogleAPIClient();
    }

    private void buildGoogleAPIClient() {
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CONNECTION_RESOLUTION_REQUEST && resultCode == RESULT_OK) {
            mGoogleApiClient.connect();
        }
    }


    private BroadcastReceiver mHandler = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

//            if(getIntent().getExtras() != null) {
            if (intent.getExtras() != null) {

                String codigoUsuario = intent.getStringExtra("codigo");
                String nombreUsuario = intent.getStringExtra("nombreUsuario");
                textView.setText(nombreUsuario);
                //btnIngresarSistema.callOnClick();

                obtenerDatosDelServicioZonasTrabajo(codigoUsuario);
            }


        }
    };

    private void loadFragment(Fragment fragment) {
        // load fragment
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_container, fragment);
        //transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        LocalBroadcastManager.getInstance(this).registerReceiver(mHandler, new IntentFilter("com.example.s3k_user1.appzonas_FCM"));
        setContentView(R.layout.activity_maps);

        toolbar = findViewById(R.id.toolbarmain);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");

        toolbar.setLogo(R.drawable.legal100);

        vistaMaps = findViewById(R.id.act_det_document);
        textView = (TextView) findViewById(R.id.TextView);


        toke = (TextView) findViewById(R.id.token);
        txtImei = (TextView) findViewById(R.id.txtImei);

        //PROGRESSBAR
        textViewTime = (TextView) findViewById(R.id.textViewTime);
        progressBarCircle = (ProgressBar) findViewById(R.id.progressBarCircle);

        // FIN PROGRESSBAR

        btnIngresarSistema = (Button) findViewById(R.id.btnIngresarSistema);

        btnImei = (ImageButton) findViewById(R.id.btnImei);

        //TODO el boton ingresar por defecto Inhabilitado
        //btnIngresarSistema.setClickable(false);
        //btnIngresarSistema.setEnabled(false);
        btnIngresarSistema.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String ime = obtenerIMEI();


                //Toast.makeText(WebTokenActivity.this, "IMEI: "+ime, Toast.LENGTH_SHORT).show();
                //Log.w(TAG,"imei: " + ime + " codigoUser: " + codigoUsuario);
                obtenerDatosDelServicioZonasTrabajo(codigoUsuario);


                textView.setText("Usuario: " + nombreUsuario);
            }
        });


        btnImei.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String token = FirebaseInstanceId.getInstance().getToken();
                String imei = obtenerIMEI();
                String phoneData = obtenerPhoneData();

                TelephonyManager telephonyMgr = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);

                if (checkSelfPermission(Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    Activity#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for Activity#requestPermissions for more details.
                    return;
                }

                String serialNumber = telephonyMgr.getSimSerialNumber();

                String deviceSoftwareVersion = telephonyMgr.getDeviceSoftwareVersion();
                String networkOperator = telephonyMgr.getNetworkOperator();
                String networkCountryIso = telephonyMgr.getNetworkCountryIso();
                String simOperator= telephonyMgr.getSimOperator();
                String voiceMailNumber= telephonyMgr.getVoiceMailNumber();

                String MyPhoneNumber = "0000000000";
                MyPhoneNumber = telephonyMgr.getSubscriberId();

                String version= System.getProperty("os.version"); // OS version
                int sdkver = Build.VERSION.SDK_INT ;     // API Level
                String sdkverv2 = Build.VERSION.RELEASE ;     // API Level
                String voiceMailNu232 = android.os.Build.DEVICE ;          // Device
                String voiceMailNu12= android.os.Build.PRODUCT   ;       // Product
                String phoneModel= Build.MODEL   ;       // Model
                //String verserial= Build.getSerial().toString()   ;



                txtImei.setText("IMEI: " +imei);

//                enviarTokenAlServidor(token,imei);

                enviarTokenATestDB(token,imei);
            }
        });


        if (getIntent().getExtras() != null) {
            //TODO habilitar boton para ingresar enviar datos al sistema
            // btnIngresarSistema.setClickable(true);

            //btnIngresarSistema.setEnabled(true);

            codigoUsuario = (String) getIntent().getExtras().get("codigo");
            nombreUsuario = (String) getIntent().getExtras().get("nombreUsuario");

            //btnIngresarSistema.callOnClick();
            textView.setText(nombreUsuario);
            obtenerDatosDelServicioZonasTrabajo(codigoUsuario);


        }

        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        buildGoogleAPIClient();

        String token = FirebaseInstanceId.getInstance().getToken();
        String myIMEI="0";
        myIMEI = obtenerImeiYVerificarPermisos();

        //MiFirebaseInstanceIdService.tokenD;
        Log.w(TAG, "Token Main: " + token);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        //enableMyLocation();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);


    }


    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        findLocation();
    }

    @Override
    public void onConnectionSuspended(int i) {
        Toast.makeText(this, "Connection suspended", Toast.LENGTH_SHORT).show();
    }

      @Override
    public void onConnectionFailed(@NonNull final ConnectionResult connectionResult) {
        if (connectionResult.hasResolution()) {
            try {
                connectionResult.startResolutionForResult(this, CONNECTION_RESOLUTION_REQUEST);
            } catch (IntentSender.SendIntentException e) {
                // There was an error with the resolution intent. Try again.
                mGoogleApiClient.connect();
            }
        } else {
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(connectionResult.getErrorCode(), this, 1);
            dialog.show();
        }
    }
}
