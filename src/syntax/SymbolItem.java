package syntax;

public class SymbolItem {
    //符号表中的每一个记录
    private  String name;   // 标识符名字
    private  String type;   // 标识符类型
    private  int linenum ;  // 在文件中的行号
    private  int  offset;   // 地址偏移

    public SymbolItem(String name, String type, int linenum,int offset) {
        this.name = name ;
        this.type = type ;
        this.linenum = linenum ;
        this.offset = offset ;
    }

    public int getOffset() {
        return offset;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public int getLinenum() {
        return linenum;
    }

    @Override
    public String toString() {
        return "<" + name + ", " + type + ", " + linenum + ", "  + offset + ">";
    }


}
