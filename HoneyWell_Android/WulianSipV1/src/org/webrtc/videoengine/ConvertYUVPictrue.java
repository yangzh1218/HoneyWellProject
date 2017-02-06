/**
 * Project Name:  GetYUVPictureLibrary
 * File Name:     ConvertYUVPictrue.java
 * Package Name:  org.webrtc.videoengine
 * @Date:         2015年4月6日
 * Copyright (c)  2015, wulian All Rights Reserved.
 */

package org.webrtc.videoengine;

/**
 * @ClassName: ConvertYUVPictrue
 * @Function: TODO
 * @Date: 2015年4月6日
 * @author Puml
 * @email puml@wuliangroup.cn
 */
public class ConvertYUVPictrue {
	public static native byte[] ConvertYUVToRGB(byte[] srcData, int width, int height,int Reverse);
	public static native int[] ConvertYUVToRGBColor(byte[] srcData, int width,
			int height, int Reverse);
}
