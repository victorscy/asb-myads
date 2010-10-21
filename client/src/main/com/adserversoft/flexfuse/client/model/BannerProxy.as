package com.adserversoft.flexfuse.client.model {
import com.adserversoft.flexfuse.client.model.vo.BannerVO;
import com.adserversoft.flexfuse.client.model.vo.HashMap;
import com.adserversoft.flexfuse.client.model.vo.HashMapEntry;
import com.adserversoft.flexfuse.client.model.vo.IMap;

import mx.collections.ArrayCollection;
import mx.collections.Sort;
import mx.collections.SortField;
import mx.rpc.remoting.mxml.RemoteObject;

import org.puremvc.as3.interfaces.IProxy;
import org.puremvc.as3.patterns.proxy.Proxy;

public class BannerProxy extends Proxy implements IProxy {
    public static const NAME:String = 'BannerProxy';
    private var _banners:IMap = new HashMap();

    private var bannerRO:RemoteObject;

    public function BannerProxy() {
        super(NAME, new ArrayCollection);
        bannerRO = new RemoteObject();
        bannerRO.destination = "banner";
        bannerRO.requestTimeout = ApplicationConstants.REQUEST_TIMEOUT_SECONDS;
    }

    public function get banners():IMap {
        return _banners;
    }

    public function set banners(m:IMap):void {
        var entries:Array = m.getEntries();
        for (var i:int = 0; i < entries.length; i++) {
            var entry:HashMapEntry = entries[i];
            var key:String = entry.key;
            var banner:BannerVO = entry.value;
            if (_banners.containsKey(key)) {
                BannerVO(_banners.getValue(key)).mergeProperties(banner);
            } else {
                _banners.put(key, banner);
            }
        }
    }


    /**
     *
     * @param uid
     * @return collection of banners which parentUid equals uid
     */
    public function getBannersByParentUid(uid:String):ArrayCollection {
        var ac:ArrayCollection = new ArrayCollection();
        for (var i:int = 0; i < _banners.getValues().length; i++) {
            var banner:BannerVO = _banners.getValues()[i];
            if (banner.parentUid == uid)ac.addItem(banner);
        }
        return ac;
    }

    public function removeBannersByParentUid(uid:String):void {
        for (var i:int = 0; i < _banners.getValues().length; i++) {
            var banner:BannerVO = _banners.getValues()[i];
            if (banner.parentUid == uid)_banners.remove(banner.uid);
        }
    }

    public function getBannersByAdPlaceUid(uid:String):ArrayCollection {
        var ac:ArrayCollection = new ArrayCollection();
        for (var i:int = 0; i < _banners.getValues().length; i++) {
            var banner:BannerVO = _banners.getValues()[i];
            if (banner.adPlaceUid == uid)ac.addItem(banner);
        }
        var dataSortField:SortField = new SortField();
        dataSortField.name = "priority";
        dataSortField.numeric = true;
        var numericDataSort:Sort = new Sort();
        numericDataSort.fields = [dataSortField];
        ac.sort = numericDataSort;
        ac.refresh();

        return ac;
    }
}
}