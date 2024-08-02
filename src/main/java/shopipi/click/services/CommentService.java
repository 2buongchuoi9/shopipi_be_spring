package shopipi.click.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import shopipi.click.applicationEvent.LikeEvent;
import shopipi.click.entity.Comment;
import shopipi.click.entity.Notification;
import shopipi.click.entity.User;
import shopipi.click.entity.productSchema.Product;
import shopipi.click.exceptions.NotFoundError;
import shopipi.click.exceptions.NoAuthorizeError;
import shopipi.click.models.paramsRequest.CommentParamsReq;
import shopipi.click.models.request.CommentReq;
import shopipi.click.repositories.CommentRepo;
import shopipi.click.repositories.ProductRepo;
import shopipi.click.repositories.UserRepo;
import shopipi.click.repositories.repositoryUtil.PageCustom;
import shopipi.click.services.productService.IUpdateProduct;
import shopipi.click.services.productService.ProductService;
import shopipi.click.utils._enum.NotificationType;
import shopipi.click.utils._enum.UserRoleEnum;

@Service
@RequiredArgsConstructor
public class CommentService {
  final private CommentRepo commentRepo;
  final private ProductRepo productRepo;
  final private MongoTemplate mongoTemplate;
  final private IUpdateProduct iUpdateProduct;
  final private ApplicationEventPublisher eventPublisher;

  public Comment add(CommentReq commentReq) {
    Product foundProduct = productRepo.findById(commentReq.getProductId())
        .orElseThrow(() -> new NotFoundError("productId", commentReq.getProductId()));

    int rightValue;
    if (commentReq.getParentId() != null) {
      Comment parent = commentRepo.findById(commentReq.getParentId())
          .orElseThrow(() -> new NotFoundError("parentId", commentReq.getParentId()));
      rightValue = parent.getRight();
      // update right của các comment có right >= rightValue lên 2 đơn vị
      mongoTemplate.updateMulti(
          Query.query(Criteria.where("right").gte(rightValue)),
          new Update().inc("right", 2),
          Comment.class);
      // update left của các comment có left > rightValue lên 2 đơn vị
      mongoTemplate.updateMulti(
          Query.query(Criteria.where("left").gt(rightValue)),
          new Update().inc("left", 2),
          Comment.class);

    } else {
      // tìm comment cuối cùng của bài viết
      Comment lastComment = mongoTemplate.findOne(Query.query(Criteria.where("newsId").is(commentReq.getProductId()))
          .with(Sort.by(Sort.Direction.DESC, "right")).limit(1), Comment.class);
      rightValue = lastComment == null ? 1 : lastComment.getRight() + 2;
    }

    // save comment
    Comment nComment = commentRepo.save(Comment.builder()
        .productId(commentReq.getProductId())
        .shopId(foundProduct.getShop().getId())
        .content(commentReq.getContent())
        .left(rightValue)
        .right(rightValue + 1)
        .parentId(commentReq.getParentId())
        .build());

    // update totalComment in product
    iUpdateProduct.totalComment(foundProduct);
    return nComment;
  }

  public PageCustom<Comment> findComments(CommentParamsReq params, Pageable pageable) {
    String productId = params.getProductId();
    String shopId = params.getShopId();
    String parentId = params.getParentId();
    String userId = params.getUserId();

    Query query = new Query();

    // newsId
    if (productId != null) {
      query.addCriteria(Criteria.where("productId").is(productId));
    }

    // shopId
    if (shopId != null) {
      query.addCriteria(Criteria.where("shopId").is(productId));
    }

    // parentId
    if (parentId != null) {
      query.addCriteria(Criteria.where("parentId").is(parentId));
    }

    // userId
    if (userId != null) {
      query.addCriteria(Criteria.where("user.id").is(userId));
    }

    long total = mongoTemplate.count(query, Comment.class);
    query.with(pageable);
    List<Comment> list = mongoTemplate.find(query, Comment.class);

    return new PageCustom<>(PageableExecutionUtils.getPage(list, pageable, () -> total));
  }

  public Comment updateComment(User user, String commentId, CommentReq commentReq) {
    Comment foundComment = commentRepo.findById(commentId)
        .orElseThrow(() -> new NotFoundError("commentId", commentId));

    if (!foundComment.getUser().getId().equals(user.getId()))
      throw new NoAuthorizeError("You are not authorized to update this comment.");

    foundComment.setContent(commentReq.getContent());
    return commentRepo.save(foundComment);
  }

  public boolean deleteComment(User user, String commentId) {
    try {
      Comment comment = commentRepo.findById(commentId)
          .orElseThrow(() -> new NotFoundError("commentId", commentId));

      // kiểm tra xem user có phải là người tạo comment không
      // hoặc là admin hoặc là chủ shop
      if (!user.getRoles().contains(UserRoleEnum.ADMIN)
          && !user.getId().equals(comment.getShopId())
          && !comment.getUser().getId().equals(user.getId()))
        throw new NoAuthorizeError("You are not authorized to delete this comment.");

      // xóa comment và các comment con của comment đó
      mongoTemplate.remove(Query.query(Criteria.where("left").gte(comment.getLeft()).lte(comment.getRight())),
          Comment.class);
      // update right của các comment có right > right của comment bị xóa giảm 2
      mongoTemplate.updateMulti(
          Query.query(Criteria.where("right").gt(comment.getRight())),
          new Update().inc("right", -2),
          Comment.class);
      // update left của các comment có left > right của comment bị xóa giảm 2
      mongoTemplate.updateMulti(
          Query.query(Criteria.where("left").gt(comment.getRight())),
          new Update().inc("left", -2),
          Comment.class);

      return true;
    } catch (NotFoundError e) {
      return false;
    }
  }

  public Comment likeComment(User user, String commentId) {
    Comment comment = commentRepo.findById(commentId)
        .orElseThrow(() -> new NotFoundError("commentId", commentId));
    if (comment.getLikes() == null)
      comment.setLikes(new ArrayList<>());
    if (comment.getLikes().contains(user.getId())) { // đã like
      comment.getLikes().remove(user.getId());
    } else {
      comment.getLikes().add(user.getId());

      eventPublisher.publishEvent(new LikeEvent(this, Notification.builder()
          .userFrom(user)
          .userTo(comment.getUser().getId())
          .description(comment.getProductId())
          .content(user.getName() + " đã thích bình luận của bạn")
          .notificationType(NotificationType.NEW_LIKE)
          .build()));
    }

    return commentRepo.save(comment);
  }

}
