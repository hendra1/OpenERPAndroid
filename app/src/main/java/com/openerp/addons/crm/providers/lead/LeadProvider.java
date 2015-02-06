package com.openerp.addons.crm.providers.lead;

import com.openerp.support.provider.OEContentProvider;

public class LeadProvider extends OEContentProvider {
	public static String CONTENTURI = "com.openerp.addons.crm.providers.lead.LeadProvider";

	public static String AUTHORITY = "com.openerp.addons.crm.providers.lead";

	@Override
	public String authority() {
		return AUTHORITY;
	}

	@Override
	public String contentUri() {
		return CONTENTURI;
	}

}
