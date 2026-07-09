package demo.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import demo.mapper.UserMapper;
import demo.model.UserModel;
import demo.service.UserService;
import org.noear.solon.annotation.Controller;
import org.noear.solon.annotation.Inject;
import org.noear.solon.annotation.Mapping;
import org.apache.ibatis.solon.annotation.Db;
import org.noear.solon.annotation.Param;

import java.util.List;

@Controller
public class UserController {

    @Inject
    private UserService userService;

    @Db
    private UserMapper userMapper;

    @Mapping("/user/list")
    public List<UserModel> list() {
        return userService.list(new LambdaQueryWrapper<UserModel>().ge(UserModel::getAge, 0));
    }

    @Mapping("/user/page")
    public Page<UserModel> page(@Param(defaultValue = "1") long current, @Param(defaultValue = "2") long size) {
        return userService.page(new Page<>(current, size));
    }

    @Mapping("/user/get")
    public UserModel get(long id) {
        return userService.getById(id);
    }

    @Mapping("/user/add")
    public UserModel add(String name, int age) {
        UserModel user = new UserModel();
        user.setName(name);
        user.setAge(age);
        userService.save(user);
        return user;
    }

    @Mapping("/user/adults")
    public List<UserModel> adults() {
        return userMapper.selectAdults();
    }
}
