package com.cmd.hms.gateway.model;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/** Used to represent the type of a Contact
 * 
 */
@Entity
@Table(name="contact_type")
public class ContactType{
  /** Corresponds to contact_type_id, auto-incremented primary-key column
   * 
   */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name="contact_type_id")
  private Long ContactTypeId;

  /** Correponds to title, is mandatory and must be less than 100 characters
   * 
   */
  @Column(name="title", length = 100, nullable = false)
  @NotBlank(message="Title is mandatory")
  @Size(max = 100)
  private String Title;

  // Joins
  /** List of Contacts with the corresponding Type
   * 
   */
  @OneToMany(mappedBy = "Type") // mappedBy refers to field name in child table pointing to this table
  private List<Contact> Contacts;

  // Getters and Setters

  /** Used to set the ContactTypeId in POST/PUT/PATCH requests
   * 
   * @param ContactTypeId - set automatically, not by the user
   */
  public void setContactTypeId(Long ContactTypeId){
    this.ContactTypeId = ContactTypeId;
  }

  /** 
   * 
   * @return
   */
  public Long getContactTypeId() {
    return ContactTypeId;
  }

  public void setTitle(String Title){
    this.Title = Title;
  }

  public String getTitle() {
    return Title;
  }

}