package com.openerp.addons.crm.services;

import android.accounts.Account;
import android.app.Service;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.Intent;
import android.content.SyncResult;
import android.os.Bundle;

import com.openerp.addons.crm.LeadDB;
import com.openerp.orm.OEHelper;
import com.openerp.receivers.SyncFinishReceiver;
import com.openerp.support.service.OEService;

public class LeadService extends OEService {

	@Override
	public Service getService() {
		return this;
	}

	@Override
	public void performSync(Context context, Account account, Bundle extras,
			String authority, ContentProviderClient provider,
			SyncResult syncResult) {
		LeadDB lead = new LeadDB(context);
		OEHelper oe = lead.getOEInstance();
		if (oe != null) {
			if (oe.syncWithServer()) {
				Intent intent = new Intent(SyncFinishReceiver.SYNC_FINISH);
				sendBroadcast(intent);
			}
		}
	}

}
