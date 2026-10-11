package main;

import javafx.application.Application;
import javafx.stage.Stage;
import main.tool.InventoryManager;
import main.ui.Login;
import main.thread.OnlineOrderThread;


public class Main extends Application {

    private InventoryManager inventoryManager;
    private OnlineOrderThread onlineOrderThread;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void init() throws Exception {

        inventoryManager = new InventoryManager();
        inventoryManager.InitDataFromDB();

        System.out.println("系统启动");
    }

    @Override
    public void start(Stage primaryStage) {
        onlineOrderThread = new OnlineOrderThread(inventoryManager);
        onlineOrderThread.setDaemon(true);
        onlineOrderThread.start();

        Login loginView = new Login(primaryStage, inventoryManager);

        primaryStage.setScene(loginView.getScene());
        primaryStage.setTitle("鲜花收银与管理系统");
        primaryStage.show();
    }

    @Override
    public void stop() throws Exception {
        System.out.println("系统关闭");

    }
}