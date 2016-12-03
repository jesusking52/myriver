package com.jhcompany.android.libs.utils;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class Lists2 {
    private Lists2() {}

    public static <T> List<T> randomSubList(List<T> list, int size) {
        List<T> result = Lists.newArrayList(list);
        if (list.size() <= size) {
            return result;
        }
        Random r = new Random();
        while (result.size() > size) {
            result.remove(r.nextInt(result.size()));
        }
        return result;
    }

    public static <T> List<T> mapOnlyFirst(List<T> cardRankDTOs, Function<T, T> mapper) {
        List<T> result = Lists.newArrayListWithCapacity(cardRankDTOs.size());
        Iterator<T> iter = cardRankDTOs.iterator();
        if (iter.hasNext()) {
            result.add(mapper.apply(iter.next()));
        }
        while (iter.hasNext()) {
            result.add(iter.next());
        }
        return result;
    }

    public static <T> boolean isNullOrEmpty(List<T> list) {
        return list == null || list.isEmpty();
    }

    /**
     * Make 1 element list or empty list (if element is null)
     */
    public static <T> List<T> fromNullable(T element) {
        if (element == null) {
            return Collections.emptyList();
        }
        return ImmutableList.of(element);
    }
}
