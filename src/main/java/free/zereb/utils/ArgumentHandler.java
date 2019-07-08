package free.zereb.utils;


import java.util.HashMap;

public class ArgumentHandler {

    private HashMap<String, Argument> arguments = new HashMap<>();


    public ArgumentHandler setArgument(String arg, Argument argument){
        arguments.putIfAbsent(arg, argument);
        return this;
    }

    public void runArgs(String[] args){
        for (String arg: args){
            arguments.forEach((cmd, interf) ->{
                if (arg.matches(cmd))
                    interf.argRun(args);
            });
        }
    }
}

