package com.baomidou.mybatisplus.solon.integration;

import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.MybatisSqlSessionFactoryBuilder;
import com.baomidou.mybatisplus.core.config.GlobalConfig;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.override.SolonMybatisMapperProxy;
import com.baomidou.mybatisplus.core.toolkit.GlobalConfigUtils;
import com.baomidou.mybatisplus.extension.repository.CrudRepository;
import com.baomidou.mybatisplus.extension.repository.IRepository;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.solon.integration.MybatisAdapterDefault;
import org.noear.solon.Utils;
import org.noear.solon.core.BeanWrap;
import org.noear.solon.core.Props;
import org.noear.solon.core.VarHolder;

import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

/**
 * 适配器 for mybatis-plus
 * <p>
 * 1.提供 mapperScan 能力
 * 2.生成 factory 的能力
 *
 * @author noear, iYarnFog
 * @since 1.5
 */
public class MybatisAdapterPlus extends MybatisAdapterDefault {

    MybatisSqlSessionFactoryBuilder factoryBuilderPlus;
    GlobalConfig globalConfig;
    SqlSession sqlSession;
    Map<Class<?>, Object> mapperCached = new HashMap<>();

    /**
     * 构建Sql工厂适配器，使用默认的 typeAliases 和 mappers 配置
     */
    protected MybatisAdapterPlus(BeanWrap dsWrap) {
        super(dsWrap);

        this.factoryBuilderPlus = new MybatisSqlSessionFactoryBuilder();

        dsWrap.context().getBeanAsync(MybatisSqlSessionFactoryBuilder.class, bean -> {
            factoryBuilderPlus = bean;
        });
    }

    /**
     * 构建Sql工厂适配器，使用属性配置
     */
    protected MybatisAdapterPlus(BeanWrap dsWrap, Props dsProps) {
        super(dsWrap, dsProps);

        this.factoryBuilderPlus = new MybatisSqlSessionFactoryBuilder();

        dsWrap.context().getBeanAsync(MybatisSqlSessionFactoryBuilder.class, bean -> {
            factoryBuilderPlus = bean;
        });
    }

    /**
     * 初始化配置
     */
    @Override
    protected void initConfiguration(Environment environment) {
        //for configuration section
        config = new MybatisConfiguration(environment);

        Props cfgProps = dsProps.getProp("configuration");
        if (cfgProps.size() > 0) {
            Utils.injectProperties(config, cfgProps);
        }


        //for globalConfig section
        globalConfig = new GlobalConfig().setDbConfig(new GlobalConfig.DbConfig());

        Props globalProps = dsProps.getProp("globalConfig");
        if (globalProps.size() > 0) {
            //尝试配置注入
            Utils.injectProperties(globalConfig, globalProps);
        }

        GlobalConfigUtils.setGlobalConfig(config, globalConfig);
    }

    /**
     * 获取会话工厂
     */
    @Override
    public SqlSessionFactory getFactory() {
        if (factory == null) {
            factory = factoryBuilderPlus.build(getConfiguration());
        }

        return factory;
    }

    public SqlSession getSession() {
        if (sqlSession == null) {
            sqlSession = new SolonSqlSession(getFactory());
        }
        return sqlSession;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getMapper(Class<T> mapperClz) {
        Object mapper = mapperCached.get(mapperClz);

        if (mapper == null) {
            synchronized (mapperClz) {
                mapper = mapperCached.get(mapperClz);
                if (mapper == null) {
                    SolonMybatisMapperProxy<T> tMybatisMapperProxy = new SolonMybatisMapperProxy<>(getFactory(), getSession(), mapperClz);
                    mapper = Proxy.newProxyInstance(
                            mapperClz.getClassLoader(),
                            new Class[]{mapperClz},
                            tMybatisMapperProxy);
                    mapperCached.put(mapperClz, mapper);
                }
            }
        }

        return (T) mapper;
    }

    /**
     * 获取全局配置
     */
    public GlobalConfig getGlobalConfig() {
        return globalConfig;
    }

    @Override
    public void injectTo(VarHolder vh) {
        if (IRepository.class.isAssignableFrom(vh.getType())) {
            vh.context().getWrapAsync(vh.getType(), serviceBw -> {
                if (serviceBw.raw() instanceof CrudRepository) {
                    //如果是 ServiceImpl
                    injectService(vh, serviceBw);
                } else {
                    //如果不是 ServiceImpl
                    vh.setValue(serviceBw.get());
                }
            });
            return;
        }

        //@Db("db1") SqlSessionFactory factory;
        if (GlobalConfig.class.isAssignableFrom(vh.getType())) {
            vh.setValue(this.getGlobalConfig());
            return;
        }

        super.injectTo(vh);


    }

    /**
     * 服务缓存
     */
    private Map<Class<?>, CrudRepository> repositoryCached = new HashMap<>();

    /**
     * 注入服务 IService
     */
    @SuppressWarnings("unchecked")
    private void injectService(VarHolder vh, BeanWrap serviceBw) {
        CrudRepository repository = serviceBw.raw();

        if (repositoryCached.containsKey(vh.getType())) {
            //从缓存获取
            repository = repositoryCached.get(vh.getType());
        } else {
            Object baseMapperOld = repository.getBaseMapper();

            if (baseMapperOld != null) {
                Class<?> baseMapperClass = null;
                for (Class<?> clz : baseMapperOld.getClass().getInterfaces()) {
                    //baseMapperOld.getClass() 是个代理类，所以要从基类接口拿
                    if (BaseMapper.class.isAssignableFrom(clz)) {
                        baseMapperClass = clz;
                        break;
                    }
                }

                if (baseMapperClass != null) {
                    //如果有 baseMapper ，说明正常；；创建新实例，并更换 baseMapper
                    repository = serviceBw.create();

                    BaseMapper baseMapper = (BaseMapper) this.getMapper(baseMapperClass);
                    repository.setBaseMapper(baseMapper);

                    //缓存
                    repositoryCached.put(vh.getType(), repository);
                }
            }
        }

        vh.setValue(repository);
    }
}
