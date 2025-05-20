package event.nbc.s3;

import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

@Profile("!test")
@Component
@RequiredArgsConstructor
public class S3Service {

	private final S3Presigner s3Presigner;
	private final AmazonS3 s3Client;

	@Value("${cloud.aws.s3.bucket}")
	private String bucketName;

	public List<String> getUploadPresignedUrl(Long eventId, List<String> imageName) {

		List<String> keys = imageName.stream().map(name -> name + eventId).toList();
		List<String> preSignedUrls = new ArrayList<>();

		for (String key : keys) {

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
		}

		return preSignedUrls;
	}

	public String getPresignedUrl(Long eventId, String imageName) {

		String key = imageName + eventId;

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
	}

	public byte[] getImageBytes(String key) {
		S3Object s3Object = s3Client.getObject(bucketName, key);

		try (S3ObjectInputStream inputStream = s3Object.getObjectContent()) {
			return inputStream.readAllBytes();
		} catch (IOException e) {
			throw new RuntimeException("이미지 읽기 실패", e);
		}
	}
}


