package free.zereb.utils;

import free.zereb.data.Damage;
import free.zereb.data.Item;

public class ItemParser {

    private ItemParser(){}

    public static Item parse(String data){
        System.out.println("parsing item: \n" + data);

        if (data == null) throw new NullPointerException("Data is null");

        String name = null,
               rarity = null,
               itemClass = null;
        int ilvl = 0;

        Item.Builder builder = new Item.Builder();

        boolean lvlFlag = false;
        boolean qualityFlag = false;

        for (String line: data.split("\\R")){
            if (rarity != null && name == null) name = line;

            //info after :
            String trim = line.substring(line.indexOf(":") + 1).trim();

            if (line.contains("Rarity:"))
                rarity = trim;

            if (line.contains("Item Level:"))
                ilvl = Integer.parseInt(trim);

            if (line.contains("Item Class:"))
                itemClass = trim;

            if (line.contains("Sockets:")){
                line = trim.replaceAll("[RGBW]", "");
                int max = 1, current = 1;
                for (char c : line.toCharArray()) {
                    if (c == '-') current++;
                    else  current = 0;
                    if (current > max) max = current;
                }
                builder.links(max);
            }



            if (line.contains("Level:") && !lvlFlag){
                builder.lvl(Integer.parseInt(line.replaceAll("\\D", "")));
                lvlFlag = true;
            }

            if (line.contains("Sockets:"))
                builder.sockets(trim);

            if (line.contains("Quality") && !qualityFlag && !line.contains("Additional Effects From Quality:")) {
                builder.quality(Integer.parseInt(line.replaceAll("\\D", "")));
                qualityFlag = true;
            }
            if (line.contains("implicit"))
                builder.implicit(line);

            if (line.contains("Physical Damage:"))
                builder.phys(new Damage(line));
            if (line.contains("Chaos Damage:"))
                builder.chaos(new Damage(line));
            if (line.matches("Adds \\d* to \\d* Fire Damage"))
                builder.fire(new Damage(line));
            if (line.matches("Adds \\d* to \\d* Cold Damage"))
                builder.cold(new Damage(line));
            if (line.matches("Adds \\d* to \\d* Lightning Damage"))
                builder.lighting(new Damage(line));
            if (line.contains("Attacks per Second:"))
                builder.aps(Double.parseDouble(line.substring(line.indexOf(":")+1).replaceAll("\\(augmented\\)", "").trim()));
            if (line.contains("increased Physical Damage"))
                builder.physInc(Integer.parseInt(line.replaceAll("\\D", "")));
        }
        return builder.build(name, rarity, ilvl, itemClass, data);

    }
}
