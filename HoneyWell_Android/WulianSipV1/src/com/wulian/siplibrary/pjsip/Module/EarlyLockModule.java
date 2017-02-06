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

/**
 * 
 */

package com.wulian.siplibrary.pjsip.Module;

import android.content.Context;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;

import com.wulian.siplibrary.manage.SipManager;
import com.wulian.siplibrary.manage.SipProfile;
import com.wulian.siplibrary.utils.WulianLog;

import org.pjsip.pjsua.EarlyLockCallback;
import org.pjsip.pjsua.pjsua;

/**
 * @author r3gis3r
 */
public class EarlyLockModule implements PjsipModule {
	private static final String THIS_FILE = "EarlyLockModule";

	private EarlyLocker locker;
	private PowerManager pm;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.csipsimple.pjsip.PjSipService.PjsipModule#setContext(android.content
	 * .Context)
	 */
	@Override
	public void setContext(Context ctxt) {
		pm = (PowerManager) ctxt.getSystemService(Context.POWER_SERVICE);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.csipsimple.pjsip.PjSipService.PjsipModule#onBeforeStartPjsip()
	 */
	@Override
	public void onBeforeStartPjsip() {
		pjsua.mod_earlylock_init();
		locker = new EarlyLocker();
		pjsua.mod_earlylock_set_callback(locker);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.csipsimple.pjsip.PjSipService.PjsipModule#
	 * onBeforeAccountStartRegistration(int, com.csipsimple.api.SipProfile)
	 */
	@Override
	public void onBeforeAccountStartRegistration(int pjId, SipProfile acc) {
		// Nothing to do here
	}

	private class WorkLocker extends Thread {
		private final long lockTime;
		private WakeLock wl;

		WorkLocker(long time) {
			lockTime = time;
			wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
					SipManager.SIP_PREFIX_MSG + "earlylock");
			wl.acquire();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Thread#run()
		 */
		@Override
		public void run() {
			try {
				WulianLog.d(THIS_FILE, "We entered a partial early lock");
				sleep(lockTime);
			} catch (InterruptedException e) {
				WulianLog.e(THIS_FILE, "Unable to lock");
			}
			wl.release();
		}
	}

	private class EarlyLocker extends EarlyLockCallback {
		public void on_create_early_lock() {
			WorkLocker wl = new WorkLocker(2000);
			wl.start();
		}
	}
}
