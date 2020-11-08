package nand2titres;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Parser {


    private final Scanner scanner;
    private CommandType currentType;
    private String currentCommand;

    public Parser(File file) throws FileNotFoundException {
        scanner = new Scanner(file);
    }

    public boolean hasMoreCommands(){
        return scanner.hasNext();
    }

    public void advance(){
        currentType = CommandType.NAN;
        while (hasMoreCommands() && currentType==CommandType.NAN){
            String line = scanner.nextLine();
            if (line.isEmpty())
                continue;
            line = line.replaceAll("\\s+","");
            if (line.length() >= 2 && line.charAt(0)=='/' && line.charAt(1)=='/')
                continue;
            String cmd = (line.split("//"))[0];
            currentCommand = cmd;
            if (cmd.startsWith("@")){
                currentType = CommandType.A_COMMAND;
            }else if (cmd.startsWith("(")){
                currentType = CommandType.L_COMMAND;
            }else {
                currentType = CommandType.C_COMMAND;
            }
        }
    }

    public CommandType commandType() {
        return currentType;
    }

    public String symbol(){

        if (currentType == CommandType.A_COMMAND)
            return currentCommand.substring(1);
        if (currentType == CommandType.L_COMMAND)
            return currentCommand.substring(1,currentCommand.length()-1);
        return null;
    }

    public String dest(){
        if (currentType == CommandType.C_COMMAND ){
            if (currentCommand.contains("="))
                return  currentCommand.split("=")[0];
            return "";
        }
        return null;
    }

    public String comp(){
        if (currentType == CommandType.C_COMMAND){
            String operation  = currentCommand.split(";")[0];
            if (operation.contains("="))
                return operation.split("=")[1];
            return operation;
        }
        return null;
    }

    public String jump(){
        if (currentType == CommandType.C_COMMAND){
            if (currentCommand.contains(";"))
                return  currentCommand.split(";")[1];
            return "";
        }
        return null;
    }

    public void close() {
        scanner.close();
    }
}
