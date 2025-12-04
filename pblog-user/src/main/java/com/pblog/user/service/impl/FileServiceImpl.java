package com.pblog.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.pblog.common.Expection.BusinessException;
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

    // 头像存储的子目录
    private static final String AVATAR_DIR = "avatars/";
    // 文章图片存储的子目录
    private static final String ARTICLE_IMAGE_DIR = "article-images/";

    // 头像最大尺寸（5MB）
    private static final long MAX_AVATAR_SIZE = 5 * 1024 * 1024;
    // 文章图片最大尺寸（10MB）
    private static final long MAX_ARTICLE_IMAGE_SIZE = 10 * 1024 * 1024;

    // 支持的图片格式
    private static final String[] ALLOWED_IMAGE_TYPES = {"image/jpeg", "image/png", "image/jpg", "image/gif"};

    // 文章图片最大宽度（超过则等比压缩）
    private static final int MAX_ARTICLE_IMAGE_WIDTH = 1920;
    // 文章图片压缩质量
    private static final float ARTICLE_IMAGE_QUALITY = 0.85f;

    @Value("${minio.bucket-name}")
    private String bucketName;
    @Value("${minio.endpoint}")
    private String minioEndpoint;

    @Autowired
    private MinioClient minioClient;
    @Autowired
    private UserMapper userMapper;


    @Override
    public String uploadImage(MultipartFile file) {
        try {
            // 1. 校验文章图片合法性
            validateArticleImageFile(file);

            // 2. 处理文章图片（仅压缩不裁剪，保持宽高比）
            InputStream processedStream = processArticleImage(file);

            // 3. 生成唯一文件名：article-images/用户ID/日期/UUID.后缀
            Integer userId = SecurityContextUtil.getUser().getId();
            String fileName = generateArticleImageFileName(file, userId);

            // 4. 确保MinIO桶存在
            ensureBucketExists();

            // 5. 上传文件到MinIO
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(fileName)
                            .stream(processedStream, processedStream.available(), -1)
                            .contentType(file.getContentType())
                            .build()
            );

            // 6. 生成并返回访问URL
            return generateImageUrl(fileName);

        } catch (Exception e) {
            log.error("文章图片上传失败", e);
            throw new BusinessException("文章图片上传失败：" + e.getMessage());
        }
    }


    @Override
    public String uploadAvatar(MultipartFile file) {
        try {
            // 1. 校验文件合法性
            validateImageFile(file);

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
                    .set(User::getAvatarUrl, fileName);     // 更新
            userMapper.update(null, lambdaUpdateWrapper);

            // 7. 生成并返回头像访问URL
            return generateImageUrl(fileName);

        } catch (Exception e) {
            log.error("头像上传失败", e);
            throw new BusinessException("头像上传失败：" + e.getMessage());
        }
    }

    /**
     * 校验头像文件（大小、格式、合法性）
     */
    private void validateImageFile(MultipartFile file) throws IOException {
        // 校验文件是否为空
        if (file.isEmpty()) {
            throw new RuntimeException("文件为空");
        }

        // 校验文件大小
        if (file.getSize() > MAX_AVATAR_SIZE) {
            throw new RuntimeException("图片大小不能超过5MB");
        }

        // 校验文件类型（MIME类型）
        validateImageFileType(file);

        // 校验是否为真实图片（防止伪装文件）
        validateRealImage(file);
    }

    /**
     * 校验文章图片文件（更大的尺寸限制）
     */
    private void validateArticleImageFile(MultipartFile file) throws IOException {
        // 校验文件是否为空
        if (file.isEmpty()) {
            throw new RuntimeException("文件为空");
        }

        // 校验文件大小（文章图片允许更大）
        if (file.getSize() > MAX_ARTICLE_IMAGE_SIZE) {
            throw new RuntimeException("图片大小不能超过10MB");
        }

        // 校验文件类型（MIME类型）
        validateImageFileType(file);

        // 校验是否为真实图片（防止伪装文件）
        validateRealImage(file);
    }

    /**
     * 通用校验图片文件类型
     */
    private void validateImageFileType(MultipartFile file) {
        String contentType = file.getContentType();
        boolean isAllowed = false;
        for (String type : ALLOWED_IMAGE_TYPES) {
            if (type.equals(contentType)) {
                isAllowed = true;
                break;
            }
        }
        if (!isAllowed) {
            throw new RuntimeException("仅支持JPG、PNG、GIF格式的图片");
        }
    }

    /**
     * 通用校验是否为真实图片
     */
    private void validateRealImage(MultipartFile file) throws IOException {
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
                .keepAspectRatio(false)  // 强制尺寸
                .outputQuality(0.8f)  // 压缩质量
                .asBufferedImage();

        // 转换为InputStream返回
        return bufferedImageToInputStream(processedImage, "jpg");
    }

    /**
     * 处理文章图片（仅压缩不裁剪，保持宽高比）
     */
    private InputStream processArticleImage(MultipartFile file) throws IOException {
        // 1. 前置校验：确保文件和输入流有效
        if (file.isEmpty() || file.getInputStream() == null) {
            throw new IllegalArgumentException("上传的图片文件为空或输入流无效");
        }

        // 2. 读取原始图片并校验有效性
        BufferedImage originalImage = ImageIO.read(file.getInputStream());
        if (originalImage == null) {
            throw new IllegalArgumentException("无法解析图片文件，请确认是有效图片格式（jpg/png/webp等）");
        }

        int originalWidth = originalImage.getWidth();
        int originalHeight = originalImage.getHeight();

        // 3. 修复核心问题：所有分支都必须设置尺寸参数（width/size）
        BufferedImage processedImage;
        if (originalWidth > MAX_ARTICLE_IMAGE_WIDTH) {
            // 宽度超限：按最大宽度等比缩放
            processedImage = Thumbnails.of(originalImage)
                    .width(MAX_ARTICLE_IMAGE_WIDTH)  // 指定宽度，高度自动按比例
                    .keepAspectRatio(true)
                    .outputQuality(ARTICLE_IMAGE_QUALITY)
                    .asBufferedImage();
        } else {
            // 宽度未超限：复用原图尺寸（关键修复），仅调整质量
            processedImage = Thumbnails.of(originalImage)
                    .size(originalWidth, originalHeight) // 显式设置原图尺寸，解决size未设置问题
                    .keepAspectRatio(true)
                    .outputQuality(ARTICLE_IMAGE_QUALITY)
                    .asBufferedImage();
        }

        // 4. 获取原文件格式（保持原格式，兼容不同图片类型）
        String suffix = getFileSuffix(file.getOriginalFilename());
        String format = suffix.startsWith(".") ? suffix.substring(1) : "jpg";
        // 兼容webp等特殊格式（ImageIO默认可能不支持，需额外依赖）
        if ("webp".equalsIgnoreCase(format)) {
            format = "png"; // 兜底：若不支持webp，转为png
        }

        // 5. 转换为InputStream返回
        return bufferedImageToInputStream(processedImage, format);
    }

    /**
     * BufferedImage转InputStream
     */
    private InputStream bufferedImageToInputStream(BufferedImage image, String format) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, format, baos);
        return new ByteArrayInputStream(baos.toByteArray());
    }

    /**
     * 生成头像文件名（格式：avatars/用户ID/20241010/UUID.jpg）
     */
    private String generateAvatarFileName(MultipartFile file, Integer userId) {
        String suffix = getFileSuffix(file.getOriginalFilename());
        String dateDir = new SimpleDateFormat("yyyyMMdd").format(new Date());
        String uuid = UUID.randomUUID().toString().replaceAll("-", "");
        return AVATAR_DIR + userId + "/" + dateDir + "/" + uuid + suffix;
    }

    /**
     * 生成文章图片文件名（格式：article-images/用户ID/20241010/UUID.后缀）
     */
    private String generateArticleImageFileName(MultipartFile file, Integer userId) {
        String suffix = getFileSuffix(file.getOriginalFilename());
        String dateDir = new SimpleDateFormat("yyyyMMdd").format(new Date());
        String uuid = UUID.randomUUID().toString().replaceAll("-", "");
        return ARTICLE_IMAGE_DIR + userId + "/" + dateDir + "/" + uuid + suffix;
    }

    /**
     * 获取文件后缀（默认jpg）
     */
    private String getFileSuffix(String originalFilename) {
        if (originalFilename == null || !originalFilename.contains(".")) {
            return ".jpg";
        }
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
        // 统一后缀为小写
        return suffix.toLowerCase();
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
     * 通用生成图片访问URL
     */

    private String generateImageUrl(String fileName) {
        // 参数校验
        if (fileName == null || fileName.trim().isEmpty()) {
            throw new IllegalArgumentException("图片文件名不能为空");
        }
        // 路径规范化（避免重复 "/"）
        String normalizedFileName = fileName.startsWith("/") ? fileName.substring(1) : fileName;
        // 拼接完整URL（如果MinIO配置了自定义域名，这里可以替换为域名）
        return minioEndpoint + "/" + bucketName + "/" + normalizedFileName;
    }
}