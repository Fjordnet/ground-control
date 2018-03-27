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

import android.support.v4.app.FragmentActivity;

import com.fjordnet.groundcontrol.OnRationaleAcknowledgedListener;
import com.fjordnet.groundcontrol.annotations.NeedsPermission;
import com.fjordnet.groundcontrol.annotations.OnShowRationale;

import static android.Manifest.permission.*;

/**
 * Test class containing  methods annotated with {@link OnShowRationale} and
 * no methods annotated with {@link NeedsPermission}.
 */
public class RationaleUnmatchedPermissionsNoGrantedCallback extends FragmentActivity {

    @OnShowRationale(CAMERA)
    protected void showCameraReason(OnRationaleAcknowledgedListener listener) {
    }

    @OnShowRationale({ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION})
    protected void showLocationReason(OnRationaleAcknowledgedListener listener) {
    }

    @OnShowRationale(READ_SMS)
    protected void showSmsReason(OnRationaleAcknowledgedListener listener) {
    }

    @OnShowRationale(WRITE_EXTERNAL_STORAGE)
    public void showWriteReason(OnRationaleAcknowledgedListener listener) {
    }

    @OnShowRationale(RECORD_AUDIO)
    protected void showMicReason(OnRationaleAcknowledgedListener listener) {
    }
}
