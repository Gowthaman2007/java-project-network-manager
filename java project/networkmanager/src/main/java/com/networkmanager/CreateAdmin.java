
package com.networkmanager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Scanner;

public class CreateAdmin {

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        System.out.print("Create admin username: ");
        String username = sc.nextLine().trim();

        System.out.print("Create admin password: ");
        String password = sc.nextLine();

        if (username.isEmpty() || password.length() < 12) {
            System.out.println(
                    "Username is required; password must be "
                    + "at least 12 characters."
            );
            return;
        }

        String sql =
                "INSERT INTO app_users "
                + "(username, password_hash, role) "
                + "VALUES (?, ?, 'ADMIN')";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(
                    1, username
            );

            ps.setString(
                    2, PasswordUtil.hashPassword(password)
            );

            ps.executeUpdate();

            System.out.println("Administrator created!");

        } catch (Exception e) {
            System.out.println(
                    "Could not create administrator: "
                    + e.getMessage()
            );
        }
    }
}
