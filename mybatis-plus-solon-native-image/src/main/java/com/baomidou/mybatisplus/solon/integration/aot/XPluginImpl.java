package com.baomidou.mybatisplus.solon.integration.aot;

import org.noear.solon.core.AppContext;
import org.noear.solon.core.Plugin;
import org.noear.solon.core.runtime.NativeDetector;

/**
 * mybatis-plus native-image 适配插件：仅在 aot 处理期注册 native 元数据
 *
 * @author songyinyin
 * @since 3.5.17
 */
public class XPluginImpl implements Plugin {

    @Override
    public void start(AppContext context) {
        if (NativeDetector.isAotRuntime()) {
            context.wrapAndPut(MybatisPlusRuntimeNativeRegistrar.class);
        }
    }
}
