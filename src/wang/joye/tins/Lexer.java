package wang.joye.tins;

import wang.joye.tins.type.Token;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class Lexer {

    String src;
    /**
     * 源文件中读取的位置
     */
    int srcPosition = 0;
    int line = 1;

    List<Token> readTokens = new LinkedList<>();
    private int nextTokenIndex = 0;
    private static Token END = new Token(Token.Type.END);

    public Lexer(String fileName) {
        readFile(fileName);
        // 直接一次性读取出来所有token
        Token token;
        while ((token = readNextTokenFromSrc()).type != Token.Type.END) {
            readTokens.add(token);
        }
        // 添加最后一个Token[END]
        readTokens.add(token);
    }

    /**
     * 重新读取所有token
     */
    public void restart() {
        nextTokenIndex = 0;
    }

    /**
     * 防止读取的token越界
     */
    private Token get(int position) {
        if (position >= readTokens.size())
            return END;
        return readTokens.get(position);
    }

    /**
     * 即lookAHead(0)
     */
    public Token peek() {
        return get(nextTokenIndex);
    }

    /**
     * 向前看n个token
     * peek()为lookAHead(0)
     */
    public Token lookAhead(int n) {
        return readTokens.get(nextTokenIndex + n);
    }

    public Token match(Token.Type type) {
        Token token = readTokens.get(nextTokenIndex++);
        if (token.type != type) {
            unexpectedToken(token, type);
        }
        return token;
    }

    public Token next() {
        return readTokens.get(nextTokenIndex++);
    }

    /**
     * 直接读取整个文件内容到src
     */
    private void readFile(String fileName) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(fileName));
            StringBuilder builder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                // readLine函数不读入换行符，需要自己添加
                builder.append(line).append('\n');
            }
            // 减去最后一个换行符
            builder.deleteCharAt(builder.length() - 1);
            src = builder.toString();
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

    /**
     * 辅助函数，防止读取越界
     */
    private char charAt(int position) {
        if (position < src.length())
            return src.charAt(position);
        else
            return 0;
    }

    /**
     * 从src读取token
     */
    public Token readNextTokenFromSrc() {

        Token token = new Token();
        if (srcPosition >= src.length()) {
            return END;
        }

        char c;
        while (((c = charAt(srcPosition++)) == ' ' || c == '\t') || c == '\n') {
            if (c == '\n') {
                line++;
            }
        }

        // srcPosition此次开始读取的位置
        int tempSrcPosition = srcPosition - 1;

        // identifier
        if (Character.isLetter(c) || c == '_') {
            c = charAt(srcPosition);
            while (Character.isLetter(c) || Character.isDigit(c)) {
                c = charAt(++srcPosition);
            }
            String name = src.substring(tempSrcPosition, srcPosition);
            token.type = checkTokenType(name);
            if (token.type == Token.Type.IDENTIFIER)
                token.name = name;
        } else if (Character.isDigit(c)) {
            // digit
            if (c != '0') {
                // 可能为整数或小数
                token = parseDecimal();
            } else if (src.charAt(srcPosition) == 'X' || src.charAt(srcPosition) == 'x') {
                token.type = Token.Type.NUMBER_VAL;
                token.value = parseHex();
            } else {
                token.type = Token.Type.NUMBER_VAL;
                token.value = parseOct();
            }
        } else if (c == '#') {
            skipComment();
            return readNextTokenFromSrc();
        } else if (c == '+') {
            token.type = Token.Type.ADD;
            if (charAt(srcPosition) == '+') {
                srcPosition++;
                token.type = Token.Type.INC;
            } else if (charAt(srcPosition) == '=') {
                srcPosition++;
                token.type = Token.Type.ADD_ASSIGN;
            }
        } else if (c == '-') {
            token.type = Token.Type.SUB;
            if (charAt(srcPosition) == '-') {
                srcPosition++;
                token.type = Token.Type.DEC;
            } else if (charAt(srcPosition) == '=') {
                srcPosition++;
                token.type = Token.Type.SUB_ASSIGN;
            }
        } else if (c == '*') {
            token.type = Token.Type.MUL;
            if (charAt(srcPosition) == '=') {
                srcPosition++;
                token.type = Token.Type.MUL_ASSIGN;
            }
        } else if (c == '/') {
            token.type = Token.Type.DIV;
            if (charAt(srcPosition) == '=') {
                srcPosition++;
                token.type = Token.Type.DIV_ASSIGN;
            }
        } else if (c == '%') {
            token.type = Token.Type.MOD;
            if (charAt(srcPosition) == '=') {
                srcPosition++;
                token.type = Token.Type.MOD_ASSIGN;
            }
        } else if (c == '>') {
            token.type = Token.Type.GT;
            if (charAt(srcPosition) == '=') {
                srcPosition++;
                token.type = Token.Type.GE;
            } else if (charAt(srcPosition) == '>') {
                token.type = Token.Type.RSH;
            }
        } else if (c == '<') {
            token.type = Token.Type.LT;
            if (charAt(srcPosition) == '=') {
                srcPosition++;
                token.type = Token.Type.LE;
            } else if (charAt(srcPosition) == '<') {
                token.type = Token.Type.LSH;
            }
        } else if (c == '=') {
            token.type = Token.Type.ASSIGN;
            if (charAt(srcPosition) == '=') {
                srcPosition++;
                token.type = Token.Type.EQ;
            }
        } else if (c == '!') {
            token.type = Token.Type.NOT;
            if (charAt(srcPosition) == '=') {
                srcPosition++;
                token.type = Token.Type.NE;
            }
        } else if (c == '|') {
            token.type = Token.Type.B_OR;
            if (charAt(srcPosition) == '|') {
                srcPosition++;
                token.type = Token.Type.OR;
            }
        } else if (c == '&') {
            token.type = Token.Type.B_AND;
            if (charAt(srcPosition) == '&') {
                srcPosition++;
                token.type = Token.Type.AND;
            }
        } else if (c == '^') {
            token.type = Token.Type.B_XOR;
        } else if (c == '{') {
            token.type = Token.Type.L_CURLY_BRACKET;
        } else if (c == '}') {
            token.type = Token.Type.R_CURLY_BRACKET;
        } else if (c == '[') {
            token.type = Token.Type.L_SQUARE_BRACKET;
        } else if (c == ']') {
            token.type = Token.Type.R_SQUARE_BRACKET;
        } else if (c == '(') {
            token.type = Token.Type.OPEN_PARENTHESIS;
        } else if (c == ')') {
            token.type = Token.Type.CLOSE_PARENTHESIS;
        } else if (c == ';') {
            token.type = Token.Type.SEMICOLON;
        } else if (c == ',') {
            token.type = Token.Type.COMMA;
        } else if (c == '?') {
            token.type = Token.Type.QUESTION_MARK;
        } else if (c == '~') {
            token.type = Token.Type.BIT_REVERSE;
        } else if (c == '.') {
            token.type = Token.Type.DOT;
        } else if (c == ':') {
            token.type = Token.Type.COLON;
        } else if (c == '"') { //获取string字面量
            StringBuilder buffer = new StringBuilder();
            char v = charAt(srcPosition++);
            // todo string长度判断
            while (v != '"') {
                buffer.append(v);
                v = charAt(srcPosition++);
                if (v == 0) {
                    error("illegal string without end!");
                }
            }
            token.type = Token.Type.STRING_VAL;
            token.value = buffer.toString();
        } else if (c == '\'') {
            // todo 合法字符判断
            token.value = charAt(srcPosition++);
            token.type = Token.Type.CHAR_VAL;
            if (charAt(srcPosition++) != '\'') {
                error("illegal character1");
            }
        } else {
            error(String.format("lex error at line %d, unexpected char: %c(%d)", line, charAt(tempSrcPosition), (int) charAt(tempSrcPosition)));
        }

        // 将行号保存到token中
        token.line = line;
        token.srcPosition = tempSrcPosition;

        return token;
    }

    /**
     * 转化十进制，包括小数
     */
    private Token parseDecimal() {
        Token token = new Token();
        token.type = Token.Type.NUMBER_VAL;
        int sum = charAt(srcPosition - 1) - '0';
        char c = charAt(srcPosition);
        while (c >= '0' && c <= '9') {
            sum = sum * 10 + c - '0';
            c = charAt(++srcPosition);
        }
        token.value = sum;
        // 遇到小数点，解析小数
        if (charAt(srcPosition) == '.') {
            double sum1 = 0;
            int radix = 10;
            token.type = Token.Type.FLOAT_VAL;
            c = charAt(++srcPosition);
            // 检查小数点后是不是数字
            if (!Character.isDigit(c)) {
                error("decimal value must be followed by number!");
            }
            while (Character.isDigit(c)) {
                sum1 += (c - '0') / 1.0 / radix;
                radix *= 10;
                c = charAt(++srcPosition);
            }
            token.value = sum / 1.0 + sum1;
        }
        return token;
    }

    private int parseHex() {
        char c = charAt(++srcPosition);
        int sum = 0;
        while (c >= '0' && c <= '9' || c >= 'A' && c <= 'F' || c >= 'a' && c <= 'f') {
            int r = c - '0';
            if (c >= 'A')
                r = c - 'A';
            if (c >= 'a')
                r = c - 'a';
            sum = sum * 16 + r;
            c = charAt(++srcPosition);
        }
        return sum;
    }

    private int parseOct() {
        char c = charAt(srcPosition);
        int sum = 0;
        while (c >= '0' && c <= '7') {
            sum = sum * 8 + c - '0';
            c = charAt(++srcPosition);
        }
        return sum;
    }

    private void skipComment() {
        while (srcPosition < src.length() && charAt(srcPosition) != '\n')
            srcPosition++;
    }

    /**
     * 判断一个标识符，是标识符还是关键字
     */
    private Token.Type checkTokenType(String identifier) {
        int start = Token.Type.INT.ordinal();
        int end = Token.Type.THIS.ordinal();
        for (int i = start; i <= end; i++) {
            String keyword = Token.Type.values()[i].name().toLowerCase();
            if (keyword.equals(identifier))
                return Token.Type.values()[i];
        }
        return Token.Type.IDENTIFIER;
    }

    public void unexpectedToken(Token unexpectedToken) {
        error(unexpectedToken, "unexpected token type: " + unexpectedToken.type.name());
    }

    public void unexpectedToken(Token unexpectedToken, Token.Type expectedType) {
        error(unexpectedToken, "unexpected token type: " + unexpectedToken.type.name() + ", expected is: " + expectedType.name());
    }

    /**
     * 根据token显示行号及错误消息
     *
     * @param token 发生错误的token
     * @param str   错误信息
     */
    public void error(Token token, String str) {
        throw new RuntimeException(String.format("at line %d, %s", token.line, str));
    }

    private void error(String str) {
        throw new RuntimeException(String.format("at line %d: %s", line, str));
    }

}