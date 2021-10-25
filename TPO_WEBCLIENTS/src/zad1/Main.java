/**
 *
 *  @author Kotnowski Borys S20610
 *
 */

package zad1;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

import java.util.Objects;

public class Main extends Application{

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("sample.fxml")));
        primaryStage.setTitle("Wyjazd");
        Service s = new Service("Poland");
        String weatherJson = s.getWeather("Warsaw");
        Double rate1 = s.getRateFor("USD");
        Double rate2 = s.getNBPRate();
        Label label = new Label(rate1.toString());
        Label label2 = new Label(rate2.toString());
        String wikiurl = "https://en.wikipedia.org/wiki/"+ s.city;
        WebView webView = new WebView();
        webView.getEngine().load(wikiurl);
        VBox vBox = new VBox(webView);
        vBox.getChildren().add(label);
        vBox.getChildren().add(label2);
        Scene scene = new Scene(vBox, 960, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
    }


    public static void main(String[] args) {

        launch(args);
    }
}
