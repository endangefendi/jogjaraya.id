package com.jogjaraya.id.model;

public class GambarModel {
    private int no;
    private String gambar;
    public GambarModel(int no, String gambar) {
        this.no=no;
        this.gambar=gambar;
    }

    public int getNo() {
        return no;
    }

    public void setNo(int no) {
        this.no = no;
    }

    public String getGambar() {
        return gambar;
    }

    public void setGambar(String gambar) {
        this.gambar = gambar;
    }
}
