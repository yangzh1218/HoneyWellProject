/**
 * Copyright (C) 2010-2012 Regis Montoya (aka r3gis - www.r3gis.fr)
 * This file is part of CSipSimple.
 *
 *  CSipSimple is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *  If you own a pjsip commercial license you can also redistribute it
 *  and/or modify it under the terms of the GNU Lesser General Public License
 *  as an android library.
 *
 *  CSipSimple is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with CSipSimple.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.wulian.siplibrary.pjsip;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.text.TextUtils;

import com.wulian.siplibrary.utils.ProviderWrapper;
import com.wulian.siplibrary.utils.WulianDefaultPreference;
import com.wulian.siplibrary.utils.WulianLog;

import java.io.File;
import java.lang.reflect.Field;

public class NativeLibManager {
	private static final String THIS_FILE = "NativeLibMgr";
	public static final String STD_LIB_NAME = "stlport_shared";
	public static final String STACK_NAME = "pjsipjni";

	public static File getBundledStackLibFile(Context ctx, String libName) {
		WulianLog.d(
				THIS_FILE,
				"getsSipSystemLibPath is:"
						+ (TextUtils.isEmpty(WulianDefaultPreference
								.getsSipSystemLibPath()) ? "NULL"
								: WulianDefaultPreference
										.getsSipSystemLibPath()));
		WulianLog.d(THIS_FILE, "libName is:" + libName);
		if (!TextUtils.isEmpty(WulianDefaultPreference.getsSipSystemLibPath())) {
			return new File(WulianDefaultPreference.getsSipSystemLibPath(),
					libName);
		} else {
			PackageInfo packageInfo = ProviderWrapper
					.getCurrentPackageInfos(ctx);
			if (packageInfo != null) {
				ApplicationInfo appInfo = packageInfo.applicationInfo;
				File f = getLibFileFromPackage(appInfo, libName, true);
				return f;
			}
			// This is the very last fallback method
			return new File(ctx.getFilesDir().getParent(), "lib"
					+ File.separator + libName);
		}
	}

	public static File getLibFileFromPackage(ApplicationInfo appInfo,
			String libName, boolean allowFallback) {
		WulianLog.v(THIS_FILE, "Dir " + appInfo.dataDir);
		WulianLog.d(
				THIS_FILE,
				"getsSipSystemLibPath is:"
						+ (TextUtils.isEmpty(WulianDefaultPreference
								.getsSipSystemLibPath()) ? "NULL"
								: WulianDefaultPreference
										.getsSipSystemLibPath()));
		WulianLog.d(THIS_FILE, "libName is:" + libName);
		if (!TextUtils.isEmpty(WulianDefaultPreference.getsSipSystemLibPath())) {
			return new File(WulianDefaultPreference.getsSipSystemLibPath(),
					libName);
		} else {
			try {
				Field f = ApplicationInfo.class.getField("nativeLibraryDir");
				File nativeFile = new File((String) f.get(appInfo), libName);
				if (nativeFile.exists()) {
					WulianLog.v(THIS_FILE, "Found native lib using clean way");
					return nativeFile;
				}
			} catch (Exception e) {
				WulianLog.e(THIS_FILE, "not get field for native lib dir", e);
			}
		}
		if (allowFallback) {
			return new File(appInfo.dataDir, "lib" + File.separator + libName);
		} else {
			return null;
		}
	}

	public static boolean isDebuggableApp(Context ctx) {
		try {
			PackageInfo pinfo = ctx.getPackageManager().getPackageInfo(
					ctx.getPackageName(), 0);
			return ((pinfo.applicationInfo.flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0);
		} catch (NameNotFoundException e) {
			// Should not happen....or something is wrong with android...
			WulianLog.e(THIS_FILE, "Not possible to find self name", e);
		}
		return false;
	}
}
