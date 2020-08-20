package cn.qd.peiwen.network.http;


import android.text.TextUtils;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import cn.qd.peiwen.network.http.interceptor.HeaderInterceptor;
import cn.qd.peiwen.network.http.interceptor.QueryParamsInterceptor;
import cn.qd.peiwen.network.http.interceptor.UserAgentInterceptor;
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

    private OkHttpClient httpClient;
    //Logger
    private IRetrofitLogger logger;

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

    public RetrofitBuilder logger(IRetrofitLogger logger) {
        this.logger = logger;
        return this;
    }

    public RetrofitBuilder userAgent(String userAgent) {
        this.userAgent = userAgent;
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

    public OkHttpClient httpClient() {
        return httpClient;
    }

    public <T> T builder(Class<T> service) {
        return this.retrofit().create(service);
    }

    private Retrofit retrofit() {
        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl(this.baseURL)
                .client(this.makeHttpClient())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create());
        return builder.build();
    }

    private OkHttpClient makeHttpClient() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.readTimeout(this.readTimeout, TimeUnit.SECONDS);
        builder.writeTimeout(this.writeTimeout, TimeUnit.SECONDS);
        builder.connectTimeout(this.connectTimeout, TimeUnit.SECONDS);
        if (!TextUtils.isEmpty(this.userAgent)) {
            builder.addInterceptor(new UserAgentInterceptor(this.userAgent));
        }
        if (null != this.headers && !this.headers.isEmpty()) {
            builder.addInterceptor(new HeaderInterceptor(this.headers));
        }
        if (null != this.queryParams && !this.queryParams.isEmpty()) {
            builder.addInterceptor(new QueryParamsInterceptor(this.queryParams));
        }

        this.buildCustomInterceptors(builder);

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor(new HttpLogger());
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        builder.addNetworkInterceptor(interceptor);

        this.httpClient = builder.build();
        this.httpClient.dispatcher().setMaxRequests(5);
        return this.httpClient;
    }

    private void buildCustomInterceptors(OkHttpClient.Builder builder) {
        if (null == this.customInterceptors || this.customInterceptors.isEmpty()) {
            return;
        }
        for (Interceptor item : this.customInterceptors) {
            builder.addInterceptor(item);
        }
    }


    class HttpLogger implements HttpLoggingInterceptor.Logger {
        @Override
        public void log(String message) {
            if (null != logger) {
                logger.log(message);
            }
        }
    }
}
