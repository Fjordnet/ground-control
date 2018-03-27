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

import android.media.AudioDeviceCallback;
import android.media.MediaCodecInfo;
import android.support.v4.app.Fragment;
import android.telecom.PhoneAccount;
import android.telephony.PhoneStateListener;

import com.fjordnet.groundcontrol.annotations.NeedsPermission;

import java.util.List;

import static android.Manifest.permission.READ_PHONE_STATE;
import static android.Manifest.permission.RECORD_AUDIO;

/**
 * Test class containing methods annotated with {@link NeedsPermission},
 * containing unsupported parameters types, with usage warnings turned off.
 */
public class ParamsUnsupportedDeclaredTypesWarningsSuppressed extends Fragment {

    // PhoneAccount is Parcelable, PhoneStateListener is not.
    @NeedsPermission(value = READ_PHONE_STATE, usageWarnings = false)
    protected void initPhone(PhoneAccount account, PhoneStateListener listener) {
    }

    // None of these types are supported.
    @NeedsPermission(value = RECORD_AUDIO, usageWarnings = false)
    protected void startRecording(MediaCodecInfo.AudioCapabilities capabilities,
            AudioDeviceCallback deviceCallback,
            List<String> prompts) {
    }
}
