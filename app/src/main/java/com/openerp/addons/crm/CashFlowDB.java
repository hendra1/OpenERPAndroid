package com.openerp.addons.crm;

import android.content.Context;

import com.openerp.base.res.ResPartnerDB;
import com.openerp.orm.OEColumn;
import com.openerp.orm.OEDatabase;
import com.openerp.orm.OEFields;

import java.util.ArrayList;
import java.util.List;

public class CashFlowDB extends OEDatabase {

	Context mContext = null;
	public CashFlowDB(Context context) {
		super(context);
		mContext = context;
	}

	@Override
	public String getModelName() {
		return "kas.bag";
	}

	@Override
	public List<OEColumn> getModelColumns() {
		List<OEColumn> cols = new ArrayList<OEColumn>();
		
		cols.add(new OEColumn("description", "Title", OEFields.varchar(255)));
        cols.add(new OEColumn("type", "Type", OEFields.varchar(64)));
		cols.add(new OEColumn("amount", "Amount", OEFields.integer()));
        cols.add(new OEColumn("category", "Category", OEFields.manyToOne(new CashFlowCateg(mContext))));
        cols.add(new OEColumn("date", "Date", OEFields.varchar(100)));
        cols.add(new OEColumn("status", "Status", OEFields.varchar(10)));

		return cols;
	}

    public class CashFlowCateg extends OEDatabase {

        public CashFlowCateg(Context context) {
            super(context);
        }

        @Override
        public String getModelName() {
            return "category.bag";
        }

        @Override
        public List<OEColumn> getModelColumns() {
            List<OEColumn> cols = new ArrayList<OEColumn>();
            cols.add(new OEColumn("name", "name", OEFields.varchar(64)));
            return cols;
        }
    }
}
