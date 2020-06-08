package com.example.ljy.baiduditu2;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class BaiduMapActivity extends AppCompatActivity {

    public LocationClient mLocationClient = null;
    private MapView mapView;
    private BaiduMap baiduMap;
    private boolean isFirstLocate = true;
    private MyLocationListener myLocationListener = new MyLocationListener();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //定位初始化
        mLocationClient = new LocationClient(getApplicationContext());
        //注册LocationListener监听器
        mLocationClient.registerLocationListener(myLocationListener);
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_baidu_map);
        mapView = (MapView) findViewById(R.id.bmapView);
        baiduMap = mapView.getMap();
        baiduMap.setMyLocationEnabled(true);
        List<String> permissionList = new ArrayList<>();
        if (ContextCompat.checkSelfPermission(BaiduMapActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (ContextCompat.checkSelfPermission(BaiduMapActivity.this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.READ_PHONE_STATE);
        }
        if (ContextCompat.checkSelfPermission(BaiduMapActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (!permissionList.isEmpty()) {
            String [] permissions = permissionList.toArray(new String[permissionList.size()]);
            ActivityCompat.requestPermissions(BaiduMapActivity.this, permissions, 1);
        } else {
            initLocation();
            //开启地图定位图层
            mLocationClient.start();
            dingeri();
        }
    }
    private void dingeri(){
        LatLng ll = new LatLng(23, 114);
        MapStatusUpdate update = MapStatusUpdateFactory.newLatLng(ll);
        baiduMap.animateMapStatus(update);
        update = MapStatusUpdateFactory.zoomTo(16f);
        baiduMap.animateMapStatus(update);
        MyLocationData.Builder locationBuilder = new MyLocationData.Builder();
        locationBuilder.latitude(23);
        locationBuilder.longitude(114);
        MyLocationData locationData = locationBuilder.build();
        baiduMap.setMyLocationData(locationData);
    }
    private void initLocation(){
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true); // 打开gps
        option.setCoorType("bd09ll"); // 设置坐标类型
        option.setScanSpan(5000);
        //设置locationClientOption
        option.setIsNeedAddress(true);
        mLocationClient.setLocOption(option);
    }
    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mLocationClient.stop();
        mapView.onDestroy();
        baiduMap.setMyLocationEnabled(false);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0) {
                    for (int result : grantResults) {
                        if (result != PackageManager.PERMISSION_GRANTED) {
                            Toast.makeText(this, "Permission must be granted to use this program!!", Toast.LENGTH_SHORT).show();
                            finish();
                            return;
                        }
                    }
                    initLocation();
                    //开启地图定位图层
                    mLocationClient.start();
                    dingeri();
                } else {
                    Toast.makeText(BaiduMapActivity.this, "An unknown error occurred!!", Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
            default:
        }
    }
    public void navigateTo(BDLocation location) {
        Toast.makeText(this, "navigateTo(BDLocation location)： " , Toast.LENGTH_SHORT).show();
        if (isFirstLocate) {
            Toast.makeText(BaiduMapActivity.this, "nav to " + location.getAddrStr(), Toast.LENGTH_SHORT).show();
            Toast.makeText(BaiduMapActivity.this, "我的纬度是： " + location.getLatitude(), Toast.LENGTH_SHORT).show();
            Toast.makeText(BaiduMapActivity.this, "我的经度是： " + location.getLongitude(), Toast.LENGTH_SHORT).show();
            LatLng ll = new LatLng(location.getLatitude(), location.getLongitude());
            MapStatusUpdate update = MapStatusUpdateFactory.newLatLng(ll);
            baiduMap.animateMapStatus(update);
            update = MapStatusUpdateFactory.zoomTo(16f);
            baiduMap.animateMapStatus(update);
            isFirstLocate = false;
        }
        MyLocationData.Builder locationBuilder = new MyLocationData.Builder();
        locationBuilder.latitude(location.getLatitude());
        locationBuilder.longitude(location.getLongitude());
        MyLocationData locationData = locationBuilder.build();
        baiduMap.setMyLocationData(locationData);
    }
    public class MyLocationListener implements BDLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location){
            Toast.makeText(BaiduMapActivity.this, "我的纬度是： " + location.getLatitude(), Toast.LENGTH_SHORT).show();
            Toast.makeText(BaiduMapActivity.this, "我的经度是： " + location.getLongitude(), Toast.LENGTH_SHORT).show();
            Toast.makeText(BaiduMapActivity.this, "MyLocationListener()： " , Toast.LENGTH_SHORT).show();
            navigateTo(location);
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
            case R.id.jishiben:
                Intent intent=new Intent(BaiduMapActivity.this,JishibenActivity.class);
                startActivity(intent);
                break;
            case R.id.tianqi:
                Intent intent1=new Intent(BaiduMapActivity.this,MainActivity.class);
                startActivity(intent1);
                break;
            case R.id.sumiao:
                Intent intent2=new Intent(BaiduMapActivity.this,TuxiangsumiaoActivity.class);
                startActivity(intent2);
                break;
            case R.id.fengjing:
                Intent intent3=new Intent(BaiduMapActivity.this,FengjingliulanActivity.class);
                startActivity(intent3);
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
