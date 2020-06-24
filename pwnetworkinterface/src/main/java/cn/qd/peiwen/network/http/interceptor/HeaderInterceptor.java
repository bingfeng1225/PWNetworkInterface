package cn.qd.peiwen.network.http.interceptor;



import java.io.IOException;
import java.util.Map;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 通用头拦截器
 * Created by nick on 2017/9/5.
 */

public class HeaderInterceptor implements Interceptor {

    private Map<String,String> headers;

    public HeaderInterceptor(Map<String,String> headers) {
        this.headers = headers;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request.Builder builder = chain.request().newBuilder();
        this.addHeaders(builder);
        return chain.proceed(builder.build());
    }

    private void addHeaders(Request.Builder builder){
        if (null == this.headers || this.headers.isEmpty()) {
            return;
        }
        for (Map.Entry<String, String> entry : this.headers.entrySet()) {
            builder.addHeader(entry.getKey(), entry.getValue());
        }
    }
}
