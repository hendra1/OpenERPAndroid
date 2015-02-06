package com.openerp.addons.crm;

import android.content.Context;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.openerp.R;
import com.openerp.addons.crm.providers.lead.LeadProvider;
import com.openerp.orm.OEDataRow;
import com.openerp.orm.OEValues;
import com.openerp.orm.SQLHelper;
import com.openerp.receivers.SyncFinishReceiver;
import com.openerp.support.AppScope;
import com.openerp.support.BaseFragment;
import com.openerp.support.listview.OEListAdapter;
import com.openerp.util.Base64Helper;
import com.openerp.util.drawer.DrawerItem;
import com.openerp.util.logger.OELog;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class CashFlow extends BaseFragment {

	public static final String TAG = CashFlow.class.getSimpleName();

	View mView = null;
	ListView mListview = null;
	OEListAdapter mListAdapter = null;
	List<Object> mCashFlowItems = new ArrayList<Object>();
	CashFlowLoader mLeadLoader = null;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		scope = new AppScope(getActivity());
		mView = inflater.inflate(R.layout.crm_cash_flow_layout, container, false);
        scope.main().setTitle(R.string.title_cash_flow);
        CashFlowDB db = new CashFlowDB(getActivity());

        SQLHelper ok = new SQLHelper();
        //ok.dropTable(db);
        //ok.createTable(db);
        //createSampleData();


		init();
		return mView;
	}


	private void init() {
		mListview = (ListView) mView.findViewById(R.id.crmCashFlowListView);

		mListAdapter = new OEListAdapter(getActivity(), R.layout.crm_cash_flow_custom_row, mCashFlowItems) {

			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				View view = convertView;
				if (view == null)
					view = getActivity().getLayoutInflater().inflate(
							getResource(), parent, false);

				OEDataRow row = (OEDataRow) mCashFlowItems.get(position);

				ImageView imgCustomerPic = (ImageView) view
						.findViewById(R.id.imgCustomerPic);

                Log.i("status :", row.getString("status"));

                if(row.getString("status").equals("not sync")) {
                    imgCustomerPic.setImageResource(R.drawable.ic_uncheck);
                }

				/*ImageView imgCustomerPic = (ImageView) view
						.findViewById(R.id.imgCustomerPic);
				Bitmap bitmap = null;
				OEDataRow partner = row.getM2ORecord("partner_id").browse();
				if (partner != null) {
					String base64Image = partner.getString("image_small");
					bitmap = Base64Helper.getBitmapImage(getActivity(),
							base64Image);
				}
				imgCustomerPic.setImageBitmap(bitmap);*/

				TextView txvAmount, txvName, txvType, txvDate, txvCategory;
				txvAmount = (TextView) view.findViewById(R.id.txvCashFlowAmount);
				txvName = (TextView) view
						.findViewById(R.id.txvCashFlowName);
				txvType = (TextView) view
						.findViewById(R.id.txvCashFlowType);
                txvCategory = (TextView) view
                        .findViewById(R.id.txvCashFlowCategory);
                txvDate = (TextView) view
                        .findViewById(R.id.txvCashFlowDate);

                String number = row.getString("amount");
                double amount = Double.parseDouble(number);
                //DecimalFormat formatter = new DecimalFormat("#,###.00");
                Locale id = new Locale("in");
                Locale.setDefault(id);
                NumberFormat nf = NumberFormat.getCurrencyInstance( Locale.getDefault() );

                txvAmount.setText(nf.format(amount));
/*				OEDataRow user = row.getM2ORecord("user_id").browse();
				String salesPerson = "";
				if (user != null)
					salesPerson = user.getString("name");*/
				txvName.setText(row.getString("description"));

                //OELog.log("Field Category : " + row.getM2ORecord.);

/*				if (salesPerson.equals("false"))
					txvName.setVisibility(View.GONE);*/
                String tgl = row.getString("date");
				txvType.setText(row.getString("type"));
                txvCategory.setText(row.getM2ORecord("category").browse().getString("name"));
                txvDate.setText(row.getString("date"));

//				if (row.getString("description").equals("false"))
//					txvType.setVisibility(View.GONE);
				return view;
			}
		};
		mListview.setAdapter(mListAdapter);
		mLeadLoader = new CashFlowLoader();
		mLeadLoader.execute();
	}

	private void checkStatus() {
		if (db().isEmptyTable()) {
			scope.main().requestSync(LeadProvider.AUTHORITY);
		}
	}

	class CashFlowLoader extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {
            android.os.Debug.waitingForDebugger();
			mCashFlowItems.clear();
			CashFlowDB db = new CashFlowDB(getActivity());
			mCashFlowItems.addAll(db.select(null,null,null,null,"id desc"));
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			mListAdapter.notifiyDataChange(mCashFlowItems);
			checkStatus();
		}
	}

	@Override
	public Object databaseHelper(Context context) {
		return new CashFlowDB(context);
	}

	@Override
	public List<DrawerItem> drawerMenus(Context context) {
		List<DrawerItem> menu = new ArrayList<DrawerItem>();

		menu.add(new DrawerItem(TAG, "Transaksi Kas", true));
		menu.add(new DrawerItem(TAG, "Transaksi Kas", 0, "#cc0000", object("all")));
		return menu;
	}

	private Fragment object(String value) {
		CashFlow lead = new CashFlow();
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

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_fragment_account_user_profile, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_cash_flow_sync:
                //dialog = inputPasswordDialog();
                //sync();
                //dialog.show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

//    private void sync()
//    {
//
//        Toast.makeText(getActivity(),"Syncronizing..",Toast.LENGTH_LONG).show();
//    }

	SyncFinishReceiver mSyncFinish = new SyncFinishReceiver() {
		public void onReceive(Context context, android.content.Intent intent) {
			mLeadLoader = new CashFlowLoader();
			mLeadLoader.execute();
		};
	};
}
