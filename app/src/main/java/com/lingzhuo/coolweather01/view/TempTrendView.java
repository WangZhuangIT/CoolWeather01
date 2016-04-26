package com.lingzhuo.coolweather01.view;

import android.content.Context;
import android.graphics.*;
import android.util.AttributeSet;
import android.util.Log;
import android.view.*;

import com.lingzhuo.coolweather01.R;

import java.util.ArrayList;

/**
 * 我的自定义的温度的折线图控件
 * Created by Wang on 2016/4/22.
 */
public class TempTrendView extends View {

    public TempTrendView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TempTrendView(Context context) {
        super(context);
        init();
    }

    Paint paint;
    private int screenWidth;
    private int viewHeight;
    private int everyWidth;
    private ArrayList<Integer> highTemp;
    private ArrayList<Integer> lowTemp;


    public void setHighTemp(ArrayList<Integer> highTemp) {
        // this.highTemp.clear();
        this.highTemp = highTemp;
        //System.out.println(this.highTemp);
        invalidate();
    }

    public void setLowTemp(ArrayList<Integer> lowTemp) {
        // this.lowTemp.clear();
        this.lowTemp = lowTemp;
        invalidate();
    }

    private void init() {
        paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setStrokeWidth(4);
        paint.setAntiAlias(true);
        paint.setTextSize(40);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        WindowManager wm = (WindowManager) getContext().getSystemService(
                Context.WINDOW_SERVICE);
        screenWidth = wm.getDefaultDisplay().getWidth();
        viewHeight = getLayoutParams().height;
        everyWidth = screenWidth / highTemp.size();
        // System.out.println("screenWidth :" + screenWidth + " viewHeight:"
        // +viewHeight);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (highTemp == null) {
            //System.out.println("highTemp不能为空");
            return;
        }
        //System.out.println(highTemp.size());
        //设置开始绘制的x坐标和y坐标
        float startX = everyWidth / 2;
        float startY = viewHeight / 5*2;
        float detay;
        float todaySY = 0;
        float todayEY = 0;
        int fudu = -10;
        int textPianyi = -40;
        int extra = -5;
        canvas.drawText("近期温度走势图", screenWidth / 2 - 140, 50, paint);
        paint.setColor(Color.rgb(241,162,89));
        for (int i = 0; i < highTemp.size() - 1; i++) {
            //设置画笔的颜色透明度
            paint.setAlpha(0xff);
            //就是等比例的改变不同点的高度显示
            detay = (highTemp.get(i + 1) - highTemp.get(i)) * fudu;
            //折线点的圆的绘制
            canvas.drawCircle(startX, startY, 15, paint);
            //折线的绘制
            canvas.drawLine(startX, startY, startX + everyWidth,
                    startY + detay, paint);
            canvas.drawText(highTemp.get(i) + "℃", startX - 15, startY
                    + textPianyi + extra, paint);
            if (i == 0) {
                todaySY = startY;
            }
            startX = startX + everyWidth;
            startY = startY + detay;
        }
        canvas.drawCircle(startX, startY, 15, paint);
        // paint.setColor(0xffFF3030);
        canvas.drawText(highTemp.get(highTemp.size() - 1) + "℃", startX, startY + textPianyi
                + extra, paint);
        // paint.setColor(Color.WHITE);


        //这是绘制最顶温度的代码
        if (lowTemp == null) {
            //System.out.println("lowTemp不能为空");
            return;
        }
        startX = everyWidth / 2;
        startY = viewHeight / 5*2 +(lowTemp.get(0) - highTemp.get(0)) * fudu;
        paint.setColor(Color.rgb(71,213,234));
        for (int i = 0; i < lowTemp.size() - 1; i++) {
            paint.setAlpha(0xff);
            detay = (lowTemp.get(i + 1) - lowTemp.get(i)) * fudu;
            canvas.drawLine(startX, startY, startX + everyWidth,
                    startY + detay, paint);
            canvas.drawCircle(startX, startY, 15, paint);
            canvas.drawText(lowTemp.get(i) + "℃", startX + 5, startY
                    - textPianyi-extra, paint);
            if (i == 0) {
                todayEY = startY;
            }
            startX = startX + everyWidth;
            startY = startY + detay;
        }
        canvas.drawCircle(startX, startY, 15, paint);
        // paint.setColor(0xff32CD32);
        canvas.drawText(lowTemp.get(lowTemp.size()-1) + "℃", startX + 5, startY - textPianyi-extra,
                paint);
        // paint.setColor(Color.WHITE);

        drawDottedLine(everyWidth / 2, todaySY, todayEY
                + (viewHeight - todayEY) - 30, canvas);
        canvas.drawText("今天", everyWidth / 2 - 38, viewHeight - 5, paint);
    }

    /**
     * 绘制竖直的虚线, endY必须大于startY
     */
    private void drawDottedLine(float sX, float startY, float endY,
                                Canvas canvas) {
        while (startY < endY) {
            canvas.drawLine(sX, startY, sX, startY + 10, paint);
            startY = startY + 20;
        }
    }
}
