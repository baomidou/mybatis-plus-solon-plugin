package demo.controller;

import demo.dso.mapper.AppxMapper;
import demo.model.AppxModel;
import org.apache.ibatis.solon.annotation.Db;
import org.noear.solon.annotation.Controller;
import org.noear.solon.annotation.Mapping;

/**
 * 分包模式，一开始就被会话工厂mapperScan()并关联好了
 * */
@Mapping("/demo/")
@Controller
public class DemoController {
    @Db
    AppxMapper appxMapper;

    @Mapping("test")
    public AppxModel test(){
        return appxMapper.appx_get();
    }
}
