package com.adserversoft.flexfuse.client.view.component.dnd {
import com.adserversoft.flexfuse.client.model.ApplicationConstants;
import com.adserversoft.flexfuse.client.model.vo.AdPlaceVO;
import com.adserversoft.flexfuse.client.model.vo.BannerVO;
import com.adserversoft.flexfuse.client.model.vo.ObjectEvent;

import mx.containers.VBox;
import mx.events.DragEvent;
import mx.managers.DragManager;

public class PlaceHolder extends VBox {

    //    public static var HEIGHT:int = DropIndicator.HEIGHT * 3;

    private var di1:DropIndicator = new DropIndicator();
    private var di2:DropIndicator = new DropIndicator();
    private var di3:DropIndicator = new DropIndicator();

    public var dndWizard:DragNDropWizard;

    public function PlaceHolder(dndWizard:DragNDropWizard) {
        this.dndWizard = dndWizard;
        percentWidth = 100;
        setStyle("verticalGap", "0");
        this.visible = true;
        addEventListener(DragEvent.DRAG_ENTER, doDragEnter);
        addEventListener(DragEvent.DRAG_EXIT, doDragExit);
        addEventListener(DragEvent.DRAG_DROP, doDragDrop);

        di1.visible = false;
        di2.visible = false;
        di3.visible = false;

        this.addChild(di1);
        this.addChild(di2);
        this.addChild(di3);
    }


    private function doDragEnter(e:DragEvent):void {
        if (!(e.dragInitiator.parent is BannerView))return;
        var dropPlace:PlaceHolder = PlaceHolder(e.currentTarget);
        var dropPaddock:Paddock = Paddock(dropPlace.parent.parent);
        var dropAdPlaceView:AdPlaceView = AdPlaceView(dropPaddock.parent);
        var adPlace:AdPlaceVO = dndWizard.adPlaces.getValue(dropAdPlaceView.adPlaceUid);
        var draggedBannerView:BannerView = BannerView(e.dragInitiator.parent);
        var banner:BannerVO = dndWizard.banners.getValue(draggedBannerView.bannerUid);
        if (banner.adFormat == adPlace.adFormat) {
            di2.visible = true;
            DragManager.acceptDragDrop(dropPlace);
        }
    }

    private function doDragExit(e:DragEvent):void {
        if (!(e.dragInitiator.parent is BannerView))return;
        var dropPlace:PlaceHolder = PlaceHolder(e.currentTarget);
        di2.visible = false;
        DragManager.acceptDragDrop(dropPlace);
    }


    private function doDragDrop(e:DragEvent):void {
        var draggedBannerView:BannerView = BannerView(e.dragInitiator.parent);
        var placeHolder:PlaceHolder = PlaceHolder(e.currentTarget);
        var dropPaddock:Paddock = Paddock(placeHolder.parent.parent);
        var dropAdPlaceView:AdPlaceView = AdPlaceView(dropPaddock.parent);
        var newBannerView:BannerView = draggedBannerView;
        var ind:int;

        if (draggedBannerView.parent.parent is Paddock) { //from left panel
            var dragPaddock:Paddock = Paddock(draggedBannerView.parent.parent);
            var bannerTrafficShare:int = BannerVO(dndWizard.banners.getValue(draggedBannerView.bannerUid)).priority;
            var bannerFromDragPaddockUID:String = null;
            for (var k:int = 1; k < dragPaddock.contentVB.getChildren().length; k++) {
                if (dragPaddock.contentVB.getChildAt(k) is BannerView) {
                    if (BannerVO(dndWizard.banners.getValue(BannerView(dragPaddock.contentVB.getChildAt(k)).bannerUid)).priority == bannerTrafficShare) {         //  find banner from drag paddock, bun not draged banner
                        if (BannerVO(dndWizard.banners.getValue(BannerView(dragPaddock.contentVB.getChildAt(k)).bannerUid)).uid != draggedBannerView.bannerUid) {
                            bannerFromDragPaddockUID = BannerVO(dndWizard.banners.getValue(BannerView(dragPaddock.contentVB.getChildAt(k)).bannerUid)).uid;
                        }
                    }
                }
            }
            ind = dropPaddock.contentVB.getChildIndex(placeHolder);

            dragPaddock.contentVB.removeChild(draggedBannerView);
            dropPaddock.contentVB.addChildAt(new PlaceHolder(dndWizard), ind);
            dropPaddock.contentVB.addChildAt(draggedBannerView, ind + 1);
            dropPaddock.contentVB.addChildAt(new PlaceHolder(dndWizard), ind + 2);

            placeHolder.removeEventListener(DragEvent.DRAG_ENTER, doDragEnter);
            placeHolder.removeEventListener(DragEvent.DRAG_EXIT, doDragExit);
            placeHolder.removeEventListener(DragEvent.DRAG_DROP, doDragDrop);

            dropPaddock.contentVB.removeChild(placeHolder);

            dropPaddock.refresh();
            dragPaddock.refresh();
            if (bannerFromDragPaddockUID != null) {
                dragPaddock.updateTrafficSharesByPriority((dndWizard.banners.getValue(bannerFromDragPaddockUID) as BannerVO).priority);
                dragPaddock.validateTrafficSharesByPriority((dndWizard.banners.getValue(bannerFromDragPaddockUID) as BannerVO).priority);
            }
            dropPaddock.updateTrafficSharesByPriority(BannerVO(dndWizard.banners.getValue(draggedBannerView.bannerUid)).priority);
            dropPaddock.validateTrafficSharesByPriority(BannerVO(dndWizard.banners.getValue(draggedBannerView.bannerUid)).priority);

        } else {//dragging from right panel to place holder in paddock
            ind = dropPaddock.contentVB.getChildIndex(placeHolder);
            newBannerView = new BannerViewUI();
            newBannerView.dndWizard = dndWizard;
            newBannerView.currentStateName = BannerView.ASSIGNED_STATE;

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

            dropPaddock.contentVB..addChildAt(new PlaceHolder(dndWizard), ind);
            dropPaddock.contentVB..addChildAt(newBannerView, ind + 1);
            dropPaddock.contentVB..addChildAt(new PlaceHolder(dndWizard), ind + 2);

            placeHolder.removeEventListener(DragEvent.DRAG_ENTER, doDragEnter);
            placeHolder.removeEventListener(DragEvent.DRAG_EXIT, doDragExit);
            placeHolder.removeEventListener(DragEvent.DRAG_DROP, doDragDrop);

            dropPaddock.contentVB.removeChild(placeHolder);

            //after all attachment, detachment is done, we need to clean up
            dropPaddock.refresh();
            newBannerView.refresh();
            dropPaddock.updateTrafficSharesByPriority(BannerVO(dndWizard.banners.getValue(newBannerView.bannerUid)).priority);
            dropPaddock.validateTrafficSharesByPriority(BannerVO(dndWizard.banners.getValue(newBannerView.bannerUid)).priority);
        }

        BannerVO(dndWizard.banners.getValue(newBannerView.bannerUid)).adPlaceUid = dropAdPlaceView.adPlaceUid;

        //notifying of the state change
        dndWizard.dispatchEvent(new ObjectEvent(DragNDropWizard.BANNER_CHANGE, newBannerView));

    }
}
}