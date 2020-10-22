package com.jogjaraya.id.model;

public class MenuBannerModel {
    String banner; String url; String tipe;
    public MenuBannerModel(String banner, String url, String tipe) {
        this.banner= banner;
        this.url=url;
        this.tipe=tipe;
    }

    public String getBanner() {
        return banner;
    }

    public void setBanner(String banner) {
        this.banner = banner;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTipe() {
        return tipe;
    }

    public void setTipe(String tipe) {
        this.tipe = tipe;
    }
}
