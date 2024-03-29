package com.smu.songgulsongul.activity;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.bumptech.glide.Glide;

import java.io.File;

import com.smu.songgulsongul.R;
import com.smu.songgulsongul.server.RetrofitClient;

import es.dmoral.toasty.Toasty;

public class SaveImageActivity extends Activity {
    boolean size_check;
    String post_image;
    int post_id;

    private File file, dir;
    private String fileName;

    int BackColor = Color.parseColor("#BFB1D8");
    int FontColor = Color.parseColor("#000000");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_save_image);

        final ImageView image = (ImageView)findViewById(R.id.save_img_pick);
        Button cancle_btn = (Button)findViewById(R.id.save_img_cancle);
        Button save_btn = (Button)findViewById(R.id.save_img_btn);
        Spinner save_img_size = (Spinner)findViewById(R.id.save_img_size);

        Intent intent = getIntent();
        post_id = intent.getIntExtra("post_id", -1);
        post_image = intent.getStringExtra("post_image");
        Glide.with(SaveImageActivity.this).load(post_image).into(image);

        size_check = true;
        final int[] image_size= {400, 600, 700, 900, 1000, 1200};

        save_img_size.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //Toast.makeText(SaveImageActivity.this, position+"번", Toast.LENGTH_SHORT).show();
                if(position>0){
                    if(image.getWidth()<image_size[position-1]||image.getHeight()<image_size[position-1]){
                        Toasty.custom(SaveImageActivity.this, "저장된 이미지의 화질이 저하될 수 있습니다", null, BackColor, FontColor, 2000, false, true).show();
                        size_check = false;
                    }
                    else{
                        size_check=true;
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        cancle_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        
        save_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!size_check) {

                    View dialogView = getLayoutInflater().inflate(R.layout.activity_popup, null);
                    androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(SaveImageActivity.this);
                    builder.setView(dialogView);
                    final AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                    alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                    ImageView icon=dialogView.findViewById(R.id.warning);

                    TextView txt=dialogView.findViewById(R.id.txtText);
                    txt.setText("선택한 규격이 원본의 사이즈보다 크기 때문에 이미지 저장 시 화질이 저하될 수 있습니다. 계속 하시겠습니까?");
                    Button ok_btn = dialogView.findViewById(R.id.okBtn);
                    ok_btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            DownloadImage();
                            Toasty.custom(getApplicationContext(), "저장 성공!", null, BackColor, FontColor, 2000, false, true).show();
                            alertDialog.dismiss();
                        }
                    });
                    Button cancel_btn = dialogView.findViewById(R.id.cancelBtn);
                    cancel_btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Toasty.custom(getApplicationContext(), "Pressed Cancel", null, BackColor, FontColor, 2000, false, true).show();
                            alertDialog.dismiss();
                        }
                    });
                } else {
                    DownloadImage();
                    Toasty.custom(SaveImageActivity.this, "저장 성공!", null, BackColor, FontColor, 2000, false, true).show();
                    finish();
                }
            }
        });

        Display display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        int width = (int)(display.getWidth()* 0.9);
        int height = (int)(display.getHeight() * 0.9);
        getWindow().getAttributes().width = width;
        getWindow().getAttributes().height = height;

    }

    public void DownloadImage(){
        fileName = post_image.substring(post_image.lastIndexOf('/') + 1);
        DownloadManager mgr = (DownloadManager)SaveImageActivity.this.getSystemService(Context.DOWNLOAD_SERVICE);

        Uri uri = Uri.parse(post_image);
        DownloadManager.Request request = new DownloadManager.Request(uri);
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE)
                .setAllowedOverRoaming(false)
                .setTitle("POST IMAGE")
                .setDescription("post image download")
                .setDestinationInExternalPublicDir(Environment.DIRECTORY_PICTURES, fileName);
        mgr.enqueue(request);

        sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri));
        //sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + "mnt/sdcard/Pictures" + fileName + ".jpg")));
    }
}