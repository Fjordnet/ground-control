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

import android.app.Activity;

import com.fjordnet.groundcontrol.OnRationaleAcknowledgedListener;
import com.fjordnet.groundcontrol.annotations.NeedsPermission;
import com.fjordnet.groundcontrol.annotations.OnShowRationale;

import static android.Manifest.permission.BODY_SENSORS;
import static android.Manifest.permission.RECORD_AUDIO;

/**
 * Test class containing both default and custom rationales for the same
 * permission set.
 */
public class RationaleDefaultAndCustom extends Activity {

    @NeedsPermission(value = BODY_SENSORS, rationaleResourceId = 2349820)
    public void initSensors() {
    }

    @OnShowRationale(BODY_SENSORS)
    protected void showSensorsRationale(OnRationaleAcknowledgedListener listener) {
        listener.onRationaleAcknowledged(this);
    }

    @OnShowRationale(RECORD_AUDIO)
    protected void showRecordRationale(OnRationaleAcknowledgedListener listener) {
        listener.onRationaleAcknowledged(this);
    }

    @NeedsPermission(value = RECORD_AUDIO, rationaleResourceId = 3892793)
    public void recordAudio() {
    }
}
