package com.example.s3k_user1.appzonas;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
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
import com.example.s3k_user1.appzonas.Sesion.SessionManager;
import com.pranavpandey.android.dynamic.toasts.DynamicToast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class DetalleDocumentosActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {
    //https://www.androidhive.info/2012/08/android-session-management-using-shared-preferences/

    TextView tituloDetalle;
    private RecyclerView recyclerView;
    private List<Documento> documentoList;
    private DetalleDocumentosActivity.StoreAdapter mAdapter;
    private View vista;
    private String IP_LEGAL = MapsActivity.IP_APK;
    private static final String TAG = DetalleDocumentosActivity.class.getSimpleName();
    SwipeRefreshLayout mSwipeRefreshLayout;
        //https://prmadi.com/handling_volley_request_when_network_connection_is_slow/
    private String EstadoDoc = "";
    SessionManager session;
    String usuario = "";

    // email
    String id = "";
    public void DocumentoPorEspecialistaListarExternoJson() {
        //https://api.myjson.com/bins/wicz0
        //String url = "http://192.168.0.12/documentosLista.json";
        mSwipeRefreshLayout.setRefreshing(true);
        Log.e(TAG,"E USUARIO ID: "+ id);
        Log.w(TAG,"W USUARIO ID: "+ id);
        Toast.makeText(this, "E USUARIO ID: "+ IP_LEGAL +" - "+ id
                , Toast.LENGTH_SHORT).show();
        String url = IP_LEGAL + "/legal/Documento/DocumentoPorEspecialistaListarExternoJson?estadoProceso="+EstadoDoc+"&usuarioId="+id;
        JsonObjectRequest JsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                url, (String) null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        JSONArray jRoutes = null;
                        try {
                            jRoutes = response.getJSONArray("data");
                            for (int i = 0; i < jRoutes.length(); i++) {
                                JSONObject jsonObject = jRoutes.getJSONObject(i);

                                Documento documentoNew = new Documento();
                                documentoNew.setNombre(jsonObject.getString("NombreArchivo"));
                                documentoNew.setDescripcion(jsonObject.getString("Nemonico"));
                                documentoNew.setTipoContrato(jsonObject.getString("SubTipoServicio"));
                                documentoNew.setFecha(jsonObject.getString("FechaRegistroString"));

                                documentoList.add(documentoNew);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Error: " + error.getMessage());
                mSwipeRefreshLayout.setRefreshing(false);
                if (error instanceof TimeoutError || error instanceof NoConnectionError) {

                    DynamicToast.makeWarning(getBaseContext(), "Error Tiempo de Respuesta, Vuelva ha iniciar sesión", Toast.LENGTH_LONG).show();
                }
            }
        });
        //JsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(7000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES,DefaultRetryPolicy.DEFAULT_MAX_RETRIES));
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(JsonObjectRequest);
        //
    }

    public void obtenerDatosDocumentosJson() {
        //https://api.myjson.com/bins/wicz0
        //String url = "http://192.168.0.12/documentosLista.json";
        String url = IP_LEGAL + "/legal/Documento/DocumentoListarExternoJson?estadoProcesoId="+EstadoDoc;
        JsonObjectRequest JsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                url, (String) null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        JSONArray jRoutes = null;
                        try {
                            jRoutes = response.getJSONArray("documentosLista");
                            for (int i = 0; i < jRoutes.length(); i++) {
                                JSONObject jsonObject = jRoutes.getJSONObject(i);

                                Documento documentoNew = new Documento();
                                documentoNew.setNombre(jsonObject.getString("NombreArchivo"));
                                documentoNew.setDescripcion(jsonObject.getString("Nemonico"));
                                documentoNew.setTipoContrato(jsonObject.getString("SubTipoServicio"));
                                documentoNew.setFecha(jsonObject.getString("FechaRegistroString"));

                                documentoList.add(documentoNew);
                            }
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
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_documentos);

        session = new SessionManager(getApplicationContext());
        session.checkLogin();
        HashMap<String, String> user = session.getUserDetails();
        usuario = user.get(SessionManager.KEY_USUARIO_NOMBRE);
        id = user.get(SessionManager.KEY_USUARIO_ID);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        String intentDocId = getIntent().getExtras().getString("vIdEstadoDoc");
        EstadoDoc = intentDocId;
        String intentDocNombre = getIntent().getExtras().getString("vNombreDoc");
        toolbar.setTitle(getIntent().getExtras().getString("vNombreDoc"));
        setSupportActionBar(toolbar);



        vista= findViewById(R.id.act_det_document);



        recyclerView = findViewById(R.id.detalle_documento_recycler_view);
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeToRefresh);




        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);
//        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//            @Override
//            public void onRefresh() {
//                shuffle();
//                mSwipeRefreshLayout.setRefreshing(false);
//            }
//        });


        documentoList = new ArrayList<>();
        if(EstadoDoc.equals("0")){
            DocumentoPorEspecialistaListarExternoJson();
        }else{
            obtenerDatosDocumentosJson();
        }

        mAdapter = new StoreAdapter(this, documentoList,mSwipeRefreshLayout);

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 1);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(1, dpToPx(6), true));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        mSwipeRefreshLayout.setRefreshing(true);

                                        DocumentoPorEspecialistaListarExternoJson();
                                    }
                                }
        );
        recyclerView.setNestedScrollingEnabled(false);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);




    }

    public void shuffle(){
        //Collections.shuffle(documentoList, new Random(System.currentTimeMillis()));
        //StoreAdapter adapter = new StoreAdapter(DetalleDocumentosActivity.this, documentoList);
        //recyclerView.setAdapter(adapter);
    }

    @Override
    public void onRefresh() {
        DocumentoPorEspecialistaListarExternoJson();
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

    class StoreAdapter extends RecyclerView.Adapter<DetalleDocumentosActivity.StoreAdapter.MyViewHolder> {
        Context contextDetalleDocumento;
        private List<Documento> movieList;
        Dialog myDialog;

        Button botonAprobar, botonCancelar;

        SwipeRefreshLayout mSwipeRefreshLayoutStore;

        public class MyViewHolder extends RecyclerView.ViewHolder {
            public CardView detalle_documentoCardView;
            public TextView name, fecha;
            public ImageView thumbnail;
            public LinearLayout det_doc_item_layout;


            public MyViewHolder(View view) {
                super(view);
                det_doc_item_layout = findViewById(R.id.det_doc_item_layout);
                detalle_documentoCardView = (CardView) view.findViewById(R.id.detalle_documento_card_view);
                name = view.findViewById(R.id.detalle_documento_title);
                fecha = view.findViewById(R.id.det_doc_fecha);
            }
        }


        public StoreAdapter(Context context, List<Documento> movieList, SwipeRefreshLayout swip) {
            this.contextDetalleDocumento = context;
            this.movieList = movieList;
            this.mSwipeRefreshLayoutStore= swip;
        }

        @Override
        public DetalleDocumentosActivity.StoreAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.detalle_documentos_item, parent, false);

            return new DetalleDocumentosActivity.StoreAdapter.MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(DetalleDocumentosActivity.StoreAdapter.MyViewHolder holder, final int position) {
            final Documento documentoViewHolder = documentoList.get(position);
            holder.name.setText(documentoViewHolder.getNombre());

            holder.fecha.setText(documentoViewHolder.getFecha());
            //TODO inicializar Dialog
            myDialog = new Dialog(contextDetalleDocumento);
            myDialog.setContentView(R.layout.act_det_doc_dialog_doc);

            holder.detalle_documentoCardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    TextView documento =  myDialog.findViewById(R.id.det_doc_tit_doc);
                    TextView memonico =  myDialog.findViewById(R.id.det_doc_tit_subt);
                    TextView tipoContrato =  myDialog.findViewById(R.id.det_doc_tipo_contrato);
                    TextView fecha =  myDialog.findViewById(R.id.det_doc_fecha);
                    documento.setText(documentoViewHolder.getNombre());
                    memonico.setText("Memonico:" + documentoViewHolder.getDescripcion());
                    tipoContrato.setText("Tipo Contrato:" + documentoViewHolder.getTipoContrato());
                    fecha.setText(documentoViewHolder.getFecha());
                    botonAprobar = myDialog.findViewById(R.id.btnAprobar);
                    botonCancelar = myDialog.findViewById(R.id.btnCancelar);
                    myDialog.show();


                    botonCancelar.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {

                            Bundle bundle = new Bundle();

                            bundle.putString("vNombreDocRechazar", documentoViewHolder.getNombre());
                            Intent newDDocumentsActivity = new Intent(v.getContext(), RechazarDocumentoActivity.class);
                            newDDocumentsActivity.putExtras(bundle);
                            v.getContext().startActivity(newDDocumentsActivity);
                        }
                    });
                    botonAprobar.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {


                            /*AlertDialog.Builder builder = new AlertDialog.Builder(contextDetalleDocumento);
                            builder.setTitle("Confirmar");
                            builder.setMessage("Seguro que desea aprobar documento");

                            builder.setCancelable(true);
                            builder.setNeutralButton("Ok", new DialogInterface.OnClickListener(){
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    myDialog.hide();
                                    Snackbar.make(vista, "Documento Aprobado", Snackbar.LENGTH_LONG)
                                            .setAction("Action", null).show();

                                }
                            });
                            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });
                            builder.show();*/
                            myDialog.hide();
                            Snackbar.make(vista, "Documento Aprobado", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                        }
                    });
                }
            });
            //Glide.with(context).load(movie.get(0));

            /*
            * final Movie movie = movieList.get(position);
            holder.name.setText(movie.getTitle());
            holder.price.setText(movie.getPrice());

            Glide.with(context)
                    .load(movie.getImage())
                    .into(holder.thumbnail);
            *
            * */

            mSwipeRefreshLayoutStore.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    documentoList.clear();
                    obtenerDatosDocumentosJson();
                    refresh();
                }
            });
        }
        private void refresh(){
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    StoreAdapter.this.notifyDataSetChanged();
                    mSwipeRefreshLayoutStore.setRefreshing(false);
                }
            },4000);
        }
        @Override
        public int getItemCount() {
            return movieList.size();
        }
    }

}
