package scorpio.migrate;


public class Column {

    private String columnName;
    private int columnType;
    private int length;
    private boolean primaryKey;
    private boolean nullable;
    private Object defaultValue;
    private String columnDefinition;
    private boolean autoincrement;
    private String describe;


    public Column(String columnName, int columnType) {
        this(columnName, columnType, -1, false, true, null,null, false,null);
    }

    public Column(String columnName,
                  int columnType,
                  int length,
                  boolean primaryKey,
                  boolean nullable,
                  Object defaultValue,
                  String columnDefinition,
                  boolean autoincrement,
                  String describe) {
        this.columnName = columnName;
        this.columnType = columnType;
        this.length = length;
        this.primaryKey = primaryKey;
        this.nullable = nullable;
        this.defaultValue = defaultValue;
        this.columnDefinition = columnDefinition;
        this.autoincrement = autoincrement;
        this.describe = describe;
    }

    public String getColumnName() {
        return columnName;
    }

    public int getColumnType() {
        return columnType;
    }

    public int getLength() {
        return length;
    }

    public void setLength(Integer length) {
        this.length = length;
    }

    public boolean isPrimaryKey() {
        return primaryKey;
    }

    public void setPrimaryKey(boolean primaryKey) {
        this.primaryKey = primaryKey;
    }

    public boolean isNullable() {
        return nullable;
    }

    public void setNullable(boolean nullable) {
        this.nullable = nullable;
    }

    public Object getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(Object defaultValue) {
        this.defaultValue = defaultValue;
    }

    public boolean isAutoincrement() {
        return autoincrement;
    }

    public void setAutoIncrement(boolean autoincrement) {
        this.autoincrement = autoincrement;
    }

    public String getColumnDefinition() {
        return columnDefinition;
    }

    public void setColumnDefinition(String columnDefinition) {
        this.columnDefinition = columnDefinition;
    }

    public String getDescribe() {
        return describe;
    }

    public void setDescribe(String describe) {
        this.describe = describe;
    }
}
