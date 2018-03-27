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

import com.fjordnet.groundcontrol.annotations.NeedsPermission;
import com.fjordnet.groundcontrol.annotations.OnPermissionDenied;
import com.fjordnet.sample.groundcontrol.DuplicatePermissionsEquality;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.READ_PHONE_STATE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

/**
 * Test class containing activity lifecycle methods annotated with
 * {@link OnPermissionDenied}, with usage warnings turned off.
 */
public class DeniedAnnotatedActivityLifecycleMethodsWarningsSuppressed
        extends DuplicatePermissionsEquality {

    @NeedsPermission(READ_EXTERNAL_STORAGE)
    protected void loadStuff(String id) {
    }

    @Override
    @OnPermissionDenied(value = READ_EXTERNAL_STORAGE, usageWarnings = false)
    protected void onStop() {
        super.onStop();
    }

    @NeedsPermission({READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE})
    protected void updateStuff(String id, String stuff) {
    }

    @Override
    @OnPermissionDenied(value = {READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE},
            usageWarnings = false)
    protected void onResume() {
        super.onResume();
    }

    @NeedsPermission(WRITE_EXTERNAL_STORAGE)
    protected void writeStuff(String id, String stuff) {
    }

    @Override
    @OnPermissionDenied(value = WRITE_EXTERNAL_STORAGE, usageWarnings = false)
    protected void onPause() {
        super.onPause();
    }

    @NeedsPermission(READ_PHONE_STATE)
    protected void doPhoneInitialization() {
    }

    @Override
    @OnPermissionDenied(value = READ_PHONE_STATE, usageWarnings = false)
    protected void onInitPhoneFailed() {
        super.onInitPhoneFailed();
    }
}
