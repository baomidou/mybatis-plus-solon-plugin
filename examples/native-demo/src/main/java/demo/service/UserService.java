package demo.service;

import com.baomidou.mybatisplus.solon.service.impl.ServiceImpl;
import demo.mapper.UserMapper;
import demo.model.UserModel;
import org.noear.solon.annotation.Component;

@Component
public class UserService extends ServiceImpl<UserMapper, UserModel> {

}
