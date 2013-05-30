/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package auctionsystem.entity;

import java.io.Serializable;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;

/**
 *
 * @author Administrator
 */
@Entity
@Inheritance(strategy=InheritanceType.JOINED) 
@DiscriminatorColumn(name="DISC", discriminatorType=DiscriminatorType.STRING,length=20)
@DiscriminatorValue("Item")
public class Item implements Serializable {
  private static final long serialVersionUID = 1L;
  private Integer id;
  private String description;
  private String image;

  public Item() {
  }
  
  public Item (String description, String image) {
    setDescription(description);
    setImage(image);
  }

  public void setId(Integer id) {
    this.id = id;
  }

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  public Integer getId() {
    return id;
  }

  @Override
  public int hashCode() {
    int hash = 0;
    hash += (id != null ? id.hashCode() : 0);
    return hash;
  }

  @Override
  public boolean equals(Object object) {
    // TODO: Warning - this method won't work in the case the id fields are not set
    if (!(object instanceof Item)) {
      return false;
    }
    Item other = (Item) object;
    if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
      return false;
    }
    return true;
  }

  @Override
  public String toString() {
    return "auctionsystem.entity.Item[id=" + id + "]";
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getImage() {
    return image;
  }

  public void setImage(String image) {
    this.image = image;
  }

}
