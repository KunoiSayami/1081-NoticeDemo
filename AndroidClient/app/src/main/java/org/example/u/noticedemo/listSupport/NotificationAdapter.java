package org.example.u.noticedemo.listSupport;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.example.u.noticedemo.R;
import org.example.u.noticedemo.types.NotificationType;

import java.util.ArrayList;

public class NotificationAdapter extends ArrayAdapter<NotificationType> {
	public NotificationAdapter(Context context, ArrayList<NotificationType> notificationTypes) {
		super(context, android.R.layout.simple_list_item_1, notificationTypes);
	}

	@Override
	public View getView(int position, View covertView, ViewGroup parent) {
		NotificationType nt = getItem(position);

		if (covertView == null) {
			covertView = LayoutInflater.from(getContext()).inflate(R.layout.notification_item, parent, false);
		}

		TextView txtTitle = covertView.findViewById(R.id.txtNotificationTitle),
				txtBody = covertView.findViewById(R.id.txtNotificationBody);

		txtTitle.setText(nt.getTitle());
		txtBody.setText(nt.getBodyShort());

		return covertView;
	}
}
