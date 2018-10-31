package com.example.s3k_user1.appzonas;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.pranavpandey.android.dynamic.toasts.DynamicToast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SplashScreenActivity extends AppCompatActivity {
    private final int DURACION_SPLASH = 2000;
    public static String USUARIO="";
    public static String PASSWORD="";
    RelativeLayout rellay1, rellay2;
    Button btnIngresarPantallaTokenWeb;
    Button btnIngresarLogin;
    EditText edtusuario, edtcontrasena;
    Handler handler = new Handler();
    Boolean respuestaLogin = false;
    View vista;
    private String IP_LEGAL = MapsActivity.IP_APK;
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            rellay1.setVisibility(View.VISIBLE);
            //rellay2.setVisibility(View.VISIBLE);
        }
    };

    public boolean ValidacionLoginExternoJson(String usuLogin, String usuPassword) {
        //https://api.myjson.com/bins/wicz0
        //String url = "http://192.168.0.12/documentosLista.json";

        final boolean respuestaL =false;
        String url = IP_LEGAL + "/legal/Usuario/ValidacionLoginExternoJson?usuLogin="+usuLogin+"&usuPassword="+usuPassword;
        JsonObjectRequest JsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                url, (String) null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject jsonObject) {

                                //respuestaL = jsonObject.getString("respuesta");


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
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_splash_screen);
        rellay1 = (RelativeLayout) findViewById(R.id.rellay1);
        rellay2 = (RelativeLayout) findViewById(R.id.rellay2);

        vista = findViewById(R.id.vistasplashScreen);

        edtusuario = findViewById(R.id.edtusuario);
        edtcontrasena = findViewById(R.id.edtcontrasena);


        btnIngresarPantallaTokenWeb = findViewById(R.id.btnIngresarSisWebToken);
        handler.postDelayed(runnable, 2000); //2000 is the timeout for the splash
        btnIngresarPantallaTokenWeb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edtusuario.getText().toString()=="" ||
                        edtcontrasena.getText().toString()=="") {
                    Snackbar.make(vista, "Ingrese datos", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }else{
                    //ValidacionLoginExternoJson(edtusuario.getText().toString(),edtcontrasena.getText().toString());
                }

                Intent intentPantalla = new Intent(SplashScreenActivity.this,MapsActivity.class);
                startActivity(intentPantalla);
            }
        });
        btnIngresarLogin = findViewById(R.id.btnIngresarLogin);
        btnIngresarLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(SplashScreenActivity.this,DocumentosActivity.class);
                startActivity(intent1);
            }
        });
        /*new Handler().postDelayed(new Runnable(){
            public void run(){
                Intent intent = new Intent(SplashScreenActivity.this, MapsActivity.class);
                startActivity(intent);
                finish();
            };
        }, DURACION_SPLASH);*/
    }

    /*
    * RelativeLayout rellay1, rellay2;

    Handler handler = new Handler();
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            rellay1.setVisibility(View.VISIBLE);
            rellay2.setVisibility(View.VISIBLE);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        rellay1 = (RelativeLayout) findViewById(R.id.rellay1);
        rellay2 = (RelativeLayout) findViewById(R.id.rellay2);

        handler.postDelayed(runnable, 2000); //2000 is the timeout for the splash
    }
    *
    * */
}
