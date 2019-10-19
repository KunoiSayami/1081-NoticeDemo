/*
 ** Copyright (C) 2019 KunoiSayami
 **
 ** This file is part of 1081-NiceDemo and is released under
 ** the AGPL v3 License: https://www.gnu.org/licenses/agpl-3.0.txt
 **
 ** This program is free software: you can redistribute it and/or modify
 ** it under the terms of the GNU Affero General Public License as published by
 ** the Free Software Foundation, either version 3 of the License, or
 ** any later version.
 **
 ** This program is distributed in the hope that it will be useful,
 ** but WITHOUT ANY WARRANTY; without even the implied warranty of
 ** MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 ** GNU Affero General Public License for more details.
 **
 ** You should have received a copy of the GNU Affero General Public License
 ** along with this program. If not, see <https://www.gnu.org/licenses/>.
 */
package org.example.u.noticedemo.types;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.example.u.noticedemo.R;

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
