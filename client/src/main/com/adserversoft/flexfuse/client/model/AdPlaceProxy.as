package com.adserversoft.flexfuse.client.model {
import com.adserversoft.flexfuse.client.model.vo.AdPlaceVO;
import com.adserversoft.flexfuse.client.model.vo.HashMap;
import com.adserversoft.flexfuse.client.model.vo.HashMapEntry;
import com.adserversoft.flexfuse.client.model.vo.IMap;

import mx.collections.ArrayCollection;

import org.puremvc.as3.interfaces.IProxy;
import org.puremvc.as3.patterns.proxy.Proxy;

public class AdPlaceProxy extends Proxy implements IProxy {
    public static const NAME:String = 'AdPlaceProxy';

    private var _adPlaces:IMap = new HashMap();

    public function AdPlaceProxy() {
        super(NAME, new ArrayCollection);
    }


    public function get adPlaces():IMap {
        return _adPlaces;
    }

    public function set adPlaces(m:IMap):void {
        var entries:Array = m.getEntries();
        for (var i:int = 0; i < entries.length; i++) {
            var entry:HashMapEntry = entries[i];
            var key:String = entry.key;
            var adPlace:AdPlaceVO = entry.value;
            if (_adPlaces.containsKey(key)) {
                AdPlaceVO(_adPlaces.getValue(key)).mergeProperties(adPlace);
            } else {
                _adPlaces.put(key, adPlace);
            }
        }
    }
}
}