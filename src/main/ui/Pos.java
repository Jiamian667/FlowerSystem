package main.ui;
//购物车
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import main.exception.StockNotEnoughException;
import main.classes.*;
import main.tool.InventoryManager;
import main.tool.MakeOrder;

public class Pos {

    private Stage primaryStage;
    private User currentUser;
    private InventoryManager inventoryManager;
    private MakeOrder makeOrder;
    private Order currentOrder;
    private ObservableList<OrderItem> cartList = FXCollections.observableArrayList();
    private Label totalLabel = new Label("总计: ¥0.00");
    private Label balanceLabel;

    public Pos(Stage primaryStage, User currentUser, InventoryManager inventoryManager) {
        this.primaryStage = primaryStage;
        this.currentUser = currentUser;
        this.inventoryManager = inventoryManager;
        this.makeOrder=new MakeOrder(inventoryManager);
        this.currentOrder = new Order(currentUser);
    }

    public Scene getScene() {
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(10));

        HBox topBox = new HBox(15);
        balanceLabel =new Label(String.format("余额：%.2f",currentUser.getBalance()));
        topBox.getChildren().addAll(
                new Label("当前用户: " + currentUser.getAccount()),
                new Label("VIP状态: " + (currentUser.isVip() ? "是" : "否")),
                balanceLabel
        );
        root.setTop(topBox);

        VBox leftBox = new VBox(10);
        leftBox.getChildren().add(new Label("商品列表:"));
        TableView<Product> productTable = createProductTable();

        Button addBtn = new Button("加入购物车 ->");
        addBtn.setOnAction(e -> {
            Product selected = productTable.getSelectionModel().getSelectedItem();
            if (selected != null) {
                OrderItem item = new OrderItem(selected, 1);
                currentOrder.addItem(item);
                cartList.add(item);
                updateTotal();

            }
        });
        leftBox.getChildren().addAll(productTable, addBtn);
        root.setCenter(leftBox);

        VBox rightBox = new VBox(10);
        rightBox.setPadding(new Insets(0, 0, 0, 20));
        rightBox.getChildren().add(new Label("当前订单 (购物车):"));

        TableView<OrderItem> cartTable = createCartTable();
        cartTable.setItems(cartList);

        Button checkoutBtn = new Button("结 算");
        checkoutBtn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
        checkoutBtn.setOnAction(e -> processCheckout());

        rightBox.getChildren().addAll(cartTable, totalLabel, checkoutBtn);
        root.setRight(rightBox);

        return new Scene(root, 800, 600);
    }

    private void updateTotal() {
        double total = currentOrder.calDiscountPrice();
        totalLabel.setText(String.format("总计: ¥%.2f", total));
    }

    private void processCheckout() {
        if (currentOrder.getItems().isEmpty()) return;
        double totalAmount = currentOrder.calDiscountPrice();
        try {
            MakeOrder.sell(currentOrder, currentUser);


            showAlert(Alert.AlertType.INFORMATION, "结算成功",
                    "已成功扣款！当前剩余余额：" + String.format("¥%", currentUser.getBalance()));

            // 清空购物车、重置界面
            cartList.clear();
            currentOrder = new Order(currentUser);
            updateTotal();
            balanceLabel.setText(String.format("余额: ¥%", currentUser.getBalance()));

        } catch (StockNotEnoughException ex) {
            showAlert(Alert.AlertType.ERROR, "结算失败", ex.getMessage());
        } catch (IllegalArgumentException ex) {
            showAlert(Alert.AlertType.WARNING, "结算提示", ex.getMessage());
        } catch (Exception ex) {
            showAlert(Alert.AlertType.ERROR, "系统错误", "结算异常: " + ex.getMessage());

        }
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private TableView<Product> createProductTable() {
        TableView<Product> table = new TableView<>();

        TableColumn<Product, String> nameCol = new TableColumn<>("名称");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<Product, String> typeCol = new TableColumn<>("类型");
        typeCol.setCellValueFactory(cellData -> {
            Product p = cellData.getValue();
            if (p instanceof CutFlower) {
                return new SimpleStringProperty("鲜切花");
            } else if (p instanceof Bouquet) {
                return new SimpleStringProperty("成品花束");
            }
            return new SimpleStringProperty("普通商品");
        });

        TableColumn<Product, Double> priceCol = new TableColumn<>("原价");
        priceCol.setCellValueFactory(new PropertyValueFactory<>("originalPrice"));

        TableColumn<Product, String> stockCol = new TableColumn<>("库存/状态");
        stockCol.setCellValueFactory(cellData -> {
            Product p = cellData.getValue();
            if (p instanceof CutFlower) {
                return new SimpleStringProperty(String.valueOf(((CutFlower) p).getStock()));
            } else if (p instanceof Bouquet) {
                return new SimpleStringProperty("配方组合");
            }
            return new SimpleStringProperty("-");
        });

        table.getColumns().addAll(nameCol, typeCol, priceCol, stockCol);

        ObservableList<Product> allProducts = FXCollections.observableArrayList();

        allProducts.addAll(inventoryManager.getGlobalStock().values());


        if (inventoryManager.getBouquetList() != null) {
            allProducts.addAll(inventoryManager.getBouquetList());
        }

        table.setItems(allProducts);
        return table;
    }

    private TableView<OrderItem> createCartTable() {
        TableView<OrderItem> table = new TableView<>();

        TableColumn<OrderItem, String> nameCol = new TableColumn<>("商品名称");
        nameCol.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getProduct().getName())
        );

        TableColumn<OrderItem, Double> priceCol = new TableColumn<>("原价");
        priceCol.setCellValueFactory(cellData ->
                new SimpleObjectProperty<>(cellData.getValue().getProduct().getOriginalPrice())
        );

        TableColumn<OrderItem, Integer> qtyCol = new TableColumn<>("数量");
        qtyCol.setCellValueFactory(new PropertyValueFactory<>("quantity"));


        table.getColumns().addAll(nameCol, priceCol, qtyCol);
        return table;
    }
}