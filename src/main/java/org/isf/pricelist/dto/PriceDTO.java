package org.isf.pricelist.dto;

import org.isf.priceslist.model.PriceList;


public class PriceDTO {
    private int id;

    private PriceList list;

    private String group;

    private String item;

    private String description;

    private Double price;

    private boolean editable;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public PriceList getList() {
        return list;
    }

    public void setList(PriceList list) {
        this.list = list;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public String getDesc() {
        return description;
    }

    public void setDesc(String desc) {
        this.description = desc;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public boolean isPrice() {
        return item.compareTo("") != 0;
    }

    public boolean isEditable() {
        return editable;
    }

    public void setEditable(boolean editable) {
        this.editable = editable;
    }


}
