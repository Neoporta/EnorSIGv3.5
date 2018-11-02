package enorsul.com.enorsul;

import android.app.Application;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import enorsul.com.enorsul.services.NewsService;

public class NewsApp extends Application {

    private static NewsApp instance;
    private NewsService service = new NewsService();

    public static final String TAG = NewsApp.class
            .getSimpleName();

    private RequestQueue mRequestQueue;

    private static NewsApp mInstance;

    public NewsApp() {
        super();
        instance = this;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
    }

    public static NewsApp getInstance() {
        return instance;
    }

    public NewsService getService() {
        return service;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }

        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req, String tag) {
        // set the default tag if tag is empty
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }

    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }

    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }


}
