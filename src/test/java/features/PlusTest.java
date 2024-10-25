package features;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import demo.DemoApp;
import demo.dso.mapper.AppxMapper2;
import demo.model.AppxModel;
import org.junit.jupiter.api.Test;
import org.noear.solon.annotation.Inject;
import org.noear.solon.test.SolonTest;


/**
 * @author noear 2021/9/3 created
 */
@SolonTest(DemoApp.class)
public class PlusTest {

    @Inject
    AppxMapper2 appxMapper2;

    @Test
    public void selectById() {
        AppxModel app = appxMapper2.selectById(2);
        System.out.println(app);

        assert app != null;
        assert app.getAppId() == 2;
    }

    @Test
    public void selectOne() {
        AppxModel app = appxMapper2.selectOne(new QueryWrapper<AppxModel>().eq("app_id",2));
        System.out.println(app);

        assert app != null;
        assert app.getAppId() == 2;
    }


    @Test
    public void selectPage() {
        Page<AppxModel> page = new Page<>(1, 10);
        IPage<AppxModel> iPage = appxMapper2.selectPage(page, new QueryWrapper<>());


        assert iPage != null;

        System.out.println("iPage.getRecords().size(): " + iPage.getRecords().size());
        assert iPage.getRecords().size() > 0;

        System.out.println("iPage.getTotal(): " + iPage.getTotal());
        assert iPage.getTotal() > 0;
    }
}
