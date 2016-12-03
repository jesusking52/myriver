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


import android.os.Parcelable;

import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@SuppressWarnings({ "rawtypes", "unchecked" })
public abstract class ParcelableWrappers {

    /**
     * 특정 인스턴스를 {@link Parcelable}로 만든다.
     *
     * @param object 인스턴스
     * @return {@link Parcelable} 객체
     */
    public static <T> Parcelable wrap(T object) {
        ParcelableWrapper<T> result = new ParcelableWrapper<T>(object);
        return result;
    }

    public static <T> ArrayList<Parcelable> wrap(Collection<T> objects) {
        ArrayList<Parcelable> result = Lists.newArrayList();
        for (T object : objects) {
            result.add(wrap(object));
        }
        return result;
    }

    /**
     * {@link Parcelable} 객체를 받아서 자바 객체로 만든다.
     *
     * @param parcelable 객체
     * @return 자바 객체
     */
    public static <T> T unwrap(Parcelable parcelable) {
        ParcelableWrapper wrapper = (ParcelableWrapper) parcelable;
        return (T) wrapper.getObject();
    }

    public static <T> List<T> unwrap(ArrayList<Parcelable> parcelables) {
        List<T> result = Lists.newArrayList();
        for (Parcelable parcelable : parcelables) {
            result.add((T) unwrap(parcelable));
        }
        return result;
    }
}

