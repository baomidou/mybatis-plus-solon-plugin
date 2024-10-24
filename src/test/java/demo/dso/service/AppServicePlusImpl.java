package demo.dso.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import demo.dso.mapper.AppxMapperPlusEx;
import demo.model.AppxModel;
import org.noear.solon.annotation.Component;

/**
 * @author noear 2022/3/28 created
 */
@Component
public class AppServicePlusImpl extends ServiceImpl<AppxMapperPlusEx, AppxModel> implements AppServicePlus {
}
