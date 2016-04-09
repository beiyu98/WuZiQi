package com.android.bruce.wuziqi;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by shuai on 2016/4/9.
 */
public class WuZiQiPanel extends View {

    //棋盘的宽度
    private int mPanelWidth;
    //棋盘每小格的边长
    private float mLineHeight;
    //棋盘的最大行数（注：棋盘式正方形）
    // TODO: 2016/4/9 做一个对外暴露的方法，用户自定义棋盘的大小
    private int MAX_LINE = 10;

    private Paint mPaint = new Paint();

    private Bitmap mWhitePiece,mBlackPiece;
    private float mScaleLineHeight = 3*1.0f/4;

    private ArrayList<Point> mWhiteArray = new ArrayList<>();
    private ArrayList<Point> mBlackArray = new ArrayList<>();

    // TODO: 2016/4/9 这样每次第一次打开应用都是白棋开局，再来一局都是黑棋开局
    private boolean mIsWhite = true;

    private boolean mIsGameOVer;
    private boolean mIsWhiteWinner;

    // TODO: 2016/4/9 做一个对外暴露的方法，用户自定义棋的种类
    public static final int MAX_COUNT_PIECES = 5;

    //重新开始一局
    public void restart(){
        mWhiteArray.clear();
        mBlackArray.clear();
        mIsGameOVer = false;

        //这样的话，每次开始都是黑棋先手
        // TODO: 2016/4/9 需要修改呀
        mIsWhite = false;

        invalidate();
    }

    public WuZiQiPanel(Context context, AttributeSet attrs) {
        super(context, attrs);

        //辅助进行view的制作，可以看到view的边界
//        setBackgroundColor(0x44ff0000);

        init();
    }

    private void init() {
        //半透明灰色
        mPaint.setColor(0x88000000);
        //抗锯齿
        mPaint.setAntiAlias(true);
        //开启抖动效果，使显示效果更好
        mPaint.setDither(true);
        //描边，因为是画线
        mPaint.setStyle(Paint.Style.STROKE);

        mWhitePiece = BitmapFactory.decodeResource(getResources(),R.drawable.stone_w2);
        mBlackPiece = BitmapFactory.decodeResource(getResources(),R.drawable.stone_b1);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int widthSize= MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);

        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        int width = Math.min(widthSize,heightSize);

        if (widthMode == MeasureSpec.UNSPECIFIED){
            width = heightSize;
        }else if (heightMode == MeasureSpec.UNSPECIFIED){
            width = widthSize;
        }

        setMeasuredDimension(width,width);
    }

    /**
     * 在宽高确定后，再次发生改变，进行回调
     * @param w
     * @param h
     * @param oldw
     * @param oldh
     */
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        //获得棋盘的宽度和每小格的边长
        mPanelWidth = w;
        mLineHeight = mPanelWidth*1.0f/MAX_LINE;

        //初始化棋子的大小
        int pieceWidth = (int) (mLineHeight*mScaleLineHeight);
        mWhitePiece = Bitmap.createScaledBitmap(mWhitePiece,pieceWidth,pieceWidth,false);
        mBlackPiece = Bitmap.createScaledBitmap(mBlackPiece,pieceWidth,pieceWidth,false);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        drawPanel(canvas);
        drawPieces(canvas);

        checkGameOver();
    }

    private void checkGameOver() {
        boolean whiteWin = checkFiveInLine(mWhiteArray);
        boolean blackWin = checkFiveInLine(mBlackArray);

        if (whiteWin || blackWin){
            mIsGameOVer = true;
            mIsWhiteWinner = whiteWin;
            String text = mIsWhiteWinner ? "白棋胜利！":"黑棋胜利！";
            Toast.makeText(getContext(),text,Toast.LENGTH_SHORT).show();
        }
    }

    private boolean checkFiveInLine(List<Point> points) {

        for (Point point : points){
            int x = point.x;
            int y = point.y;

            boolean win = checkHorizontal(x,y,points);
            if (win == true) return true;

            win = checkVertical(x,y,points);
            if (win == true) return  true;

            win = checkLeftDiagonal(x,y,points);
            if (win == true) return true;

            win = checkRightDiagonal(x,y,points);
            if (win == true) return true;
        }

        return false;
    }

    private boolean checkHorizontal(int x, int y, List<Point> points) {
        int count  = 1;

        //左边
        for (int i = 1; i < MAX_COUNT_PIECES; i++) {
            if (points.contains(new Point(x-i,y))){
                count++;
            }else {
                break;
            }
        }

        if (count == MAX_COUNT_PIECES) return true;

        for (int i = 1; i < MAX_COUNT_PIECES; i++) {
            if (points.contains(new Point(x+i,y))){
                count++;
            }else {
                break;
            }
        }

        if (count == MAX_COUNT_PIECES) return true;

        return false;
    }

    private boolean checkVertical(int x, int y, List<Point> points) {
        int count  = 1;

        for (int i = 1; i < MAX_COUNT_PIECES; i++) {
            if (points.contains(new Point(x,y+i))){
                count++;
            }else {
                break;
            }
        }

        if (count == MAX_COUNT_PIECES) return true;

        for (int i = 1; i < MAX_COUNT_PIECES; i++) {
            if (points.contains(new Point(x,y-i))){
                count++;
            }else {
                break;
            }
        }

        if (count == MAX_COUNT_PIECES) return true;

        return false;
    }

    private boolean checkLeftDiagonal(int x, int y, List<Point> points) {
        int count  = 1;

        for (int i = 1; i < MAX_COUNT_PIECES; i++) {
            if (points.contains(new Point(x-i,y-i))){
                count++;
            }else {
                break;
            }
        }

        if (count == MAX_COUNT_PIECES) return true;

        for (int i = 1; i < MAX_COUNT_PIECES; i++) {
            if (points.contains(new Point(x+i,y+i))){
                count++;
            }else {
                break;
            }
        }

        if (count == MAX_COUNT_PIECES) return true;

        return false;
    }

    private boolean checkRightDiagonal(int x, int y, List<Point> points) {
        int count  = 1;

        for (int i = 1; i < MAX_COUNT_PIECES; i++) {
            if (points.contains(new Point(x+i,y-i))){
                count++;
            }else {
                break;
            }
        }

        if (count == MAX_COUNT_PIECES) return true;

        for (int i = 1; i < MAX_COUNT_PIECES; i++) {
            if (points.contains(new Point(x-i,y+i))){
                count++;
            }else {
                break;
            }
        }

        if (count == MAX_COUNT_PIECES) return true;

        return false;
    }



    private void drawPieces(Canvas canvas) {
        for (int i = 0,n = mWhiteArray.size(); i < n; i++) {
            Point whitePoint = mWhiteArray.get(i);
            canvas.drawBitmap(mWhitePiece,
                    (whitePoint.x+(1-mScaleLineHeight)/2)*mLineHeight,
                    (whitePoint.y+(1-mScaleLineHeight)/2)*mLineHeight,null);
        }

        for (int i = 0,n = mBlackArray.size(); i < n; i++) {
            Point blackPoint = mBlackArray.get(i);
            canvas.drawBitmap(mBlackPiece,
                    (blackPoint.x+(1-mScaleLineHeight)/2)*mLineHeight,
                    (blackPoint.y+(1-mScaleLineHeight)/2)*mLineHeight,null);
        }
    }

    private void drawPanel(Canvas canvas) {

        int w = mPanelWidth;
        float lineHeight = mLineHeight;

        for (int i = 0; i < MAX_LINE; i++) {
            int startX = (int) (lineHeight/2);
            int endX = (int) (w - lineHeight/2);
            int y = (int) ((0.5+i)*lineHeight);

            //画横线
            canvas.drawLine(startX,y,endX,y,mPaint);
            //因为棋盘是正方形，因此画竖线时坐标刚好相反
            canvas.drawLine(y,startX,y,endX,mPaint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        //判断游戏是否结束
        if (mIsGameOVer){
            return false;
        }

        int action = event.getAction();
        if (action == MotionEvent.ACTION_UP){
            int x = (int) event.getX();
            int y = (int) event.getY();
            Point point = getValidLocation(x,y);
            if (mWhiteArray.contains(point) || mBlackArray.contains(point)){
                return false;
            }
            if (mIsWhite){
                mWhiteArray.add(point);
            }else {
                mBlackArray.add(point);
            }

            //重绘
            invalidate();
            mIsWhite = !mIsWhite;
        }
        return true;
    }

    private Point getValidLocation(int x, int y) {
        return new Point((int) (x/mLineHeight),(int)(y/mLineHeight));
    }

    private static final String INSTANCE = "instance";
    private static final String INSTANCE_IS_GAME_OVER = "instance_is_game_over";
    private static final String INSTANCE_WHITE_ARRAY = "instance_white_array";
    private static final String INSTANCE_BLACK_ARRAY = "instance-black_array";
    //保存数据
    @Override
    protected Parcelable onSaveInstanceState() {

        Bundle bundle = new Bundle();
        bundle.putParcelable(INSTANCE,super.onSaveInstanceState());
        bundle.putBoolean(INSTANCE_IS_GAME_OVER,mIsGameOVer);
        bundle.putParcelableArrayList(INSTANCE_WHITE_ARRAY,mWhiteArray);
        bundle.putParcelableArrayList(INSTANCE_BLACK_ARRAY,mBlackArray);
        return bundle;
    }

    //回复数据
    @Override
    protected void onRestoreInstanceState(Parcelable state) {

        //首先判断是否是我们自己存储的数据
        if (state instanceof Bundle){
            Bundle bundle = (Bundle) state;
            mIsGameOVer = bundle.getBoolean(INSTANCE_IS_GAME_OVER);
            mWhiteArray = bundle.getParcelableArrayList(INSTANCE_WHITE_ARRAY);
            mBlackArray = bundle.getParcelableArrayList(INSTANCE_BLACK_ARRAY);

            super.onRestoreInstanceState(bundle.getParcelable(INSTANCE));
            return;
        }

        super.onRestoreInstanceState(state);
    }
}
