package com.bstoneinfo.lib.net;

import java.util.LinkedList;

import android.text.TextUtils;

import com.bstoneinfo.lib.net.BSConnection.BSConnectionListener;

public class BSConnectionQueue {

    private final LinkedList<BSConnection> runningConnections = new LinkedList<BSConnection>();
    private final LinkedList<BSConnection> waitingConnections = new LinkedList<BSConnection>();
    private final LinkedList<BSConnectionListener> waitingListeners = new LinkedList<BSConnectionListener>();
    private final int queueSize;

    public BSConnectionQueue(int size) {
        queueSize = size;
    }

    public boolean containsConnection(String url) {
        for (BSConnection runningConnection : runningConnections) {
            if (TextUtils.equals(runningConnection.url, url)) {
                return true;
            }
        }
        for (BSConnection waitingConnection : waitingConnections) {
            if (TextUtils.equals(waitingConnection.url, url)) {
                return true;
            }
        }
        return false;
    }

    public boolean containsConnection(BSConnection connection) {
        for (BSConnection runningConnection : runningConnections) {
            if (runningConnection.equals(connection)) {
                return true;
            }
        }
        for (BSConnection waitingConnection : waitingConnections) {
            if (waitingConnection.equals(connection)) {
                return true;
            }
        }
        return false;
    }

    public void clear() {
        for (BSConnection connection : runningConnections) {
            connection.cancel();
        }
        runningConnections.clear();
        waitingConnections.clear();
    }

    void add(BSConnection connection, BSConnectionListener listener) {
        synchronized (runningConnections) {
            for (BSConnection runningConnection : runningConnections) {
                if (runningConnection.equals(connection)) {
                    connection.start(listener, runningConnection);
                    return;
                }
            }
            if (runningConnections.size() < queueSize) {
                runningConnections.add(connection);
                connection.start(listener, connection);
            } else {
                waitingConnections.add(connection);
                waitingListeners.add(listener);
            }
        }
    }

    void runNext(BSConnection justFinished) {
        synchronized (runningConnections) {
            runningConnections.remove(justFinished);
        }
        if (!waitingConnections.isEmpty()) {
            add(waitingConnections.removeFirst(), waitingListeners.removeFirst());
        }
    }

}
