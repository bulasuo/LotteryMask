package com.abu.xbase.model;

import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.UiThread;

import com.abu.xbase.Task.Task;
import com.abu.xbase.util.ToastUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * https://www.cnblogs.com/jerrychoi/archive/2010/04/15/1712931.html
 *
 * @author abu
 *         2018/2/13    09:27
 *         bulasuo@foxmail.com
 */

public class RWSocket {

    private static final ThreadFactory RWSocketThreadFactory = new ThreadFactory() {
        private final AtomicInteger mCount = new AtomicInteger(1);

        @Override
        public Thread newThread(Runnable r) {
            return new Thread(r, "RWSocketThreadPool #" + mCount.getAndIncrement());
        }
    };

    private static ExecutorService mThreadPool = new ThreadPoolExecutor(100, Integer.MAX_VALUE,
            1000L, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<>(), RWSocketThreadFactory);

    private String mHost;
    private int mPort;
    private Handler mHandler;
    private Socket mSocket;
    private OutputStream out;
    private boolean mDestroy;
    private Task mOnDestroyTask;
    private ReaderThread mReaderThread;
    private final Object destroyLock = new Object();
    private final Object socketLock = new Object();
    private String student_id, teacher_id;

    /**
     * @param host
     * @param port
     * @param handler
     * @param destroyTask 链路连接错误等导致的释放thread destroy,调度者需要合理处理
     */
    public RWSocket(@NonNull String host, int port,
                    @NonNull Handler handler,
                    @NonNull Task destroyTask,
                    String studentId, String teacherId) {
        ToastUtil.showDebug("" + studentId + "-" + teacherId);
        student_id = studentId;
        teacher_id = teacherId;
        mHost = host;
        mPort = port;
        mHandler = handler;
        mOnDestroyTask = destroyTask;
        ToastUtil.showDebug("init-SocketReadThread");
    }

    @UiThread
    public void start() {
        mDestroy = false;
        tryCloseRes();
        mThreadPool.execute(() -> {
            try {
//                Thread.sleep(200);
                getNewSocket();
                mReaderThread = new ReaderThread();
                out = mSocket.getOutputStream();
                mReaderThread.start();
//                Thread.sleep(200);
                connectionSocket(student_id, teacher_id);
//                Thread.sleep(100);
//                getHistorySocket(teacher_id);

            } catch (Exception e) {
                ToastUtil.showDebug("socket初始化失败");
                e.printStackTrace();
                onDestroy_destroyLock();
            }
        });

    }


    private void onDestroy_destroyLock() {
        synchronized (destroyLock) {
            ToastUtil.showDebug("onDestroy_destroyLock");
            if (!mDestroy) {
                mDestroy = true;
                if (mOnDestroyTask != null && mHandler != null)
                    mHandler.post(() -> {
                        try {
                            if (mOnDestroyTask != null) {
                                mOnDestroyTask.apply();
                                mOnDestroyTask = null;
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });
                mHandler = null;
            }
            tryCloseRes();
        }
    }

    @UiThread
    public void tryDestroy_destroyLock() {
        synchronized (destroyLock) {
            ToastUtil.showDebug("tryDestroy_destroyLock");
            mDestroy = true;
            mHandler = null;
            tryCloseRes();
        }
    }

    private void tryCloseRes() {
        try {
            if (mReaderThread != null) {
                mReaderThread.tryDestroyThis = true;
                mReaderThread = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (mSocket != null) {
            mThreadPool.execute(() -> {
                try {
                    if (mSocket != null) {
                        mSocket.shutdownInput();
                        mSocket.shutdownOutput();
                        mSocket.close();
                        mSocket = null;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
    }

    private Socket getNewSocket() throws Exception {
        synchronized (socketLock) {
            mSocket = new Socket(mHost, mPort);
            mSocket.setSoTimeout(60000);
            mSocket.setTcpNoDelay(true);
            return mSocket;
        }
    }

    //获取历史记录包
    private void getHistorySocket(String teacher_id) {
        JSONObject obj = new JSONObject();
        try {
            obj.put("messageId", 11);
            obj.put("teacherId", teacher_id);
            String jsonStr = obj.toString();
            sendMsg(11, jsonStr);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //发送建立连接包
    private void connectionSocket(String student_id, String teacher_id) {

        JSONObject msgBody = new JSONObject();
        try {
            msgBody.put("userId", student_id);
            msgBody.put("userType", "2");
            msgBody.put("teacherId", teacher_id);
            String jsonStr = msgBody.toString();
            sendMsg(1, jsonStr);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private final Object outLock = new Object();

    public void sendMsg(final int type, final String jsonStr) {
        mThreadPool.execute(() -> {
            synchronized (outLock) {
                try {
                    ToastUtil.showDebug("发送-start--::" + type + "-" + jsonStr);
//                OutputStream out = mSocket.getOutputStream();
                    byte[] bytes = jsonStr.getBytes("utf-8");
                    out.write((byte) type);
                    out.write(int2Byte(bytes.length));
                    out.write(longToByteArray(System.currentTimeMillis()));
                    out.write(bytes);
                    out.flush();
                    ToastUtil.showDebug("发送-end::" + type + "-" + jsonStr);
                } catch (Exception e) {
                    ToastUtil.showDebug("发送失败::" + type + "-" + jsonStr);
                    e.printStackTrace();
                }
            }
        });

    }

    private static byte[] longToByteArray(long s) {
        byte[] targets = new byte[8];
        for (int i = 0; i < 8; i++) {
            int offset = (targets.length - 1 - i) * 8;
            targets[i] = (byte) ((s >>> offset) & 0xff);
        }
        return targets;
    }

    private static byte[] readByteArray(InputStream is, int length) throws Exception {
        if (length <= 0) {
            throw new Exception("[TCP]tcp读取长度不应小于0");
        }
        int count = 0;
        byte[] bytes = new byte[length];
        for (; (count += is.read(bytes, count, (bytes.length - count))) < bytes.length; ) ;
        return bytes;
    }

    private static int bytes2Int(byte[] byteNum) {
        int num = 0;
        for (int ix = 0; ix < 4; ++ix) {
            num <<= 8;
            num |= (byteNum[ix] & 0xff);
        }
        return num;
    }


    private static long bytes2Long(byte[] byteNum) {
        long num = 0;
        for (int ix = 0; ix < 8; ++ix) {
            num <<= 8;
            num |= (byteNum[ix] & 0xff);
        }
        return num;
    }

    private static byte[] int2Byte(int intValue) {
        byte[] b = new byte[4];
        for (int i = 0; i < 4; i++) {
            b[i] = (byte) (intValue >> 8 * (3 - i) & 0xFF);
        }
        return b;
    }

    class ReaderThread extends Thread {
        private boolean tryDestroyThis;

        public ReaderThread() {
            super.setName(this.getClass().getName());
        }

        @Override
        public void run() {
            ToastUtil.showDebug("run-reader-thread");
            Socket socket = mSocket;
            InputStream inputStream;
            Message msg;
            byte[] bytes;
            int type;
            int length;
            long timestamp;
            String jsonBody;
            try {
                inputStream = socket.getInputStream();
                ToastUtil.showDebug("000000000-reader-init");
                while (!tryDestroyThis && !socket.isClosed() && socket.isConnected()) {
                    ToastUtil.showDebug(":222-reader");

                    bytes = readByteArray(inputStream, 1);
                    type = bytes[0] & 0xff;
                    ToastUtil.showDebug(":type:" + type);

                    bytes = readByteArray(inputStream, 4);
                    length = bytes2Int(bytes);
                    ToastUtil.showDebug(":length:" + length);

                    bytes = readByteArray(inputStream, 8);
                    timestamp = bytes2Long(bytes);
                    ToastUtil.showDebug(":timestamp:" + timestamp);

                    if (length > 0) {
                        bytes = readByteArray(inputStream, length);
                        jsonBody = new String(bytes, "utf-8");
                    } else {
                        jsonBody = null;
                    }
                    ToastUtil.showDebug(":jsonBody:" + jsonBody);

                    if (mHandler != null) {
                        msg = mHandler.obtainMessage();
                        msg.what = type;
                        msg.obj = jsonBody;
                        mHandler.handleMessage(msg);
                    }
                    if (type == 1) {
                        ToastUtil.showDebug("建立连接成功*************************");
                        getHistorySocket(teacher_id);
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                ToastUtil.showDebug("finally-close-res");
                onDestroy_destroyLock();
            }
        }
    }

}
