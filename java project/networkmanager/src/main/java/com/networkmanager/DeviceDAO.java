package com.networkmanager;

import java.sql.*;

public class DeviceDAO {

    public void addDevice(Device d) {
        String sql = "INSERT INTO devices(device_name,ip_address,mac_address,status) VALUES(?,?,?,?)";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, d.getDeviceName());
            ps.setString(2, d.getIpAddress());
            ps.setString(3, d.getMacAddress());
            ps.setString(4, d.getStatus());
            ps.executeUpdate();

            System.out.println("Device added!");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateDevice(int id, String status) {
        String sql = "UPDATE devices SET status=? WHERE id=?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, status);
            ps.setInt(2, id);

            if (ps.executeUpdate() > 0)
                System.out.println("Device updated!");
            else
                System.out.println("Device not found!");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deleteDevice(int id) {
        String sql = "DELETE FROM devices WHERE id=?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.executeUpdate();

            System.out.println("Device deleted!");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}