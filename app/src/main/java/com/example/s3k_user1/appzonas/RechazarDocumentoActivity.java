package com.example.s3k_user1.appzonas;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
import com.pranavpandey.android.dynamic.toasts.DynamicToast;

import org.json.JSONException;
import org.json.JSONObject;

public class RechazarDocumentoActivity extends AppCompatActivity {
    TextView rechazo_doc_texto, rechazo_doc_observacion;
    private String IP_LEGAL = MapsActivity.IP_APK;
    Button btnRechazar;
    private String respuestaRevisizarDocuento = "";
    View vista;
    public void RevizarDocumentoJson(int usuarioId, int documentoId, String esRechazado, String observacion, String perfil) {
        //RevizarDocumentoJson(int usuarioId, int documentoId, string esRechazado, string observacion, string perfil)
        String url = IP_LEGAL+"/legal/RevisionDocumento/RevizarDocumentoJson?usuarioId="+usuarioId+"&documentoId="+documentoId+ "&esRechazado="+esRechazado + "&observacion="+observacion+ "&perfil="+perfil;
        Log.w("URL LOGIN: ", url);
        Log.w("datos parametros: ", usuarioId+" - docid: "+documentoId +" - "+ esRechazado+" - "+observacion+ " - "+perfil );
        JsonObjectRequest JsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                url, (String) null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        Log.e("MENSAJE VALIDA: ", jsonObject.toString());
                        try {
                            //JSONObject objectUser = jsonObject.getJSONObject("usuario");

                            String respuesta = jsonObject.getString("mensaje");
                            //String mensaje = jsonObject.getString("mensaje");

                            respuestaRevisizarDocuento= respuesta;

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
        });



        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(JsonObjectRequest);

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rechazar_documento);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        String intentDocumentoARechazar = getIntent().getExtras().getString("vNombreDocRechazar");

        final int UsuarioId = getIntent().getExtras().getInt("vUsuarioId");
        final int DocumentoId = getIntent().getExtras().getInt("vDocumentoId");
        final String Perfil = getIntent().getExtras().getString("vPerfil");
        final String Rechazar = getIntent().getExtras().getString("vRechazar");

        vista= findViewById(R.id.act_rech_doc);

        rechazo_doc_texto = findViewById(R.id.rechazo_doc_texto);
        rechazo_doc_texto.setText(intentDocumentoARechazar);

        btnRechazar= findViewById(R.id.btnRechazar);

        rechazo_doc_observacion= findViewById(R.id.rechazo_doc_observacion);


        setSupportActionBar(toolbar);


        btnRechazar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (rechazo_doc_observacion.getText().toString().equals("")){
                    Snackbar.make(vista, "Ingrese una Observacion", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }else{
                    RevizarDocumentoJson(UsuarioId,DocumentoId,Rechazar, rechazo_doc_observacion.getText().toString(),Perfil);
                    Snackbar.make(vista, respuestaRevisizarDocuento, Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }

            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Rechazar Documento");
    }

}
