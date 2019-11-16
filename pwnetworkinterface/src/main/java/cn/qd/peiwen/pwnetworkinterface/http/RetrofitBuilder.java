package cn.qd.peiwen.pwnetworkinterface.http;


import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import cn.qd.peiwen.pwlogger.PWLogger;
import cn.qd.peiwen.pwnetworkinterface.http.interceptor.HeaderInterceptor;
import cn.qd.peiwen.pwnetworkinterface.http.interceptor.QueryParamsInterceptor;
import cn.qd.peiwen.pwnetworkinterface.http.interceptor.UserAgentInterceptor;
import cn.qd.peiwen.pwtools.EmptyUtils;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;


/**
 * Created by nick on 2017/9/5.
 */

public class RetrofitBuilder {
    private String baseURL;                                     //服务器根地址

    //TimeOut
    private long readTimeout = 60;                              //读取超时时限s
    private long writeTimeout = 60;                             //链接超时时限s
    private long connectTimeout = 60;                           //链接超时时限s

    private boolean enableLogger = false;

    //Headers And Params
    private String userAgent;                                   //UserAgent请求头
    private Map<String, String> headers;                         //公共请求头
    private Map<String, String> queryParams;                     //公共请求参数?a=b&c=d
    private List<Interceptor> customInterceptors;

    public RetrofitBuilder() {

    }

    public RetrofitBuilder baseURL(String baseURL) {
        this.baseURL = baseURL;
        return this;
    }

    public RetrofitBuilder readTimeout(long readTimeout) {
        this.readTimeout = readTimeout;
        return this;
    }

    public RetrofitBuilder writeTimeout(long writeTimeout) {
        this.writeTimeout = writeTimeout;
        return this;
    }

    public RetrofitBuilder connectTimeout(long connectTimeout) {
        this.connectTimeout = connectTimeout;
        return this;
    }

    public RetrofitBuilder userAgent(String userAgent) {
        this.userAgent = userAgent;
        return this;
    }

    public RetrofitBuilder enableLogger(boolean enable) {
        this.enableLogger = enable;
        return this;
    }

    public RetrofitBuilder headers(Map<String, String> headers) {
        this.headers = headers;
        return this;
    }

    public RetrofitBuilder queryParams(Map<String, String> queryParams) {
        this.queryParams = queryParams;
        return this;
    }

    public RetrofitBuilder customInterceptors(List<Interceptor> customInterceptors) {
        this.customInterceptors = customInterceptors;
        return this;
    }


    private Retrofit retrofit() {
        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl(this.baseURL)
                .client(this.httpClient())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create());
        return builder.build();
    }

    private OkHttpClient httpClient() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.readTimeout(this.readTimeout, TimeUnit.SECONDS);
        builder.writeTimeout(this.writeTimeout, TimeUnit.SECONDS);
        builder.connectTimeout(this.connectTimeout, TimeUnit.SECONDS);
        if (EmptyUtils.isNotEmpty(this.userAgent)) {
            builder.addInterceptor(new UserAgentInterceptor(this.userAgent));
        }
        if (EmptyUtils.isNotEmpty(this.headers)) {
            builder.addInterceptor(new HeaderInterceptor(this.headers));
        }
        if (EmptyUtils.isNotEmpty(this.queryParams)) {
            builder.addInterceptor(new QueryParamsInterceptor(this.queryParams));
        }

        this.buildCustomInterceptors(builder);

        if (this.enableLogger) {
            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor(new HttpLogger());
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            builder.addNetworkInterceptor(interceptor);
        }

        OkHttpClient httpClient = builder.build();
        httpClient.dispatcher().setMaxRequests(5);
        return httpClient;
    }

    private void buildCustomInterceptors(OkHttpClient.Builder builder) {
        if (EmptyUtils.isEmpty(this.customInterceptors)) {
            return;
        }
        for (Interceptor item : this.customInterceptors) {
            builder.addInterceptor(item);
        }
    }

    public <T> T builder(Class<T> service) {
        return this.retrofit().create(service);
    }

    public class HttpLogger implements HttpLoggingInterceptor.Logger {
        @Override
        public void log(String message) {
            PWLogger.d("" + message);
        }
    }
}