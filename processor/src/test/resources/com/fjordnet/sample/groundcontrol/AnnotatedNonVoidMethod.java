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
import com.fjordnet.groundcontrol.annotations.OnPermissionDenied;
import com.fjordnet.groundcontrol.annotations.OnShowRationale;

import static android.Manifest.permission.RECORD_AUDIO;

/**
 * Class containing method annotated with Ground Control annotations that return something
 * other than {@code void}.
 */
public class AnnotatedNonVoidMethod extends Activity {

    @NeedsPermission(RECORD_AUDIO)
    public boolean recordVoicemail() {
        return true;
    }

    @OnPermissionDenied(RECORD_AUDIO)
    public int onRecordFailed() {
        return 1;
    }

    @OnShowRationale(RECORD_AUDIO)
    public String getRecordRationale(OnRationaleAcknowledgedListener listener) {
        return "just cuz";
    }
}
