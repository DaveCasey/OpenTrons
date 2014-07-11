package com.opentrons.otbtalpha.cordova;

import com.opentrons.otbtalpha.cordova.OTBTLogicAlpha.ExecuteResult;

public interface IUpdateListener {
	public void handleUpdate(ExecuteResult logicResult, Object[] listenerExtras);
	public void closeListener(ExecuteResult logicResult, Object[] listenerExtras);
}
