package com.example.ljy.baiduditu2;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.ContentUris;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Toast;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class TuxiangsumiaoActivity extends AppCompatActivity {
    public SeekBar seekBar;
    public static final int TAKE_PHOTO = 1;
    public static final int CHOOSE_PHOTO = 2;
    private ImageView picture1;
    private Uri imageUri;
    private Bitmap bitmap;
    private int sumiaoprogress=10;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tuxiangsumiao);
        //初始化组件
        Button takephoto = (Button) findViewById(R.id.takephotos);
        Button choosephoto = (Button) findViewById(R.id.choosephotos);
        Button baocun = (Button) findViewById(R.id.baocun);
        seekBar=(SeekBar) findViewById(R.id.sumiaoshendu);
        picture1 = (ImageView) findViewById(R.id.picture1);
        //takephoto按钮监听器,paizhao()方法进行拍照
        takephoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                paizhao();//拍照
            }
        });
        //choosephoto按钮监听器：openAlbum()方法打开相册
        choosephoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<String> permissionList = new ArrayList<>();
                if (ContextCompat.checkSelfPermission(TuxiangsumiaoActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    permissionList.add(Manifest.permission.ACCESS_FINE_LOCATION);
                }
                if (ContextCompat.checkSelfPermission(TuxiangsumiaoActivity.this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                    permissionList.add(Manifest.permission.READ_PHONE_STATE);
                }
                if (ContextCompat.checkSelfPermission(TuxiangsumiaoActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
                }
                if (!permissionList.isEmpty()) {
                    String [] permissions = permissionList.toArray(new String[permissionList.size()]);
                    ActivityCompat.requestPermissions(TuxiangsumiaoActivity.this, permissions, 1);
                } else {
                    openAlbum();//打开相册
                }
            }
        });
        //baocun按钮监听器：openAlbum()方法保存相册
        baocun.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bitmap!=null){
                    saveBitmap(bitmap, "111.JPEG");
                }else {
                    Toast.makeText(TuxiangsumiaoActivity.this, "请拍照或选择照片", Toast.LENGTH_SHORT).show();
                }
            }
        });
        //seekBar改变监听器
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (bitmap!=null){
                    bitmap=compressScale(bitmap,480f,20);  //压缩图片
                    bitmap = createPencli(bitmap, progress, progress);
                    picture1.setImageBitmap(bitmap);
                }
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }
    //拍照
    public void paizhao(){
        File outputImage = new File(getExternalCacheDir(), "output_image.jpg");
        try {
            if (outputImage.exists()) {
                outputImage.delete();
            }
            outputImage.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (Build.VERSION.SDK_INT >= 24) {
            imageUri = FileProvider.getUriForFile(TuxiangsumiaoActivity.this,
                    "com.example.cameraalbumtest.fileprovider", outputImage);
        } else {
            imageUri = Uri.fromFile(outputImage);
        }
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent, TAKE_PHOTO);
    }
    // 打开相册
    private void openAlbum() {
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        startActivityForResult(intent, CHOOSE_PHOTO); // 打开相册
    }
    //保存到相册
    public void saveBitmap(Bitmap bitmap1, String bitName){
        String fileName ;
        File file ;
        if(Build.BRAND .equals("Xiaomi") ){ // 小米手机
            fileName = Environment.getExternalStorageDirectory().getPath()+"/DCIM/Camera/"+bitName ;
        }else{ // Meizu 、Oppo
            fileName = Environment.getExternalStorageDirectory().getPath()+"/DCIM/"+bitName ;
        }
        file = new File(fileName);
        if(file.exists()){
            file.delete();
        }
        FileOutputStream out;
        try{
            out = new FileOutputStream(file);
            // 格式为 JPEG，照相机拍出的图片为JPEG格式的，PNG格式的不能显示在相册中
            if(bitmap1.compress(Bitmap.CompressFormat.JPEG, 90, out))
            {
                out.flush();
                out.close();
// 插入图库
                MediaStore.Images.Media.insertImage(this.getContentResolver(), file.getAbsolutePath(), bitName, null);
                Toast.makeText(TuxiangsumiaoActivity.this, "已保存到相册", Toast.LENGTH_SHORT).show();
            }
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        // 发送广播，通知刷新图库的显示
        this.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + fileName)));
        bitmap1=null;
    }

    //拍照 paizhao()和打开相册openAlbum()方法的startActivityForResult的回调
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {
            case TAKE_PHOTO:
                if (resultCode == RESULT_OK) {
                    try {
                        // 将拍摄的照片显示出来
//---------------------------------------------------------------拍照的程序接口---------------------------------------------------------------
//---------------------------------------------------------------拍照的程序接口---------------------------------------------------------------
                        bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
                        bitmap=compressScale(bitmap,1024f,1000);  //压缩图片
                        bitmap = createPencli(bitmap, sumiaoprogress, sumiaoprogress);
                        picture1.setImageBitmap(bitmap);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
            case CHOOSE_PHOTO:
                if (resultCode == RESULT_OK) {
                    // 判断手机系统版本号
                    if (Build.VERSION.SDK_INT >= 19) {
                        // 4.4及以上系统使用这个方法处理图片
                        handleImageOnKitKat(data,sumiaoprogress);
                    } else {
                        // 4.4以下系统使用这个方法处理图片
                        handleImageBeforeKitKat(data,sumiaoprogress);
                    }
                }
                break;
            default:
                break;
        }
    }
    //打开相册：根据图片的URI找到图片真实位置：4.4（19）以上系统
    @TargetApi(19)
    private void handleImageOnKitKat(Intent data,int progress) {
        String imagePath = null;
        Uri uri = data.getData();
        Log.d("TAG", "handleImageOnKitKat: uri is " + uri);
        if (DocumentsContract.isDocumentUri(this, uri)) {
            // 如果是document类型的Uri，则通过document id处理
            String docId = DocumentsContract.getDocumentId(uri);
            if ("com.android.providers.media.documents".equals(uri.getAuthority())) {
                String id = docId.split(":")[1]; // 解析出数字格式的id
                String selection = MediaStore.Images.Media._ID + "=" + id;
                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection);
            } else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())) {
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(docId));
                imagePath = getImagePath(contentUri, null);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            // 如果是content类型的Uri，则使用普通方式处理
            imagePath = getImagePath(uri, null);
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            // 如果是file类型的Uri，直接获取图片路径即可
            imagePath = uri.getPath();
        }
        displayImage(imagePath,progress); // 根据图片路径显示图片
    }
    //打开相册：根据图片的URI找到图片真实位置：4.4（19）以下系统
    private void handleImageBeforeKitKat(Intent data,int progress) {
        Uri uri = data.getData();
        String imagePath = getImagePath(uri, null);
        displayImage(imagePath,progress);
    }
    // 通过Uri和selection来获取真实的图片路径
    private String getImagePath(Uri uri, String selection) {
        String path = null;
        Cursor cursor = getContentResolver().query(uri, null, selection, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }
    //将相册中的照片显示到屏幕
    private void displayImage(String imagePath,int progress) {
        if (imagePath != null) {
//------------------------------------------------------------将相册中的照片显示到屏幕程序接口---------------------------------------------------------------
//------------------------------------------------------------将相册中的照片显示到屏幕程序接口---------------------------------------------------------------
            bitmap = BitmapFactory.decodeFile(imagePath);
            bitmap=compressScale(bitmap,1024f,1000);  //压缩图片
            bitmap= createPencli(bitmap, progress, progress);
            picture1.setImageBitmap(bitmap);
        } else {
            Toast.makeText(this, "failed to get image", Toast.LENGTH_SHORT).show();
        }
    }

    //-------------------------------------------------------------------图像处理（开始）--------------------------------------------------------
    //-------------------------------------------------------------------图像处理（开始）--------------------------------------------------------
    //总方法，调用1-5步的方法，a和b都为告诉模糊的参数
    public Bitmap createPencli(Bitmap bitmap1,int a,int b) {
        int width = bitmap1.getWidth();
        int height = bitmap1.getHeight();
        int[] pixels = new int[width * height];
        bitmap1.getPixels(pixels, 0, width, 0, 0, width, height);
        bitmap1=null;
        int[] gray = getGray(pixels, width, height);
        int[] inverse = getInverse(gray);
        int[] guassBlur = gaussBlur(inverse, width, height, a, b);
        int[] output = deceasecolorCompound(guassBlur, gray);
        return create(pixels, output, width, height);
    }
    //第一步：去色，将压缩好的图片变为灰度图，即黑白图
    int[] getGray(int[] pixels, int width, int height) {
        int gray[] = new int[width * height];
        for (int i = 0; i < width - 1; i++) {
            for (int j = 0; j < height - 1; j++) {
                int index = width * j + i;
                int rgba = pixels[index];
                int g = ((rgba & 0x00FF0000) >> 16) * 3 + ((rgba & 0x0000FF00) >> 8) * 6 + ((rgba & 0x000000FF)) * 1;
                gray[index] = g / 10;
            }
        }
        return gray;
    }
    //(第二步：取反色，对去色灰度图取反色：得到每个像素的补色，R(补) = 255 - R
    public int[] getInverse(int[] gray) {
        int[] inverse = new int[gray.length];

        for (int i = 0, size = gray.length; i < size; i++) {
            inverse[i] = 255 - gray[i];
        }
        return inverse;
    }
    //第三步：高斯模糊，把反相后的像素值平均一下。
    public int[] gaussBlur(int[] data, int width, int height, int radius, float sigma) {
        float pa = (float) (1 / (Math.sqrt(2 * Math.PI) * sigma));
        float pb = -1.0f / (2 * sigma * sigma);
        // generate the Gauss Matrix
        float[] gaussMatrix = new float[radius * 2 + 1];
        float gaussSum = 0f;
        for (int i = 0, x = -radius; x <= radius; ++x, ++i) {
            float g = (float) (pa * Math.exp(pb * x * x));
            gaussMatrix[i] = g;
            gaussSum += g;
        }
        for (int i = 0, length = gaussMatrix.length; i < length; ++i) {
            gaussMatrix[i] /= gaussSum;
        }
        // x direction
        for (int y = 0; y < height; ++y) {
            for (int x = 0; x < width; ++x) {
                float r = 0, g = 0, b = 0;
                gaussSum = 0;
                for (int j = -radius; j <= radius; ++j) {
                    int k = x + j;
                    if (k >= 0 && k < width) {
                        int index = y * width + k;
                        int color = data[index];
                        int cr = (color & 0x00ff0000) >> 16;
                        int cg = (color & 0x0000ff00) >> 8;
                        int cb = (color & 0x000000ff);

                        r += cr * gaussMatrix[j + radius];
                        g += cg * gaussMatrix[j + radius];
                        b += cb * gaussMatrix[j + radius];

                        gaussSum += gaussMatrix[j + radius];
                    }
                }
                int index = y * width + x;
                int cr = (int) (r / gaussSum);
                int cg = (int) (g / gaussSum);
                int cb = (int) (b / gaussSum);

                data[index] = cr << 16 | cg << 8 | cb | 0xff000000;
            }
        }
        // y direction
        for (int x = 0; x < width; ++x) {
            for (int y = 0; y < height; ++y) {
                float r = 0, g = 0, b = 0;
                gaussSum = 0;
                for (int j = -radius; j <= radius; ++j) {
                    int k = y + j;
                    if (k >= 0 && k < height) {
                        int index = k * width + x;
                        int color = data[index];
                        int cr = (color & 0x00ff0000) >> 16;
                        int cg = (color & 0x0000ff00) >> 8;
                        int cb = (color & 0x000000ff);

                        r += cr * gaussMatrix[j + radius];
                        g += cg * gaussMatrix[j + radius];
                        b += cb * gaussMatrix[j + radius];

                        gaussSum += gaussMatrix[j + radius];
                    }
                }
                int index = y * width + x;
                int cr = (int) (r / gaussSum);
                int cg = (int) (g / gaussSum);
                int cb = (int) (b / gaussSum);
                data[index] = cr << 16 | cg << 8 | cb | 0xff000000;
            }
        }
        return data;
    }
    //第四步：颜色减淡混合，将第去色后的像素和高斯模糊得到的像素值进行计算其计算公式：结果色 = 基色 + (混合色 * 基色) / (255 - 混合色)
    public int[] deceasecolorCompound(int[] guassBlur, int[] gray) {

        for (int i = 0, length = gray.length; i < length; ++i) {
            int bColor = gray[i];
            int br = (bColor & 0x00ff0000) >> 16;
            int bg = (bColor & 0x0000ff00) >> 8;
            int bb = (bColor & 0x000000ff);

            int mColor = guassBlur[i];
            int mr = (mColor & 0x00ff0000) >> 16;
            int mg = (mColor & 0x0000ff00) >> 8;
            int mb = (mColor & 0x000000ff);

            int nr = colorDodgeFormular(br, mr);
            int ng = colorDodgeFormular(bg, mg);
            int nb = colorDodgeFormular(bb, mb);

            gray[i] = nr << 16 | ng << 8 | nb | 0xff000000;
        }
        return gray;
    }
    //第四步：颜色减淡混合的子方法
    private static int colorDodgeFormular(int base, int mix) {
        int result = base + (base * mix) / (255 - mix);
        result = result > 255 ? 255 : result;
        return result;
    }
    //最后一步第五步：生成图像
    public Bitmap create(int[] pixels, int[] output, int width, int height) {
        for (int i = 0, size = pixels.length; i < size; i++) {
            int gray = output[i];
            int pixel = (pixels[i] & 0xff000000) | (gray << 16) | (gray << 8) | gray;//注意加上原图的 alpha通道

            output[i] = pixel;
        }
        return Bitmap.createBitmap(output, width, height, Bitmap.Config.ARGB_8888);
    }
    //-------------------------------------------------------------------图像处理（结尾）--------------------------------------------------------
    //-------------------------------------------------------------------图像处理（结尾）--------------------------------------------------------


    //---------------------------------------------------------------图片压缩（开始）---------------------------------------------------------------
//---------------------------------------------------------------图片压缩（开始）---------------------------------------------------------------
    //先比例大小压缩
    public static Bitmap compressScale(Bitmap image,float daxiao,int zhiliang) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        // 判断如果图片大于1M,进行压缩避免在生成图片（BitmapFactory.decodeStream）时溢出
        if (baos.toByteArray().length / 1024 > zhiliang) {
            baos.reset();// 重置baos即清空baos
            image.compress(Bitmap.CompressFormat.JPEG, 80, baos);// 这里压缩80%，把压缩后的数据存放到baos中
            if (baos.toByteArray().length / 1024 > zhiliang*1.5) {
                baos.reset();// 重置baos即清空baos
                image.compress(Bitmap.CompressFormat.JPEG, 80, baos);// 二次压缩，这里压缩80%，把压缩后的数据存放到baos中
            }
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        // 开始读入图片，此时把options.inJustDecodeBounds 设回true了
        newOpts.inJustDecodeBounds = true;
        Bitmap bitmap1 = BitmapFactory.decodeStream(isBm, null, newOpts);
        newOpts.inJustDecodeBounds = false;
        int w = newOpts.outWidth;
        int h = newOpts.outHeight;
        // 现在主流手机比较多是800*480分辨率，所以高和宽我们设置为
        // float hh = 800f;// 这里设置高度为800f
        // float ww = 480f;// 这里设置宽度为480f
        float hh = daxiao;
        float ww = daxiao;
        // 缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
        int be = 1;// be=1表示不缩放
        if (w > h && w > ww) {// 如果宽度大的话根据宽度固定大小缩放
            be = (int) (newOpts.outWidth / ww);
        } else if (w < h && h > hh) { // 如果高度高的话根据高度固定大小缩放
            be = (int) (newOpts.outHeight / hh);
        }
        if (be <= 0)
            be = 1;
        newOpts.inSampleSize = be; // 设置缩放比例
        // newOpts.inPreferredConfig = Config.RGB_565;//降低图片从ARGB888到RGB565
        // 重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
        isBm = new ByteArrayInputStream(baos.toByteArray());
        bitmap1 = BitmapFactory.decodeStream(isBm, null, newOpts);
        return compressImage(bitmap1,zhiliang);// 压缩好比例大小后再进行质量压缩
    }
    // 压缩好比例大小后再进行质量压缩
    public static Bitmap compressImage(Bitmap image,int zhiliang) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int options = 90;
        while (baos.toByteArray().length / 1024 > zhiliang) { // 循环判断如果压缩后图片是否大于1000kb,大于继续压缩
            baos.reset(); // 重置baos即清空baos
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);// 这里压缩options%，把压缩后的数据存放到baos中
            options -= 10;// 每次都减少10
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());// 把压缩后的数据baos存放到ByteArrayInputStream中
        Bitmap bitmap1 = BitmapFactory.decodeStream(isBm, null, null);// 把ByteArrayInputStream数据生成图片
        return bitmap1;
    }
//---------------------------------------------------------------图片压缩（结尾）---------------------------------------------------------------
//---------------------------------------------------------------图片压缩（结尾）---------------------------------------------------------------

    //---------------------------------------------------------------菜单功能（开始）---------------------------------------------------------------
//---------------------------------------------------------------菜单功能（开始）---------------------------------------------------------------
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
                Intent intent=new Intent(TuxiangsumiaoActivity.this,BaiduMapActivity.class);
                startActivity(intent);
                break;
            case R.id.tianqi:
                Intent intent1=new Intent(TuxiangsumiaoActivity.this,MainActivity.class);
                startActivity(intent1);
                break;
            case R.id.jishiben:
                Intent intent2=new Intent(TuxiangsumiaoActivity.this,JishibenActivity.class);
                startActivity(intent2);
                break;
            case R.id.fengjing:
                Intent intent3=new Intent(TuxiangsumiaoActivity.this,FengjingliulanActivity.class);
                startActivity(intent3);
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    //---------------------------------------------------------------菜单功能（结尾）---------------------------------------------------------------
//---------------------------------------------------------------菜单功能（结尾）---------------------------------------------------------------

    @Override
    protected void onDestroy() {
        super.onDestroy();
        bitmap.recycle();
    }
}