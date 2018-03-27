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

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.fjordnet.groundcontrol.annotations.NeedsPermission;
import com.fjordnet.groundcontrol.annotations.OnPermissionDenied;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.provider.MediaStore.ACTION_IMAGE_CAPTURE;
import static android.provider.MediaStore.ACTION_VIDEO_CAPTURE;
import static android.widget.Toast.LENGTH_SHORT;

/**
 * A fragment using Ground Control.
 */
public class SampleFragment extends BaseFragment {

    private static final int REQUEST_MEDIA_CAPTURE = 42;

    public SampleFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_sample, container, false);

        view.findViewById(R.id.actionCaptureImage)
                .setOnClickListener(button -> captureMedia(false));
        view.findViewById(R.id.actionCaptureVideo)
                .setOnClickListener(button -> captureMedia(true));

        view.findViewById(R.id.actionExportLogs).setOnClickListener(button -> exportLogs());
        view.findViewById(R.id.actionExportState).setOnClickListener(button -> exportState());

        return view;
    }

    @NeedsPermission(value = CAMERA, rationaleResourceId = R.string.request_camera_explanation)
    protected void captureMedia(boolean video) {

        Intent intent = new Intent(video ? ACTION_VIDEO_CAPTURE : ACTION_IMAGE_CAPTURE);
        final boolean activityResolved = null != intent.resolveActivity(
                getActivity().getPackageManager());

        if (activityResolved) {
            startActivityForResult(intent, REQUEST_MEDIA_CAPTURE);
        }
    }

    @NeedsPermission(WRITE_EXTERNAL_STORAGE)
    protected void exportLogs() {
        Toast.makeText(getActivity(), R.string.toast_exported_logs, LENGTH_SHORT).show();
    }

    @NeedsPermission(WRITE_EXTERNAL_STORAGE)
    protected void exportState() {
        Toast.makeText(getActivity(), R.string.toast_exported_state, LENGTH_SHORT).show();
    }

    @OnPermissionDenied(WRITE_EXTERNAL_STORAGE)
    protected void onWriteExternalStorageDenied() {
        Toast.makeText(getActivity(), R.string.toast_cannot_export, LENGTH_SHORT).show();
    }
}
