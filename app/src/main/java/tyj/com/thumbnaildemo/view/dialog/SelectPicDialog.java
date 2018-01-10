package tyj.com.thumbnaildemo.view.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.widget.TextView;

import tyj.com.thumbnaildemo.R;


/**
 * @author chenye
 */

public class SelectPicDialog extends Dialog {

    private Activity activity;
    private SelectPicItemClickListener listener;

    private TextView mTakePhotoTv;
    private TextView mGoAlbumTv;
    private TextView mCancelTv;

    public SelectPicDialog(Activity activity) {
        super(activity, R.style.CustomDialog);
        this.activity = activity;
        View view = View.inflate(activity, R.layout.item_show_camera_album, null);
        mTakePhotoTv = view.findViewById(R.id.go_camera_tv);
        mGoAlbumTv = view.findViewById(R.id.go_album_tv);
        mCancelTv = view.findViewById(R.id.cancel_tv);
        setContentView(view);
        setCancelable(true);
        initClicks();
        Window window = getWindow();
        window.setWindowAnimations(R.style.animStyle);
        LayoutParams wl = window.getAttributes();
        wl.x = 0;
        wl.y = activity.getWindowManager().getDefaultDisplay().getHeight();
        wl.width = ViewGroup.LayoutParams.MATCH_PARENT;
        wl.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        onWindowAttributesChanged(wl);
        setOnDismissListener(mOnDismissListener);
        setOnShowListener(mOnShowListener);
    }

    private OnDismissListener mOnDismissListener = new OnDismissListener() {
        @Override
        public void onDismiss(DialogInterface dialogInterface) {
            backgroundAlpha(activity, 1.0f);
        }
    };

    private OnShowListener mOnShowListener = new OnShowListener() {
        @Override
        public void onShow(DialogInterface dialog) {
            backgroundAlpha(activity, 0.7f);
        }
    };

    private void initClicks() {
        mTakePhotoTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                if (listener != null) {
                    listener.clickTakePhoto();
                }
            }
        });
        mGoAlbumTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                if (listener != null) {
                    listener.clickGoAlbum();
                }
            }
        });
        mCancelTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }


    public interface SelectPicItemClickListener {
        /**
         * 跳转到拍照界面
         */
        void clickTakePhoto();

        /**
         * 跳转到自定义的相册界面
         */
        void clickGoAlbum();
    }

    public void setItemClickListener(SelectPicItemClickListener listener) {
        this.listener = listener;
    }

    public void backgroundAlpha(Activity activity, float bgAlpha) {
        LayoutParams lp = activity.getWindow().getAttributes();
        //0.0-1.0
        lp.alpha = bgAlpha;
        activity.getWindow().setAttributes(lp);
    }
}
