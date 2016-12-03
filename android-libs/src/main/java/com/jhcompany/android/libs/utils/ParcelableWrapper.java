/**
 * Copyright (C) 2014 VCNC Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.jhcompany.android.libs.utils;


import android.os.Parcel;
import android.os.Parcelable;

import com.jhcompany.android.libs.jackson.Jackson;

public class ParcelableWrapper<T> implements Parcelable {

    private T object;

    public ParcelableWrapper() {
    }

    public ParcelableWrapper(T object) {
        this.object = object;
    }

    public ParcelableWrapper(Parcel in) {
        readFromParcel(in);
    }

    public T getObject() {
        return object;
    }

    public void setObject(T object) {
        this.object = object;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (object == null) {
            return;
        }

        try {
            Class<T> clazz = (Class<T>) object.getClass();
            String jsonString = Jackson.objectToString(object, clazz);
            dest.writeString(object.getClass().getName());
            dest.writeString(jsonString);
        } catch (Throwable e) {
            throw new IllegalStateException(e);
        }
    }

    @SuppressWarnings("unchecked")
    private void readFromParcel(Parcel in) {
        try {
            String clazzName = in.readString();
            Class<?> clazz = Class.forName(clazzName);
            String jsonString = in.readString();
            object = (T) Jackson.stringToObject(jsonString, clazz);
        } catch (Throwable e) {
            throw new IllegalStateException(e);
        }
    }

    @SuppressWarnings("rawtypes")
    public static final Creator CREATOR = new Creator() {
        public ParcelableWrapper createFromParcel(Parcel in) {
            return new ParcelableWrapper(in);
        }

        public ParcelableWrapper[] newArray(int size) {
            return new ParcelableWrapper[size];
        }
    };
}

