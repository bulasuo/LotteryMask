package com.abu.xbase.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.abu.xbase.model.RWSocket;
import com.abu.xbase.util.ToastUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author abu
 *         2018/2/26    10:59
 *         ..
 */

public class SurfaceViewL extends SurfaceView implements SurfaceHolder.Callback, Runnable {
    /**
     * SurfaceHolder
     */
    private SurfaceHolder mSurfaceHolder;
    /**
     * 画布
     */
    private Canvas mCanvas;
    /**
     * 子线程标志位
     */
    private boolean tryDestroy;

    public static class PathPaintPar {
        public PathPaintPar(XPath path, Paint paint) {
            this.path = path;
            this.paint = paint;
        }

        public XPath path;
        public Paint paint;
    }

    private Map<String, PathPaintPar> mPaths = Collections.synchronizedMap(new LinkedHashMap<String, PathPaintPar>());

    /**
     * 画笔
     */
    private Paint mLocalPaint, mLastRemotePaint, mClearPaint, mClearEndPaint;
    private String mLastRemotePaintColor;
    private XPath mClearEndPath;

    /**
     * 本地绘画路径
     */
    private XPath mLocalPath, mLastRemotePath;

    private boolean shouldDraw;

    public void tryDraw() {
        shouldDraw = true;
    }

    private long lastDrawTime;

    /**
     * 上次的坐标
     */
    private float mLastX, mLastY, mLastRemoteX, mLastRemoteY;
    /**
     * 允许本地触摸绘图
     */
    private boolean enableLocalDraw;
    /**
     * 允许远程流绘图
     */
    private boolean enableRemoteDraw;
    private String studentId, teacherId;
    private int localBoardW, localBoardH;
    private float mClearEndWidth = 30;
    private float mScreen_scale;

    public void setRwSocket(RWSocket rwSocket, String studentId, String teacherId,
                            int localBoardW, int localBoardH, float clearEndWidth, float screen_scale) {
        this.rwSocket = rwSocket;
        this.studentId = studentId;
        this.teacherId = teacherId;
        this.localBoardW = localBoardW;
        this.localBoardH = localBoardH;
        this.mClearEndWidth = clearEndWidth;
        this.mScreen_scale = screen_scale;

        float endHelf = mClearEndWidth / 2;
        mClearEndPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        mClearEndPaint.setAntiAlias(true);
        mClearEndPaint.setStyle(Paint.Style.STROKE);
        mClearEndPaint.setStrokeCap(Paint.Cap.SQUARE);
        mClearEndPaint.setStrokeJoin(Paint.Join.BEVEL);
        mClearEndPaint.setStrokeWidth(mClearEndWidth + 1);
        mClearEndPaint.setDither(true);//消除拉动，使画面圓滑
        mClearEndPaint.setAlpha(0);
        mClearEndPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        mClearEndPath = new XPath();
        mClearEndPath.moveTo(localBoardW - endHelf, 0);
        mClearEndPath.quadTo(localBoardW - endHelf, 0, localBoardW - endHelf, localBoardH);

        mLocalPaint.setStrokeWidth(1f * mScreen_scale);
        mClearPaint.setStrokeWidth(20f * mScreen_scale);
    }

    private RWSocket rwSocket;


    public SurfaceViewL(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    /**
     * 初始化
     */
    private void init() {
        //初始化 SurfaceHolder mSurfaceHolder
        mSurfaceHolder = getHolder();
        mSurfaceHolder.addCallback(this);

        this.setZOrderOnTop(true);
//        this.setZOrderMediaOverlay(true);
        mSurfaceHolder.setFormat(PixelFormat.TRANSLUCENT);
//        mSurfaceHolder.setFormat(PixelFormat.RGBA_8888);
//        mSurfaceHolder.setFormat(PixelFormat.OPAQUE);

        setFocusable(true);
        setFocusableInTouchMode(true);
        /**
         * 保持屏幕长亮
         */
        this.setKeepScreenOn(true);

        //画笔
        mLocalPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        mLocalPaint.setStrokeWidth(1f);
        mLocalPaint.setColor(Color.parseColor("#FF000000"));
        mLocalPaint.setStyle(Paint.Style.STROKE);
        mLocalPaint.setStrokeJoin(Paint.Join.ROUND);
        mLocalPaint.setStrokeCap(Paint.Cap.ROUND);

//        mClearPaint = new Paint();
        mClearPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        mClearPaint.setAntiAlias(true);
        mClearPaint.setStyle(Paint.Style.STROKE);
        mClearPaint.setStrokeCap(Paint.Cap.SQUARE);
        mClearPaint.setStrokeJoin(Paint.Join.BEVEL);
        mClearPaint.setStrokeWidth(20f);
        mClearPaint.setDither(true);//消除拉动，使画面圓滑
        mClearPaint.setAlpha(0);
        mClearPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));


        //路径
        mLocalPath = new XPath();
        mLastRemotePath = new XPath();
//        mPaths.add(new PathPaintPar(mLocalPath, mLocalPaint));
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {//创建
        tryDestroy = false;
        ToastUtil.showDebug("surfaceCreated-destroy-" + tryDestroy);
        //绘制线程
        new Thread(this).start();
    }


    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {//改变

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {//销毁
        tryDestroy = true;
        ToastUtil.showDebug("surfaceDestroyed-destroy-" + tryDestroy);
    }

    @Override
    public void run() {
        /*if(true){

            try {
                mCanvas = mSurfaceHolder.lockCanvas();
                XPath mLocalBufPath = new XPath();
                Paint mPaint = new Paint();
                mPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
                mPaint.setAntiAlias(true);
                mPaint.setStyle(Paint.Style.STROKE);
                mPaint.setStrokeWidth(30);
                mPaint.setStrokeCap(Paint.Cap.ROUND);  //圆头
                mPaint.setDither(true);//消除拉动，使画面圓滑
                mPaint.setStrokeJoin(Paint.Join.ROUND); //结合方式，平滑
                mPaint.setAlpha(0); ////
                mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
                mLocalBufPath = new XPath();
                mLocalBufPath.moveTo(100, 0);
                mLocalBufPath.quadTo(100, 0, 0, 100);
                mCanvas.drawPath(mLocalBufPath, mPaint);

                 mLocalBufPath = new XPath();
                mLocalBufPath.moveTo(0, 0);
                mLocalBufPath.quadTo(0, 0, 100, 100);
                mCanvas.drawPath(mLocalBufPath, mLocalPaint);
//                mLocalPaint.setColor(Color.parseColor("#00000000"));

                 mLocalBufPath = new XPath();
                mPaint = new Paint();
                mPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
                mPaint.setAntiAlias(true);
                mPaint.setStyle(Paint.Style.STROKE);
                mPaint.setStrokeWidth(30);
                mPaint.setStrokeCap(Paint.Cap.ROUND);  //圆头
                mPaint.setDither(true);//消除拉动，使画面圓滑
                mPaint.setStrokeJoin(Paint.Join.ROUND); //结合方式，平滑
                mPaint.setAlpha(0); ////
                mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
                mLocalBufPath = new XPath();
                mLocalBufPath.moveTo(100, 0);
                mLocalBufPath.quadTo(100, 0, 0, 100);
                mCanvas.drawPath(mLocalBufPath, mPaint);


                mLocalBufPath = null;
                lastDrawTime = System.currentTimeMillis();
            } catch (Exception e){
                e.printStackTrace();
            } finally {
                if (mCanvas != null) {
                    mSurfaceHolder.unlockCanvasAndPost(mCanvas);
                }
            }
            return;
        }*/


        try {
            lastDrawTime = System.currentTimeMillis();
            while (!tryDestroy) {
                if (!drawmPaths()) {
                    //绘图等待 代码空间
                    long delay = System.currentTimeMillis() - lastDrawTime;
                    if (delay > 6000)
                        Thread.sleep(200);
                    else if (delay > 100)
                        Thread.sleep(60);
                }

            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 不是新颜色 且 上次paint 不为null   返回 null
     *
     * @param color
     * @return
     */
    private Paint formNewRemotePaint(String color, int traceMode) {
        if (mLastRemotePaint == null
                || traceMode != lastTraceMode
                || !TextUtils.equals(mLastRemotePaintColor, color)) {
            mLastRemotePaintColor = color;
            lastTraceMode = traceMode;
            if (traceMode == 0) {//绘制path
                Paint remotePaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
                remotePaint.setStrokeWidth(1f * mScreen_scale);
                remotePaint.setColor(Color.parseColor("#".concat(color.substring(6, 8)).concat(color.substring(0, 6))));
                remotePaint.setStyle(Paint.Style.STROKE);
                remotePaint.setStrokeJoin(Paint.Join.ROUND);
                remotePaint.setStrokeCap(Paint.Cap.ROUND);
                mLastRemotePaint = remotePaint;
            } else if (traceMode == 1) {//橡皮擦
                mLastRemotePaint = mClearPaint;
            }
            return mLastRemotePaint;
        }
        return null;
    }


    private String lastTraceId;//上一次笔记id
    private int lastTraceMode = -1;//0 - 绘制模式, 1 - 橡皮擦模式

    public ArrayList<JSONObject> onHistoryPaths(String jsonStr, int localBoardW, int localBoardH) {
        if (TextUtils.isEmpty(jsonStr)) {
            return null;
        }

        long bulasuoTime1 = System.currentTimeMillis();

        ArrayList<JSONObject> testPictures = null;
        try {
            JSONObject jsonObject = JSONObject.parseObject(jsonStr);
            JSONArray jsonArray = jsonObject.getJSONArray("messages");
            if (jsonArray != null && jsonArray.size() > 0) {
                JSONArray points;
                JSONObject content;
                JSONObject msg;
                JSONObject point;
                Paint paint;
                XPath path;
                String traceId;
                int traceMode;
                float x;
                float y;
                PathPaintPar pathPaintPar;
                for (int i = 0, j = jsonArray.size(); i < j; i++) {
                    msg = JSONObject.parseObject((String) jsonArray.get(i));
                    String msgType = msg.getString("msgType");
                    if ("5".equals(msgType)) {//试题类消息
                        if (testPictures == null)
                            testPictures = new ArrayList<>();
                        testPictures.add(msg);
                        continue;
                    } else if ("3".equals(msgType)) {//笔记类消息
                        content = JSONObject.parseObject(msg.getString("content"));
//                        if(!TextUtils.equals(content.getString("operatorId"), teacherId)
//                                && !TextUtils.equals(content.getString("operatorId"), studentId))
//                            continue;
                        traceId = content.getString("traceId");
                        pathPaintPar = mPaths.get(traceId);
                        if (pathPaintPar != null) {
//                            paint = pathPaintPar.paint;
                            path = pathPaintPar.path;
                        } else {
                            traceMode = content.getIntValue("traceMode");
                            formNewRemotePaint(content.getString("color"), traceMode);
                            path = new XPath();
                            mLastRemotePath = path;
                            mPaths.put(traceId, new PathPaintPar(path, mLastRemotePaint));
                        }
                        points = content.getJSONArray("points");
                        if (points != null && points.size() > 0) {
                            for (int ii = 0, jj = points.size(); ii < jj; ii++) {
                                point = points.getJSONObject(ii);
                                x = point.getFloatValue("x") * localBoardW;
                                y = point.getFloatValue("y") * localBoardH;
                                if (pathPaintPar != null) {
                                    path.quadTo(path.mLastX, path.mLastY, x, y);
                                } else {
                                    path.moveTo(x, y);
                                }

                                mLastRemoteX = x;
                                mLastRemoteY = y;

//                                ToastUtil.showDebug("mLastRemoteXY-" + mLastRemoteX + "-" + mLastRemoteY);
                            }
                        }
                        lastTraceId = traceId;
                        if (i % 300 == 0)
                            shouldDraw = true;
                    }
                }
                shouldDraw = true;
            }
        } catch (Exception e) {
            ToastUtil.showException(e);
        }

        ToastUtil.showDebug("onHistoryPaths*********-" + (System.currentTimeMillis() - bulasuoTime1));

        return testPictures;
    }

    public void onPath(String jsonStr, int localBoardW, int localBoardH) {
        JSONArray points;
        JSONObject content;
        JSONObject point;
        Paint paint;
        XPath path;
        String traceId;
        PathPaintPar pathPaintPar;
        int traceMode;
        float x;
        float y;
        content = JSONObject.parseObject(jsonStr);
        if (TextUtils.equals(content.getString("operatorId"), studentId))
            return;
        traceId = content.getString("traceId");

        pathPaintPar = mPaths.get(traceId);
        if (pathPaintPar != null) {
//                            paint = pathPaintPar.paint;
            path = pathPaintPar.path;
        } else {
            traceMode = content.getIntValue("traceMode");
            formNewRemotePaint(content.getString("color"), traceMode);
            path = new XPath();
            mLastRemotePath = path;
            mPaths.put(traceId, new PathPaintPar(path, mLastRemotePaint));
        }
        points = content.getJSONArray("points");
        if (points != null && points.size() > 0) {
            for (int ii = 0, jj = points.size(); ii < jj; ii++) {
                point = points.getJSONObject(ii);
                x = point.getFloatValue("x") * localBoardW;
                y = point.getFloatValue("y") * localBoardH;
                if (pathPaintPar != null) {
                    path.quadTo(path.mLastX, path.mLastY, x, y);
                } else {
                    path.moveTo(x, y);
                }

                mLastRemoteX = x;
                mLastRemoteY = y;

//                                ToastUtil.showDebug("mLastRemoteXY-" + mLastRemoteX + "-" + mLastRemoteY);
            }
        }
        lastTraceId = traceId;
        shouldDraw = true;
    }

    public void onClearPaths() {
        shouldClearScreen = true;
        shouldDraw = true;
    }


    private boolean shouldClearScreen;

    /**
     * 绘制
     */
    private boolean drawmPaths() {
        if (!shouldDraw)
            return false;
        try {
            mCanvas = mSurfaceHolder.lockCanvas();
            mCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
//            mCanvas.drawColor(0xFFF5F5F5);

            if (shouldClearScreen) {
                mPaths.clear();
                mLastRemotePaintColor = null;
                shouldClearScreen = false;
            } else {
                Iterator<PathPaintPar> it = mPaths.values().iterator();
                PathPaintPar pathPaintPar;
                while (it.hasNext()) {
                    pathPaintPar = it.next();
                    mCanvas.drawPath(pathPaintPar.path, pathPaintPar.paint);
                }
                if (mClearEnd) {
                    mCanvas.drawPath(mClearEndPath, mClearEndPaint);
                }
            }
            shouldDraw = false;
            lastDrawTime = System.currentTimeMillis();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (mCanvas != null) {
                mSurfaceHolder.unlockCanvasAndPost(mCanvas);
            }
        }
        return true;
    }

    private boolean mClearEnd;

    public void onClearEnd(boolean clearEnd) {
        if (mClearEnd != clearEnd) {
            mClearEnd = clearEnd;
            shouldDraw = true;
        }
    }

    private String localTraceId;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!enableLocalDraw)
            return true;

        float x = event.getX();
        float y = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mLastX = x;
                mLastY = y;
                mLocalPath = new XPath();
                localTraceId = String.valueOf(System.currentTimeMillis());
                mPaths.put(localTraceId, new PathPaintPar(mLocalPath, mLocalPaint));
                mLocalPath.moveTo(mLastX, mLastY);
                sendReomtePoint(true, localTraceId, x / localBoardW, y / localBoardH);
                break;
            case MotionEvent.ACTION_MOVE:
                float dx = Math.abs(x - mLastX);
                float dy = Math.abs(y - mLastY);
                if (dx >= 3 || dy >= 3) {
                    mLocalPath.quadTo(mLastX, mLastY, x, y);
                    shouldDraw = true;
                    mLastX = x;
                    mLastY = y;
                    sendReomtePoint(false, localTraceId, x / localBoardW, y / localBoardH);
                }
                break;
            case MotionEvent.ACTION_UP:
                break;
            default:
                break;
        }
        return true;
    }

    private void sendReomtePoint(boolean isStartPoint, String traceId, float x, float y) {
        if (rwSocket != null) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("messageId", UUID.randomUUID());
            jsonObject.put("operatorId", studentId);
            jsonObject.put("operatorType", 2);
            jsonObject.put("teacherId", teacherId);
            jsonObject.put("isStartPoint", isStartPoint);
            jsonObject.put("color", "000000FF");
            jsonObject.put("traceId", traceId);
            jsonObject.put("traceMode", 0);
            JSONArray jsonArray = new JSONArray();
            JSONObject point = new JSONObject();
            point.put("x", x);
            point.put("y", y);
            jsonArray.add(point);
            jsonObject.put("points", jsonArray);

            rwSocket.sendMsg(3, jsonObject.toJSONString());

        }
    }

    /**
     * 测量
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        /*int wSpecMode = MeasureSpec.getMode(widthMeasureSpec);
        int wSpecSize = MeasureSpec.getSize(widthMeasureSpec);
        int hSpecMode = MeasureSpec.getMode(heightMeasureSpec);
        int hSpecSize = MeasureSpec.getSize(heightMeasureSpec);

        if (wSpecMode == MeasureSpec.AT_MOST && hSpecMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(300, 300);
        } else if (wSpecMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(300, hSpecSize);
        } else if (hSpecMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(wSpecSize, 300);
        }*/
    }

    public boolean isEnableLocalDraw() {
        return enableLocalDraw;
    }

    public void enableLocalDraw(boolean enableLocalDraw) {
        this.enableLocalDraw = enableLocalDraw;
    }

    public boolean isEnableRemoteDraw() {
        return enableRemoteDraw;
    }

    public void enableRemoteDraw(boolean enableRemoteDraw) {
        this.enableRemoteDraw = enableRemoteDraw;
    }
}
