package demo.dso.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import demo.dso.repository.AppxRepository;
import demo.model.AppxModel;
import org.noear.solon.annotation.Component;
import org.noear.solon.annotation.Inject;

@Component
public class AppxService {
    @Inject
    public AppxRepository appxRepository;

    public AppxModel getById(int id) {
        return appxRepository.getById(id);
    }

    public AppxModel getOne(int i) {
        return appxRepository.getOne(new QueryWrapper<AppxModel>().eq("app_id", 2));
    }

    public IPage<AppxModel> page(Page<AppxModel> page) {
        return appxRepository.page(page);
    }
}
