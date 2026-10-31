package com.networkmanager;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;

public class NetworkManagerFX extends Application {

    private final TableView<Device> table = new TableView<>();
    private final DeviceDAO dao = new DeviceDAO();
    private final NetworkScanner scanner = new NetworkScanner();

    private final Label totalLabel = new Label("Total: 0");
    private final Label onlineLabel = new Label("Online: 0");
    private final Label offlineLabel = new Label("Offline: 0");

    @Override
    public void start(Stage stage) {

        // ---------- INPUT FIELDS ----------
        TextField name = new TextField();
        name.setPromptText("Device Name");

        TextField ip = new TextField();
        ip.setPromptText("IP Address");

        TextField mac = new TextField();
        mac.setPromptText("MAC Address");

        TextField status = new TextField();
        status.setPromptText("Status");

        // ---------- SEARCH ----------
        TextField search = new TextField();
        search.setPromptText("Search IP Address");

        Button searchButton = new Button("Search");

        // ---------- BUTTONS ----------
        Button add = new Button("Add Device");
        Button update = new Button("Update Status");
        Button delete = new Button("Delete Selected");
        Button refresh = new Button("Refresh");
        Button scan = new Button("Scan Network");

        // ---------- TABLE COLUMNS ----------
        TableColumn<Device, Integer> id =
                new TableColumn<>("ID");

        id.setCellValueFactory(
                new PropertyValueFactory<>("id"));

        TableColumn<Device, String> nameColumn =
                new TableColumn<>("Name");

        nameColumn.setCellValueFactory(
                new PropertyValueFactory<>("deviceName"));

        TableColumn<Device, String> ipColumn =
                new TableColumn<>("IP");

        ipColumn.setCellValueFactory(
                new PropertyValueFactory<>("ipAddress"));

        TableColumn<Device, String> macColumn =
                new TableColumn<>("MAC");

        macColumn.setCellValueFactory(
                new PropertyValueFactory<>("macAddress"));

        TableColumn<Device, String> statusColumn =
                new TableColumn<>("Status");

        statusColumn.setCellValueFactory(
                new PropertyValueFactory<>("status"));

        table.getColumns().addAll(
                id,
                nameColumn,
                ipColumn,
                macColumn,
                statusColumn
        );

        // ---------- SELECT TABLE ROW ----------
        table.getSelectionModel()
                .selectedItemProperty()
                .addListener((obs, oldDevice, selected) -> {

                    if (selected != null) {

                        name.setText(
                                selected.getDeviceName());

                        ip.setText(
                                selected.getIpAddress());

                        mac.setText(
                                selected.getMacAddress());

                        status.setText(
                                selected.getStatus());
                    }
                });

        // ---------- ADD DEVICE ----------
        add.setOnAction(e -> {

            if (name.getText().isEmpty()
                    || ip.getText().isEmpty()) {

                showMessage(
                        "Please enter Device Name and IP Address.");

                return;
            }

            Device device = new Device(
                    name.getText(),
                    ip.getText(),
                    mac.getText(),
                    status.getText()
            );

            dao.addDevice(device);

            clearFields(
                    name,
                    ip,
                    mac,
                    status
            );

            loadData();
        });

        // ---------- UPDATE ----------
        update.setOnAction(e -> {

            Device selected =
                    table.getSelectionModel()
                            .getSelectedItem();

            if (selected == null) {

                showMessage(
                        "Please select a device.");

                return;
            }

            dao.updateDevice(
                    selected.getId(),
                    status.getText()
            );

            loadData();
        });

        // ---------- DELETE ----------
        delete.setOnAction(e -> {

            Device selected =
                    table.getSelectionModel()
                            .getSelectedItem();

            if (selected == null) {

                showMessage(
                        "Please select a device.");

                return;
            }

            dao.deleteDevice(
                    selected.getId());

            clearFields(
                    name,
                    ip,
                    mac,
                    status
            );

            loadData();
        });

        // ---------- REFRESH ----------
        refresh.setOnAction(e -> loadData());

        // ---------- SEARCH ----------
        searchButton.setOnAction(e -> {

            String text =
                    search.getText().trim();

            if (text.isEmpty()) {

                loadData();

            } else {

                searchDevice(text);
            }
        });

        // ---------- NETWORK SCAN ----------
        scan.setOnAction(e -> {

            scan.setDisable(true);
            scan.setText("Scanning...");

            table.getItems().clear();

            totalLabel.setText("Total: 0");
            onlineLabel.setText("Online: 0");
            offlineLabel.setText("Offline: 0");

            Thread thread = new Thread(() -> {

                List<Device> devices =
                        scanner.scan();

                Platform.runLater(() -> {

                    ObservableList<Device> list =
                            FXCollections.observableArrayList(
                                    devices
                            );

                    table.setItems(list);

                    int total =
                            devices.size();

                    totalLabel.setText(
                            "Total: " + total
                    );

                    onlineLabel.setText(
                            "Online: " + total
                    );

                    offlineLabel.setText(
                            "Offline: 0"
                    );

                    scan.setDisable(false);
                    scan.setText("Scan Network");

                    System.out.println(
                            "Scan completed: "
                                    + total
                                    + " devices found."
                    );
                });

            });

            thread.setDaemon(true);
            thread.start();
        });

        // ---------- SUMMARY ----------
        HBox summary = new HBox(
                30,
                totalLabel,
                onlineLabel,
                offlineLabel
        );

        // ---------- SEARCH BOX ----------
        HBox searchBox = new HBox(
                10,
                search,
                searchButton
        );

        // ---------- BUTTON BOX ----------
        HBox buttons = new HBox(
                10,
                add,
                update,
                delete,
                refresh,
                scan
        );

        // ---------- ROOT ----------
        VBox root = new VBox(
                12,
                new Label("NETWORK MANAGER"),
                summary,
                searchBox,
                name,
                ip,
                mac,
                status,
                buttons,
                table
        );

        root.setPadding(
                new Insets(15)
        );

        // ---------- WINDOW ----------
        Scene scene =
                new Scene(
                        root,
                        900,
                        650
                );

        stage.setTitle(
                "Network Manager"
        );

        stage.setScene(scene);

        stage.show();

        // Load database when application starts
        loadData();
    }

    // =====================================================
    // LOAD DATABASE
    // =====================================================

    private void loadData() {

        ObservableList<Device> list =
                FXCollections.observableArrayList();

        int online = 0;
        int offline = 0;

        String sql =
                "SELECT * FROM devices";

        try (
                Connection con =
                        DBConnection.getConnection();

                Statement st =
                        con.createStatement();

                ResultSet rs =
                        st.executeQuery(sql)
        ) {

            while (rs.next()) {

                String deviceStatus =
                        rs.getString("status");

                Device device =
                        new Device(
                                rs.getString("device_name"),
                                rs.getString("ip_address"),
                                rs.getString("mac_address"),
                                deviceStatus
                        );

                device.setId(
                        rs.getInt("id"));

                list.add(device);

                if (deviceStatus != null
                        && deviceStatus.equalsIgnoreCase("online")) {

                    online++;

                } else {

                    offline++;
                }
            }

            table.setItems(list);

            totalLabel.setText(
                    "Total: " + list.size());

            onlineLabel.setText(
                    "Online: " + online);

            offlineLabel.setText(
                    "Offline: " + offline);

        } catch (Exception e) {

            e.printStackTrace();

            showMessage(
                    "Unable to load database.");
        }
    }

    // =====================================================
    // SEARCH DEVICE
    // =====================================================

    private void searchDevice(String text) {

        ObservableList<Device> result =
                FXCollections.observableArrayList();

        for (Device device :
                table.getItems()) {

            if (
                    device.getIpAddress()
                            .toLowerCase()
                            .contains(text.toLowerCase())

                    ||

                    device.getDeviceName()
                            .toLowerCase()
                            .contains(text.toLowerCase())
            ) {

                result.add(device);
            }
        }

        table.setItems(result);

        if (result.isEmpty()) {

            showMessage(
                    "No matching device found.");
        }
    }

    // =====================================================
    // CLEAR INPUT FIELDS
    // =====================================================

    private void clearFields(
            TextField name,
            TextField ip,
            TextField mac,
            TextField status) {

        name.clear();
        ip.clear();
        mac.clear();
        status.clear();

        table.getSelectionModel()
                .clearSelection();
    }

    // =====================================================
    // MESSAGE BOX
    // =====================================================

    private void showMessage(
            String message) {

        Alert alert =
                new Alert(
                        Alert.AlertType.INFORMATION);

        alert.setTitle(
                "Network Manager");

        alert.setHeaderText(null);

        alert.setContentText(
                message);

        alert.showAndWait();
    }

    // =====================================================
    // MAIN
    // =====================================================

    public static void main(
            String[] args) {

        launch(args);
    }
}