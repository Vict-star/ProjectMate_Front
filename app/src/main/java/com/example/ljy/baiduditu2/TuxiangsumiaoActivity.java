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
        //��ʼ�����
        Button takephoto = (Button) findViewById(R.id.takephotos);
        Button choosephoto = (Button) findViewById(R.id.choosephotos);
        Button baocun = (Button) findViewById(R.id.baocun);
        seekBar=(SeekBar) findViewById(R.id.sumiaoshendu);
        picture1 = (ImageView) findViewById(R.id.picture1);
        //takephoto��ť������,paizhao()������������
        takephoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                paizhao();//����
            }
        });
        //choosephoto��ť��������openAlbum()���������
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
                    openAlbum();//�����
                }
            }
        });
        //baocun��ť��������openAlbum()�����������
        baocun.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bitmap!=null){
                    saveBitmap(bitmap, "111.JPEG");
                }else {
                    Toast.makeText(TuxiangsumiaoActivity.this, "�����ջ�ѡ����Ƭ", Toast.LENGTH_SHORT).show();
                }
            }
        });
        //seekBar�ı������
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (bitmap!=null){
                    bitmap=compressScale(bitmap,480f,20);  //ѹ��ͼƬ
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
    //����
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
    // �����
    private void openAlbum() {
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        startActivityForResult(intent, CHOOSE_PHOTO); // �����
    }
    //���浽���
    public void saveBitmap(Bitmap bitmap1, String bitName){
        String fileName ;
        File file ;
        if(Build.BRAND .equals("Xiaomi") ){ // С���ֻ�
            fileName = Environment.getExternalStorageDirectory().getPath()+"/DCIM/Camera/"+bitName ;
        }else{ // Meizu ��Oppo
            fileName = Environment.getExternalStorageDirectory().getPath()+"/DCIM/"+bitName ;
        }
        file = new File(fileName);
        if(file.exists()){
            file.delete();
        }
        FileOutputStream out;
        try{
            out = new FileOutputStream(file);
            // ��ʽΪ JPEG��������ĳ���ͼƬΪJPEG��ʽ�ģ�PNG��ʽ�Ĳ�����ʾ�������
            if(bitmap1.compress(Bitmap.CompressFormat.JPEG, 90, out))
            {
                out.flush();
                out.close();
// ����ͼ��
                MediaStore.Images.Media.insertImage(this.getContentResolver(), file.getAbsolutePath(), bitName, null);
                Toast.makeText(TuxiangsumiaoActivity.this, "�ѱ��浽���", Toast.LENGTH_SHORT).show();
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
        // ���͹㲥��֪ͨˢ��ͼ�����ʾ
        this.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + fileName)));
        bitmap1=null;
    }

    //���� paizhao()�ʹ����openAlbum()������startActivityForResult�Ļص�
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {
            case TAKE_PHOTO:
                if (resultCode == RESULT_OK) {
                    try {
                        // ���������Ƭ��ʾ����
//---------------------------------------------------------------���յĳ���ӿ�---------------------------------------------------------------
//---------------------------------------------------------------���յĳ���ӿ�---------------------------------------------------------------
                        bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
                        bitmap=compressScale(bitmap,1024f,1000);  //ѹ��ͼƬ
                        bitmap = createPencli(bitmap, sumiaoprogress, sumiaoprogress);
                        picture1.setImageBitmap(bitmap);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
            case CHOOSE_PHOTO:
                if (resultCode == RESULT_OK) {
                    // �ж��ֻ�ϵͳ�汾��
                    if (Build.VERSION.SDK_INT >= 19) {
                        // 4.4������ϵͳʹ�������������ͼƬ
                        handleImageOnKitKat(data,sumiaoprogress);
                    } else {
                        // 4.4����ϵͳʹ�������������ͼƬ
                        handleImageBeforeKitKat(data,sumiaoprogress);
                    }
                }
                break;
            default:
                break;
        }
    }
    //����᣺����ͼƬ��URI�ҵ�ͼƬ��ʵλ�ã�4.4��19������ϵͳ
    @TargetApi(19)
    private void handleImageOnKitKat(Intent data,int progress) {
        String imagePath = null;
        Uri uri = data.getData();
        Log.d("TAG", "handleImageOnKitKat: uri is " + uri);
        if (DocumentsContract.isDocumentUri(this, uri)) {
            // �����document���͵�Uri����ͨ��document id����
            String docId = DocumentsContract.getDocumentId(uri);
            if ("com.android.providers.media.documents".equals(uri.getAuthority())) {
                String id = docId.split(":")[1]; // ���������ָ�ʽ��id
                String selection = MediaStore.Images.Media._ID + "=" + id;
                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection);
            } else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())) {
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(docId));
                imagePath = getImagePath(contentUri, null);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            // �����content���͵�Uri����ʹ����ͨ��ʽ����
            imagePath = getImagePath(uri, null);
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            // �����file���͵�Uri��ֱ�ӻ�ȡͼƬ·������
            imagePath = uri.getPath();
        }
        displayImage(imagePath,progress); // ����ͼƬ·����ʾͼƬ
    }
    //����᣺����ͼƬ��URI�ҵ�ͼƬ��ʵλ�ã�4.4��19������ϵͳ
    private void handleImageBeforeKitKat(Intent data,int progress) {
        Uri uri = data.getData();
        String imagePath = getImagePath(uri, null);
        displayImage(imagePath,progress);
    }
    // ͨ��Uri��selection����ȡ��ʵ��ͼƬ·��
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
    //������е���Ƭ��ʾ����Ļ
    private void displayImage(String imagePath,int progress) {
        if (imagePath != null) {
//------------------------------------------------------------������е���Ƭ��ʾ����Ļ����ӿ�---------------------------------------------------------------
//------------------------------------------------------------������е���Ƭ��ʾ����Ļ����ӿ�---------------------------------------------------------------
            bitmap = BitmapFactory.decodeFile(imagePath);
            bitmap=compressScale(bitmap,1024f,1000);  //ѹ��ͼƬ
            bitmap= createPencli(bitmap, progress, progress);
            picture1.setImageBitmap(bitmap);
        } else {
            Toast.makeText(this, "failed to get image", Toast.LENGTH_SHORT).show();
        }
    }

    //-------------------------------------------------------------------ͼ������ʼ��--------------------------------------------------------
    //-------------------------------------------------------------------ͼ������ʼ��--------------------------------------------------------
    //�ܷ���������1-5���ķ�����a��b��Ϊ����ģ���Ĳ���
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
    //��һ����ȥɫ����ѹ���õ�ͼƬ��Ϊ�Ҷ�ͼ�����ڰ�ͼ
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
    //(�ڶ�����ȡ��ɫ����ȥɫ�Ҷ�ͼȡ��ɫ���õ�ÿ�����صĲ�ɫ��R(��) = 255 - R
    public int[] getInverse(int[] gray) {
        int[] inverse = new int[gray.length];

        for (int i = 0, size = gray.length; i < size; i++) {
            inverse[i] = 255 - gray[i];
        }
        return inverse;
    }
    //����������˹ģ�����ѷ���������ֵƽ��һ�¡�
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
    //���Ĳ�����ɫ������ϣ�����ȥɫ������غ͸�˹ģ���õ�������ֵ���м�������㹫ʽ�����ɫ = ��ɫ + (���ɫ * ��ɫ) / (255 - ���ɫ)
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
    //���Ĳ�����ɫ������ϵ��ӷ���
    private static int colorDodgeFormular(int base, int mix) {
        int result = base + (base * mix) / (255 - mix);
        result = result > 255 ? 255 : result;
        return result;
    }
    //���һ�����岽������ͼ��
    public Bitmap create(int[] pixels, int[] output, int width, int height) {
        for (int i = 0, size = pixels.length; i < size; i++) {
            int gray = output[i];
            int pixel = (pixels[i] & 0xff000000) | (gray << 16) | (gray << 8) | gray;//ע�����ԭͼ�� alphaͨ��

            output[i] = pixel;
        }
        return Bitmap.createBitmap(output, width, height, Bitmap.Config.ARGB_8888);
    }
    //-------------------------------------------------------------------ͼ������β��--------------------------------------------------------
    //-------------------------------------------------------------------ͼ������β��--------------------------------------------------------


    //---------------------------------------------------------------ͼƬѹ������ʼ��---------------------------------------------------------------
//---------------------------------------------------------------ͼƬѹ������ʼ��---------------------------------------------------------------
    //�ȱ�����Сѹ��
    public static Bitmap compressScale(Bitmap image,float daxiao,int zhiliang) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        // �ж����ͼƬ����1M,����ѹ������������ͼƬ��BitmapFactory.decodeStream��ʱ���
        if (baos.toByteArray().length / 1024 > zhiliang) {
            baos.reset();// ����baos�����baos
            image.compress(Bitmap.CompressFormat.JPEG, 80, baos);// ����ѹ��80%����ѹ��������ݴ�ŵ�baos��
            if (baos.toByteArray().length / 1024 > zhiliang*1.5) {
                baos.reset();// ����baos�����baos
                image.compress(Bitmap.CompressFormat.JPEG, 80, baos);// ����ѹ��������ѹ��80%����ѹ��������ݴ�ŵ�baos��
            }
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        // ��ʼ����ͼƬ����ʱ��options.inJustDecodeBounds ���true��
        newOpts.inJustDecodeBounds = true;
        Bitmap bitmap1 = BitmapFactory.decodeStream(isBm, null, newOpts);
        newOpts.inJustDecodeBounds = false;
        int w = newOpts.outWidth;
        int h = newOpts.outHeight;
        // ���������ֻ��Ƚ϶���800*480�ֱ��ʣ����ԸߺͿ���������Ϊ
        // float hh = 800f;// �������ø߶�Ϊ800f
        // float ww = 480f;// �������ÿ��Ϊ480f
        float hh = daxiao;
        float ww = daxiao;
        // ���űȡ������ǹ̶��������ţ�ֻ�ø߻��߿�����һ�����ݽ��м��㼴��
        int be = 1;// be=1��ʾ������
        if (w > h && w > ww) {// �����ȴ�Ļ����ݿ�ȹ̶���С����
            be = (int) (newOpts.outWidth / ww);
        } else if (w < h && h > hh) { // ����߶ȸߵĻ����ݸ߶ȹ̶���С����
            be = (int) (newOpts.outHeight / hh);
        }
        if (be <= 0)
            be = 1;
        newOpts.inSampleSize = be; // �������ű���
        // newOpts.inPreferredConfig = Config.RGB_565;//����ͼƬ��ARGB888��RGB565
        // ���¶���ͼƬ��ע���ʱ�Ѿ���options.inJustDecodeBounds ���false��
        isBm = new ByteArrayInputStream(baos.toByteArray());
        bitmap1 = BitmapFactory.decodeStream(isBm, null, newOpts);
        return compressImage(bitmap1,zhiliang);// ѹ���ñ�����С���ٽ�������ѹ��
    }
    // ѹ���ñ�����С���ٽ�������ѹ��
    public static Bitmap compressImage(Bitmap image,int zhiliang) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);// ����ѹ������������100��ʾ��ѹ������ѹ��������ݴ�ŵ�baos��
        int options = 90;
        while (baos.toByteArray().length / 1024 > zhiliang) { // ѭ���ж����ѹ����ͼƬ�Ƿ����1000kb,���ڼ���ѹ��
            baos.reset(); // ����baos�����baos
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);// ����ѹ��options%����ѹ��������ݴ�ŵ�baos��
            options -= 10;// ÿ�ζ�����10
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());// ��ѹ���������baos��ŵ�ByteArrayInputStream��
        Bitmap bitmap1 = BitmapFactory.decodeStream(isBm, null, null);// ��ByteArrayInputStream��������ͼƬ
        return bitmap1;
    }
//---------------------------------------------------------------ͼƬѹ������β��---------------------------------------------------------------
//---------------------------------------------------------------ͼƬѹ������β��---------------------------------------------------------------

    //---------------------------------------------------------------�˵����ܣ���ʼ��---------------------------------------------------------------
//---------------------------------------------------------------�˵����ܣ���ʼ��---------------------------------------------------------------
    //�����˵���Դ�ļ�
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.actionbar,menu);  //�����˵��ļ�
        return super.onCreateOptionsMenu(menu);
    }
    // �ò˵�ͬʱ��ʾͼ�������
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
    //���ò˵�ѡ�к�Ķ���
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){//�ж�ѡ����һ��
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
    //---------------------------------------------------------------�˵����ܣ���β��---------------------------------------------------------------
//---------------------------------------------------------------�˵����ܣ���β��---------------------------------------------------------------

    @Override
    protected void onDestroy() {
        super.onDestroy();
        bitmap.recycle();
    }
}