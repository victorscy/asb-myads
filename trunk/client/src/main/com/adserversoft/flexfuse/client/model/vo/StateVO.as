package com.adserversoft.flexfuse.client.model.vo
{
import com.adserversoft.flexfuse.client.model.ApplicationConstants;

import flash.utils.ByteArray;

import mx.collections.ArrayCollection;
import mx.formatters.ZipCodeFormatter;

[Bindable]
[RemoteClass(alias="com.adserversoft.flexfuse.server.api.State")]
public dynamic class StateVO extends BaseVO
{

    public var adPlaces:ArrayCollection;
    public var banners:ArrayCollection;


}
}
