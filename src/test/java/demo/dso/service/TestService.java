package demo.dso.service;

import demo.model.AppxModel;
import org.noear.solon.annotation.Component;

/**
 * @author noear 2022/4/26 created
 */
@Component
public class TestService extends TestServiceBase {
    public AppxModel getApp(int app_id) throws Exception {
        return sqlMapper1.appx_get2(app_id);
    }
}
