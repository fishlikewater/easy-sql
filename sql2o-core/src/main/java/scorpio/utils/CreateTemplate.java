package scorpio.utils;

import org.apache.commons.lang.StringUtils;
import org.sql2o.Sql2o;
import scorpio.BaseUtils;
import scorpio.core.ParseTpl;

import java.io.File;
import java.util.*;

/**
 * @author <p><a>fishlikewater@126.com</a></p>
 * @date 2019年04月22日 16:06
 * @since
 **/
public class CreateTemplate {

    private static String url;
    private static String user;
    private static String password;
    private static String driver;
    private static String tableName;
    private static String pack;
    private static String fileMapper;
    private static String basePath;
    private static String templatePath;
    private static String ID;
    private static String Type;


    public static void init(Properties p){
        url = p.getProperty("jdbc.url");
        user = p.getProperty("jdbc.username");
        if(StringUtils.isBlank(user)){
            user = null;
        }
        password = p.getProperty("jdbc.password");
        if(StringUtils.isBlank(password)){
            password = null;
        }
        driver = p.getProperty("jdbc.driver");
        tableName = p.getProperty("table");
        pack = p.getProperty("pack");
        fileMapper = p.getProperty("fileMapper");
        basePath = p.getProperty("basePath");
        templatePath = CreateTemplate.class.getResource(p.getProperty("templates")).getPath();
        //templatePath = templatePath.substring(1, templatePath.length());
    }

    /**
     * 创建数据库操作类
     */
    public static void createMapper() {
        Map<String, Object> root = new HashMap<>();
        String className = toUpperCaseFirstOne(tableName);
        root.put("package", pack);
        root.put("className", className);
        root.put("table", tableName);
        if (StringUtils.isNotBlank(fileMapper)) {
            root.put("fileMapper", fileMapper);
        }
        String path = basePath + "/src/main/java/";
        String p = pack + ".mapper";
        ParseTpl.parseSqlTemplate("/MapperTemplate.ftl", getJavaDirectoty(path, p) + className + "Mapper.java", templatePath, root);
    }

    /**
     * 创建数据库映射实体类
     */
    public static void createModel() {
        String className = toUpperCaseFirstOne(tableName);
        Sql2o sql2o = BaseUtils.open(url, user, password);
        String dataType = BaseUtils.getDataType();
        boolean isMysql = dataType.toLowerCase().contains("mysql");
        String sql = isMysql ?"SHOW COLUMNS FROM " + tableName:"PRAGMA table_info('"+tableName+"')";
        List<Map<String, Object>> maps = sql2o.open().createQuery(sql).executeAndFetchTable().asList();
        Map<String, Object> root = new HashMap<>();
        List list = new ArrayList();
        for (Map m : maps) {
            String field = isMysql?NameUtils.getCamelName(m.get("field") + ""):NameUtils.getCamelName(m.get("name") + "");
            String type = m.get("type") + "";
            String key = isMysql?m.get("key") + "":m.get("pk") + "";
            if (StringUtils.startsWith(type, "int")) {
                field = "Integer " + field;
                type = "Integer";
            }/*else if(StringUtils.startsWith(type, "varchar") || StringUtils.startsWith(type, "char")){
                field = "String " + field;
                type = "String";
            }*/ else if (StringUtils.startsWith(type, "decimal")) {
                field = "Decimal " + field;
                type = "Decimal";
            } else if (StringUtils.startsWith(type, "double") || StringUtils.startsWith(type, "float")) {
                field = "double " + field;
                type = "double";
            }else if(StringUtils.startsWith(type.toLowerCase(), "long")){
                field = "long " + field;
                type = "long";
            } else {
                field = "String " + field;
                type = "String";
            }
            if (StringUtils.equals("PRI", key)||StringUtils.equals("1", key)) {
                ID = NameUtils.getFirstUpperName(NameUtils.getCamelName(m.get("field") + ""));
                Type = type;
                root.put("id", field);
            } else {
                list.add(field);
            }
        }
        root.put("list", list);
        root.put("package", pack);
        root.put("className", className);
        String path = basePath + "/src/main/java/";
        String p = pack + ".entity";
        ParseTpl.parseSqlTemplate("/EntityTemplate.ftl", getJavaDirectoty(path, p) + className + ".java", templatePath, root);
    }

    public static void createService() {
        Map<String, Object> root = new HashMap<>();
        String className = toUpperCaseFirstOne(tableName);
        root.put("package", pack);
        root.put("className", className);
        root.put("id", ID);
        root.put("type", Type);
        root.put("dao", NameUtils.getFirstLowerName(className));
        String path = basePath + "/src/main/java/";
        String p = pack + ".service";
        ParseTpl.parseSqlTemplate("/ServiceTemplate.ftl", getJavaDirectoty(path, p) + className + "Service.java", templatePath, root);
    }

    /**
     * 首字母大写
     *
     * @param s
     * @return
     */
    public static String toUpperCaseFirstOne(String s) {
        s = NameUtils.getCamelName(s);
        if (Character.isUpperCase(s.charAt(0)))
            return s;
        else
            return (new StringBuilder()).append(Character.toUpperCase(s.charAt(0))).append(s.substring(1)).toString();
    }

    /**
     * 构造存放java文件的目录
     *
     * @param basePath
     * @param packsge
     * @return
     */
    public static String getJavaDirectoty(String basePath, String packsge) {
        String[] split = packsge.split("\\.");
        for (String s : split) {
            basePath += File.separator + s;
        }

        return basePath + File.separator;
    }
}
