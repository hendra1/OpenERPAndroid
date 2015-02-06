package com.openerp.addons.crm;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.openerp.R;
import com.openerp.addons.crm.providers.lead.LeadProvider;
import com.openerp.orm.OEDataRow;
import com.openerp.receivers.SyncFinishReceiver;
import com.openerp.support.AppScope;
import com.openerp.support.BaseFragment;
import com.openerp.support.listview.OEListAdapter;
import com.openerp.util.Base64Helper;
import com.openerp.util.drawer.DrawerItem;

public class Lead extends BaseFragment {

	public static final String TAG = Lead.class.getSimpleName();

	View mView = null;
	ListView mListview = null;
	OEListAdapter mListAdapter = null;
	List<Object> mLeadItems = new ArrayList<Object>();
	LeadLoader mLeadLoader = null;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		scope = new AppScope(getActivity());
		mView = inflater.inflate(R.layout.crm_lead_layout, container, false);
		init();
		return mView;
	}

	private void init() {
		mListview = (ListView) mView.findViewById(R.id.crmLeadListView);
		mListAdapter = new OEListAdapter(getActivity(),
				R.layout.crm_lead_custom_row, mLeadItems) {
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				View view = convertView;
				if (view == null)
					view = getActivity().getLayoutInflater().inflate(
							getResource(), parent, false);

				OEDataRow row = (OEDataRow) mLeadItems.get(position);

				ImageView imgCustomerPic = (ImageView) view
						.findViewById(R.id.imgCustomerPic);
				Bitmap bitmap = null;
				OEDataRow partner = row.getM2ORecord("partner_id").browse();
				if (partner != null) {
					String base64Image = partner.getString("image_small");
					bitmap = Base64Helper.getBitmapImage(getActivity(),
							base64Image);
				}
				imgCustomerPic.setImageBitmap(bitmap);

				TextView txvName, txvSalesPerson, txvDescription;
				txvName = (TextView) view.findViewById(R.id.txvLeadName);
				txvSalesPerson = (TextView) view
						.findViewById(R.id.txvLeadSalesPerson);
				txvDescription = (TextView) view
						.findViewById(R.id.txvLeadDescription);

				txvName.setText(row.getString("name"));
				OEDataRow user = row.getM2ORecord("user_id").browse();
				String salesPerson = "";
				if (user != null)
					salesPerson = user.getString("name");
				txvSalesPerson.setText(salesPerson);

				if (salesPerson.equals("false"))
					txvSalesPerson.setVisibility(View.GONE);

				txvDescription.setText(row.getString("description"));
				if (row.getString("description").equals("false"))
					txvDescription.setVisibility(View.GONE);
				return view;
			}
		};
		mListview.setAdapter(mListAdapter);
		mLeadLoader = new LeadLoader();
		mLeadLoader.execute();
	}

	private void checkStatus() {
		if (db().isEmptyTable()) {
			scope.main().requestSync(LeadProvider.AUTHORITY);
		}
	}

	class LeadLoader extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {
			mLeadItems.clear();
			LeadDB db = new LeadDB(getActivity());
			mLeadItems.addAll(db.select());
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			mListAdapter.notifiyDataChange(mLeadItems);
			checkStatus();
		}
	}

	@Override
	public Object databaseHelper(Context context) {
		return new LeadDB(context);
	}

	@Override
	public List<DrawerItem> drawerMenus(Context context) {
		List<DrawerItem> menu = new ArrayList<DrawerItem>();

		menu.add(new DrawerItem(TAG, "Leads", true));
		menu.add(new DrawerItem(TAG, "Leads", 0, "#cc0000", object("all")));
		return menu;
	}

	private Fragment object(String value) {
		Lead lead = new Lead();
		Bundle bundle = new Bundle();
		bundle.putString("lead_value", value);
		lead.setArguments(bundle);
		return lead;

	}

	@Override
	public void onResume() {
		super.onResume();
		getActivity().registerReceiver(mSyncFinish,
				new IntentFilter(SyncFinishReceiver.SYNC_FINISH));
	}

	@Override
	public void onPause() {
		super.onPause();
		getActivity().unregisterReceiver(mSyncFinish);
	}

	SyncFinishReceiver mSyncFinish = new SyncFinishReceiver() {
		public void onReceive(Context context, android.content.Intent intent) {
			mLeadLoader = new LeadLoader();
			mLeadLoader.execute();
		};
	};
}
