def generate_error_dict(code: int, info: str):
    return {'code': code, 'info': info}

def SUCCESS_LOGIN():
    return (200, [], generate_error_dict(0, ''))

def SUCCESS_REGISTER():
    return (200, [], generate_error_dict(0, ''))

def ERROR_INVALID_PASSWORD_OR_USER():
    return (403, [], generate_error_dict(1, 'Invalid password or username'))

def ERROR_USERNAME_ALREADY_EXIST():
    return (400, [], generate_error_dict(2, 'Username already exist'))

def ERROR_USERNAME_TOO_LONG():
    return (400, [], generate_error_dict(3, 'User name should be shorter then 16'))