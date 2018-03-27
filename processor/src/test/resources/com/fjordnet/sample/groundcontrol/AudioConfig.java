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

import java.util.Date;

/**
 * Data class implementing {@link Parcelable} for the purposes of testing
 * handling of single instances and arrays of {@link Parcelable} instances.
 */
public class AudioConfig implements Parcelable {

    private int quality;
    private String fileName;
    private Date date;

    protected AudioConfig(Parcel in) {
        quality = in.readInt();
        fileName = in.readString();
        date = new Date(in.readLong());
    }

    public AudioConfig(int quality, String fileName, Date date) {
        this.quality = quality;
        this.fileName = fileName;
        this.date = date;
    }

    public static final Creator<AudioConfig> CREATOR = new Creator<AudioConfig>() {
        @Override
        public AudioConfig createFromParcel(Parcel in) {
            return new AudioConfig(in);
        }

        @Override
        public AudioConfig[] newArray(int size) {
            return new AudioConfig[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(quality);
        out.writeString(fileName);
        out.writeLong(date.getTime());
    }
}
