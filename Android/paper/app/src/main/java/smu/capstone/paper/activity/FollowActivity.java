package smu.capstone.paper.activity;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import smu.capstone.paper.R;
import smu.capstone.paper.fragment.FragFollower;
import smu.capstone.paper.fragment.FragFollowing;

public class FollowActivity extends AppCompatActivity {
    private FragmentManager fm;
    private FragmentTransaction ft;

    private TextView follow_follow;
    private TextView follow_follower;

    private FragFollowing fragFollowing;
    private FragFollower fragFollower;

    private ImageView follow_img;
    private TextView follow_intro;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_follow);




        follow_intro = findViewById(R.id.follow_intro);
        follow_img = findViewById(R.id.follow_image);
        follow_follow = findViewById(R.id.follow_follow); // 내가 팔로잉 하는사람
        follow_follower = findViewById(R.id.follow_follower); // 나를 팔로잉 하는사람들
                                            //나의 팔로워



        fragFollower = new FragFollower();
        fragFollowing = new FragFollowing();


        // 사용자 정보 초기화 및 툴바초기화

        Toolbar toolbar = (Toolbar) findViewById(R.id.follow_toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();

        actionBar.setTitle("user1234"); // 사용자 아이디 추가
        actionBar.setDisplayHomeAsUpEnabled(true); // 뒤로가기 버튼 만들기
        actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_ios_new_24); //뒤로가기 버튼 이미지 지정
        follow_img.setImageBitmap(drawable2Bitmap(getResources().getDrawable(R.drawable.test)));
        follow_intro.setText("안녕하세요!\n 반갑습니다~!");


        //팔로우에 밑줄 긋고 팔로우창띄움
        SpannableString content = new SpannableString("팔로우");
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0); follow_follow.setText(content);

        fm = getSupportFragmentManager();

        ft = fm.beginTransaction();
        ft.replace(R.id.follow_frag, fragFollower);
        ft.commit();


        follow_follow.setOnClickListener(new View.OnClickListener() { // 나를 팔로잉 하는 사람들 보여줌
            @Override
            public void onClick(View view) {

                //팔로우에 밑줄 긋고 팔로우창띄움
                SpannableString content = new SpannableString("팔로우");
                content.setSpan(new UnderlineSpan(), 0, content.length(), 0); follow_follow.setText(content);
                follow_follower.setText("팔로잉");

                ft = fm.beginTransaction();
                ft.replace(R.id.follow_frag, fragFollower);
                ft.commit();
            }
        });

        follow_follower.setOnClickListener(new View.OnClickListener() { // 내가 팔로잉 하는사람들 보여줌
            @Override
            public void onClick(View view) {

                //팔로우에 밑줄 긋고 팔로우창띄움
                follow_follow.setText("팔로우");
                SpannableString content = new SpannableString("팔로잉");
                content.setSpan(new UnderlineSpan(), 0, content.length(), 0); follow_follower.setText(content);

                ft = fm.beginTransaction();
                ft.replace(R.id.follow_frag, fragFollowing);
                ft.commit();
            }
        });




    }
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:{ // 뒤로가기 버튼 눌렀을 때
                finish();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }


    public static Bitmap drawable2Bitmap(Drawable drawable) {
        Bitmap bitmap = Bitmap
                .createBitmap(
                        drawable.getIntrinsicWidth(),
                        drawable.getIntrinsicHeight(),
                        drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                                : Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight());
        drawable.draw(canvas);
        return bitmap;
    }
}