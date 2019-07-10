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
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;

class Poeprices {


    private double min = 0;
    private double max = 0;
    private String curency;
    private String warning;

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
                    String json = response.body()
                            .replaceAll("\\{", "")
                            .replaceAll("}", "")
                            .replaceAll("\"", "");

                    List<String> tokens = Arrays.asList(json.split(","));
                    HashMap<String, String> values = new HashMap<>();
                    tokens.forEach(token -> {
                        if (token.contains(":")) {
                            String[] subTokens = token.split(":");
                            values.putIfAbsent(subTokens[0].trim(), subTokens[1].trim());
                        }
                    });


                    if (values.get("min") != null && values.get("max") != null) {
                        min = Double.parseDouble(values.get("min"));
                        max = Double.parseDouble(values.get("max"));
                    }
                    curency = values.get("currency");
                    warning = values.get("warning_msg");
                    String error = values.get("error_msg");


                    String guiResult = String.format("%.2f - %.2f %s\n%s \n%s", min, max, curency, warning, error);

                    SwingUtilities.invokeLater(() -> {
                        cadiro.frame.pack();
                        cadiro.labelPricecheck.setText(Util.swingLabelNewlines(guiResult));
                    });

                });
    }


}
