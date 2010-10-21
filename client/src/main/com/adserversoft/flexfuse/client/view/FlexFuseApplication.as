package com.adserversoft.flexfuse.client.view {

import com.adserversoft.flexfuse.client.ApplicationFacade;
import com.adserversoft.flexfuse.client.view.component.button.CustomSaveButton;
import com.adserversoft.flexfuse.client.view.viewstack.MainPanelViewStack;

import flash.events.Event;
import flash.events.MouseEvent;

import mx.containers.ApplicationControlBar;
import mx.containers.HBox;
import mx.controls.Button;
import mx.controls.Image;
import mx.controls.Text;
import mx.core.Application;
import mx.events.FlexEvent;

public class FlexFuseApplication extends Application {
    public static const LOG_OUT:String = "LOG_OUT";
    public static const SETTINGS:String = "SETTINGS";
    public static const SAVE:String = "SAVE";
    public static const CREATE_REPORTS:String = "CREATE_REPORTS";
    public static const INIT:String = 'INIT';

    public var copyRightsText:Text;
    public var dockedBar:ApplicationControlBar;
    public var logOutBtn:Button;
    public var settingsBtn:Button;
    public var logo:Image;
    public var hBox:HBox;
    public var buttonsHB:HBox;
    public var saveB:CustomSaveButton;
    public var reportsBtn:Button;

    //    public var adPlacesCanvas:AdPlacesCanvas;
    //    public var bannersCanvas:BannersCanvas;
    public var mainPanelViewstack:MainPanelViewStack;
    
    [Bindable]
    public var instId:int;

    public function FlexFuseApplication() {
        super();
        addEventListener(FlexEvent.CREATION_COMPLETE, onCreationComplete);
    }

    private function onCreationComplete(event:Event):void {
        dockedBar.setStyle("dropShadowEnabled", "false");
        reportsBtn.addEventListener(MouseEvent.CLICK, onCreateReports);
        saveB.addEventListener(MouseEvent.CLICK, onSave);
        settingsBtn.addEventListener(MouseEvent.CLICK, onSettings);
        logOutBtn.addEventListener(MouseEvent.CLICK, onLogOut);
        ApplicationFacade.getInstance().startup(this);
    }

    public function onCreateReports(event:Event):void {
        dispatchEvent(new Event(CREATE_REPORTS));
    }

    public function onSave(event:Event):void {
        dispatchEvent(new Event(SAVE));
    }

    public function onSettings(event:Event):void {
        dispatchEvent(new Event(SETTINGS));
    }

    public function onLogOut(event:Event):void {
        dispatchEvent(new Event(LOG_OUT));
    }
}
}