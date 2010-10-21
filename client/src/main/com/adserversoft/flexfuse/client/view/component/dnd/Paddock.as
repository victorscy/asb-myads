package com.adserversoft.flexfuse.client.view.component.dnd {
import com.adserversoft.flexfuse.client.model.ApplicationConstants;
import com.adserversoft.flexfuse.client.model.vo.AdPlaceVO;
import com.adserversoft.flexfuse.client.model.vo.BannerVO;
import com.adserversoft.flexfuse.client.model.vo.ObjectEvent;

import flash.events.Event;

import mx.collections.ArrayCollection;
import mx.containers.HBox;
import mx.containers.VBox;
import mx.controls.HRule;
import mx.core.IUIComponent;
import mx.core.ScrollPolicy;
import mx.events.DragEvent;
import mx.events.FlexEvent;
import mx.managers.DragManager;
import mx.states.State;

[Bindable]
public class Paddock extends VBox {
    public var dndWizard:DragNDropWizard;
    public var bannersAddedState:State;
    public var dragHereState:State;
    public var contentVB:AutosizeVBox;
    public var headersHB:HBox;
    public var dragMessageHB:HBox;
    public var headersVB:VBox;
    public var contentBottomLineHR:HRule;

    public function Paddock() {

        addEventListener(FlexEvent.CREATION_COMPLETE, onCreationComplete);

    }

    private function onCreationComplete(event:Event):void {
        var i:int;

        states = new Array();
        states.push(bannersAddedState);
        states.push(dragHereState);
        currentState = dragHereState.name;

        percentWidth = 100;
        setStyle("verticalGap", "0");
        setStyle("backgroundColor", "#f7f7f7");
        setStyle("verticalAlign", "bottom");
        setStyle("horizontalAlign", "center");

        verticalScrollPolicy = ScrollPolicy.OFF;
        horizontalScrollPolicy = ScrollPolicy.OFF;

        addEventListener(DragEvent.DRAG_ENTER, doDragEnter);
        addEventListener(DragEvent.DRAG_EXIT, doDragExit);
        addEventListener(DragEvent.DRAG_DROP, doDragDrop);

        var maxBannerPriority:int = 0;
        var adPlaceView:AdPlaceView = AdPlaceView(parent);
        var adPlace:AdPlaceVO = dndWizard.adPlaces.getValue(adPlaceView.adPlaceUid);
        var assignedBanners:ArrayCollection = adPlace.banners;
        for (i = 0; i < assignedBanners.length; i++) {
            if ((assignedBanners.getItemAt(i) as BannerVO).priority > maxBannerPriority) {
                maxBannerPriority = (assignedBanners.getItemAt(i) as BannerVO).priority;
            }
            addBanner(assignedBanners.getItemAt(i) as BannerVO);
        }

        for (i = 1; i <= maxBannerPriority; i++) {
            validateTrafficSharesByPriority(i);
        }
        parent.removeChild(this);

    }

    private function doDragEnter(event:DragEvent):void {
        var dropPaddock:Paddock = Paddock(event.currentTarget);
        if (dropPaddock.hasBanners())return;
        var dropAdPlaceView:AdPlaceView = dropPaddock.parent as AdPlaceView;

        //can only drop banner if this ad place's ad format matches banner's ad format
        if (event.dragInitiator.parent is BannerView) {
            var draggedBannerView:BannerView = event.dragInitiator.parent as BannerView;
            var adPlace:AdPlaceVO = dndWizard.adPlaces.getValue(dropAdPlaceView.adPlaceUid);
            var banner:BannerVO = dndWizard.banners.getValue(draggedBannerView.bannerUid);
            if (banner.adFormat == adPlace.adFormat) {
                setColor(event, "#c7c7f7");
            }
        }
    }

    private function doDragExit(event:DragEvent):void {
        setColor(event, "#f7f7f7");
    }

    private function setColor(event:DragEvent, borderColor:String):void {
        if (event.dragInitiator.parent is BannerView) {
            var paddock:Paddock = event.currentTarget as Paddock;
            paddock.setStyle("backgroundColor", borderColor);
            DragManager.acceptDragDrop(IUIComponent(event.currentTarget));
        }
    }

    private function doDragDrop(event:DragEvent):void {
        var draggedBannerView:BannerView = event.dragInitiator.parent as BannerView;
        var dropPaddock:Paddock = event.currentTarget as Paddock;
        var dropAdPlaceView:AdPlaceView = dropPaddock.parent as AdPlaceView;
        var newBannerView:BannerView = draggedBannerView;

        //dragging from another paddock
        if (draggedBannerView.parent.parent is Paddock) {//if dragging from another paddock
            var draggedPaddock:Paddock = draggedBannerView.parent.parent as Paddock;
            var bannerPriority:int = BannerVO(dndWizard.banners.getValue(draggedBannerView.bannerUid)).priority;
            draggedPaddock.contentVB.removeChild(draggedBannerView);
            draggedPaddock.refresh();
            draggedPaddock.updateTrafficSharesByPriority(bannerPriority);
            draggedPaddock.validateTrafficSharesByPriority(bannerPriority);
            //dragging from the banners panel
        } else {
            newBannerView = new BannerViewUI();
            newBannerView.dndWizard = dndWizard;
            //creating new banner vo using clone
            var newBanner:BannerVO = BannerVO(dndWizard.banners.getValue(draggedBannerView.bannerUid)).clone();

            //generating new uid for this object and setting bindings
            newBanner.uid = ApplicationConstants.getNewUid();
            //            newBanner.adPlaceUid = dropAdPlaceView.adPlaceUid;
            newBanner.parentUid = draggedBannerView.bannerUid;

            newBannerView.bannerUid = newBanner.uid;
            dndWizard.banners.put(newBanner.uid, newBanner);

            //changing banner's state to display different columns
            newBannerView.currentStateName = BannerView.ASSIGNED_STATE;
        }

        //adding banner to this paddock
        dropPaddock.contentVB.addChild(new PlaceHolder(dndWizard));
        dropPaddock.contentVB.addChild(newBannerView);
        dropPaddock.contentVB.addChild(new PlaceHolder(dndWizard));

        BannerVO(dndWizard.banners.getValue(newBannerView.bannerUid)).adPlaceUid = dropAdPlaceView.adPlaceUid;

        dropPaddock.setColor(event, "#f7f7f7");

        //we don't need to listen on the paddock after the first drop, banner and place holders will serve as glue now
        dropPaddock.removeEventListener(DragEvent.DRAG_ENTER, doDragEnter);
        dropPaddock.removeEventListener(DragEvent.DRAG_EXIT, doDragExit);
        dropPaddock.removeEventListener(DragEvent.DRAG_DROP, doDragDrop);
        dropPaddock.refresh();
        dropPaddock.updateTrafficSharesByPriority(1);
        dropPaddock.validateTrafficSharesByPriority(1);

        var adPlaceView:AdPlaceView = AdPlaceView(parent);
        var adPlace:AdPlaceVO = dndWizard.adPlaces.getValue(adPlaceView.adPlaceUid);
        if (adPlace.banners.length == 0) {
            adPlace.isAdFormatEnabled = true;
        } else {
            adPlace.isAdFormatEnabled = false;
        }
        //notifying of the state change
        dndWizard.dispatchEvent(new ObjectEvent(DragNDropWizard.BANNER_CHANGE, newBannerView));
    }

    private function hasBanners():Boolean {
        for (var i:int = 0; i < contentVB.getChildren().length; i++) {
            if (contentVB.getChildAt(i) is BannerView)return true;
        }
        return false;
    }

    public function updateTrafficSharesByPriority(priority:int):void {
        var count:int = 0;
        var lastBanner:BannerVO;
        var k:int;
        for (k = 1; k < contentVB.getChildren().length; k++) {//starting from 1 because it should be a place holder
            if (contentVB.getChildAt(k) is BannerView) {
                if (BannerVO(dndWizard.banners.getValue(BannerView(contentVB.getChildAt(k)).bannerUid)).priority == priority) {
                    count++;
                }
            }
        }
        if (count == 0) return;
        for (k = 1; k < contentVB.getChildren().length; k++) {//starting from 1 because it should be a place holder
            if (contentVB.getChildAt(k) is BannerView) {
                if (BannerVO(dndWizard.banners.getValue(BannerView(contentVB.getChildAt(k)).bannerUid)).priority == priority) {
                    dndWizard.banners.getValue(BannerView(contentVB.getChildAt(k)).bannerUid).trafficShare = 100 / count;
                    lastBanner = dndWizard.banners.getValue(BannerView(contentVB.getChildAt(k)).bannerUid);
                }
            }
        }
        lastBanner.trafficShare = 100 - int(100 / count) * (count - 1);
//        refresh();
//        validateTrafficSharesByPriority(priority);
    }

    public function validateTrafficSharesByPriority(priority:int):void {
        var totalTrafficShare:int = 0;
        var k:int;

        for (k = 1; k < contentVB.getChildren().length; k++) {//starting from 1 because it should be a place holder
            if (contentVB.getChildAt(k) is BannerView) {
                if (BannerVO(dndWizard.banners.getValue(BannerView(contentVB.getChildAt(k)).bannerUid)).priority == priority) {
                    totalTrafficShare += BannerVO(dndWizard.banners.getValue(BannerView(contentVB.getChildAt(k)).bannerUid)).trafficShare;
                }
            }
        }

        if (totalTrafficShare != 100) {  // wrong traffic share
            //dndWizard.dispatchEvent(new ObjectEvent(DragNDropWizard.BANNER_TRAFFIC_SHARE_INVALID, null));
            for (k = 1; k < contentVB.getChildren().length; k++) {
                if (contentVB.getChildAt(k) is BannerView) {
                    if (BannerVO(dndWizard.banners.getValue(BannerView(contentVB.getChildAt(k)).bannerUid)).priority == priority) {
                        (contentVB.getChildAt(k) as BannerView).trafficShareTI.setStyle("color", "0xFF0000");
                    }
                }
            }

        } else {       // valid banner traffic share
            //dndWizard.dispatchEvent(new ObjectEvent(DragNDropWizard.BANNER_TRAFFIC_SHARE_VALID, null));
            for (k = 1; k < contentVB.getChildren().length; k++) {
                if (contentVB.getChildAt(k) is BannerView) {
                    if (BannerVO(dndWizard.banners.getValue(BannerView(contentVB.getChildAt(k)).bannerUid)).priority == priority) {
                        (contentVB.getChildAt(k) as BannerView).trafficShareTI.setStyle("color", "0x000000");
                    }
                }
            }
        }
        refresh();
    }


    public function refresh():void {
        if (hasBanners()) {
            currentState = bannersAddedState.name;
        } else {
            currentState = dragHereState.name;
        }

        if (currentState == dragHereState.name) {
            addEventListener(DragEvent.DRAG_ENTER, doDragEnter);
            addEventListener(DragEvent.DRAG_EXIT, doDragExit);
            addEventListener(DragEvent.DRAG_DROP, doDragDrop);
            contentVB.removeAllChildren();
        } else {

            //inserting place holders at the bottom and top if necessary
            var placeHolder:PlaceHolder;
            if (!(contentVB.getChildAt(0) is PlaceHolder)) {
                placeHolder = new PlaceHolder(dndWizard);
                contentVB.addChildAt(placeHolder, 0);
            }
            if (!(contentVB.getChildAt(contentVB.getChildren().length - 1) is PlaceHolder)) {
                placeHolder = new PlaceHolder(dndWizard);
                contentVB.addChildAt(placeHolder, contentVB.getChildren().length);
            }

            //removing two place holders in a row, refreshing banners
            try {
                for (var i:int = 0; i < contentVB.getChildren().length; i++) {
                    if (contentVB.getChildAt(i) is PlaceHolder && contentVB.getChildAt(i + 1) is PlaceHolder) {
                        contentVB.removeChildAt(i);
                    } else if (contentVB.getChildAt(i) is BannerView) {
                        BannerView(contentVB.getChildAt(i)).refresh();
                    }
                }
            } catch(event:*) {
            }

            //updating priorities
            var priority:int = 1;
            for (var k:int = 1; k < contentVB.getChildren().length; k++) {//starting from 1 because it should be a place holder
                if (contentVB.getChildAt(k) is BannerView) {
                    BannerVO(dndWizard.banners.getValue(BannerView(contentVB.getChildAt(k)).bannerUid)).priority = priority;
                } else {
                    ++priority;
                }
            }

        }


        callLater(resizeContent);

    }

    private function resizeContent():void {
        contentVB.invalidateSize();
        contentVB.validateNow();
    }

    /**
     * Adds banner to paddock as if drag-dropped from the banners panel, used on state load
     * @param banner
     */
    public function addBanner(banner:BannerVO):void {
        //changing state if first drop
        if (currentState == dragHereState.name) {//first drop, changing state
            currentState = bannersAddedState.name;
        }
        var insertionPoint:int = 0;
        //looking for the insertion point based on the priority
        for (var i:int = 0; i < contentVB.getChildren().length; i++) {
            if (contentVB.getChildAt(i) is BannerView) {
                var bv:BannerView = contentVB.getChildAt(i) as BannerView;
                var b:BannerVO = dndWizard.banners.getValue(bv.bannerUid) as BannerVO;
                insertionPoint = i + 1;
                if (banner.priority <= b.priority) {
                    break;
                }
            }
        }
        var newBannerView:BannerViewUI = new BannerViewUI();
        newBannerView.dndWizard = dndWizard;
        newBannerView.bannerUid = banner.uid;
        newBannerView.currentStateName = BannerView.ASSIGNED_STATE;
        contentVB.addChildAt(newBannerView, insertionPoint);

        //inserting place holders between banners of different priority
        try {
            for (var d:int = 0; d < contentVB.getChildren().length; d++) {
                if (contentVB.getChildAt(d) is BannerView && contentVB.getChildAt(d + 1) is BannerView) {
                    var bv1:BannerView = contentVB.getChildAt(d) as BannerView;
                    var bv2:BannerView = contentVB.getChildAt(d + 1) as BannerView;
                    var ban1:BannerVO = dndWizard.banners.getValue(bv1.bannerUid) as BannerVO;
                    var ban2:BannerVO = dndWizard.banners.getValue(bv2.bannerUid) as BannerVO;
                    if (ban1.priority != ban2.priority) {
                        var placeHolder:PlaceHolder = new PlaceHolder(dndWizard);
                        contentVB.addChildAt(placeHolder, d + 1);
                    }
                }
            }
        } catch(e:*) {
        }

        refresh();
        newBannerView.refresh();
    }
}
}