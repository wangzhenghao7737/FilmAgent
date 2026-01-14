package com.xiaosa.filmagent.entity.regions;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
public class RegionEntity {
    private String name;
    private String adcode;
    private String citycode;
    public RegionEntity(String name, String adcode, String citycode) {
        this.name = name;
        this.adcode = adcode;
        this.citycode = citycode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAdcode() {
        return adcode;
    }

    public void setAdcode(String adcode) {
        this.adcode = adcode;
    }

    public String getCitycode() {
        return citycode;
    }

    public void setCitycode(String citycode) {
        this.citycode = citycode;
    }
}
