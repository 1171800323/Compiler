package parser;

public class SymbolItem {
    private final String id; // 标识符
    private final String type;  // 类型
    private final int offset;  // 偏移量
    private final int lineNum;  // 行号

    public SymbolItem(String id, String type, int offset, int lineNum) {
        this.id = id;
        this.type = type;
        this.offset = offset;
        this.lineNum = lineNum;
    }

    public String getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public int getOffset() {
        return offset;
    }

    public int getLineNum() {
        return lineNum;
    }

    @Override
    public String toString() {
        return "<" + id + ", " + type + ", " + offset + ", " + lineNum + ">";
    }
}
