package com.jogjaraya.id.model;

public class IklanModel {

    private String iklan_url, iklan_gambar;

    public IklanModel(String iklan_url, String iklan_gambar) {
        this.iklan_url = iklan_url;
        this.iklan_gambar = iklan_gambar;
    }

    public String getIklan_url() {
        return iklan_url;
    }

    public void setIklan_url(String iklan_url) {
        this.iklan_url = iklan_url;
    }

    public String getIklan_gambar() {
        return iklan_gambar;
    }

    public void setIklan_gambar(String iklan_gambar) {
        this.iklan_gambar = iklan_gambar;
    }
}