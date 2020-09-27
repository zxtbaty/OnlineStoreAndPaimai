package org.jinyuanjava.litemall.wx.vo;

import org.jinyuanjava.litemall.db.domain.LitemallCart;

public class cartVo extends LitemallCart {

	private Integer stock;//库存

	public cartVo(LitemallCart cart, Integer number) {
		this.setId(cart.getId());
		this.setUserId(cart.getUserId());
		setGoodsId(cart.getGoodsId());
		setGoodsSn(cart.getGoodsSn());
		setGoodsName(cart.getGoodsName());
		setProductId(cart.getProductId());
		setGoodsPosKey(cart.getGoodsPosKey());
		setPrice(cart.getPrice());
		setNumber(cart.getNumber());
		setSpecifications(cart.getSpecifications());
		setChecked(cart.getChecked());
	    setPicUrl(cart.getPicUrl());
	    setComId(cart.getComId());
	    setStoreId(cart.getStoreId());
	    setStoreName(cart.getStoreName());
	    setAddTime(cart.getAddTime());
	    setUpdateTime(cart.getUpdateTime());
	    setDeleted(cart.getDeleted());
	    setStock(number);
	}

	public Integer getStock() {
		return stock;
	}

	public void setStock(Integer stock) {
		this.stock = stock;
	}



}
