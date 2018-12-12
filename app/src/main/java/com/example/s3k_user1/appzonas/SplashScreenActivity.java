package com.example.s3k_user1.appzonas;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatEditText;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.s3k_user1.appzonas.Sesion.SessionManager;
import com.example.s3k_user1.appzonas.app.VolleySingleton;
import com.pranavpandey.android.dynamic.toasts.DynamicToast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SplashScreenActivity extends AppCompatActivity {
    private final int DURACION_SPLASH = 2000;
    public static String USUARIOID="";
    public static String USUARIONOMBRE="";
    public static String USUARIOEMPLEADO="";
    public static String USUARIOCORREO="";
    public static String USUARIOROL="";
    public static String EMPLEADOID="";
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
    private String IP_LEGAL = WebTokenActivity.IP_APK;

    ProgressBar progressBar;

    ProgressDialog progressDialog;

    SessionManager session;
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            rellay1.setVisibility(View.VISIBLE);
            imagen_logo_splash_screen.setImageResource(R.drawable.legal100);

            //rellay2.setVisibility(View.VISIBLE);
        }
    };




    public void ValidacionLogin() {
        final String username = edtusuario.getText().toString();
        final String password = edtcontrasena.getText().toString();

        //https://api.myjson.com/bins/wicz0
        //String url = "http://192.168.0.12/documentosLista.json";
        if (TextUtils.isEmpty(username)) {
            edtusuario.setError("Ingrese su Usuario");
            edtusuario.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            edtcontrasena.setError("Ingrese su Contraseña");
            edtcontrasena.requestFocus();
            return;
        }
        Log.e("MENSAJE VALIDA: ", "u:" + username + " - "+ password);
        //String url = IP_LEGAL+"/legal/Usuario/ValidacionLoginExternoJson?usuLogin="+usuLogin+"&usuPassword="+usuPassword;

        String URls = IP_LEGAL+ "/legal/Usuario/ValidacionLoginExternoJson";
        Log.e("URL LOGIN: ", URls);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URls,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressBar.setVisibility(View.GONE);
                        progressDialog.dismiss();
                        try {
                            //converting response to json object
                            JSONObject jsonObject = new JSONObject(response);

                            //if no error in response
                            if (jsonObject.getBoolean("respuesta")) {
                                Log.e("Entro", "entro");
                                //Toast.makeText(getApplicationContext(), jsonObject.getString("mensaje"), Toast.LENGTH_SHORT).show();
                                Snackbar.make(vista, jsonObject.getString("mensaje"), Snackbar.LENGTH_LONG)
                                        .setAction("Action", null).show();
                                //getting the user from the response
                                //JSONObject userJson = jsonObject.getJSONObject("user");




                                //storing the user in shared preferences

                                //SharedPrefManager.getInstance(getApplicationContext()).userLogin(user);
                                JSONObject objectUser = jsonObject.getJSONObject("usuario");


                                String mensaje = jsonObject.getString("mensaje");
                                USUARIOID =jsonObject.getString("usuarioId");
                                USUARIONOMBRE =jsonObject.getString("usuarioNombre");

                                USUARIOEMPLEADO =objectUser.getString("NombreEmpleado");
                                EMPLEADOID =objectUser.getInt("EmpleadoID")+"";
                                USUARIOCORREO =jsonObject.getString("correo");

                                USUARIOROL =jsonObject.getString("rol");

                                respuestaLogin = jsonObject.getBoolean("respuesta");
                                Log.e("Respuesta Login", String.valueOf(respuestaLogin));

                                Log.e("Log EMPLEADOID", String.valueOf(EMPLEADOID));

                                //mensajeLogin = mensaje;

                                session.createLoginSession(USUARIONOMBRE,EMPLEADOID, USUARIOID,USUARIOEMPLEADO,USUARIOCORREO,USUARIOROL);

                                //starting the profile activity
                                finish();
                                startActivity(new Intent(getApplicationContext(), ActividadPrincipal.class));
                            } else {
                                //Toast.makeText(getApplicationContext(), jsonObject.getString("mensaje"), Toast.LENGTH_SHORT).show();
                                Snackbar.make(vista, jsonObject.getString("mensaje"), Snackbar.LENGTH_LONG)
                                        .setAction("Action", null).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        if (error instanceof TimeoutError || error instanceof NoConnectionError) {

                            DynamicToast.makeWarning(getBaseContext(), "Error Tiempo de Respuesta, Vuelva ha iniciar sesión", Toast.LENGTH_LONG).show();
                        }
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("usuLogin", username);
                params.put("usuPassword", password);
                return params;
            }
        };

        //VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);

        //AppSin
        VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);


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

        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        btnIngresarPantallaTokenWeb = findViewById(R.id.btnIngresarSisWebToken);
        handler.postDelayed(runnable, 2000); //2000 is the timeout for the splash
        btnIngresarPantallaTokenWeb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                Intent intent1 = new Intent(SplashScreenActivity.this,WebTokenActivity.class);
                startActivity(intent1);
            }
        });

        btnIngresarLogin = findViewById(R.id.btnIngresarLogin);
        btnIngresarLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                progressDialog = new ProgressDialog(SplashScreenActivity.this);
                progressDialog.setMessage("Espere...");
                progressDialog.setIndeterminate(false);
                progressDialog.setCancelable(false);
                progressDialog.show();

                ValidacionLogin();
                //Intent intentPantalla = new Intent(SplashScreenActivity.this,ActividadPrincipal.class);
                //startActivity(intentPantalla);
            }
        });
        /*new Handler().postDelayed(new Runnable(){
            public void run(){
                Intent intent = new Intent(SplashScreenActivity.this, WebTokenActivity.class);
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
