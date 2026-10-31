package com.networkmanager;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class NetworkScanner {

    public List<Device> scan() {

        List<Device> devices = new ArrayList<>();

        try {
            String localIP = InetAddress.getLocalHost().getHostAddress();

            System.out.println("Local IP: " + localIP);

            String base = localIP.substring(0, localIP.lastIndexOf("."));

            ExecutorService pool = Executors.newFixedThreadPool(30);

            List<Future<Device>> results = new ArrayList<>();

            for (int i = 1; i <= 254; i++) {

                final String ip = base + "." + i;

                results.add(pool.submit(() -> {

                    try {
                        InetAddress host =
                                InetAddress.getByName(ip);

                        if (host.isReachable(150)) {

                            String name = host.getHostName();

                            System.out.println(
                                    "Online: " + ip
                            );

                            return new Device(
                                    name,
                                    ip,
                                    "N/A",
                                    "Online"
                            );
                        }

                    } catch (Exception ignored) {
                    }

                    return null;
                }));
            }

            for (Future<Device> result : results) {

                try {
                    Device d = result.get();

                    if (d != null)
                        devices.add(d);

                } catch (Exception ignored) {
                }
            }

            pool.shutdown();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return devices;
    }
}