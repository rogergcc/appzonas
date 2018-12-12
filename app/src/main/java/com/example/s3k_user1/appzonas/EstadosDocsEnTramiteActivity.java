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

    public void ListarDocumentosAprobadosApp() {

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



    public void RevizarDocumentoJson(int usuarioId, int documentoId, String esRechazado, String observacion, String perfil, String fileImagenOrPdf, String nombre, boolean esImagen) {
        //RevizarDocumentoJson(int usuarioId, int documentoId, string esRechazado, string observacion, string perfil)
        String url = IP_LEGAL+"/legal/RevisionDocumento/RevizarDocumentoJson";
        //Log.w("URL LOGIN: ", url);
        //Log.w("datos parametros: ", usuarioId+" - docid:"+documentoId +" - esRechazado:"+ esRechazado+" -Observacion:"+observacion+ " -perfil:"+perfil );

        JSONObject js = new JSONObject();

        try {
            JSONObject params = new JSONObject();


            js.put("usuarioId",usuarioId);
            js.put("documentoId",documentoId);
            js.put("esRechazado",esRechazado);
            js.put("observacion",observacion);
            js.put("perfil",perfil);
            js.put("fileImagenOrPdf",fileImagenOrPdf);
            js.put("nombre",nombre);
            js.put("esImagen",esImagen);

            Log.e("Datos:", js.toString());

        }catch (JSONException e) {
            e.printStackTrace();
        }

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
        if(NombreDoc.equals("APROBADOS")){
            ListarDocumentosAprobadosApp();
        }else if(NombreDoc.equals("RECHAZADOS")){
            ListarDocumentosRechazadosApp();
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
        if(NombreDoc.equals("APROBADOS")){
            ListarDocumentosAprobadosApp();
        }else if(NombreDoc.equals("RECHAZADOS")){
            ListarDocumentosRechazadosApp();
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
                            bundle.putInt("vUsuarioId", Integer.parseInt(usuarioId));
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
                            myDialog.hide();

                            if (perfil.equals("1003") || perfil.equals("1004")) {
                                Bundle bundle = new Bundle();

                                bundle.putInt("vusuarioId", Integer.parseInt(usuarioId));
                                bundle.putInt("vdocumentoId", documentoId);
                                bundle.putString("vesRechazado", "No");
                                bundle.putString("vobservacion", "");
                                bundle.putString("vperfil", perfil);

                                bundle.putString("vnombre", documentoViewHolder.getNombre());
                                Intent newDDocumentsActivity = new Intent(v.getContext(), UploadImageActivity.class);
                                newDDocumentsActivity.putExtras(bundle);
                                v.getContext().startActivity(newDDocumentsActivity);
                                //startActivity(new Intent(v.getContext(), UploadImageActivity.class));
                            } else {
                                RevizarDocumentoJson(Integer.parseInt(usuarioId),documentoId,"No","",perfil,"","",false);
                                Snackbar.make(vista, respuestaRevisizarDocuento, Snackbar.LENGTH_LONG)
                                        .setAction("Action", null).show();

//                                Bundle bundle = new Bundle();
//
//                                bundle.putInt("vusuarioId", Integer.parseInt(id));
//                                bundle.putInt("vdocumentoId", documentoId);
//                                bundle.putString("vesRechazado", "No");
//                                bundle.putString("vobservacion", "");
//                                bundle.putString("vperfil", perfil);
//
//                                bundle.putString("vnombre", documentoViewHolder.getNombre());
//                                Intent newDDocumentsActivity = new Intent(v.getContext(), UploadImageActivity.class);
//                                newDDocumentsActivity.putExtras(bundle);
//                                v.getContext().startActivity(newDDocumentsActivity);

                            }
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
                    //DocumentoPorEspecialistaListarExternoJson();
                    if(NombreDoc.equals("APROBADOS")){
                        ListarDocumentosAprobadosApp();
                    }else if(NombreDoc.equals("RECHAZADOS")){
                        ListarDocumentosRechazadosApp();
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

