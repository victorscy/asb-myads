package com.adserversoft.flexfuse.client.view.component.dnd {
import com.adserversoft.flexfuse.client.model.ApplicationConstants;
import com.adserversoft.flexfuse.client.model.vo.BannerVO;
import com.adserversoft.flexfuse.client.model.vo.ObjectEvent;

import flash.display.InteractiveObject;
import flash.display.Sprite;
import flash.events.Event;
import flash.events.FocusEvent;
import flash.events.KeyboardEvent;
import flash.events.MouseEvent;
import flash.ui.Keyboard;
import flash.utils.ByteArray;

import mx.binding.utils.BindingUtils;
import mx.containers.HBox;
import mx.containers.VBox;
import mx.controls.Alert;
import mx.controls.Button;
import mx.controls.ComboBox;
import mx.controls.HRule;
import mx.controls.Label;
import mx.controls.TextInput;
import mx.controls.VRule;
import mx.core.UIComponent;
import mx.core.UITextField;
import mx.core.mx_internal;
import mx.events.CloseEvent;
import mx.events.DragEvent;
import mx.events.FlexEvent;
import mx.managers.DragManager;
import mx.managers.IFocusManagerComponent;

public class BannerView extends DraggableControl {
    public static const DRAG_BANNER:String = "DRAG_BANNER";
    public static const EDIT_BANNER:String = "EDIT_BANNER";
    public static const PREVIEW_BANNER:String = "PREVIEW_BANNER";

    //    public var topDropIndicator:HBox;
    //    public var bottomDropIndicator:HBox;

    public var hBox:HBox;
    public var dragB:Button;
    public var bannerNameTI:TextInput;
    public var adFormatCB:ComboBox;
    public var fileSizeL:Label;
    public var fileSizeVR:VRule;
    public var bannerNameVR:VRule;
    public var headersHB:HBox;
    public var hRule:HRule;
    public var trafficShareTI:TextInput;
    public var trafficShareVR:VRule;
    public var viewsL:Label;
    public var viewsVR:VRule;
    public var clicksL:Label;
    public var clicksVR:VRule;
    public var dragVR:VRule;
    public var ctrL:Label;
    public var ctrVR:VRule;
    public var topBorderLineHR:HRule;
    public var activateB:Button;
    public var activateVR:VRule;
    public var adFormatVR:VRule;
    public var containerVB:VBox;
    public var deleteBtn:Button;
    public var previewBtn:Button;
    public var editBtn:Button;

    public var bottomDropIndicatorHB:HBox;
    public var topDropIndicatorHB:HBox;

    public var invalidField:UIComponent;

    public var bannerUid:String;
    public var dndWizard:DragNDropWizard;

    public static var REGULAR_STATE:String = "REGULAR_STATE";
    public static var ASSIGNED_STATE:String = "ASSIGNED_STATE";

    public var currentStateName:String = REGULAR_STATE;


    use namespace mx_internal;

    public function BannerView() {
        super();
        addEventListener(FlexEvent.CREATION_COMPLETE, onCreationComplete);
    }

    protected function onCreationComplete(event:Event):void {
        var banner:BannerVO = dndWizard.banners.getValue(bannerUid);

        adFormatCB.dataProvider = ApplicationConstants.sortedAdFormatsCollection;

        BindingUtils.bindProperty(bannerNameTI, "text", banner, "bannerName");
        BindingUtils.bindProperty(banner, "bannerName", bannerNameTI, "text");

        BindingUtils.bindProperty(trafficShareTI, "text", banner, "trafficShare");
        BindingUtils.bindProperty(banner, "trafficShare", trafficShareTI, "text");

        BindingUtils.bindProperty(fileSizeL, "text", banner, "fileSizeString");

        BindingUtils.bindProperty(adFormatCB, "selectedItem", banner, "adFormat");
        BindingUtils.bindProperty(banner, "adFormat", adFormatCB, "selectedItem");

        BindingUtils.bindProperty(viewsL, "text", banner, "views");
        BindingUtils.bindProperty(clicksL, "text", banner, "clicks");
        BindingUtils.bindProperty(ctrL, "text", banner, "ctr");
        BindingUtils.bindProperty(adFormatCB, "enabled", banner, "isAdFormatEnabled");

        adFormatCB.selectedItem = banner.adFormat;

        bannerNameTI.addEventListener(KeyboardEvent.KEY_UP, onKeyUp);
        bannerNameTI.addEventListener(FocusEvent.FOCUS_IN, onFocusIn);
        bannerNameTI.addEventListener(FocusEvent.MOUSE_FOCUS_CHANGE, onFocusChange);
        bannerNameTI.addEventListener(FocusEvent.KEY_FOCUS_CHANGE, onFocusChange);
        bannerNameTI.addEventListener(FlexEvent.ENTER, onFocusChange);

        trafficShareTI.addEventListener(KeyboardEvent.KEY_UP, onKeyUp);
        trafficShareTI.addEventListener(FocusEvent.MOUSE_FOCUS_CHANGE, onFocusChange);
        trafficShareTI.addEventListener(FocusEvent.KEY_FOCUS_CHANGE, onFocusChange);
        trafficShareTI.addEventListener(FlexEvent.ENTER, onFocusChange);

        dragB.addEventListener(MouseEvent.MOUSE_MOVE, doMouseMove);
        activateB.addEventListener(MouseEvent.CLICK, onChangeStateBanner);
        deleteBtn.addEventListener(MouseEvent.CLICK, onRemoveBanner);
        previewBtn.addEventListener(MouseEvent.CLICK, onPreviewBanner);
        editBtn.addEventListener(MouseEvent.CLICK, onEditBanner);

        adFormatCB.addEventListener(Event.CHANGE, onSelectionChanged);

        if (banner.bannerState == ApplicationConstants.STATE_ACTIVE) {
            activateB.selected = false;
        } else {
            activateB.selected = true;
        }

        setState(currentStateName);
    }

    public function updateProviders():void {
        var banner:BannerVO = dndWizard.banners.getValue(bannerUid);
        adFormatCB.selectedItem = banner.adFormat;
    }

    private function setState(c:String):void {
        if (c == REGULAR_STATE) {

        } else if (c == ASSIGNED_STATE) {
            headersHB.removeChild(fileSizeL);
            headersHB.removeChild(fileSizeVR);

            headersHB.addChildAt(activateB, headersHB.getChildIndex(dragVR));
            headersHB.addChildAt(activateVR, headersHB.getChildIndex(activateB));

            headersHB.addChildAt(trafficShareTI, headersHB.getChildIndex(bannerNameVR));
            headersHB.addChildAt(trafficShareVR, headersHB.getChildIndex(trafficShareTI));

            //            headersHB.addChildAt(viewsL, headersHB.getChildIndex(adFormatVR));
            //            headersHB.addChildAt(viewsVR, headersHB.getChildIndex(viewsL));
            //
            //            headersHB.addChildAt(clicksL, headersHB.getChildIndex(viewsVR));
            //            headersHB.addChildAt(clicksVR, headersHB.getChildIndex(clicksL));
            //
            //            headersHB.addChildAt(ctrL, headersHB.getChildIndex(clicksVR));
            //            headersHB.addChildAt(ctrVR, headersHB.getChildIndex(ctrL));
        }
    }

    /**
     * Adding/removing top border depending on the neighbor above
     */
    public function refresh():void {
        try {
            var parentContainer:VBox = VBox(this.parent);
            var ind:int = parentContainer.getChildIndex(this);
            if (parentContainer.getChildAt(ind - 1) is BannerView) {//removing top border
                topBorderLineHR.visible = false;
                topBorderLineHR.height = 0;
            } else {//adding top border
                topBorderLineHR.visible = true;
                topBorderLineHR.height = 1;
            }
        } catch(e:*) {
        }
    }

    private function onKeyUp(event:KeyboardEvent):void {
        if (event.keyCode == Keyboard.ESCAPE) {
            if (event.currentTarget == bannerNameTI) {
                bannerNameTI.text = BannerVO(dndWizard.banners.getValue(bannerUid)).bannerName;
                changeFocus(event);
            } else {
                var trafficShareString:String = trafficShareTI.text;
                if (int(trafficShareTI.text) > 100) {
                    trafficShareTI.text = String(100);
                }
                if (int(trafficShareTI.text) < 1) {
                    trafficShareTI.text = String(1);
                }
                changeFocus(event);
            }
        }
    }

    private function onFocusIn(event:FocusEvent):void {
        bannerNameTI.selectionEndIndex = bannerNameTI.text.length;
        UITextField(bannerNameTI.getTextField()).alwaysShowSelection = true;
    }

    private function onFocusChange(event:Event):void {
        if (event.currentTarget == bannerNameTI) {
            if (ApplicationConstants.deleteWhiteSpaces(bannerNameTI.text) == "") {
                invalidField = bannerNameTI;
                Alert.show("Banner name is required.",
                        "Invalid", Alert.OK, ApplicationConstants.application as Sprite, onAlertClose);
            } else {
                dndWizard.banners.getValue(bannerUid).bannerName = bannerNameTI.text;
            }
            changeFocus(event);
        } else if (event.currentTarget == trafficShareTI) {
            var trafficShareString:String = trafficShareTI.text;
            if (int(trafficShareTI.text) > 100) {
                trafficShareTI.text = String(100);
            }
            if (int(trafficShareTI.text) < 1) {
                trafficShareTI.text = String(1);
            }
            var paddock:Paddock = this.parent.parent as Paddock;
            paddock.validateTrafficSharesByPriority(BannerVO(dndWizard.banners.getValue(bannerUid)).priority);
            changeFocus(event);
        }
        dndWizard.dispatchEvent(new ObjectEvent(DragNDropWizard.BANNER_CHANGE, dndWizard.banners.getValue(bannerUid)));

    }

    public function onAlertClose(event:CloseEvent):void {
        if (invalidField != null) invalidField.setFocus();
    }

    private function changeFocus(event:Event):void {
        bannerNameTI.selectionEndIndex = 0;
        UITextField(bannerNameTI.getTextField()).alwaysShowSelection = true;
        var component:InteractiveObject = event is FocusEvent ?
                                          FocusEvent(event).relatedObject : event.currentTarget.parent;
        if (component is IFocusManagerComponent) {
            IFocusManagerComponent(component).setFocus();
        } else {
            stage.focus = component;
        }
    }


    private function onSelectionChanged(event:Event):void {
        dndWizard.dispatchEvent(new ObjectEvent(DragNDropWizard.BANNER_CHANGE, dndWizard.banners.getValue(bannerUid)));
    }


    public function clone():* {
        var myBA:ByteArray = new ByteArray();
        myBA.writeObject(this);
        myBA.position = 0;
        return(myBA.readObject());
    }

    private function onRemoveBanner(e:MouseEvent):void {
        var ab:BannerView = BannerView(e.currentTarget.parent.parent.parent.parent);
        dndWizard.onBannerRemove(ab.bannerUid);
    }

    private function onChangeStateBanner(e:MouseEvent):void {
        var ab:BannerView = BannerView(e.currentTarget.parent.parent.parent);
        BannerVO(dndWizard.banners.getValue(ab.bannerUid)).bannerState = activateB.selected ? ApplicationConstants.STATE_INACTIVE : ApplicationConstants.STATE_ACTIVE;
        dndWizard.dispatchEvent(new ObjectEvent(DragNDropWizard.BANNER_CHANGE, ab));
    }

    private function onEditBanner(e:MouseEvent):void {
        var ab:BannerView = BannerView(e.currentTarget.parent.parent.parent.parent);
        dndWizard.onBannerEdit(ab.bannerUid);
    }

    private function onPreviewBanner(e:MouseEvent):void {
        var ab:BannerView = BannerView(e.currentTarget.parent.parent.parent.parent);
        var banner:BannerVO=dndWizard.banners.getValue(ab.bannerUid);
        dndWizard.onBannerPreView(banner);

    }


    override protected function doDragEnter(e:DragEvent):void {
        var dropPlaceBannerView:BannerView = BannerView(e.currentTarget);
        if (!(e.dragInitiator.parent is BannerView))return;

        var draggedBannerView:BannerView = BannerView(e.dragInitiator.parent);
        //if dragging from paddock to the banners panel
        if (!(dropPlaceBannerView.parent.parent is Paddock) && draggedBannerView.parent.parent is Paddock) {
            return;
        }

        var dropBanner:BannerVO = dndWizard.banners.getValue(dropPlaceBannerView.bannerUid);
        var draggedBanner:BannerVO = dndWizard.banners.getValue(draggedBannerView.bannerUid);
        if (dropBanner.adFormat == draggedBanner.adFormat) {
            if (dropPlaceBannerView.contentMouseY > dropPlaceBannerView.height / 2) {
                bottomDropIndicatorHB.visible = true;
                bottomDropIndicatorHB.height = 3;
            } else {
                topDropIndicatorHB.visible = true;
                topDropIndicatorHB.height = 3;
            }
            DragManager.acceptDragDrop(dropPlaceBannerView);
        }
    }

    override protected function doDragDrop(e:DragEvent):void {
        var draggedBannerView:BannerView = BannerView(e.dragInitiator.parent);
        var dropPlaceBannerView:BannerView = BannerView(e.currentTarget);
        var newBannerView:BannerView = draggedBannerView;

        //dragging from right panel to right panel
        if (!(dropPlaceBannerView.parent.parent is Paddock)) {
            super.doDragDrop(e);
            draggedBannerView.removeDropIndicators();
            dropPlaceBannerView.removeDropIndicators();
            return;
        }
        //else...
        var dropPaddock:Paddock = Paddock(dropPlaceBannerView.parent.parent);
        var dropAdPlaceView:AdPlaceView = AdPlaceView(dropPaddock.parent);

        //no changes necessary if dropping on yourself, just remove drop indicators
        if (draggedBannerView == dropPlaceBannerView) {
            draggedBannerView.removeDropIndicators();
            dropPlaceBannerView.removeDropIndicators();
            return;
        }

        var ind:int;
        var childrenLength:int;


        //dragging from paddock to paddock
        if (draggedBannerView.parent.parent is Paddock) {
            var dragPaddock:Paddock = Paddock(draggedBannerView.parent.parent);
            //calculating insertion index
            ind = dropPaddock.contentVB.getChildIndex(dropPlaceBannerView);
            if (dropPlaceBannerView.indicatorPos == DraggableControl.INDICATOR_UPPER) {
                ind = ind < 2 ? 1 : ind - 1;
            } else {
                childrenLength = dropPaddock.contentVB.getChildren().length;
                ind = ind > childrenLength - 3 ? childrenLength - 3 : ind;
                ++ind;
            }
            //inserting
            var dragBannerPriority:int = dndWizard.banners.getValue(draggedBannerView.bannerUid).priority;
            dropPaddock.contentVB.addChildAt(draggedBannerView, ind);
            dropPaddock.refresh();
            dragPaddock.refresh();
            dragPaddock.updateTrafficSharesByPriority(dragBannerPriority);
            dragPaddock.validateTrafficSharesByPriority(dragBannerPriority);

            //dragging from right panel to paddock
        } else {
            //creating a new banner
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

            //find insertion point
            ind = dropPaddock.contentVB.getChildIndex(dropPlaceBannerView);
            if (dropPlaceBannerView.indicatorPos == DraggableControl.INDICATOR_UPPER) {
                ind = ind < 2 ? 1 : ind;
            } else {
                childrenLength = dropPaddock.contentVB.getChildren().length;
                ind = ind > childrenLength - 2 ? childrenLength - 2 : ind;
                ++ind;
            }
            //inserting
            dropPaddock.contentVB.addChildAt(newBannerView, ind);
            newBannerView.refresh();
        }

        BannerVO(dndWizard.banners.getValue(newBannerView.bannerUid)).adPlaceUid = dropAdPlaceView.adPlaceUid;
        //notifying of the state change
        dndWizard.dispatchEvent(new ObjectEvent(DragNDropWizard.BANNER_CHANGE, newBannerView));

        draggedBannerView.refresh();
        dropPlaceBannerView.refresh();

        //we need to remove drop indicators in any case
        draggedBannerView.removeDropIndicators();
        dropPlaceBannerView.removeDropIndicators();
        dropPaddock.refresh();
        dropPaddock.updateTrafficSharesByPriority(dndWizard.banners.getValue(bannerUid).priority);
        dropPaddock.validateTrafficSharesByPriority(dndWizard.banners.getValue(bannerUid).priority);
    }

    override protected function removeDropIndicators():void {
        topDropIndicatorHB.visible = false;
        topDropIndicatorHB.height = 0;
        bottomDropIndicatorHB.visible = false;
        bottomDropIndicatorHB.height = 0;
    }


}
}