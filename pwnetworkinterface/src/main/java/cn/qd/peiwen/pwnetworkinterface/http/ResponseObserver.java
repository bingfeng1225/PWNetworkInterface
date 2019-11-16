package cn.qd.peiwen.pwnetworkinterface.http;


import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * Created by nick on 2017/9/5.
 */

public abstract class ResponseObserver<T> implements Observer<T> {
    @Override
    public void onSubscribe(Disposable d) {
        this.onStart();
    }

    @Override
    public void onNext(T entity) {
        this.onSuccess(entity);
        this.onCompleted();
    }

    @Override
    public void onError(Throwable e) {
        e.printStackTrace();
        this.onFailure(e);
        this.onCompleted();
    }

    protected abstract void onStart();

    protected abstract void onSuccess(T entity);

    protected abstract void onFailure(Throwable e);

    protected abstract void onCompleted();
}
