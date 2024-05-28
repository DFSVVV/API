package apicommon.service;

import apicommon.model.entity.InterfaceInfo;
import com.baomidou.mybatisplus.extension.service.IService;


/**
 * @author Kramir
 * @description 针对表【interface_info(接口信息)】的数据库操作Service
 * @createDate 2024-05-18 11:47:27
 */
public interface InnerInterfaceInfoService  {
    InterfaceInfo getInterfaceInfo(String path,String method);
}
