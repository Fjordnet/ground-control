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

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Locale;

/**
 * Simple class for testing Ground Control with {@link Parcelable}.
 */
public class Contact implements Parcelable {

    private String firstName;
    private String lastName;
    private String email;
    private int birthYear;
    private int birthMonth;
    private int birthDate;

    public Contact(String firstName,
            String lastName,
            String email,
            int birthYear,
            int birthMonth,
            int birthDate) {

        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.birthYear = birthYear;
        this.birthMonth = birthMonth;
        this.birthDate = birthDate;
    }

    protected Contact(Parcel in) {
        firstName = in.readString();
        lastName = in.readString();
        email = in.readString();
        birthYear = in.readInt();
        birthMonth = in.readInt();
        birthDate = in.readInt();
    }

    public static final Creator<Contact> CREATOR = new Creator<Contact>() {
        @Override
        public Contact createFromParcel(Parcel in) {
            return new Contact(in);
        }

        @Override
        public Contact[] newArray(int size) {
            return new Contact[size];
        }
    };

    @Override
    public String toString() {
        return String.format(Locale.ENGLISH,
                "%s %s born %02d/%02d",
                firstName, lastName, 1 + birthMonth, birthDate);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(firstName);
        out.writeString(lastName);
        out.writeString(email);
        out.writeInt(birthYear);
        out.writeInt(birthMonth);
        out.writeInt(birthDate);
    }
}
