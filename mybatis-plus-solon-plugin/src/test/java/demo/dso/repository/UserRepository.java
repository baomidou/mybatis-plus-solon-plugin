package demo.dso.repository;

import com.baomidou.mybatisplus.solon.repository.CrudRepository;
import demo.dso.mapper.UserMapper;
import demo.model.UserModel;
import org.noear.solon.annotation.Component;

@Component
public class UserRepository extends CrudRepository<UserMapper, UserModel> {

}
