package dev.matrix.video360;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.graphics.SurfaceTexture;
import android.media.MediaPlayer;
import android.opengl.GLES11Ext;
import android.view.Surface;

import com.badlogic.gdx.graphics.GLTexture;

/**
 * @author rostyslav.lesovyi
 */
public class VideoPlayer implements MediaPlayer.OnCompletionListener, SurfaceTexture.OnFrameAvailableListener {

	private int mSurfaceCounter;
	private MediaPlayer mMediaPlayer;
	private GLTexture mGLTexture;
	private SurfaceTexture mSurfaceTexture;

	public VideoPlayer(Context context) {
		AssetFileDescriptor afd = context.getResources().openRawResourceFd(R.raw.video);
		if (afd == null) {
			throw new RuntimeException("unknown resource");
		}

		try {
			mMediaPlayer = new MediaPlayer();
			mMediaPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
			mMediaPlayer.setOnCompletionListener(this);
			mMediaPlayer.prepare();
			mMediaPlayer.setVolume(0, 0);
			mMediaPlayer.start();

			afd.close();
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	public synchronized GLTexture updateTexture() {
		if (mGLTexture == null) {
			mGLTexture = new GLTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES) {
				@Override
				public int getWidth() {
					return mMediaPlayer.getVideoWidth();
				}

				@Override
				public int getHeight() {
					return mMediaPlayer.getVideoHeight();
				}

				@Override
				public int getDepth() {
					return 0;
				}

				@Override
				public boolean isManaged() {
					return false;
				}

				@Override
				protected void reload() {
				}
			};
			mSurfaceTexture = new SurfaceTexture(mGLTexture.getTextureObjectHandle());
			mSurfaceTexture.setOnFrameAvailableListener(this);
			mMediaPlayer.setSurface(new Surface(mSurfaceTexture));
		}
		if (mSurfaceCounter > 0) {
			mSurfaceTexture.updateTexImage();
			--mSurfaceCounter;
		}
		return mGLTexture;
	}

	public void release() {
		mMediaPlayer.release();
		mSurfaceTexture.release();
	}

	@Override
	public void onCompletion(MediaPlayer mp) {
		mMediaPlayer.start();
	}

	@Override
	public synchronized void onFrameAvailable(SurfaceTexture surfaceTexture) {
		++mSurfaceCounter;
	}
}
