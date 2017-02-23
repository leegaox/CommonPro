package com.biu.modulebase.common.widget.expandablelistview;

import com.biu.modulebase.common.base.BaseItem;

public class CityVO extends BaseItem {
    private String name;

    /**
     *
     */
    private static final long serialVersionUID = -818298150422450699L;
    /** 城市的id号 **/
    private String city_id;
    /** 城市名 **/
    private String city_name;
    /** 属于哪个省市 **/
    private String city_sub_id;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCity_id() {
        return city_id;
    }

    public void setCity_id(String city_id) {
        this.city_id = city_id;
    }

    public String getCity_name() {
        return city_name;
    }

    public void setCity_name(String city_name) {
        this.city_name = city_name;
    }

    public String getCity_sub_id() {
        return city_sub_id;
    }

    public void setCity_sub_id(String city_sub_id) {
        this.city_sub_id = city_sub_id;
    }
}
