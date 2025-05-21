package event.nbc.s3;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import event.nbc.s3.exception.S3ExceptionCode;
import lombok.RequiredArgsConstructor;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;
import event.nbc.s3.exception.S3Exception;

@Component
@RequiredArgsConstructor
public class S3Service {

	private final S3Presigner s3Presigner;

	@Value("${cloud.aws.s3.bucket}")
	private String bucketName;

	public List<String> getUploadPresignedUrl(Long eventId, List<String> image) {

		List<String> keys = image.stream().map(imageName -> imageName + eventId).toList();
		List<String> preSignedUrls = new ArrayList<>();

		for (String key : keys) {
			try {
				PutObjectRequest putObjectRequest =
					PutObjectRequest.builder()
						.bucket(bucketName)
						.key(key)
						.build();

				PutObjectPresignRequest putObjectPresignRequest =
					PutObjectPresignRequest.builder()
						.signatureDuration(Duration.ofMinutes(5))
						.putObjectRequest(putObjectRequest)
						.build();

				PresignedPutObjectRequest presignedPutObjectRequest =
					s3Presigner.presignPutObject(putObjectPresignRequest);

				preSignedUrls.add(presignedPutObjectRequest.url().toString());
			} catch (Exception e) {
				throw new S3Exception(S3ExceptionCode.PRESIGNED_PUT_URL_GENERATION_FAILED);
			}
		}
		return preSignedUrls;
	}

	public String getPresignedUrl(Long eventId, String imageName) {

		String key = imageName + eventId;

		try {
			GetObjectRequest getObjectRequest = GetObjectRequest.builder()
				.bucket(bucketName)
				.key(key)
				.build();

			GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
				.getObjectRequest(getObjectRequest)
				.signatureDuration(Duration.ofMinutes(5))
				.build();

			return s3Presigner.presignGetObject(presignRequest)
				.url()
				.toString();

		} catch (Exception e) {
			throw new S3Exception(S3ExceptionCode.PRESIGNED_GET_URL_GENERATION_FAILED);
		}
	}
}


