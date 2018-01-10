package tyj.com.thumbnaildemo.util.bitmap;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import tyj.com.thumbnaildemo.App;
import tyj.com.thumbnaildemo.util.file.FileUtil;

/**
 * @author ChenYe
 */
public class BitmapUtil {

    /**
     * 获取所有图片路径
     *
     * @param context
     * @return
     */
    public static List<String> getAllPicPath(Context context) {
        List<String> urls = new ArrayList<>();
        ContentResolver contentResolver = context.getContentResolver();
        Cursor mCursor = contentResolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Images.ImageColumns.DATA}, "", null,
                MediaStore.MediaColumns.DATE_ADDED + " DESC");
        if (mCursor.moveToFirst()) {
            int _date = mCursor.getColumnIndex(MediaStore.Images.Media.DATA);
            do {
                // 获取图片的路径
                String path = mCursor.getString(_date);
                Boolean has = false;
                if (has) {
                    continue;
                }
                urls.add(path);
            } while (mCursor.moveToNext());
        }
        mCursor.close();
        Log.e("BitmapUtil", "当前手机共有照片" + urls.size() + "张");
        return urls;
    }

    /**
     * 通过传过来的图片路径去返回该图片的缩略图路径，如果不存在缩略图
     * 就返回null
     *
     * @param context
     * @param path
     * @return
     */
    public static String queryImageThumbnailByPath(Context context, String path) {
        Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        String[] projection = new String[]{MediaStore.Images.Media._ID};
        String selection = MediaStore.Images.Media.DATA + " = ? ";
        String[] selectionArgs = new String[]{path};

        Cursor cursor = query(context, uri, projection, selection,
                selectionArgs);
        int id = -1;
        if (cursor.moveToFirst()) {
            id = cursor.getInt(cursor.getColumnIndex(MediaStore.Images.Media._ID));
        }
        cursor.close();
        if (id == -1) {
            return null;
        }

        uri = MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI;
        projection = new String[]{MediaStore.Images.Thumbnails.DATA};
        selection = MediaStore.Images.Thumbnails.IMAGE_ID + " = ? ";
        selectionArgs = new String[]{String.valueOf(id)};

        cursor = query(context, uri, projection, selection, selectionArgs);
        String thumbnail = null;
        if (cursor.moveToFirst()) {
            int idxData = cursor.getColumnIndex(MediaStore.Images.Thumbnails.DATA);
            thumbnail = cursor.getString(idxData);
        }
        cursor.close();
        return thumbnail;
    }

    private static Cursor query(Context context, Uri uri, String[] projection,
                                String selection, String[] selectionArgs) {
        ContentResolver cr = context.getContentResolver();
        Cursor cursor = cr.query(uri, projection, selection, selectionArgs,
                null);
        return cursor;
    }

    /**
     * 可用于生成缩略图
     * Creates a centered bitmap of the desired size. Recycles the input.
     *
     * @param source
     */
    public static Bitmap extractMiniThumb(Bitmap source, int width, int height) {
        return extractMiniThumb(source, width, height, true);
    }

    public static Bitmap extractMiniThumb(Bitmap source, int width, int height,
                                          boolean recycle) {
        if (source == null) {
            return null;
        }

        float scale;
        if (source.getWidth() < source.getHeight()) {
            scale = width / (float) source.getWidth();
        } else {
            scale = height / (float) source.getHeight();
        }
        Matrix matrix = new Matrix();
        matrix.setScale(scale, scale);
        Bitmap miniThumbnail = transform(matrix, source, width, height, false);

        if (recycle && miniThumbnail != source) {
            source.recycle();
        }
        return miniThumbnail;
    }

    public static Bitmap transform(Matrix scaler, Bitmap source,
                                   int targetWidth, int targetHeight, boolean scaleUp) {
        int deltaX = source.getWidth() - targetWidth;
        int deltaY = source.getHeight() - targetHeight;
        if (!scaleUp && (deltaX < 0 || deltaY < 0)) {
            /*
             * In this case the bitmap is smaller, at least in one dimension,
             * than the target. Transform it by placing as much of the image as
             * possible into the target and leaving the top/bottom or left/right
             * (or both) black.
             */
            Bitmap b2 = Bitmap.createBitmap(targetWidth, targetHeight,
                    Bitmap.Config.ARGB_8888);
            Canvas c = new Canvas(b2);

            int deltaXHalf = Math.max(0, deltaX / 2);
            int deltaYHalf = Math.max(0, deltaY / 2);
            Rect src = new Rect(deltaXHalf, deltaYHalf, deltaXHalf
                    + Math.min(targetWidth, source.getWidth()), deltaYHalf
                    + Math.min(targetHeight, source.getHeight()));
            int dstX = (targetWidth - src.width()) / 2;
            int dstY = (targetHeight - src.height()) / 2;
            Rect dst = new Rect(dstX, dstY, targetWidth - dstX, targetHeight
                    - dstY);
            c.drawBitmap(source, src, dst, null);
            return b2;
        }
        float bitmapWidthF = source.getWidth();
        float bitmapHeightF = source.getHeight();

        float bitmapAspect = bitmapWidthF / bitmapHeightF;
        float viewAspect = (float) targetWidth / targetHeight;

        if (bitmapAspect > viewAspect) {
            float scale = targetHeight / bitmapHeightF;
            if (scale < .9F || scale > 1F) {
                scaler.setScale(scale, scale);
            } else {
                scaler = null;
            }
        } else {
            float scale = targetWidth / bitmapWidthF;
            if (scale < .9F || scale > 1F) {
                scaler.setScale(scale, scale);
            } else {
                scaler = null;
            }
        }

        Bitmap b1;
        if (scaler != null) {
            // this is used for minithumb and crop, so we want to filter here.
            b1 = Bitmap.createBitmap(source, 0, 0, source.getWidth(),
                    source.getHeight(), scaler, true);
        } else {
            b1 = source;
        }

        int dx1 = Math.max(0, b1.getWidth() - targetWidth);
        int dy1 = Math.max(0, b1.getHeight() - targetHeight);

        Bitmap b2 = Bitmap.createBitmap(b1, dx1 / 2, dy1 / 2, targetWidth,
                targetHeight);

        if (b1 != source) {
            b1.recycle();
        }
        return b2;
    }

    /**
     * 获取bitmap大小
     *
     * @param bitmap
     * @return
     */
    public static int getBitmapSize(Bitmap bitmap) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {    //API 19
            return bitmap.getAllocationByteCount();
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {//API 12
            return bitmap.getByteCount();
        }
        return bitmap.getRowBytes() * bitmap.getHeight();                //earlier version
    }

    /**
     * 将Bitmap转换成文件
     * 保存文件
     *
     * @param bm
     * @param fileName
     * @throws IOException
     */
    public static File saveFile(Bitmap bm, String path, String fileName) {
        File dirFile = new File(path);
        if (!dirFile.exists()) {
            dirFile.mkdir();
        }
        File myCaptureFile = new File(path, fileName);
        BufferedOutputStream bos = null;
        try {
            bos = new BufferedOutputStream(new FileOutputStream(myCaptureFile));
            bm.compress(Bitmap.CompressFormat.JPEG, 80, bos);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("File", "保存缩略图出错啦");
            return null;
        } finally {
            try {
                if (bos != null) {
                    bos.flush();
                    bos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return myCaptureFile;
    }

    /**
     * 因为原图的缩略图不存在或者找不到，所以就调用这个方法，用原图来自己创建缩略图。
     *
     * @param sourPath     原图路径
     * @param tempSavePath 临时保存缩略的路径
     * @return 创建的缩略图byte[]
     */
    public static byte[] createThumbnail(String sourPath, String tempSavePath) {
        Bitmap sourBitmap = BitmapFactory.decodeFile(sourPath);
        Bitmap thumBitmap = BitmapUtil.extractMiniThumb(sourBitmap, 200, 200);
        //下面的saveFile 的第三个参数，是临时保存的缩略的名字，可以随便取，因为一旦有新的缩略图产生就
        //会覆盖掉
        File thumFile = BitmapUtil.saveFile(thumBitmap, tempSavePath, "test");
        if (thumFile != null) {
            String fileSize = FileUtil.FormatFileSize(thumFile.length());
            Log.e("BitmapUtil", "创建的缩略图的大小:" + fileSize);
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        thumBitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] bytes = baos.toByteArray();
        Toast.makeText(App.mShareInstance, "没有找到缩略图，当前的缩略图是临时生成的", Toast.LENGTH_SHORT).show();
        return bytes;
    }
}
