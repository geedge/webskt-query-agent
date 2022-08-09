package com.mesasoft.cn.sketch.config;

import com.mesasoft.cn.util.TimeUtils;
import org.apache.log4j.Logger;

import java.sql.*;
import java.util.Date;
import java.util.Properties;

/**
 * Created with IntelliJ IDEA.
 * User: joy
 * Date: 2021/12/28
 * Time: 2:56 PM
 * Description: No Description
 */
public class MariaDbBase {

    private static final Logger LOG = Logger.getLogger(MariaDbBase.class);
    private static final Properties props = new Properties();

    private final Statement statement;

    public MariaDbBase(Connection conn, Statement stat) {
        statement = stat;
    }

    /**
     * 执行写入sql
     */
    public void writeSqlExecute(String sql){
        try {
            statement.executeUpdate(sql);
        } catch (SQLIntegrityConstraintViolationException e){
            LOG.error("Duplicated entry for key 'PRIMARY'");
        } catch (SQLException exception) {
            LOG.error("Sql : " + sql);
            exception.printStackTrace();
        }
    }

    /**
     * 执行查询sql
     */
    public ResultSet querySqlExecute(String sql){
        ResultSet set = null;
        try {
            set = statement.executeQuery(sql);
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return set;
    }


    /**
     * 获得指定表格、按指定时间字段的过期记录
     * @param tableName 库表名称
     * @param timeColumnName 时间列名
     * @return 查询结果
     */
    public ResultSet getExpiredRecord(String tableName, String timeColumnName){
        Date lastUpdateTime = new Timestamp(getExpiredTime(ApplicationConfig.UPDATE_EXPIRED_DAY).getTime());

        String resSql = "SELECT *"
                + " FROM " + ApplicationConfig.DATABASE + "." + tableName
                + " WHERE " + timeColumnName + " < '" + lastUpdateTime + '\'';

        LOG.debug("Update task: expired query sql" + resSql);

        return querySqlExecute(resSql);
    }

    /**
     * TODO: getUnlabeledRecord() 考虑多个来源的情况
     */

    /**
     * 获得过期时间, 当前时间的expiredRangeDays天之前的日期为过期日期
     */
    public static Date getExpiredTime(int expiredRangeDays){
        return new Timestamp(TimeUtils.getStartOfDay(-expiredRangeDays).getTime());
    }
}
