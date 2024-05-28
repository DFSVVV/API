package apicommon.service;


import apicommon.model.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;



/**
 * 用户服务
 *
 * @author DFSVVV
 */
public interface InnerUserService {
    User getInvokeUser(String accessKey);

}
