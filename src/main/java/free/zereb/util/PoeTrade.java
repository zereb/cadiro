package free.zereb.util;

import free.zereb.Cadiro;
import free.zereb.data.Item;
import free.zereb.utils.Util;

import javax.swing.*;
import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Base64;
import java.util.Iterator;
import java.util.List;

public class PoeTrade {
    private Cadiro cadiro;
    private HttpClient httpClient;
    private Item item;

    public PoeTrade(Item item, Cadiro cadiro) {
        this.cadiro = cadiro;
        this.item = item;

        httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(15))
                .build();


        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://www.pathofexile.com/api/trade/search/Hardcore%20Delirium"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString("{\"query\":{\"status\":{\"option\":\"online\"},\"term\":\"" + item.name + "\",\"stats\":[{\"type\":\"and\",\"filters\":[],\"disabled\":true}],\"filters\":{\"trade_filters\":{\"disabled\":false,\"filters\":{\"sale_type\":{\"option\":\"priced\"}}}}},\"sort\":{\"price\":\"asc\"}}"))
                .build();


        httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenAccept(response -> {
                    StringBuilder guiResult = new StringBuilder();
                    ResponseFirst responseFirst = cadiro.gson.fromJson(response.body(), ResponseFirst.class);
                    guiResult.append("\n Total:").append(responseFirst.total).append("\n");
                    if (responseFirst.total < 9)
                        guiResult.append(secondRequest(responseFirst, 0, responseFirst.total - 1));
                    else {
                        guiResult.append("-=== cheap ===-\n").append(secondRequest(responseFirst, 0, 3));
                        guiResult.append("-=== middle ===-\n").append(secondRequest(responseFirst, responseFirst.total / 2 - 2, responseFirst.total / 2 + 1));
                        guiResult.append("-=== expensive ===-\n").append(secondRequest(responseFirst, responseFirst.total - 4, responseFirst.total - 1));
                    }
                    System.out.println(guiResult);

                    SwingUtilities.invokeLater(() -> {
                        cadiro.labelPricecheck.setText(Util.swingLabelNewlines(guiResult.toString()));
                        cadiro.frame.pack();
                    });
                });
    }

    public String secondRequest(ResponseFirst responseFirst, int first, int last) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://www.pathofexile.com/api/trade/fetch/" + responseFirst.result.subList(first, last).toString().replaceAll("[\\[\\] ]", "") + "?query=" + responseFirst.id))
                .header("Content-Type", "application/json")
                .build();

        System.out.println(request.uri());
        StringBuilder guiResult = new StringBuilder();
        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            ResponseSecond responseSecond = cadiro.gson.fromJson(response.body(), ResponseSecond.class);
            System.out.println(responseSecond);

            for (ResponseSecond.Result result : responseSecond.result) {
                guiResult.append(item.name).append(result.toString()).append("\n");
            }
        } catch (IOException | InterruptedException e) {
            guiResult.append(e.getMessage());
            e.printStackTrace();
        }

        return guiResult.toString();
    }

    class ResponseFirst {
        public String id;
        public String complexity;
        public List<String> result;
        public int total;
    }

    public class ResponseSecond {
        public List<Result> result;


        public class Result {
            public String toString() {
                String corrupted = "";
                String price = "null";
                if (listing.price != null) price = listing.price.amount + " " + listing.price.currency;
                if (item.corrupted) corrupted = "corrupted";
                return String.format(" %s - %s ilvl: %d", corrupted, price, item.ilvl);
            }

            public Item item;
            public Listing listing;

            public class Item {
                public boolean corrupted;
                public String name;
                public int ilvl;
            }

            public class Listing {
                public String indexed;
                public Price price;

                public class Price {
                    public float amount;
                    public String currency;
                }
            }
        }
    }
}
