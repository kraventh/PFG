/*
* Copyright 2013 The Android Open Source Project
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package es.dlacalle.pfg;

import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.util.Map;

import es.dlacalle.common.activities.SampleActivityBase;
import es.dlacalle.common.logger.Log;
import es.dlacalle.common.logger.LogFragment;
import es.dlacalle.common.logger.LogWrapper;
import es.dlacalle.common.logger.MessageOnlyLogFilter;

/**
 * A simple launcher activity containing a summary sample description, sample log and a custom
 * {@link android.support.v4.app.Fragment} which can display a view.
 * <p/>
 * For devices with displays with a width of 720dp or greater, the sample log is always visible,
 * on other devices it's visibility is controlled by an item on the Action Bar.
 */

//ToDo implementar sharedpreferences
public class MainActivity extends SampleActivityBase
        implements ConfigFragment.ConfigFragmentListener,
        NotificacionesFragment.NotificationFragmentListener {

    public static final String TAG = "MainActivity";
    public TextView textView;
    SharedPreferences preferences;
    private PFGFragment pfgFragment;
    private ConfigFragment configFragment;
    private NotificacionesFragment notifiFragment;
    private NotificationReceiver nReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        pfgFragment = new PFGFragment();
        configFragment = new ConfigFragment();
        notifiFragment = new NotificacionesFragment();

        nReceiver = new NotificationReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("es.dlacalle.pfg.servicios.NOTIFICATION_LISTENER_EXAMPLE");
        registerReceiver(nReceiver, filter);

        textView = (TextView) findViewById(R.id.principal_textview);


        if (savedInstanceState == null) {

            FragmentTransaction transaction = getFragmentManager().beginTransaction();

            // Add the fragment to the 'fragment_container' FrameLayout
            // Replace whatever is in the fragment_container view with this fragment,
            // and add the transaction to the back stack so the user can navigate back

            transaction.replace(R.id.sample_content_fragment, pfgFragment).commit();

            Map<String, ?> keys = preferences.getAll();

            for (Map.Entry<String, ?> entry : keys.entrySet()) {
                textView.setText(textView.getText() + "\n" + entry.getKey() + ": " +
                        entry.getValue().toString());
            }

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(nReceiver);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_connect_scan_bt: {
                // Launch the DeviceListActivity to see devices and do scan
/*                Intent serverIntent = new Intent(this, DeviceListActivity.class);
                startActivityForResult(serverIntent, Constants.REQUEST_CONNECT_DEVICE_SECURE);
                return true;*/
                pfgFragment.onOptionsItemSelected(item);
                //}
                //case R.id.menu_home: {
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.sample_content_fragment, pfgFragment);
                transaction.addToBackStack(null);
                transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                transaction.commit();
                break;
            }
            case R.id.menu_settings: {
                //Cambia al ConfigFragment para seleccionar la aplicación

                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.sample_content_fragment, configFragment);
                transaction.addToBackStack(null);
                transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                transaction.commit();
                break;
            }
            case R.id.menu_act_notificaciones: {
                //Cambia al ConfigFragment para seleccionar la aplicación

                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.sample_content_fragment, notifiFragment);
                transaction.addToBackStack(null);

                transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                transaction.commit();
                break;
            }

        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Create a chain of targets that will receive log data
     */
    @Override
    public void initializeLogging() {
        // Wraps Android's native log framework.
        LogWrapper logWrapper = new LogWrapper();
        // Using Log, front-end to the logging chain, emulates android.util.log method signatures.
        Log.setLogNode(logWrapper);

        // Filter strips out everything except the message text.
        MessageOnlyLogFilter msgFilter = new MessageOnlyLogFilter();
        logWrapper.setNext(msgFilter);

        // On screen logging via a fragment with a TextView.
        LogFragment logFragment = (LogFragment) getSupportFragmentManager()
                .findFragmentById(R.id.log_fragment);
        msgFilter.setNext(logFragment.getLogView());

        Log.i(TAG, "Ready");
    }

    //Interfaces con ConfigFragment
    public void ConfigFragmentInteraction(String nombre, String paquete, Drawable icono) {
        notifiFragment.actualizaAppMonitorizada(nombre, paquete, icono);
    }

    public void ConfigFragmentInteraction(String nombre, String paquete, int icono) {
        notifiFragment.actualizaAppMonitorizada(nombre, paquete, icono);
    }

    //Interfaces con NotificacionFragment
    public void onFragmentInteraction(Uri uri) {

    }

    class NotificationReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String temp = intent.getStringExtra("notification_event") + "\n" + textView.getText();
            textView.setText(temp);
        }
    }

}
