package features;

import demo.DemoApp;
import org.junit.jupiter.api.Test;
import org.noear.snack.ONode;
import org.noear.solon.test.HttpTester;
import org.noear.solon.test.SolonTest;


@SolonTest(DemoApp.class)
public class PageTest extends HttpTester {

    @Test
    public void test() throws Exception {
        String json = path("/page/test").get();
        assert ONode.loadStr(json).get("records").count() == 2;
    }
}
