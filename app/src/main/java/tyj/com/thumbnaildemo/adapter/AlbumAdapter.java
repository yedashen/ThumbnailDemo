package tyj.com.thumbnaildemo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.List;

import tyj.com.thumbnaildemo.R;

/**
 * @author ChenYe
 *         created by on 2017/12/20 0020. 14:01
 **/

public class AlbumAdapter extends BaseAdapter {

    private List<String> mPicUrls;
    private Context mContext;
    private LayoutInflater mInflater;

    public AlbumAdapter(List<String> picUrls, Context context) {
        this.mPicUrls = picUrls;
        this.mContext = context;
        mInflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getCount() {
        return mPicUrls == null ? 0 : mPicUrls.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_album_adapter, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.updateView(position);
        return convertView;
    }

    class ViewHolder {
        private ImageView mIv;

        public ViewHolder(View view) {
            mIv = (ImageView) view.findViewById(R.id.item_iv);
        }

        public void updateView(int position) {
            Glide.with(mContext)
                    .load("file://" + mPicUrls.get(position))
                    .asBitmap()
                    .into(mIv);
        }
    }

    public String getUrl(int position) {
        return mPicUrls.get(position);
    }
}
