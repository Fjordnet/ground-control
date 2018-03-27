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

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

public class Rationale implements Parcelable {

    public static final Creator<Rationale> CREATOR = new Creator<Rationale>() {
        @Override
        public Rationale createFromParcel(Parcel in) {
            return new Rationale(in);
        }

        @Override
        public Rationale[] newArray(int size) {
            return new Rationale[size];
        }
    };

    public final String message;
    public final String title;
    public final String buttonText;

    public Rationale(@NonNull String message, @NonNull Context context) {

        this(message, "", context);
    }

    public Rationale(@NonNull String message,
            @NonNull String title,
            @NonNull Context context) {

        this(message, title, context.getString(R.string.rationale_ok));
    }

    public Rationale(@NonNull String message,
            @NonNull String title,
            @NonNull String buttonText) {

        this.message = message;
        this.title = title;
        this.buttonText = buttonText;
    }

    protected Rationale(Parcel in) {
        message = in.readString();
        title = in.readString();
        buttonText = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(message);
        dest.writeString(title);
        dest.writeString(buttonText);
    }
}
