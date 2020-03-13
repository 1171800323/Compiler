package lexer;

public class Tag {
    // 设置种别码
    public final static int
            ID = 1,                                               /* 标识符*/
            INT = 2, FLOAT = 3, STRING = 4, STRUCT = 5, BOOL = 6, /* 定义数据类型关键字*/
            NUM = 7,        // 无符号整型常数                     /* 常量*/
            REAL = 8,       // 浮点数
            CHARS = 9,      // 字符串常数
            SCI = 10,        // 科学计数法
            TRUE = 11,
            FALSE = 12,
            IF = 13, ELSE = 14, DO = 15, WHILE = 16,              /* 关键字*/
            FOR = 17, BREAK = 18, RETURN = 19,
            AND = 20, OR = 21, NOT = 22,                          /* 运算符和界限符*/
            EQ = 23, NE = 24, LE = 25, GE = 26, GT = 27, LT = 28,
            ADD = 29, SUB = 30, MUL = 31, DIV = 32,
            INC = 33,        // "++"
            DEC = 34,        // "--"
            ASSIGN = 35,     // "="
            SEMI = 36,       // ";"
            COMMA = 37,      // ","
            Dot = 38,        // "."
            LS = 39,         // "("
            RS = 40,         // ")"
            LM = 41,         // "["
            RM = 42,         // "]"
            LB = 43,         // "{"
            RB = 44,         // "}"
            INDEX = 45;      // 数组索引
}