package com.pblog.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.pblog.common.entity.User;
import com.pblog.common.utils.SecurityContextUtil;
import com.pblog.user.mapper.UserMapper;
import com.pblog.user.service.FileService;
import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

@Slf4j
@Service
public class FileServiceImpl implements FileService {

    // 头像存储的子目录（便于分类管理）
    private static final String AVATAR_DIR = "avatars/";
    // 头像最大尺寸（5MB）
    private static final long MAX_AVATAR_SIZE = 5 * 1024 * 1024;
    // 支持的图片格式
    private static final String[] ALLOWED_IMAGE_TYPES = {"image/jpeg", "image/png", "image/jpg"};

    @Value("${minio.bucket-name}")
    private String bucketName;
    @Value("${minio.endpoint}")
    private String minioEndpoint;

    @Autowired
    private MinioClient minioClient;
    @Autowired
    private UserMapper userMapper;


    @Override
    public String uploadAvatar(MultipartFile file) {
        try {
            // 1. 校验文件合法性
            validateAvatarFile(file);

            // 2. 处理图片（压缩、裁剪为200x200）
            InputStream processedStream = processAvatarImage(file);

            // 3. 生成唯一文件名（避免重复）：用户ID/日期/UUID.后缀
            Integer userId = SecurityContextUtil.getUser().getId();
            String fileName = generateAvatarFileName(file, userId);

            // 4. 确保MinIO桶存在，不存在则创建
            ensureBucketExists();

            // 5. 上传文件到MinIO
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(fileName)  // 存储路径+文件名
                            .stream(processedStream, processedStream.available(), -1)
                            .contentType(file.getContentType())  // 保持原文件类型
                            .build()
            );
            // TODO 删除原有的头像，或者最多保存9个历史头像

            // 6.更新用户对应的头像路径
            LambdaUpdateWrapper<User> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
            lambdaUpdateWrapper.eq(User::getId, userId)  // 条件
                    .set(User::getAvatar, fileName);     // 更新
            userMapper.update(null, lambdaUpdateWrapper);

            // 6. 生成并返回头像访问URL
            return generateAvatarUrl(fileName);

        } catch (Exception e) {
            throw new RuntimeException("头像上传失败：" + e.getMessage());
        }
    }

    /**
     * 校验头像文件（大小、格式、合法性）
     */
    private void validateAvatarFile(MultipartFile file) throws IOException {
        // 校验文件是否为空
        if (file.isEmpty()) {
            throw new RuntimeException("请选择头像文件");
        }

        // 校验文件大小
        if (file.getSize() > MAX_AVATAR_SIZE) {
            throw new RuntimeException("头像大小不能超过5MB");
        }

        // 校验文件类型（MIME类型）
        String contentType = file.getContentType();
        boolean isAllowed = false;
        for (String type : ALLOWED_IMAGE_TYPES) {
            if (type.equals(contentType)) {
                isAllowed = true;
                break;
            }
        }
        if (!isAllowed) {
            throw new RuntimeException("仅支持JPG、PNG格式的图片");
        }

        // 校验是否为真实图片（防止伪装文件）
        BufferedImage image = ImageIO.read(file.getInputStream());
        if (image == null) {
            throw new RuntimeException("无效的图片文件");
        }
    }

    /**
     * 处理头像图片（压缩+裁剪为200x200）
     */
    private InputStream processAvatarImage(MultipartFile file) throws IOException {
        // 压缩并裁剪为200x200的正方形（保持中心区域）
        BufferedImage processedImage = Thumbnails.of(file.getInputStream())
                .size(200, 200)  // 目标尺寸
                .keepAspectRatio(false)  // 强制尺寸（避免拉伸可先裁剪中心区域）
                .outputQuality(0.8)  // 压缩质量（0.0-1.0）
                .asBufferedImage();

        // 转换为InputStream返回
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(processedImage, "jpg", baos);  // 统一转为jpg格式
        return new ByteArrayInputStream(baos.toByteArray());
    }

    /**
     * 生成唯一文件名（格式：avatars/用户ID/20241010/UUID.jpg）
     */
    private String generateAvatarFileName(MultipartFile file, Integer userId) {
        // 获取文件后缀（默认jpg）
        String originalFilename = file.getOriginalFilename();
        String suffix = originalFilename != null && originalFilename.contains(".")
                ? originalFilename.substring(originalFilename.lastIndexOf("."))
                : ".jpg";

        // 日期目录（按天划分，便于管理）
        String dateDir = new SimpleDateFormat("yyyyMMdd").format(new Date());

        // 生成UUID避免文件名重复
        String uuid = UUID.randomUUID().toString().replaceAll("-", "");

        // 最终路径：avatars/1001/20241010/xxx.jpg
        return AVATAR_DIR + userId + "/" + dateDir + "/" + uuid + suffix;
    }

    /**
     * 确保MinIO桶存在，不存在则创建
     */
    private void ensureBucketExists() throws Exception {
        boolean exists = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
        if (!exists) {
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
        }
    }

    /**
     * 生成头像访问 URL
     */
    public String generateAvatarUrl(String fileName) {
        // 格式：MinIO地址/桶名/文件路径（需确保桶可公开访问或通过签名URL访问）
        // 参数校验
        if (fileName == null || fileName.trim().isEmpty()) {
            throw new IllegalArgumentException("文件名不能为空");
        }
        // 路径规范化（避免重复 "/"）
        String normalizedFileName = fileName.startsWith("/") ? fileName.substring(1) : fileName;
        return minioEndpoint + "/" + bucketName + "/" + normalizedFileName;
    }
}
