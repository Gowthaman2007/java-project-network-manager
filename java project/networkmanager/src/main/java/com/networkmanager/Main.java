package com.networkmanager;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);
        DeviceDAO dao = new DeviceDAO();

        while (true) {

            System.out.println("\n===== NETWORK MANAGER =====");
            System.out.println("1. Add Device");
            System.out.println("2. View Devices");
            System.out.println("3. Search Device");
            System.out.println("4. Update Status");
            System.out.println("5. Delete Device");
            System.out.println("6. Exit");
            System.out.print("Enter choice: ");

            int choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {

            case 1:
                System.out.print("Device Name: ");
                String name = sc.nextLine();

                System.out.print("IP Address: ");
                String ip = sc.nextLine();

                System.out.print("MAC Address: ");
                String mac = sc.nextLine();

                System.out.print("Status: ");
                String status = sc.nextLine();

                dao.addDevice(new Device(name, ip, mac, status));
                break;

            case 2:
                dao.viewDevices();
                break;

            case 3:
                System.out.print("Enter IP Address: ");
                ip = sc.nextLine();
                dao.searchDevice(ip);
                break;

            case 4:
                System.out.print("Enter Device ID: ");
                int id = sc.nextInt();
                sc.nextLine();

                System.out.print("Enter New Status: ");
                status = sc.nextLine();

                dao.updateDevice(id, status);
                break;

            case 5:
                System.out.print("Enter Device ID: ");
                id = sc.nextInt();

                dao.deleteDevice(id);
                break;

            case 6:
                System.out.println("Thank you!");
                sc.close();
                return;

            default:
                System.out.println("Invalid choice!");
            }
        }
    }
}