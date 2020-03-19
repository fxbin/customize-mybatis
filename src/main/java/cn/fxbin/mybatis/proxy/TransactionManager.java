package cn.fxbin.mybatis.proxy;

import cn.fxbin.mybatis.DbTools;

import javax.tools.DocumentationTool;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * TransactionManager
 *
 * @author fxbin
 * @version v1.0
 * @since 2020/3/19 13:39
 */
public class TransactionManager {

    public static Map<Thread, Connection> threadConnectionMap = new ConcurrentHashMap<Thread, Connection>();

    public static Connection getConnection() {
        Connection connection = threadConnectionMap.get(Thread.currentThread());
        try {
            if (connection == null) {
                connection = DbTools.getConnection();
                threadConnectionMap.put(Thread.currentThread(), connection);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return connection;
    }

}
