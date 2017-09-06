package com.xuenan.example.commonutil;

import android.content.Context;

import com.bigkoo.svprogresshud.view.SVCircleProgressBar;


/**
 * Author: implistarry
 * Date: 2016-06-30
 * Time: 16:57
 * Usage:弹窗工具
 */

public class SVProgressHUDUtil {
    private static com.bigkoo.svprogresshud.SVProgressHUD svProgressHUD;

    public static void showWithMaskType(Context context, com.bigkoo.svprogresshud.SVProgressHUD.SVProgressHUDMaskType maskType) {
        if (svProgressHUD == null) {
            svProgressHUD = new com.bigkoo.svprogresshud.SVProgressHUD(context);
        }
        svProgressHUD.showWithMaskType(maskType);
    }
    /**
     *加载一般的进度条
     */
    public static void showWithStatus(Context context, String string) {
        if (svProgressHUD == null) {
            svProgressHUD = new com.bigkoo.svprogresshud.SVProgressHUD(context);
        }
        svProgressHUD.showWithStatus(string);
    }
    /**
     *加载进度条 可设置样式背景 点击能否取消等
     */
    public static void showWithStatus(Context context, String string, com.bigkoo.svprogresshud.SVProgressHUD.SVProgressHUDMaskType maskType) {
        if (svProgressHUD == null) {
            svProgressHUD = new com.bigkoo.svprogresshud.SVProgressHUD(context);
        }
        svProgressHUD.showWithStatus(string, maskType);
    }
    /**
     *加载提示信息弹窗
     */
    public static void showInfoWithStatus(Context context, String string) {
        if (svProgressHUD == null) {
            svProgressHUD = new com.bigkoo.svprogresshud.SVProgressHUD(context);
        }
        svProgressHUD.showInfoWithStatus(string);
    }
    /**
     *加载提示信息弹窗 可设置样式背景 点击能否取消等
     */
    public static void showInfoWithStatus(Context context, String string, com.bigkoo.svprogresshud.SVProgressHUD.SVProgressHUDMaskType maskType) {
        if (svProgressHUD == null) {
            svProgressHUD = new com.bigkoo.svprogresshud.SVProgressHUD(context);
        }
        svProgressHUD.showInfoWithStatus(string, maskType);
    }
    /**
     *操作成功提示信息弹窗
     */
    public static void showSuccessWithStatus(Context context, String string) {
        if (svProgressHUD == null) {
            svProgressHUD = new com.bigkoo.svprogresshud.SVProgressHUD(context);
        }
        svProgressHUD.showSuccessWithStatus(string);
    }
    /**
     *操作成功提示信息弹窗 可设置样式背景 点击能否取消等
     */
    public static void showSuccessWithStatus(Context context, String string, com.bigkoo.svprogresshud.SVProgressHUD.SVProgressHUDMaskType maskType) {
        if (svProgressHUD == null) {
            svProgressHUD = new com.bigkoo.svprogresshud.SVProgressHUD(context);
        }
        svProgressHUD.showSuccessWithStatus(string, maskType);
    }
    /**
     *操作失败提示信息弹窗
     */
    public static void showErrorWithStatus(Context context, String string) {
        if (svProgressHUD == null) {
            svProgressHUD = new com.bigkoo.svprogresshud.SVProgressHUD(context);
        }
        svProgressHUD.showErrorWithStatus(string);
    }
    /**
     *操作失败提示信息弹窗 可设置样式背景 点击能否取消等
     */
    public static void showErrorWithStatus(Context context, String string, com.bigkoo.svprogresshud.SVProgressHUD.SVProgressHUDMaskType maskType) {
        if (svProgressHUD == null) {
            svProgressHUD = new com.bigkoo.svprogresshud.SVProgressHUD(context);
        }
        svProgressHUD.showErrorWithStatus(string, maskType);
    }
    /**
     *进度条  可加载进度
     */
    public static void showWithProgress(Context context, String string, com.bigkoo.svprogresshud.SVProgressHUD.SVProgressHUDMaskType maskType) {
        if (svProgressHUD == null) {
            svProgressHUD = new com.bigkoo.svprogresshud.SVProgressHUD(context);
        }
        svProgressHUD.showWithProgress(string, maskType);
    }
    /**
     *进度条  设置进度
     */
    public static SVCircleProgressBar getProgressBar(Context context) {
        if (svProgressHUD == null) {
            svProgressHUD = new com.bigkoo.svprogresshud.SVProgressHUD(context);
        }
        return svProgressHUD.getProgressBar();
    }
    /**
     *设置文本
     */
    public static void setText(Context context, String string) {
        if (svProgressHUD == null) {
            svProgressHUD = new com.bigkoo.svprogresshud.SVProgressHUD(context);
        }
        svProgressHUD.setText(string);
    }

    /**
     *进度条 是否正在展示
     */
    public static boolean isShowing(Context context) {
        if (svProgressHUD == null) {
            svProgressHUD = new com.bigkoo.svprogresshud.SVProgressHUD(context);
        }
        return svProgressHUD.isShowing();
    }
    /**
     *弹窗关闭
     */
    public static void dismiss(Context context) {
        if (svProgressHUD != null) {
            svProgressHUD.dismissImmediately();
            svProgressHUD = null;
        }
    }
}
