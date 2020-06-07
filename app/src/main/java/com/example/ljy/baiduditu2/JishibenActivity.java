package com.example.ljy.baiduditu2;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class JishibenActivity extends AppCompatActivity {
    File file;//声明保存文件的file对象
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jishiben);
        List<String> permissionList = new ArrayList<>();
        if (ContextCompat.checkSelfPermission(JishibenActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (ContextCompat.checkSelfPermission(JishibenActivity.this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.READ_PHONE_STATE);
        }
        if (ContextCompat.checkSelfPermission(JishibenActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (!permissionList.isEmpty()) {
            String [] permissions = permissionList.toArray(new String[permissionList.size()]);
            ActivityCompat.requestPermissions(JishibenActivity.this, permissions, 1);

        }else {
        EditText editText= (EditText) findViewById(R.id.jishitext);
        Button button= (Button) findViewById(R.id.baocunbutton);
        file=new File(Environment.getExternalStorageDirectory(),"Text.text");  //初始化文件对象
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FileOutputStream fileOutputStream=null;    //声明文件输出流对象
                String text=editText.getText().toString();   //得到文本
                try {
                    fileOutputStream=new FileOutputStream(file);  //获得文件输出流对象
                    fileOutputStream.write(text.getBytes()); //保存信息
                    fileOutputStream.flush();  //清除缓存
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }finally {
                    if (fileOutputStream!=null){
                        try {
                            fileOutputStream.close();
                            Toast.makeText(JishibenActivity.this,"保存成功",Toast.LENGTH_SHORT).show();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
        FileInputStream fileInputStream= null;//声明文件输入流对象
        try {
            fileInputStream = new FileInputStream(file);//获得文件输入流对象
            fileInputStream.read(new byte[fileInputStream.available()]);  //读取数据
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
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
            case R.id.ditu:
                Intent intent=new Intent(JishibenActivity.this,BaiduMapActivity.class);
                startActivity(intent);
                break;
            case R.id.tianqi:
                Intent intent1=new Intent(JishibenActivity.this,MainActivity.class);
                startActivity(intent1);
                break;
            case R.id.sumiao:
                Intent intent2=new Intent(JishibenActivity.this,TuxiangsumiaoActivity.class);
                startActivity(intent2);
                break;
            case R.id.fengjing:
                Intent intent3=new Intent(JishibenActivity.this,FengjingliulanActivity.class);
                startActivity(intent3);
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
