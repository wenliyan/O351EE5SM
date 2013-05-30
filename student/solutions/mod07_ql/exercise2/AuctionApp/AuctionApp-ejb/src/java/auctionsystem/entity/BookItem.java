/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package auctionsystem.entity;

import java.io.Serializable;
import javax.persistence.Entity;

/**
 *
 * @author Administrator
 */
@Entity
public class BookItem extends Item implements Serializable {
  private String title;
  private String author;

  /** Creates a new instance of BookItem */
  public BookItem() {
  }

  public BookItem(String description, String image, String title, String author) {
    super(description, image);
    setTitle(title);
    setAuthor(author);
  }

  @Override
  public String toString() {
    return "auctionsystem.entity.BookItem[id=" + getId() + "]";
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getAuthor() {
    return author;
  }

  public void setAuthor(String author) {
    this.author = author;
  }

}
