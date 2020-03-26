package com.cntracechain.star.qrCode;

import java.io.InputStream;
import java.io.OutputStream;

public interface QrCodeHandler {

    /**
     * 通过内容生成二维码到流
     * @param content
     * @param input
     * @param output
     */
    void encoderQRCode(String content, InputStream input, OutputStream output, Integer scaling);

    /**
     * 通过内容生成二维码到流
     * @param content
     * @param output
     */
    void encoderQRCodeWithoutBackground(String content, OutputStream output, Integer scaling);

    /**
     * 通过流识别二维码内容
     * @param input
     * @return
     */
    String decoderQRCode(InputStream input);
}
