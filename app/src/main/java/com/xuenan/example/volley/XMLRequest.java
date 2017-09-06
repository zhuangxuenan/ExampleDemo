package com.xuenan.example.volley;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.toolbox.HttpHeaderParser;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.util.Map;

/**自定义XML请求方法
 * Created by xuenan on 2016/4/12.
 */
public class XMLRequest extends Request<XmlPullParser> {
    private Response.Listener<XmlPullParser> mListener;
    private Map<String,String>map;
//    private static Map<String, String> mHeader = new HashMap<String, String>();
//
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
    public XMLRequest(int method, String url,Map<String,String>map, Response.Listener<XmlPullParser> listener,
                      Response.ErrorListener errorListener) {
        super(method, url, errorListener);
        mListener = listener;
    }

    public XMLRequest(String url,Map<String,String>map, Response.Listener<XmlPullParser> listener, Response.ErrorListener errorListener) {
        this(Method.GET, url,map, listener, errorListener);
    }
    //传递参数
    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return map;
    }
    @Override
    protected Response<XmlPullParser> parseNetworkResponse(NetworkResponse response) {
        try {
            String xmlString = new String(response.data,
                    HttpHeaderParser.parseCharset(response.headers));
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser xmlPullParser = factory.newPullParser();
            xmlPullParser.setInput(new StringReader(xmlString));
            return Response.success(xmlPullParser, HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        } catch (XmlPullParserException e) {
            return Response.error(new ParseError(e));
        }
    }

    @Override
    protected void deliverResponse(XmlPullParser response) {
        mListener.onResponse(response);
    }
    //设置请求超时时间
    @Override
    public RetryPolicy getRetryPolicy() {
        RetryPolicy retryPolicy = new DefaultRetryPolicy(15000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        return retryPolicy;
    }
}
