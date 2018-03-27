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
import com.fjordnet.groundcontrol.annotations.OnShowRationale;

import java.util.Date;

import static android.Manifest.permission.READ_CALENDAR;
import static android.Manifest.permission.WRITE_CALENDAR;

/**
 * Test class containing methods with incorrect parameters annotated with {@link OnShowRationale}.
 */
public class RationaleIncorrectParameterList extends Fragment {

    @NeedsPermission(READ_CALENDAR)
    public void loadEvents() {
    }

    @OnShowRationale(READ_CALENDAR)
    protected void showCalendarReadRationale() {
    }

    @NeedsPermission(WRITE_CALENDAR)
    public void writeEvent(String title, String description, Date startTime, Date endTime) {
    }

    @OnShowRationale(WRITE_CALENDAR)
    protected void showCalendarWriteRationale(String title, String description) {
    }
}
