package com.jogjaraya.id.model;

public class JelajahArtikelModel {

    private String listing_id, listing_nama,  listing_alamat,  listing_kategori,  listing_deskripsi,
     listing_wilayah_kode,  listing_wilayah_nama,  listing_gambar;

    public JelajahArtikelModel(String listing_id, String listing_nama, String listing_alamat, String listing_kategori,
                               String listing_deskripsi, String listing_wilayah_kode, String listing_wilayah_nama,
                               String listing_gambar) {
        this.listing_id = listing_id;
        this.listing_nama = listing_nama;
        this.listing_alamat = listing_alamat;
        this.listing_kategori = listing_kategori;
        this.listing_deskripsi = listing_deskripsi;
        this.listing_wilayah_kode = listing_wilayah_kode;
        this.listing_wilayah_nama = listing_wilayah_nama;
        this.listing_gambar = listing_gambar;
    }

    public String getListing_id() {
        return listing_id;
    }

    public void setListing_id(String listing_id) {
        this.listing_id = listing_id;
    }

    public String getListing_nama() {
        return listing_nama;
    }

    public void setListing_nama(String listing_nama) {
        this.listing_nama = listing_nama;
    }

    public String getListing_alamat() {
        return listing_alamat;
    }

    public void setListing_alamat(String listing_alamat) {
        this.listing_alamat = listing_alamat;
    }

    public String getListing_kategori() {
        return listing_kategori;
    }

    public void setListing_kategori(String listing_kategori) {
        this.listing_kategori = listing_kategori;
    }

    public String getListing_deskripsi() {
        return listing_deskripsi;
    }

    public void setListing_deskripsi(String listing_deskripsi) {
        this.listing_deskripsi = listing_deskripsi;
    }

    public String getListing_wilayah_kode() {
        return listing_wilayah_kode;
    }

    public void setListing_wilayah_kode(String listing_wilayah_kode) {
        this.listing_wilayah_kode = listing_wilayah_kode;
    }

    public String getListing_wilayah_nama() {
        return listing_wilayah_nama;
    }

    public void setListing_wilayah_nama(String listing_wilayah_nama) {
        this.listing_wilayah_nama = listing_wilayah_nama;
    }

    public String getListing_gambar() {
        return listing_gambar;
    }

    public void setListing_gambar(String listing_gambar) {
        this.listing_gambar = listing_gambar;
    }
}