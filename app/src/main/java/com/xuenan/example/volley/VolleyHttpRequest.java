package com.xuenan.example.volley;

import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request.Method;
import com.android.volley.RetryPolicy;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.xuenan.example.base.MyApp;
import com.xuenan.example.commonutil.AppLogMessageMgr;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


/**
 * @author xuenan
 *         请求超时设置三个参数的含义  等待时间  重复请求次数 下次请求等待时间
 */
public class VolleyHttpRequest {
    /**
     * 1.
     * StringRequest GET方式
     * @param url           地址
     * @param volleyRequest 回调函数
     */
    public static void String_request(Context context, String url, VolleyHandler<String> volleyRequest) {
        Volley_StringRequest(context, Method.GET, url, null, volleyRequest);
    }

    /**
     * 1.
     * StringRequset POST方式
     *
     * @param url           地址
     * @param map           参数
     * @param volleyRequest 回调函数
     */
    public static void String_request(Context context, String url, final Map<String, String> map, VolleyHandler<String> volleyRequest) {
        Volley_StringRequest(context, Method.POST, url, map, volleyRequest);
    }

    /**
     * 1.
     * 封装 StringRequest 数据请求
     *
     * @param method        方式
     * @param url           地址
     * @param params        参数
     * @param volleyRequest 回调对象
     */
    private static void Volley_StringRequest(final Context context, int method,final String url, final Map<String, String> params, VolleyHandler<String> volleyRequest) {
        StringRequest stringrequest = new StringRequest(method, url, volleyRequest.reqLis, volleyRequest.reqErr) {
            //传递参数
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return params;
            }
            //在此返回服务器返回的json信息 处理一些比如token过期 账号禁用的信息
            @Override
            protected void deliverResponse(String response) {
                super.deliverResponse(response);
                AppLogMessageMgr.e(response);
            }
            //给服务器上传请求头参数
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                String cookies = "";
                if (cookies != null && cookies.length() > 0) {
                    HashMap<String, String> headers = new HashMap<>();
                    //正常情况下
                    headers.put("token",cookies);
                    return headers;
                }
                return super.getHeaders();
            }

            //设置请求超时
            @Override
            public RetryPolicy getRetryPolicy() {
                RetryPolicy retryPolicy = new DefaultRetryPolicy(8000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
                return retryPolicy;
            }
        };
        stringrequest.setTag(url);
        MyApp.getInstance().getQueue().add(stringrequest);
    }

    /**
     * 2.
     * JsonObjectRequest GET 请求
     *
     * @param url           请求地址
     * @param volleyRequest 回调函数对象
     */
    public static void JsonObject_Request(Context context,String url, VolleyHandler<JSONObject> volleyRequest) {
        Volley_JsonObjectRequest(context,Method.GET, url, null, volleyRequest);
    }

    /**
     * 2
     * JsonObjectRequest POST 请求
     *
     * @param url           请求地址
     * @param jsonObject    请求参数
     * @param volleyRequest 回调函数对象
     */
    public static void JsonObject_Request(Context context,String url, JSONObject jsonObject, VolleyHandler<JSONObject> volleyRequest) {
        Volley_JsonObjectRequest(context,Method.POST, url, jsonObject, volleyRequest);
    }

    /**
     * 2.
     * 封装 JsonObjectRequest 请求方法
     *
     * @param method        方式
     * @param url           地址
     * @param jsonObject    参数
     * @param volleyRequest 回调函数对象
     */
    private static void Volley_JsonObjectRequest(final Context context,int method, String url, JSONObject jsonObject, VolleyHandler<JSONObject> volleyRequest) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(method, url, jsonObject, volleyRequest.reqLis, volleyRequest.reqErr) {
            //给服务器上传请求头参数
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                String cookies = "";
                if (cookies != null && cookies.length() > 0) {
                    HashMap<String, String> headers = new HashMap<>();
                    //正常情况下
                    headers.put("token",cookies);
                    return headers;
                }
                return super.getHeaders();
            }
            //设置请求超时
            @Override
            public RetryPolicy getRetryPolicy() {
                RetryPolicy retryPolicy = new DefaultRetryPolicy(8000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
                return retryPolicy;
            }
        };
        jsonObjectRequest.setTag(url);
        MyApp.getInstance().getQueue().add(jsonObjectRequest);
    }

    /**
     * 取消某个界面的网络	请求
     *
     * @param tag 请求的tag
     */
    public static void CancelAllRequest(String tag) {
        MyApp.getQueue().cancelAll(tag);
    }
}
