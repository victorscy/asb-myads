package com.adserversoft.flexfuse.client.model {
import com.adserversoft.flexfuse.client.model.vo.BaseVO;
import com.adserversoft.flexfuse.client.model.vo.ServerRequestVO;
import com.adserversoft.flexfuse.client.model.vo.StateVO;

import mx.collections.ArrayCollection;
import mx.rpc.events.FaultEvent;
import mx.rpc.events.ResultEvent;
import mx.rpc.remoting.mxml.Operation;
import mx.rpc.remoting.mxml.RemoteObject;

import org.puremvc.as3.interfaces.IProxy;
import org.puremvc.as3.patterns.proxy.Proxy;

public class StateProxy extends Proxy implements IProxy {
    public static const NAME:String = 'StateProxy';

    private var stateRO:RemoteObject;
    private var _state:StateVO = new StateVO();


    public function StateProxy() {
        super(NAME, new ArrayCollection);
        stateRO = new RemoteObject();
        stateRO.destination = "state";
        stateRO.requestTimeout = ApplicationConstants.REQUEST_TIMEOUT_SECONDS;

        stateRO.saveState.addEventListener("result", saveStateResultHandler);
        stateRO.saveState.addEventListener("fault", faultHandler);

        stateRO.loadState.addEventListener("result", loadStateResultHandler);
        stateRO.loadState.addEventListener("fault", faultHandler);
    }


    private function saveStateResultHandler(event:*):void {
        if (event.result.result == ApplicationConstants.FAILURE) {
            sendNotification(ApplicationConstants.SERVER_FAULT, event.result);
        } else if (event.result.result == ApplicationConstants.SUCCESS) {
            sendNotification(ApplicationConstants.STATE_SAVED);
        }
    }

    private function loadStateResultHandler(event:*):void {
        if (event.result.result == ApplicationConstants.FAILURE) {
            sendNotification(ApplicationConstants.SERVER_FAULT, event.result);
        } else if (event.result.result == ApplicationConstants.SUCCESS) {
            state = (ResultEvent)(event).result.resultingObject as StateVO;
            var bannerProxy:BannerProxy = facade.retrieveProxy(BannerProxy.NAME) as BannerProxy;
            bannerProxy.banners = BaseVO.collection2map(state.banners);
            var adPlaceProxy:AdPlaceProxy = facade.retrieveProxy(AdPlaceProxy.NAME) as AdPlaceProxy;
            adPlaceProxy.adPlaces = BaseVO.collection2map(state.adPlaces);
            sendNotification(ApplicationConstants.STATE_LOADED);
        }
    }

    private function faultHandler(event:FaultEvent):void {
        sendNotification(ApplicationConstants.SERVER_FAULT, event);
    }


    public function saveState():void {
        var userProxy:UserProxy = facade.retrieveProxy(UserProxy.NAME) as UserProxy;
        var bannerProxy:BannerProxy = facade.retrieveProxy(BannerProxy.NAME) as BannerProxy;
        var adPlaceProxy:AdPlaceProxy = facade.retrieveProxy(AdPlaceProxy.NAME) as AdPlaceProxy;
        var settingsProxy:SettingsProxy = facade.retrieveProxy(SettingsProxy.NAME) as SettingsProxy;

        var sr:ServerRequestVO = new ServerRequestVO(userProxy.authenticatedUser.sessionId, ApplicationConstants.VERSION, settingsProxy.settings.installationId);
        var state:StateVO = new StateVO();
        state.adPlaces = BaseVO.array2collection(adPlaceProxy.adPlaces.getValues());
        state.banners = BaseVO.array2collection(bannerProxy.banners.getValues());
        Operation(stateRO.saveState).arguments = new Array(sr, state);
        stateRO.saveState(sr, state);
    }


    public function loadState():void {
        var userProxy:UserProxy = facade.retrieveProxy(UserProxy.NAME) as UserProxy;
        var settingsProxy:SettingsProxy = facade.retrieveProxy(SettingsProxy.NAME) as SettingsProxy;

        var sr:ServerRequestVO = new ServerRequestVO(userProxy.authenticatedUser.sessionId, ApplicationConstants.VERSION, settingsProxy.settings.installationId);
        Operation(stateRO.loadState).arguments = new Array(sr);
        stateRO.loadState(sr);
    }


    public function get state():StateVO {
        return _state;
    }

    public function set state(value:StateVO):void {
        _state = value;
    }
}
}
