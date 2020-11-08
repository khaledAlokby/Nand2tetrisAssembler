package nand2titres;

public class Assembler {
    public static void main(String[] args) throws Exception {
        String path = "C:\\Users\\kalokby\\OneDrive - Ribbon Communications\\Desktop\\nand2tetris\\nand2tetris\\projects\\06\\pong\\Pong.asm";
        Code code = new Code(path);
        code.convertCode();
        Code.scanned = true;
        code = new Code(path);
        code.convertCode();
    }
}
