package ru.vaszol;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;
import java.nio.file.Path;
import java.util.Map;

public class Main extends Application {
    private Stage stage;
    private Map<String, Long> sizes;
    private ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
    private PieChart pieChart;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        this.stage = stage;
        stage.setTitle("Анализатор диска");

        Button button = new Button("Выберите каталог");
        button.setOnAction(actionEvent -> {
            File directory = new DirectoryChooser().showDialog(stage);
            String path = directory.getPath();
            sizes = new Analizer().calculateDirectorySize(Path.of(path));
            buildChart(path);
        });

        StackPane pane = new StackPane();
        pane.getChildren().add(button);
        stage.setScene(new Scene(pane, 300, 250));
        stage.show();
    }

    private void buildChart(String path) {
        pieChart = new PieChart(pieChartData);

        refillChart(path);

        Button backButton = new Button(path);
        backButton.setOnAction(actionEvent -> refillChart(path));

        BorderPane borderPane = new BorderPane();
        borderPane.setTop(backButton);
        borderPane.setCenter(pieChart);

        stage.setScene(new Scene(borderPane, 900, 600));
        stage.show();
    }

    private void refillChart(String path) {
        pieChartData.clear();
        pieChartData.addAll(
                sizes
                        .entrySet()
                        .parallelStream()
                        .filter(stringLongEntry -> {
                            Path parent = Path.of(stringLongEntry.getKey()).getParent();
                            return parent != null && parent.toString().equals(path);
                        })
                        .map(stringLongEntry -> new PieChart.Data(stringLongEntry.getKey(), stringLongEntry.getValue()))
                        .toList()
        );
        pieChart.getData().forEach(data -> {
            data.getNode()
                    .addEventHandler(
                            MouseEvent.MOUSE_PRESSED,
                            event -> refillChart(data.getName())
                    );
        });
    }
}