
package com.networkmanager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class LoginService {

    public String authenticate(
            String username, String password) {

        String sql =
                "SELECT password_hash, role "
                + "FROM app_users WHERE username = ?";

        try (Connection con = DBConnection.getConnection()) {

            if (con == null) {
                return null;
            }

            try (PreparedStatement ps =
                    con.prepareStatement(sql)) {

                ps.setString(1, username.trim());

                try (ResultSet rs = ps.executeQuery()) {

                    if (rs.next()
                            && PasswordUtil.verifyPassword(
                                    password,
                                    rs.getString("password_hash"))) {

                        return rs.getString("role");
                    }
                }
            }

        } catch (Exception e) {
            System.err.println(
                    "Login error: " + e.getMessage()
            );
        }

        return null;
    }
}
