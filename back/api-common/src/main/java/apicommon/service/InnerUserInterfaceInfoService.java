package apicommon.service;


import apicommon.model.entity.UserInterfaceInfo;
import com.baomidou.mybatisplus.extension.service.IService;



/**
 * @author Kramir
 * @description 针对表【user_interface_info(用户接口关系表)】的数据库操作Service
 * @createDate 2024-05-21 13:44:08
 */
public interface InnerUserInterfaceInfoService{

    //void validUserInterfaceInfo(UserInterfaceInfo userInterfaceInfo, boolean add);
    boolean invokeCount(Long interfaceInfoId, Long userId);
}
