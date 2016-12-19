package funnybrain.testrxjava;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.util.Arrays;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 1
        Observable<String> myObservable1 =
                Observable.from(Arrays.asList("Hello from RxJava", "Welcome...", "Goodbye"));
        Subscriber<String> mySubscriber1 = new Subscriber<String>() {
            @Override
            public void onCompleted() {
                Log.i(TAG, "Rx Java events completed");
            }

            @Override
            public void onError(Throwable throwable) {
                Log.e(TAG, "Error found processing stream", throwable);
            }

            @Override
            public void onNext(String s) {
                Log.i(TAG, "New event - " + s);
            }
        };
        myObservable1.subscribe(mySubscriber1);

        // 2
        Observable<Integer> myObservable2 = Observable.create(
                new Observable.OnSubscribe<Integer>() {
                    @Override
                    public void call(Subscriber<? super Integer> subscriber) {
                        subscriber.onNext(10);
                        subscriber.onNext(3);
                        subscriber.onNext(9);
                        subscriber.onCompleted();
                    }
                }
        );
        Action1<Integer> onNextAction = new Action1<Integer>() {
            @Override
            public void call(Integer integer) {
                Log.i(TAG, "New number: " + integer);
            }
        };
        Action1<Throwable> onError = new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                Log.e(TAG, "Error: " + throwable.getMessage(), throwable);
            }
        };
        Action0 onComplete = new Action0() {
            @Override
            public void call() {
                Log.i(TAG, "Rx number stream completed");
            }
        };
        myObservable2.subscribe(onNextAction, onError, onComplete);

        // 3
        String content = "This is an example \n " +
                         "Looking for lines with the word RxJava\n" +
                         "We are finished.";
        Observable
                .just(content)
                .flatMap(new Func1<String, Observable<String>>() {
                    @Override
                    public Observable<String> call(final String s) {
                        return Observable.from(s.split("\n"));
                    }})
                .filter(new Func1<String, Boolean>() {
                    @Override
                    public Boolean call(String s) {
                        return s.contains("RxJava");
                    }
                })
                .count()
                .subscribe(new Subscriber<Integer>() {
                    @Override
                    public void onCompleted() {
                        Log.i(TAG, "onCompleted");
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        Log.e(TAG, "throwable: ", throwable);
                    }

                    @Override
                    public void onNext(Integer integer) {
                        Log.i(TAG, "Number of Lines " + integer);
                    }
                });
    }
}
