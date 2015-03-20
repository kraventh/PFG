package es.dlacalle.pfg;

import android.app.Activity;
import android.app.Fragment;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.PreferenceFragment;
import android.widget.Toast;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link NotificacionesFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link NotificacionesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NotificacionesFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment NotificacionesFragment.
     */
    public static NotificacionesFragment newInstance() {
        NotificacionesFragment fragment = new NotificacionesFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public NotificacionesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.notificaciones_prefs);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        switch(key) {
            case "limpiar_historico_notif":
                CheckBoxPreference cb_lhn = (CheckBoxPreference) findPreference(key);
                // Set summary to be the user-description for the selected value
                if (cb_lhn.isChecked()) {
                    ((MainActivity) getActivity()).textView.setText("");
                    Toast.makeText(getActivity().getApplicationContext(), "Histórico eliminado", Toast.LENGTH_SHORT).show();
                    cb_lhn.setChecked(false);
                }
                break;

            case "habilitar_historico_notificaciones":
                CheckBoxPreference cb_hhn = (CheckBoxPreference) findPreference(key);
                // Set summary to be the user-description for the selected value
                if (cb_hhn.isChecked()) ((MainActivity) getActivity()).textView.setText("");
                else ((MainActivity) getActivity()).textView.setText("Histórico deshabilitado. Habilítelo en el menú \"Notificaciones\"");

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
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " debe implementar OnFragmentInteractionListener");
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
    public interface OnFragmentInteractionListener {
        public void onFragmentInteraction(Uri uri);
    }


}
