package com.bstoneinfo.lib.connection;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import android.os.Handler;

public class BSConnection {

    public enum BSConnectionMethod {
        GET,
        POST
    }

    public enum BSConnectionStatus {
        Init,
        Running,
        Finished,
        Failed,
        Canceled
    }

    public interface BSConnectionListener {
        void finished(byte[] response);

        void failed(Exception exception);
    }

    public interface BSProgressListener {
        void progress(int downloadedBytes, int totalBytes);
    }

    protected final String url;
    private BSConnectionStatus connectionStatus = BSConnectionStatus.Init;
    private BSConnectionMethod requestMethod = BSConnectionMethod.GET;
    private final HashMap<String, String> parameters = new HashMap<String, String>();
    private final HashMap<String, String> properties = new HashMap<String, String>();
    private final ArrayList<BSConnection> equalConnections = new ArrayList<BSConnection>();
    private BSConnectionQueue connectionQueue;
    private BSConnectionListener conectionListener;
    private BSProgressListener progressListener;
    private Handler handler;

    public BSConnection(String url) {
        this.url = url;
    }

    protected boolean equals(BSConnection connection) {
        return false;
    }

    public BSConnectionStatus getConnectionStatus() {
        return connectionStatus;
    }

    public void setRequestMethod(BSConnectionMethod method) {
        requestMethod = method;
    }

    public void addParameter(String key, String value) {
        parameters.put(key, value);
    }

    public void addProperty(String key, String value) {
        properties.put(key, value);
    }

    public void setConnectionQueue(BSConnectionQueue queue) {
        connectionQueue = queue;
    }

    public void setProgressListener(BSProgressListener progressListener) {
        this.progressListener = progressListener;
    }

    public void start(BSConnectionListener listener) {
        handler = new Handler();
        if (connectionStatus == BSConnectionStatus.Init) {
            if (connectionQueue != null) {
                connectionQueue.add(this, listener);
            } else {
                start(listener, this);
            }
            connectionStatus = BSConnectionStatus.Running;
        }
    }

    void start(final BSConnectionListener listener, BSConnection entityConnection) {
        if (connectionStatus != BSConnectionStatus.Init) {
            return;
        }
        this.conectionListener = new BSConnectionListener() {

            @Override
            public void finished(final byte[] response) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (connectionStatus == BSConnectionStatus.Running) {
                            connectionStatus = BSConnectionStatus.Finished;
                            listener.finished(response);
                        }
                    }
                });
            }

            @Override
            public void failed(final Exception exception) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (connectionStatus == BSConnectionStatus.Running) {
                            connectionStatus = BSConnectionStatus.Failed;
                            listener.failed(exception);
                        }
                    }
                });
            }
        };
        entityConnection.run(this);
    }

    private void notifyProgress(final int downloadedBytes, final int totalBytes) {
        if (progressListener != null) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    progressListener.progress(downloadedBytes, totalBytes);
                }
            });
        }
    }

    private void run(BSConnection equalConnection) {
        equalConnections.add(equalConnection);
        if (equalConnections.size() == 1) {
            new Thread() {
                @Override
                public void run() {
                    String urlString = url;
                    if (requestMethod == BSConnectionMethod.GET && parameters != null) {
                        StringBuffer param = new StringBuffer();
                        int i = 0;
                        for (String key : parameters.keySet()) {
                            if (i == 0) {
                                param.append("?");
                            } else {
                                param.append("&");
                            }
                            param.append(key).append("=").append(parameters.get(key));
                            i++;
                        }
                        urlString += param;
                    }

                    HttpURLConnection urlConnection = null;
                    try {
                        URL url = new URL(urlString);
                        urlConnection = (HttpURLConnection) url.openConnection();

                        urlConnection.setRequestMethod(requestMethod.toString());
                        urlConnection.setDoOutput(true);
                        urlConnection.setDoInput(true);
                        urlConnection.setUseCaches(false);

                        if (properties != null) {
                            for (String key : properties.keySet()) {
                                urlConnection.addRequestProperty(key, properties.get(key));
                            }
                        }

                        if (requestMethod == BSConnectionMethod.POST && parameters != null) {
                            StringBuffer param = new StringBuffer();
                            for (String key : parameters.keySet()) {
                                param.append("&");
                                param.append(key).append("=").append(parameters.get(key));
                            }
                            urlConnection.getOutputStream().write(param.toString().getBytes());
                            urlConnection.getOutputStream().flush();
                            urlConnection.getOutputStream().close();
                        }

                        final ByteArrayOutputStream os = new ByteArrayOutputStream();
                        BufferedInputStream bis = new BufferedInputStream(urlConnection.getInputStream());
                        BufferedOutputStream bos = new BufferedOutputStream(os);
                        byte[] buffer = new byte[1024 * 16]; //创建存放输入流的缓冲 

                        int totalBytes = 0;
                        try {
                            totalBytes = Integer.parseInt(urlConnection.getHeaderField("Content-Length"));
                        } catch (Exception e) {
                        }
                        int readBytes = 0;
                        for (BSConnection connection : equalConnections) {
                            connection.notifyProgress(readBytes, totalBytes);
                        }
                        int num = -1; //读入的字节数 
                        while (true) {
                            boolean bAllCanceled = true;
                            for (BSConnection connection : equalConnections) {
                                if (connection.connectionStatus == BSConnectionStatus.Running) {
                                    bAllCanceled = false;
                                    break;
                                }
                            }
                            if (bAllCanceled) {
                                break;
                            }
                            num = bis.read(buffer); // 读入到缓冲区
                            if (num == -1) {
                                bos.flush();
                                break; //已经读完 
                            }
                            bos.flush();
                            bos.write(buffer, 0, num);
                            readBytes += num;
                            for (BSConnection connection : equalConnections) {
                                connection.notifyProgress(readBytes, totalBytes);
                            }
                        }
                        bos.close();
                        bis.close();
                        for (BSConnection connection : equalConnections) {
                            if (connection.conectionListener != null) {
                                connection.conectionListener.finished(os.toByteArray());
                            }
                        }
                    } catch (final Exception e) {
                        for (BSConnection connection : equalConnections) {
                            if (connection.conectionListener != null) {
                                connection.conectionListener.failed(e);
                            }
                        }
                    } finally {
                        if (urlConnection != null) {
                            urlConnection.disconnect();
                        }
                    }

                    if (connectionQueue != null) {
                        connectionQueue.runNext(BSConnection.this);
                    }
                }
            }.start();
        }
    }

    public void cancel() {
        if (connectionStatus == BSConnectionStatus.Running) {
            connectionStatus = BSConnectionStatus.Canceled;
        }
    }
}
