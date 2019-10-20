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

import org.json.JSONException;
import org.json.JSONObject;

public class NotificationType {
	private String title;
	private String body;
	private String timestamp;
	public NotificationType(String _title, String _body){
		this.title = _title;
		this.body = _body;
	}
	public NotificationType(JSONObject j) throws JSONException {
		this.title = j.getString("title");
		this.body = j.getString("body");
		this.timestamp = j.getString("timestamp");
	}

	public String getTitle() {
		return this.title;
	}

	public String getBodyShort() {
		if (getBody().length() > 40) {
			return getBody().substring(0, 40) + "...";
		}
		return getBody();
	}

	public String getTimestamp() {
		return this.timestamp;
	}

	public String getBody() {
		return this.body;
	}

	@Override
	public String toString() {
		return this.title;
	}
}
