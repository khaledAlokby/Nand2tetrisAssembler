package nand2titres;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Code {
    private final Parser parser;
    private final FileWriter hackFile;
    public static boolean scanned = false;
    String newLine = System.lineSeparator();
    static Map<String, String> compMap;
    static SymbolTable symbolTable = new SymbolTable();

    static {
        compMap = new HashMap<>();
        compMap.put("0", "0101010");
        compMap.put("1", "0111111");
        compMap.put("-1", "0111010");
        compMap.put("D", "0001100");
        compMap.put("A", "0110000");
        compMap.put("!D", "0001101");
        compMap.put("!A", "0110001");
        compMap.put("-D", "0001111");
        compMap.put("-A", "0110011");
        compMap.put("D+1", "0011111");
        compMap.put("A+1", "0110111");
        compMap.put("D-1", "0001110");
        compMap.put("A-1", "0110010");
        compMap.put("D+A", "0000010");
        compMap.put("D-A", "0010011");
        compMap.put("A-D", "0000111");
        compMap.put("D&A", "0000000");
        compMap.put("D|A", "0010101");
        compMap.put("M", "1110000");
        compMap.put("!M", "1110001");
        compMap.put("-M", "1110011");
        compMap.put("M+1", "1110111");
        compMap.put("M-1", "1110010");
        compMap.put("D+M", "1000010");
        compMap.put("D-M", "1010011");
        compMap.put("M-D", "1000111");
        compMap.put("D&M", "1000000");
        compMap.put("D|M", "1010101");
    }

    private int cmdCounter=0;
    private int vars = 16;


    public Code(String fileName) throws IOException {
        this.parser = new Parser(new File(fileName));
        hackFile = new FileWriter("Pong.hack", false);
    }

    public String dest() {
        String d1 = "";
        String d2 = "";
        String d3 = "";

        String mDest = parser.dest();
        if (mDest == null)
            return "000";
        if (mDest.isEmpty() || mDest.equals("null"))
            return "000";
        if (mDest.contains("A")) {
            d1 = "1";
        } else {
            d1 = "0";
        }
        if (mDest.contains("D")) {
            d2 = "1";
        } else {
            d2 = "0";
        }
        if (mDest.contains("M")) {
            d3 = "1";
        } else {
            d3 = "0";
        }

        return d1 + d2 + d3;
    }

    public String comp() throws Exception {
        if (!compMap.containsKey(parser.comp()))
            throw new Exception("comp not found, maybe not a C_instruction, or different order!!");
        return compMap.get(parser.comp());
    }

    public String jump() {
        switch (parser.jump()) {
            case "JGT":
                return "001";
            case "JEQ":
                return "010";
            case "JGE":
                return "011";
            case "JLT":
                return "100";
            case "JNE":
                return "101";
            case "JLE":
                return "110";
            case "JMP":
                return "111";
            default:
                return "000";
        }
    }

    public void convertCode() throws Exception {
        while (parser.hasMoreCommands()){
            parser.advance();
            if (parser.commandType() == CommandType.C_COMMAND){
                if (scanned) {
                    String cmd = "111" + comp() + dest() + jump() + newLine;
                    hackFile.write(cmd);
                }else {
                    cmdCounter++;
                }
            }else if (parser.commandType() == CommandType.A_COMMAND){
                if (scanned) {
                    if (isNum(parser.symbol())) {
                        Integer decimal = Integer.valueOf(parser.symbol());
                        String cmd = Integer.toBinaryString(decimal);
                        while (cmd.length() < 16) {
                            cmd = "0" + cmd;
                        }
                        hackFile.write(cmd + newLine);
                    } else {
                        if (symbolTable.getAddress(parser.symbol()) == -1){
                            symbolTable.addEntry(parser.symbol(),vars++);
                        }
                        Integer address = symbolTable.getAddress(parser.symbol());
                        String cmd = Integer.toBinaryString(address);
                        while (cmd.length() < 16) {
                            cmd = "0" + cmd;
                        }
                        hackFile.write(cmd + newLine);
                    }
                } else {
                    cmdCounter++;
                    if (!symbolTable.contains(parser.symbol())){
                        symbolTable.addEntry(parser.symbol(),-1);
                    }
                }
            }else if (parser.commandType() == CommandType.L_COMMAND){
                if(!scanned){
                    symbolTable.addEntry(parser.symbol(),cmdCounter);
                }
            }

        }
        if (scanned) {
            parser.close();
            hackFile.close();
        }
    }

    private boolean isNum(String str){
        try {
            Integer.parseInt(str);
            return true;
        }catch (NumberFormatException ex){
            return false;
        }
    }
}
