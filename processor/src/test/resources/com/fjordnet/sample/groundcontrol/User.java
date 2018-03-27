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

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Data class implementing {@link Parcelable} for the purposes of testing
 * handling of single instances and arrays of {@link Parcelable} instances.
 */
public class User implements Parcelable {

    private String displayName;
    private String email;
    private long userId;

    protected User(Parcel in) {
        displayName = in.readString();
        email = in.readString();
        userId = in.readLong();
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(displayName);
        out.writeString(email);
        out.writeLong(userId);
    }
}
