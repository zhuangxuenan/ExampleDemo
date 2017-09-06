package com.xuenan.example.volley;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;

import java.util.HashMap;
import java.util.Map;

/**
 * 自定义请求 返回字节数组 可以用作数据缓存
 * Created by xuenan on 2016/4/12.
 */
public class ByteArrayRequest extends Request<byte[]> {
    private final Response.Listener<byte[]> mListener;
    private Map<String,String> map;
    private static Map<String, String> mHeader = new HashMap<String, String>();
    public ByteArrayRequest(int method, String url,Map<String,String>map, Response.Listener<byte[]> listener, Response.ErrorListener errlistener) {
        super(method, url, errlistener);
        this.mListener = listener;
        this.map = map;
    }

    //传递参数
    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return map;
    }
//    /**
//     * 设置访问自己服务器时必须传递的参数，密钥等
//     */
//    static {
//        mHeader.put("APP-Key", "LBS-AAA");
//        mHeader.put("APP-Secret", "ad12msa234das232in");
//    }
//    @Override
//    public Map<String, String> getHeaders() throws AuthFailureError {
//        // 默认返回 return Collections.emptyMap();
//        return mHeader;
//    }
    @Override
    protected Response<byte[]> parseNetworkResponse(NetworkResponse response) {
        if(response == null){
            return null;
        }
        if(response.statusCode != 200){
            return null;
        }
        byte[] bytes = response.data;
        return Response.success(bytes, null);
    }
    @Override
    protected void deliverResponse(byte[] response) {
        if(mListener != null){
            mListener.onResponse(response);
        }
    }
    //设置请求超时时间
    @Override
    public RetryPolicy getRetryPolicy() {
        RetryPolicy retryPolicy = new DefaultRetryPolicy(15000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        return retryPolicy;
    }
}
