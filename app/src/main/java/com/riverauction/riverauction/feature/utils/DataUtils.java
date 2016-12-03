package com.riverauction.riverauction.feature.utils;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;
import android.widget.Spinner;

import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.jhcompany.android.libs.jackson.Jackson;
import com.jhcompany.android.libs.utils.FileUtils;
import com.jhcompany.android.libs.utils.Lists2;
import com.riverauction.riverauction.R;
import com.riverauction.riverauction.api.model.CDayOfWeekType;
import com.riverauction.riverauction.api.model.CGender;
import com.riverauction.riverauction.api.model.CLocation;
import com.riverauction.riverauction.api.model.CSido;
import com.riverauction.riverauction.api.model.CSidoSigungu;
import com.riverauction.riverauction.api.model.CStudentDepartmentType;
import com.riverauction.riverauction.api.model.CStudentLevel;
import com.riverauction.riverauction.api.model.CStudentStatus;
import com.riverauction.riverauction.api.model.CSubject;
import com.riverauction.riverauction.widget.spinner.SpinnerItem;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

public final class DataUtils {
    private static final String ISO_860_DATE_FORMAT_TIME_ONLY = "HH:MM";

    public static String getUniversity(Spinner universitySpinner) {
        SpinnerItem spinnerItem = (SpinnerItem) universitySpinner.getSelectedItem();
        if (universitySpinner.getSelectedItemPosition() == 0) {
            return null;
        }

        if (spinnerItem == null) {
            return null;
        }
        return spinnerItem.getTitle();
    }

    public static Integer getUniversityRank(Context context, String university){
        HashMap<String, Integer> universityMap = Maps.newHashMap();
        Integer universityRank = 0;

        final AssetManager assetManager = context.getAssets();

        InputStream is = null;

        try{
            is = assetManager.open("university_rank.json");
            String jsonMapString = FileUtils.readString(is);
            universityMap = Jackson.stringToObject(jsonMapString, HashMap.class);

            if(universityMap.containsKey(university)){
                universityRank = universityMap.get(university);
            }
            else {
                universityRank = 0;
            }

        } catch (IOException e) {
            Log.e("universityRank", e.getMessage(), e);
        } finally{
            try {
                if (is != null) is.close();
            } catch (Exception ignored) {}
        }

        return universityRank;
    }

    public static String getSido(Spinner sidoSpinner) {
        SpinnerItem spinnerItem = (SpinnerItem) sidoSpinner.getSelectedItem();
        if (sidoSpinner.getSelectedItemPosition() == 0) {
            return null;
        }

        if (spinnerItem == null) {
            return null;
        }
        return spinnerItem.getTitle();
    }

    public static String getSigungu(Spinner sigunguSpinner) {
        SpinnerItem spinnerItem = (SpinnerItem) sigunguSpinner.getSelectedItem();
        if (sigunguSpinner.getSelectedItemPosition() == 0) {
            return null;
        }

        if (spinnerItem == null) {
            return null;
        }
        return spinnerItem.getTitle();
    }

    public static HashMap<String, List<String>> getSidoSigungu(Context context) {
        HashMap<String, List<String>> sidoSigungu = Maps.newHashMap();
        final AssetManager assetManager = context.getAssets();
        InputStream is = null;
        try {
            is = assetManager.open("sido_sigungu.json");
            String jsonMapString = FileUtils.readString(is);
            sidoSigungu = Jackson.stringToObject(jsonMapString, HashMap.class);
        } catch (IOException e) {
            Log.e("sidoSigungu", e.getMessage(), e);
        } finally {
            try {
                if (is != null) is.close();
            } catch (Exception ignored) {}
        }

        return sidoSigungu;
    }

    public static List<CSido> getSidoSigunguForFilter(Context context) {
        CSidoSigungu sidoSigungu = null;
        final AssetManager assetManager = context.getAssets();
        InputStream is = null;
        try {
            is = assetManager.open("sido_sigungu_zoncode.json");
            String jsonMapString = FileUtils.readString(is);
            sidoSigungu = Jackson.stringToObject(jsonMapString, CSidoSigungu.class);
        } catch (IOException e) {
            Log.e("sidoSigungu", e.getMessage(), e);
        } finally {
            try {
                if (is != null) is.close();
            } catch (Exception ignored) {}
        }

        if (sidoSigungu == null) {
            return null;
        } else {
            return sidoSigungu.getSidoSigungu();
        }
    }

    public static String convertAvailableDayOfWeekToString(Context context, List<CDayOfWeekType> dayOfWeekTypes) {
        if (Lists2.isNullOrEmpty(dayOfWeekTypes)) {
            return null;
        }

        List<String> dayOfWeekString = Lists.newArrayList();
        for (CDayOfWeekType dayOfWeekType : dayOfWeekTypes) {
            switch (dayOfWeekType) {
                case MON: {
                    dayOfWeekString.add(context.getString(R.string.common_mon));
                    break;
                }
                case TUE: {
                    dayOfWeekString.add(context.getString(R.string.common_tue));
                    break;
                }
                case WED: {
                    dayOfWeekString.add(context.getString(R.string.common_wed));
                    break;
                }
                case THU: {
                    dayOfWeekString.add(context.getString(R.string.common_thu));
                    break;
                }
                case FRI: {
                    dayOfWeekString.add(context.getString(R.string.common_fri));
                    break;
                }
                case SAT: {
                    dayOfWeekString.add(context.getString(R.string.common_sat));
                    break;
                }
                case SUN: {
                    dayOfWeekString.add(context.getString(R.string.common_sun));
                    break;
                }
            }
        }

        return Joiner.on(", ").skipNulls().join(dayOfWeekString);
    }

    public static String convertGenderToLongString(Context context, CGender gender) {
        if (context == null || gender == null) {
            return null;
        }

        if (gender == CGender.MALE) {
            return context.getString(R.string.sign_up_gender_man);
        } else if (gender == CGender.FEMALE) {
            return context.getString(R.string.sign_up_gender_woman);
        } else {
            return null;
        }
    }

    public static String convertGenderToShortString(Context context, CGender gender) {
        if (context == null || gender == null) {
            return null;
        }

        if (gender == CGender.MALE) {
            return context.getString(R.string.common_gender_male_short);
        } else if (gender == CGender.FEMALE) {
            return context.getString(R.string.common_gender_female_short);
        } else {
            return null;
        }
    }

    public static boolean isInteger(String value) {
        try {
            Integer integerValue = Integer.valueOf(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static String convertToAnonymousName(String name) {
        if (Strings.isNullOrEmpty(name)) {
            return null;
        }

        if (name.length() == 1) {
            return name;
        } else {
            return name.substring(0, 1) + "OO";
        }
    }

    public static String convertSubjectToString(List<CSubject> subjects) {
        if (subjects == null || subjects.isEmpty()) {
            return "";
        }

        List<String> convertedSubjects = Lists.newArrayList(Iterables.transform(subjects, input -> input.getName()));
        return Joiner.on(", ").skipNulls().join(convertedSubjects);
    }

    public static String convertCareerToString(Integer career) {
        if (career == null) {
            return "";
        }
        if (career == 0) {
            return "1년 미만";
        } else if (career == 6) {
            return "5년 이상";
        } else {
            return String.valueOf(career) + "년";
        }
    }

    public static String convertAvailableDaysOfWeekToString(Context context, List<CDayOfWeekType> dayOfWeekTypes) {
        if (dayOfWeekTypes == null || dayOfWeekTypes.isEmpty()) {
            return "";
        }

        List<String> dayOfWeekTypeStrings = Lists.newArrayList();
        for (CDayOfWeekType dayOfWeekType :dayOfWeekTypes) {
            if (dayOfWeekType == CDayOfWeekType.MON) {
                dayOfWeekTypeStrings.add(context.getString(R.string.common_mon));
            } else if (dayOfWeekType == CDayOfWeekType.TUE) {
                dayOfWeekTypeStrings.add(context.getString(R.string.common_tue));
            } else if (dayOfWeekType == CDayOfWeekType.WED) {
                dayOfWeekTypeStrings.add(context.getString(R.string.common_wed));
            } else if (dayOfWeekType == CDayOfWeekType.THU) {
                dayOfWeekTypeStrings.add(context.getString(R.string.common_thu));
            } else if (dayOfWeekType == CDayOfWeekType.FRI) {
                dayOfWeekTypeStrings.add(context.getString(R.string.common_fri));
            } else if (dayOfWeekType == CDayOfWeekType.SAT) {
                dayOfWeekTypeStrings.add(context.getString(R.string.common_sat));
            } else if (dayOfWeekType == CDayOfWeekType.SUN) {
                dayOfWeekTypeStrings.add(context.getString(R.string.common_sun));
            }
        }

        return Joiner.on(", ").skipNulls().join(dayOfWeekTypeStrings);
    }

    public static String convertStudentStatusToString(Context context, CStudentStatus studentStatus) {
        switch (studentStatus) {
            case KINDERGARTEN: {
                return context.getString(R.string.sign_up_student_status_kindergarten);
            }
            case ELEMENTARY_SCHOOL: {
                return context.getString(R.string.sign_up_student_status_elementary_school);
            }
            case MIDDLE_SCHOOL: {
                return context.getString(R.string.sign_up_student_status_middle_school);
            }
            case HIGH_SCHOOL: {
                return context.getString(R.string.sign_up_student_status_high_school);
            }
            case UNIVERSITY: {
                return context.getString(R.string.sign_up_student_status_university);
            }
            case RETRY_UNIVERSITY: {
                return context.getString(R.string.sign_up_student_status_retry_university);
            }
            case ORDINARY: {
                return context.getString(R.string.sign_up_student_status_ordinary);
            }
        }

        return "";
    }

    public static String convertStudentDepartmentTypeToString(Context context, CStudentDepartmentType type) {
        switch (type) {
            case LIBERAL_ARTS: {
                return context.getString(R.string.sign_up_department_type_liberal_arts);
            }
            case NATURAL_SCIENCES: {
                return context.getString(R.string.sign_up_department_type_natural_sciences);
            }
            case ART_MUSIC_PHYSICAL: {
                return context.getString(R.string.sign_up_department_type_art_music_physical);
            }
            case COMMERCIAL_AND_TECHNICAL: {
                return context.getString(R.string.sign_up_department_type_commercial_and_technical);
            }
            case NONE: {
                return context.getString(R.string.sign_up_department_type_none);
            }
        }

        return "";
    }

    public static String convertStudentLevelToString(Context context, CStudentLevel level) {
        switch (level) {
            case HIGH: {
                return context.getString(R.string.sign_up_level_high);
            }
            case MIDDLE: {
                return context.getString(R.string.sign_up_level_middle);
            }
            case LOW: {
                return context.getString(R.string.sign_up_level_low);
            }
        }

        return "";
    }

    public static String convertRemainTimeToString(Context context, Long expiresIn) {
        return String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toHours(expiresIn),
                TimeUnit.MILLISECONDS.toMinutes(expiresIn) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(expiresIn)));
    }

    public static String convertLocationToString(CLocation location) {
        List<String> locationList = Lists.newArrayList();
        if (!Strings.isNullOrEmpty(location.getSido())) {
            locationList.add(location.getSido());
        }
        if (!Strings.isNullOrEmpty(location.getSigungu())) {
            locationList.add(location.getSigungu());
        }
        if (!Strings.isNullOrEmpty(location.getBname())) {
            locationList.add(location.getBname());
        }

        return Joiner.on(" ").skipNulls().join(locationList);
    }

    public static List<Integer> convertSubjectsToIds(List<CSubject> subjects) {
        if (Lists2.isNullOrEmpty(subjects)) {
            return null;
        }
        return Lists.transform(subjects, input -> input.getId());
    }
}
