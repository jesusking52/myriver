package com.jhcompany.android.libs.utils;

import com.google.common.collect.Lists;

import java.lang.ref.WeakReference;
import java.util.List;

public class WeakReferences<T> {
    private List<WeakReference<T>> mReferences = Lists.newArrayList();

    public void add(T object) {
        synchronized (this) {
            WeakReference<T> reference = new WeakReference<T>(object);
            mReferences.add(reference);
        }
    }

    public void remove(T object) {
        synchronized (this) {
            List<WeakReference<T>> copy = Lists.newArrayList(mReferences);
            for (WeakReference<T> reference : copy) {
                if (reference.get() == null) {
                    mReferences.remove(reference);
                } else if (reference.get() == object) {
                    mReferences.remove(reference);
                }
            }
        }
    }

    public void visit(Visitor<T> visitor) {
        synchronized (this) {
            List<WeakReference<T>> copy = Lists.newArrayList(mReferences);
            for (WeakReference<T> reference : copy) {
                if (reference.get() == null) {
                    mReferences.remove(reference);
                } else {
                    visitor.visit(reference.get());
                }
            }
        }
    }

    public interface Visitor<T> {
        public void visit(T reference);
    }
}
