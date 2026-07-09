package demo;

import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.noear.solon.annotation.Bean;
import org.noear.solon.annotation.Configuration;
import org.noear.solon.annotation.Inject;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Statement;

@Configuration
public class Config {

    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor());
        return interceptor;
    }

    /**
     * 初始化表结构与数据（代码方式，避免 native 下 RUNSCRIPT 读取 classpath 资源）
     */
    @Bean
    public void initDb(@Inject("db1") DataSource dataSource) throws Exception {
        try (Connection conn = dataSource.getConnection(); Statement st = conn.createStatement()) {
            st.execute("CREATE TABLE IF NOT EXISTS `user` (id BIGINT AUTO_INCREMENT PRIMARY KEY, name VARCHAR(64), age INT)");
            st.execute("INSERT INTO `user` (name, age) VALUES ('solon', 8), ('mybatis-plus', 12), ('native', 20)");
        }
    }
}
