package com.adserversoft.flexfuse.client.model.vo {
import com.adserversoft.flexfuse.client.ApplicationFacade;
import com.adserversoft.flexfuse.client.model.ApplicationConstants;
import com.adserversoft.flexfuse.client.model.BannerProxy;

import mx.collections.ArrayCollection;

[Bindable]
[RemoteClass(alias="com.adserversoft.flexfuse.server.api.AdPlace")]
public dynamic class AdPlaceVO extends BaseVO {
    public static var counter:int = 0;

    public var id:Object;
    public var uid:String;
    public var adPlaceName:String;
    public var adPlaceState:int = ApplicationConstants.STATE_ACTIVE;

    public var adFormatId:int;
    public var isAdFormatEnabled:Boolean = true;

    private var _views:int;
    private var _clicks:int;
    public var ctr:String;
    public var expand:Boolean = false;

    public function AdPlaceVO() {
        counter++;
        adFormat = ApplicationConstants.sortedAdFormatsCollection.getItemAt(0) as AdFormatVO;
        views = 0;
        clicks = 0;
        ctr = String((views == 0) ? 0 : Math.round(((clicks / views) * 100) * 100) / 100) + "%";
    }


    public function get views():int {
        return _views;
    }

    public function set views(v:int):void {
        _views = v;
        ctr = String((views == 0) ? 0 : Math.round(((clicks / views) * 100) * 100) / 100) + "%";
    }

    public function get clicks():int {
        return _clicks;
    }

    public function set clicks(v:int):void {
        _clicks = v;
        ctr = String((views == 0) ? 0 : Math.round(((clicks / views) * 100) * 100) / 100) + "%";
    }

    public function get adFormat():AdFormatVO {
        return ApplicationConstants.AD_FORMATS.getValue(adFormatId);
    }

    public function set adFormat(af:AdFormatVO):void {
        this.adFormatId = af.id;
    }

    public function get banners():ArrayCollection {
        var bannerProxy:BannerProxy = ApplicationFacade.getInstance().retrieveProxy(BannerProxy.NAME) as BannerProxy;
        return bannerProxy.getBannersByAdPlaceUid(uid);
    }

    public function mergeProperties(adPlace:AdPlaceVO):void {
        this.id = adPlace.id;
        this.uid = adPlace.uid;
        this.adPlaceName = adPlace.adPlaceName;
        this.adPlaceState = adPlace.adPlaceState;
        this.adFormatId = adPlace.adFormatId;
        this.views = adPlace.views;
        this.clicks = adPlace.clicks;
        this.expand = adPlace.expand;
    }

}
}