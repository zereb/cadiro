package free.zereb.data;

import java.util.LinkedList;
import java.util.List;

public class Item {

    public final String name;
    public final String rarity;
    public final int ilvl;
    public final String data;
    public final String implicit;
    public final List<String> mods;
    public final String flavorText;
    public final String itemClass;
    public final String sockets;
    public final int quality;
    public final int lvl;

    //weapon stats
    public final Damage phys;
    public final Damage fire;
    public final Damage cold;
    public final Damage lighting;
    public final Damage chaos;
    public final double aps;
    public final int physInc;


    public static class Builder{
        private List<String> mods = new LinkedList<>();
        private String flavorText;
        private String implicit;
        private String sockets;
        private Damage phys;
        private Damage fire;
        private Damage cold;
        private Damage lighting;
        private Damage chaos;
        private int quality;
        private int physInc;
        private double aps;
        private int lvl;

        public Item build(String name, String rarity, int ilvl, String itemClass, String data) {

            return new Item(name, rarity, ilvl, data, implicit, mods, flavorText, itemClass, sockets, quality, lvl, phys, fire, cold, lighting, chaos, aps, physInc);
        }

        public Builder lvl(int lvl){
            this.lvl = lvl;
            return this;
        }


        public Builder aps(double aps){
            this.aps = aps;
            return this;
        }

        public Builder physInc(int physInc){
            this.physInc = physInc;
            return this;
        }

        public Builder quality(int quality){
            this.quality = quality;
            return this;
        }

        public Builder sockets(String sockets){
            this.sockets = sockets;
            return this;
        }
        public Builder phys(Damage phys){
            this.phys = phys;
            return this;
        }
        public Builder fire(Damage fire){
            this.fire = fire;
            return this;
        }
        public Builder cold(Damage cold){
            this.cold = cold;
            return this;
        }
        public Builder lighting(Damage lighting){
            this.lighting = lighting;
            return this;
        }
        public Builder chaos(Damage chaos){
            this.chaos = chaos;
            return this;
        }


        public Builder addMod(String mod){
            mods.add(mod);
            return this;
        }

        public Builder flavorText(String flavorText){
            this.flavorText = flavorText;
            return this;
        }

        public Builder implicit(String implicit){
            this.implicit = implicit;
            return this;
        }


    }

    private Item(String name, String rarity, int ilvl, String data, String implicit, List<String> mods, String flavorText, String itemClass, String sockets, int quality, int lvl, Damage phys, Damage fire, Damage cold, Damage lighting, Damage chaos, double aps, int physInc) {
        this.name = name;
        this.rarity = rarity;
        this.ilvl = ilvl;
        this.data = data;
        this.implicit = implicit;
        this.mods = mods;
        this.flavorText = flavorText;
        this.itemClass = itemClass;
        this.sockets = sockets;
        this.quality = quality;
        this.lvl = lvl;
        this.phys = phys;
        this.fire = fire;
        this.cold = cold;
        this.lighting = lighting;
        this.chaos = chaos;
        this.aps = aps;
        this.physInc = physInc;
    }

    @Override
    public String toString() {
        return "Item{ \n" +
                "name='" + name + '\'' +
                ", itemClass='" + itemClass + '\'' +
                ", rarity='" + rarity + '\'' +
                ", sockets='" + sockets + '\'' +
                ". quality = " + quality +
                ", ilvl=" + ilvl +
                ", lvl=" + lvl +
                "\n implicit='" + implicit + '\'' +
                "\n mods=" + mods +
                "\n flavorText='" + flavorText + '\'' +
                "\n phys=" + phys +
                ", fire=" + fire +
                ", cold=" + cold +
                ", lighting=" + lighting +
                ", chaos=" + chaos +
                "\n}";
    }

    public String getDamage() {
        double physDps = 0;
        double chaosDps = 0;
        double eleDps = 0;

        if (phys != null)
            physDps = phys.avg * aps;
        if (chaos != null)
            chaosDps = chaos.avg * aps;
        if (fire != null)
            eleDps = fire.avg * aps;
        if (cold != null)
            eleDps = eleDps + cold.avg * aps;
        if (lighting != null)
            eleDps = eleDps + lighting.avg * aps;

        double Q20Phys = physDps * (physInc + 120) / (physInc + quality + 100);
        double Q30Phys = physDps * (physInc + 130) / (physInc + quality + 100);
        double totalDmg = physDps + chaosDps + eleDps;
        double Q20Total = Q20Phys + chaosDps + eleDps;
        double Q30Total = Q30Phys + chaosDps + eleDps;

        if (totalDmg > 0)
            return String.format("Physical dps: %.2f   Q20: %.2f   Q30: %.2f \n" +
                    "Chaos dps: %.2f   Elemental dps: %.2f \n" +
                    "Total dps: %.2f   Q20: %.2f   Q30: %.2f", phys.avg * aps, Q20Phys, Q30Phys, chaosDps, eleDps, totalDmg, Q20Total, Q30Total);
        else return "";
    }
}
