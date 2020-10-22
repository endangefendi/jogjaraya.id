package com.jogjaraya.id.model;

public class MenuIconModel {
    String judul,icon,slug;
    public MenuIconModel(String judul,String icon,String slug) {
        this.judul=judul;
        this.icon=icon;
        this.slug=slug;
    }

    public String getJudul() {
        return judul;
    }

    public void setJudul(String judul) {
        this.judul = judul;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }
}
