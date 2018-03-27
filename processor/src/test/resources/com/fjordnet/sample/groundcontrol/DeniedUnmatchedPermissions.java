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

import com.fjordnet.groundcontrol.annotations.NeedsPermission;
import com.fjordnet.groundcontrol.annotations.OnPermissionDenied;

import static android.Manifest.permission.GET_ACCOUNTS;
import static android.Manifest.permission.READ_CONTACTS;
import static android.Manifest.permission.WRITE_CONTACTS;

/**
 * Test class containing method annotated with {@link OnPermissionDenied} containing
 * permissions that do not match permissions listed in any {@link NeedsPermission} annotations.
 */
public class DeniedUnmatchedPermissions extends Fragment {

    @NeedsPermission({READ_CONTACTS, WRITE_CONTACTS, GET_ACCOUNTS})
    public void loadContacts() {
    }

    @OnPermissionDenied({WRITE_CONTACTS, READ_CONTACTS})
    public void onContactsPermissionDenied() {
    }
}
