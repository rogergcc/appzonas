package com.example.s3k_user1.appzonas;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.s3k_user1.appzonas.Sesion.SessionManager;

import java.util.HashMap;

/**
 * Fragmento para la pestaña "PERFIL" De la sección "Mi Cuenta"
 */
public class FragmentoPerfil extends Fragment {
    SessionManager session;
    String sesion_usuario = "";

    // id
    String sesion_id = "";

    String sesion_empleado = "";
    String sesion_correo = "";
    TextView usuario;
    TextView correo;
    public FragmentoPerfil() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragmento_perfil, container, false);

        session = new SessionManager(getActivity().getApplicationContext());
        session.checkLogin();
        HashMap<String, String> user = session.getUserDetails();
        sesion_usuario = user.get(SessionManager.KEY_USUARIO_NOMBRE);
        sesion_id = user.get(SessionManager.KEY_USUARIO_ID);
        sesion_empleado = user.get(SessionManager.KEY_USUARIO_EMPLEADO);
        sesion_correo = user.get(SessionManager.KEY_USUARIO_CORREO);

        usuario = view.findViewById(R.id.perfil_texto_nombre);
        correo = view.findViewById(R.id.perfil_texto_email);

        usuario.setText(sesion_usuario);
        correo.setText(sesion_correo);

        return view;
    }
}
