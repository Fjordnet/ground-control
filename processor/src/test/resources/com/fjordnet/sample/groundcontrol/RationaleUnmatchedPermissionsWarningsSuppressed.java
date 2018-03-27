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

import com.fjordnet.groundcontrol.OnRationaleAcknowledgedListener;
import com.fjordnet.groundcontrol.annotations.NeedsPermission;
import com.fjordnet.groundcontrol.annotations.OnShowRationale;

import static android.Manifest.permission.*;

/**
 * Test class containing multiple methods annotated with {@link OnShowRationale} containing
 * permissions that do not match permissions listed in any {@link NeedsPermission} annotations.
 * Usage warnings are turned off.
 */
public class RationaleUnmatchedPermissionsWarningsSuppressed extends Activity {

    @NeedsPermission(CAMERA)
    public void openCamera(boolean video) {
    }

    @OnShowRationale(value = CAMERA, usageWarnings = false)
    protected void showCameraReason(OnRationaleAcknowledgedListener listener) {
    }

    @OnShowRationale(value = {ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION}, usageWarnings = false)
    protected void showLocationReason(OnRationaleAcknowledgedListener listener) {
    }

    @OnShowRationale(value = READ_SMS, usageWarnings = false)
    protected void showSmsReason(OnRationaleAcknowledgedListener listener) {
    }

    @OnShowRationale(value = WRITE_EXTERNAL_STORAGE, usageWarnings = false)
    public void showWriteReason(OnRationaleAcknowledgedListener listener) {
    }

    @OnShowRationale(value = RECORD_AUDIO, usageWarnings = false)
    protected void showMicReason(OnRationaleAcknowledgedListener listener) {
    }
}
