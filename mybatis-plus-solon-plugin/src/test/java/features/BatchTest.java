package features;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import demo.DemoApp;
import demo.dso.repository.UserRepository;
import demo.model.UserModel;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.noear.solon.annotation.Inject;
import org.noear.solon.test.SolonTest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 批量方法（saveBatch / updateBatchById / saveOrUpdateBatch）
 * 验证 3.5.17 CompatibleSet.executeBatch 新签名下的执行路径
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SolonTest(DemoApp.class)
public class BatchTest {

    @Inject
    UserRepository userRepository;

    @Test
    @Order(1)
    public void saveBatch() {
        List<UserModel> list = new ArrayList<>();
        for (int i = 1; i <= 3; i++) {
            UserModel user = new UserModel();
            user.setUserId((long) i);
            user.setNickname("batch-" + i);
            user.setPassword("p1");
            user.setDeleted(0);
            list.add(user);
        }

        assert userRepository.saveBatch(list, 100);
        assert list.stream().allMatch(u -> u.getId() != null);
        assert userRepository.count(new LambdaQueryWrapper<UserModel>().likeRight(UserModel::getNickname, "batch-")) == 3;
    }

    @Test
    @Order(2)
    public void updateBatchById() {
        List<UserModel> list = userRepository.list(new LambdaQueryWrapper<UserModel>().likeRight(UserModel::getNickname, "batch-"));
        assert list.size() == 3;

        list.forEach(u -> u.setPassword("p2"));
        assert userRepository.updateBatchById(list, 100);

        assert userRepository.count(new LambdaQueryWrapper<UserModel>().eq(UserModel::getPassword, "p2")) == 3;
    }

    @Test
    @Order(3)
    public void saveOrUpdateBatch() {
        UserModel existing = userRepository.getOne(new LambdaQueryWrapper<UserModel>().eq(UserModel::getNickname, "batch-1"));
        assert existing != null;
        existing.setNickname("batch-1-updated");

        UserModel fresh = new UserModel();
        fresh.setUserId(9L);
        fresh.setNickname("batch-9");
        fresh.setPassword("p3");
        fresh.setDeleted(0);

        assert userRepository.saveOrUpdateBatch(Arrays.asList(existing, fresh), 100);

        assert fresh.getId() != null;
        assert userRepository.getById(existing.getId()).getNickname().equals("batch-1-updated");
        assert userRepository.count(new LambdaQueryWrapper<UserModel>().likeRight(UserModel::getNickname, "batch-")) == 4;
    }
}
