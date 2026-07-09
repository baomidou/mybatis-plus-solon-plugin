package com.baomidou.mybatisplus.solon.integration.aot;

import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.MybatisXMLLanguageDriver;
import com.baomidou.mybatisplus.core.conditions.AbstractWrapper;
import com.baomidou.mybatisplus.core.conditions.ISqlSegment;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.interfaces.Compare;
import com.baomidou.mybatisplus.core.conditions.interfaces.Func;
import com.baomidou.mybatisplus.core.conditions.interfaces.Join;
import com.baomidou.mybatisplus.core.conditions.interfaces.Nested;
import com.baomidou.mybatisplus.core.conditions.query.Query;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.baomidou.mybatisplus.core.override.MybatisMapperProxy;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import org.noear.solon.Solon;
import org.noear.solon.aot.RuntimeNativeMetadata;
import org.noear.solon.aot.RuntimeNativeRegistrar;
import org.noear.solon.aot.hint.ExecutableMode;
import org.noear.solon.aot.hint.MemberCategory;
import org.noear.solon.core.AppClassLoader;
import org.noear.solon.core.AppContext;
import org.noear.solon.core.util.ClassUtil;
import org.noear.solon.core.util.ScanUtil;

import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Method;

/**
 * mybatis-plus aot 注册 native 元数据（对齐 mybatis-plus-spring-boot-native-image 模块）
 *
 * @author songyinyin
 * @since 2.3
 */
public class MybatisPlusRuntimeNativeRegistrar implements RuntimeNativeRegistrar {

    @Override
    public void register(AppContext context, RuntimeNativeMetadata metadata) {
        //语言驱动与配置
        metadata.registerDefaultConstructor(MybatisXMLLanguageDriver.class);
        metadata.registerReflection(MybatisConfiguration.class, MemberCategory.DECLARED_FIELDS, MemberCategory.INTROSPECT_PUBLIC_CONSTRUCTORS);
        metadata.registerAllDeclaredMethod(MybatisConfiguration.class, ExecutableMode.INVOKE);

        //mapper 代理
        metadata.registerReflection(MybatisMapperProxy.class, MemberCategory.DECLARED_FIELDS, MemberCategory.INTROSPECT_PUBLIC_CONSTRUCTORS);
        metadata.registerAllDeclaredMethod(MybatisMapperProxy.class, ExecutableMode.INVOKE);
        metadata.registerReflection("com.baomidou.mybatisplus.solon.override.SolonMybatisMapperProxy",
                MemberCategory.DECLARED_FIELDS, MemberCategory.INVOKE_DECLARED_CONSTRUCTORS, MemberCategory.INVOKE_DECLARED_METHODS);
        metadata.registerAllDeclaredMethod(BaseMapper.class, ExecutableMode.INVOKE);

        //条件构造器家族（扫描注册所有 Wrapper 派生类）
        metadata.registerJdkProxy(AbstractWrapper.DoSomething.class);
        metadata.registerReflection(AbstractWrapper.DoSomething.class);
        registerWrapperHierarchy(metadata);

        metadata.registerReflection(ISqlSegment.class, MemberCategory.DECLARED_FIELDS, MemberCategory.INTROSPECT_PUBLIC_CONSTRUCTORS);
        metadata.registerReflection(Compare.class, MemberCategory.DECLARED_FIELDS, MemberCategory.INTROSPECT_PUBLIC_CONSTRUCTORS);
        metadata.registerReflection(Func.class, MemberCategory.DECLARED_FIELDS, MemberCategory.INTROSPECT_PUBLIC_CONSTRUCTORS);
        metadata.registerReflection(Join.class, MemberCategory.DECLARED_FIELDS, MemberCategory.INTROSPECT_PUBLIC_CONSTRUCTORS);
        metadata.registerReflection(Nested.class, MemberCategory.DECLARED_FIELDS, MemberCategory.INTROSPECT_PUBLIC_CONSTRUCTORS);
        metadata.registerReflection(Query.class, MemberCategory.DECLARED_FIELDS, MemberCategory.INTROSPECT_PUBLIC_CONSTRUCTORS);

        //实体元数据（aot 期应用上下文已完成 mapper 装载，TableInfo 缓存即全部实体）
        for (TableInfo tableInfo : TableInfoHelper.getTableInfos()) {
            metadata.registerReflection(tableInfo.getEntityType(),
                    MemberCategory.DECLARED_FIELDS,
                    MemberCategory.INVOKE_DECLARED_CONSTRUCTORS,
                    MemberCategory.INVOKE_DECLARED_METHODS);
        }

        //lambda 序列化（LambdaQueryWrapper 列解析依赖）
        metadata.registerSerialization(SerializedLambda.class);
        metadata.registerSerialization(SFunction.class);
        registerLambdaCapturingClasses(metadata);
    }

    /**
     * 注册所有 Wrapper 派生类的反射元数据
     */
    private void registerWrapperHierarchy(RuntimeNativeMetadata metadata) {
        for (String name : ScanUtil.scan(AppClassLoader.global(), "com/baomidou/mybatisplus", n -> n.endsWith(".class"))) {
            String className = name.substring(0, name.length() - 6).replace('/', '.');
            try {
                Class<?> clz = ClassUtil.loadClass(AppClassLoader.global(), className);
                if (clz != null && Wrapper.class.isAssignableFrom(clz)) {
                    metadata.registerReflection(clz, MemberCategory.DECLARED_FIELDS, MemberCategory.INTROSPECT_PUBLIC_CONSTRUCTORS);
                    metadata.registerAllDeclaredMethod(clz, ExecutableMode.INVOKE);
                }
            } catch (LinkageError ignored) {
                //可选依赖缺失（如 kotlin wrapper），跳过
            }
        }
    }

    /**
     * 注册应用主包内含 $deserializeLambda$ 的类（SFunction 方法引用反序列化依赖）
     */
    private void registerLambdaCapturingClasses(RuntimeNativeMetadata metadata) {
        Class<?> source = Solon.app().source();
        if (source == null || source.getPackage() == null) {
            return;
        }
        String dir = source.getPackage().getName().replace('.', '/');
        for (String name : ScanUtil.scan(AppClassLoader.global(), dir, n -> n.endsWith(".class"))) {
            String className = name.substring(0, name.length() - 6).replace('/', '.');
            try {
                Class<?> clz = ClassUtil.loadClass(AppClassLoader.global(), className);
                if (clz == null) {
                    continue;
                }
                for (Method method : clz.getDeclaredMethods()) {
                    if (method.getName().contains("$deserializeLambda$")) {
                        metadata.registerLambdaSerialization(clz);
                        break;
                    }
                }
            } catch (LinkageError ignored) {
                //可选依赖缺失，跳过
            }
        }
    }

}
