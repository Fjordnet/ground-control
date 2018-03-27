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

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;

/**
 * Dialog fragment for displaying permission rationales.
 */
public class RationaleDialogFragment extends DialogFragment {

    interface RationaleDialogListener {

        void onRationaleDismissed();
    }

    private static final String ARG_TITLE_ID = "arg.titleId";
    private static final String ARG_MESSAGE_ID = "arg.messageId";

    private int titleId;
    private int messageId;

    static RationaleDialogFragment newInstance(int titleId, int messageId) {
        Bundle args = new Bundle();
        args.putInt(ARG_TITLE_ID, titleId);
        args.putInt(ARG_MESSAGE_ID, messageId);

        RationaleDialogFragment fragment = new RationaleDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public RationaleDialogFragment() {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (RationaleDialogListener.class.isAssignableFrom(context.getClass())) {
            return;
        }

        throw new IllegalArgumentException(RationaleDialogFragment.class.getSimpleName()
                + " must be used in the context of an Activity that implements "
                + RationaleDialogListener.class.getSimpleName());
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        Bundle args = getArguments();
        if (null != args) {
            titleId = args.getInt(ARG_TITLE_ID);
            messageId = args.getInt(ARG_MESSAGE_ID);
        }

        return new AlertDialog.Builder(getActivity())
                .setTitle(titleId)
                .setMessage(messageId)
                .setPositiveButton(R.string.rationale_ok, (dialog, buttonId)
                        -> ((RationaleDialogListener) getActivity()).onRationaleDismissed())
                .create();
    }
}
