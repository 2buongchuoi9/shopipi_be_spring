package shopipi.click.repositories.repositoryUtil;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import lombok.Getter;

@Getter
public class PageCustom<T> {
  private List<T> content;
  private int totalPage;
  private int currentPage;
  private int pageSize;
  private long totalElement;

  public PageCustom(PageImpl<T> pageImpl) {
    this.content = pageImpl.getContent();
    this.currentPage = pageImpl.getPageable().getPageNumber();
    this.totalPage = pageImpl.getTotalPages();
    this.pageSize = pageImpl.getPageable().getPageSize();
    this.totalElement = pageImpl.getTotalElements();
  }

  public PageCustom(Page<T> page) {
    this.content = page.getContent();
    this.currentPage = page.getPageable().getPageNumber();
    this.totalPage = page.getTotalPages();
    this.pageSize = page.getPageable().getPageSize();
    this.totalElement = page.getTotalElements();
  }

}
