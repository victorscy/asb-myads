package com.adserversoft.flexfuse.client.view.component.dnd {
import com.adserversoft.flexfuse.client.ApplicationFacade;
import com.adserversoft.flexfuse.client.model.ApplicationConstants;
import com.adserversoft.flexfuse.client.model.BannerProxy;
import com.adserversoft.flexfuse.client.model.vo.AdPlaceVO;
import com.adserversoft.flexfuse.client.model.vo.BannerVO;
import com.adserversoft.flexfuse.client.model.vo.IDragNDropWizard;
import com.adserversoft.flexfuse.client.model.vo.IMap;
import com.adserversoft.flexfuse.client.model.vo.ObjectEvent;

import flash.events.Event;
import flash.events.MouseEvent;

import mx.containers.HBox;
import mx.containers.HDividedBox;
import mx.controls.Button;
import mx.core.UIComponent;
import mx.events.FlexEvent;

public class DragNDropWizard extends HDividedBox implements IDragNDropWizard {

    public static var ADD_BANNER_BTN_CLICK:String = "ADD_BANNER_BTN_CLICK";
    public static var PREVIEW_BANNER_BTN_CLICK:String = "PREVIEW_BANNER_BTN_CLICK";
    public static var EDIT_BANNER_BTN_CLICK:String = "EDIT_BANNER_BTN_CLICK";
    public static var REMOVE_BANNER_BTN_CLICK:String = "REMOVE_BANNER_BTN_CLICK";

    public static var ADD_AD_PLACE_BTN_CLICK:String = "ADD_AD_PLACE_BTN_CLICK";
    public static var GET_AD_TAG_AD_PLACE_BTN_CLICK:String = "GET_AD_TAG_AD_PLACE_BTN_CLICK";
    public static var REMOVE_AD_PLACE_BTN_CLICK:String = "REMOVE_AD_PLACE_BTN_CLICK";

    public static var BANNER_CHANGE:String = "BANNER_CHANGE";
    public static var BANNER_TRAFFIC_SHARE_INVALID:String = "BANNER_TRAFFIC_SHARE_INVALID";
    public static var BANNER_TRAFFIC_SHARE_VALID:String = "BANNER_TRAFFIC_SHARE_VALID";
    public static var AD_PLACE_CHANGE:String = "AD_PLACE_CHANGE";


    public var hDividedBox:HDividedBox;
    public var adPlacesPanel:AdPlacesPanel;
    public var bannersPanel:BannersPanel;
    public var addAdPlaceB:Button;
    public var addBannerB:Button;
    public var leftHB:HBox;

    public var banners:IMap;
    public var adPlaces:IMap;


    public function DragNDropWizard() {

        super();
        if (UIComponent(this).initialized) {
            onCreationComplete(null);
        } else {
            UIComponent(this).addEventListener(FlexEvent.CREATION_COMPLETE, onCreationComplete);
        }

    }

    private function onCreationComplete(event:Event):void {
        adPlacesPanel = new AdPlacesPanelUI();
        adPlacesPanel.id = "adPlacesPanel";
        bannersPanel = new BannersPanelUI();
        bannersPanel.id = "bannersPanel";

        hDividedBox.addChild(adPlacesPanel);
        hDividedBox.addChild(bannersPanel);

        adPlacesPanel.addAdPlaceB.addEventListener(MouseEvent.CLICK, onAdPlaceAdd);
        bannersPanel.addBannerB.addEventListener(MouseEvent.CLICK, onBannerAdd);

        //used for testing
        //        var b:BannerVO = new BannerVO();
        //        b.uid = ApplicationConstants.getNewUid();
        //        banners.put(b.uid, b);
        //
        //        var b2:BannerVO = new BannerVO();
        //        b2.uid = ApplicationConstants.getNewUid();
        //        banners.put(b2.uid, b2);
        //
        //        var a:AdPlaceVO = new AdPlaceVO();
        //        a.uid = ApplicationConstants.getNewUid();
        //        adPlaces.put(a.uid, a);

    }


    public function setMaps(bbs:IMap, aps:IMap):void {
        banners = bbs;
        adPlaces = aps;


        adPlacesPanel.removeAllAdPlaces();
        bannersPanel.removeAllBanners();

        var k:int;
        var adPlace:AdPlaceVO;

        for (k = 0; k < adPlaces.getKeys().length; k++) {
            adPlace = adPlaces.getValues()[k] as AdPlaceVO;
            addAdPlace(adPlace, false);
        }

        for (var i:int = 0; i < banners.getKeys().length; i++) {
            var banner:BannerVO = banners.getValues()[i] as BannerVO;
            if (banner.adPlaceUid == null) {
                addBanner(banner);
                bannerAdFormatEnable(banner);
            }
        }

        for (k = 0; k < adPlaces.getKeys().length; k++) {
            adPlace = adPlaces.getValues()[k] as AdPlaceVO;
            adPlaceAdFormatEnable(adPlace);
        }

    }

    public function onBannerPreView(banner:BannerVO):void {
        dispatchEvent(new ObjectEvent(PREVIEW_BANNER_BTN_CLICK, banner));
    }

    private function onBannerAdd(e:MouseEvent):void {
        dispatchEvent(new ObjectEvent(ADD_BANNER_BTN_CLICK, null));
    }


    public function onBannerRemove(uid:String):void {
        dispatchEvent(new ObjectEvent(REMOVE_BANNER_BTN_CLICK, uid));
    }

    public function onBannerEdit(uid:String):void {
        dispatchEvent(new ObjectEvent(EDIT_BANNER_BTN_CLICK, uid));
    }

    private function onAdPlaceAdd(e:MouseEvent):void {
        var adPlace:AdPlaceVO = new AdPlaceVO();
        adPlace.adPlaceName = "new ad place " + AdPlaceVO.counter;
        adPlace.uid = ApplicationConstants.getNewUid();
        adPlaces.put(adPlace.uid, adPlace);
        addAdPlace(adPlace, true);
        dispatchEvent(new ObjectEvent(ADD_AD_PLACE_BTN_CLICK, adPlace.uid));
    }

    private function addAdPlace(adPlace:AdPlaceVO, setFocusOnCreate:Boolean):void {
        var newAdPlace:AdPlaceView = new AdPlaceViewUI();
        newAdPlace.dndWizard = this;
        newAdPlace.adPlaceUid = adPlace.uid;
        newAdPlace.setFocusOnCreate = setFocusOnCreate;
        adPlacesPanel.leftVB.addChildAt(newAdPlace, adPlacesPanel.leftVB.getChildren().length - 1);
    }


    public function addBanner(banner:BannerVO):void {
        var newBanner:BannerView = new BannerViewUI();
        newBanner.dndWizard = this;
        newBanner.bannerUid = banner.uid;
        newBanner.id = "bannerView" + String(bannersPanel.rightVB.getChildren().length - 1);
        bannersPanel.rightVB.addChildAt(newBanner, bannersPanel.rightVB.getChildren().length - 1);
    }

    public function updateBanner(banner:BannerVO):void {
        var bannerView:BannerView = findBannerViewByUid(banner.uid);
        bannerView.updateProviders();
    }

    public function addCustomEventListener(type:String, func:Function):void {
        this.addEventListener(type, func);
    }

    public function deleteAdPlace(ap:AdPlaceVO):void {
        var adPlaceView:AdPlaceView = adPlacesPanel.getAdPlaceViewByUid(ap.uid);
        if (adPlaceView != null)adPlacesPanel.leftVB.removeChild(adPlaceView);
    }

    public function deleteBanner(banner:BannerVO):void {
        var bannerView:BannerView = findBannerViewByUid(banner.uid);
        if (banners.getValue(banner.parentUid) != null) {
            bannerAdFormatEnable(banners.getValue(banner.parentUid));
        }
        if (banner.adPlaceUid == null) {
            bannersPanel.rightVB.removeChild(bannerView);
        } else {
            var pad:Paddock = Paddock(bannerView.parent.parent);
            pad.contentVB.removeChild(bannerView);
            pad.updateTrafficSharesByPriority(banner.priority);
            pad.validateTrafficSharesByPriority(banner.priority);
            pad.refresh();
            var adPlace:AdPlaceVO = adPlaces.getValue(banner.adPlaceUid);
            adPlaceAdFormatEnable(adPlace);
        }
    }

    private function findBannerViewByUid(uid:String):BannerView {
        var bannerView:BannerView = bannersPanel.getBannerViewByUid(uid);
        if (bannerView != null) {
            return bannerView;
        } else {
            bannerView = adPlacesPanel.getBannerViewByUid(uid);
            if (bannerView != null) {
                return bannerView;
            } else {
                trace("Couldn't remove banner from ui:" + uid);
            }
        }
        return null;
    }


    private function adPlaceAdFormatEnable(adPlace:AdPlaceVO):void {
        if (adPlace.banners.length == 0) {
            adPlace.isAdFormatEnabled = true;
        } else {
            adPlace.isAdFormatEnabled = false;
        }
    }

    public function bannerAdFormatEnable(banner:BannerVO):void {
        if (banner.adPlaceUid != null) {
            banner.isAdFormatEnabled = false;
            return;
        }
        var bannerProxy:BannerProxy = ApplicationFacade.getInstance().retrieveProxy(BannerProxy.NAME) as BannerProxy;
        banner.isAdFormatEnabled = bannerProxy.getBannersByParentUid(banner.uid).length == 0;
    }
}
}