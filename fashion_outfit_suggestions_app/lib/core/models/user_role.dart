enum UserRole {
  user,
  admin;

  static UserRole? fromJson(dynamic value) {
    if (value == null) return null;
    switch (value.toString().toUpperCase()) {
      case 'USER':
        return UserRole.user;
      case 'ADMIN':
        return UserRole.admin;
      default:
        return null;
    }
  }

}
