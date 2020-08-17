package com.suchengyu.gmall.model.product;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.suchengyu.gmall.model.base.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * <p>
 * SpuImage
 * </p>
 *
 */
@Data
@ApiModel(description = "Spu图片")
@TableName("spu_image")
public class SpuImage extends BaseEntity {
	
	private static final long serialVersionUID = 1L;
	
	@ApiModelProperty(value = "商品id")
	@TableField("spu_id")
	private Long spuId;

	@ApiModelProperty(value = "图片名称")
	@TableField("img_name")
	private String imgName;

	@ApiModelProperty(value = "图片路径")
	@TableField("img_url")
	private String imgUrl;

	public static long getSerialVersionUID() {
		return serialVersionUID;
	}

	public Long getSpuId() {
		return spuId;
	}

	public void setSpuId(Long spuId) {
		this.spuId = spuId;
	}

	public String getImgName() {
		return imgName;
	}

	public void setImgName(String imgName) {
		this.imgName = imgName;
	}

	public String getImgUrl() {
		return imgUrl;
	}

	public void setImgUrl(String imgUrl) {
		this.imgUrl = imgUrl;
	}
}

