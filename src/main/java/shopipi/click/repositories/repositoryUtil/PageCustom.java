package shopipi.click.repositories.repositoryUtil;

import java.io.Serializable;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import lombok.Getter;

@Getter
public class PageCustom<T> implements Serializable {
  private List<T> content;
  private int totalPage;
  private int currentPage;
  private int pageSize;
  private long totalElement;
  private boolean last;

  public PageCustom(PageImpl<T> pageImpl) {
    this.content = pageImpl.getContent();
    this.currentPage = pageImpl.getPageable().getPageNumber();
    this.totalPage = pageImpl.getTotalPages();
    this.pageSize = pageImpl.getPageable().getPageSize();
    this.totalElement = pageImpl.getTotalElements();
    this.last = pageImpl.isLast();
  }

  public PageCustom(Page<T> page) {
    this.content = page.getContent();
    this.currentPage = page.getPageable().getPageNumber();
    this.totalPage = page.getTotalPages();
    this.pageSize = page.getPageable().getPageSize();
    this.totalElement = page.getTotalElements();
    this.last = page.isLast();
  }

}
