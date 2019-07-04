package org.zereb.data;

public class Damage {

    public final int low;
    public final int high;
    public final double avg;

    public Damage(String data){


        int separator = data.indexOf("-");
        low = Integer.parseInt(data.substring(0, separator).replaceAll("\\D", ""));
        high = Integer.parseInt(data.substring(separator).replaceAll("\\D", ""));

        avg = (low + high) / 2;
    }

    @Override
    public String toString() {
        return low + "-" + high + "  " + avg;
    }
}
