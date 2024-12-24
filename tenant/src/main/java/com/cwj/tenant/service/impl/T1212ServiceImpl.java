package com.cwj.tenant.service.impl;


import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cwj.tenant.mapper.T1212Mapper;

import com.cwj.tenant.po.ProPoint;
import com.cwj.tenant.po.R;
import com.cwj.tenant.po.RequestParam;
import com.cwj.tenant.po.T1212;
import com.cwj.tenant.service.T1212Service;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author cwj
 */
@Slf4j
@Service
@Scope("prototype")
public class T1212ServiceImpl extends ServiceImpl<T1212Mapper, T1212> implements T1212Service {

    @Autowired
    T1212Mapper t1212Mapper;


    private List<ProPoint> cur = new ArrayList<>();
    private List<ProPoint> op = new ArrayList<>();
    private List<ProPoint> os = new ArrayList<>();

    private HashMap<String, List<T1212>> mlist = new HashMap<>();

    //        System.out.println(getmap);

    private int cnt_un = 0;

    public void Mv_Category(ProPoint proPoint, T1212 mv, String propertyId) {
        String mvpid = mv.getPrevPropertyId();
        int type = mvpid.isEmpty() ? 3 : mvpid.equals(propertyId) ? 1 : 2;
//        ProPoint proPoint = new ProPoint();
        proPoint.setPropertyId(type <= 2 ? mvpid : "");
        proPoint.setPropertyName(mv.getPrevPropertyName());
        proPoint.setPropertyAddress(mv.getPrevPropertyAddress());
        proPoint.setPropertyType(mv.getPrevPropertyType().toString());
        proPoint.setPropertyGrade(mv.getPrevPropertyGradeLabel());

        if (StringUtils.isNotEmpty(mv.getPrevPropertyLonLat()))
            proPoint.setCenter(Arrays.stream(mv.getPrevPropertyLonLat()
                    .split(",")).mapToDouble(Double::parseDouble).boxed().toArray(Double[]::new));
        else proPoint.setCenter(new Double[2]);

        List<ProPoint> tar = type == 1 ? cur : type == 2 ? op : os;

        tar.add(proPoint);

        if (type == 3) {
            mvpid = mv.getPrevPropertyAddress();
            if (mvpid.isEmpty()) {
                mvpid = "未知项目" + cnt_un;
                System.out.println(mvpid);
                cnt_un++;
            }
        }
        mlist.computeIfAbsent(mvpid, k -> new ArrayList<>()).add(mv);
    }


    @Override
    public R getmap(RequestParam requestParam) {//通过参数获取
//        double su=0;
        List<T1212> getmap = t1212Mapper.getmap(requestParam);
//        for(T1212 t:getmap)su+=t.getLeaseArea();
//        System.out.println(su);
        String propertyId = requestParam.getPropertyId();
        //先写迁入
        getmap.forEach(mv -> {
            ProPoint proPoint = new ProPoint();
            Mv_Category(proPoint, mv, propertyId);
        });

        System.out.println("cur:" + cur);
        System.out.println("op:" + op);
        System.out.println("os:" + os);

        //已经将三类项目分类完成，map里存的是每个项目的租户动向
        // 对于每一个项目，还需要填入的内容， 租户个数（去重),租户迁址面积，top3行业
        //对于当前项目比较特殊，租户个数（去重),租户迁址面积，top3行业（汇总——）
//        System.out.println(mlist);
        HashSet<String> op_tenant = new HashSet<>();
        HashSet<String> os_tenant = new HashSet<>();

        double sum_area = 0;
        HashMap<String, Double> ind_area = new HashMap<>();

        HashSet<String> calculatedpro = new HashSet<>();
        List<ProPoint> nop = new ArrayList<>();
        List<ProPoint> nos = new ArrayList<>();

        for (ProPoint pp : op) {
            if (calculatedpro.contains(pp.getPropertyId())) continue;
            calculatedpro.add(pp.getPropertyId());
            String propertyId1 = pp.getPropertyId();
            List<T1212> t1212s = mlist.get(propertyId1);//这个项目的搬迁信息
//            System.out.println(propertyId1+","+t1212s.size());
            HashSet<String> sub_tenant = new HashSet<>();
            double subarea = 0;
            HashMap<String, Double> sub_ind_area = new HashMap<>();
            if (t1212s != null) {
                for (T1212 t : t1212s) {
                    sub_tenant.add(t.getTenantId());
                    double l = t.getLeaseArea();
                    subarea += l;
                    sub_ind_area.merge(t.getPrimaryIndustry(), l, Double::sum);

                    op_tenant.add(t.getTenantId());

                    sum_area += l;
                    ind_area.merge(t.getPrimaryIndustry(), l, Double::sum);
                }
            }

            pp.setTenantNum(sub_tenant.size());
            pp.setOtherTenantNum(null);
            pp.setSelfTenantNum(null);
            pp.setOtherSourceTenantNum(null);
            pp.setTenantArea((double) Math.round(subarea));


            String collect = sub_ind_area.entrySet()
                    .stream()
                    .sorted((entry1, entry2) -> Double.compare(entry2.getValue(), entry1.getValue())) // 按值降序排序
                    .limit(3) // 取前 3 个元素
                    .map(Map.Entry::getKey) // 获取键
                    .collect(Collectors.joining(","));
            pp.setTopicIndustrial(collect);
            nop.add(pp);
//            System.out.println(pp);
        }
//        System.out.println(sum_area);

        cnt_un--;
        System.out.println(cnt_un);

        for (ProPoint pp : os) {

            String propertyAd1 = pp.getPropertyAddress();
            if (propertyAd1.isEmpty()) {
                propertyAd1 = "未知项目" + cnt_un;
                cnt_un--;
            }
            if (cnt_un < -1 || calculatedpro.contains(propertyAd1)) continue;
            calculatedpro.add(propertyAd1);
//            System.out.println(propertyAd1);

            List<T1212> t1212s = mlist.get(propertyAd1);//这个项目的搬迁信息

            HashSet<String> sub_tenant = new HashSet<>();
            double subarea = 0;
            HashMap<String, Double> sub_ind_area = new HashMap<>();
            System.out.println(propertyAd1);
            if (t1212s != null) {
                for (T1212 t : t1212s) {
                    sub_tenant.add(t.getTenantId());
                    double l = t.getLeaseArea();
                    subarea += l;
                    sub_ind_area.merge(t.getPrimaryIndustry(), l, Double::sum);

                    os_tenant.add(t.getTenantId());


                    sum_area += l;
                    ind_area.merge(t.getPrimaryIndustry(), l, Double::sum);

                }
            }
            pp.setTenantNum(sub_tenant.size());
            pp.setOtherTenantNum(null);
            pp.setSelfTenantNum(null);
            pp.setOtherSourceTenantNum(null);
            pp.setTenantArea((double) Math.round(subarea));

            String collect = sub_ind_area.entrySet()
                    .stream()
                    .sorted((entry1, entry2) -> Double.compare(entry2.getValue(), entry1.getValue())) // 按值降序排序
                    .limit(3) // 取前 3 个元素
                    .map(Map.Entry::getKey) // 获取键
                    .collect(Collectors.joining(","));
            pp.setTopicIndustrial(collect);
            nos.add(pp);
        }
        cnt_un++;


        HashSet<String> sub_tenant = new HashSet<>();
//        for (ProPoint pp : cur) {
//            String propertyId1 = pp.getPropertyId();
//            if (calculatedpro.contains(propertyId1)) continue;
//            calculatedpro.add(propertyId1);
        List<T1212> t1212s = mlist.get(propertyId);//这个项目的搬迁信息
//            System.out.println("---------" + t1212s.size());
        if (t1212s != null) {
            for (T1212 t : t1212s) {
                sub_tenant.add(t.getTenantId());
//                sel_tenant.add(t.getTenantId());
                double l = t.getLeaseArea();
                sum_area += l;
                ind_area.merge(t.getPrimaryIndustry(), l, Double::sum);
            }
        }


        ProPoint cen = !cur.isEmpty() ? cur.get(0) : new ProPoint();

        HashSet<String> all_tenant = new HashSet<>();
        for (String s : os_tenant) all_tenant.add(s);
        for (String s : op_tenant) all_tenant.add(s);
        for (String s : sub_tenant) all_tenant.add(s);

        cen.setTenantNum(all_tenant.size());
        cen.setSelfTenantNum(sub_tenant.size());
        cen.setOtherTenantNum(op_tenant.size());
        cen.setOtherSourceTenantNum(os_tenant.size());

        T1212 t1212 = getmap.get(0);
        cen.setPropertyId(t1212.getCurPropertyId());
        cen.setPropertyName(t1212.getCurPropertyName());
        cen.setPropertyAddress(t1212.getCurPropertyAddress());
        cen.setPropertyType(t1212.getCurPropertyType().toString());
        cen.setPropertyGrade(t1212.getCurPropertyGradeLabel());
        cen.setCenter(Arrays.stream(t1212.getPrevPropertyLonLat().split(","))
                .mapToDouble(Double::parseDouble).boxed().toArray(Double[]::new));


        cen.setTenantArea((double) Math.round(sum_area));
        //        pp.setTenantArea((double) Math.round(subarea));
        String collect = ind_area.entrySet()
                .stream()
                .sorted((entry1, entry2) -> Double.compare(entry2.getValue(), entry1.getValue())) // 按值降序排序
                .limit(3) // 取前 3 个元素
                .map(Map.Entry::getKey) // 获取键
                .collect(Collectors.joining(","));
        cen.setTopicIndustrial(collect);


        JSONObject res = new JSONObject();


        res.put("curProperty", cen);
        res.put("otherProperty", nop);
        res.put("otherSource", nos);

        cur.clear();
        op.clear();
        os.clear();
        mlist.clear();

//        String jsonString = JSONObject.toJSONString(res, SerializerFeature.WriteMapNullValue);

        return R.ok(res);
    }
}
