package com.jhcompany.android.libs.log;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.google.common.base.Strings;
import com.jhcompany.android.libs.BuildConfig;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 내부적으로 사용하는 로깅 클래스이다. 기본적으로 trace, debug의 경우, DEBUG모드로 디바이스에 올린 경우에만 찍힌다.
 * 릴리즈하기위해 apk파일을 뽑은 경우에는 info, warn, error만 찍힌다. 내부 디버깅용으로 trace, debug를 사용하면
 * 된다. 실제로 이 클래스를 만들기 위해서는 {@link LoggerFactory}를 이용하면 된다.
 */
public class Logger {

    private static final Pattern PATTERN_SPLIT = Pattern.compile("(.{1,1020}(?:\\s\\n|$))|(.{0,1024})", Pattern.DOTALL);

    private final String mName;

    Logger(String name) {
        this.mName = name;
    }

    // trace
    public void trace(String msg) {
        if (BuildConfig.DEBUG) {
            Log.v(mName, Strings.nullToEmpty(msg));
        }
    }

    public void trace(String format, Object... args) {
        if (BuildConfig.DEBUG) {
            Log.v(mName, replace(Strings.nullToEmpty(format), args));
        }
    }

    public void trace(String msg, Throwable t) {
        if (BuildConfig.DEBUG) {
            Log.v(mName, Strings.nullToEmpty(msg), t);
        }
    }

    // debug
    public void debug(String msg) {
        if (BuildConfig.DEBUG) {
            Log.d(mName, Strings.nullToEmpty(msg));
        }
    }

    public void debug(String format, Object... args) {
        if (BuildConfig.DEBUG) {
            Log.d(mName, replace(Strings.nullToEmpty(format), args));
        }
    }

    public void debug(String msg, Throwable t) {
        if (BuildConfig.DEBUG) {
            Log.d(mName, Strings.nullToEmpty(msg), t);
        }
    }

    /**
     * 로그캣 길이 제한을 넘는 아주 긴 문자열을 출력할 때 사용. 약 1024 글자씩 끊어서 출력한다.
     *
     * @param msg 출력할 내용
     */
    public void debugSplit(String msg) {
        if (BuildConfig.DEBUG) {
            Matcher regexMatcher = PATTERN_SPLIT.matcher(msg);
            int i = 0;
            while (regexMatcher.find()) {
                Log.d(mName, regexMatcher.group());
                ++i;
            }
            // 이유는 잘 모르겠지만 위처럼 로그를 찍고 나면 이후에 태그 포맷이 깨진 로그가 찍힌다.
            // 어차피 깨지는 판에 위의 split 정보나 출력해주자.
            if (0 < i) {
                Log.d(mName, "Log split count=" + i);
            }
            // 이후의 로그들은 안 깨진다.
        }
    }

    public void info(String msg) {
        Log.i(mName, Strings.nullToEmpty(msg));
    }

    public void info(String format, Object... args) {
        Log.i(mName, replace(Strings.nullToEmpty(format), args));
    }

    public void info(String msg, Throwable t) {
        Log.i(mName, Strings.nullToEmpty(msg), t);
    }

    // warn
    public void warn(String msg) {
        Log.w(mName, Strings.nullToEmpty(msg));
    }

    public void warn(String format, Object... args) {
        Log.w(mName, replace(Strings.nullToEmpty(format), args));
    }

    public void warn(String msg, Throwable t) {
        Log.w(mName, Strings.nullToEmpty(msg), t);
    }

    // error
    public void error(String msg) {
        Log.e(mName, Strings.nullToEmpty(msg));
    }

    public void error(String format, Object... args) {
        Log.w(mName, replace(Strings.nullToEmpty(format), args));
    }

    public void error(String msg, Throwable t) {
        Log.e(mName, Strings.nullToEmpty(msg), t);
    }

    private String replace(String msg, Object... args) {
        String result = msg;
        for (Object arg : args) {
            try {
                result = result.replaceFirst("\\{\\}", String.valueOf(arg));
            } catch (Exception e) {
            }
        }
        return result;
    }

    /**
     * Return the full log of system with user's device information. It requires
     * permission as follow. <br>
     * <uses-permission android:name="android.permission.READ_LOGS" /><br>
     * <br>
     *
     * @param context <ul>
     *                <li>{@link Context}
     *                </ul>
     * @return log messages
     */
    public static final String getLog(Context context) {
        return getLogByTag(context, null);
    }

    public static final String getLogByTag(Context context, String tag) {
        // To get device brand name
        String brand = Build.BRAND;
        // To get device manufacturer name
        String manufacturer = Build.MANUFACTURER;
        // To get model name
        String model = Build.MODEL;
        // To get user's API version
        String sdkVer = String.valueOf(Build.VERSION.SDK_INT);
        // To get App's version in Androidmenifest.xml
        String appVerName = "";
        //To get App's version name in Androidmenifest.xml
        int appVerCode = 0;
        //To get MCC
        String mcc = getMcc(context);
        //TO get MNC
        String mnc = getMnc(context);

        PackageManager pm = context.getPackageManager();
        try {
            appVerName = pm.getPackageInfo(context.getPackageName(), PackageManager.GET_SIGNATURES).versionName;
            appVerCode = pm.getPackageInfo(context.getPackageName(), PackageManager.GET_SIGNATURES).versionCode;
        } catch (NameNotFoundException e1) {
            e1.printStackTrace();
        }
        // To get information of user
        String information = "Brand: " + brand +
                "\nManufacturer: " + manufacturer +
                "\nModel: " + model +
                "\nAPI Version: " + sdkVer +
                "\nApplication Version Name: " + appVerName +
                "\nApplication Version Code: " + appVerCode +
                "\nMCC : " + mcc +
                "\nMNC : " + mnc +
                "\n";

        String[] logcatCommand;
        if (tag == null) {
            logcatCommand = new String[] { "logcat", "-d", "-v", "time" };
        } else {
            logcatCommand = new String[] { "logcat", "-d", "-v", "time", tag + ":V", "*:S" };
        }

        Process logcatProc = null;
        try {
            logcatProc = Runtime.getRuntime().exec(logcatCommand);
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
        BufferedReader reader = null;
        String lineSeparatoer = System.getProperty("line.separator");
        StringBuilder strOutput = new StringBuilder();
        try {
            reader = new BufferedReader(new InputStreamReader(logcatProc.getInputStream()));
            String line;
            strOutput.append("---------------------------------------------------------------\n");
            strOutput.append(information);
            strOutput.append("---------------------------------------------------------------\n");
            while ((line = reader.readLine()) != null) {
                strOutput.append(line);
                strOutput.append(lineSeparatoer);
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return strOutput.toString();
    }

    private static String getMcc(Context context) {
        TelephonyManager tel = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String networkOperator = tel.getNetworkOperator();

        if (networkOperator != null) {
            try {
                int mcc = Integer.parseInt(networkOperator.substring(0, 3));
                return Integer.toString(mcc);
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }

    public static String getMnc(Context context) {
        TelephonyManager tel = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String networkOperator = tel.getNetworkOperator();

        if (networkOperator != null) {
            try {
                int mnc = Integer.parseInt(networkOperator.substring(3));
                return Integer.toString(mnc);
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }
}
