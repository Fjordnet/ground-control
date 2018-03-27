/*
 * Copyright 2018 Fjord
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

import android.app.Activity;

import com.fjordnet.groundcontrol.annotations.NeedsPermission;
import com.fjordnet.groundcontrol.annotations.OnPermissionDenied;

import static android.Manifest.permission.*;

/**
 * Test class containing multiple methods annotated with {@link OnPermissionDenied} containing
 * permissions that do not match permissions listed in any {@link NeedsPermission} annotations.
 */
public class DeniedUnmatchedPermissionsMultipleWarnings extends Activity {

    @OnPermissionDenied(value = READ_CONTACTS, usageWarnings = true)
    public void onReadContactsPermissionDenied() {
    }

    @OnPermissionDenied({READ_SMS, SEND_SMS, RECEIVE_SMS, RECEIVE_MMS})
    public void onAccessSmsPermissionsDenied() {
    }

    @NeedsPermission(RECORD_AUDIO)
    public void startRecording() {
    }

    @OnPermissionDenied(RECORD_AUDIO)
    public void onRecordAudioDenied() {
    }

    @NeedsPermission(ACCESS_FINE_LOCATION)
    public void requestLocation() {
    }

    @OnPermissionDenied({ACCESS_COARSE_LOCATION, ACCESS_FINE_LOCATION})
    public void onLocationRequestDenied() {
    }
}
