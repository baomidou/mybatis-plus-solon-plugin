package demo.controller;

import demo.dso.service.AppServicePlus;
import demo.model.AppxModel;
import org.apache.ibatis.solon.annotation.Db;
import org.noear.solon.annotation.Controller;
import org.noear.solon.annotation.Mapping;

/**
 * @author noear 2022/3/28 created
 */
@Mapping("/plus/")
@Controller
public class PlusController {
    @Db
    AppServicePlus appServicePlus;

    @Mapping("test")
    public AppxModel test() {
        return appServicePlus.getById(12);
    }
}
