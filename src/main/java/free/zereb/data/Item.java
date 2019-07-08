package free.zereb.data;

public class Item {

    private double physDps = 0;
    private double chaosDps = 0;
    private double eleDps = 0;
    private double Q30Total;
    private double Q20Total;
    private double Total;
    private double Q20Phys;
    private double Q30Phys;
    private String name;
    private String rarity;

    public final String data;

    public Item(String data){
        this.data = data;
        Damage physical = null;
        Damage chaos = null;
        Damage cold = null;
        Damage lighting = null;
        Damage fire = null;

        int physIncreased = 0;
        double attackSpeed = 0;
        int quality = 0;

        System.out.println(data);

        int i = 0;
        for (String line: data.split("\\R")){
            i++;
            if (i == 2)
                name = line;
            if (line.contains("Rarity"))
                rarity = line.substring(line.indexOf(":")).trim();
            if (line.contains("Quality"))
                quality = Integer.parseInt(line.replaceAll("\\D", ""));
            if (line.contains("Physical Damage:"))
                physical = new Damage(line);
            if (line.contains("Chaos Damage:"))
                chaos = new Damage(line);
            if (line.matches("Adds \\d* to \\d* Fire Damage"))
                fire = new Damage(line.replaceAll(" to ", "-"));
            if (line.matches("Adds \\d* to \\d* Cold Damage"))
                cold = new Damage(line.replaceAll(" to ", "-"));
            if (line.matches("Adds \\d* to \\d* Lightning Damage"))
                lighting = new Damage(line.replaceAll(" to ", "-"));
            if (line.contains("Attacks per Second:"))
                attackSpeed = Double.parseDouble(line.substring(line.indexOf(":")+1).replaceAll("\\(augmented\\)", "").trim());
            if (line.contains("increased Physical Damage"))
                physIncreased = Integer.parseInt(line.replaceAll("\\D", ""));
        }

        if (physical != null)
            physDps = physical.avg * attackSpeed;
        if (chaos != null)
            chaosDps = chaos.avg * attackSpeed;
        if (fire != null)
            eleDps = fire.avg * attackSpeed;
        if (cold != null)
            eleDps = eleDps + cold.avg * attackSpeed;
        if (lighting != null)
            eleDps = eleDps + lighting.avg * attackSpeed;


        Q20Phys = physDps * (physIncreased + 120) / (physIncreased + quality + 100);
        Q30Phys = physDps * (physIncreased + 130) / (physIncreased + quality + 100);
        Total = physDps + chaosDps + eleDps;
        Q20Total = Q20Phys + chaosDps + eleDps;
        Q30Total = Q30Phys + chaosDps + eleDps;

    }

    public String toString(){
        return String.format("Physical dps: %.2f Q20: %.2f Q30: %.2f \n" +
                "Chaos dps: %.2f Elemental dps: %.2f \n" +
                "Total dps: %.2f Q20: %.2f Q30: %.2f", physDps, Q20Phys, Q30Phys, chaosDps, eleDps, Total, Q20Total, Q30Total);
    }
}
