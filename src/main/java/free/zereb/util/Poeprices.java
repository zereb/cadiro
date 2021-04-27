package free.zereb.util;

import free.zereb.Cadiro;
import free.zereb.data.Item;
import free.zereb.utils.Util;

import javax.swing.*;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Base64;

class Poeprices {


    Poeprices(Item item, Cadiro cadiro) {
        String league = URLEncoder.encode(Cadiro.league, StandardCharsets.UTF_8);
        String data = Base64.getEncoder().encodeToString(item.data.getBytes());
        String urlStr = "https://www.poeprices.info/api?l=" + league + "&i=" + data;

        HttpClient httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(15))
                .build();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(urlStr))
                .build();

        System.out.println(request.toString());

        httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenAccept(response -> {
                    System.out.println(response.body());
                    ResponsePoePricesJson json =  cadiro.gson.fromJson(response.body(), ResponsePoePricesJson.class);

                    String guiResult = String.format("%.2f - %.2f %s \nConfidence: %s \n%s \n%s ",
                            json.min, json.max, json.currency, json.pred_confidence_score, json.warning_msg, json.error_msg);

                    SwingUtilities.invokeLater(() -> {
                        cadiro.labelPricecheck.setText(Util.swingLabelNewlines(guiResult));
                        cadiro.frame.pack();
                    });
                });
    }

    class ResponsePoePricesJson {
        public double min;
        public double max;
        public String currency;
        public String warning_msg;
        public double error;
        public String pred_confidence_score;
        public String error_msg;
    }


}
