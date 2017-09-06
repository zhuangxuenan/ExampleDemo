package com.xuenan.example.commonutil;

import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.content.Context;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

public class SystemInfoUtils {

    /**
     * 得到当前手机里运行的进程总数
     */

    public static int getProcessCount(Context context) {
        ActivityManager am = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        return am.getRunningAppProcesses().size();
    }

    /**
     * 得到可用内存ram
     */

    public static long getAvailRam(Context context) {
        ActivityManager am = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        MemoryInfo outInfo = new MemoryInfo();
        am.getMemoryInfo(outInfo);
        return outInfo.availMem;

    }

    /**
     * 得到总内存ram
     */

    public static long getTotalRam(Context context) {
        // 下面的代码，在4.0以后才支持
        // ActivityManager am = (ActivityManager)
        // context.getSystemService(Context.ACTIVITY_SERVICE);
        // MemoryInfo outInfo = new MemoryInfo();
        // am.getMemoryInfo(outInfo);
        // return outInfo.totalMem;
        // MemTotal: 516452 kB
        File file = new File("/proc/meminfo");
        try {
            FileInputStream fis = new FileInputStream(file);
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    fis));
            String line = reader.readLine();
            StringBuffer buffer = new StringBuffer();
            for (char c : line.toCharArray()) {
                if (c >= '0' && c <= '9') {
                    buffer.append(c);
                }
            }
            fis.close();
            reader.close();
            return Integer.valueOf(buffer.toString()) * 1024;

            // 字符串--字符

        } catch (Exception e) {
            //
            e.printStackTrace();
            return 0;
        }

    }

}
