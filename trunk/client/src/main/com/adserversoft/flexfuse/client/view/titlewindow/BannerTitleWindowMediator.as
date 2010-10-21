package com.adserversoft.flexfuse.client.view.titlewindow {
import com.adserversoft.flexfuse.client.controller.BaseMediator;
import com.adserversoft.flexfuse.client.controller.PopManager;
import com.adserversoft.flexfuse.client.model.AdPlaceProxy;
import com.adserversoft.flexfuse.client.model.ApplicationConstants;
import com.adserversoft.flexfuse.client.model.BannerProxy;
import com.adserversoft.flexfuse.client.model.UploadProxy;
import com.adserversoft.flexfuse.client.model.vo.ObjectFileReference;
import com.adserversoft.flexfuse.client.view.canvas.BannerInfoCanvasMediator;
import com.adserversoft.flexfuse.client.view.canvas.BannerInfoCanvasUI;
import com.adserversoft.flexfuse.client.view.canvas.BannerTargetingCanvasMediator;

import flash.display.Sprite;
import flash.events.Event;
import flash.events.KeyboardEvent;
import flash.events.TimerEvent;
import flash.utils.Timer;

import mx.controls.Alert;
import mx.core.UIComponent;
import mx.events.CloseEvent;
import mx.events.FlexEvent;
import mx.events.ValidationResultEvent;

import org.puremvc.as3.interfaces.IMediator;
import org.puremvc.as3.interfaces.INotification;

public class BannerTitleWindowMediator extends BaseMediator implements IMediator {
    public static const NAME:String = 'BannerTitleWindowMediator';

    private var bannerProxy:BannerProxy;
    private var adPlaceProxy:AdPlaceProxy;
    private var uploadProxy:UploadProxy;


    public function BannerTitleWindowMediator(u:String, viewComponent:Object) {
        this.uid = u;
        super(NAME, viewComponent);


        if (UIComponent(viewComponent).initialized) {
            onInit(null);
        } else {
            UIComponent(viewComponent).addEventListener(FlexEvent.CREATION_COMPLETE, onInit);
        }
    }

    private function onInit(event:Event):void {
        bannerProxy = facade.retrieveProxy(BannerProxy.NAME) as BannerProxy;
        adPlaceProxy = facade.retrieveProxy(AdPlaceProxy.NAME) as AdPlaceProxy;
        uploadProxy = facade.retrieveProxy(UploadProxy.NAME) as UploadProxy;
        registerMediators();

        if (uiComponent.mode == ApplicationConstants.EDIT) {
            uiComponent.title = "Edit Banner";
        } else if (uiComponent.mode == ApplicationConstants.CREATE) {
            uiComponent.banner.uid = ApplicationConstants.getNewUid();
        }
        addEventListeners();
        onIndexChanged(event);
    }

    private function registerMediators():void {
        uiComponent.bannerInfoCanvas.banner = uiComponent.banner;
        uiComponent.bannerTargetingCanvas.banner = uiComponent.banner;
        registerMediator(NAME, BannerInfoCanvasMediator, BannerInfoCanvasMediator.NAME, uiComponent.bannerInfoCanvas);
        registerMediator(NAME, BannerTargetingCanvasMediator, BannerTargetingCanvasMediator.NAME, uiComponent.bannerTargetingCanvas);
    }

    private function unregisterMediators():void {
        unregisterMediator(NAME, BannerInfoCanvasMediator);
        unregisterMediator(NAME, BannerTargetingCanvasMediator);
    }

    public override function addEventListeners():void {
        uiComponent.addEventListener(BaseTitleWindow.CLOSE_POPUP, onClose);
        uiComponent.addEventListener(BaseTitleWindow.CANCEL, onClose);
        uiComponent.addEventListener(BaseTitleWindow.SAVE, onSave);
        uiComponent.addEventListener(KeyboardEvent.KEY_UP, uiComponent.keyup);
        uiComponent.addEventListener(BaseTitleWindow.INDEX_CHANGED_EVENT, onIndexChanged);
    }

    public override function removeEventListeners():void {
        uiComponent.removeEventListener(BaseTitleWindow.CLOSE_POPUP, onClose);
        uiComponent.removeEventListener(BaseTitleWindow.CANCEL, onClose);
        uiComponent.removeEventListener(BaseTitleWindow.SAVE, onSave);
        uiComponent.removeEventListener(KeyboardEvent.KEY_UP, uiComponent.keyup);
        uiComponent.removeEventListener(BaseTitleWindow.INDEX_CHANGED_EVENT, onIndexChanged);
    }

    public override function getMediatorName():String {
        return uid + "::" + NAME;
    }

    private function get uiComponent():BannerTitleWindow {

        return viewComponent as BannerTitleWindow;
    }


    private function onSave(event:Event):void {
        if (validate()) {
            PopManager.setEnabledStateToPopUp(false, BannerTitleWindowMediator);
            uiComponent.banner.dayBits = uiComponent.bannerTargetingCanvas.bannerTargetingTimeCanvas.getDayBitsSelected();
            uiComponent.banner.hourBits = uiComponent.bannerTargetingCanvas.bannerTargetingTimeHourCanvas.getHourBitsSelected();
            uiComponent.banner.countryBits = uiComponent.bannerTargetingCanvas.bannerTargetingGeoCanvas.getGeoBitsSelected();
            if (uiComponent.banner.isBannerFileChanged) { // upload banner file change
                uploadProxy.uploadBannerToSession(event);
            } else {
                sendNotification(ApplicationConstants.BANNER_FILE_UPLOADED, event);
            }
        }
    }

    private function onClose(event:Event):void {
        removeEventListeners();
        unregisterMediators();
        PopManager.closePopUpWindow(uiComponent, getMediatorName());
    }

    public function validate():Boolean {
        var result:ValidationResultEvent = uiComponent.bannerInfoCanvas.bannerNameStringValidator.validate();
        if (result.type == ValidationResultEvent.INVALID) {
            removeEventListeners();
            Alert.show("Banner name is required.", "Invalid", Alert.OK, ApplicationConstants.application as Sprite, onAlertClose);
            uiComponent.invalidField = uiComponent.bannerInfoCanvas.bannerNameTI;
            uiComponent.tabNavigator.selectedChild = uiComponent.bannerInfoCanvas;
            return false
        }
        result = uiComponent.bannerInfoCanvas.targetURLStringValidator.validate();
        if (result.type == ValidationResultEvent.INVALID) {
            removeEventListeners();
            Alert.show("Target URL is required.", "Invalid", Alert.OK, ApplicationConstants.application as Sprite, onAlertClose);
            uiComponent.invalidField = uiComponent.bannerInfoCanvas.targetURLTI;
            uiComponent.tabNavigator.selectedChild = uiComponent.bannerInfoCanvas;
            return false;
        }

        if (!uiComponent.bannerInfoCanvas.validateURL()) {
            removeEventListeners();
            Alert.show("Target URL is required.", "Invalid", Alert.OK, ApplicationConstants.application as Sprite, onAlertClose);
            uiComponent.invalidField = uiComponent.bannerInfoCanvas.targetURLTI;
            uiComponent.tabNavigator.selectedChild = uiComponent.bannerInfoCanvas;
            return false;
        }
        result = uiComponent.bannerInfoCanvas.bannerFileStringValidator.validate();
        if (result.type == ValidationResultEvent.INVALID) {
            removeEventListeners();
            Alert.show(result.message, "Invalid", Alert.OK, ApplicationConstants.application as Sprite, onAlertClose);
            uiComponent.invalidField = uiComponent.bannerInfoCanvas.browseBtn;
            uiComponent.tabNavigator.selectedChild = uiComponent.bannerInfoCanvas;
            return false;
        }
        if (uiComponent.bannerTargetingCanvas.bannerTargetingTimeCanvas.isWeekDaysNotSelected()) {
            removeEventListeners();
            Alert.show("At least one day should be selected.", "Invalid", Alert.OK, ApplicationConstants.application as Sprite, onAlertClose);
            uiComponent.invalidField = uiComponent.bannerTargetingCanvas.bannerTargetingTimeCanvas.cb0;
            uiComponent.tabNavigator.selectedChild = uiComponent.bannerTargetingCanvas;
            uiComponent.bannerTargetingCanvas.tabNavigator.selectedChild = uiComponent.bannerTargetingCanvas.bannerTargetingTimeCanvas;
            return false;
        }
        if (uiComponent.bannerTargetingCanvas.bannerTargetingTimeHourCanvas.isHoursNotSelected()) {
            removeEventListeners();
            Alert.show("At least one hour of the day must be selected.", "Invalid", Alert.OK, ApplicationConstants.application as Sprite, onAlertClose);
            uiComponent.invalidField = uiComponent.bannerTargetingCanvas.bannerTargetingTimeHourCanvas.cb5;
            uiComponent.tabNavigator.selectedChild = uiComponent.bannerTargetingCanvas;
            uiComponent.bannerTargetingCanvas.tabNavigator.selectedChild = uiComponent.bannerTargetingCanvas.bannerTargetingTimeHourCanvas;
            return false;
        }
        if (!uiComponent.bannerTargetingCanvas.bannerTargetingCappingCanvas.isMaxNumberViewsGreater()) {
            removeEventListeners();
            Alert.show("Maximum number of views for the whole display period must be greater then or equal to daily views limit.",
                    "Invalid", Alert.OK, ApplicationConstants.application as Sprite, onAlertClose);
            uiComponent.invalidField = uiComponent.bannerTargetingCanvas.bannerTargetingCappingCanvas.mnofTI;
            uiComponent.tabNavigator.selectedChild = uiComponent.bannerTargetingCanvas;
            uiComponent.bannerTargetingCanvas.tabNavigator.selectedChild = uiComponent.bannerTargetingCanvas.bannerTargetingCappingCanvas;
            return false;
        }
        return true;
    }

    public function onAlertClose(event:CloseEvent):void {
        var minuteTimer:Timer = new Timer(300, 1);
        minuteTimer.addEventListener(TimerEvent.TIMER_COMPLETE, onTimerComplete);
        minuteTimer.start();
        //uiComponent
        uiComponent.invalidField.setFocus();
    }

    public function onTimerComplete(event:TimerEvent):void {
        addEventListeners();
    }


    override public function listNotificationInterests():Array {
        return [
            ApplicationConstants.BANNER_FILE_SELECTED,
            ApplicationConstants.BANNER_FILE_UPLOADED,
            ApplicationConstants.SERVER_FAULT
        ];
    }

    override public function handleNotification(note:INotification):void {
        switch (note.getName()) {
            case ApplicationConstants.BANNER_FILE_SELECTED:
                var fileRef:ObjectFileReference = note.getBody() as ObjectFileReference;
                uiComponent.fileRef = fileRef;
                uiComponent.banner.filename  = fileRef.name;
                uiComponent.banner.fileSize = fileRef.size;
                uiComponent.banner.isBannerFileChanged = true;
                uiComponent.banner.bannerContentTypeId =
                        ApplicationConstants.getBannerContentTypeIdByFileType(fileRef.type);
                break;
            case ApplicationConstants.BANNER_FILE_UPLOADED:
                var notificationName:String = uiComponent.mode == ApplicationConstants.CREATE ? ApplicationConstants.BANNER_ADDED : ApplicationConstants.BANNER_UPDATED;
                sendNotification(notificationName, uiComponent.banner);
                sendNotification(ApplicationConstants.STATE_CHANGED, true);
                PopManager.setEnabledStateToPopUp(true, BannerTitleWindowMediator);
                onClose(null);
                break;
            case ApplicationConstants.SERVER_FAULT:
                PopManager.setEnabledStateToPopUp(true, BannerTitleWindowMediator);
                break;
        }
    }

    private function onIndexChanged(event:Event):void {
        if (uiComponent.tabNavigator.selectedChild is BannerInfoCanvasUI) {//.selectedChild == BannerInfoCanvasUI) {
            uiComponent.bannerInfoCanvas.bannerNameTI.setFocus();
        }
    }
}
}