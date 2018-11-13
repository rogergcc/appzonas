package com.example.s3k_user1.appzonas;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.example.s3k_user1.appzonas.Model.EstadoProceso;
import com.example.s3k_user1.appzonas.Sesion.SessionManager;
import com.pranavpandey.android.dynamic.toasts.DynamicToast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class DocumentosActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private List<EstadoProceso> estadoProcesos;
    private StoreAdapter mAdapter;
    private String IP_LEGAL = MapsActivity.IP_APK;
    private static final String TAG = DocumentosActivity.class.getSimpleName();
    public String EstadoID="";
    SessionManager session;
    String usuario = "";

    // email
    String id = "";

    public void obtenerEstadoProcesoStatusTramiteJson() {
        final List<EstadoProceso> procesoListScope = new ArrayList<>();
        //https://api.myjson.com/bins/wicz0
        //String url = "http://192.168.0.12/documentosLista.json";
        String url = IP_LEGAL + "/legal/EstadoProceso/EstadoProcesoListarJsonExterno?usuarioId="+id;
        Log.e("EstadoProcesoStatus: ", url);
        JsonObjectRequest JsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                url, (String) null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e("EstadoProcesoStatus: ", response.toString());
                        JSONArray jRoutes = null;
                        try {
                            jRoutes = response.getJSONArray("estadosprocesos");
                            for (int i = 0; i < jRoutes.length(); i++) {
                                JSONObject jsonObject = jRoutes.getJSONObject(i);

                                EstadoProceso estadoProceso = new EstadoProceso();
                                estadoProceso.setEstadoProcesoId(jsonObject.getString("EstadoProcesoId"));
                                estadoProceso.setNombre(jsonObject.getString("Nombre"));
                                estadoProceso.setDescripcion(jsonObject.getString("Descripcion"));
                                estadoProceso.setTipo(jsonObject.getString("Tipo"));
                                estadoProceso.setEstado(jsonObject.getString("Estado"));
                                estadoProceso.setCantidadDocsSegunEstadoProceso(jsonObject.getString("CantidaDocumentoxEstado"));
                                //String estadoID = estadoProceso.getEstadoProcesoId();
                                //estadoProceso.setCantidadDocsSegunEstadoProceso(obtenerDatosDocumentosJson(estadoID));
                                estadoProcesos.add(estadoProceso);
                                //estadoProcesos.addAll(procesoListScope);
                            }
                            Log.e("estadoProcesos.size Sco", String.valueOf(procesoListScope.size()));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Error: " + error.getMessage());

                if (error instanceof TimeoutError || error instanceof NoConnectionError) {

                    DynamicToast.makeWarning(getBaseContext(), "Error Tiempo de Respuesta", Toast.LENGTH_LONG).show();
                }
            }
        });
        //Log.e("SIZE", String.valueOf(estadoProcesos.get(2).toString()));
        //JsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(7000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES,DefaultRetryPolicy.DEFAULT_MAX_RETRIES));
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(JsonObjectRequest);
        //

    }
    public String obtenerDatosDocumentosJson(String estadoId) {
        //https://api.myjson.com/bins/wicz0
        //String url = "http://192.168.0.12/documentosLista.json";
        final String[] cantidadRegistros = new String[1];
        String url = IP_LEGAL + "/legal/Documento/DocumentoListarExternoJson?estadoProcesoId="+estadoId;
        JsonObjectRequest JsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                url, (String) null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        String cantida = "";
                        JSONObject jsonObject = response;
                        //Log.w(getCallingActivity().getClassName(), response.toString());


                        try {
                            EstadoID = (response.getString("cantidad"));
                            cantida = (response.getString("cantidad"));
                            cantidadRegistros[0] = cantida;

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }



                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Error: " + error.getMessage());

                if (error instanceof TimeoutError || error instanceof NoConnectionError) {

                    DynamicToast.makeWarning(getBaseContext(), "Error Tiempo de Respuesta, Docs", Toast.LENGTH_LONG).show();
                }
            }
        });
        //JsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(7000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES,DefaultRetryPolicy.DEFAULT_MAX_RETRIES));
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(JsonObjectRequest);
        //
        return EstadoID;
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.sesion_usuario, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        String msg ="";Fragment fragment;
        switch (item.getItemId()) {

            case R.id.cerrar_sesion:
                msg="Sesion";
                session.logoutUser();
                Intent intent1 = new Intent(this,SplashScreenActivity.class);
                startActivity(intent1);
                return true;



        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_documentos);
        session = new SessionManager(getApplicationContext());
        session.checkLogin();
        HashMap<String, String> user = session.getUserDetails();
        usuario = user.get(SessionManager.KEY_USUARIO_NOMBRE);
        id = user.get(SessionManager.KEY_USUARIO_ID);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        recyclerView = findViewById(R.id.documento_recycler_view);
        estadoProcesos = new ArrayList<>();
        EstadoProceso estaProcesoPrimero = new EstadoProceso();
        estaProcesoPrimero.setNombre("POR APROBAR");
        estaProcesoPrimero.setEstadoProcesoId("26");
        estaProcesoPrimero.setCantidadDocsSegunEstadoProceso("0");
        estadoProcesos.add(estaProcesoPrimero);
        obtenerEstadoProcesoStatusTramiteJson();

        String datos="";
        for (int i =0;i<estadoProcesos.size();i++){
            datos +=estadoProcesos.get(i).getEstadoProcesoId() +
                    " - "+estadoProcesos.get(i).getNombre()+"- "
                    +estadoProcesos.get(i).getCantidadDocsSegunEstadoProceso();
        }
        Log.e("DDDDD", datos);
        mAdapter = new StoreAdapter(this, estadoProcesos);

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 1);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(1, dpToPx(6), true));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
        Log.e("estadoProcesos.size X", String.valueOf(estadoProcesos.size()));
        recyclerView.setNestedScrollingEnabled(false);
        

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Documentos");
    }

    public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

        private int spanCount;
        private int spacing;
        private boolean includeEdge;

        public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
            this.spanCount = spanCount;
            this.spacing = spacing;
            this.includeEdge = includeEdge;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view); // item position
            int column = position % spanCount; // item column

            if (includeEdge) {
                outRect.left = spacing - column * spacing / spanCount; // spacing - column * ((1f / spanCount) * spacing)
                outRect.right = (column + 1) * spacing / spanCount; // (column + 1) * ((1f / spanCount) * spacing)

                if (position < spanCount) { // top edge
                    outRect.top = spacing;
                }
                outRect.bottom = spacing; // item bottom
            } else {
                outRect.left = column * spacing / spanCount; // column * ((1f / spanCount) * spacing)
                outRect.right = spacing - (column + 1) * spacing / spanCount; // spacing - (column + 1) * ((1f /    spanCount) * spacing)
                if (position >= spanCount) {
                    outRect.top = spacing; // item top
                }
            }
        }
    }

    /**
     * Converting dp to pixel
     */
    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }

    class StoreAdapter extends RecyclerView.Adapter<StoreAdapter.MyViewHolder> {
        private Context context;
        private List<EstadoProceso> estadoProcesoList;

        public class MyViewHolder extends RecyclerView.ViewHolder {
            public CardView documentoCardView;
            public TextView title, subtitle,cantidad;
            public ImageView thumbnail;

            public MyViewHolder(View view) {
                super(view);
                documentoCardView = (CardView) view.findViewById(R.id.documento_card_view);
                title = view.findViewById(R.id.titleDocItem);
                subtitle = view.findViewById(R.id.subtitleDocItem);
                cantidad = view.findViewById(R.id.cantidadRe);
            }
        }


        public StoreAdapter(Context context, List<EstadoProceso> estadoProcesoList) {
            this.context = context;
            this.estadoProcesoList = estadoProcesoList;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.documentos_item, parent, false);

            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, final int position) {
            final EstadoProceso estadoProcesoViewHolder = estadoProcesos.get(position);

            holder.title.setText(estadoProcesoViewHolder.getNombre());
            holder.subtitle.setText(estadoProcesoViewHolder.getTipo());
            holder.cantidad.setText(estadoProcesoViewHolder.getCantidadDocsSegunEstadoProceso());
            holder.documentoCardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    bundle.putString("vIdEstadoDoc", estadoProcesoViewHolder.getEstadoProcesoId());
                    bundle.putString("vNombreDoc", estadoProcesoViewHolder.getNombre());


                    Intent newDDocumentsActivity = new Intent(v.getContext(), DetalleDocumentosActivity.class);
                    newDDocumentsActivity.putExtras(bundle);
                    v.getContext().startActivity(newDDocumentsActivity);
                }
            });
            //Glide.with(context).load(movie.get(0));

            /*
            * final Movie movie = estadoProcesoList.get(position);
            holder.name.setText(movie.getTitle());
            holder.price.setText(movie.getPrice());

            Glide.with(context)
                    .load(movie.getImage())
                    .into(holder.thumbnail);
            *
            * */
        }

        @Override
        public int getItemCount() {
            return estadoProcesoList.size();
        }
    }
}
