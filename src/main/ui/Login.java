package main.ui;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import main.classes.User;
import main.tool.InventoryManager;
import main.tool.ConSQL;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Login {

    private Stage primaryStage;
    private InventoryManager inventoryManager;

    public Login(Stage primaryStage, InventoryManager inventoryManager) {
        this.primaryStage = primaryStage;
        this.inventoryManager = inventoryManager;
    }

    public Scene getScene() {
        GridPane grid = new GridPane();
        grid.setAlignment(javafx.geometry.Pos.CENTER);
        grid.setVgap(10);
        grid.setHgap(10);

        Label title = new Label("欢迎使用鲜花收银系统");
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        grid.add(title, 0, 0, 2, 1);

        Label accLabel = new Label("账号:");
        TextField accField = new TextField();
        accField.setPromptText("请输入账号");
        grid.add(accLabel, 0, 1);
        grid.add(accField, 1, 1);

        Label pwdLabel = new Label("密码:");
        PasswordField pwdField = new PasswordField();
        pwdField.setPromptText("请输入密码");
        grid.add(pwdLabel, 0, 2);
        grid.add(pwdField, 1, 2);

        Label msgLabel = new Label();
        msgLabel.setStyle("-fx-text-fill: red;");
        grid.add(msgLabel, 1, 3);

        Button loginBtn = new Button("登 录");
        grid.add(loginBtn, 1, 4);

        loginBtn.setOnAction(e -> {
            String account = accField.getText().trim();
            String pwd = pwdField.getText().trim();

            if (account.isEmpty() || pwd.isEmpty()) {
                msgLabel.setText("账号或密码不能为空！");
                return;
            }

            User user = authenticate(account, pwd);

            if (user != null) {
                Pos posView = new Pos(primaryStage, user, inventoryManager);
                primaryStage.setScene(posView.getScene());
            } else {
                msgLabel.setText("账号或密码错误！");
            }
        });

        return new Scene(grid, 400, 300);
    }


    private User authenticate(String account, String password) {
        String sql = "SELECT account, password, balance, is_vip FROM users WHERE account = ? AND password = ?";

        try (Connection conn = ConSQL.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, account);
            pstmt.setString(2, password);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String acc = rs.getString("account");
                    String pwd = rs.getString("password");
                    double balance = rs.getDouble("balance");
                    boolean isVip = rs.getBoolean("is_vip");

                    return new User(acc, pwd, balance, isVip);
                }
            }

        } catch (SQLException e) {
            System.err.println("数据库查询失败，错误信息：");
            e.printStackTrace();
        }

        return null;
    }
}