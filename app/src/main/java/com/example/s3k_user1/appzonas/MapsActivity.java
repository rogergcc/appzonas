package com.example.s3k_user1.appzonas;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.iid.FirebaseInstanceId;
import com.pranavpandey.android.dynamic.toasts.DynamicToast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends AppCompatActivity implements
        OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final int FINE_LOCATION_PERMISSION_REQUEST = 1;
    private static final int CONNECTION_RESOLUTION_REQUEST = 2;
    private static final int MY_PERMISSIONS_REQUEST_READ_PHONE_STATE = 1;

    private static final int REQUEST_PERMISSIONS_LOCATION_SETTINGS_REQUEST_CODE = 33;
    private static final int REQUEST_PERMISSIONS_LAST_LOCATION_REQUEST_CODE = 34;
    private static final int REQUEST_PERMISSIONS_CURRENT_LOCATION_REQUEST_CODE = 35;

    private GoogleApiClient mGoogleApiClient;
    private GoogleMap mMap;
    private Location mLastLocation;

    LatLng userLocation;

    private Button btnIngresarSistema;
    private ImageButton btnImei;
    private TextView textView;

    private TextView txtImei;

    private TextView toke;
    /**
     * Permissions that need to be explicitly requested from end user.
     */
    /**
     * Callback received when a permissions request has been completed.
     */
    /*
     * funciono https://developer.here.com/documentation/android-starter/dev_guide/topics/request-android-permissions.html
     *
     * */

    //Minimo tiempo para updates en Milisegundos
    private static final long MIN_CAMBIO_DISTANCIA_PARA_UPDATES = 1; // 10 metros
    //Minimo tiempo para updates en Milisegundos
    private static final long MIN_TIEMPO_ENTRE_UPDATES = 1000 * 60 * 1; // 1 minuto

    private SupportMapFragment mapFragment;
    private List<Zonas> zonasList;

    public boolean accesoHabilitado= false;



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
        }
    }


    public String obtenerIMEI() {

        TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        @SuppressLint("MissingPermission") String imei =tm.getDeviceId(); // Obtiene el imei  or  "352319065579474";
        return imei;

    }
    public String obtenerImeiYVerificarPermisos()
    {

        if(Build.VERSION.SDK_INT  < Build.VERSION_CODES.M)
        {
            //Menores a Android 6.0
            String imei= obtenerIMEI();
            return imei;
        }
        else
        {
            // Mayores a Android 6.0
            String imei="";
            if (checkSelfPermission(Manifest.permission.READ_PHONE_STATE)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.READ_PHONE_STATE},
                        MY_PERMISSIONS_REQUEST_READ_PHONE_STATE);
                imei="";
            } else {
                imei= obtenerIMEI();
            }

            return imei;

        }
    }


    public void obtenerDatosDelServicioZonasTrabajo(final String codigoUsuario) {
        //https://api.myjson.com/bins/wicz0
        String url = "http://192.168.1.36/legal/ZonaTrabajo/ZonaTrabajoListarJsonExterno?id="+codigoUsuario;
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

                            final double[] radioDeZonaTrabajo = new double[1];
                            final MarkerOptions finalMarkerOptions = markerOptions;

                            mMap.addMarker(finalMarkerOptions);
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
                                codigoZonaTrabajo=zonasList.get(i).getZonaTrabajoId();
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
                            //true
                        }

                        if (existeUsuario == false)
                            estas = " sin Zona de Trabajo asignado";
                        else
                            estas = habilitarAcceso ? " Acceso Habilitado y Zona: "+codigoZonaTrabajo : "Se encuentra fuera de una Zona de Trabajo asignado";
                        String mensaje = "Usuario " + estas;
                        if (existeUsuario == false)
                            DynamicToast.makeError(getBaseContext(), mensaje, Toast.LENGTH_LONG).show();
                        else{
                            if (habilitarAcceso){
                                habilitarAccesoAZonaTrabajoUsuario(codigoZonaTrabajo,"1");
                                obtenerTokenDelUsuario(codigoUsuario);

                                DynamicToast.makeSuccess(getBaseContext(), estas, Toast.LENGTH_LONG).show();

                            }else{
                                habilitarAccesoAZonaTrabajoUsuario(codigoZonaTrabajo,"0");
                                toke.setText("Token");
                                DynamicToast.makeError(getBaseContext(), estas, Toast.LENGTH_LONG).show();
                            }
                        }
                        accesoHabilitado = habilitarAcceso;

                        //Toast.makeText(getBaseContext(), mensaje, Toast.LENGTH_LONG).show();

                        //DynamicToast.makeSuccess(getBaseContext(), mensaje, Toast.LENGTH_LONG).show();

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Log.e("Volley", error.toString());
                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                    //Toast.makeText(getBaseContext(), "Tiempo de Respuesta, Vuelga Generar", Toast.LENGTH_LONG).show();
                    DynamicToast.makeWarning(getBaseContext(), "Error Tiempo de Respuesta, Vuelva ha iniciar sesión", Toast.LENGTH_LONG).show();
                }
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(JsonObjectRequest);

    }

    public void habilitarAccesoAZonaTrabajoUsuario(String zona, String habilitarDeshabilitarUbicacion){
        Log.w(TAG,"zona: "+ zona);
        String habilitarUbicacion =habilitarDeshabilitarUbicacion;
        //Actualiza en parametro ubicacion segun  el id del la Zona Trabajo
        //http://192.168.1.33/legal/ZonaTrabajo/ZonaTrabajoEditarSenEncuentraEnZonaSegunIdJson?id=1&ubicacion=1
        String url = "http://192.168.1.36/legal/ZonaTrabajo/ZonaTrabajoEditarSenEncuentraEnZonaSegunIdJson?id="+zona+"&ubicacion="+habilitarUbicacion;

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
    private String tokenUser;
    private void enviarTokenAlServidor(String token , String imei) {


        String URL = "http://192.168.1.36/legal/DispositivoUsuario/ActualizarTokenDispositivoDelUsuarioSegunImeiRegistradoJson?imei="+imei+"&token="+token;

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                URL, (String) null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.w(TAG, "actualizartoken"+response.toString());


                        try { LayoutInflater inflater = getLayoutInflater();
                            View layout = inflater.inflate(R.layout.custom_toast,
                                    (ViewGroup) findViewById(R.id.custom_toast_layout));
                            TextView text = (TextView) layout.findViewById(R.id.text);
                            String respuestaActualizar = (response.getString("respuesta"));
                            if ( respuestaActualizar.equals("true")) {
                                //Toast.makeText(MapsActivity.this, "Token Registrado Correctamente", Toast.LENGTH_SHORT).show();
                                DynamicToast.makeSuccess(getBaseContext(), "Token Registrado Correctamente", Toast.LENGTH_LONG).show();


                            }
                            else
                                //Toast.makeText(MapsActivity.this, "Token No Registrado, IMEI no registrado en el Sistema\"", Toast.LENGTH_SHORT).show();
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

    public void obtenerTokenDelUsuario(final String codigoUsuario) {
        String codigUsuario = codigoUsuario;
        //http://192.168.1.36/legal/Token/TokenListarJsonExterno?id_usuario=1
        String url = "http://192.168.1.36/legal/Token/TokenListarJsonExterno?id_usuario="+codigoUsuario;
        zonasList = new ArrayList<>();
        //jsonObjectRequest=new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
        JsonObjectRequest JsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                url, (String) null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        JSONArray jRoutes = null;
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

    public static final String TAG = "NOTICIAS";
    public static String TOKEN = "T";
    String codigoUsuario = "";
    String nombreUsuario = "";

    private BroadcastReceiver mHandler = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            String codigoUsuario = intent.getStringExtra("codigo");
            String nombreUsuario = intent.getStringExtra("nombreUsuario");
            textView.setText(nombreUsuario);
            //btnIngresarSistema.callOnClick();
            obtenerDatosDelServicioZonasTrabajo(codigoUsuario);

        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.drawable.logo);

        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setTitle("");
        LocalBroadcastManager.getInstance(this).registerReceiver(mHandler, new IntentFilter("com.example.s3k_user1.appzonas_FCM"));
        setContentView(R.layout.activity_maps);

        textView = (TextView) findViewById(R.id.TextView);

        toke= (TextView) findViewById(R.id.token);

        txtImei = (TextView) findViewById(R.id.txtImei);

        btnIngresarSistema = (Button) findViewById(R.id.btnIngresarSistema);

        btnImei= (ImageButton) findViewById(R.id.btnImei);

        //TODO el boton ingresar por defecto Inhabilitado
        //btnIngresarSistema.setClickable(false);
        //btnIngresarSistema.setEnabled(false);
        btnIngresarSistema.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String ime = obtenerIMEI();
                //Toast.makeText(MapsActivity.this, "IMEI: "+ime, Toast.LENGTH_SHORT).show();
                //Log.w(TAG,"imei: " + ime + " codigoUser: " + codigoUsuario);
                obtenerDatosDelServicioZonasTrabajo(codigoUsuario);



                textView.setText("Usuario: "+ nombreUsuario);
            }
        });

        btnImei.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String token = FirebaseInstanceId.getInstance().getToken();
                String imei = obtenerIMEI();
                txtImei.setText("IMEI: " +imei);

                enviarTokenAlServidor(token,imei);
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
            //obtenerTokenDelUsuario(codigoUsuario);

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
