package com.example.ljy.baiduditu2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.menu.MenuBuilder;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.example.ljy.baiduditu2.weather.WeatherActivity;

import java.lang.reflect.Method;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        if (prefs.getString("weather", null) != null) {
            Intent intent = new Intent(this, WeatherActivity.class);
            startActivity(intent);
            finish();
        }
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
            case R.id.sumiao:
                Intent intent1=new Intent(MainActivity.this,TuxiangsumiaoActivity.class);
                startActivity(intent1);
                break;
            case R.id.ditu:
                Intent intent=new Intent(MainActivity.this,BaiduMapActivity.class);
                startActivity(intent);
                break;
            case R.id.jishiben:
                Intent intent2=new Intent(MainActivity.this,JishibenActivity.class);
                startActivity(intent2);
                break;
            case R.id.fengjing:
                Intent intent3=new Intent(MainActivity.this,FengjingliulanActivity.class);
                startActivity(intent3);
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}