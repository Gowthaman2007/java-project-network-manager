
package com.networkmanager;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class LoginFX extends Application {

    private final LoginService loginService = new LoginService();

    @Override
    public void start(Stage stage) {

        Label icon = new Label("🛡");
        icon.setFont(Font.font(42));

        Label title = new Label("NETWATCH");
        title.setFont(Font.font("Arial", 28));
        title.setTextFill(Color.web("#60A5FA"));

        Label subtitle = new Label(
                "LAB NETWORK MONITORING SYSTEM");
        subtitle.setTextFill(Color.LIGHTGRAY);

        TextField username = new TextField();
        username.setPromptText("Administrator username");
        username.setMaxWidth(300);

        PasswordField password = new PasswordField();
        password.setPromptText("Password");
        password.setMaxWidth(300);

        Label message = new Label();
        message.setTextFill(Color.web("#F87171"));
        message.setWrapText(true);

        Button login = new Button("SIGN IN");
        login.setMaxWidth(300);
        login.setPrefHeight(42);
        login.setStyle(
                "-fx-background-color: #2563EB;" +
                "-fx-text-fill: white;" +
                "-fx-font-weight: bold;" +
                "-fx-background-radius: 8;"
        );

        login.setOnAction(e -> {

            String user = username.getText().trim();
            String pass = password.getText();

            if (user.isEmpty() || pass.isEmpty()) {
                message.setText(
                        "Enter your username and password.");
                return;
            }

            String role = loginService.authenticate(user, pass);

            if (role != null) {

                message.setText("");

                try {
                    // Open the existing monitoring dashboard.
                    NetworkManagerFX dashboard =
                            new NetworkManagerFX();

                    dashboard.start(stage);

                    stage.setTitle(
                            "NETWATCH - " + user + " (" + role + ")");

                } catch (Exception ex) {
                    message.setText(
                            "Unable to open dashboard.");
                    ex.printStackTrace();
                }

            } else {
                message.setText(
                        "Invalid credentials or database unavailable.");
            }
        });

        VBox card = new VBox(
                16,
                icon,
                title,
                subtitle,
                username,
                password,
                login,
                message
        );

        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(35));
        card.setMaxWidth(390);
        card.setStyle(
                "-fx-background-color: #172033;" +
                "-fx-background-radius: 18;"
        );

        VBox root = new VBox(card);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(25));
        root.setStyle("-fx-background-color: #0B1120;");

        Scene scene = new Scene(root, 850, 600);

        stage.setTitle("NETWATCH - Administrator Login");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
