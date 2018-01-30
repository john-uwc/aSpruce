package uwc.android.spruce.widget.iv;

/**
 * Created by steven on 14/09/2017.
 */

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.SurfaceTexture;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Surface;
import android.view.TextureView;

import java.io.File;

/**
 * Created by arthur on 17/1/24.
 */
public class VideoView extends TextureView implements TextureView.SurfaceTextureListener {

    private final String TAG = getClass().getSimpleName();

    private int fixedWidth;
    private int fixedHeight;

    private boolean isPlaying = false;
    private MediaPlayer mediaPlayer;
    private Surface surface;
//    private boolean isSurfaceTextureAvailable = true;
    private String videoPath;

    private Matrix  matrix;

    private MediaPlayer.OnVideoSizeChangedListener mSizeChangedListener =
            new MediaPlayer.OnVideoSizeChangedListener() {
                public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
                    int videoWidth = mp.getVideoWidth();
                    int videoHeight = mp.getVideoHeight();
                    if (videoWidth != 0 && videoHeight != 0) {
                        getSurfaceTexture().setDefaultBufferSize(videoWidth, videoHeight);
                        requestLayout();
                        transformVideo(videoWidth, videoHeight);
                        Log.d(TAG, String.format("OnVideoSizeChangedListener, mVideoWidth=%d,mVideoHeight=%d", videoWidth, videoHeight));
                    }
                }
            };

    public VideoView(Context context) {
        this(context, null);
    }

    public VideoView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VideoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setSurfaceTextureListener(this);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (fixedWidth == 0 || fixedHeight == 0) {
            setMeasuredDimension(getDefaultSize(0,widthMeasureSpec),getDefaultSize(0,heightMeasureSpec));
        } else {
            setMeasuredDimension(fixedWidth, fixedHeight);
        }
        Log.d(TAG, String.format("onMeasure, fixedWidth=%d,fixedHeight=%d", fixedWidth, fixedHeight));
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int i, int i1) {
        surface = new Surface(surfaceTexture);
//        isSurfaceTextureAvailable = true;
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int i, int i1) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
        if (mediaPlayer != null) {
            stopPlay();mediaPlayer.release();
        }
        surface = null;
        isPlaying = false;
        return true;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {

    }

    public void setVideoPath(String path) {
        videoPath = path;
    }

    public void setFixedSize(int width, int height) {
        fixedHeight = height;
        fixedWidth = width;
        Log.d(TAG, "setFixedSize,width=" + width + "height=" + height);
        requestLayout();
    }

    public void startPlay() {
//        if (isPlaying || !isSurfaceTextureAvailable)
//            return;
        if (isPlaying)
            return;
        if (TextUtils.isEmpty(videoPath)) {
            Log.e(TAG,"视频路径异常是空~");
            return;
        }
        try {
            final File file = new File(videoPath);

            if (!file.exists()) {//文件不存在
                Log.e(TAG, "视频文件不存在" + videoPath);
                return;
            }
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(file.getAbsolutePath());
            mediaPlayer.setOnVideoSizeChangedListener(mSizeChangedListener);
            mediaPlayer.setSurface(surface);
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            //mediaPlayer.setVolume(0, 0); //设置左右音道的声音为0
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mediaPlayer.start();
                    isPlaying = true;
                }
            });
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    stopPlay();
                }
            });
            mediaPlayer.prepareAsync();
        } catch (Exception e) {
            Log.e(TAG,"videoView:" + e.toString());
            e.printStackTrace();
        }
    }

    public void stopPlay() {
        if (mediaPlayer != null && isPlaying) {
            mediaPlayer.stop();
            mediaPlayer.reset();
        }
        isPlaying = false;
    }

    public void mute(boolean enable){
        if(null == mediaPlayer)
            return;
        mediaPlayer.setVolume(enable ? 0f : 1f, enable ? 0f : 1f);
    }

    public boolean getPlayStatus() {
        return isPlaying;
    }

    //需求:视频等比例放大,直至一边铺满View的某一边,另一边超出View的另一边,再移动到View的正中央,这样长边两边会被裁剪掉同样大小的区域,视频看起来不会变形
    //也即是:先把视频区(实际的大小显示区)与View(定义的大小)区的两个中心点重合, 然后等比例放大或缩小视频区,直至一条边与View的一条边相等,另一条边超过
    //View的另一条边,这时再裁剪掉超出的边, 使视频区与View区大小一样. 这样在不同尺寸的手机上,视频看起来不会变形,只是水平或竖直方向的两端被裁剪了一些.
    private void transformVideo(int videoWidth, int videoHeight) {
        if (getResizedHeight() == 0 || getResizedWidth() == 0) {
            Log.d(TAG, "transformVideo, getResizedHeight=" + getResizedHeight() + "," + "getResizedWidth=" + getResizedWidth());
            return;
        }
        float sx = (float) getResizedWidth() / (float) videoWidth;
        float sy = (float) getResizedHeight() / (float) videoHeight;
        Log.d(TAG, "transformVideo, sx=" + sx);
        Log.d(TAG, "transformVideo, sy=" + sy);

        float maxScale = Math.max(sx, sy);
        if (this.matrix == null) {
            matrix = new Matrix();
        } else {
            matrix.reset();
        }

        //第2步:把视频区移动到View区,使两者中心点重合.
        matrix.preTranslate((getResizedWidth() - videoWidth) / 2, (getResizedHeight() - videoHeight) / 2);

        //第1步:因为默认视频是fitXY的形式显示的,所以首先要缩放还原回来.
        matrix.preScale(videoWidth / (float) getResizedWidth(), videoHeight / (float) getResizedHeight());

        //第3步,等比例放大或缩小,直到视频区的一边超过View一边, 另一边与View的另一边相等. 因为超过的部分超出了View的范围,所以是不会显示的,相当于裁剪了.
        matrix.postScale(maxScale, maxScale, getResizedWidth() / 2, getResizedHeight() / 2);//后两个参数坐标是以整个View的坐标系以参考的

        Log.d(TAG, "transformVideo, maxScale=" + maxScale);

        setTransform(matrix);
        postInvalidate();
        Log.d(TAG, "transformVideo, videoWidth=" + videoWidth + "," + "videoHeight=" + videoHeight);
    }

    private int getResizedWidth() {
        if (fixedWidth == 0) {
            return getWidth();
        } else {
            return fixedWidth;
        }
    }

    private int getResizedHeight() {
        if (fixedHeight== 0) {
            return getHeight();
        } else {
            return fixedHeight;
        }
    }
}
