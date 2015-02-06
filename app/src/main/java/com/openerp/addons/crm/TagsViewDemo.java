package com.openerp.addons.crm;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.openerp.R;
import com.openerp.base.res.ResPartnerDB;
import com.openerp.orm.OEDataRow;
import com.openerp.support.BaseFragment;
import com.openerp.support.listview.OEListAdapter;
import com.openerp.util.Base64Helper;
import com.openerp.util.drawer.DrawerItem;
import com.openerp.util.tags.MultiTagsTextView.TokenListener;
import com.openerp.util.tags.TagsView;
import com.openerp.util.tags.TagsView.CustomTagViewListener;

public class TagsViewDemo extends BaseFragment implements TokenListener {

	View mView = null;
	TagsView mTagsView = null;
	OEListAdapter mAdapter = null;
	List<Object> mPartners = new ArrayList<Object>();

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mView = inflater.inflate(R.layout.tags_view_layout, container, false);
		init();
		return mView;
	}

	private void init() {

		mPartners.clear();
		mPartners.addAll(db().select());
		mTagsView = (TagsView) mView.findViewById(R.id.tagsView);
		mTagsView.setCustomTagView(new CustomTagViewListener() {

			@Override
			public View getViewForTags(LayoutInflater layoutInflater,
					Object object, ViewGroup tagsViewGroup) {
				View view = layoutInflater.inflate(
						R.layout.tags_view_custom_tag_layout, tagsViewGroup,
						false);
				initView(object, view);

				return view;
			}
		});

		mAdapter = new OEListAdapter(getActivity(),
				R.layout.tags_view_custom_layout, mPartners) {
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				View view = convertView;
				if (view == null)
					view = getActivity().getLayoutInflater().inflate(
							getResource(), parent, false);
				initView(mPartners.get(position), view);
				return view;
			}
		};
		mTagsView.setTokenListener(this);
		mTagsView.setAdapter(mAdapter);
	}

	private void initView(Object object, View view) {
		OEDataRow row = (OEDataRow) object;
		ImageView imgPic = (ImageView) view.findViewById(R.id.tagImage);
		TextView txvName, txvEmail;
		txvName = (TextView) view.findViewById(R.id.tagName);
		txvEmail = (TextView) view.findViewById(R.id.tagEmail);

		imgPic.setImageBitmap(Base64Helper.getBitmapImage(getActivity(),
				row.getString("image_small")));
		txvName.setText(row.getString("name"));
		txvEmail.setText(row.getString("email"));
	}

	@Override
	public Object databaseHelper(Context context) {
		return new ResPartnerDB(context);
	}

	@Override
	public List<DrawerItem> drawerMenus(Context context) {
		List<DrawerItem> menu = new ArrayList<DrawerItem>();

		menu.add(new DrawerItem("key", "TagView demo", true));
		menu.add(new DrawerItem("key", "TagView Demo", 0, "#0099cc",
				object("all")));
		return menu;
	}

	private Fragment object(String value) {
		TagsViewDemo lead = new TagsViewDemo();
		Bundle bundle = new Bundle();
		bundle.putString("value", value);
		lead.setArguments(bundle);
		return lead;

	}

	@Override
	public void onTokenAdded(Object token, View view) {
		OEDataRow row = (OEDataRow) token;
		Toast.makeText(getActivity(), row.getString("name") + " added.",
				Toast.LENGTH_LONG).show();
	}

	@Override
	public void onTokenSelected(Object token, View view) {

	}

	@Override
	public void onTokenRemoved(Object token) {
		OEDataRow row = (OEDataRow) token;
		Toast.makeText(getActivity(), row.getString("name") + " removed.",
				Toast.LENGTH_LONG).show();
	}

}
