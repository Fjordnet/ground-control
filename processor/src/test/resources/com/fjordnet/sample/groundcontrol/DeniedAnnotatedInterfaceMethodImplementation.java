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

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

/**
 * Test class containing a method from an interface, annotated with {@link OnPermissionDenied}.
 */
public class DeniedAnnotatedInterfaceMethodImplementation extends Activity implements LogReader {

    @Override
    public void loadLogs(String logName) {
        prepareLog(logName);
    }

    @Override
    @OnPermissionDenied(WRITE_EXTERNAL_STORAGE)
    public void onLoadLogsFailed() {
    }

    @NeedsPermission(WRITE_EXTERNAL_STORAGE)
    protected void prepareLog(String logName) {
    }

    @Override
    public void showLoadLogsRationale(OnRationaleAcknowledgedListener listener) {
    }
}
