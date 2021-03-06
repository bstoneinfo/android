package com.bstoneinfo.lib.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.util.LruCache;
import android.text.TextUtils;

import com.bstoneinfo.lib.app.BSApplication;
import com.bstoneinfo.lib.common.BSObserverCenter.BSObserverEvent;
import com.bstoneinfo.lib.connection.BSConnection;
import com.bstoneinfo.lib.connection.BSConnectionQueue;
import com.bstoneinfo.lib.connection.BSFileConnection;
import com.bstoneinfo.lib.connection.BSConnection.BSProgressListener;
import com.bstoneinfo.lib.connection.BSFileConnection.BSFileConnectionListener;

public class BSImageLoader {

    public enum BSImageLoadStatus {
        INIT,
        LOCAL_LOADING,
        REMOTE_LOADING,
        LOADED,
        FAILED,
        CANCELED
    }

    public interface BSImageLoaderListener {
        public void finished(Bitmap bitmap);

        public void failed(Throwable throwable);
    }

    public interface BSStatusChangedListener {
        void statusChanged(BSImageLoadStatus status);
    }

    private static final LruCache<String, Bitmap> imageCache;
    private static final BSLooperThread loadThread;
    private BSStatusChangedListener statusChangedListener;
    private BSProgressListener progressListener;
    private BSImageLoadStatus loadStatus = BSImageLoadStatus.INIT;

    //    public static char[] tmpMemory;

    static {
        int percent = BSApplication.getApplication().getRemoteConfig().optInt("ImageCachePercent", 30);
        if (percent <= 0) {
            imageCache = null;
        } else {
            long memorySize = Runtime.getRuntime().maxMemory();
            BSLog.d("Memory Size: " + memorySize / 1024 + "K");
            //            int testMemSize = 32 * 1024 * 1024;
            //            tmpMemory = new char[(int) ((memorySize - testMemSize) / 2)];
            //            memorySize = testMemSize;
            imageCache = new LruCache<String, Bitmap>((int) memorySize / 1024 * percent / 100) {

                @Override
                protected int sizeOf(String key, Bitmap bitmap) {
                    int bitmapSize = getBitmapSize(bitmap) / 1024;
                    return bitmapSize == 0 ? 1 : bitmapSize;
                }

                @Override
                protected void entryRemoved(boolean evicted, String key, Bitmap oldValue, Bitmap newValue) {
                    super.entryRemoved(evicted, key, oldValue, newValue);
                }

                private int getBitmapSize(Bitmap bitmap) {
                    return bitmap.getRowBytes() * bitmap.getHeight();
                }
            };
        }
        loadThread = new BSLooperThread("BSImageLoader Thread");
    }

    public static void clearMemoryCache() {
        if (imageCache != null) {
            synchronized (imageCache) {
                imageCache.evictAll();
            }
        }
    }

    private final ArrayList<BSConnection> connections = new ArrayList<BSConnection>();
    private BSConnectionQueue connectionQueue;

    public BSImageLoader() {
    }

    public void setConnectionQueue(BSConnectionQueue queue) {
        connectionQueue = queue;
    }

    public BSImageLoadStatus getImageLoadStatus() {
        return loadStatus;
    }

    public boolean isLoading() {
        return loadStatus == BSImageLoadStatus.LOCAL_LOADING || loadStatus == BSImageLoadStatus.REMOTE_LOADING;
    }

    public void setStatusChangedListener(BSStatusChangedListener statusChangedListener) {
        this.statusChangedListener = statusChangedListener;
    }

    public void setProgressListener(BSProgressListener progressListener) {
        this.progressListener = progressListener;
    }

    public void loadImage(final String imageUrl, final BSImageLoaderListener listener) {
        String localPath = BSUtils.getDiskPath(imageUrl);
        if (TextUtils.isEmpty(localPath)) {
            return;
        }
        if (getImageLoadStatus() != BSImageLoadStatus.INIT) {
            return;
        }
        final Handler handler = new Handler();
        if (new File(localPath).exists()) {
            loadStatus = BSImageLoadStatus.LOCAL_LOADING;
            if (statusChangedListener != null) {
                statusChangedListener.statusChanged(loadStatus);
            }
            loadBitampFromLocalFile(handler, localPath, listener);
        } else if (BSUtils.isHttpUrl(imageUrl)) {
            loadStatus = BSImageLoadStatus.REMOTE_LOADING;
            if (statusChangedListener != null) {
                statusChangedListener.statusChanged(loadStatus);
            }
            loadThread.run(new Runnable() {
                @Override
                public void run() {
                    BSFileConnection connection = new BSFileConnection(imageUrl);
                    connection.setConnectionQueue(connectionQueue);
                    connection.start(new BSFileConnectionListener() {
                        @Override
                        public void finished(String localPath) {
                            loadBitampFromLocalFile(handler, localPath, listener);
                        }

                        @Override
                        public void failed(Exception e) {
                            notifyFailed(handler, listener, e);
                        }
                    });
                }
            });
        } else {
            notifyFailed(handler, listener, new FileNotFoundException(imageUrl + " not found."));
        }
    }

    public void cancel() {
        for (BSConnection connection : connections) {
            connection.cancel();
        }
        connections.clear();
        loadStatus = BSImageLoadStatus.CANCELED;
        if (statusChangedListener != null) {
            statusChangedListener.statusChanged(loadStatus);
        }
    }

    public static Bitmap getBitampFromMemoryCache(String imageUrl) {
        if (imageCache == null) {
            return null;
        }
        synchronized (imageCache) {
            return imageCache.get(getKey(imageUrl));
        }
    }

    private void addBitampToMemoryCache(String imageUrl, Bitmap bitmap) {
        if (imageCache != null) {
            synchronized (imageCache) {
                imageCache.put(getKey(imageUrl), bitmap);
            }
        }
    }

    private void loadBitampFromLocalFile(final Handler handler, final String localPath, final BSImageLoaderListener listener) {
        loadThread.run(new Runnable() {
            @Override
            public void run() {
                if (getImageLoadStatus() == BSImageLoadStatus.CANCELED) {
                    return;
                }
                InputStream stream = null;
                try {
                    stream = new FileInputStream(localPath);
                } catch (FileNotFoundException e) {
                }
                if (stream != null) {
                    try {
                        Bitmap bitmap = BitmapFactory.decodeStream(stream, null, null);
                        addBitampToMemoryCache("file://" + localPath, bitmap);
                        notifyFinished(handler, listener, bitmap);
                    } catch (Throwable e) {
                        if (e instanceof OutOfMemoryError) {
                            clearMemoryCache();
                            final Object lock = new Object();
                            synchronized (lock) {
                                new Handler(Looper.getMainLooper()).post(new Runnable() {
                                    @Override
                                    public void run() {
                                        BSApplication.defaultNotificationCenter.notifyOnMainThread(BSObserverEvent.LOW_MEMORY_WARNING);
                                        synchronized (lock) {
                                            lock.notify();
                                        }
                                    }
                                });
                                try {
                                    lock.wait();
                                } catch (InterruptedException e1) {
                                    e1.printStackTrace();
                                }
                            }
                            try {
                                Bitmap bitmap = BitmapFactory.decodeStream(stream, null, null);
                                addBitampToMemoryCache("file://" + localPath, bitmap);
                                notifyFinished(handler, listener, bitmap);
                            } catch (Throwable t) {
                                notifyFailed(handler, listener, t);
                            }
                        } else {
                            notifyFailed(handler, listener, e);
                        }
                    } finally {
                        try {
                            stream.close();
                        } catch (IOException e) {
                        }
                    }
                }
            }
        });
    }

    private void notifyFinished(Handler handler, final BSImageLoaderListener listener, final Bitmap bitmap) {
        if (isLoading()) {
            loadStatus = BSImageLoadStatus.LOADED;
            handler.post(new Runnable() {
                @Override
                public void run() {
                    if (statusChangedListener != null) {
                        statusChangedListener.statusChanged(loadStatus);
                    }
                    if (listener != null) {
                        listener.finished(bitmap);
                    }
                }
            });
        }
    }

    private void notifyFailed(Handler handler, final BSImageLoaderListener listener, final Throwable e) {
        if (loadStatus == BSImageLoadStatus.INIT || isLoading()) {
            loadStatus = BSImageLoadStatus.FAILED;
            handler.post(new Runnable() {
                @Override
                public void run() {
                    if (statusChangedListener != null) {
                        statusChangedListener.statusChanged(loadStatus);
                    }
                    if (listener != null) {
                        listener.failed(e);
                    }
                }
            });
        }
    }

    private static String getKey(String imageUrl) {
        return BSUtils.getDiskPath(imageUrl);
    }

}
