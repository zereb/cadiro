package free.zereb.utils;

public class Util {

    public static String swingLabelNewlines(String label){
        return "<html>" + label.replaceAll("\n", "<br \\>") + "</html>";
    }
}
