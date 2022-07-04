package kr.tangram.smartgym.util

class Define {

    interface Preferences {
        companion object {
        }
    }

    interface AppData {
        companion object {
            const val ScreenLock = "ScreenLock"
        }
    }

    interface BusEvent{
        companion object {
            const val DeviceState = "DeviceState"
        }
    }

    interface DeviceInfo {
        interface Type {
            companion object {
                const val Rookie = "Rookie"
                const val Led = "Led"
            }
        }
    }


    interface Extra {
        companion object {
            const val Identifier = "Identifier"
        }
    }
}