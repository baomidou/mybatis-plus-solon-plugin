package features;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import demo.DemoApp;
import demo.dso.mapper.AppxMapperPlus;
import demo.dso.mapper.AppxMapperPlusEx;
import demo.dso.service.AppxService;
import demo.model.AppxModel;
import org.apache.ibatis.solon.annotation.Db;
import org.junit.jupiter.api.Test;
import org.noear.solon.annotation.Inject;
import org.noear.solon.test.SolonTest;

/**
 * @author noear 2022/3/28 created
 */
@SolonTest(DemoApp.class)
public class PlusServiceTest {

    @Inject
    AppxService appxService;

    @Db
    AppxMapperPlus appxMapperPlus;
    @Db
    AppxMapperPlus appxMapperPlus2;

    @Db
    AppxMapperPlusEx appxMapperPlusEx;
    @Db
    AppxMapperPlusEx appxMapperPlusEx2;

    @Test
    public void selectById() {
        AppxModel app = appxService.getById(2);
        System.out.println(app);

        assert app != null;
        assert app.getAppId() == 2;
    }

    @Test
    public void selectById1() {
        AppxModel app = appxMapperPlus.selectById(2);
        System.out.println(app);

        assert app != null;
        assert app.getAppId() == 2;
    }

    @Test
    public void selectById2() {
        AppxModel app = appxMapperPlusEx.selectById(2);
        System.out.println(app);

        assert app != null;
        assert app.getAppId() == 2;
    }

    @Test
    public void selectOne() {
        AppxModel app = appxService.getOne(2);
        System.out.println(app);

        assert app != null;
        assert app.getAppId() == 2;
    }


    @Test
    public void selectPage() {
        IPage<AppxModel> page = appxService.page(new Page<>(1, 10));

        assert page != null;

        System.out.println("iPage.getRecords().size(): " + page.getRecords().size());
        assert !page.getRecords().isEmpty();

        System.out.println("iPage.getTotal(): " + page.getTotal());
        assert page.getTotal() > 0;
    }
}
