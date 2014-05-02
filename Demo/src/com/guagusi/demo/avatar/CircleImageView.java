package com.guagusi.demo.avatar;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.Xfermode;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;
import java.lang.ref.WeakReference;

/**
 * @author 古阿古斯
 * @description
 * @date 2013年1月2日
 */
public class CircleImageView extends ImageView {

    private static final Xfermode sXfermode = new PorterDuffXfermode(PorterDuff.Mode.DST_IN);

    private Context mContext;

    private Bitmap mMaskBitmap;
    private Paint mPaint;
    private WeakReference<Bitmap> mWeakBitmap;

    public CircleImageView(Context context) {
        super(context);
        init(context);
    }

    public CircleImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CircleImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
        mContext = context;
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    }

    @Override
    public void invalidate() {
        mWeakBitmap = null;
        super.invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if(!isInEditMode()){
            int i = canvas.saveLayer(0.0f,0.0f,getWidth(),getHeight(),null,Canvas.ALL_SAVE_FLAG);

            try {
                Bitmap bitmap = mWeakBitmap != null ? mWeakBitmap.get() : null;
                if(bitmap == null || bitmap.isRecycled()){
                    Drawable drawable = getDrawable();
                    if(drawable != null){
                        bitmap = Bitmap.createBitmap(getWidth(),getHeight(), Bitmap.Config.ARGB_8888);
                        Canvas bitmapCanvas = new Canvas(bitmap);
                        drawable.setBounds(0,0,getWidth(),getHeight());
                        drawable.draw(bitmapCanvas);

                        mMaskBitmap = getBitmap(getWidth(),getHeight());

                        //绘制 Bitmap
                        mPaint.reset();
                        mPaint.setFilterBitmap(false);
                        mPaint.setXfermode(sXfermode);
                        bitmapCanvas.drawBitmap(mMaskBitmap,0.0f,0.0f,mPaint);

                        mWeakBitmap = new WeakReference<Bitmap>(bitmap);
                    }
                }

                if(bitmap != null){
                    mPaint.setXfermode(null);
                    canvas.drawBitmap(bitmap,0.0f,0.0f,mPaint);
                    return;
                }
            }catch (Exception e){
                System.gc();
            }finally {
                canvas.restoreToCount(i);
            }
        }else {
            super.onDraw(canvas);
        }

    }

    public static Bitmap getBitmap(int width,int height){
        Bitmap bitmap = Bitmap.createBitmap(width,height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.BLACK);
        canvas.drawOval(new RectF(0.0f,0.0f,width,height),paint);
        return bitmap;
    }
}
