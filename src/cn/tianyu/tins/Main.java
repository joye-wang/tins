package cn.tianyu.tins;

import cn.tianyu.tins.ast.TopDefNode;
import cn.tianyu.tins.symbol.SymbolTable;
import cn.tianyu.tins.type.Token;

public class Main {

    public static void main(String[] args) {
        String testFile = "sample/test1.tins";
        testLexer(testFile);
        // testParser(testFile);
        // testPreParser(testFile);
    }

    public static void testLexer(String fileName) {
        Lexer lexer = new Lexer(fileName);
        /*Token token = lexer.next();
        while (token.type != Token.Type.END) {
            System.out.println(token.type.name());
            token = lexer.next();
        }*/
        System.out.println(lexer.lookAheadIgnoreLineBreak(0).type.name());
        System.out.println(lexer.lookAheadIgnoreLineBreak(1).type.name());
        System.out.println(lexer.lookAheadIgnoreLineBreak(2).type.name());
        System.out.println(lexer.lookAheadIgnoreLineBreak(3).type.name());
        System.out.println(lexer.lookAheadIgnoreLineBreak(4).type.name());
        System.out.println(lexer.lookAheadIgnoreLineBreak(5).type.name());
        System.out.println(lexer.peekIgnoreLineBreak().type.name());
        System.out.println(lexer.peekIgnoreLineBreak().type.name());
        System.out.println(lexer.nextIgnoreLineBreak().type.name());
        System.out.println(lexer.nextIgnoreLineBreak().type.name());
        System.out.println(lexer.peekIgnoreLineBreak().type.name());
        System.out.println(lexer.nextIgnoreLineBreak().type.name());
    }

    public static void testParser(String fileName) {
        Parser parser = new Parser(fileName);
        TopDefNode ast = parser.parser();
        // ast.dump();
    }

    public static void testPreParser(String fileName) {
        Lexer lexer = new Lexer(fileName);
        DefParser defParser = new DefParser(lexer);
        defParser.preParser();
        // 输出符号表
        SymbolTable.dumpSymbol();
    }

}
