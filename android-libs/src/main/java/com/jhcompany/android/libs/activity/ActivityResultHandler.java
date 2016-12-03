package com.jhcompany.android.libs.activity;

import android.os.Bundle;

import com.google.common.collect.Maps;

import java.util.Map;

public class ActivityResultHandler {

    private static final String KEY_REQUEST_ARRAY = "_request_array_";

    private Map<Integer, Bundle> requests = Maps.newHashMap();

    public void putRequestData(int requestCode, Bundle bundle) {
        if (bundle != null) {
            requests.put(requestCode, bundle);
        }
    }

    public Bundle getRequestData(int requestCode) {
        if (requests.containsKey(requestCode)) {
            return requests.remove(requestCode);
        }
        return null;
    }

    public synchronized Bundle saveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putIntArray(KEY_REQUEST_ARRAY, toPrimitive(requests.keySet().toArray(new Integer[requests.size()])));
        for (Integer requestCode : requests.keySet()) {
            bundle.putParcelable(requestCode.toString(), requests.get(requestCode));
        }
        return bundle;
    }

    public synchronized void restoreInstanceState(Bundle bundle) {
        if (bundle != null) {
            int[] integerArray = bundle.getIntArray(KEY_REQUEST_ARRAY);
            for (int request : integerArray) {
                requests.put(request, (Bundle) bundle.getParcelable(String.valueOf(request)));
            }
        }
    }

    private static int[] toPrimitive(Integer[] array) {
        if (array == null) {
            return null;
        } else if (array.length == 0) {
            return new int[0];
        }
        final int[] result = new int[array.length];
        for (int i = 0; i < array.length; i++) {
            result[i] = array[i].intValue();
        }
        return result;
    }

}
