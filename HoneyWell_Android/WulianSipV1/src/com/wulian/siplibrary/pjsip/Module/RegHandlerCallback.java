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

package com.wulian.siplibrary.pjsip.Module;

import android.content.Context;
import android.util.SparseIntArray;

import org.pjsip.pjsua.MobileRegHandlerCallback;
import org.pjsip.pjsua.pj_str_t;
import org.pjsip.pjsua.pjsua;


public class RegHandlerCallback extends MobileRegHandlerCallback {
    
    private SparseIntArray accountCleanRegisters = new SparseIntArray();
    private pj_str_t EMPTY_STR = pjsua.pj_str_copy("");
    private Context mCtxt;
    
    public RegHandlerCallback(Context ctxt) {
        mCtxt = ctxt;
    }
    
    public void set_account_cleaning_state(int acc_id, int active) {
        accountCleanRegisters.put(acc_id, active);
    }
    
    @Override
    public pj_str_t on_restore_contact(int acc_id) {
        int active = accountCleanRegisters.get(acc_id, 0);
        if(active == 0) {
            return EMPTY_STR;
        }
        return EMPTY_STR;
    }
    
    @Override
    public void on_save_contact(int acc_id, pj_str_t contact, int expires) {
    }

}
