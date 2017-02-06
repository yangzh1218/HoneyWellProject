/*
 *  Copyright (c) 2012 The WebRTC project authors. All Rights Reserved.
 *
 *  Use of this source code is governed by a BSD-style license
 *  that can be found in the LICENSE file in the root of the source
 *  tree. An additional intellectual property rights grant can be found
 *  in the file PATENTS.  All contributing project authors may
 *  be found in the AUTHORS file in the root of the source tree.
 */

package org.webrtc.videoengine;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.opengl.GLSurfaceView;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.wulian.siplibrary.utils.WulianLog;

import java.io.FileOutputStream;
import java.util.concurrent.locks.ReentrantLock;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.opengles.GL10;

@TargetApi(5)
public class ViEAndroidGLES20 extends GLSurfaceView implements
		GLSurfaceView.Renderer {
	private static String TAG = "WEBRTC-JR";
	private static final boolean DEBUG = false;
	// True if onSurfaceCreated has been called.
	private boolean surfaceCreated = false;
	private boolean openGLCreated = false;
	private boolean isSupportReverse = false;
	// True if NativeFunctionsRegistered has been called.
	private boolean nativeFunctionsRegisted = false;
	private ReentrantLock nativeFunctionLock = new ReentrantLock();
	// Address of Native object that will do the drawing.
	private long nativeObject = 0;
	private int viewWidth = 0;
	private int viewHeight = 0;

	/********************* PML TEST THE Capture screen *************/
	private static Handler mHandler;
	private static boolean isTakePic;
	private static boolean isReturnPictureState;
	private static String sFileDir;
	private static boolean isSaveiPicture;

	/********************* PML *************/

	// private ViESurfaceRenderer ViewRender;

	public static boolean UseOpenGL2(Object renderWindow) {
		return ViEAndroidGLES20.class.isInstance(renderWindow);
	}

	public ViEAndroidGLES20(Context context) {
		super(context);
		this.isSupportReverse = false;
		init(false, 0, 0);
	}

	public ViEAndroidGLES20(Context context, boolean isSupportReverse) {
		super(context);
		this.isSupportReverse = isSupportReverse;
		init(false, 0, 0);
	}

	public ViEAndroidGLES20(Context context, boolean translucent, int depth,
			int stencil) {
		super(context);
		this.isSupportReverse = false;
		init(translucent, depth, stencil);
	}

	private void init(boolean translucent, int depth, int stencil) {
		isTakePic = false;
		isReturnPictureState = true;
		// By default, GLSurfaceView() creates a RGB_565 opaque surface.
		// If we want a translucent one, we should change the surface's
		// format here, using PixelFormat.TRANSLUCENT for GL Surfaces
		// is interpreted as any 32-bit surface with alpha by SurfaceFlinger.
		if (translucent) {
			this.getHolder().setFormat(PixelFormat.TRANSLUCENT);
		}
		// Setup the context factory for 2.0 rendering.
		// See ContextFactory class definition below
		setEGLContextFactory(new ContextFactory());

		// We need to choose an EGLConfig that matches the format of
		// our surface exactly. This is going to be done in our
		// custom config chooser. See ConfigChooser class definition
		// below.
		setEGLConfigChooser(translucent ? new ConfigChooser(8, 8, 8, 8, depth,
				stencil) : new ConfigChooser(5, 6, 5, 0, depth, stencil));

		// Set the renderer responsible for frame rendering
		this.setRenderer(this);

		this.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
	}

	/********************* PML TEST THE Capture screen *************/

	class TakePicTask extends AsyncTask<Void, Void, Bitmap> {
		byte[] picData;
		int picWidth;
		int picHeight;
		String filePath;

		public TakePicTask(byte[] data, int width, int height) {
			this.picData = data;
			this.picWidth = width;
			this.picHeight = height;
		}

		@Override
		protected Bitmap doInBackground(Void... params) {
			Bitmap bmp = null;
			int fileState = ViERenderer.FILE_OK;
			try {
				if (false) {
					byte[] dst = ConvertYUVPictrue.ConvertYUVToRGB(picData,
							picWidth, picHeight, isSupportReverse ? 1 : 0);
					bmp = createMyBitmap(dst, picWidth, picHeight);
				} else {
					int[] dst=ConvertYUVPictrue.ConvertYUVToRGBColor(picData,
							picWidth, picHeight, isSupportReverse ? 1 : 0);
					bmp = createMyBitmapByColor(dst, picWidth, picHeight);
				}
			} catch (Exception ex) {
				Log.e("Sys", "Error:" + ex.getMessage());
			}

			if (picData != null && bmp != null && sFileDir != null
					&& isSaveiPicture) {
				WulianLog.d(TAG, "decodeByteArray");
				try {
					WulianLog.d(TAG, "getRenderFrameDataSuccess sFileDir is:"
							+ sFileDir);
					if (!FileUtils.checksdfilepath(sFileDir)) {
						fileState = ViERenderer.FILE_MOUNT_EXCEPTION;
					} else {
						String fileName = FileUtils.getfilename();
						filePath = sFileDir + "/" + fileName + ".jpg";
						WulianLog.d(TAG,
								"getRenderFrameDataSuccess fileName is:"
										+ filePath);
						FileOutputStream fops = new FileOutputStream(filePath);
						// fops.write(data);
						bmp.compress(Bitmap.CompressFormat.PNG, 90, fops);
						fops.flush();
						fops.close();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			return bmp;
		}

		@Override
		protected void onPostExecute(Bitmap bmp) {
			if (mHandler != null) {
				Bundle bundle = new Bundle();
				bundle.putParcelable(ViERenderer.GET_PICTURE, bmp);
				bundle.putString(ViERenderer.GET_PATH, filePath);
				Message msg = new Message();
				msg.what = (bmp == null) ? ViERenderer.FILE_PICTURE_CREATE_EXCEPTION
						: ViERenderer.FILE_OK;
				msg.setData(bundle);
				mHandler.sendMessage(msg);

			}
		}
	}

	public static void setHandler(Handler handler) {
		mHandler = handler;
	}

	public static void setTakePic(String fileDir) {
		sFileDir = fileDir;
		isTakePic = true;
		isReturnPictureState = true;
		isSaveiPicture = true;
	}

	public static void setTakePicNotSave() {
		isTakePic = true;
		isReturnPictureState = true;
		isSaveiPicture = false;
	}

	public int getIsTakePic() {
		if (mHandler != null) {
			int result = isTakePic ? 1 : 0;
			if (isTakePic)
				isTakePic = false;
			return result;
		} else {
			return 0;
		}
	}

	public static void setIsReturnPictureState() {
		isReturnPictureState = true;
	}

	public int getIsReturnPictureState() {

		if (mHandler != null) {
			int result = isReturnPictureState ? 1 : 0;
			if (isReturnPictureState)
				isReturnPictureState = false;
			return result;
		} else {
			return 0;
		}
	}

	public void getReturnPictureState(int width, int height) {
		if (mHandler != null) {
			Message msg = new Message();
			msg.what = ViERenderer.PICTURE_HAS_COMING;
			Bundle bd = new Bundle();
			bd.putInt("width", width);
			bd.putInt("height", height);
			msg.setData(bd);
			mHandler.sendMessage(msg);
		}
	}
	
	/*
	 * byte[] data保存的是纯RGB的数据，而非完整的图片文件数据
	 */
	 public Bitmap createMyBitmapByColor(int[] colors, int width, int height) {
		if (colors == null) {
			return null;
		}
		Bitmap bmp = null;
		try {
			bmp = Bitmap.createBitmap(colors, 0, width, width, height,
					Bitmap.Config.ARGB_8888);
		} catch (Exception e) {
			// TODO: handle exception

			return null;
		}

		return bmp;
	}

	/*
	 * byte[] data保存的是纯RGB的数据，而非完整的图片文件数据
	 */
	static public Bitmap createMyBitmap(byte[] data, int width, int height) {
		int[] colors = convertByteToColor(data);
		if (colors == null) {
			return null;
		}

		Bitmap bmp = null;

		try {
			bmp = Bitmap.createBitmap(colors, 0, width, width, height,
					Bitmap.Config.ARGB_8888);
		} catch (Exception e) {
			// TODO: handle exception

			return null;
		}

		return bmp;
	}

	/*
	 * 将RGB数组转化为像素数组
	 */
	private static int[] convertByteToColor(byte[] data) {
		int size = data.length;
		if (size == 0) {
			return null;
		}

		// 理论上data的长度应该是3的倍数，这里做个兼容
		int arg = 0;
		if (size % 3 != 0) {
			arg = 1;
		}

		int[] color = new int[size / 3 + arg];
		int red, green, blue;

		if (arg == 0) { // 正好是3的倍数
			for (int i = 0; i < color.length; ++i) {

				color[i] = (data[i * 3] << 16 & 0x00FF0000)
						| (data[i * 3 + 1] << 8 & 0x0000FF00)
						| (data[i * 3 + 2] & 0x000000FF) | 0xFF000000;
			}
		} else { // 不是3的倍数
			for (int i = 0; i < color.length - 1; ++i) {
				color[i] = (data[i * 3] << 16 & 0x00FF0000)
						| (data[i * 3 + 1] << 8 & 0x0000FF00)
						| (data[i * 3 + 2] & 0x000000FF) | 0xFF000000;
			}

			color[color.length - 1] = 0xFF000000; // 最后一个像素用黑色填充
		}

		return color;
	}

	@SuppressLint("NewApi")
	public void getRenderFrameDataSuccess(byte[] data, int width, int height) {
		WulianLog.d(TAG, "getRenderFrameData size is:" + data.length
				+ "width is:" + width + ";height is:" + height);
		TakePicTask capturePictureTask = new TakePicTask(data, width, height);
		capturePictureTask.execute();
		/**
		 * Bitmap bmp = null; int fileState = ViERenderer.FILE_OK; try { byte []
		 * dst= ConvertYUVPictrue.ConvertYUVToRGB(data, width, height); bmp=
		 * createMyBitmap(dst, width, height); } catch (Exception ex) {
		 * Log.e("Sys", "Error:" + ex.getMessage()); }
		 * 
		 * if (data != null && bmp != null&&sFileDir!=null&&isSaveiPicture) {
		 * WulianLog.d(TAG, "decodeByteArray"); try { WulianLog.d(TAG,
		 * "getRenderFrameDataSuccess sFileDir is:" + sFileDir); if
		 * (!FileUtils.checksdfilepath(sFileDir)) { fileState =
		 * ViERenderer.FILE_MOUNT_EXCEPTION; } else { String fileName =
		 * FileUtils.getfilename(); fileName = sFileDir + "/" + fileName +
		 * ".jpg"; WulianLog.d(TAG, "getRenderFrameDataSuccess fileName is:" +
		 * fileName); FileOutputStream fops = new FileOutputStream(fileName); //
		 * fops.write(data); bmp.compress(Bitmap.CompressFormat.PNG, 90, fops);
		 * fops.flush(); fops.close(); } } catch (Exception e) {
		 * e.printStackTrace(); } } if (mHandler != null) { Bundle bundle = new
		 * Bundle(); bundle.putParcelable(ViERenderer.GET_PICTURE, bmp); Message
		 * msg = new Message(); msg.what = (bmp == null )?
		 * ViERenderer.FILE_PICTURE_CREATE_EXCEPTION : ViERenderer.FILE_OK;
		 * msg.setData(bundle); mHandler.sendMessage(msg); }
		 **/
	}

	public void getRenderFrameDataFail() {
		if (mHandler != null) {
			Message msg = new Message();
			msg.what = ViERenderer.TAKE_PICTURE_FAIL;
			mHandler.sendMessage(msg);
		}
	}

	/*******************************************************************/

	private static class ContextFactory implements
			GLSurfaceView.EGLContextFactory {
		private static int EGL_CONTEXT_CLIENT_VERSION = 0x3098;

		public EGLContext createContext(EGL10 egl, EGLDisplay display,
				EGLConfig eglConfig) {
			checkEglError("Before eglCreateContext", egl);
			int[] attrib_list = { EGL_CONTEXT_CLIENT_VERSION, 2, EGL10.EGL_NONE };
			EGLContext context = egl.eglCreateContext(display, eglConfig,
					EGL10.EGL_NO_CONTEXT, attrib_list);
			checkEglError("After eglCreateContext", egl);
			return context;
		}

		public void destroyContext(EGL10 egl, EGLDisplay display,
				EGLContext context) {
			egl.eglDestroyContext(display, context);
		}
	}

	private static void checkEglError(String prompt, EGL10 egl) {
		int error;
		while ((error = egl.eglGetError()) != EGL10.EGL_SUCCESS) {
			Log.e(TAG, String.format("%s: EGL error: 0x%x", prompt, error));
		}
	}

	private static class ConfigChooser implements
			GLSurfaceView.EGLConfigChooser {

		public ConfigChooser(int r, int g, int b, int a, int depth, int stencil) {
			mRedSize = r;
			mGreenSize = g;
			mBlueSize = b;
			mAlphaSize = a;
			mDepthSize = depth;
			mStencilSize = stencil;
		}

		// This EGL config specification is used to specify 2.0 rendering.
		// We use a minimum size of 4 bits for red/green/blue, but will
		// perform actual matching in chooseConfig() below.
		private static int EGL_OPENGL_ES2_BIT = 4;
		private static int[] s_configAttribs2 = { EGL10.EGL_RED_SIZE, 4,
				EGL10.EGL_GREEN_SIZE, 4, EGL10.EGL_BLUE_SIZE, 4,
				EGL10.EGL_RENDERABLE_TYPE, EGL_OPENGL_ES2_BIT, EGL10.EGL_NONE };

		public EGLConfig chooseConfig(EGL10 egl, EGLDisplay display) {

			// Get the number of minimally matching EGL configurations
			int[] num_config = new int[1];
			egl.eglChooseConfig(display, s_configAttribs2, null, 0, num_config);
			// WulianLog.d(TAG, "chooseConfig(EGL10 egl, EGLDisplay display)");
			int numConfigs = num_config[0];

			if (numConfigs <= 0) {
				throw new IllegalArgumentException(
						"No configs match configSpec");
			}

			// Allocate then read the array of minimally matching EGL configs
			EGLConfig[] configs = new EGLConfig[numConfigs];
			egl.eglChooseConfig(display, s_configAttribs2, configs, numConfigs,
					num_config);

			if (DEBUG) {
				printConfigs(egl, display, configs);
			}
			// Now return the "best" one
			return chooseConfig(egl, display, configs);
		}

		public EGLConfig chooseConfig(EGL10 egl, EGLDisplay display,
				EGLConfig[] configs) {
			// WulianLog.d(TAG,
			// " chooseConfig(EGL10 egl, EGLDisplay display, EGLConfig[] configs)");
			for (EGLConfig config : configs) {
				int d = findConfigAttrib(egl, display, config,
						EGL10.EGL_DEPTH_SIZE, 0);
				int s = findConfigAttrib(egl, display, config,
						EGL10.EGL_STENCIL_SIZE, 0);

				// We need at least mDepthSize and mStencilSize bits
				if (d < mDepthSize || s < mStencilSize)
					continue;

				// We want an *exact* match for red/green/blue/alpha
				int r = findConfigAttrib(egl, display, config,
						EGL10.EGL_RED_SIZE, 0);
				int g = findConfigAttrib(egl, display, config,
						EGL10.EGL_GREEN_SIZE, 0);
				int b = findConfigAttrib(egl, display, config,
						EGL10.EGL_BLUE_SIZE, 0);
				int a = findConfigAttrib(egl, display, config,
						EGL10.EGL_ALPHA_SIZE, 0);

				if (r == mRedSize && g == mGreenSize && b == mBlueSize
						&& a == mAlphaSize)
					return config;
			}
			return null;
		}

		private int findConfigAttrib(EGL10 egl, EGLDisplay display,
				EGLConfig config, int attribute, int defaultValue) {
			// WulianLog.d(TAG,
			// " findConfigAttrib(EGL10 egl, EGLDisplay display,");
			if (egl.eglGetConfigAttrib(display, config, attribute, mValue)) {
				return mValue[0];
			}
			return defaultValue;
		}

		private void printConfigs(EGL10 egl, EGLDisplay display,
				EGLConfig[] configs) {
			int numConfigs = configs.length;
			Log.d(TAG, String.format("%d configurations", numConfigs));
			for (int i = 0; i < numConfigs; i++) {
				Log.d(TAG, String.format("Configuration %d:\n", i));
				printConfig(egl, display, configs[i]);
			}
		}

		private void printConfig(EGL10 egl, EGLDisplay display, EGLConfig config) {
			int[] attributes = { EGL10.EGL_BUFFER_SIZE, EGL10.EGL_ALPHA_SIZE,
					EGL10.EGL_BLUE_SIZE,
					EGL10.EGL_GREEN_SIZE,
					EGL10.EGL_RED_SIZE,
					EGL10.EGL_DEPTH_SIZE,
					EGL10.EGL_STENCIL_SIZE,
					EGL10.EGL_CONFIG_CAVEAT,
					EGL10.EGL_CONFIG_ID,
					EGL10.EGL_LEVEL,
					EGL10.EGL_MAX_PBUFFER_HEIGHT,
					EGL10.EGL_MAX_PBUFFER_PIXELS,
					EGL10.EGL_MAX_PBUFFER_WIDTH,
					EGL10.EGL_NATIVE_RENDERABLE,
					EGL10.EGL_NATIVE_VISUAL_ID,
					EGL10.EGL_NATIVE_VISUAL_TYPE,
					0x3030, // EGL10.EGL_PRESERVED_RESOURCES,
					EGL10.EGL_SAMPLES,
					EGL10.EGL_SAMPLE_BUFFERS,
					EGL10.EGL_SURFACE_TYPE,
					EGL10.EGL_TRANSPARENT_TYPE,
					EGL10.EGL_TRANSPARENT_RED_VALUE,
					EGL10.EGL_TRANSPARENT_GREEN_VALUE,
					EGL10.EGL_TRANSPARENT_BLUE_VALUE,
					0x3039, // EGL10.EGL_BIND_TO_TEXTURE_RGB,
					0x303A, // EGL10.EGL_BIND_TO_TEXTURE_RGBA,
					0x303B, // EGL10.EGL_MIN_SWAP_INTERVAL,
					0x303C, // EGL10.EGL_MAX_SWAP_INTERVAL,
					EGL10.EGL_LUMINANCE_SIZE, EGL10.EGL_ALPHA_MASK_SIZE,
					EGL10.EGL_COLOR_BUFFER_TYPE, EGL10.EGL_RENDERABLE_TYPE,
					0x3042 // EGL10.EGL_CONFORMANT
			};
			String[] names = { "EGL_BUFFER_SIZE", "EGL_ALPHA_SIZE",
					"EGL_BLUE_SIZE", "EGL_GREEN_SIZE", "EGL_RED_SIZE",
					"EGL_DEPTH_SIZE", "EGL_STENCIL_SIZE", "EGL_CONFIG_CAVEAT",
					"EGL_CONFIG_ID", "EGL_LEVEL", "EGL_MAX_PBUFFER_HEIGHT",
					"EGL_MAX_PBUFFER_PIXELS", "EGL_MAX_PBUFFER_WIDTH",
					"EGL_NATIVE_RENDERABLE", "EGL_NATIVE_VISUAL_ID",
					"EGL_NATIVE_VISUAL_TYPE", "EGL_PRESERVED_RESOURCES",
					"EGL_SAMPLES", "EGL_SAMPLE_BUFFERS", "EGL_SURFACE_TYPE",
					"EGL_TRANSPARENT_TYPE", "EGL_TRANSPARENT_RED_VALUE",
					"EGL_TRANSPARENT_GREEN_VALUE",
					"EGL_TRANSPARENT_BLUE_VALUE", "EGL_BIND_TO_TEXTURE_RGB",
					"EGL_BIND_TO_TEXTURE_RGBA", "EGL_MIN_SWAP_INTERVAL",
					"EGL_MAX_SWAP_INTERVAL", "EGL_LUMINANCE_SIZE",
					"EGL_ALPHA_MASK_SIZE", "EGL_COLOR_BUFFER_TYPE",
					"EGL_RENDERABLE_TYPE", "EGL_CONFORMANT" };
			int[] value = new int[1];
			for (int i = 0; i < attributes.length; i++) {
				int attribute = attributes[i];
				String name = names[i];
				if (egl.eglGetConfigAttrib(display, config, attribute, value)) {
					Log.d(TAG, String.format("  %s: %d\n", name, value[0]));
				} else {
					// WulianLog.d(TAG, String.format("  %s: failed\n", name));
					while (egl.eglGetError() != EGL10.EGL_SUCCESS)
						;
				}
			}
		}

		// Subclasses can adjust these values:
		protected int mRedSize;
		protected int mGreenSize;
		protected int mBlueSize;
		protected int mAlphaSize;
		protected int mDepthSize;
		protected int mStencilSize;
		private int[] mValue = new int[1];
	}

	// IsSupported
	// Return true if this device support Open GL ES 2.0 rendering.
	public static boolean IsSupported(Context context) {
		ActivityManager am = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		ConfigurationInfo info = am.getDeviceConfigurationInfo();
		if (info.reqGlEsVersion >= 0x20000) {
			// Open GL ES 2.0 is supported.
			// WulianLog.d(TAG, " IsSupported true");
			return true;
		}
		// WulianLog.d(TAG, " IsSupported false");
		return false;
	}

	@SuppressLint("NewApi")
	public void onDrawFrame(GL10 gl) {
		nativeFunctionLock.lock();
		if (!nativeFunctionsRegisted || !surfaceCreated) {
			nativeFunctionLock.unlock();
			return;
		}
		// WulianLog.d(TAG, " onDrawFrame");
		if (!openGLCreated) {
			if (0 != CreateOpenGLNative(nativeObject, viewWidth, viewHeight)) {
				return; // Failed to create OpenGL
			}
			openGLCreated = true; // Created OpenGL successfully
		}
		DrawNative(nativeObject); // Draw the new frame

		nativeFunctionLock.unlock();
	}

	public void onSurfaceChanged(GL10 gl, int width, int height) {
		surfaceCreated = true;
		viewWidth = width;
		viewHeight = height;
		// WulianLog.d(TAG, "onSurfaceChanged");
		nativeFunctionLock.lock();
		if (nativeFunctionsRegisted) {
			if (CreateOpenGLNative(nativeObject, width, height) == 0)
				openGLCreated = true;
		}
		nativeFunctionLock.unlock();
	}

	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		// WulianLog.d(TAG, "onSurfaceCreated");
	}

	public int IsVideoReverseNativeObject() {
		return isSupportReverse ? 1 : 0;
	}

	public void RegisterNativeObject(long nativeObject) {
		nativeFunctionLock.lock();
		this.nativeObject = nativeObject;
		nativeFunctionsRegisted = true;
		nativeFunctionLock.unlock();
	}

	public void DeRegisterNativeObject() {
		nativeFunctionLock.lock();
		nativeFunctionsRegisted = false;
		openGLCreated = false;
		this.nativeObject = 0;
		nativeFunctionLock.unlock();
	}

	public void ReDraw() {
		if (surfaceCreated) {
			// Request the renderer to redraw using the render thread context.
			this.requestRender();
		}
	}

	private native int CreateOpenGLNative(long nativeObject, int width,
			int height);

	private native void DrawNative(long nativeObject);

	static {
		System.loadLibrary("yuvpicture");
	}

}
