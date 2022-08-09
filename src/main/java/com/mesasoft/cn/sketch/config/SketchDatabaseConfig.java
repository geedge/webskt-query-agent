//package com.zhazhapan.efo.sketch.config;
//
//import com.alibaba.druid.pool.DruidDataSource;
//import lombok.Data;
//import org.apache.ibatis.session.SqlSessionFactory;
//import org.mybatis.spring.SqlSessionFactoryBean;
//import org.mybatis.spring.annotation.MapperScan;
//import org.springframework.beans.factory.annotation.Qualifier;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.boot.context.properties.ConfigurationProperties;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
//import org.springframework.jdbc.datasource.DataSourceTransactionManager;
//
//import javax.sql.DataSource;
//import java.sql.SQLException;
//
///**
// * @ProjectName
// * @Description: 后台数据源配置类
// */
//@Data
//@Configuration
//@ConfigurationProperties(prefix = "sketch.datasource.druid")
//@MapperScan(basePackages = SketchDatabaseConfig.PACKAGE, sqlSessionFactoryRef = "sketchSqlSessionFactory")
//public class SketchDatabaseConfig {
//    /**
//     * dao层的包路径
//     */
//    static final String PACKAGE = "com.mao.mysqlhive.demomh.mapper.sketch";
//
//    /**
//     * mapper文件的相对路径
//     */
//    private static final String MAPPER_LOCATION = "classpath:mappers/sketch/*Mapper.xml";
//
//    @Value("${sketch.datasource.druid.filters}")
//    private String filters;
//    @Value("${sketch.datasource.druid.driverClassName}")
//    private String url;
//    @Value("${sketch.datasource.druid.url}")
//    private String username;
//    @Value("${sketch.datasource.druid.username}")
//    private String password;
//    @Value("${sketch.datasource.druid.password}")
//    private String driverClassName;
//    @Value("${sketch.datasource.druid.initialSize}")
//    private int initialSize;
//    @Value("${sketch.datasource.druid.minIdle}")
//    private int minIdle;
//    @Value("${sketch.datasource.druid.maxActive}")
//    private int maxActive;
//    @Value("${sketch.datasource.druid.maxWait}")
//    private long maxWait;
//    @Value("${sketch.datasource.druid.timeBetweenEvictionRunsMillis}")
//    private long timeBetweenEvictionRunsMillis;
//    @Value("${sketch.datasource.druid.minEvictableIdleTimeMillis}")
//    private long minEvictableIdleTimeMillis;
//    @Value("${sketch.datasource.druid.validationQuery}")
//    private String validationQuery;
//    @Value("${sketch.datasource.druid.testWhileIdle}")
//    private boolean testWhileIdle;
//    @Value("${sketch.datasource.druid.testOnBorrow}")
//    private boolean testOnBorrow;
//    @Value("${sketch.datasource.druid.testOnReturn}")
//    private boolean testOnReturn;
//    @Value("${sketch.datasource.druid.poolPreparedStatements}")
//    private boolean poolPreparedStatements;
//    @Value("${sketch.datasource.druid.maxPoolPreparedStatementPerConnectionSize}")
//    private int maxPoolPreparedStatementPerConnectionSize;
//
//
//    @Bean(name = "sketchDataSource")
//    public DataSource sketchDataSource() throws SQLException {
//        DruidDataSource druid = new DruidDataSource();
//        // 监控统计拦截的filters
//        druid.setFilters(filters);
//
//        // 配置基本属性
//        druid.setDriverClassName(driverClassName);
//        druid.setUsername(username);
//        druid.setPassword(password);
//        druid.setUrl(url);
//
//        //初始化时建立物理连接的个数
//        druid.setInitialSize(initialSize);
//        //最大连接池数量
//        druid.setMaxActive(maxActive);
//        //最小连接池数量
//        druid.setMinIdle(minIdle);
//        //获取连接时最大等待时间，单位毫秒。
//        druid.setMaxWait(maxWait);
//        //间隔多久进行一次检测，检测需要关闭的空闲连接
//        druid.setTimeBetweenEvictionRunsMillis(timeBetweenEvictionRunsMillis);
//        //一个连接在池中最小生存的时间
//        druid.setMinEvictableIdleTimeMillis(minEvictableIdleTimeMillis);
//        //用来检测连接是否有效的sql
//        druid.setValidationQuery(validationQuery);
//        //建议配置为true，不影响性能，并且保证安全性。
//        druid.setTestWhileIdle(testWhileIdle);
//        //申请连接时执行validationQuery检测连接是否有效
//        druid.setTestOnBorrow(testOnBorrow);
//        druid.setTestOnReturn(testOnReturn);
//        //是否缓存preparedStatement，也就是PSCache，oracle设为true，mysql设为false。分库分表较多推荐设置为false
//        druid.setPoolPreparedStatements(poolPreparedStatements);
//        // 打开PSCache时，指定每个连接上PSCache的大小
//        druid.setMaxPoolPreparedStatementPerConnectionSize(maxPoolPreparedStatementPerConnectionSize);
//        return druid;
//    }
//
//    @Bean(name = "sketchTransactionManager")
//    public DataSourceTransactionManager sketchTransactionManager() throws SQLException {
//        return new DataSourceTransactionManager(sketchDataSource());
//    }
//
//    @Bean(name = "sketchSqlSessionFactory")
//    public SqlSessionFactory sketchSqlSessionFactory(@Qualifier("sketchDataSource") DataSource sketchDataSource) throws Exception {
//        final SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
//        sessionFactory.setDataSource(sketchDataSource);
//        sessionFactory.setMapperLocations(new PathMatchingResourcePatternResolver().getResources(SketchDatabaseConfig.MAPPER_LOCATION));
//
//        return sessionFactory.getObject();
//    }
//}
