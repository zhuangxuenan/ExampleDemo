package com.xuenan.example.commonutil;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;

import java.util.List;

public class ServiceStatusUtils {


	/**
	 * 校验某个服务是否活着
	 * @param context 上下文
	 * @param serviceName 服务名称
	 * @return
	 */

	public static boolean isRunningService(Context context ,String serviceName){
		//管理Activity 还可以管理服务
		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningServiceInfo> serviceInfos = am.getRunningServices(100);
		for(RunningServiceInfo serviceInfo : serviceInfos){
			String name = serviceInfo.service.getClassName();
			if(serviceName.equals(name)){
				return true;
			}
		}
		return false;
	}

}
