package kr.tangram.smartgym.data.remote.request

import kr.tangram.smartgym.data.remote.model.DeviceInfo


class ReqDeviceLoad (
    var deviceDiv : String,
    var device : List<DeviceInfo>
)