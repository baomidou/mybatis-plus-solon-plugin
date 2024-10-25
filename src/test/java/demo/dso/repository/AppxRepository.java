package demo.dso.repository;

import com.baomidou.mybatisplus.extension.repository.CrudRepository;
import demo.dso.mapper.AppxMapperPlusEx;
import demo.model.AppxModel;
import org.noear.solon.annotation.Component;

@Component
public class AppxRepository extends CrudRepository<AppxMapperPlusEx, AppxModel> {

}
