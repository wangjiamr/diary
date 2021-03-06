package com.diary.bizImpl.app;

import com.diary.common.BizException;
import com.diary.common.StoreException;
import com.diary.entity.app.*;
import com.diary.entity.res.ResCar;
import com.diary.entity.res.ResCouple;
import com.diary.entity.res.ResCoupleRequire;
import com.diary.entity.utils.DrdsIDUtils;
import com.diary.entity.utils.DrdsTable;
import com.diary.entity.utils.GameUtils;
import com.diary.providers.biz.app.UserCarBiz;
import com.diary.providers.biz.app.UserCoupleBiz;
import com.diary.providers.store.app.*;
import com.diary.providers.store.res.*;
import com.google.inject.Inject;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.guiceside.commons.lang.BeanUtils;
import org.guiceside.commons.lang.StringUtils;
import org.guiceside.persistence.hibernate.dao.enums.Persistent;
import org.guiceside.support.hsf.BaseBiz;
import org.guiceside.support.hsf.HSFServiceFactory;

import java.util.List;


/**
 * @author zhenjiaWang
 * @version 1.0 2012-05
 * @since JDK1.5
 */

public class UserCoupleBizImp extends BaseBiz implements UserCoupleBiz {

    @Inject
    private HSFServiceFactory hsfServiceFactory;

    @Override
    public String relationship(Long userId, Long coupleId) throws BizException {
        JSONObject resultObj = new JSONObject();
        resultObj.put("result", -1);
        try {
            AppUserStore appUserStore = hsfServiceFactory.consumer(AppUserStore.class);
            AppUserManStore appUserManStore = hsfServiceFactory.consumer(AppUserManStore.class);
            AppUserLadyStore appUserLadyStore = hsfServiceFactory.consumer(AppUserLadyStore.class);
            ResCoupleRequireStore resCoupleRequireStore = hsfServiceFactory.consumer(ResCoupleRequireStore.class);
            AppUserCoupleStore appUserCoupleStore = hsfServiceFactory.consumer(AppUserCoupleStore.class);
            AppUserLimitStore appUserLimitStore = hsfServiceFactory.consumer(AppUserLimitStore.class);
            ResCoupleStore resCoupleStore = hsfServiceFactory.consumer(ResCoupleStore.class);
            AppUserCarStore appUserCarStore = hsfServiceFactory.consumer(AppUserCarStore.class);
            AppUserHouseStore appUserHouseStore = hsfServiceFactory.consumer(AppUserHouseStore.class);
            AppUserManHistStore appUserManHistStore = hsfServiceFactory.consumer(AppUserManHistStore.class);
            AppUserLadyHistStore appUserLadyHistStore = hsfServiceFactory.consumer(AppUserLadyHistStore.class);
            ResCarStore resCarStore = hsfServiceFactory.consumer(ResCarStore.class);
            ResHouseStore resHouseStore = hsfServiceFactory.consumer(ResHouseStore.class);
            ResClothesStore resClothesStore = hsfServiceFactory.consumer(ResClothesStore.class);
            ResLuxuryStore resLuxuryStore = hsfServiceFactory.consumer(ResLuxuryStore.class);
            if (appUserManStore != null && appUserLadyStore != null && appUserStore != null
                    && resCoupleRequireStore != null && appUserCoupleStore != null && resCoupleStore != null && appUserLimitStore != null
                    && appUserCarStore != null && appUserHouseStore != null) {
                AppUser appUser = appUserStore.getById(userId);
                if (appUser != null) {
                    List<ResCoupleRequire> requireList = resCoupleRequireStore.getListByCoupleId(coupleId);
                    boolean pass = false;
                    Integer fireValue = 0;
                    Integer coupleLimit = 1;
                    Integer day = -1;
                    AppUserMan appUserMan = null;
                    AppUserLady appUserLady = null;
                    AppUserLimit appUserLimit = null;
                    JSONArray resultArray = new JSONArray();
                    if (appUser.getGender() == 1) {
                        appUserMan = appUserManStore.getByUserId(userId);
                        if (appUserMan != null) {
                            Integer userCarLevel = 0;
                            List<AppUserCar> appUserCarList = appUserCarStore.getByUserId(appUser.getId());
                            if (appUserCarList != null && !appUserCarList.isEmpty()) {
                                for (AppUserCar appUserCar : appUserCarList) {
                                    userCarLevel += appUserCar.getCarId().getLevel();
                                }
                            }
                            appUserMan.setCar(userCarLevel);
                            Integer userHouseLevel = 0;
                            List<AppUserHouse> appUserHouseList = appUserHouseStore.getByUserId(appUser.getId());
                            if (appUserHouseList != null && !appUserHouseList.isEmpty()) {
                                for (AppUserHouse appUserHouse : appUserHouseList) {
                                    userHouseLevel += appUserHouse.getHouseId().getLevel();
                                }
                            }
                            appUserMan.setHours(userHouseLevel);

                            day = appUserMan.getDays();
                            coupleLimit = appUserLimitStore.getCountByUserIdDayAction(userId, appUserMan.getDays(), "COUPLE");
                            if (requireList != null && !requireList.isEmpty()) {
                                pass = GameUtils.requirePass(requireList, appUserMan);
                                if (pass) {
                                    for (ResCoupleRequire coupleRequire : requireList) {
                                        String requireKey = coupleRequire.getAttrKey().toLowerCase();
                                        if (StringUtils.isNotBlank(requireKey)) {
                                            Integer userValue = BeanUtils.getValue(appUserMan, requireKey, Integer.class);
                                            if (userValue != null) {
                                                fireValue = userValue;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    } else if (appUser.getGender() == 2) {
                        appUserLady = appUserLadyStore.getByUserId(userId);
                        if (appUserLady != null) {
                            day = appUserLady.getDays();
                            coupleLimit = appUserLimitStore.getCountByUserIdDayAction(userId, appUserLady.getDays(), "COUPLE");
                            if (requireList != null && !requireList.isEmpty()) {
                                pass = GameUtils.requirePass(requireList, appUserLady);
                                if (pass) {
                                    for (ResCoupleRequire coupleRequire : requireList) {
                                        String requireKey = coupleRequire.getAttrKey().toLowerCase();
                                        if (StringUtils.isNotBlank(requireKey)) {
                                            Integer userValue = BeanUtils.getValue(appUserLady, requireKey, Integer.class);
                                            if (userValue != null) {
                                                fireValue = userValue;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    if (coupleLimit == 0) {
                        appUserLimit = new AppUserLimit();
                        appUserLimit.setId(DrdsIDUtils.getID(DrdsTable.APP));
                        appUserLimit.setUserId(appUser);
                        appUserLimit.setAction("COUPLE");
                        appUserLimit.setDay(day);
                        bind(appUserLimit, userId);
                        appUserLimit.setUseYn("Y");
                    }
                    AppUserCouple appUserCouple = appUserCoupleStore.getByUserId(userId);
                    if (appUserCouple != null) {
                        //自己有对象的情况下
                        if (appUser.getGender() == 1) {
                            AppUserMan oldMan = (AppUserMan) appUserMan.clone();
                            appUserMan.setMoney(appUserMan.getMoney() - 5000);
                            appUserMan.setPositive(appUserMan.getPositive() - 30);
                            JSONArray effectArray = GameUtils.diffEffectMan(oldMan, appUserMan);
                            appUserManStore.save(appUserMan, Persistent.UPDATE, appUserLimit);
                            GameUtils.addResultArray(resultArray,
                                    "啧啧啧！" + GameUtils.callName(appUser.getGender()) + "，到处沾花惹草，吃着碗里的看着锅里的可不好。", null);
                            GameUtils.addResultArray(resultArray,
                                    "最终", effectArray);
                        } else if (appUser.getGender() == 2) {
                            AppUserLady oldLady = (AppUserLady) appUserLady.clone();
                            appUserLady.setHappy(appUserLady.getHappy() - 30);
                            appUserLady.setPopularity(appUserLady.getPopularity() - 30);
                            JSONArray effectArray = GameUtils.diffEffectLady(oldLady, appUserLady);
                            appUserLadyStore.save(appUserLady, Persistent.UPDATE, appUserLimit);
                            GameUtils.addResultArray(resultArray,
                                    "啧啧啧！" + GameUtils.callName(appUser.getGender()) + "，红杏出墙会毁掉名声，不作死就不会死。", null);
                            GameUtils.addResultArray(resultArray,
                                    "最终", effectArray);
                        }
                        resultObj.put("result", 1);
                        resultObj.put("resultArray", resultArray);
                    } else {
                        //自己没对象
                        if (pass && coupleLimit == 0) {
                            //符合条件
                            ResCouple resCouple = resCoupleStore.getById(coupleId);
                            if (resCouple != null) {

                                appUserCouple = appUserCoupleStore.getByCoupleId(coupleId);
                                if (appUserCouple == null) {
                                    //如果对象没有人
                                    appUserCouple = new AppUserCouple();
                                    appUserCouple.setId(DrdsIDUtils.getID(DrdsTable.APP));
                                    appUserCouple.setUserId(appUser);
                                    appUserCouple.setCoupleId(resCouple);
                                    appUserCouple.setValue(fireValue);
                                    appUserCouple.setUseYn("Y");
                                    bind(appUserCouple, userId);
                                    if (appUser.getGender() == 1) {
                                        if (appUserMan != null) {
                                            // GameUtils.useHour(appUserMan);
                                            appUserCoupleStore.save(appUserCouple, Persistent.SAVE, appUserMan, appUserLimit);
                                        }
                                    } else if (appUser.getGender() == 2) {
                                        if (appUserLady != null) {
                                            // GameUtils.useHour(appUserLady);
                                            appUserCoupleStore.save(appUserCouple, Persistent.SAVE, appUserLady, appUserLimit);
                                        }
                                    }
                                    GameUtils.addResultArray(resultArray, "你们互相欣赏对方，一见钟情，有情人终成眷属！", null);
                                    resultObj.put("result", 0);
                                    resultObj.put("resultArray", resultArray);
                                } else {
                                    //如果对象有人
                                    AppUser coupleUser = appUserCouple.getUserId();
                                    if (coupleUser != null) {
                                        Integer currentMaxValue = appUserCouple.getValue();
                                        Object appUserObj = null;
                                        if (appUser.getGender() == 1) {
                                            appUserObj = appUserMan;

                                        } else if (appUser.getGender() == 2) {
                                            appUserObj = appUserLady;
                                        }

                                        boolean fire = false;
                                        fireValue = 0;
                                        if (requireList != null && !requireList.isEmpty()) {
                                            for (ResCoupleRequire coupleRequire : requireList) {
                                                String requireKey = coupleRequire.getAttrKey().toLowerCase();
                                                if (StringUtils.isNotBlank(requireKey)) {
                                                    Integer userValue = BeanUtils.getValue(appUserObj, requireKey, Integer.class);
                                                    if (userValue != null) {
                                                        if (userValue > currentMaxValue) {
                                                            fireValue = userValue;
                                                            fire = true;
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                        if (fire) {
                                            //抢成功
                                            AppUserCouple appUserCoupleFire = new AppUserCouple();
                                            appUserCoupleFire.setId(DrdsIDUtils.getID(DrdsTable.APP));
                                            appUserCoupleFire.setUserId(appUser);
                                            appUserCoupleFire.setCoupleId(resCouple);
                                            appUserCoupleFire.setValue(fireValue);
                                            appUserCoupleFire.setUseYn("Y");
                                            bind(appUserCoupleFire, userId);

                                            appUserCoupleStore.deleteFire(appUserCoupleFire, appUserCouple, appUserMan, appUserLimit);
                                            GameUtils.addResultArray(resultArray, "你各方面碾压" + coupleUser.getNickName() + "，挖墙脚成功！", null);
                                            resultObj.put("result", 0);
                                            resultObj.put("resultArray", resultArray);
                                        } else {
                                            //抢失败
                                            appUserLimitStore.save(appUserLimit, Persistent.SAVE);
                                            GameUtils.addResultArray(resultArray, "你各方面都不如" + coupleUser.getNickName() + "，去排行版了解下敌情吧！", null);
                                            resultObj.put("result", 1);
                                            resultObj.put("resultArray", resultArray);
                                        }
                                    }
                                }
                            }
                        } else {
                            //不符合条件
                            if (coupleLimit == 1) {
                                GameUtils.addResultArray(resultArray, "先努力成为更好的自己，死缠烂打只会招人烦！", null);
                            } else if (coupleLimit == 0) {
                                appUserLimitStore.save(appUserLimit, Persistent.SAVE);
                                JSONArray failAttrName = null;
                                if (appUser.getGender() == 1) {
                                    if (appUserMan != null) {
                                        failAttrName = GameUtils.failAttrNames(requireList, appUserMan, appUser.getGender());
                                    }
                                } else if (appUser.getGender() == 2) {
                                    if (appUserLady != null) {
                                        failAttrName = GameUtils.failAttrNames(requireList, appUserLady, appUser.getGender());
                                    }
                                }
                                GameUtils.addResultArray(resultArray, "你激动地介绍自己，可对方并没看上你，还是先成为更好的自己吧！", null);
                                GameUtils.addResultArray(resultArray, "要求：", failAttrName);
                            }

                            resultObj.put("result", 1);
                            resultObj.put("resultArray", resultArray);
                        }
                    }
                }
            }
        } catch (Exception ex) {
            if (ex instanceof StoreException) {
                throw new StoreException(ex);
            } else {
                throw new BizException(ex);
            }
        }
        return resultObj.toString();
    }

    @Override
    public String breakUp(Long userId, Long coupleId) throws BizException {
        JSONObject resultObj = new JSONObject();
        resultObj.put("result", -1);
        try {
            AppUserStore appUserStore = hsfServiceFactory.consumer(AppUserStore.class);
            AppUserManStore appUserManStore = hsfServiceFactory.consumer(AppUserManStore.class);
            AppUserLadyStore appUserLadyStore = hsfServiceFactory.consumer(AppUserLadyStore.class);
            AppUserCoupleStore appUserCoupleStore = hsfServiceFactory.consumer(AppUserCoupleStore.class);
            AppUserLimitStore appUserLimitStore = hsfServiceFactory.consumer(AppUserLimitStore.class);
            ResCoupleStore resCoupleStore = hsfServiceFactory.consumer(ResCoupleStore.class);
            if (appUserManStore != null && appUserLadyStore != null && appUserStore != null && appUserCoupleStore != null && resCoupleStore != null
                    && appUserLimitStore != null) {
                AppUser appUser = appUserStore.getById(userId);
                if (appUser != null) {
                    Integer coupleLimit = 1;
                    Integer day = -1;
                    AppUserMan appUserMan = null;
                    AppUserLady appUserLady = null;
                    AppUserLimit appUserLimit = null;
                    JSONArray resultArray = new JSONArray();
                    if (appUser.getGender() == 1) {
                        appUserMan = appUserManStore.getByUserId(userId);
                        if (appUserMan != null) {
                            day = appUserMan.getDays();
                            coupleLimit = appUserLimitStore.getCountByUserIdDayAction(userId, appUserMan.getDays(), "COUPLE");

                        }
                    } else if (appUser.getGender() == 2) {
                        appUserLady = appUserLadyStore.getByUserId(userId);
                        if (appUserLady != null) {
                            day = appUserLady.getDays();
                            coupleLimit = appUserLimitStore.getCountByUserIdDayAction(userId, appUserLady.getDays(), "COUPLE");
                        }
                    }
                    if (coupleLimit == 0) {
                        appUserLimit = new AppUserLimit();
                        appUserLimit.setId(DrdsIDUtils.getID(DrdsTable.APP));
                        appUserLimit.setUserId(appUser);
                        appUserLimit.setAction("COUPLE");
                        appUserLimit.setDay(day);
                        bind(appUserLimit, userId);
                        appUserLimit.setUseYn("Y");
                    }

                    if (coupleLimit == 0) {
                        ResCouple resCouple = resCoupleStore.getById(coupleId);
                        if (resCouple != null) {
                            AppUserCouple appUserCouple = appUserCoupleStore.getByUserId(userId);
                            if (appUserCouple != null) {
                                if (appUser.getGender() == 1) {
                                    if (appUserMan != null) {
                                        //   GameUtils.useHour(appUserMan);
                                        appUserCoupleStore.delete(appUserCouple, appUserMan, appUserLimit);
                                    }
                                } else if (appUser.getGender() == 2) {
                                    if (appUserLady != null) {
                                        //   GameUtils.useHour(appUserLady);
                                        appUserCoupleStore.delete(appUserCouple, appUserLady, appUserLimit);
                                    }
                                }
                                GameUtils.addResultArray(resultArray, "天下没有不散的宴席，祝好！", null);
                                resultObj.put("result", 0);
                                resultObj.put("resultArray", resultArray);
                            } else {
                                GameUtils.addResultArray(resultArray, "没有对象你来瞎凑什么热闹！", null);
                                resultObj.put("result", 1);
                                resultObj.put("resultArray", resultArray);
                            }
                        }
                    } else {
                        GameUtils.addResultArray(resultArray, "移情别恋也好，三观不和也好，改日再来！", null);
                        resultObj.put("result", 1);
                        resultObj.put("resultArray", resultArray);
                    }
                }
            }
        } catch (Exception ex) {
            if (ex instanceof StoreException) {
                throw new StoreException(ex);
            } else {
                throw new BizException(ex);
            }
        }
        return resultObj.toString();
    }
}
