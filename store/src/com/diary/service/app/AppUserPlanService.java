package com.diary.service.app;

import com.diary.common.StoreException;
import com.diary.entity.app.AppUserPlan;
import com.diary.providers.store.app.AppUserPlanStore;
import com.google.inject.Singleton;
import org.guiceside.commons.Page;
import org.guiceside.persistence.TransactionType;
import org.guiceside.persistence.Transactional;
import org.guiceside.persistence.hibernate.dao.enums.Persistent;
import org.guiceside.persistence.hibernate.dao.hquery.HQuery;
import org.guiceside.persistence.hibernate.dao.hquery.Selector;

import java.util.List;

/**
 * Created by Lara Croft on 2016/12/21.
 */
@Singleton
public class AppUserPlanService extends HQuery implements AppUserPlanStore {

    @Transactional(type = TransactionType.READ_ONLY)
    public AppUserPlan getById(Long id, Selector... selectors) throws StoreException {
        return $(id, selectors).get(AppUserPlan.class);
    }

    @Override
    @Transactional(type = TransactionType.READ_ONLY)
    public Page<AppUserPlan> getPageList(int start, int limit, List<Selector> selectorList) throws StoreException {
        return $(selectorList).page(AppUserPlan.class, start, limit);
    }

    @Override
    @Transactional(type = TransactionType.READ_ONLY)
    public List<AppUserPlan> getByUserId(Long userId) throws StoreException {
        return $($alias("userId", "userId"), $eq("userId.id", userId)).list(AppUserPlan.class);
    }

    @Override
    @Transactional(type = TransactionType.READ_WRITE)
    public void delete(AppUserPlan appUserPlan) throws StoreException {
        $(appUserPlan).delete();
    }

    @Override
    @Transactional(type = TransactionType.READ_WRITE)
    public void save(AppUserPlan appUserPlan, Persistent persistent) throws StoreException {
        $(appUserPlan).save(persistent);
    }
}