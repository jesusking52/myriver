package com.riverauction.riverauction.feature.notification.message;

import android.content.Context;
import android.text.SpannableStringBuilder;
import android.text.style.CharacterStyle;
import android.text.style.TextAppearanceSpan;

import com.google.common.collect.Lists;
import com.riverauction.riverauction.R;
import com.riverauction.riverauction.feature.utils.DataUtils;

import java.util.List;
import java.util.Map;

public final class NotificationComposer {
    private NotificationComposer() {
    }

    public static CharSequence style(Context context, String skeleton, Map<String, String> words) {
        if (words == null) {
            return skeleton;
        }
        TextAppearanceSpan textAppearanceSpan = new TextAppearanceSpan(context, R.style.NotificationGreyishTwoText);
        final SpannableStringBuilder builder = new SpannableStringBuilder(skeleton);
        List<String> keys = Lists.newArrayList(words.keySet());

        // skeleton 에서 바꿔야할 string 을 바꾼다.
        for (String key : keys) {
            while (true) {
                int start = builder.toString().indexOf(key);
                String word = words.get(key);
                if (key.equals("{{USER_NAME}}")) {
                    word = DataUtils.convertToAnonymousName(word);
                }

                if (start != -1) {
                    builder.replace(start, start + key.length(), word);

                    if (textAppearanceSpan != null) {
                        CharacterStyle style = TextAppearanceSpan.wrap(textAppearanceSpan);
                        builder.setSpan(style, start, start + word.length(), SpannableStringBuilder.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }
                } else {
                    break;
                }
            }
        }

        return builder;
    }
}
