/*
 * Copyright 2017-2018 Fjord
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

package com.fjordnet.sample.groundcontrol;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.fjordnet.groundcontrol.GroundControl;
import com.fjordnet.groundcontrol.OnRationaleAcknowledgedListener;
import com.fjordnet.groundcontrol.annotations.NeedsPermission;
import com.fjordnet.groundcontrol.annotations.OnPermissionDenied;
import com.fjordnet.groundcontrol.annotations.OnShowRationale;

import java.util.Calendar;

import static android.Manifest.permission.*;
import static android.view.View.GONE;
import static android.view.View.VISIBLE;

/**
 * Example use of Ground Control.
 */
public class MainActivity extends AppCompatActivity
        implements RationaleDialogFragment.RationaleDialogListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String STATE_RATIONALE_LISTENER = "MainActivity.state.rationaleListener";
    private static final int MENU_FRAGMENT = Menu.FIRST;

    private CheckBox isLocationGranted;
    private CheckBox isReadContactsGranted;
    private CheckBox isWriteContactsGranted;
    private CheckBox isMicrophoneGranted;

    private TextView inlineRationale;
    private Button ackRationaleButton;

    private View coordinatorLayout;

    private OnRationaleAcknowledgedListener rationaleListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (null != savedInstanceState) {
            rationaleListener = savedInstanceState.getParcelable(STATE_RATIONALE_LISTENER);
        }

        coordinatorLayout = findViewById(R.id.coordinator);

        isLocationGranted = (CheckBox) findViewById(R.id.is_location_enabled);
        isReadContactsGranted = (CheckBox) findViewById(R.id.is_read_contacts_enabled);
        isWriteContactsGranted = (CheckBox) findViewById(R.id.is_write_contacts_enabled);
        isMicrophoneGranted = (CheckBox) findViewById(R.id.is_microphone_enabled);

        findViewById(R.id.request_location).setOnClickListener(
                ignored -> getLocation(true, System.currentTimeMillis()));
        findViewById(R.id.request_read_contacts).setOnClickListener(ignored -> loadContacts());
        findViewById(R.id.request_write_contacts).setOnClickListener(
                ignored -> updateContacts(0L, createContacts()));
        findViewById(R.id.request_microphone).setOnClickListener(
                ignored -> useMicrophone("GroundControlAudio"));

        findViewById(R.id.request_all_permissions).setOnClickListener(ignored -> useAllTheThings());

        findViewById(R.id.launch_app_settings).setOnClickListener(
                ignored -> GroundControl.showAppSettings(this));

        inlineRationale = (TextView) findViewById(R.id.inlineRationale);
        ackRationaleButton = (Button) findViewById(R.id.ackRationaleButton);
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkAllPermissions();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "Activity destroyed!");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.add(Menu.NONE, MENU_FRAGMENT, Menu.NONE, R.string.menu_fragment);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (MENU_FRAGMENT != item.getItemId()) {
            return super.onOptionsItemSelected(item);
        }

        startActivity(new Intent(this, SecondaryActivity.class));
        return true;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(STATE_RATIONALE_LISTENER, rationaleListener);
    }

    @Override
    public void onRationaleDismissed() {
        if (null == rationaleListener) {
            return;
        }

        rationaleListener.onRationaleAcknowledged(this);
    }

    protected final void checkAllPermissions() {
        isLocationGranted.setChecked(
                GroundControl.hasPermissions(this, Manifest.permission.ACCESS_COARSE_LOCATION));
        isReadContactsGranted.setChecked(
                GroundControl.hasPermissions(this, Manifest.permission.READ_CONTACTS));
        isWriteContactsGranted.setChecked(
                GroundControl.hasPermissions(this, Manifest.permission.WRITE_CONTACTS));
        isMicrophoneGranted.setChecked(
                GroundControl.hasPermissions(this, Manifest.permission.RECORD_AUDIO));
    }

    protected void showSnackbar(@NonNull String message) {
        if (TextUtils.isEmpty(message)) {
            return;
        }

        Snackbar.make(coordinatorLayout, message, Snackbar.LENGTH_LONG).show();
    }

    @OnPermissionDenied({ACCESS_COARSE_LOCATION, ACCESS_FINE_LOCATION})
    protected void onLocationDenied() {
        showSnackbar(getString(R.string.permission_denied, ACCESS_COARSE_LOCATION));
    }

    @OnShowRationale({ACCESS_COARSE_LOCATION, ACCESS_FINE_LOCATION})
    protected void showLocationRationale(OnRationaleAcknowledgedListener listener) {
        rationaleListener = listener;
        RationaleDialogFragment rationale = RationaleDialogFragment.newInstance(
                R.string.request_location_title, R.string.request_location_explanation);
        rationale.show(getSupportFragmentManager(), "rationale");
    }

    @NeedsPermission({ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION})
    protected void getLocation(boolean fine, Long timestamp) {
        showSnackbar(getString(R.string.permission_granted,
                fine ? ACCESS_FINE_LOCATION : ACCESS_COARSE_LOCATION));
        checkAllPermissions();
    }

    @NeedsPermission(READ_CONTACTS)
    protected void loadContacts() {
        showSnackbar(getString(R.string.permission_granted, READ_CONTACTS));
        checkAllPermissions();
    }

    @OnShowRationale(READ_CONTACTS)
    protected void showLoadContactsRationale(OnRationaleAcknowledgedListener listener) {
        showSnackbar(getString(R.string.request_read_contacts_explanation));
        listener.onRationaleAcknowledged(this);
    }

    @OnPermissionDenied(READ_CONTACTS)
    protected void onReadContactsDenied() {
        showSnackbar(getString(R.string.permission_denied, READ_CONTACTS));
    }

    @OnShowRationale(value = WRITE_CONTACTS, handleRestarts = true)
    protected void showUpdateContactsRationale(final OnRationaleAcknowledgedListener listener) {

        inlineRationale.setText(R.string.request_write_contacts_explanation);
        ackRationaleButton.setOnClickListener(view -> {
            inlineRationale.setVisibility(GONE);
            ackRationaleButton.setVisibility(GONE);
            listener.onRationaleAcknowledged(this);
        });

        inlineRationale.setVisibility(VISIBLE);
        ackRationaleButton.setVisibility(VISIBLE);
    }

    @OnPermissionDenied(WRITE_CONTACTS)
    protected void onWriteContactsDenied() {
        showSnackbar(getString(R.string.permission_denied, WRITE_CONTACTS));
    }

    @NeedsPermission(WRITE_CONTACTS)
    protected void updateContacts(long delay, Contact... contacts) {
        showSnackbar(getString(R.string.permission_granted, WRITE_CONTACTS));
        Log.d(TAG, String.format("Updated %d contacts: %s", contacts.length,
                TextUtils.join(", ", contacts)));
        checkAllPermissions();
    }

    @NeedsPermission(RECORD_AUDIO)
    protected void useMicrophone(String audioFileName) {
        showSnackbar(getString(R.string.permission_granted, RECORD_AUDIO));
        checkAllPermissions();
    }

    @OnPermissionDenied(RECORD_AUDIO)
    protected void onRecordAudioDenied() {
        showSnackbar(getString(R.string.permission_denied, RECORD_AUDIO));
    }

    @NeedsPermission({ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION, READ_CONTACTS, WRITE_CONTACTS,
            RECORD_AUDIO})
    protected void useAllTheThings() {
        showSnackbar(getString(R.string.permissions_granted));
        checkAllPermissions();
    }

    @OnPermissionDenied({ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION, READ_CONTACTS, WRITE_CONTACTS,
            RECORD_AUDIO})
    protected void onAllTheThingsDenied() {
        showSnackbar(getString(R.string.permissions_denied));
    }

    @OnPermissionDenied(value = READ_SMS, usageWarnings = false)
    protected void onReadSmsDenied() {
    }

    @OnPermissionDenied({RECEIVE_MMS, RECEIVE_SMS})
    protected void onReceiveSmsMmsDenied() {
    }

    private Contact[] createContacts() {
        return new Contact[] {
                // Populate with fake data.
                new Contact("Justin", "Hong", null, 1991, Calendar.MARCH, 26)
        };
    }
}
