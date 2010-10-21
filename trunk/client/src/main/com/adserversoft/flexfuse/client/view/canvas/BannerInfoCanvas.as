package com.adserversoft.flexfuse.client.view.canvas {
import com.adserversoft.flexfuse.client.model.ApplicationConstants;
import com.adserversoft.flexfuse.client.model.vo.BannerVO;

import flash.events.Event;
import flash.events.FocusEvent;
import flash.events.MouseEvent;

import mx.containers.FormItem;
import mx.controls.Button;
import mx.controls.CheckBox;
import mx.controls.ComboBox;
import mx.controls.DateField;
import mx.controls.TextInput;
import mx.events.FlexEvent;
import mx.validators.StringValidator;

public class BannerInfoCanvas extends BaseCanvas {
    public static const BROWSE:String = "BROWSE";
    [Bindable]
    public var bannerNameTI:TextInput;
    public var bannerFileFI:FormItem;
    [Bindable]
    public var targetURLTI:TextInput;
    [Bindable]
    public var bannerFileTI:TextInput;

    public var browseBtn:Button;
    public var adFormat:ComboBox;

    public var bannerNameStringValidator:StringValidator;
    public var targetURLStringValidator:StringValidator;
    public var bannerFileStringValidator:StringValidator;

    public static const ONGOING_CHB_CLICK:String = "ONGOING_CHB_CLICK";
    public static const START_DATE_EDIT:String = "START_DATE_EDIT";
    public static const END_DATE_EDIT:String = "END_DATE_EDIT";
    public var startDateDF:DateField;
    public var endDateDF:DateField;
    public var ongoingChB:CheckBox;

    public var banner:BannerVO;

    public function BannerInfoCanvas() {
        super();
        addEventListener(FlexEvent.CREATION_COMPLETE, onCreationComplete);
    }


    protected function onCreationComplete(event:*):void {
        browseBtn.addEventListener(MouseEvent.CLICK, browse);
        bannerNameTI.addEventListener(FocusEvent.FOCUS_OUT, onDeleteWhiteSpace);
        ongoingChB.addEventListener(MouseEvent.CLICK, ongoingClick);
        startDateDF.addEventListener(Event.CHANGE, onStartDateEdit);
        endDateDF.addEventListener(Event.CHANGE, onEndDateEdit);
        dispatchEvent(new Event(INIT));
    }

    private function browse(e:Event):void {
        dispatchEvent(new Event(BROWSE));
    }

    public function validateURL():Boolean {
        var goodStartUrls:Array = new Array("http://www.", "http://", "www.");
        var url:String = new String(targetURLTI.text.toLowerCase());
        if (url == "http://www.") return false;
        var index:int;
        for (var i:int = 0; i < goodStartUrls.length; i++) {
            index = url.indexOf(goodStartUrls[i]);
            if (index == 0) {
                if (url.length > goodStartUrls[i].length) return true;
            }
        }
        if (url.length > 0) return true;
        return false;
    }

    private function onDeleteWhiteSpace(focusEvent:FocusEvent):void {
        bannerNameTI.text = ApplicationConstants.deleteWhiteSpaces(bannerNameTI.text);
    }

    private function ongoingClick(event:Event):void {
        dispatchEvent(new Event(ONGOING_CHB_CLICK));
    }

    public function validate_start_date():Boolean {
        startDateDF.formatString = "MM/DD/YY";
        var start:Date = startDateDF.selectedDate;
        var thisMoment:Date = new Date();
        var toDay:Date = new Date(thisMoment.getFullYear(), thisMoment.getMonth(), thisMoment.getDate(), 0, 0, 0, 0);
        if (start < toDay) return false;
        return true;
    }

    public function validate_end_date():Boolean {
        if (ongoingChB.selected) return true;
        var start:Date = startDateDF.selectedDate;
        var end:Date = endDateDF.selectedDate;
        if (start > end) return false;
        var thisMoment:Date = new Date();
        var tomorrow:Date = new Date(thisMoment.getFullYear(), thisMoment.getMonth(), thisMoment.getDate() + 1, 0, 0, 0, 0);
        if (end < tomorrow) return false;
        return true;
    }

    private function onStartDateEdit(event:Event):void {
        dispatchEvent(new Event(START_DATE_EDIT));
    }

    private function onEndDateEdit(event:Event):void {
        dispatchEvent(new Event(END_DATE_EDIT));
    }
}
}