package scorpio.core;

import freemarker.ext.beans.BeansWrapperBuilder;
import freemarker.template.Configuration;
import freemarker.template.Template;
import lombok.extern.slf4j.Slf4j;
import scorpio.exception.BaseRuntimeException;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.Locale;
import java.util.Map;

@Slf4j
public class ParseTpl {

    private static Configuration config;

    /**
     * 解析sqltemplate
     * @param sqlTemplate
     * @param paramMap
     * @return
     */
    public static String parseSqlTemplate(String sqlTemplate, Map paramMap){
        sqlTemplate = processSqlmap(sqlTemplate);
        Template t;
        StringWriter stringWriter = new StringWriter();
        Configuration cfg = getConfiguration();
        try {
            //freemarker在解析数据格式的时候，自动默认将数字按3为分割（1，000），需要禁用掉
           // String disableNumberParserStr = "<#setting number_format=\"#\">";
            t = new Template("dao template parser",
                    new StringReader(sqlTemplate), cfg);

            t.process(paramMap, stringWriter);
            return stringWriter.toString().replaceAll("#", "'");
        } catch (Exception e) {
            throw new BaseRuntimeException("Get the exception of the expression content", e);
        }
    }


    /**
     * 从sqlmap文件中取得sql
     * @param sqlTemplate
     * @return
     */
    public static String processSqlmap(String sqlTemplate) {
        if(BaseMap.getBasicMap().containsKey(sqlTemplate)) {
            String key = sqlTemplate;
            sqlTemplate = BaseMap.getBasicMap().get(sqlTemplate);
            log.debug("Get the SQL template from `sqlmap`:");
            log.debug("key:" + key);
            log.debug("sql:" + sqlTemplate);
        }
        return sqlTemplate;
    }


    /**
     * freemark配置获取
     * @return
     */
    private static Configuration getConfiguration(){
        if(config == null){
            Configuration cfg = new Configuration(Configuration.VERSION_2_3_26);
            cfg.setObjectWrapper(new BeansWrapperBuilder(Configuration.VERSION_2_3_26).build());
            cfg.setEncoding(Locale.getDefault(), "GBK");
            cfg.setNumberFormat("#");
            config = cfg;
        }
        return config;
    }

}
