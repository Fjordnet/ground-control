/*
 * Copyright 2017 Fjord
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

import android.Manifest;
import android.app.Activity;

import com.fjordnet.groundcontrol.annotations.NeedsPermission;

/**
 * Class utilizing {@link NeedsPermission} annotation, but it is not an activity,
 * nor does it have access to one through a no-argument getter.
 */
public class NoActivityAccess {

    public void onRequestPermissionsResult(int requestCode,
            String[] permissions,
            int[] grantResults) {
    }

    @NeedsPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    public void writeSettings() {
    }

    private Activity getActivity() {
        return null;
    }
}
