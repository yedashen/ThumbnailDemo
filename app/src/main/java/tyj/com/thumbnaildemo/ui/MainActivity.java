package tyj.com.thumbnaildemo.ui;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.io.File;

import pub.devrel.easypermissions.EasyPermissions;
import tyj.com.thumbnaildemo.App;
import tyj.com.thumbnaildemo.R;
import tyj.com.thumbnaildemo.util.bitmap.BitmapUtil;
import tyj.com.thumbnaildemo.util.file.FileUtil;
import tyj.com.thumbnaildemo.view.dialog.SelectPicDialog;

/**
 * @author ChenYe
 *         老铁，记得给权限啊，我没写很多的权限校验代码。
 */
public class MainActivity extends AppCompatActivity {
    private static final String TAG = "====MainActivity=====";
    private String mSdFileAbsolutePath = App.mShareInstance.getExternalFilesDir(null).getAbsolutePath();
    private static final int PERMISSION_CODE = 200;
    public static final String SELECT_PIC = "selectUrl";
    public static final int REQUEST_CODE = 38;
    private ImageView mSourceIv;
    private ImageView mThumbnailIv;
    private SelectPicDialog mSelectPicDialog = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mSourceIv = (ImageView) findViewById(R.id.source_iv);
        mThumbnailIv = (ImageView) findViewById(R.id.thumbnail_iv);
        requestPermission();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE && data != null) {
            showThumbPic(data.getStringExtra(SELECT_PIC));
        }
    }

    private void showThumbPic(String sourcePath) {
        Log.e(TAG, "原图路径为: " + sourcePath);
        Glide.with(this).load(sourcePath).into(mSourceIv);
        File sourPathFile = new File(sourcePath);
        try {
            long sourPathFileSize = FileUtil.getFileSize(sourPathFile);
            String sourPathFileformetSize = FileUtil.FormatFileSize(sourPathFileSize);
            Log.e(TAG, "原图大小为:" + sourPathFileformetSize);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //这块通过图片sourPathpath查找缩略图的路径
        String thumbnailPath = BitmapUtil.queryImageThumbnailByPath(this, sourcePath);
        Log.e(TAG, "根据原图查询到的缩略图路径为: " + thumbnailPath);
        if (TextUtils.isEmpty(thumbnailPath)) {
            //缩略图路径不存在
            Glide.with(this).load(BitmapUtil.createThumbnail(sourcePath, mSdFileAbsolutePath)).into(mThumbnailIv);
        } else {
            //通过缩略图路径存在
            getThumbnail(sourcePath, thumbnailPath);
        }
    }

    /**
     * 通过缩略图路径加载缩略图
     *
     * @param thumbnailPath
     */
    private void getThumbnail(String sourPath, String thumbnailPath) {
        File thumbFile = new File(thumbnailPath);
        try {
            //如果thumFileSize等于0代表缩略图地址是无效的路径，我们就生成缩略图
            if (thumbFile.length() == 0) {
                Glide.with(this).load(BitmapUtil.createThumbnail(sourPath, mSdFileAbsolutePath)).into(mThumbnailIv);
                return;
            } else {
                Glide.with(this).load(thumbnailPath).into(mThumbnailIv);
            }
            String thumFileFormetSize = FileUtil.FormatFileSize(thumbFile.length());
            Log.e(TAG, "根据原图找到的缩略图大小为:" + thumFileFormetSize);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void requestPermission() {
        if (!EasyPermissions.hasPermissions(this, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            EasyPermissions.requestPermissions(this, "需要读取SD卡权限", PERMISSION_CODE, Manifest.permission.READ_EXTERNAL_STORAGE
                    , Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
    }

    public void showSelect(View view) {
        if (mSelectPicDialog == null) {
            mSelectPicDialog = new SelectPicDialog(this);
            mSelectPicDialog.setItemClickListener(new SelectPicDialog.SelectPicItemClickListener() {
                @Override
                public void clickTakePhoto() {

                }

                @Override
                public void clickGoAlbum() {
                    AlbumActivity.newInstance(MainActivity.this, REQUEST_CODE);
                }
            });
        }
        mSelectPicDialog.show();
    }
}
