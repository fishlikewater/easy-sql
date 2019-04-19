# sql2o的简单封装

## 功能特点

> 轻量级,使用sql2o的api操作数据库       
> 内置常用查询模板,快速使用          
> sql使用专门的资源文件维护,与代码分离        
> 使用注解,最大化减少代码量

> 2019-04-19 ActiveRecord 模式

```java
@Data
@Mapping(fileMapper = "/Resources.sqlmap")
public class Resources extends BaseModel<Resources> {
    @Id
    private Integer id;
    @Column("name")
    private String name;

    private String resUrl;

    private Integer type;

    private Integer parentId;
    
    private Integer sort;
}
```
实体类（默认表名驼峰格式，可通过注解@Mapping table属性指定）继承BaseModel, 支持注解的注解@Mapping,@Id,@Column,@Transient
        
## 使用方式

### 准备工作

1.首先将数据源注入

1.1 javaConfig方式注入数据源

<pre>
    @Bean
    public Sql2o sql2o(){
        BaseUtils.getBuilder.setDebug(Boolean.valueOf(debug));
        return BaseUtils.open(dataSource());
    }
</pre>

1.２ 其他方式注入　　　　　　　　　
>　这里留有几种方式初始化，只用在使用前调用这几个方法中一个就可以了
<pre>
    BaseUtils．open(String url, String user, String password);
    BaseUtils．open(DataSource dataSource);
    BaseUtils．open(Sql2o sql2o_);
</pre>

2.编写一个DAO 使其继承BaseDAO,在类上使用@Table注解，标明其对应的数据库表，以及主键和映射实体类
例如:     
<pre>
    @Table(pojo = AgendaDTO.class,table = "m_article", pk = "a_id")
    public class AgendaDAO extends BaseMapper {
    
    }
</pre>
这里的主键pk也可以放在映射的实体中，使用@Id标注:
```java
public class AgendaDTO implements BaseObject {

    @Transient
    private static final long serialVersionUID = 1L;
    @Id
    private String id;
    private String title;
    private String startTime;
    private String endTime;
    private Boolean allDay;
    private String color;
    private String url;
    private Boolean editable;
    private String className;//class
    private String backgroundColor;
    private String borderColor;
    private String textColor;
    private String userId;
    private String isFinish;
    private String createTime;
}
```

实体映射类需要实现BaseObject, 这里的 @Transient表示不是映射字段(主键默认字段名为id)

### api调用

> 到此就可以使用api来操作数据了

插入数据到数据库
<pre>
    AgendaDAO agendaDAO = new AgendaDAO();
      AgendaDTO agendaDTO = new AgendaDTO();
      /** 设置属性省略 */
      articleDAO.create(agendaDTO);/** 设置有主键,或者主键自增 */
      articleDAO.createAndId(agendaDTO);/** 根据配置策略生成主键,目前默认为uuid */
      
      AgendaDTO[] agendaDTOs = new AgendaDTO[];
      articleDAO.createAndId(agendaDTOs);/** 批量创建 */
</pre>
> 还有其他创建方式就不列出来了       


删除数据
<pre>
    AgendaDAO agendaDAO = new AgendaDAO();
    AgendaDTO agendaDTO = new AgendaDTO();
        /** 设置属性省略 */
    agendaDAO.remove(id);
    agendaDAO.removeByIds(ids);
    agendaDAO.removeByCondition(agendaDTO);
    agendaDAO.removeByCriteria(" title='test' and color='red'");/**自定义条件*/

</pre>
> 还有其他删除方式就不列出来了

更新数据
<pre>
    AgendaDAO agendaDAO = new AgendaDAO();
    AgendaDTO agendaDTO = new AgendaDTO();
        /** 设置属性省略 */
    agendaDTO.update(agendaDTO);
    agendaDTO.update(map);/** 可以是和映射实体对应的Map */
    agendaDTO.updateNotIgnoreNull(agendaDTO);
    agendaDTO.updateByCondtion(BaseObject data, BaseObject condition);
    agendaDTO.updateByConditionIgnoreEmpty(BaseObject data, BaseObject condition);
</pre>
> 还有其他更新方式就不列出来了


查询的方式比较多，可以将查询的结果封装成任意对应的对象，就不一一详说了,下面说一下模板:   
模板文件以.sqlmap为后缀,默认查找路径为当前DAO类同目录,可以通过@Table注解中的fileMapper配置,可以指定一个目录,也可以指定具体的文件
如果不配置fileMapper或者指定目录，默认查找文件名为当前类名。模板以键值对的形式保存

<pre>
    queryLableName = SELECT name from article_lable where id in(${lableIds});
    
    updateStatus = update m_article set is_public=${status} where a_id=#${id}#;
       
    queryCountByTitle = select count(*) from m_article a left join article_type t on a.type_id=t.id where 1=1
                     &lt;#if title??&gt;
                       and a.name like #%${title}%#
                    &lt;/#if&gt;;
</pre>
> 模板的写法可查看freemark语法(这里使用#${id}#，#包裹表示字符串),如此就可以使用DAO调用编写的sql

<pre>
    Map<String, Object> paramMap = new HashMap<>(); 
    paramMap.put("title", title); 
    int count = articleDAO.queryCountByTpl("queryCountByTitle", paramMap);

</pre>

> 这里的sql可以做缓存 只要配置BaseUtils.getBuilder().setDev(false)。dev默认为false，在开发时最好设置为true，这不会缓存sql，可随意更改sql，并立即生效。

### 支持表创建

> 如果需要在初始化的时候自动创建表，需要配置BaseUtils.getBuilder().setCreate(true)，dto需要注解配置:
<pre>
    @IdGenerator(value = Generator.DEFINED, idclass = MyIdFactory.class)
    private String aId;

    @Column(value = "name",type = Types.VARCHAR, length = 20,nullable = false)
    private String name;

    @Column(value = "flag",type = Types.BOOLEAN,defaultValue = "0")
    private Boolean flag;
</pre>

>  @IdGenerator 注解中有属性 value 枚举表示生成主键的方式，枚举如下：
<pre>
    public enum Generator {
    
        AUTO,UUID,DEFINED
    
    
    }
</pre>
AUTO表示数据库主键自增，uuid表示生成uuid主键，defined表示自定义生成，配置definedd时，一定要配置属性idclass，表示主键生成的类，这个类继承类IdDefined，实例如下:
<pre>
    public class MyIdFactory implements IdDefined {
        @Override
        public String getId() {
            return "t_option"+UUIDUtils.get();
        }
    }
</pre>
>  @Column拥有的属性如下:
<pre>
    public @interface Column {
    
        String value();
    
        int type() default Types.VARCHAR;
    
        int length() default 255;
    
        boolean nullable() default false;
    
        String describe() default "";
    
        boolean index() default false;
    
        String columnDefined() default "";
    
        String defaultValue() default "";
    
    }
</pre> 

其中 index 表示该列是否建立索引，columnDefined用来定义一些不好设置的数据类型如:
decimal(10,2)，defaultValue为默认值，如果类型为Types.BOOLEAN时，默认值用字符串"0","1"表示false与true。

> 注意：查询结果如果为list&lt;map&gt; 那么所有的表头字段均被转换为小写。
                     
> 2018-03-13 添加多数据源支持

代码层面支持多只读数据源及多写数据源，需开启BaseUtils.getBuilder().setOPenReadyAndWrite(true),
使用public static void openOnlyReady(DataSource... dataSource)与public static void openWrite(DataSource... dataSource)分别注入数据源，在进行数据库操作时，自动通过sql语句判断读写，随机选择读或写的数据源。          

> 2018-12-30 添加多数据源支持  

配置spring boot 注解自动配置 启动类上添加@EnableSql2o 注解(spring 容器中需注入数据源)         