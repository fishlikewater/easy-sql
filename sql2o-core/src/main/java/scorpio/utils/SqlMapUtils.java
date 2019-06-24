package scorpio.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author <p>fishlikewater@126.com</p>
 * @date 2019年04月18日 14:14
 * @since 1.6
 **/
@Slf4j
public class SqlMapUtils {

    public static Map<String, Map<String, String>> sqlCache = new HashMap<>();

    public static ReentrantLock lock = new ReentrantLock();

    public static void cacheSqlMap(String className, Map<String, String> sqlMap){
        if(!sqlCache.containsKey(className)){
            sqlCache.put(className, sqlMap);
        }
    }

    public static Map<String, String> getSqlCahceMap(String className){

        return sqlCache.get(className);
    }

    public static <T> Map<String, String> getSqlMap(String path, Class<T> t) {
        lock.lock();
        URL url = t.getResource(path);
        try {
            if (url == null) {
                log.debug("no sqlmapping file {}", path);
                return new HashMap<String, String>();
            }
            //如果不在jar中
            InputStream in = null;
            if (!url.getFile().contains(".jar")) {
                try {
                    in = new FileInputStream(url.toURI().getPath());
                } catch (FileNotFoundException e) {
                    log.error("{}", "not find" + url.toURI().getPath(), e);
                    return new HashMap<String, String>();
                }
                File file = new File(url.toURI());
            } else {
                in = t.getResourceAsStream(path);
            }
            return loadSqlMap(url, in);
        } catch (URISyntaxException e) {
            log.error("{}", "file" + t.getClass().getSimpleName() + ".sqlmap not find");
            return new HashMap<String, String>();
        }finally {
            lock.unlock();
        }
    }

    public static Map<String, String> loadSqlMap(URL url, InputStream fo) {
        Map<String, String> tempUserSqlMap = new HashMap<String, String>();
        try {
            char[] chars = IOUtils.toCharArray(fo, "UTF-8");
            StringBuffer s = new StringBuffer();

            char status = '=';
            String key = "";
            Pattern p = Pattern.compile("\\s+");
            boolean commetStatus = false;
            for (int i = 0; i < chars.length; i++) {
                if (!commetStatus && '/' == chars[i] && '*' == chars[i + 1]) {
                    commetStatus = true;
                } else if (commetStatus && '/' == chars[i] && '*' == chars[i - 1]) {
                    commetStatus = false;
                } else if (!commetStatus) {
                    if ('=' == status && chars[i] == status) {
                        status = ';';
                        key = s.toString();
                        s = new StringBuffer();
                    } else if (';' == status && chars[i] == status) {
                        status = '=';
                        s.toString().replaceAll("\r|\n", "");
                        Matcher m = p.matcher(s);
                        String sql = m.replaceAll(" ");
                        tempUserSqlMap.put(StringUtils.trim(key), StringUtils.trim(sql));
                        s = new StringBuffer();
                    } else {
                        s.append(chars[i]);
                    }
                }
            }
        } catch (FileNotFoundException e) {
            log.error("{}", "not find: " + url, e);

        } catch (IOException e) {
            log.debug("{}", " load sqlmapping file error! ");
        } finally {
            IOUtils.closeQuietly(fo);
        }
        return tempUserSqlMap;
    }

}
