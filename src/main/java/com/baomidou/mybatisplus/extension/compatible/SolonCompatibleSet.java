/*
 * Copyright (c) 2011-2024, baomidou (jobob@qq.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.baomidou.mybatisplus.extension.compatible;

import com.baomidou.mybatisplus.core.toolkit.ExceptionUtils;
import org.apache.ibatis.logging.Log;
import org.apache.ibatis.reflection.ExceptionUtil;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.noear.solon.core.util.GenericUtil;
import org.noear.solon.core.util.ResourceUtil;
import org.noear.solon.data.tran.TranUtils;

import java.io.InputStream;
import java.util.function.Consumer;

/**
 * spring 兼容方法集接口实现类
 */
public class SolonCompatibleSet implements CompatibleSet {

    @Override
    public SqlSession getSqlSession(SqlSessionFactory sessionFactory) {
        return sessionFactory.openSession();
    }

    @Override
    public void closeSqlSession(SqlSession sqlSession, SqlSessionFactory sqlSessionFactory) {
        if (sqlSession != null) {
            sqlSession.close();
        }
    }

    @Override
    public boolean executeBatch(SqlSessionFactory sqlSessionFactory, Log log, Consumer<SqlSession> consumer) {
        boolean transaction = TranUtils.inTrans();
        SqlSession sqlSession = sqlSessionFactory.openSession(ExecutorType.BATCH);
        if (!transaction) {
            log.warn("SqlSession [" + sqlSession + "] Transaction not enabled");
        }
        try {
            consumer.accept(sqlSession);
            //非事务情况下，强制commit。
            sqlSession.commit(!transaction);
            return true;
        } catch (RuntimeException t) {
            throw t;
        } catch (Throwable t) {
            sqlSession.rollback();
            Throwable unwrapped = ExceptionUtil.unwrapThrowable(t);
            if (unwrapped instanceof RuntimeException) {
                throw (RuntimeException) unwrapped;
            } else {
                throw ExceptionUtils.mpe(unwrapped);
            }
        } finally {
            sqlSession.close();
        }
    }

    @Override
    public InputStream getInputStream(String path) throws Exception {
        return ResourceUtil.findResource(path).openStream();
    }

}
