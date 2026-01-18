package org.sxi.biz;

import org.sxi.dao.PropertiesDataCore;
import org.sxi.vo.Property;

import java.util.Map;

public class PropertyBiz {

    public void changeProperty(Property property) {
        if (property == null || property.getName() == null || property.getName().trim().isEmpty()) {
            throw new BizException("属性名不能为空");
        }
        
        // 更新配置
        PropertiesDataCore.updateProperty(property.getName(), property.getValue());
    }
    
    public void changeProperties(Map<String, String> properties) {
        if (properties == null || properties.isEmpty()) {
            throw new BizException("配置列表不能为空");
        }
        
        // 更新配置
        PropertiesDataCore.updateProperties(properties);
    }
}
