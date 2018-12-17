package com.example.s3k_user1.appzonas;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
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

import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.s3k_user1.appzonas.Model.Documento;
import com.example.s3k_user1.appzonas.Others.UploadImageActivity;
import com.example.s3k_user1.appzonas.Sesion.SessionManager;
import com.example.s3k_user1.appzonas.app.AppSingleton;
import com.pranavpandey.android.dynamic.toasts.DynamicToast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;



public class EstadosDocsEnTramiteActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {


    TextView tituloDetalle;
    private RecyclerView recyclerView;
    private List<Documento> documentoList;
    private EstadosDocsEnTramiteActivity.StoreAdapter mAdapter;
    private View vista;
    private String IP_LEGAL = WebTokenActivity.IP_APK;
    private static final String TAG = EstadosDocsEnTramiteActivity.class.getSimpleName();
    SwipeRefreshLayout mSwipeRefreshLayout;
    //https://prmadi.com/handling_volley_request_when_network_connection_is_slow/

    private String EstadoDoc = "";
    private String NombreDoc = "";
    SessionManager session;

    String usuario = "";

    // email
    String usuarioId = "";
    String empleadoId = "";
    String perfil = "";
    int cantidadDePetiticiones=0;
    private String respuestaRevisizarDocuento = "";

    ProgressDialog progressDialog;
    FloatingActionButton floatingActionButton;

    //METODOS
    public void DocumentoPorEspecialistaListarExternoJson() {
        // TODO  POR APROBAR
        String  REQUEST_TAG = "com.example.s3k_user1.appzonas";
        String servicio = "";
        if (perfil.equals("1003") || perfil.equals("1004")) {
            servicio="DocumentoPorResponsableRevisionListarExternoJson";
        } else {
            servicio="DocumentoPorEspecialistaListarExternoJson";
        }
        cantidadDePetiticiones++;
        Log.e(TAG,"CANTIDA DE PETICIONES: "+ cantidadDePetiticiones);


        if ( cantidadDePetiticiones<=1){
            //Toast.makeText(this, "E USUARIO ID: "+ IP_LEGAL +" - "+ id, Toast.LENGTH_SHORT).show();
            String url = IP_LEGAL + "/legal/Documento/"+servicio+"?estadoProcesoId="+EstadoDoc+"&usuarioId="+usuarioId;
            Log.w("PorEspecialista url:",url);
            JsonObjectRequest JsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                    url, (String) null,
                    new Response.Listener<JSONObject>() {


                        @Override
                        public void onResponse(JSONObject response) {
                            Log.w(TAG,response.toString());
                            documentoList.clear();
                            hidepDialog();
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
                                    //Log.wtf("Doc List Dentro del Method",documentoList.get(0).getFecha());
                                    mAdapter.notifyDataSetChanged();
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
                    hidepDialog();
                    mSwipeRefreshLayout.setRefreshing(false);
                    if (error instanceof TimeoutError || error instanceof NoConnectionError) {

                        DynamicToast.makeWarning(getBaseContext(), "Error Tiempo de Respuesta, Vuelva solitar Documentos POR APROBAR", Toast.LENGTH_LONG).show();
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

    public void ListarDocumentosAprobadosApp() {
        //TODO APROBADOS
        String  REQUEST_TAG = "com.example.s3k_user1.appzonas";
        cantidadDePetiticiones++;

        if ( cantidadDePetiticiones<=1){
            //Toast.makeText(this, "E USUARIO ID: "+ IP_LEGAL +" - "+ id, Toast.LENGTH_SHORT).show();
            String url = IP_LEGAL + "/legal/RevisionDocumento/ListarDocumentosAprobadosApp?empleadoId="+empleadoId+"&perfil="+perfil;
            Log.w("URL AprobadosApp: ", url);
            JsonObjectRequest JsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                    url, (String) null,
                    new Response.Listener<JSONObject>() {


                        @Override
                        public void onResponse(JSONObject response) {
                            Log.w(TAG,response.toString());
                            documentoList.clear();
                            hidepDialog();
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
                                    //Log.wtf("Doc List Dentro del Method",documentoList.get(0).getFecha());
                                    mAdapter.notifyDataSetChanged();
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
                    hidepDialog();
                    mSwipeRefreshLayout.setRefreshing(false);
                    if (error instanceof TimeoutError || error instanceof NoConnectionError) {

                        DynamicToast.makeWarning(getBaseContext(), "Error Tiempo de Respuesta, Vuelva solicitar Documentos Aprobados", Toast.LENGTH_LONG).show();
                    }
                }
            }) {
                @Override
                public Request.Priority getPriority() {
                    return Priority.HIGH;
                }
            };

            AppSingleton.getInstance(getApplicationContext()).addToRequestQueue(JsonObjectRequest,REQUEST_TAG);
        }

    }

    public void ListarDocumentosRechazadosApp() {
        //TODO RECHAZADOS
        String  REQUEST_TAG = "com.example.s3k_user1.appzonas";
        cantidadDePetiticiones++;
        if ( cantidadDePetiticiones<=1){
            //Toast.makeText(this, "E USUARIO ID: "+ IP_LEGAL +" - "+ id, Toast.LENGTH_SHORT).show();
            String url = IP_LEGAL + "/legal/RevisionDocumento/ListarDocumentosRechazadosApp?empleadoId="+empleadoId+"&perfil="+perfil;
            Log.w("URL RechazadosApp: ", url);
            JsonObjectRequest JsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                    url, (String) null,
                    new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject response) {
                            Log.w(TAG,response.toString());
                            documentoList.clear();
                            hidepDialog();
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
                                    //Log.wtf("Doc List Dentro del Method",documentoList.get(0).getFecha());
                                    mAdapter.notifyDataSetChanged();
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
                    hidepDialog();
                    mSwipeRefreshLayout.setRefreshing(false);
                    if (error instanceof TimeoutError || error instanceof NoConnectionError) {

                        DynamicToast.makeWarning(getBaseContext(), "Error Tiempo de Respuesta, Vuelva solicitar Documentos Rechazados", Toast.LENGTH_LONG).show();
                    }
                }
            }) {
                @Override
                public Request.Priority getPriority() {
                    return Priority.HIGH;
                }
            };

            AppSingleton.getInstance(getApplicationContext()).addToRequestQueue(JsonObjectRequest,REQUEST_TAG);
        }

    }

    public void ListarDocumentosPorControlStatusApp() {
        //TODO STATUS TRAMITE
        String  REQUEST_TAG = "com.example.s3k_user1.appzonas";
        cantidadDePetiticiones++;
        if ( cantidadDePetiticiones<=1){
            //Toast.makeText(this, "E USUARIO ID: "+ IP_LEGAL +" - "+ id, Toast.LENGTH_SHORT).show();
            String url = IP_LEGAL + "/legal/RevisionDocumento/ListarDocumentosPorControlStatusApp?empleadoId="+empleadoId;
            Log.w("URL RechazadosApp: ", url);
            JsonObjectRequest JsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                    url, (String) null,
                    new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject response) {
                            Log.w(TAG,response.toString());
                            documentoList.clear();
                            hidepDialog();
                            JSONArray jRoutes = null;
                            try {
                                jRoutes = response.getJSONArray("data");

                                for (int i = 0; i < jRoutes.length(); i++) {
                                    JSONObject jsonObject = jRoutes.getJSONObject(i);

                                    Documento documentoNew = new Documento();
                                    documentoNew.setDocumentoId(jsonObject.getInt("DocumentoId"));
                                    //documentoNew.setNombre(jsonObject.getString("NombreArchivo"));
                                    documentoNew.setStatus(jsonObject.getString("Status"));
                                    documentoNew.setDescripcion(jsonObject.getString("Nemonico"));
                                    documentoNew.setTipoContrato(jsonObject.getString("SubTipoServicio"));
                                    documentoNew.setFecha(jsonObject.getString("FechaRegistroString"));

                                    documentoList.add(documentoNew);
                                    //Log.wtf("Doc List Dentro del Method",documentoList.get(0).getFecha());
                                    mAdapter.notifyDataSetChanged();
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
                    hidepDialog();
                    mSwipeRefreshLayout.setRefreshing(false);
                    if (error instanceof TimeoutError || error instanceof NoConnectionError) {

                        DynamicToast.makeWarning(getBaseContext(), "Error Tiempo de Respuesta, Vuelva solicitar Documentos Rechazados", Toast.LENGTH_LONG).show();
                    }
                }
            }) {
                @Override
                public Request.Priority getPriority() {
                    return Priority.HIGH;
                }
            };

            AppSingleton.getInstance(getApplicationContext()).addToRequestQueue(JsonObjectRequest,REQUEST_TAG);
        }

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_estados_docs_en_tramite);



        session = new SessionManager(getApplicationContext());
        session.checkLogin();
        HashMap<String, String> user = session.getUserDetails();
        usuario = user.get(SessionManager.KEY_USUARIO_NOMBRE);
        usuarioId = user.get(SessionManager.KEY_USUARIO_ID);
        empleadoId = user.get(SessionManager.KEY_EMPLEADO_ID);
        perfil = user.get(SessionManager.KEY_USUARIO_ROL);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        String intentDocId = getIntent().getExtras().getString("vIdEstadoDoc");
        EstadoDoc = intentDocId;

        String intentDocNombre = getIntent().getExtras().getString("vNombreDoc");
        NombreDoc = intentDocNombre;

        toolbar.setTitle(getIntent().getExtras().getString("vNombreDoc"));
        setSupportActionBar(toolbar);



        vista= findViewById(R.id.act_EnTramite);



        recyclerView = findViewById(R.id.EnTramite_recycler_view);
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

        progressDialog = new ProgressDialog(EstadosDocsEnTramiteActivity.this);
        progressDialog.setMessage("Espere...");
        progressDialog.setIndeterminate(false);
        progressDialog.setCancelable(false);

        showpDialog();

        //DocumentoPorEspecialistaListarExternoJson();
        if(NombreDoc.equals("POR APROBAR")){
            DocumentoPorEspecialistaListarExternoJson();
        }else if(NombreDoc.equals("APROBADOS")){
            ListarDocumentosAprobadosApp();
        }else if(NombreDoc.equals("RECHAZADOS")){
            ListarDocumentosRechazadosApp();
        }else if(NombreDoc.equals("STATUS TRAMITE")){
            ListarDocumentosPorControlStatusApp();
        }

        floatingActionButton = findViewById(R.id.fabDocsEnTramite);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                poblarRecyclerView();
            }
        });
        mAdapter = new StoreAdapter(this, documentoList,mSwipeRefreshLayout);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 1);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(1, dpToPx(6), true));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        //recyclerView.setAdapter(mAdapter);
        poblarRecyclerView();

        if (documentoList.size()==0){
            hidepDialog();
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


    }
    private void poblarRecyclerView(){
        //DocumentoPorEspecialistaListarExternoJson();
        if(NombreDoc.equals("POR APROBAR")){
            DocumentoPorEspecialistaListarExternoJson();
        }else if(NombreDoc.equals("APROBADOS")){
            ListarDocumentosAprobadosApp();
        }else if(NombreDoc.equals("RECHAZADOS")){
            ListarDocumentosRechazadosApp();
        }else if(NombreDoc.equals("STATUS TRAMITE")){
            ListarDocumentosPorControlStatusApp();
        }
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
    }
    protected void showpDialog() {

        if (!progressDialog.isShowing()) progressDialog.show();
    }

    protected void hidepDialog() {

        if (progressDialog.isShowing()) progressDialog.dismiss();
    }
    public void shuffle(){
        //Collections.shuffle(documentoList, new Random(System.currentTimeMillis()));
        //StoreAdapter adapter = new StoreAdapter(EstadosDocsEnTramiteActivity.this, documentoList);
        //recyclerView.setAdapter(adapter);
    }

    @Override
    public void onRefresh() {
        //M documentoList.clear();

        //Descomentar DocumentoPorEspecialistaListarExternoJson();
        //Descomentar mSwipeRefreshLayout.setRefreshing(false);
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

    class StoreAdapter extends RecyclerView.Adapter<EstadosDocsEnTramiteActivity.StoreAdapter.MyViewHolder> {
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
        public EstadosDocsEnTramiteActivity.StoreAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.detalle_documentos_item, parent, false);

            return new EstadosDocsEnTramiteActivity.StoreAdapter.MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(EstadosDocsEnTramiteActivity.StoreAdapter.MyViewHolder holder, final int position) {
            final Documento documentoViewHolder = documentoList.get(position);
            holder.name.setText(documentoViewHolder.getDescripcion());
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
                    //documento.setText temporalmente Status antes Nombre
                    if(NombreDoc.equals("STATUS TRAMITE")){
                        documento.setText("Status: "+documentoViewHolder.getStatus());
                    }else{
                        documento.setText(documentoViewHolder.getNombre());
                    }

                    memonico.setText("Memonico: " + documentoViewHolder.getDescripcion());
                    tipoContrato.setText("Tipo Contrato: " + documentoViewHolder.getTipoContrato());
                    fecha.setText(documentoViewHolder.getFecha());
                    botonAprobar = myDialog.findViewById(R.id.btnAprobar);
                    botonCancelar = myDialog.findViewById(R.id.btnCancelar);

                    botonAprobar.setVisibility(View.GONE);
                    botonCancelar.setVisibility(View.GONE);

                    myDialog.show();

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
                    //DocumentoPorEspecialistaListarExternoJson();
                    if(NombreDoc.equals("POR APROBAR")){
                        DocumentoPorEspecialistaListarExternoJson();
                    }else if(NombreDoc.equals("APROBADOS")){
                        ListarDocumentosAprobadosApp();
                    }else if(NombreDoc.equals("RECHAZADOS")){
                        ListarDocumentosRechazadosApp();
                    }else if(NombreDoc.equals("STATUS TRAMITE")){
                        ListarDocumentosPorControlStatusApp();
                    }

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

