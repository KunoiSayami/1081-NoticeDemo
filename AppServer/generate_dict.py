def generate_error_dict(code: int, info: str):
    return {'code': code, 'info': info}

def SUCCESS_200OK():
    return (200, [], generate_error_dict(0, ''))

def SUCCESS_LOGIN(user_name: str, session_string: str):
    return (200, [user_name, session_string,], generate_error_dict(0, ''))

def SUCCESS_REGISTER():
    return SUCCESS_200OK()

def ERROR_INVALID_PASSWORD_OR_USER():
    return (403, [], generate_error_dict(1, 'Invalid password or username'))

def ERROR_USERNAME_ALREADY_EXIST():
    return (400, [], generate_error_dict(2, 'Username already exist'))

def ERROR_USERNAME_TOO_LONG():
    return (400, [], generate_error_dict(3, 'User name should be shorter then 16'))

def ERROR_USER_SESSION_INVALID():
    return (400, [], generate_error_dict(4, 'User session invalid'))

def ERROR_USER_SESSION_EXPIRED():
    return (400, [], generate_error_dict(5, 'User session expired'))

def SUCCESS_REGISTER_FIREBASE_ID():
    return SUCCESS_200OK()

def SUCCESS_VERIFY_SESSION():
    return SUCCESS_200OK()

def ERROR_USER_SESSION_MISSING():
    return (400, [], generate_error_dict(6, 'Session string missing'))

def SUCCESS_LOGOUT():
    return SUCCESS_200OK()

def ERROR_INVALID_REQUEST():
    return (403, [], generate_error_dict(7, 'Invalid request'))