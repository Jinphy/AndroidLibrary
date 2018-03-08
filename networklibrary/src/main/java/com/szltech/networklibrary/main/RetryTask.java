package com.szltech.networklibrary.main;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import rx.Observable;

/**
 * RxJava失败重连的任务
 * Created by Jinphy on 2018/3/8.
 */

public class RetryTask {

    /**
     * DESC: Rxjava失败重连的次数
     * Created by Jinphy, on 2018/3/8, at 9:42
     */
    private int retryCount;

    /**
     * DESC: 失败时抛出的异常
     * Created by Jinphy, on 2018/3/8, at 9:43
     */
    private Throwable error;

    private RetryTask() {

    }

    public RetryTask(int retryCount, Throwable error) {
        this.retryCount = retryCount;
        this.error = error;
    }


    public static Observable<?> make(Observable<? extends Throwable> attempt) {

        /**
         * DESC: 失败重连的结束次数，必实际的连接次数+1，即如果 maxCount = 4，则实际重连次数为3次
         * Created by Jinphy, on 2018/3/8, at 9:55
         */
        int maxCount = 4;

        /**
         * DESC: 重连的时间间隔
         * Created by Jinphy, on 2018/3/8, at 10:00
         */
        int interval = 3;

        return attempt.zipWith(Observable.range(1, maxCount), (error, i) -> new RetryTask(i, error))
                .flatMap(task -> {
                    if (task.retryCount < maxCount && (task.error instanceof ConnectException
                            || task.error instanceof SocketTimeoutException
                            || task.error instanceof TimeoutException)) {
                        return Observable.timer(task.retryCount * interval, TimeUnit.SECONDS);
                    } else {
                        return Observable.error(task.error);
                    }
                });
    }


}
