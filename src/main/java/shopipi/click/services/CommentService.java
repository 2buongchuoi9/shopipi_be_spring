package shopipi.click.services;

import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import shopipi.click.entity.Comment;
import shopipi.click.entity.productSchema.Product;
import shopipi.click.exceptions.NotFoundError;
import shopipi.click.models.request.CommentReq;
import shopipi.click.repositories.CommentRepo;
import shopipi.click.repositories.ProductRepo;
import shopipi.click.repositories.UserRepo;

@Service
@RequiredArgsConstructor
public class CommentService {
  final private CommentRepo commentRepo;
  final private UserRepo userRepo;
  final private ProductRepo productRepo;
  final private MongoTemplate mongoTemplate;

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
    return commentRepo.save(Comment.builder()
        .productId(commentReq.getProductId())
        .shopId(foundProduct.getShop().getId())
        .content(commentReq.getContent())
        .left(rightValue)
        .right(rightValue + 1)
        .parentId(commentReq.getParentId())
        .build());
  }

  // public List<Comment> getComments(CommentParamsReq params, Pageable pageable)
  // {
  // String newsId = params.getNewsId();
  // String parentId = params.getParentId();
  // String userId = params.getUserId();

  // Query query = new Query();

  // // newsId
  // if (newsId != null) {
  // query.addCriteria(Criteria.where("newsId").is(newsId));
  // }

  // // parentId
  // if (parentId != null) {
  // query.addCriteria(Criteria.where("parentId").is(parentId));
  // }

  // // userId
  // if (userId != null) {
  // query.addCriteria(Criteria.where("user.id").is(userId));
  // }

  // query.with(pageable.getSort());

  // // if (parentId == null) {
  // // return commentRepo.findByNewsIdOrderByLeftDesc(newsId);
  // // } else {
  // // Comment parent = commentRepo.findById(parentId)
  // // .orElseThrow(() -> new NotFoundError("parentId", parentId));
  // // return commentRepo.findByNewsIdAndParentIdOrderByLeftDesc(newsId,
  // // parent.getId());
  // // }

  // return mongoTemplate.find(query, Comment.class);

  // }

  // public Comment updateComment(User user, String commentId, CommentReq
  // commentReq) {
  // Comment comment = commentRepo.findById(commentId)
  // .orElseThrow(() -> new NotFoundError("commentId", commentId));
  // if (!comment.getUser().getId().equals(user.getId())) {
  // throw new NotFoundError("commentId", commentId);
  // }
  // comment.setContent(commentReq.getContent());
  // return commentRepo.save(comment);
  // }

  // public boolean deleteComment(User user, String commentId) {
  // try {
  // Comment comment = commentRepo.findById(commentId)
  // .orElseThrow(() -> new NotFoundError("commentId", commentId));
  // if (!comment.getUser().getId().equals(user.getId())) {
  // throw new NotFoundError("commentId", commentId);
  // }
  // // xóa comment và các comment con của comment đó
  // mongoTemplate.remove(Query.query(Criteria.where("left").gte(comment.getLeft()).lte(comment.getRight())),
  // Comment.class);
  // // update right của các comment có right > right của comment bị xóa giảm 2
  // mongoTemplate.updateMulti(
  // Query.query(Criteria.where("right").gt(comment.getRight())),
  // new Update().inc("right", -2),
  // Comment.class);
  // // update left của các comment có left > right của comment bị xóa giảm 2
  // mongoTemplate.updateMulti(
  // Query.query(Criteria.where("left").gt(comment.getRight())),
  // new Update().inc("left", -2),
  // Comment.class);

  // // update totalComment in news

  // long remainingComments = mongoTemplate.count(
  // Query.query(Criteria.where("newsId").is(comment.getNewsId())),
  // Comment.class);

  // mongoTemplate.updateFirst(
  // Query.query(Criteria.where("id").is(comment.getNewsId())),
  // new Update().set("totalComment", remainingComments),
  // News.class);

  // return true;
  // } catch (NotFoundError e) {
  // return false;
  // }
  // }

  // public Comment likeComment(User user, String commentId) {
  // Comment comment = commentRepo.findById(commentId)
  // .orElseThrow(() -> new NotFoundError("commentId", commentId));
  // if (comment.getLikes() == null)
  // comment.setLikes(new ArrayList<>());
  // if (comment.getLikes().contains(user.getId())) { // đã like
  // comment.getLikes().remove(user.getId());
  // } else {
  // comment.getLikes().add(user.getId());
  // }

  // return commentRepo.save(comment);

  // }

}
