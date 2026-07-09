package com.baomidou.mybatisplus.solon.integration;

import com.baomidou.mybatisplus.core.spi.CompatibleHelper;
import com.baomidou.mybatisplus.core.toolkit.reflect.GenericTypeUtils;
import org.apache.ibatis.solon.integration.MybatisAdapterManager;
import org.noear.solon.core.AppContext;
import org.noear.solon.core.Plugin;
import org.noear.solon.core.util.GenericUtil;

/**
 * @author noear
 * @since 1.5
 */
public class XPluginImpl implements Plugin {

    @Override
    public void start(AppContext context) {
        //
        // 此插件的 solon.plugin.priority 会大于 mybatis-solon-plugin 的值
        //
        MybatisAdapterManager.setAdapterFactory(new MybatisAdapterFactoryPlus());

        // 提供反射处理类
        GenericTypeUtils.setGenericTypeResolver((GenericUtil::resolveTypeArguments));

        // 传递容器上下文（供 CompatibleSet.getBean 使用）
        if (CompatibleHelper.hasCompatibleSet()) {
            CompatibleHelper.getCompatibleSet().setContext(context);
        }
    }
}
