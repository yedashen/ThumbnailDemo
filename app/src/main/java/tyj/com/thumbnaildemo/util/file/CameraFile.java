package tyj.com.thumbnaildemo.util.file;

import android.os.Environment;
import android.util.Log;

import java.io.File;

/**
 * @author ChenYe
 *         created by on 2018/1/10 0010. 10:54
 *         与camera相关的，其实就是存储拍照的图片到自己指定的文件夹下
 **/

public class CameraFile {

    public static File getOutputMediaFile() {
        //下面需要sd卡的读、写权限
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                "Cy测试");
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.e("CameraFile", "创建文件夹失败");
                return null;
            }
        }
        File mediaFile = new File(mediaStorageDir.getPath() +
                File.separator + "IMG_" + System.currentTimeMillis() + ".jpg");
        return mediaFile;
    }

    /**
     * 目前就我知道的是：当你跳转到相机界面之前你要设置一个file（文件A）来给拍照的时候存图片用的，但是如果你进入到了拍照
     * 界面直接点击返回（就是没拍照），那么去判断这个文件是否存在得到的结果是“文件A不存在”，如果你拍照了，那么判断
     * 文件A是否存在的结果是“文件A存在”，由此我用文件A是否存在来判断拍照是否成功
     *
     * @param path
     * @return
     */
    public static boolean haveTakePhoto(String path) {
        File file = new File(path);
        return file.exists();
    }
}
