package com.cntracechain.star.qrCode;

import com.swetake.util.Qrcode;
import jp.sourceforge.qrcode.QRCodeDecoder;
import jp.sourceforge.qrcode.exception.DecodingFailedException;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class QrCodeCreate implements QrCodeHandler {

    private static final String IMG_TYPE = "bmp";

    private static final int SIZE = 3;

    private static final int STAR_SIZE= 12;

    public QrCodeCreate() {
    }

    public void encoderQRCode(String content, InputStream input, OutputStream output, Integer scaling) {
        this.encoderQRCode(content, input, output, IMG_TYPE, SIZE, scaling);
    }

    @Override
    public void encoderQRCodeWithoutBackground(String content, OutputStream output, Integer scaling) {
        this.encoderQRCode(content, null, output, IMG_TYPE, SIZE, scaling);
    }

    public String decoderQRCode(InputStream input) {
        String content = null;
        try {
            BufferedImage bufImg = ImageIO.read(input);
            QRCodeDecoder decoder = new QRCodeDecoder();
            content = new String(decoder.decode(new QrCodeCreateImg(bufImg)), "utf-8");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (DecodingFailedException e) {
            e.printStackTrace();
        }
        return content;
    }

    /**
     * 生成二维码(QRCode)图片
     *
     * @param content 存储内容
     * @param input   背景图流
     * @param output  最终生成的二维码流
     * @param imgType 图片类型
     * @param size    二维码尺寸
     */
    private void encoderQRCode(String content, InputStream input, OutputStream output, String imgType, int size, Integer scaling) {
        try {
            BufferedImage bufImg = null;
            if (null == input) {
                BufferedImage backImg = new BufferedImage(30, 30, BufferedImage.TYPE_USHORT_GRAY);
                bufImg = this.qRCodeCommon(content, size, backImg, false);
            } else {
                BufferedImage backImg = ImageIO.read(input);
                bufImg = this.qRCodeCommon(content, size, backImg, true);
                bufImg = this.combine(bufImg, backImg);
            }
            if (bufImg != null) {
                if (scaling != null) {
                    bufImg = resize(bufImg,scaling,scaling);
                }
                ImageIO.write(bufImg, imgType, output);
            } else {
                throw new Exception("二维码生成失败:" + content);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 改变图片尺寸
     *
     * @param buffImg
     * @param width
     * @return
     */
    private BufferedImage changeSize(BufferedImage buffImg, int width) {
        Image img = buffImg.getScaledInstance(width, width, Image.SCALE_DEFAULT);

        buffImg = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_BYTE_GRAY);
        Graphics g = buffImg.createGraphics();
        g.drawImage(img, 0, 0, null);
        g.dispose();
        return buffImg;
    }

    /**
     * 二维码与背景图片组合
     *
     * @param qrImg
     * @return
     * @throws IOException
     */
    private BufferedImage combine(BufferedImage qrImg, BufferedImage backImg) throws IOException {

        qrImg = this.getNoBgColorImage(qrImg);
        int width = qrImg.getWidth();// 获取层图的宽度

        backImg = this.changeSize(backImg, width);

        Graphics2D graphics = backImg.createGraphics();

        graphics.drawImage(qrImg, 0, 0, width, width, null);
        graphics.dispose();// 释放图形上下文使用的系统资源

        return backImg;
    }

    /**
     * 提高亮度
     *
     * @param red
     * @param gre
     * @param blu
     * @param flag
     * @return
     */
    private static Color getBrighter(int red, int gre, int blu, int flag) {

        red += flag;
        gre += flag;
        blu += flag;

        if (red > 255) red = 255;
        if (gre > 255) gre = 255;
        if (blu > 255) blu = 255;

        if (red < 0) red = 0;
        if (gre < 0) gre = 0;
        if (blu < 0) blu = 0;

        return new Color(red, gre, blu);
    }

    /**
     * 图片白色背景透明化
     *
     * @param image
     * @return
     */
    private BufferedImage getNoBgColorImage(BufferedImage image) {
        ImageIcon imageIcon = new ImageIcon(image);
        BufferedImage bufferedImage = new BufferedImage(imageIcon.getIconWidth(), imageIcon.getIconHeight(), BufferedImage.TYPE_4BYTE_ABGR);
        Graphics2D graphics = (Graphics2D) bufferedImage.getGraphics();
        graphics.drawImage(imageIcon.getImage(), 0, 0, imageIcon.getImageObserver());
        int alpha = 0;
        for (int j1 = bufferedImage.getMinY(); j1 < bufferedImage.getHeight(); j1++) {
            for (int j2 = bufferedImage.getMinX(); j2 < bufferedImage.getWidth(); j2++) {
                int rgb = bufferedImage.getRGB(j2, j1);

                int R = (rgb & 0xff0000) >> 16;
                int G = (rgb & 0xff00) >> 8;
                int B = (rgb & 0xff);
                if (R == 255 && G == 255 && B == 255) {
                    rgb = ((alpha + 1) << 24) | (rgb & 0x00ffffff);
                }

                bufferedImage.setRGB(j2, j1, rgb);
            }
        }

        graphics.drawImage(bufferedImage, 0, 0, imageIcon.getImageObserver());
        graphics.dispose();
        return bufferedImage;
    }

    /**
     * 生成黑白二维码
     *
     * @param content
     * @param size
     * @return
     */
    private BufferedImage qRCodeCommon(String content, int size, BufferedImage backImg, boolean back) {
        BufferedImage bufImg = null;
        try {
            Qrcode qrcode = new Qrcode();
            // 设置二维码排错率，可选L(7%)、M(15%)、Q(25%)、H(30%)，排错率越高可存储的信息越少，但对二维码清晰度的要求越小
            qrcode.setQrcodeErrorCorrect('H');
            qrcode.setQrcodeEncodeMode('B');
            // 设置设置二维码尺寸，取值范围1-40，值越大尺寸越大，可存储的信息越大
            qrcode.setQrcodeVersion(size);

            // 获得内容的字节数组，设置编码格式
            byte[] contentBytes = content.getBytes("utf-8");
            // 图片尺寸
            boolean codeOut[][] = qrcode.calQrcode(contentBytes);

            int x = 8;

            int imgSize = (codeOut.length * 3 + 4) * x;

            backImg = this.changeSize(backImg, imgSize);

            bufImg = new BufferedImage(imgSize, imgSize, BufferedImage.TYPE_BYTE_BINARY);
            Graphics2D graphics = bufImg.createGraphics();

            graphics.setBackground(Color.WHITE);
            graphics.clearRect(0, 0, imgSize, imgSize);

            // 设置偏移量，不设置可能导致解析出错
            int pixoff = 1;
            // 输出内容> 二维码
            if (contentBytes.length > 0 && contentBytes.length < 800) {
                for (int i = 0; i < codeOut.length; i++) {
                    for (int j = 0; j < codeOut.length; j++) {
                        if (codeOut[j][i]) {
                            if (this.check(j, i, codeOut.length)) {
                                graphics.setColor(Color.BLACK);
                                graphics.fillRect((j * 3 + pixoff) * x, (i * 3 + pixoff) * x, 3 * x, 3 * x);
                            } else {
                                graphics.setColor(Color.BLACK);
                                star(graphics, (j * 3 + pixoff) * x + STAR_SIZE, (i * 3 + pixoff) * x + STAR_SIZE, STAR_SIZE);
                            }
                        } else if (back) {
                            int rgb = backImg.getRGB((j * 3 + pixoff) * x, (i * 3 + pixoff) * x);

                            int R = (rgb & 0xff0000) >> 16;
                            int G = (rgb & 0xff00) >> 8;
                            int B = (rgb & 0xff);

                            int rgb2 = backImg.getRGB((j * 3 + pixoff) * x + STAR_SIZE / 2, (i * 3 + pixoff) * x + STAR_SIZE / 2);

                            int R2 = (rgb2 & 0xff0000) >> 16;
                            int G2 = (rgb2 & 0xff00) >> 8;
                            int B2 = (rgb2 & 0xff);
                            if ((R != 255 || G != 255 || B != 255) && (R2 != 255 || G2 != 255 || B2 != 255)) {
                                graphics.setColor(this.getBrighter(R, G, B, 200));
                                star(graphics, (j * 3 + pixoff) * x + STAR_SIZE, (i * 3 + pixoff) * x + STAR_SIZE, STAR_SIZE);
                            }
                        }
                    }
                }
            } else {
                throw new Exception("QRCode content bytes length = " + contentBytes.length + " not in [0, 800].");
            }
            //graphics.setColor(Color.WHITE);
            //graphics.drawString(String.format("%s_%s", SIZE, STAR_SIZE), 30,30);
            graphics.dispose();
            bufImg.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bufImg;
    }

    /**
     * 特征框不能够改变形状
     *
     * @param j
     * @param i
     * @param length
     * @return
     */
    private boolean symmetry(int j, int i, int length) {
        if (i == 0 || i == 6) {
            if (j < 7 || (length - j) < 8) {
                return true;
            }
        }

        if (1 < i && i < 5 && 1 < j && j < 5) {
            return true;
        }

        if (length - 2 > i && i > length - 6 && 1 < j && j < 5) {
            return true;
        }

        if (i == length - 5 && length - 10 < j && j < length - 4) {
            return true;
        }

        if (i == length - 9 && length - 10 < j && j < length - 4) {
            return true;
        }

        if (i == length - 7 && j == length - 7) {
            return true;
        }

        if (i == length - 1 || i == length - 7) {
            if (j < 7) {
                return true;
            }
        }
        return false;
    }

    private boolean check(int j, int i, int length) {
        return symmetry(j, i, length) || symmetry(i, j, length);
    }

    /**
     * 画五角星⭐
     *
     * @param g
     * @param x0
     * @param y0
     * @param r
     */
    private void star(Graphics g, int x0, int y0, int r) {
        double ch = 72 * Math.PI / 180;// 圆心角的弧度数
        int x1 = x0;
        int x2 = (int) (x0 - Math.sin(ch) * r);
        int x3 = (int) (x0 + Math.sin(ch) * r);
        int x4 = (int) (x0 - Math.sin(ch / 2) * r);
        int x5 = (int) (x0 + Math.sin(ch / 2) * r);

        int y1 = y0 - r;
        int y2 = (int) (y0 - Math.cos(ch) * r);
        int y3 = y2;
        int y4 = (int) (y0 + Math.cos(ch / 2) * r);
        int y5 = y4;

        int bx = (int) (x0 + Math.cos(ch) * Math.tan(ch / 2) * r);
        int by = y2;

        Polygon a = new Polygon();
        Polygon b = new Polygon();

        a.addPoint(x2, y2);
        a.addPoint(x5, y5);
        a.addPoint(bx, by);
        b.addPoint(x1, y1);
        b.addPoint(bx, by);
        b.addPoint(x3, y3);
        b.addPoint(x4, y4);

        g.fillPolygon(a);
        g.fillPolygon(b);

    }

    /**
     * 图像缩放
     * @param source
     * @param targetW
     * @param targetH
     * @return
     */
    public BufferedImage resize(BufferedImage source, int targetW,
                                       int targetH) {
        int type = source.getType();
        BufferedImage target = null;
        double sx = (double) targetW / source.getWidth();
        double sy = (double) targetH / source.getHeight();
        target = new BufferedImage(targetW, targetH, type);
        Graphics2D g = target.createGraphics();
        g.drawRenderedImage(source, AffineTransform.getScaleInstance(sx, sy));
        g.dispose();
        return target;
    }

}

