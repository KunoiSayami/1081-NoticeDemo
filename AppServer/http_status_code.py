class http_status_codes:
	def generate_error_dict(self, code: int, info: str):
		return {'code': code, 'info': info}

	@property
	def SUCCESS_200OK(self):
		return (200, [], self.generate_error_dict(0, ''))

	def SUCCESS_LOGIN(self, user_name: str, session_string: str):
		return (200, [user_name, session_string,], self.generate_error_dict(0, ''))

	@property
	def SUCCESS_REGISTER(self):
		return self.SUCCESS_200OK

	@property
	def ERROR_INVALID_PASSWORD_OR_USER(self):
		return (403, [], self.generate_error_dict(1, 'Invalid password or username'))

	@property
	def ERROR_USERNAME_ALREADY_EXIST(self):
		return (400, [], self.generate_error_dict(2, 'Username already exist'))

	@property
	def ERROR_USERNAME_TOO_LONG(self):
		return (400, [], self.generate_error_dict(3, 'User name should be shorter then 16'))

	@property
	def ERROR_USER_SESSION_INVALID(self):
		return (400, [], self.generate_error_dict(4, 'User session invalid'))

	@property
	def ERROR_USER_SESSION_EXPIRED(self):
		return (400, [], self.generate_error_dict(5, 'User session expired'))

	@property
	def SUCCESS_REGISTER_FIREBASE_ID(self):
		return self.SUCCESS_200OK

	@property
	def SUCCESS_VERIFY_SESSION(self):
		return self.SUCCESS_200OK

	@property
	def ERROR_USER_SESSION_MISSING(self):
		return (400, [], self.generate_error_dict(6, 'Session string missing'))

	@property
	def SUCCESS_LOGOUT(self):
		return self.SUCCESS_200OK

	@property
	def ERROR_INVALID_REQUEST(self):
		return (403, [], self.generate_error_dict(7, 'Invalid request'))

	@property
	def ERROR_400_BAD_REQUEST(self):
		return (400, [], self.generate_error_dict(400, 'Bad Request'))

	@property
	def ERROR_403_FORBIDDEN(self):
		return (403, [], self.generate_error_dict(403, 'Forbidden'))
	
	@property
	def SUCCESS_SEND_FIREBASE_NOTIFICATION(self):
		return self.SUCCESS_200OK
	
	@property
	def ERROR_SEND_FIREBASE_NOTIFICATION_FAILURE(self):
		return (400, [], self.generate_error_dict(1001, 'Send notification failure'))
	
	@property
	def ERROR_SEND_FIREBASE_NOTIFICATION_PARTILAL_FAILURE(self):
		return (400, [], self.generate_error_dict(1002, 'Send notification partial failure'))

	def SUCCESS_FETCH_NOTIFICATIONS(self, jsonObj: str):
		return (200, [jsonObj,], self.generate_error_dict(0, ''))

HTTP_STATUS_CODES = http_status_codes()