package apicommon.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户接口关系表
 *
 * @TableName user_interface_info
 */
@TableName(value = "user_interface_info")
@Data
public class UserInterfaceInfo implements Serializable {
    /**
     * 编号
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户编号
     */
    private Long userId;

    /**
     * 接口id
     */
    private Long interfaceId;

    /**
     * 总使用次数
     */
    private Long times;

    /**
     * 剩余调用次数
     */
    private Long leftTime;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 是否删除 0-未删除 1-删除
     */
    @TableLogic
    private Integer isDelete;

    /**
     * 0正常 1禁用
     */
    private Integer status;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}