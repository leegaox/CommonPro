package com.biu.modulebase.common.base;

import java.io.Serializable;

/**
 * 对象的基类
 * 
 * @author Lee
 *
 */
public class BaseItem extends Object implements Serializable, Cloneable {
    private static final long serialVersionUID = 8162436250378587953L;
    /**主键id**/
    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
