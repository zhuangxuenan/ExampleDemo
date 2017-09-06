package com.xuenan.example.volley;

import com.android.volley.Response;
import com.android.volley.VolleyError;


/**
 *  抽象出 成功的监听和失败的监听
 *  用来回调信息
 * @author yuan
 *
 * @param <T>
 */

public abstract class VolleyHandler<T> {


	public Response.Listener<T> reqLis;
	public Response.ErrorListener reqErr;

	public VolleyHandler() {
		// 初始化 变量
		reqLis = new reqListener();
		reqErr = new reqErrorListener();
	}

	public abstract void reqSuccess(T response);

	public abstract void reqError(String error);

	/**
	 * 成功后的监听
	 *
	 * @author yuan
	 *
	 */
	public class reqListener implements Response.Listener<T> {

		@Override
		public void onResponse(T response) {
			// 使用抽象函数 设置 回调函数 reqSuccess
			reqSuccess(response);
		}
	}

	/**
	 * 失败后的监听
	 *
	 * @author yuan
	 *
	 */
	public class reqErrorListener implements Response.ErrorListener {

		@Override
		public void onErrorResponse(VolleyError error) {
			// 设置回调函数 使用 抽象方法 ReqError
			reqError(error.getMessage());
		}
	}
}
