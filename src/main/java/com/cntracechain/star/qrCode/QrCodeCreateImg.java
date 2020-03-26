package com.cntracechain.star.qrCode;

import jp.sourceforge.qrcode.data.QRCodeImage;

import java.awt.image.BufferedImage;

// 二维码图片对象实现QRCodeImage接口
public class QrCodeCreateImg implements QRCodeImage {

	BufferedImage bufImg;

	public QrCodeCreateImg(BufferedImage bufImg) {
		this.bufImg = bufImg;
	}

	public int getHeight() {
		return bufImg.getHeight();
	}

	public int getPixel(int x, int y) {
		return bufImg.getRGB(x, y);
	}

	public int getWidth() {
		return bufImg.getWidth();
	}

}

