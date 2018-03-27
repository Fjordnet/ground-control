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
import com.fjordnet.groundcontrol.annotations.OnPermissionDenied;
import com.fjordnet.groundcontrol.annotations.OnShowRationale;

import static android.Manifest.permission.READ_CALL_LOG;
import static android.Manifest.permission.READ_PHONE_STATE;
import static android.Manifest.permission.WRITE_CALL_LOG;

/**
 * Test class verifying permission set equality from matching
 * {@link NeedsPermission} and {@link OnPermissionDenied} annotations.
 */
public class DuplicatePermissionsEquality extends FragmentActivity {

    @NeedsPermission({READ_PHONE_STATE, READ_CALL_LOG, READ_PHONE_STATE, WRITE_CALL_LOG})
    protected void initPhone() {
    }

    @OnPermissionDenied({READ_PHONE_STATE, READ_CALL_LOG, WRITE_CALL_LOG})
    protected void onInitPhoneFailed() {
    }

    @OnShowRationale(
            {WRITE_CALL_LOG, READ_PHONE_STATE, WRITE_CALL_LOG, READ_CALL_LOG, READ_PHONE_STATE})
    protected void onShowPhoneRationale(OnRationaleAcknowledgedListener listener) {
    }
}
