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

package com.fjordnet.groundcontrol;

import android.app.Activity;
import android.app.Fragment;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * <p>
 * Callbacks for when a permissions rationale is acknowledged by the user.
 * </p><p>
 * An instance of this class will be passed to methods annotated with {@code OnShowRationale}.
 * The implementation of those methods should invoke the appropriate version of
 * {@code onRationaleAcknowledged} depending on the type of the class containing the methods.
 * Invocation of any of the other methods will result in an {@link UnsupportedOperationException}.
 * </p>
 */
public class OnRationaleAcknowledgedListener implements Parcelable {

    public static final Creator<OnRationaleAcknowledgedListener> CREATOR
            = new Creator<OnRationaleAcknowledgedListener>() {

        @Override
        public OnRationaleAcknowledgedListener createFromParcel(Parcel in) {
            return new OnRationaleAcknowledgedListener(in.createStringArray(), in.readInt());
        }

        @Override
        public OnRationaleAcknowledgedListener[] newArray(int size) {
            return new OnRationaleAcknowledgedListener[size];
        }
    };

    protected String[] permissions;
    protected int requestCode;

    protected OnRationaleAcknowledgedListener(String[] permissions, int requestCode) {
        this.permissions = permissions;
        this.requestCode = requestCode;
    }

    /**
     * Callback when a permissions rationale is acknowledged by the user.
     *
     * @param activity the instance of the {@link Activity} subclass containing methods
     * annotated by {@code OnShowRationale}.
     */
    public void onRationaleAcknowledged(Activity activity) {
        throw new UnsupportedOperationException();
    }

    /**
     * Callback when a permissions rationale is acknowledged by the user.
     *
     * @param fragment the instance of the {@link Fragment} subclass containing methods
     * annotated by {@code OnShowRationale}.
     */
    public void onRationaleAcknowledged(Fragment fragment) {
        throw new UnsupportedOperationException();
    }

    /**
     * Callback when a permissions rationale is acknowledged by the user.
     *
     * @param supportFragment the instance of the {@link android.support.v4.app.Fragment}
     * subclass containing methods annotated by {@code OnShowRationale}.
     */
    public void onRationaleAcknowledged(android.support.v4.app.Fragment supportFragment) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringArray(permissions);
        dest.writeInt(requestCode);
    }
}
