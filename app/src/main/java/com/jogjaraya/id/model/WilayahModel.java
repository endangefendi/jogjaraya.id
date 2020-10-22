package com.jogjaraya.id.model;

public class WilayahModel {

    private String wilayah_kode, wilayah_nama;

    public WilayahModel(String wilayah_kode, String wilayah_nama) {
        this.wilayah_kode = wilayah_kode;
        this.wilayah_nama = wilayah_nama;
    }

    public String getWilayah_kode() {
        return wilayah_kode;
    }

    public void setWilayah_kode(String wilayah_kode) {
        this.wilayah_kode = wilayah_kode;
    }

    public String getWilayah_nama() {
        return wilayah_nama;
    }

    public void setWilayah_nama(String wilayah_nama) {
        this.wilayah_nama = wilayah_nama;
    }
}