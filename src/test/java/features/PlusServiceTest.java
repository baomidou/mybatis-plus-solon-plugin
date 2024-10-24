package features;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import demo.DemoApp;
import demo.dso.mapper.AppxMapperPlus;
import demo.dso.mapper.AppxMapperPlusEx;
import demo.dso.service.AppServicePlus;
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

    @Db
    AppServicePlus appServicePlus;
    @Inject
    AppServicePlus appServicePlus2;

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
        AppxModel app = appServicePlus.getById(2);
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
        AppxModel app = appServicePlus.getOne(new QueryWrapper<AppxModel>().eq("app_id",2));
        System.out.println(app);

        assert app != null;
        assert app.getAppId() == 2;
    }


    @Test
    public void selectPage() {
        Page<AppxModel> page = new Page<>(1, 10);
        IPage<AppxModel> iPage = appServicePlus.page(page, new QueryWrapper<>());


        assert iPage != null;

        System.out.println("iPage.getRecords().size(): " + iPage.getRecords().size());
        assert iPage.getRecords().size() > 0;

        System.out.println("iPage.getTotal(): " + iPage.getTotal());
        assert iPage.getTotal() > 0;
    }
}
