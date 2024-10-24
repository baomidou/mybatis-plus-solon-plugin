package demo.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import demo.dso.mapper.AppxMapper;
import demo.model.AppxModel;
import org.apache.ibatis.solon.annotation.Db;
import org.noear.solon.annotation.Controller;
import org.noear.solon.annotation.Mapping;

/**
 * 分面演示（使用sqlhelper组件）
 *
 * */
@Mapping("/page/")
@Controller
public class PageController {
    @Db
    AppxMapper appxMapper;

    @Mapping("test")
    public Object test() throws Throwable {
        Page<AppxModel> page = new Page<>(2, 2);

        return appxMapper.appx_get_page(page);
    }
}
