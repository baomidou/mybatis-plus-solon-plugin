package demo.controller;

import demo.dso.service.AppxService;
import demo.model.AppxModel;
import org.noear.solon.annotation.Controller;
import org.noear.solon.annotation.Inject;
import org.noear.solon.annotation.Mapping;

/**
 * @author noear 2022/3/28 created
 */
@Mapping("/plus/")
@Controller
public class PlusController {
    @Inject
    AppxService appxService;

    @Mapping("test")
    public AppxModel test() {
        return appxService.getById(12);
    }
}
