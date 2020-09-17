package com.example.velveteyebrows.network;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.LruCache;
import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.ImageLoader;

public class ServiceImageRequester  {


    private static ServiceImageRequester _instance;
    private final Context _context;
    private RequestQueue _requestQueue;
    private final ImageLoader _imageLoader;


    private ServiceImageRequester(Context context) {

        _context = context.getApplicationContext();
        _requestQueue = getRequestQueue();

        _imageLoader = new ImageLoader(_requestQueue, new ImageLoader.ImageCache() {

            private final LruCache<String, Bitmap> _lruCache
                    = new LruCache<>(100);

            @Override
            public Bitmap getBitmap(String url) {
                return  _lruCache.get(url);
            }

            @Override
            public void putBitmap(String url, Bitmap bitmap) {
                _lruCache.put(url, bitmap);
            }
        });
    }



    private RequestQueue getRequestQueue(){

        if(_requestQueue == null){

            Cache cache = new DiskBasedCache(_context.getCacheDir(), 10*1024*1024);
            Network network = new BasicNetwork(new HurlStack());

            _requestQueue = new RequestQueue(cache, network);
            _requestQueue.start();
        }

        return _requestQueue;
    }

    public static ServiceImageRequester getInstance(Context context){
        if(_instance == null){
            _instance = new ServiceImageRequester(context);
        }
        return _instance;
    }

    public ImageLoader getImageLoader(){
        return _imageLoader;
    }
}
