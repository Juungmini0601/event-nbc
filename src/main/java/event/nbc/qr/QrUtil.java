package event.nbc.qr;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import org.springframework.stereotype.Component;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;

@Component
public class QrUtil {

	public byte[] toQrImage(String text, int width, int height) throws Exception {

		Map<EncodeHintType, Object> hints = new HashMap<>();

		hints.put(EncodeHintType.MARGIN, 1);
		BitMatrix matrix = new MultiFormatWriter()
			.encode(text, BarcodeFormat.QR_CODE, width, height, hints);

		BufferedImage qrImage = MatrixToImageWriter.toBufferedImage(matrix);

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		ImageIO.write(qrImage, "PNG", out);
		return out.toByteArray();
	}
}
