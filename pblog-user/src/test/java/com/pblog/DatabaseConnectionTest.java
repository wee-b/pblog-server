package com.pblog;

import com.pblog.user.PBlogApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
@SpringBootTest(classes = PBlogApplication.class)
@RunWith(SpringRunner.class)
public class DatabaseConnectionTest {

    @Autowired(required = false)
    private DataSource dataSource;

    @Autowired(required = false)
    private JdbcTemplate jdbcTemplate;

    @Value("${spring.datasource.password}")
    private String dbPassword;

    // 私有变量存储数据库连接信息
    private static final String DB_URL = "jdbc:mysql://localhost:3306/pblog?characterEncoding=utf8&serverTimezone=Asia/Shanghai";
    private static final String USERNAME = "root";       // 数据库账号
    private static final String PASSWORD = "1234";       // 数据库密码
    private static final String DRIVER_CLASS = "com.mysql.cj.jdbc.Driver";

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Test
    public void testRedisConnection() {
        try {
            // 向 Redis 写入测试数据
            stringRedisTemplate.opsForValue().set("test_key", "Hello Redis!");

            // 从 Redis 读取数据
            String value = stringRedisTemplate.opsForValue().get("test_key");

            // 验证结果
            if ("Hello Redis!".equals(value)) {
                System.out.println("✅ Redis 连接成功！");
            } else {
                System.out.println("❌ Redis 数据读写异常！");
            }
        } catch (Exception e) {
            System.err.println("❌ Redis 连接失败：" + e.getMessage());
            e.printStackTrace();
        }
    }



    @Test
    public void printDbConfig() {
        System.out.println("Spring 加载的数据库密码：" + dbPassword);
        // 若打印为空或不是 1234，说明配置未被正确加载
    }

    @Test
    public void testDataSourceConnection() throws SQLException {
        if (dataSource == null) {
            System.out.println("❌ 数据源 DataSource 注入失败，可能是数据库依赖或配置错误");
            return;
        }

        // 尝试获取数据库连接
        try (Connection connection = dataSource.getConnection()) {
            if (connection != null && !connection.isClosed()) {
                System.out.println("✅ 数据库连接成功！");
                System.out.println("数据库 URL：" + connection.getMetaData().getURL());
                System.out.println("数据库用户：" + connection.getMetaData().getUserName());
            } else {
                System.out.println("❌ 数据库连接失败，连接对象为空或已关闭");
            }
        } catch (SQLException e) {
            System.out.println("❌ 数据库连接抛出异常：" + e.getMessage());
            e.printStackTrace(); // 打印详细异常栈，方便定位问题（如密码错误、URL 错误等）
        }
    }

    /**
     * 手动加载驱动并创建数据库连接，验证账号密码是否正确
     */
    @Test
    public void testManualConnection() {
        Connection connection = null;
        try {
            // 1. 加载数据库驱动
            Class.forName(DRIVER_CLASS);
            System.out.println("✅ 数据库驱动加载成功");

            // 2. 手动创建连接（使用私有变量中的账号密码）
            connection = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);

            // 3. 验证连接是否有效
            if (connection != null && !connection.isClosed()) {
                System.out.println("✅ 数据库连接成功！");
                System.out.println("连接信息：" + connection.getMetaData().getURL());
                System.out.println("当前用户：" + connection.getMetaData().getUserName());
            } else {
                System.out.println("❌ 数据库连接失败，连接对象为空或已关闭");
            }
        } catch (ClassNotFoundException e) {
            System.out.println("❌ 数据库驱动加载失败：" + e.getMessage());
            e.printStackTrace();
        } catch (SQLException e) {
            System.out.println("❌ 数据库连接失败（账号密码或URL错误）：" + e.getMessage());
            e.printStackTrace();
        } finally {
            // 4. 关闭连接（避免资源泄漏）
            if (connection != null) {
                try {
                    connection.close();
                    System.out.println("✅ 数据库连接已关闭");
                } catch (SQLException e) {
                    System.out.println("❌ 关闭连接时发生错误：" + e.getMessage());
                }
            }
        }
    }


}
