package demo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import demo.model.UserModel;

import java.util.List;

public interface UserMapper extends BaseMapper<UserModel> {

    /**
     * xml 定义的查询（验证 native 下 mapper xml 资源可用）
     */
    List<UserModel> selectAdults();
}
