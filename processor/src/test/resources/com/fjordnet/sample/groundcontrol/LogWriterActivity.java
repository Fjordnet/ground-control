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

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

/**
 * Abstract class for testing Ground Control scenarios involving abstract methods.
 */
public abstract class LogWriterActivity extends Activity {

    public static void init() {
    }

    @NeedsPermission(WRITE_EXTERNAL_STORAGE)
    public abstract void rotateLogs();

    @OnPermissionDenied(WRITE_EXTERNAL_STORAGE)
    public abstract void onRotateLogsFailed();

    @OnShowRationale(WRITE_EXTERNAL_STORAGE)
    public abstract void onShowRotateLogsRationale(OnRationaleAcknowledgedListener listener);

    protected abstract void writeLogs(String fileName, String contents);

    protected abstract void onWriteFailed();

    protected abstract void showWriteLogsReason(OnRationaleAcknowledgedListener listener);

    private void addLog(String logMessage) {
    }

    private void onAddLogFailed() {
    }

    private void showAddLogRationale(OnRationaleAcknowledgedListener listener) {
    }
}
