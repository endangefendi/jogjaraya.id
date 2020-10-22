package com.jogjaraya.id.model;

public class DaruratModel {
    private String darurat_nama, darurat_slug;

    public DaruratModel(String darurat_nama, String darurat_slug) {
        this.darurat_nama = darurat_nama;
        this.darurat_slug = darurat_slug;
    }

    public String getDarurat_nama() {
        return darurat_nama;
    }

    public void setDarurat_nama(String darurat_nama) {
        this.darurat_nama = darurat_nama;
    }

    public String getDarurat_slug() {
        return darurat_slug;
    }

    public void setDarurat_slug(String darurat_slug) {
        this.darurat_slug = darurat_slug;
    }
}
