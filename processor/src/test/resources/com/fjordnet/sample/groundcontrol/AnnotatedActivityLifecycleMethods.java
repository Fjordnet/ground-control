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
import android.content.Intent;
import android.os.Bundle;

import com.fjordnet.groundcontrol.annotations.NeedsPermission;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

/**
 * Test class containing activity lifecycle methods annotated with
 * {@link NeedsPermission}.
 */
public class AnnotatedActivityLifecycleMethods extends DuplicatePermissionsEquality {

    @Override
    @NeedsPermission(WRITE_EXTERNAL_STORAGE)
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    @NeedsPermission(WRITE_EXTERNAL_STORAGE)
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    @NeedsPermission(WRITE_EXTERNAL_STORAGE)
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    @NeedsPermission(WRITE_EXTERNAL_STORAGE)
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    @NeedsPermission(WRITE_EXTERNAL_STORAGE)
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
    }

    @Override
    @NeedsPermission(WRITE_EXTERNAL_STORAGE)
    protected void onStart() {
        super.onStart();
    }

    @Override
    @NeedsPermission(WRITE_EXTERNAL_STORAGE)
    protected void onStop() {
        super.onStop();
    }

    @Override
    @NeedsPermission(WRITE_EXTERNAL_STORAGE)
    protected void onResume() {
        super.onResume();
    }

    @Override
    @NeedsPermission(WRITE_EXTERNAL_STORAGE)
    protected void onPause() {
        super.onPause();
    }

    @NeedsPermission(WRITE_EXTERNAL_STORAGE)
    protected void logState() {
    }

    @Override
    @NeedsPermission(WRITE_EXTERNAL_STORAGE)
    protected void initPhone() {
        super.initPhone();
    }
}
