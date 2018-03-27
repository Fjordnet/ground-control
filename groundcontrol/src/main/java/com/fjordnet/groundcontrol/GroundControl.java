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

package com.fjordnet.groundcontrol;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;
import static android.support.v4.app.ActivityCompat.shouldShowRequestPermissionRationale;
import static android.support.v4.content.ContextCompat.checkSelfPermission;

/**
 * This is the primary interface for permission management in this library.
 */
public abstract class GroundControl {

    protected GroundControl() {
    }

    /**
     * Checks whether or not a specific permission has been granted.
     *
     * @param context {@link Context}
     * @param permissions one or more permissions to check.
     *
     * @return {@code true} if the permissions have been granted.
     */
    public static boolean hasPermissions(@NonNull Context context, String... permissions) {

        if (null == permissions || 0 >= permissions.length) {
            return false;
        }

        // If we are on pre-M, simply return true.
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M)
            return true;

        // Check each permission.
        for (String permission : permissions) {
            if (PERMISSION_GRANTED != checkSelfPermission(context, permission)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Given an array of permissions, filter out any permissions that have already been granted.
     *
     * @param context context within which to check whether the specified permissions
     * have been granted.
     * @param permissions the permissions to be checked.
     *
     * @return a new array of permissions, the subset of the provided permissions that have
     * not yet been granted.
     */
    public static String[] filterGrantedPermissions(@NonNull Context context,
            String... permissions) {

        if (null == permissions || 0 >= permissions.length) {
            return new String[0];
        }

        List<String> filteredPermissions = new ArrayList<>();
        for (String permission : permissions) {
            if (PERMISSION_GRANTED != checkSelfPermission(context, permission)) {
                filteredPermissions.add(permission);
            }
        }

        String[] remainingPermissions = new String[filteredPermissions.size()];
        filteredPermissions.toArray(remainingPermissions);
        return remainingPermissions;
    }

    /**
     * Checks whether a rationale dialog should be displayed for the supplied permissions.
     *
     * @param activity activity instance in which the permissions are being requested.
     * @param permissions which permissions to query for whether a rationale dialog
     * should be shown.
     *
     * @return {@code true} if the rationale dialog should be shown for at least
     * one of the supplied permissions.
     */
    public static boolean shouldShowRationale(Activity activity, String... permissions) {

        if (null == activity || null == permissions || 0 >= permissions.length) {
            return false;
        }

        for (String permission : permissions) {
            if (shouldShowRequestPermissionRationale(activity, permission)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Create a light style alert dialog for displaying a rationale.
     *
     * @param context used for creating the alert dialog.
     * @param rationale the rationale to display in the alert dialog.
     * @param listener callback when the user acknowledges the rationale.
     *
     * @return an alert dialog containing the rationale for requesting a permission.
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP_MR1)
    @NonNull
    public static AlertDialog createPermissionRationaleDialog(
            @NonNull final Context context,
            @NonNull final Rationale rationale,
            final DialogInterface.OnClickListener listener) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context,
                android.R.style.Theme_DeviceDefault_Light_Dialog_Alert);

        return builder.setTitle(rationale.title)
                .setMessage(rationale.message)
                .setPositiveButton(rationale.buttonText, listener)
                .setCancelable(false)
                .create();
    }

    /**
     * Creates and returns an Intent to the application settings.
     *
     * @param context {@link Context}
     *
     * @return An intent to view the application settings
     */
    public static Intent createAppSettingsIntent(Context context) {
        // Intent to the app settings page for the package name passed in.
        Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse("package:" + context.getPackageName()));
        return intent;
    }

    /**
     * Launches the user to the application settings screen.
     * @param context {@link Context}
     */
    public static void showAppSettings(Context context) {
        context.startActivity(createAppSettingsIntent(context));
    }
}
