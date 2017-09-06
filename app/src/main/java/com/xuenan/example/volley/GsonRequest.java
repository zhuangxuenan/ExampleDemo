package com.xuenan.example.volley;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.RetryPolicy;
import com.android.volley.toolbox.HttpHeaderParser;
import com.google.gson.Gson;

import java.io.UnsupportedEncodingException;
import java.util.Map;


/**
 * 自定义  Gson 请求方法
 *
 * @param <T>
 * @author yuan
 *         http://blog.csdn.net/guolin_blog/article/details/17612763
 */
public class GsonRequest<T> extends Request<T> {

    private Listener<T> glistener;
    private Gson gson;
    private Class<T> gClass;
    private Map<String,String>map;
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
    /**
     * 构造函数 ，可以有多个不同参数的
     *
     * @param method
     * @param url
     * @param listener 返回对象
     */
    public GsonRequest(int method, String url,Map<String,String>map, Class<T> clazz, Listener<T> listener, ErrorListener errorlistener) {
        super(method, url, errorlistener);
        //初始化 参数
        this.gson = new Gson();
        this.gClass = clazz;
        this.glistener = listener;
        this.map = map;
    }

    public GsonRequest(String url,Map<String,String>map, Class<T> clazz, Listener<T> listener,
                       ErrorListener errorListener) {
        this(Method.GET, url,map, clazz, listener, errorListener);
    }

    @Override
    protected Response<T> parseNetworkResponse(NetworkResponse response) {
        try {
            //将字符流转成字符串，并且设置 字符编码 ，来自响应信息的报文信息
            String jsonString = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
            //返回信息 使用 gson 直接转 对象，第二个参数 设置编码
            return Response.success(gson.fromJson(jsonString, gClass), HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            // 出错的时候，将错误信息重新调出
            return Response.error(new ParseError(e));
        }
    }
    //传递参数
    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return map;
    }
    @Override
    protected void deliverResponse(T response) {
        glistener.onResponse(response);
    }

    //设置请求超时时间
    //请求读写时间  重复次数  下次请求读写时间
    @Override
    public RetryPolicy getRetryPolicy() {
        RetryPolicy retryPolicy = new DefaultRetryPolicy(15000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        return retryPolicy;
    }
}
