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
package org.example.u.noticedemo;

import android.content.Context;

public class HelpMessageSupport {
	private static String ERROR_INVALID_PASSWORD_OR_USER;
	private static String ERROR_USERNAME_ALREADY_EXIST;
	private static String ERROR_USERNAME_TOO_LONG;
	private static String ERROR_USER_SESSION_INVALID;
	private static String ERROR_USER_SESSION_EXPIRED;
	private static String ERROR_UNKNOWN_ERROR;
	static void init_strings(Context context) {
		ERROR_INVALID_PASSWORD_OR_USER = context.getResources().getString(R.string.help_message_1);
		ERROR_USERNAME_ALREADY_EXIST = context.getResources().getString(R.string.help_message_2);
		ERROR_USERNAME_TOO_LONG = context.getResources().getString(R.string.help_message_3);
		ERROR_USER_SESSION_INVALID = context.getResources().getString(R.string.help_message_4);
		ERROR_USER_SESSION_EXPIRED = context.getResources().getString(R.string.help_message_5);
		ERROR_UNKNOWN_ERROR = context.getResources().getString(R.string.help_message_default);
	}
	static String getHelperMessageFromErrorCode(int error_code) {
		switch (error_code) {
			case 1:
				return ERROR_INVALID_PASSWORD_OR_USER;
			case 2:
				return ERROR_USERNAME_ALREADY_EXIST;
			case 3:
				return ERROR_USERNAME_TOO_LONG;
			case 4:
				return ERROR_USER_SESSION_INVALID;
			case 5:
				return ERROR_USER_SESSION_EXPIRED;
			default:
				return ERROR_UNKNOWN_ERROR;
		}
	}
}
