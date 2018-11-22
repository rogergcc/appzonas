package com.example.s3k_user1.appzonas;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.Volley;
import com.example.s3k_user1.appzonas.Model.Documento;
import com.example.s3k_user1.appzonas.Sesion.SessionManager;
import com.example.s3k_user1.appzonas.app.AppSingleton;
import com.example.s3k_user1.appzonas.app.MyApplication;
import com.pranavpandey.android.dynamic.toasts.DynamicToast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
    String perfil = "";
    int cantidadDePetiticiones=0;
    private String respuestaRevisizarDocuento = "";

    ProgressDialog progressDialog;

    public void DocumentoPorEspecialistaListarExternoJson() {
        String  REQUEST_TAG = "com.example.s3k_user1.appzonas";



        //progressDialog.setCancelable(false);
        //progressDialog.setIndeterminate(true);
        //progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mSwipeRefreshLayout.setRefreshing(true);

        cantidadDePetiticiones++;

        Log.e(TAG,"CANTIDA DE PETICIONES: "+ cantidadDePetiticiones);
        Log.w(TAG,"W USUARIO ID: "+ id);

        if ( cantidadDePetiticiones<=1){
        //Toast.makeText(this, "E USUARIO ID: "+ IP_LEGAL +" - "+ id, Toast.LENGTH_SHORT).show();
        String url = IP_LEGAL + "/legal/Documento/DocumentoPorEspecialistaListarExternoJson?estadoProcesoId="+EstadoDoc+"&usuarioId="+id;

        JsonObjectRequest JsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                url, (String) null,
                new Response.Listener<JSONObject>() {


                    @Override
                    public void onResponse(JSONObject response) {
                        Log.w(TAG,response.toString());
                        progressDialog.dismiss();
                        JSONArray jRoutes = null;
                        try {
                            jRoutes = response.getJSONArray("data");
                            for (int i = 0; i < jRoutes.length(); i++) {
                                JSONObject jsonObject = jRoutes.getJSONObject(i);

                                Documento documentoNew = new Documento();
                                documentoNew.setDocumentoId(jsonObject.getInt("DocumentoId"));
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
                        //progressDialog.hide();

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Error: " + error.getMessage());
                //progressDialog.hide();
                progressDialog.dismiss();
                mSwipeRefreshLayout.setRefreshing(false);
                if (error instanceof TimeoutError || error instanceof NoConnectionError) {

                    DynamicToast.makeWarning(getBaseContext(), "Error Tiempo de Respuesta, Vuelva ha iniciar sesión", Toast.LENGTH_LONG).show();
                }
            }
        }) {
            @Override
                public Request.Priority getPriority() {
                return Priority.HIGH;
            }
        };


        //JsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(7000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES,DefaultRetryPolicy.DEFAULT_MAX_RETRIES));
        /*JsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));*/

        //RequestQueue requestQueue = Volley.newRequestQueue(DetalleDocumentosActivity.this);
            // requestQueue.add(JsonObjectRequest);
            // MyApplication.getInstance().addToRequestQueue(JsonObjectRequest);

            AppSingleton.getInstance(getApplicationContext()).addToRequestQueue(JsonObjectRequest,REQUEST_TAG);
        }

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
        })
            {@Override
                public Request.Priority getPriority() {
                return Priority.NORMAL;
            };
        };



        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(JsonObjectRequest);

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
        setContentView(R.layout.activity_detalle_documentos);



        session = new SessionManager(getApplicationContext());
        session.checkLogin();
        HashMap<String, String> user = session.getUserDetails();
        usuario = user.get(SessionManager.KEY_USUARIO_NOMBRE);
        id = user.get(SessionManager.KEY_USUARIO_ID);

        perfil = user.get(SessionManager.KEY_USUARIO_ROL);



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
//        if(EstadoDoc.equals("26")){
//            DocumentoPorEspecialistaListarExternoJson();
//        }else{
//            obtenerDatosDocumentosJson();
//        }
        progressDialog = new ProgressDialog(DetalleDocumentosActivity.this);
        progressDialog.setMessage("Loading...");
        progressDialog.setIndeterminate(false);
        progressDialog.setCancelable(false);
        progressDialog.show();

        DocumentoPorEspecialistaListarExternoJson();
        mAdapter = new StoreAdapter(this, documentoList,mSwipeRefreshLayout);

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 1);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(1, dpToPx(6), true));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
        //mSwipeRefreshLayout.setOnRefreshListener(this);
        /*mSwipeRefreshLayout.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        mSwipeRefreshLayout.setRefreshing(true);
                                        //documentoList.clear();
                                        DocumentoPorEspecialistaListarExternoJson();
                                    }
                                }
        );*/
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
        //documentoList.clear();
        DocumentoPorEspecialistaListarExternoJson();
        mSwipeRefreshLayout.setRefreshing(false);
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
            final int documentoId = documentoViewHolder.getDocumentoId();
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
                            bundle.putInt("vUsuarioId", Integer.parseInt(id));
                            bundle.putInt("vDocumentoId", documentoViewHolder.getDocumentoId());
                            bundle.putString("vPerfil", perfil);
                            bundle.putString("vRechazar", "Si");
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
                            RevizarDocumentoJson(Integer.parseInt(id),documentoId,"No","",perfil);
                            Snackbar.make(vista, respuestaRevisizarDocuento, Snackbar.LENGTH_LONG)
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
            //TODO COMENTANDO  SWIPE
            mSwipeRefreshLayoutStore.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    //documentoList.clear();
                    DocumentoPorEspecialistaListarExternoJson();
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
