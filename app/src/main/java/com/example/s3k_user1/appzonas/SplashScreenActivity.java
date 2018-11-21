package com.example.s3k_user1.appzonas;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Handler;
import android.support.design.button.MaterialButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.s3k_user1.appzonas.Others.UploadImageActivity;
import com.example.s3k_user1.appzonas.Sesion.SessionManager;
import com.pranavpandey.android.dynamic.toasts.DynamicToast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

public class SplashScreenActivity extends AppCompatActivity {
    private final int DURACION_SPLASH = 2000;
    public static String USUARIOID="";
    public static String USUARIONOMBRE="";
    public static String USUARIOEMPLEADO="";
    public static String USUARIOCORREO="";
    public static String USUARIOROL="";
    RelativeLayout rellay1, rellay2;
    ImageView imagen_logo_splash_screen;
    Button btnIngresarPantallaTokenWeb;
    Button btnIngresarLogin;
    TextInputEditText edtusuario;
    AppCompatEditText edtcontrasena;
    Handler handler = new Handler();

    boolean respuestaLogin = false;
    String mensajeLogin = "";
    View vista;
    private String IP_LEGAL = MapsActivity.IP_APK;


    SessionManager session;
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            rellay1.setVisibility(View.VISIBLE);
            imagen_logo_splash_screen.setImageResource(R.drawable.legal100);

            //rellay2.setVisibility(View.VISIBLE);
        }
    };

    public void ValidacionLoginExternoJson(String usuLogin, String usuPassword) {
        //https://api.myjson.com/bins/wicz0
        //String url = "http://192.168.0.12/documentosLista.json";

        Log.e("MENSAJE VALIDA: ", "u:" + usuLogin + " - "+ usuPassword);
        String url = IP_LEGAL+"/legal/Usuario/ValidacionLoginExternoJson?usuLogin="+usuLogin+"&usuPassword="+usuPassword;
        Log.e("URL LOGIN: ", url);
        JsonObjectRequest JsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                url,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        Log.e("MENSAJE VALIDA: ", jsonObject.toString());
                        try {
                            JSONObject objectUser = jsonObject.getJSONObject("usuario");

                            String respuestSesion = jsonObject.getString("respuesta");
                            String mensaje = jsonObject.getString("mensaje");
                            USUARIOID =jsonObject.getString("usuarioId");
                            USUARIONOMBRE =jsonObject.getString("usuarioNombre");

                            USUARIOEMPLEADO =objectUser.getString("NombreEmpleado");

                            USUARIOCORREO =jsonObject.getString("correo");

                            USUARIOROL =jsonObject.getString("rol");

                            respuestaLogin = jsonObject.getBoolean("respuesta");
                            Log.e("Respuesta Login", String.valueOf(respuestaLogin));
                            mensajeLogin = mensaje;
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                if (error instanceof TimeoutError || error instanceof NoConnectionError) {

                    DynamicToast.makeWarning(getBaseContext(), "Error Tiempo de Respuesta, Vuelva ha iniciar sesión", Toast.LENGTH_LONG).show();
                }
            }
        }) {
            @Override
            public Priority getPriority() {
                return Priority.IMMEDIATE;
            }
        };

        /*
        Toast.makeText(SplashScreenActivity.this,
                "resp_F_validacion: " + respuestaLogin,
                Toast.LENGTH_SHORT).show();
                */


        RequestQueue requestQueue = Volley.newRequestQueue(SplashScreenActivity.this);
        JsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(JsonObjectRequest);

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_splash_screen);
        session = new SessionManager(getApplicationContext());


        rellay1 = (RelativeLayout) findViewById(R.id.rellay1);
        rellay2 = (RelativeLayout) findViewById(R.id.rellay2);
        imagen_logo_splash_screen = findViewById(R.id.imagen_logo_splash_screen);

        vista = findViewById(R.id.vistasplashScreen);

        edtusuario = findViewById(R.id.edtusuario);
        edtcontrasena = findViewById(R.id.edtcontrasena);


        btnIngresarPantallaTokenWeb = findViewById(R.id.btnIngresarSisWebToken);
        handler.postDelayed(runnable, 2000); //2000 is the timeout for the splash
        btnIngresarPantallaTokenWeb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(SplashScreenActivity.this,MapsActivity.class);
                startActivity(intent1);
            }
        });

        btnIngresarLogin = findViewById(R.id.btnIngresarLogin);
        btnIngresarLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edtusuario.getText().toString().equals("") ||
                        edtcontrasena.getText().toString().equals("")) {
                    Snackbar.make(vista, "Ingrese datos", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }else{
                    ValidacionLoginExternoJson(edtusuario.getText().toString(),edtcontrasena.getText().toString());


                    if (respuestaLogin){
                        session.createLoginSession(USUARIONOMBRE, USUARIOID,USUARIOEMPLEADO,USUARIOCORREO,USUARIOROL);


                        Intent intentPantalla = new Intent(SplashScreenActivity.this,ActividadPrincipal.class);
                        intentPantalla.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intentPantalla);
                    }else{
                        Snackbar.make(vista, mensajeLogin, Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    }

                }
                //Intent intentPantalla = new Intent(SplashScreenActivity.this,ActividadPrincipal.class);
                //startActivity(intentPantalla);
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
