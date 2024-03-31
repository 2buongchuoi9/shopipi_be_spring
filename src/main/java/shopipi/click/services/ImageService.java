package shopipi.click.services;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import shopipi.click.entity.Image;
import shopipi.click.exceptions.NotFoundError;
import shopipi.click.repositories.ImageRepo;
import shopipi.click.repositories.repositoryUtil.PageCustom;

@Service
@RequiredArgsConstructor
@SuppressWarnings({ "null", "rawtypes" })
public class ImageService {
  private final ImageRepo imageRepo;
  private final MongoTemplate mongoTemplate;

  private final CloudinaryService cloudinaryService;

  public Image addImageAndFile(MultipartFile multipartFile) {
    Image image = new Image();
    Map uploadResult = cloudinaryService.uploadV2(multipartFile);
    if (uploadResult != null) {
      image.setPublicId(uploadResult.get("public_id").toString());
      image.setUrl(uploadResult.get("url").toString());
      imageRepo.save(image);
    }
    return image;
  }

  public PageCustom<Image> findAll(Pageable pageable) {
    Query query = new Query();
    long total = imageRepo.count();
    query.with(pageable);
    List<Image> list = mongoTemplate.find(query, Image.class);
    return new PageCustom<>(PageableExecutionUtils.getPage(list, pageable, () -> total));
  }

  public boolean delete(String id) {
    try {
      Image image = imageRepo.findById(id).orElseThrow(() -> new NotFoundError("not found imageId=" + id));
      if (image.getPublicId() != null)
        cloudinaryService.delete(image.getPublicId());
      imageRepo.delete(image);
      return true;
    } catch (Exception e) {
      return false;
    }
  }
}
