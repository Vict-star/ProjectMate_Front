package com.example.ljy.baiduditu2;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.GridView;
import android.widget.SimpleAdapter;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class FengjingliulanActivity extends AppCompatActivity {
    private int[] picture=new int[]{
            R.drawable.img1,R.drawable.img2,R.drawable.img3,R.drawable.img4,R.drawable.img5,R.drawable.img6,R.drawable.img7,R.drawable.img8,R.drawable.img9,
            R.drawable.img10,R.drawable.img11,R.drawable.img12,R.drawable.img13,R.drawable.img14,R.drawable.img15,R.drawable.img16,R.drawable.img17,R.drawable.img18,
            R.drawable.img19,R.drawable.img20,R.drawable.img21,R.drawable.img22,R.drawable.img23,R.drawable.img24,R.drawable.img25,R.drawable.img26,R.drawable.img27,
            R.drawable.img28,R.drawable.img29,R.drawable.img30,R.drawable.img31,R.drawable.img32,R.drawable.img33,R.drawable.img34,R.drawable.img35,R.drawable.img36,
            R.drawable.img37,R.drawable.img38,R.drawable.img39,R.drawable.img40,R.drawable.img41,R.drawable.img42,R.drawable.img43,R.drawable.img44,R.drawable.img45,
            R.drawable.img46,R.drawable.img47,R.drawable.img48,R.drawable.img49,R.drawable.img50,R.drawable.img51,R.drawable.img52,R.drawable.img53,R.drawable.img54,
            R.drawable.img4,R.drawable.img5,R.drawable.img6,R.drawable.img31,R.drawable.img32,R.drawable.img33, R.drawable.img10,R.drawable.img11,R.drawable.img12,
            R.drawable.img13,R.drawable.img6,R.drawable.img7,R.drawable.img8,R.drawable.img9,R.drawable.img14,R.drawable.img15,R.drawable.img16,R.drawable.img17,
            R.drawable.img19,R.drawable.img20,R.drawable.img21,R.drawable.img22,R.drawable.img23,R.drawable.img24,R.drawable.img25,R.drawable.img26,R.drawable.img27,
            R.drawable.img46,R.drawable.img47,R.drawable.img48,R.drawable.img49,R.drawable.img50,R.drawable.img51,R.drawable.img52,R.drawable.img53,R.drawable.img54,
            R.drawable.img37,R.drawable.img38,R.drawable.img39,R.drawable.img40,R.drawable.img41,R.drawable.img42,R.drawable.img43,R.drawable.img44,R.drawable.img45};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fengjingliulan);
        GridView gridView= (GridView) findViewById(R.id.fengjinggridview);
        List<Map<String,Integer>> mapList=new ArrayList<Map<String,Integer>>();
        for (int i=0;i<picture.length;i++){
            Map<String,Integer> map=new HashMap<String,Integer>();
            map.put("image",picture[i]);
            mapList.add(map);
        }
        SimpleAdapter simpleAdapter=new SimpleAdapter(this,mapList,R.layout.fengjingliulan,new String[]{"image"},new int[]{R.id.fengjingimage});
        gridView.setAdapter(simpleAdapter);
    }
    //解析菜单资源文件
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.actionbar,menu);  //解析菜单文件
        return super.onCreateOptionsMenu(menu);
    }
    // 让菜单同时显示图标和文字
    @Override
    public boolean onMenuOpened(int featureId, Menu menu) {
        if (menu != null) {
            if (menu.getClass().getSimpleName().equalsIgnoreCase("MenuBuilder")) {
                try {
                    Method method = menu.getClass().getDeclaredMethod("setOptionalIconsVisible", Boolean.TYPE);
                    method.setAccessible(true);
                    method.invoke(menu, true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return super.onMenuOpened(featureId, menu);
    }
    //设置菜单选中后的动作
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){//判断选中哪一项
            case R.id.ditu:
                Intent intent=new Intent(FengjingliulanActivity.this,BaiduMapActivity.class);
                startActivity(intent);
                break;
            case R.id.tianqi:
                Intent intent1=new Intent(FengjingliulanActivity.this,MainActivity.class);
                startActivity(intent1);
                break;
            case R.id.jishiben:
                Intent intent2=new Intent(FengjingliulanActivity.this,JishibenActivity.class);
                startActivity(intent2);
                break;
            case R.id.sumiao:
                Intent intent3=new Intent(FengjingliulanActivity.this,TuxiangsumiaoActivity.class);
                startActivity(intent3);
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
