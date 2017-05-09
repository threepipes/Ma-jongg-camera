package com.threepipes_s.ma_jongg_camera;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.View;

public class Grid extends View {
    RectF[] rects;
    int r;
    Paint p;

    public Grid(Context context) {
        super(context);
        p = new Paint();
        p.setColor(Color.DKGRAY);
        p.setStyle(Paint.Style.STROKE);
        p.setStrokeWidth(1);
        setSize(0, 0);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        setSize(w, h);
    }

    public void setSize(int w, int h) {
        int wid = w / 15;
        int hei = wid * 7 / 5;
        int y = h / 2 - hei / 2;
        rects = new RectF[14];
        r = wid / 10;
        for(int i = 0; i < 14; i++) {
            int x = wid * i + wid / 2;
            rects[i] = new RectF(x, y, x + wid, y + hei);
        }
    }

    protected void onDraw(Canvas canvas){
        super.onDraw(canvas);
        for(RectF rect: rects) {
            canvas.drawRoundRect(
                    rect,
                    r,   // 角丸を表す円のrx
                    r,   // 角丸を表す円のry
                    p);
        }
    }
}