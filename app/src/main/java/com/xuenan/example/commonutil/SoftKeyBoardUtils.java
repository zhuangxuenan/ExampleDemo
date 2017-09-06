package com.xuenan.example.commonutil;

import android.content.Context;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Function:软键盘管理类
 * author: elmand6 on  2016/1/22 17:04.
 * email：lysa8156@sina.com
 */
public class SoftKeyBoardUtils {

    /**
     * 500毫秒后打开软键盘
     *
     * @param mEditText 输入框
     * @param mContext  上下文
     */
    public static void openKeybord(final EditText mEditText, final Context mContext) {

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            public void run() {
                InputMethodManager imm = (InputMethodManager) mContext
                        .getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(mEditText, InputMethodManager.RESULT_SHOWN);
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,
                        InputMethodManager.HIDE_IMPLICIT_ONLY);
            }
        }, 500);
    }

    /**
     * 多长时间后打开软键盘
     *
     * @param mEditText 输入框
     * @param mContext  上下文
     * @param duration  延迟时间单位毫秒
     */
    public static void openKeybord(final EditText mEditText, final Context mContext, long duration) {

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            public void run() {
                InputMethodManager imm = (InputMethodManager) mContext
                        .getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(mEditText, InputMethodManager.RESULT_SHOWN);
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,
                        InputMethodManager.HIDE_IMPLICIT_ONLY);
            }
        }, duration);
    }

    /**
     * 隐藏软键盘
     *
     * @param mEditText 输入框
     * @param mContext  上下文
     */
    public static void closeKeybord(final EditText mEditText, final Context mContext) {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                InputMethodManager imm = (InputMethodManager) mContext
                        .getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(mEditText.getWindowToken(), 0);
            }
        }, 0);
    }

    /**
     * 延迟多长时间后隐藏软键盘
     *
     * @param mEditText 输入框
     * @param mContext  上下文
     * @param duration  延迟时间单位毫秒
     */
    public static void closeKeybord(final EditText mEditText, final Context mContext, long duration) {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                InputMethodManager imm = (InputMethodManager) mContext
                        .getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(mEditText.getWindowToken(), 0);
            }
        }, duration);
    }
}
