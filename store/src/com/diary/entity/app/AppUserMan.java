package com.diary.entity.app;

import org.guiceside.persistence.entity.IdEntity;
import org.guiceside.persistence.entity.Tracker;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 12-8-15
 * Time: 下午4:26
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Table(name = "APP_USER_MAN")
public class AppUserMan extends IdEntity implements Tracker {

    private static final long serialVersionUID = 1L;

    private Long id;

    private AppUser userId;

    private Long health;

    private Long money;


    private Long ability;


    private Long experience;

    private Long happly;


    private Long positive;

    private Long connections;

    private Integer days;

    private Integer hours;

    private Date created;

    private String createdBy;

    private Date updated;

    private String updatedBy;

    private String useYn;

    @Id
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID")
    public AppUser getUserId() {
        return userId;
    }

    public void setUserId(AppUser userId) {
        this.userId = userId;
    }

    @Column(name = "HEALTH")
    public Long getHealth() {
        return health;
    }

    public void setHealth(Long health) {
        this.health = health;
    }

    @Column(name = "MONEY")
    public Long getMoney() {
        return money;
    }

    public void setMoney(Long money) {
        this.money = money;
    }

    @Column(name = "ABILITY")
    public Long getAbility() {
        return ability;
    }

    public void setAbility(Long ability) {
        this.ability = ability;
    }

    @Column(name = "EXPERIENCE")
    public Long getExperience() {
        return experience;
    }

    public void setExperience(Long experience) {
        this.experience = experience;
    }

    @Column(name = "HAPPLY")
    public Long getHapply() {
        return happly;
    }

    public void setHapply(Long happly) {
        this.happly = happly;
    }

    @Column(name = "POSITIVE")
    public Long getPositive() {
        return positive;
    }

    public void setPositive(Long positive) {
        this.positive = positive;
    }

    @Column(name = "CONNECTIONS")
    public Long getConnections() {
        return connections;
    }

    public void setConnections(Long connections) {
        this.connections = connections;
    }

    @Column(name = "DAYS")
    public Integer getDays() {
        return days;
    }

    public void setDays(Integer days) {
        this.days = days;
    }

    @Column(name = "HOURS")
    public Integer getHours() {
        return hours;
    }

    public void setHours(Integer hours) {
        this.hours = hours;
    }

    @Column(name = "CREATED", updatable = false)
    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    @Column(name = "CREATED_BY")
    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    @Column(name = "UPDATED")
    public Date getUpdated() {
        return updated;
    }

    public void setUpdated(Date updated) {
        this.updated = updated;
    }

    @Column(name = "UPDATED_BY")
    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    @Column(name = "USE_YN")
    public String getUseYn() {
        return useYn;
    }

    public void setUseYn(String useYn) {
        this.useYn = useYn;
    }
}