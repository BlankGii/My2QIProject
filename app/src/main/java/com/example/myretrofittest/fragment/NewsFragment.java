package com.example.myretrofittest.fragment;

import android.app.Fragment;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.example.myretrofittest.R;
import com.example.myretrofittest.loadingView.LVBlock;
import com.example.myretrofittest.network.Api;
import com.example.myretrofittest.network.adapter.HeadNewsAdapter;
import com.example.myretrofittest.network.bean.HeadNewResponse;
import com.example.myretrofittest.network.bean.News;
import com.example.myretrofittest.util.AndroidUtil;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by gcy on 2017/6/2 0002.
 */
public class NewsFragment extends Fragment {
    private final String URL = "http://c.m.163.com/nc/article/headline/T1348647853363/";
    private final String TAG = "NewsFragment";
    @Bind(R.id.listView)
    RecyclerView mListView;
    @Bind(R.id.parentView)
    FrameLayout mParentView;
    @Bind(R.id.loadingView)
    LVBlock mLoadingView;
    private List<News> mData;
    private HeadNewsAdapter mAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_news, null);
        ButterKnife.bind(this, view);
        initView();
//        更新头条数据
        startLoadingView();
        return view;
    }

    private void startLoadingView() {
        mLoadingView.setVisibility(View.VISIBLE);
        mLoadingView.isShadow(false);
        mLoadingView.startAnim();
        Observable.timer(2, TimeUnit.SECONDS).subscribe(new Consumer() {

            @Override
            public void accept(Object o) throws Exception {
                initHeadNews();
            }
        });
    }

    private void initHeadNews() {
        getRetrofitObject(URL).create(Api.class).getNewsLists()
                .subscribeOn(Schedulers.io())              //在IO线程进行网络请求
                .observeOn(AndroidSchedulers.mainThread())  //回到主线程去处理请求结果
                .subscribe(new Observer<HeadNewResponse>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(HeadNewResponse value) {
                        mData.clear();
                        for (HeadNewResponse.T1348647853363Bean childBean : value.getT1348647853363()) {
                            News news = new News();
                            news.setContent(childBean.getDigest());
                            news.setPicUrl(childBean.getImgsrc());
                            news.setTitle(childBean.getTitle());
                            mData.add(news);
                        }
                        mAdapter.notifyDataSetChanged();
                        mLoadingView.stopAnim();
                        mLoadingView.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError(Throwable e) {
                        AndroidUtil.showHintBar(mParentView, "下载失败");
                    }

                    @Override
                    public void onComplete() {
                        AndroidUtil.showHintBar(mParentView, "下载成功");
                    }
                });
    }

    private void initView() {
        mData = new ArrayList<>();
        mListView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mAdapter = new HeadNewsAdapter(mData);
        mAdapter.openLoadAnimation(BaseQuickAdapter.SLIDEIN_RIGHT);
        mAdapter.isFirstOnly(false);
        mListView.setAdapter(mAdapter);
    }

    private Retrofit getRetrofitObject(String url) {
        OkHttpClient.Builder builder = new OkHttpClient().newBuilder();
        builder.readTimeout(10, TimeUnit.SECONDS);
        builder.connectTimeout(9, TimeUnit.SECONDS);

        Log.e(TAG, "=================>DEBUG_ING");
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        builder.addInterceptor(interceptor);
        //缓存
        File httpCacheDirectory = new File(getActivity().getExternalCacheDir(), "responses");
        builder.cache(new Cache(httpCacheDirectory, 10 * 1024 * 1024));
        initInterceptor(builder);
//            builder.setCache(new Cache(httpCacheDirectory,10 * 1024 * 1024));
        return new Retrofit.Builder().baseUrl(url)
                .client(builder.build())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
    }

    private void initInterceptor(OkHttpClient.Builder builder) {
        Interceptor interceptor = new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request request = chain.request();
                if (!isNetworkReachable(getActivity())) {
                    request = request.newBuilder()
                            .cacheControl(CacheControl.FORCE_CACHE)
                            .build();
                    Log.e(TAG, "=====================>暂无网络");
                }

                Response response = chain.proceed(request);
                if (isNetworkReachable(getActivity())) {
                    int maxAge = 60 * 60; // read from cache for 1 minute
                    response.newBuilder()
                            .removeHeader("Pragma")
                            .header("Cache-Control", "public, max-age=" + maxAge)
                            .build();
                } else {
                    int maxStale = 60 * 60 * 24 * 28; // tolerate 4-weeks stale
                    response.newBuilder()
                            .removeHeader("Pragma")
                            .header("Cache-Control", "public, only-if-cached, max-stale=" + maxStale)
                            .build();
                }
                return response;
            }
        };
        builder.interceptors().add(interceptor);
    }

    /**
     * 判断网络是否可用
     *
     * @param context Context对象
     */
    private Boolean isNetworkReachable(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo current = cm.getActiveNetworkInfo();
        if (current == null) {
            return false;
        }
        return (current.isAvailable());
    }
}
