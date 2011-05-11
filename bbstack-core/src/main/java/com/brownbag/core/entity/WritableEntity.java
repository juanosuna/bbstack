/*
 * BROWN BAG CONFIDENTIAL
 *
 * Brown Bag Consulting LLC
 * Copyright (c) 2011. All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of Brown Bag Consulting LLC and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to Brown Bag Consulting LLC
 * and its suppliers and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Brown Bag Consulting LLC.
 */

package com.brownbag.core.entity;

import org.hibernate.annotations.NaturalId;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.persistence.*;
import java.util.Date;
import java.util.UUID;

@MappedSuperclass
@EntityListeners({WritableEntity.WritableEntityListener.class})
public abstract class WritableEntity {

    private static final long serialVersionUID = 1L;

    public static final String SCHEMA = "SAMPLE";
    public static final String SYSTEM_USER = "system";

    public static String getCurrentUser() {
        if (SecurityContextHolder.getContext() != null && SecurityContextHolder.getContext().getAuthentication() != null
                && SecurityContextHolder.getContext().getAuthentication().getPrincipal() != null) {
            org.springframework.security.core.userdetails.User user = (org.springframework.security.core.userdetails.User)
                    SecurityContextHolder.getContext().getAuthentication().getPrincipal();
                    return user.getUsername();
        } else {
            return SYSTEM_USER;
        }
    }

    @Id
    @GeneratedValue
    private Long id;

    @NaturalId
    @Column(unique = true, nullable = false, updatable = false)
    private String uuid;

    @Version
    private Integer version;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    private Date lastModified;

    @Column(nullable = false)
    private String lastModifiedBy;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    private Date created;

    @Column(nullable = false)
    private String createdBy;

    protected WritableEntity() {
        uuid = UUID.randomUUID().toString();
    }

    public Long getId() {
        return id;
    }

    public String getUuid() {
        return uuid;
    }

    public Integer getVersion() {
        return version;
    }

    public Date getLastModified() {
        return lastModified;
    }

    public void setLastModified(Date lastModified) {
        this.lastModified = lastModified;
    }

    public String getLastModifiedBy() {
        return lastModifiedBy;
    }

    public void setLastModifiedBy(String lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof WritableEntity)) return false;

        WritableEntity that = (WritableEntity) o;

        if (!getUuid().equals(that.getUuid())) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return getUuid().hashCode();
    }

    @Override
    public String toString() {
        return "WritableEntity{" +
                "uuid=" + getUuid() +
                '}';
    }

    public void updateLastModified() {
        new WritableEntityListener().onPreUpdate(this);
    }

    public static class WritableEntityListener {
        @PrePersist
        public void onPrePersist(WritableEntity writableEntity) {
            writableEntity.created = new Date();
            writableEntity.lastModified = writableEntity.created;

            writableEntity.createdBy = getCurrentUser();
            writableEntity.lastModifiedBy = writableEntity.createdBy;
        }

        @PreUpdate
        public void onPreUpdate(WritableEntity writableEntity) {
            writableEntity.lastModified = new Date();
            writableEntity.lastModifiedBy = getCurrentUser();
        }
    }
}
