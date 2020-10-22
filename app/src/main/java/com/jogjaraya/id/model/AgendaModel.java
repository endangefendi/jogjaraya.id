package com.jogjaraya.id.model;

public class AgendaModel {

    private String event_id,  event_judul,  event_tanggal_mulai,  event_tanggal_selesai,
     event_deskripsi,  event_lokasi,  event_wilayah_kode,
     event_gambar;

    public AgendaModel(String event_id, String event_judul, String event_tanggal_mulai, String event_tanggal_selesai,
                       String event_deskripsi, String event_lokasi, String event_wilayah_kode,
                       String event_gambar) {
        this.event_id = event_id;
        this.event_judul = event_judul;
        this.event_tanggal_mulai = event_tanggal_mulai;
        this.event_tanggal_selesai = event_tanggal_selesai;
        this.event_deskripsi = event_deskripsi;
        this.event_lokasi = event_lokasi;
        this.event_wilayah_kode = event_wilayah_kode;
        this.event_gambar = event_gambar;
    }

    public String getEvent_id() {
        return event_id;
    }

    public void setEvent_id(String event_id) {
        this.event_id = event_id;
    }

    public String getEvent_judul() {
        return event_judul;
    }

    public void setEvent_judul(String event_judul) {
        this.event_judul = event_judul;
    }

    public String getEvent_tanggal_mulai() {
        return event_tanggal_mulai;
    }

    public void setEvent_tanggal_mulai(String event_tanggal_mulai) {
        this.event_tanggal_mulai = event_tanggal_mulai;
    }

    public String getEvent_tanggal_selesai() {
        return event_tanggal_selesai;
    }

    public void setEvent_tanggal_selesai(String event_tanggal_selesai) {
        this.event_tanggal_selesai = event_tanggal_selesai;
    }

    public String getEvent_deskripsi() {
        return event_deskripsi;
    }

    public void setEvent_deskripsi(String event_deskripsi) {
        this.event_deskripsi = event_deskripsi;
    }

    public String getEvent_lokasi() {
        return event_lokasi;
    }

    public void setEvent_lokasi(String event_lokasi) {
        this.event_lokasi = event_lokasi;
    }

    public String getEvent_wilayah_kode() {
        return event_wilayah_kode;
    }

    public void setEvent_wilayah_kode(String event_wilayah_kode) {
        this.event_wilayah_kode = event_wilayah_kode;
    }

    public String getEvent_gambar() {
        return event_gambar;
    }

    public void setEvent_gambar(String event_gambar) {
        this.event_gambar = event_gambar;
    }
}