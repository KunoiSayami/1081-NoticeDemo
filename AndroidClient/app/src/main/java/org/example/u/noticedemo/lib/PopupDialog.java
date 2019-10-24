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
package org.example.u.noticedemo.lib;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;

import androidx.annotation.StringRes;

import org.example.u.noticedemo.R;

import java.io.PrintWriter;
import java.io.StringWriter;

public class PopupDialog {

	public static void build(Context context, String title, String body) {
		AlertDialog.Builder dlgAlert = new AlertDialog.Builder(context);
		dlgAlert.setTitle(title);
		dlgAlert.setMessage(body);
		dlgAlert.setPositiveButton("OK",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						//dismiss the dialog
						dialog.dismiss();
					}
				});
		dlgAlert.setCancelable(true);
		dlgAlert.create().show();
	}

	public static void build(Context context, @StringRes int title_id, @StringRes int body_id) {
		build(context, context.getString(title_id), context.getString(body_id));
	}

	public static void build(Context context, Exception e) {
		StringWriter sw = new StringWriter();
		e.printStackTrace(new PrintWriter(sw));
		build(context, "Exception occurred!", sw.toString());
	}
}
