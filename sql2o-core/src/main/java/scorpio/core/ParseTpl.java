package scorpio.core;

import freemarker.ext.beans.BeansWrapperBuilder;
import freemarker.template.Configuration;
import freemarker.template.Template;
import lombok.extern.slf4j.Slf4j;
import scorpio.exception.BaseRuntimeException;

import java.io.*;
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
     *
     * @param inFile 模板文件
     * @param outFile 输出文件
     * @param templatePath 模板目录
     * @param paramMap 参数
     */
    public static void parseSqlTemplate(String inFile, String outFile, String templatePath, Map paramMap) {

        Configuration cfg = getConfiguration();
        try {
            File file = new File(outFile);
            File parentFile = file.getParentFile();
            if(!parentFile.exists()){
                parentFile.mkdirs();
            }
            Writer stringWriter=new OutputStreamWriter(new FileOutputStream(file), "gbk");
            cfg.setDirectoryForTemplateLoading(new File(templatePath));
            //String disableNumberParserStr = "<#setting number_format=\"#\">";
            Template t = cfg.getTemplate(inFile);
            t.process(paramMap, stringWriter);
        } catch (Exception e) {
            throw new RuntimeException("Get the exception of the expression content", e);
        }
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
