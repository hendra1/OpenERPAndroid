package com.openerp.addons.crm;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;

import com.openerp.base.res.ResPartnerDB;
import com.openerp.orm.OEColumn;
import com.openerp.orm.OEDatabase;
import com.openerp.orm.OEFields;

public class LeadDB extends OEDatabase {

	Context mContext = null;
	public LeadDB(Context context) {
		super(context);
		mContext = context;
	}

	@Override
	public String getModelName() {
		return "crm.lead";
	}

	@Override
	public List<OEColumn> getModelColumns() {
		List<OEColumn> cols = new ArrayList<OEColumn>();
		
		cols.add(new OEColumn("name", "name", OEFields.varchar(64)));
		cols.add(new OEColumn("partner_id", "Customer", OEFields.manyToOne(new ResPartnerDB(mContext))));
		cols.add(new OEColumn("user_id", "Sales Person", OEFields.manyToOne(new ResUsers(mContext))));
		cols.add(new OEColumn("categ_ids", "Category", OEFields.manyToMany(new CRMCaseCateg(mContext))));
		cols.add(new OEColumn("description", "Description", OEFields.text()));
		
		return cols;
	}
	class ResUsers extends OEDatabase {

		public ResUsers(Context context) {
			super(context);
		}

		@Override
		public String getModelName() {
			return "res.users";
		}

		@Override
		public List<OEColumn> getModelColumns() {
			List<OEColumn> cols = new ArrayList<OEColumn>();
			cols.add(new OEColumn("name", "name", OEFields.varchar(64)));
			return cols;
		}
	}
	class CRMCaseCateg extends OEDatabase {

		public CRMCaseCateg(Context context) {
			super(context);
		}

		@Override
		public String getModelName() {
			return "crm.case.categ";
		}

		@Override
		public List<OEColumn> getModelColumns() {
			List<OEColumn> cols = new ArrayList<OEColumn>();
			cols.add(new OEColumn("name", "name", OEFields.varchar(64)));
			return cols;
		}
	}
}
