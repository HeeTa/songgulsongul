package smu.capstone.paper.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import smu.capstone.paper.R;
import smu.capstone.paper.activity.PostActivity;
import smu.capstone.paper.adapter.PostImageAdapter;

public class FragPostTag extends Fragment {

    private View view;
    PostImageAdapter adapter;
    JsonObject tag_data;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.frag_post_tag, container, false);

        GridView gridView = view.findViewById(R.id.frag_tag_grid);
        final JsonObject obj = getTagData();

        adapter = new PostImageAdapter(this.getContext(), R.layout.post_image_item, obj);
        gridView.setAdapter(adapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                Intent intent = new Intent(getContext(), PostActivity.class);

                // 게시글 id 전달
                int postId = obj.getAsJsonArray("data").get(position).getAsJsonObject().get("post_id").getAsInt();
                intent.putExtra("postId", postId);

                startActivity(intent);

                Log.d("TAG", position + "is Clicked");      // Can not getting this method.

            }
        });

        return view;
    }

    // server에서 data전달
    public JsonObject getTagData(){
        tag_data = new JsonObject();
        JSONObject item = new JSONObject();
        JSONArray arr= new JSONArray();
        int pid = 1;

        //임시 데이터 저장
        try{
            for(int i = 0; i < 14; i++){
                JSONObject obj = new JSONObject();
                obj.put("image", R.drawable.ic_favorite);
                obj.put("post_id", pid);
                pid++;
                arr.put(obj);
            }
            item.put("data", arr);
        }catch (JSONException e){
            e.printStackTrace();
        }
        return tag_data;
    }
}
