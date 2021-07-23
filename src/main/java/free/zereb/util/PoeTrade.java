package free.zereb.util;

import free.zereb.Cadiro;
import free.zereb.data.Item;
import free.zereb.utils.Util;

import javax.swing.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;

public class PoeTrade {
    private final Cadiro cadiro;
    private final HttpClient httpClient;
    private final Item item;

    public PoeTrade(Item item, Cadiro cadiro){
        this.cadiro = cadiro;
        this.item = item;
        URI uri = null;
        try {
            uri = new URI("https", "www.pathofexile.com", "/api/trade/search/" + Cadiro.league, null);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(15))
                .build();

        String reqTemplate;
        if (item.rarity.equals("Unique") && item.links < 5)
            reqTemplate = "{\"query\":{\"status\":{\"option\":\"online\"},\"name\":\"" + item.name + "\",\"stats\":[{\"type\":\"and\",\"filters\":[],\"disabled\":true}],\"filters\":{\"trade_filters\":{\"disabled\":false,\"filters\":{\"sale_type\":{\"option\":\"priced\"}}}}},\"sort\":{\"price\":\"asc\"}}";
        else if (item.rarity.equals("Unique"))
            reqTemplate = "{\"query\":{\"status\":{\"option\":\"online\"},\"name\":\"" + item.name + "\",\"stats\":[{\"type\":\"and\",\"filters\":[]}],\"filters\":{\"socket_filters\":{\"disabled\":false,\"filters\":{\"links\":{\"min\":" + item.links + "}}}}},\"sort\":{\"price\":\"asc\"}}";

        else if(item.rarity.equals("Gem"))
            reqTemplate = "{\"query\":{\"status\":{\"option\":\"online\"},\"type\":\"" + item.name + "\",\"stats\":[{\"type\":\"and\",\"filters\":[],\"disabled\":true}],\"filters\":{\"misc_filters\":{\"disabled\":false,\"filters\":{\"quality\":{\"min\":" + item.quality + "},\"gem_level\":{\"min\":" + item.lvl + "}}}}},\"sort\":{\"price\":\"asc\"}}";
        else
            reqTemplate = "{\"query\":{\"status\":{\"option\":\"online\"},\"type\":\"" + item.name + "\",\"stats\":[{\"type\":\"and\",\"filters\":[]}],\"filters\":{\"trade_filters\":{\"disabled\":true,\"filters\":{\"price\":{\"min\":29}}},\"type_filters\":{\"filters\":{\"rarity\":{\"option\":\"rare\"}},\"disabled\":true}}},\"sort\":{\"price\":\"asc\"}}";

        System.out.println(reqTemplate);


        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(reqTemplate))
                .build();
        System.out.println(request);
        System.out.println(item.name);


        httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenAccept(response -> {
                    System.out.println(response.body());
                    StringBuilder guiResult = new StringBuilder();
                    ResponseFirst responseFirst = cadiro.gson.fromJson(response.body(), ResponseFirst.class);

                    if (responseFirst.error != null){
                        guiResult.append(responseFirst.error.message);
                        SwingUtilities.invokeLater(() -> {
                            cadiro.labelPricecheck.setText(Util.swingLabelNewlines(guiResult.toString()));
                            cadiro.frame.pack();
                        });
                    }

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
        public Error error;
        public String id;
        public String complexity;
        public List<String> result;
        public int total;

        public class Error{
            public int code;
            public String message;
        }
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
