package es.dlacalle.pfg;

import android.app.Activity;
import android.app.Fragment;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.widget.Toast;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link es.dlacalle.pfg.NotificacionesFragment.NotificationFragmentListener} interface
 * to handle interaction events.
 * Use the {@link NotificacionesFragment} factory method to
 * create an instance of this fragment.
 */
public class NotificacionesFragment extends PreferenceFragment
        implements SharedPreferences.OnSharedPreferenceChangeListener {
    private NotificationFragmentListener mListener;

    private PreferenceScreen app_monitorizada;

    public NotificacionesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.notificaciones_prefs);

        Preference limpia_cache = findPreference("limpiar_historico_notif");
        limpia_cache.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference pref) {

                ((MainActivity) getActivity()).textView.setText("");
                Toast.makeText(getActivity().getApplicationContext(), "Histórico eliminado", Toast.LENGTH_SHORT).show();

                return true;
            }
        });
        //Actualizo la preferencia de la aplicación a monitorizar
        compruebaAppMonitorizada();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        switch (key) {

            case "habilitar_historico_notificaciones":
                CheckBoxPreference cb_hhn = (CheckBoxPreference) findPreference(key);
                if (cb_hhn.isChecked()) ((MainActivity) getActivity()).textView.setText("");
                else ((MainActivity) getActivity()).textView.
                        setText("Histórico deshabilitado. Habilítelo en el menú \"Notificaciones\"");

                break;
        }


    }

    /*
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            // Inflate the layout for this fragment

    
            return inflater.inflate(R.layout.fragment_notificaciones, container, false);
        }
    */

    /**
     * Comprueba las preferencias guardadas y cumplimenta la preferenceScreen con los
     * datos de la aplicación seleccionada en una sesión anterior.
     */
    public void compruebaAppMonitorizada() {
        Drawable icono = null;
        //Nombre de la app
        EditTextPreference titulo = (EditTextPreference) findPreference("app_monitorizada_titulo");
        //Nombre del paquete
        EditTextPreference paquete = (EditTextPreference) findPreference("app_monitorizada_paquete");

        if (titulo.getText().equals("Ninguna")) {
            titulo.setSummary(titulo.getText());
            paquete.setSummary(paquete.getText());
        } else {
            try {
                //Icono de la app
                icono = getActivity().getPackageManager().
                        getPackageInfo(paquete.getText(), 0).
                        applicationInfo.loadIcon(getActivity().getPackageManager());
            } catch (PackageManager.NameNotFoundException e) {
                Toast.makeText(getActivity(),
                        "Paquete '" + paquete.getText() + "' no encontrado",
                        Toast.LENGTH_SHORT).show();
            }
            app_monitorizada = (PreferenceScreen) findPreference("app_monitorizada");
            app_monitorizada.setTitle(titulo.getText());
            app_monitorizada.setSummary(paquete.getText());
            app_monitorizada.setIcon(icono);
            titulo.setSummary(titulo.getText());
            paquete.setSummary(paquete.getText());
        }

    }

    public void actualizaAppMonitorizada(String nombre, String paquete, Object icono) {
        EditTextPreference titulo = (EditTextPreference) findPreference("app_monitorizada_titulo");
        EditTextPreference paqueText = (EditTextPreference) findPreference("app_monitorizada_paquete");

        app_monitorizada = (PreferenceScreen) findPreference("app_monitorizada");
        app_monitorizada.setTitle(nombre);
        titulo.setSummary(nombre);
        titulo.setText(nombre);
        app_monitorizada.setSummary(paquete);
        paqueText.setSummary(paquete);
        paqueText.setText(paquete);

        if (icono.getClass().equals(Integer.class))
            app_monitorizada.setIcon((int) icono);
        else
            app_monitorizada.setIcon((Drawable) icono);

    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (NotificationFragmentListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " debe implementar NotificationFragmentListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);

    }

    @Override
    public void onPause() {
        getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
        super.onPause();
    }


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface NotificationFragmentListener {
        public void onFragmentInteraction(Uri uri);
    }


}
