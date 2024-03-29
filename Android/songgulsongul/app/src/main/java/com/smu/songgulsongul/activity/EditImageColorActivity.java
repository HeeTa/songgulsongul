package com.smu.songgulsongul.activity;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;

import androidx.appcompat.app.AppCompatActivity;

import org.opencv.android.Utils;
import org.opencv.core.Mat;

import com.smu.songgulsongul.songgul;
import com.smu.songgulsongul.R;

import es.dmoral.toasty.Toasty;

public class EditImageColorActivity extends AppCompatActivity {

    long first_time = 0;
    long second_time = 0;

    ImageView editPreview;

    SeekBar seekBarHue;
    SeekBar seekBarSaturation;
    SeekBar seekBarBrightness;
    SeekBar seekBarContrast;

    Button done;

    long paperImgAddress;
    long croppedImgAddress;
    long editingImageAddress;
    //long editingImageColorAddress;

    Mat previewImage;
    Bitmap previewImageBitmap;

    int BackColor = Color.parseColor("#BFB1D8");
    int FontColor = Color.parseColor("#000000");

    public native void setColors(long inputImageAddress, long outputImageAddress, int hueProgress, int saturationProgress, int brightnessProgress, int contrastProgress);

    public void updatePreviewColors(){
        Mat locMat = new Mat();
        previewImage.release();
        updateColors(editingImageAddress,locMat.getNativeObjAddr());
        previewImage = locMat;
    }

    public void updateColors(long inputImageAddress, long outputImageAddress){
        setColors(inputImageAddress,outputImageAddress, seekBarHue.getProgress(),seekBarSaturation.getProgress(),seekBarBrightness.getProgress(),seekBarContrast.getProgress());



    }

    public void updatePreviewImageView(){
        /**/
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(previewImageBitmap!=null)
                    previewImageBitmap.recycle();
                previewImageBitmap = Bitmap.createBitmap(previewImage.cols(),previewImage.rows(), Bitmap.Config.ARGB_8888);
                Utils.matToBitmap(previewImage, previewImageBitmap);
                editPreview.setImageBitmap(previewImageBitmap);
            }
        });

        /*
        if(previewImageBitmap!=null)
            previewImageBitmap.recycle();
        previewImageBitmap = Bitmap.createBitmap(previewImage.cols(),previewImage.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(previewImage, previewImageBitmap);
        editPreview.setImageBitmap(previewImageBitmap);
         */
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_edit_image_colors);

        seekBarHue = findViewById(R.id.seekBarHue);
        seekBarSaturation = findViewById(R.id.seekBarSaturation);
        seekBarBrightness = findViewById(R.id.seekBarBrightness);
        seekBarContrast = findViewById(R.id.seekBarContrast);

        done = findViewById(R.id.edit_done);

        editPreview = findViewById(R.id.editPreview);

        //editingImageAddress = getIntent().getLongExtra("editingImageAddress", 0);
        editingImageAddress = ((songgul)getApplication()).getEditingMat().getNativeObjAddr();
        previewImage = ((songgul)getApplication()).getEditingMat().clone();
        updatePreviewImageView();


        seekBarHue.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                /*
                Mat locMat = new Mat();
                previewImage.release();
                updateColors(editingImageAddress,locMat.getNativeObjAddr());
                previewImage = locMat;*/
                updatePreviewColors();
                updatePreviewImageView();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        seekBarSaturation.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                updatePreviewColors();
                updatePreviewImageView();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        seekBarBrightness.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                updatePreviewColors();
                updatePreviewImageView();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        seekBarContrast.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                updatePreviewColors();
                updatePreviewImageView();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        //편집 적용
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            //Intent intent = new Intent( EditActivity.this , EditDoneActivity.class);
            //startActivity(intent);
                //TODO: 네이티브 어드레스로 접근한 Mat 객체도 double free 이슈가 발생하는가?
                //Mat locMat = new Mat();
                //Mat oldEditingMat = new Mat(editingImageAddress);
                updateColors(editingImageAddress,editingImageAddress);

                finish();
            }
        });



    }

    @Override
    public void onBackPressed() {
        second_time = System.currentTimeMillis();
        if(second_time-first_time <2000){
            super.onBackPressed();
            finish();
        }
        else{
            Toasty.custom(this, "한번 더 누르면 적용을 취소합니다", null, BackColor, FontColor, 2000, false, true).show();
            first_time = System.currentTimeMillis();
        }
    }


    @Override
    public void finish() {
        super.finish();
        previewImageBitmap.recycle();
        previewImage.release();
    }
}
