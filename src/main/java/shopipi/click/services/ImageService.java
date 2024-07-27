package shopipi.click.services;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import shopipi.click.entity.Image;
import shopipi.click.exceptions.NotFoundError;
import shopipi.click.repositories.ImageRepo;
import shopipi.click.repositories.repositoryUtil.PageCustom;
import shopipi.click.utils._enum.FileTypeEnum;

@Service
@RequiredArgsConstructor
@SuppressWarnings({ "null", "rawtypes" })
public class ImageService {
  private final ImageRepo fileRepo;
  private final MongoTemplate mongoTemplate;

  private final CloudinaryService cloudinaryService;

  public Image addImageAndFile(MultipartFile multipartFile) {
    Map uploadResult = cloudinaryService.uploadImage(multipartFile);

    if (uploadResult != null) {
      Image file = Image.builder()
          .publicId(uploadResult.get("public_id").toString())
          .url(uploadResult.get("url").toString())
          // .extension(uploadResult.get("format").toString())
          .build();
      setAttributes(file, multipartFile, FileTypeEnum.IMAGE.name());
      return fileRepo.save(file);
    } else
      throw new NotFoundError("upload image failed");
  }

  public Image addVideoFile(MultipartFile multipartFile) {
    Map uploadResult = cloudinaryService.uploadVideo(multipartFile);
    System.out.println("uploadResult: " + uploadResult);
    if (uploadResult != null) {
      Image video = Image.builder()
          .publicId(uploadResult.get("public_id").toString())
          .url(uploadResult.get("url").toString())
          // .extension(uploadResult.get("format").toString())
          .build();
      setAttributes(video, multipartFile, FileTypeEnum.VIDEO.name());

      return fileRepo.save(video);
    } else
      throw new NotFoundError("upload video failed");
  }

  public PageCustom<Image> findAll(Pageable pageable, String type) {
    Query query = new Query();
    if (!type.equals(FileTypeEnum.ALL.name()))
      query.addCriteria(Criteria.where("type").is(type));
    long total = fileRepo.count();
    query.with(pageable);
    List<Image> list = mongoTemplate.find(query, Image.class);

    return new PageCustom<>(PageableExecutionUtils.getPage(list, pageable, () -> total));
  }

  public boolean delete(String id) {
    try {
      Image image = fileRepo.findById(id).orElseThrow(() -> new NotFoundError("not found imageId=" + id));
      if (image.getPublicId() != null)
        cloudinaryService.delete(image.getPublicId());
      fileRepo.delete(image);
      return true;
    } catch (Exception e) {
      return false;
    }
  }

  private void setAttributes(Image file, MultipartFile multipartFile, String type) {
    String originalFilename = multipartFile.getOriginalFilename();
    long size = multipartFile.getSize();
    String mimeType = multipartFile.getContentType();

    if (file.getExtension() == null || file.getExtension().isEmpty()) {
      String extension = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
      file.setExtension(extension);
    }

    file.setDescription(originalFilename);
    file.setType(type);
    file.setSize(size);
    file.setMimeType(mimeType);
  }

  public Boolean deleteByUrl(String url) {
    Image file = fileRepo.findByUrl(url).orElseThrow(() -> new NotFoundError("not found url=" + url));

    if (file.getPublicId() != null)
      cloudinaryService.delete(file.getPublicId());

    fileRepo.delete(file);
    return true;
  }

}
