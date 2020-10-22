package com.jogjaraya.id.model;

public class ArtikelModel {

    private String tulisan_slug, tulisan_judul,tulisan_isi,tulisan_gambar,tulisan_tanggal,tulisan_kategori_slug,tulisan_kategori_nama,tulisan_author;
    private int tulisan_views,tulisan_rating;

    public ArtikelModel(String tulisan_slug, String tulisan_judul, String tulisan_isi, String tulisan_gambar, String tulisan_tanggal,
                        int tulisan_views, int tulisan_rating, String tulisan_kategori_slug, String tulisan_kategori_nama, String tulisan_author) {
        this.tulisan_author=tulisan_author;
        this.tulisan_slug=tulisan_slug;
        this.tulisan_judul=tulisan_judul;
        this.tulisan_isi=tulisan_isi;
        this.tulisan_gambar=tulisan_gambar;
        this.tulisan_tanggal=tulisan_tanggal;
        this.tulisan_views=tulisan_views;
        this.tulisan_rating=tulisan_rating;
        this.tulisan_kategori_slug=tulisan_kategori_slug;
        this.tulisan_kategori_nama=tulisan_kategori_nama;
    }

    public String getTulisan_author() {
        return tulisan_author;
    }

    public void setTulisan_author(String tulisan_author) {
        this.tulisan_author = tulisan_author;
    }

    public String getTulisan_kategori_slug() {
        return tulisan_kategori_slug;
    }

    public void setTulisan_kategori_slug(String tulisan_kategori_slug) {
        this.tulisan_kategori_slug = tulisan_kategori_slug;
    }

    public String getTulisan_kategori_nama() {
        return tulisan_kategori_nama;
    }

    public void setTulisan_kategori_nama(String tulisan_kategori_nama) {
        this.tulisan_kategori_nama = tulisan_kategori_nama;
    }

    public int getTulisan_views() {
        return tulisan_views;
    }

    public void setTulisan_views(int tulisan_views) {
        this.tulisan_views = tulisan_views;
    }

    public int getTulisan_rating() {
        return tulisan_rating;
    }

    public void setTulisan_rating(int tulisan_rating) {
        this.tulisan_rating = tulisan_rating;
    }

    public String getTulisan_slug() {
        return tulisan_slug;
    }

    public void setTulisan_slug(String tulisan_slug) {
        this.tulisan_slug = tulisan_slug;
    }

    public String getTulisan_judul() {
        return tulisan_judul;
    }

    public void setTulisan_judul(String tulisan_judul) {
        this.tulisan_judul = tulisan_judul;
    }

    public String getTulisan_isi() {
        return tulisan_isi;
    }

    public void setTulisan_isi(String tulisan_isi) {
        this.tulisan_isi = tulisan_isi;
    }

    public String getTulisan_gambar() {
        return tulisan_gambar;
    }

    public void setTulisan_gambar(String tulisan_gambar) {
        this.tulisan_gambar = tulisan_gambar;
    }

    public String getTulisan_tanggal() {
        return tulisan_tanggal;
    }

    public void setTulisan_tanggal(String tulisan_tanggal) {
        this.tulisan_tanggal = tulisan_tanggal;
    }
}
