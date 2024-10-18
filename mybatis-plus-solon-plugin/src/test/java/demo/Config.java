package demo;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.config.GlobalConfig;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.apache.ibatis.solon.annotation.Db;
import org.noear.solon.annotation.Bean;
import org.noear.solon.annotation.Configuration;

@Configuration
public class Config {

    @Bean
    public void db1_cfg(@Db("db1") MybatisConfiguration cfg,
                        @Db("db1") GlobalConfig globalConfig) {
        //增加 mybatis-plus 的自带分页插件
        MybatisPlusInterceptor plusInterceptor = new MybatisPlusInterceptor();
        plusInterceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));

        cfg.addInterceptor(plusInterceptor);
    }

//    @Bean
//    public MybatisSqlSessionFactoryBuilder factoryBuilderNew(){
//        return new MybatisSqlSessionFactoryBuilderImpl();
//    }
}

