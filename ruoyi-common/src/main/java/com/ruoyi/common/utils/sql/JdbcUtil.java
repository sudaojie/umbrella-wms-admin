package com.ruoyi.common.utils.sql;

import java.sql.*;

/**
 * @author xutianbao
 * @date 2022/10/27 18:26
 **/
public class JdbcUtil {

    public static Connection getConnection(String driver, String url, String name, String pwd) {
        try {
            Class.forName(driver);
            // 获取连接对象
            return DriverManager.getConnection(url, name, pwd);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

}
