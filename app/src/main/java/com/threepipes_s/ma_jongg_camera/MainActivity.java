package com.threepipes_s.ma_jongg_camera;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.RectF;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.Size;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

public class MainActivity extends Activity {

    SurfaceView sv;
    SurfaceHolder sh;
    Camera cam;
    Grid grid;
    FrameLayout fl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        fl = new FrameLayout(this);
        setContentView(fl);

        sv = new SurfaceView(this);
        sh = sv.getHolder();
        sh.addCallback(new SurfaceHolderCallback());

        Button btn = new Button(this);
        btn.setText("撮影");
        btn.setLayoutParams(new LayoutParams(200, 150));
        btn.setOnClickListener(new TakePictureClickListener());

        fl.addView(sv);
        fl.addView(btn);
        grid = new Grid(getApplicationContext());
        fl.addView(grid);
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    class SurfaceHolderCallback implements SurfaceHolder.Callback {
        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            cam = Camera.open();
            Parameters param = cam.getParameters();
            List<Size> ss = param.getSupportedPictureSizes();
            Size pictSize = ss.get(0);

            param.setPictureSize(pictSize.width, pictSize.height);
            cam.setParameters(param);
        }
        @Override
        public void surfaceChanged(SurfaceHolder holder, int f, int w, int h) {
            try {
                cam.setDisplayOrientation(0);
                cam.setPreviewDisplay(sv.getHolder());

//                Parameters param = cam.getParameters();
//                List<Size> previewSizes =
//                        cam.getParameters().getSupportedPreviewSizes();
//                Size pre = previewSizes.get(0);
//                param.setPreviewSize(pre.width, pre.height);
//
//                LayoutParams lp = new LayoutParams(pre.width, pre.height);
//                sv.setLayoutParams(lp);
//
//                grid.setSize(w, h);
//
//                cam.setParameters(param);
                cam.startPreview();
            } catch (Exception e) { }
        }
        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            cam.stopPreview();
            cam.release();
        }
    }

    class TakePictureClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            cam.autoFocus(autoFocusCallback);
        }

        private Camera.AutoFocusCallback autoFocusCallback = new Camera.AutoFocusCallback() {
            @Override
            public void onAutoFocus(boolean success, Camera camera) {
                cam.takePicture(new Camera.ShutterCallback() {
                    @Override
                    public void onShutter() {}
                }, null, new TakePictureCallback());
            }
        };
    }

    class TakePictureCallback implements Camera.PictureCallback {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            try {
                File dir = new File(
                        Environment.getExternalStorageDirectory(), "Camera");
                if(!dir.exists()) {
                    dir.mkdir();
                }
                Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                int width = bitmap.getWidth();
                int height = bitmap.getHeight();
                RectF[] rects = Grid.createRects(width, height);
                for(int i = 0; i < rects.length; i++) {
                    RectF rect = rects[i];
                    Bitmap subImage = Bitmap.createBitmap(
                            bitmap,
                            (int)rect.left, (int)rect.top, (int)rect.width(), (int)rect.height(),
                            null, true
                    );

                    File f = new File(dir, "img_" + i + ".jpg");
                    FileOutputStream fos = new FileOutputStream(f);
//                    fos.write(data);
                    subImage.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                    fos.close();
                }

                Toast.makeText(getApplicationContext(),
                        "写真を保存しました", Toast.LENGTH_LONG).show();
                cam.startPreview();
            } catch (Exception e) {
                Log.e("IMG", "Save image error: " + e);
            }
        }
    }
}