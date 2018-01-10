package tyj.com.thumbnaildemo.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import tyj.com.thumbnaildemo.R;
import tyj.com.thumbnaildemo.adapter.AlbumAdapter;
import tyj.com.thumbnaildemo.util.bitmap.BitmapUtil;

/**
 * @author ChenYe
 *         created by on 2018/1/10 0010. 10:19
 *         希望在上一个界面就给权限。
 **/

public class AlbumActivity extends Activity {

    private GridView mGridView;

    public static void newInstance(Activity activity, int code) {
        activity.startActivityForResult(new Intent(activity, AlbumActivity.class), code);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album);
        mGridView = (GridView) findViewById(R.id.album_gv);
        initGridView();
    }

    private void initGridView() {
        final AlbumAdapter adapter = new AlbumAdapter(BitmapUtil.getAllPicPath(getApplicationContext()), this);
        mGridView.setAdapter(adapter);
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(AlbumActivity.this, MainActivity.class);
                intent.putExtra(MainActivity.SELECT_PIC, adapter.getUrl(position));
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }
}
