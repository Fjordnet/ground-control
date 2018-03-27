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

import android.app.Fragment;

import com.fjordnet.groundcontrol.OnRationaleAcknowledgedListener;
import com.fjordnet.groundcontrol.annotations.NeedsPermission;
import com.fjordnet.groundcontrol.annotations.OnShowRationale;

import static android.Manifest.permission.CALL_PHONE;
import static android.Manifest.permission.READ_CALL_LOG;
import static android.Manifest.permission.READ_PHONE_STATE;
import static android.Manifest.permission.WRITE_CALL_LOG;

/**
 * Test class containing method annotated with {@link OnShowRationale} containing
 * permissions that do not match permissions listed in any {@link NeedsPermission} annotations.
 */
public class RationaleUnmatchedPermissions extends Fragment {

    @NeedsPermission({READ_CALL_LOG, READ_PHONE_STATE, CALL_PHONE})
    public void initPhone() {
    }

    @OnShowRationale({READ_PHONE_STATE, WRITE_CALL_LOG, CALL_PHONE, READ_CALL_LOG})
    public void showWhyPhone(OnRationaleAcknowledgedListener listener) {
    }
}
