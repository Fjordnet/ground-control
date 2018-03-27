/*
 * Copyright 2017-2018 Fjord
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

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.fjordnet.groundcontrol.annotations.NeedsPermission;
import com.fjordnet.groundcontrol.annotations.OnPermissionDenied;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.widget.Toast.LENGTH_SHORT;

/**
 * Test Ground Control annotations in a parent class.
 */
public abstract class BaseFragment extends Fragment {

    protected static final int ACTION_UPDATE_LOCATION = Menu.FIRST;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.add(Menu.NONE, ACTION_UPDATE_LOCATION, Menu.NONE,
                getString(R.string.action_update_location));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (ACTION_UPDATE_LOCATION != item.getItemId()) {
            return super.onOptionsItemSelected(item);
        }

        retrieveLocation();
        return true;
    }

    @NeedsPermission(ACCESS_FINE_LOCATION)
    protected void retrieveLocation() {
        Toast.makeText(getActivity(), getString(R.string.permission_granted, ACCESS_FINE_LOCATION),
                LENGTH_SHORT).show();
    }

    @OnPermissionDenied(ACCESS_FINE_LOCATION)
    protected void onLocationPermissionDenied() {
        Toast.makeText(getActivity(), getString(R.string.permission_denied, ACCESS_FINE_LOCATION),
                LENGTH_SHORT).show();
    }
}
